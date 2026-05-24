package DLL;

import BLL.EstadoHabitacion;
import java.sql.*;
import javax.swing.JOptionPane;

public class HotelController {
    private static HotelController instance;

    private HotelController() {}

    public static HotelController getInstance() {
        if (instance == null) {
            instance = new HotelController();
        }
        return instance;
    }

    // Valida si un DNI pertenece a la lista previa autorizada por la Empresa.
    public boolean validarInvitadoPrevia(String codigoEvento, String dni) {
        String sql = "SELECT lip.* FROM lista_invitados_previa lip " +
                     "JOIN reservas_hotel rh ON lip.id_reserva = rh.id " +
                     "WHERE rh.codigo_unico_evento = ? AND lip.dni = ?";
        
        Connection con = ConexionController.getInstance().getConnection();
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoEvento);
            ps.setString(2, dni);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al validar lista previa: " + e.getMessage());
            return false;
        }
    }

    // Procesa el Check-In incremental de habitaciones dobles.
    // Transiciona el estado usando el Enum EstadoHabitacion: Libre -> Half -> Completa.
    public boolean procesarCheckInHabitacion(String codigoEvento, String dniHuesped, String numeroHabitacion) {
        Connection con = ConexionController.getInstance().getConnection();
        
        String sqlReserva = "SELECT id FROM reservas_hotel WHERE codigo_unico_evento = ?";
        String sqlUsuario = "SELECT id_usuario FROM datos_personas WHERE dni = ?";
        String sqlHabitacion = "SELECT id, estado FROM habitaciones WHERE numero = ?";
        
        try {
            con.setAutoCommit(false); // Transacción segura

            int idReserva = 0;
            try (PreparedStatement ps = con.prepareStatement(sqlReserva)) {
                ps.setString(1, codigoEvento);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) idReserva = rs.getInt("id");
                }
            }

            int idUsuario = 0;
            try (PreparedStatement ps = con.prepareStatement(sqlUsuario)) {
                ps.setString(1, dniHuesped);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) idUsuario = rs.getInt("id_usuario");
                }
            }

            if (idReserva == 0 || idUsuario == 0) {
                JOptionPane.showMessageDialog(null, "Error: Evento o DNI no encontrados.", "Error de Check-in", JOptionPane.ERROR_MESSAGE);
                con.rollback();
                return false;
            }

            int idHabitacion = 0;
            String estadoStr = "";
            try (PreparedStatement ps = con.prepareStatement(sqlHabitacion)) {
                ps.setString(1, numeroHabitacion);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idHabitacion = rs.getInt("id");
                        estadoStr = rs.getString("estado");
                    }
                }
            }

            // String de la BdD a nuestro Enum de BLL
            EstadoHabitacion estadoActual = EstadoHabitacion.valueOf(estadoStr);
            EstadoHabitacion nuevoEstado;
            String casillero = "";

            if (estadoActual == EstadoHabitacion.Libre) {
                nuevoEstado = EstadoHabitacion.Half;
                casillero = "1";
            } else if (estadoActual == EstadoHabitacion.Half) {
                nuevoEstado = EstadoHabitacion.Completa;
                casillero = "2";
            } else {
                JOptionPane.showMessageDialog(null, "Error: Habitación Completa o SOBRE-ASIGNACIÓN.", "Límite Excedido", JOptionPane.WARNING_MESSAGE);
                con.rollback();
                return false;
            }

            // Insertar asignación.
            String sqlAsignar = "INSERT INTO asignaciones_habitaciones (id_reserva, id_habitacion, id_usuario, casillero_checkin) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlAsignar)) {
                ps.setInt(1, idReserva);
                ps.setInt(2, idHabitacion);
                ps.setInt(3, idUsuario);
                ps.setString(4, casillero);
                ps.executeUpdate();
            }

            // Actualicé la tabla habitaciones con el String del enum de EstadoHabitación.java.
            String sqlUpdateHab = "UPDATE habitaciones SET estado = ? WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlUpdateHab)) {
                ps.setString(1, nuevoEstado.toString());
                ps.setInt(2, idHabitacion);
                ps.executeUpdate();
            }

            con.commit();
            JOptionPane.showMessageDialog(null, "Check-in Exitoso! Habitación " + numeroHabitacion + " pasó a: " + nuevoEstado);
            return true;

        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            if (e.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(null, "Error: El invitado ya cuenta con un Check-in registrado.", "DNI Duplicado", JOptionPane.ERROR_MESSAGE);
            } else {
                System.err.println("Error en transacción de Check-in: " + e.getMessage());
            }
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // Obtiene el cronograma de actividades estructurado en HTML para el panel de UI.
    public String obtenerCronogramaEventos(String codigoEvento) {
        StringBuilder cronograma = new StringBuilder();
        
        String sql = "SELECT a.nombre, a.descripcion, a.importancia, a.categoria, a.hora_actividad " +
                     "FROM actividades a " +
                     "JOIN reservas_hotel rh ON a.id_reserva = rh.id " +
                     "WHERE rh.codigo_unico_evento = ? " +
                     "ORDER BY a.hora_actividad ASC";

        Connection con = ConexionController.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoEvento);

            try (ResultSet rs = ps.executeQuery()) {
                cronograma.append("<html><body style='width: 320px;'>");
                cronograma.append("<h2 style='text-align: center; color: #1a5f7a;'>📅 Cronograma de Actividades</h2>");
                cronograma.append("<p style='text-align: center;'><b>Evento:</b> ").append(codigoEvento).append("</p><hr>");

                boolean tieneActividades = false;
                
                while (rs.next()) {
                    tieneActividades = true;
                    
                    String hora = rs.getTime("hora_actividad").toString().substring(0, 5);
                    String nombre = rs.getString("nombre");
                    String desc = rs.getString("descripcion");
                    String importancia = rs.getString("importancia");
                    String categoria = rs.getString("categoria");

                    String colorImportancia = "gray";
                    if (importancia.equalsIgnoreCase("Alta")) {
                        colorImportancia = "red";
                    } else if (importancia.equalsIgnoreCase("Media")) {
                        colorImportancia = "orange";
                    }

                    cronograma.append("<p style='margin-bottom: 2px;'><b>⏱️ ").append(hora).append(" hs</b> - ").append(nombre).append("</p>");
                    cronograma.append("<p style='margin-left: 15px; color: #555; margin-top: 0px;'><i>").append(desc != null ? desc : "Sin descripción").append("</i><br>");
                    cronograma.append("<small>📁 Cat: ").append(categoria)
                              .append(" | <font color='").append(colorImportancia).append("'>🔥 ").append(importancia).append("</font></small></p>");
                    cronograma.append("<hr style='border-top: 1px dashed #ccc;'>");
                }

                cronograma.append("</body></html>");

                if (!tieneActividades) {
                    return "<html><body>❌ No se encontraron actividades registradas para el código de evento ingresado.</body></html>";
                }

                return cronograma.toString();
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el cronograma: " + e.getMessage());
            return "<html><body>❌ Error técnico al consultar la base de datos.</body></html>";
        }
    }

    // Traigo un listado plano de nombres de actividades vinculadas al evento.
    public String[] obtenerNombresActividades(String codigoEvento) {
        java.util.List<String> lista = new java.util.ArrayList<>();
        String sql = "SELECT a.nombre FROM actividades a " +
                     "JOIN reservas_hotel rh ON a.id_reserva = rh.id " +
                     "WHERE rh.codigo_unico_evento = ?";
        
        Connection con = ConexionController.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoEvento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nombres de actividades: " + e.getMessage());
        }
        
        return lista.toArray(new String[0]);
    }

    // Asiento de forma segura la asistencia de un invitado validando relaciones de la base de datos.
    public boolean registrarAsistenciaActividad(String codigoEvento, String nombreActividad, String dniInvitado) {
        Connection con = ConexionController.getInstance().getConnection();
        
        String sqlIds = "SELECT a.id AS id_act_real, lip.id AS id_inv_real " +
                        "FROM reservas_hotel rh " +
                        "JOIN actividades a ON a.id_reserva = rh.id " +
                        "JOIN lista_invitados_previa lip ON lip.id_reserva = rh.id " +
                        "WHERE rh.codigo_unico_evento = ? AND a.nombre = ? AND lip.dni = ?";
        
        String sqlInsertAsistencia = "INSERT INTO asistencias_actividades (id_actividad, id_invitado, asistio) " +
                                     "VALUES (?, ?, 'S') " +
                                     "ON DUPLICATE KEY UPDATE asistio = 'S'";

        try {
            int idActividad = 0;
            int idInvitado = 0;

            try (PreparedStatement ps = con.prepareStatement(sqlIds)) {
                ps.setString(1, codigoEvento);
                ps.setString(2, nombreActividad);
                ps.setString(3, dniInvitado);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idActividad = rs.getInt("id_act_real");
                        idInvitado = rs.getInt("id_inv_real");
                    }
                }
            }

            if (idActividad == 0 || idInvitado == 0) {
                JOptionPane.showMessageDialog(null, "❌ El DNI no corresponde a un invitado autorizado para este evento.", "Validación Fallida", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sqlInsertAsistencia)) {
                ps.setInt(1, idActividad);
                ps.setInt(2, idInvitado);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(null, "✅ Asistencia registrada con éxito para el invitado DNI: " + dniInvitado + "\nen la actividad: " + nombreActividad, "Estado Actualizado", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (SQLException e) {
            System.err.println("Error al registrar asistencia en la BD: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "❌ Error técnico al asentar la asistencia.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
     // Realiza un sorteo aleatorio entre los asistentes confirmados de un evento
    // e impacta la tabla de premios (Mapea CU41 y CU42).
    public boolean ejecutarSorteoPremio(String codigoEvento, String descripcionPremio) {
        Connection con = ConexionController.getInstance().getConnection();
        
        // Query con ORDER BY RAND() para seleccionar un único ganador al azar que haya asistido ('S')
        String sqlSorteo = "SELECT lip.id AS id_invitado_real, dp.nombre_completo, lip.dni " +
                           "FROM asistencias_actividades aa " +
                           "JOIN lista_invitados_previa lip ON aa.id_invitado = lip.id " +
                           "JOIN datos_personas dp ON lip.dni = dp.dni " +
                           "JOIN reservas_hotel rh ON lip.id_reserva = rh.id " +
                           "WHERE rh.codigo_unico_evento = ? AND aa.asistio = 'S' " +
                           "ORDER BY RAND() " +
                           "LIMIT 1";
        
        String sqlInsertPremio = "INSERT INTO premios (id_invitado, descripcion, estado) VALUES (?, ?, 'Entregado')";

        try {
            int idInvitadoGanador = 0;
            String nombreGanador = "";
            String dniGanador = "";

            // Ejecutamos la selección aleatoria (Verificar elegibilidad - CU41)
            try (PreparedStatement ps = con.prepareStatement(sqlSorteo)) {
                ps.setString(1, codigoEvento);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idInvitadoGanador = rs.getInt("id_invitado_real");
                        nombreGanador = rs.getString("nombre_completo");
                        dniGanador = rs.getString("dni");
                    }
                }
            }

            // Si nadie asistió a ninguna actividad o el código está mal, salimos
            if (idInvitadoGanador == 0) {
                JOptionPane.showMessageDialog(null, "❌ No se encontraron invitados con asistencias registradas para este evento.", "Sorteo Vacío", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Insertamos el premio asignado en la base de datos (Entregar premio - CU42)
            try (PreparedStatement ps = con.prepareStatement(sqlInsertPremio)) {
                ps.setInt(1, idInvitadoGanador);
                ps.setString(2, descripcionPremio);
                ps.executeUpdate();
            }

            // Mostramos el ganador con bombos y platillos en la pantalla
            String mensajeExito = "<html><body style='width: 250px; text-align: center;'>"
                                + "<h2 style='color: #2ecc71;'>🎉 ¡Tenemos Ganador! 🎉</h2>"
                                + "<p><b>Invitado:</b> " + nombreGanador + "</p>"
                                + "<p><b>DNI:</b> " + dniGanador + "</p>"
                                + "<hr>"
                                + "<p><b>Premio otorgado:</b><br><i style='color: #555;'>" + descripcionPremio + "</i></p>"
                                + "</body></html>";
            
            JOptionPane.showMessageDialog(null, mensajeExito, "Sorteo Exitoso", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (SQLException e) {
            System.err.println("Error en la ejecución del sorteo: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "❌ Error técnico al procesar el sorteo en la BD.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}