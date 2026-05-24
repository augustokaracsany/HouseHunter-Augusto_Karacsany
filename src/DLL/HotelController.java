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
        
        String sqlIds = "SELECT a.id AS id_act_real, dp.id_usuario AS id_user_real " +
                        "FROM reservas_hotel rh " +
                        "JOIN actividades a ON a.id_reserva = rh.id " +
                        "JOIN lista_invitados_previa lip ON lip.id_reserva = rh.id " +
                        "JOIN datos_personas dp ON lip.dni = dp.dni " +
                        "WHERE rh.codigo_unico_evento = ? AND a.nombre = ? AND lip.dni = ?";
        
        String sqlInsertAsistencia = "INSERT INTO asistencias_actividades (id_actividad, id_usuario, asistio) " +
                                     "VALUES (?, ?, 'S') " +
                                     "ON DUPLICATE KEY UPDATE asistio = 'S'";

        try {
            int idActividad = 0;
            int idUsuario = 0;

            try (PreparedStatement ps = con.prepareStatement(sqlIds)) {
                ps.setString(1, codigoEvento);
                ps.setString(2, nombreActividad);
                ps.setString(3, dniInvitado);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idActividad = rs.getInt("id_act_real");
                        idUsuario = rs.getInt("id_user_real");
                    }
                }
            }

            if (idActividad == 0 || idUsuario == 0) {
                JOptionPane.showMessageDialog(null, "❌ El DNI no corresponde a un invitado registrado y autorizado.", "Validación Fallida", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sqlInsertAsistencia)) {
                ps.setInt(1, idActividad);
                ps.setInt(2, idUsuario);
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

    // Realiza un sorteo aleatorio entre los asistentes confirmados de un evento (Mapea CU41 y CU42).
    public boolean ejecutarSorteoPremio(String codigoEvento, String descripcionPremio) {
        Connection con = ConexionController.getInstance().getConnection();
        
        // Seleccionamos un ganador al azar que asistió ('S') mapeando contra el esquema relacional real
        String sqlSorteo = "SELECT rh.id AS id_res_real, aa.id_usuario, dp.nombre, dp.apellido, dp.dni " +
                           "FROM asistencias_actividades aa " +
                           "JOIN actividades act ON aa.id_actividad = act.id " +
                           "JOIN reservas_hotel rh ON act.id_reserva = rh.id " +
                           "JOIN datos_personas dp ON aa.id_usuario = dp.id_usuario " +
                           "WHERE rh.codigo_unico_evento = ? AND aa.asistio = 'S' " +
                           "ORDER BY RAND() " +
                           "LIMIT 1";
        
        String sqlInsertPremio = "INSERT INTO premios (id_reserva, nombre_premio, entregado, id_ganador_usuario) VALUES (?, ?, 'S', ?)";

        try {
            int idReserva = 0;
            int idUsuarioGanador = 0;
            String nombreCompleto = "";
            String dniGanador = "";

            try (PreparedStatement ps = con.prepareStatement(sqlSorteo)) {
                ps.setString(1, codigoEvento);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idReserva = rs.getInt("id_res_real");
                        idUsuarioGanador = rs.getInt("id_usuario");
                        nombreCompleto = rs.getString("nombre") + " " + rs.getString("apellido");
                        dniGanador = rs.getString("dni");
                    }
                }
            }

            if (idUsuarioGanador == 0) {
                JOptionPane.showMessageDialog(null, "❌ No hay invitados con asistencias registradas ('S') para este evento.", "Sorteo Vacío", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sqlInsertPremio)) {
                ps.setInt(1, idReserva);
                ps.setString(2, descripcionPremio);
                ps.setInt(3, idUsuarioGanador);
                ps.executeUpdate();
            }

            String mensajeExito = "<html><body style='width: 250px; text-align: center;'>"
                                + "<h2 style='color: #2ecc71;'>🎉 ¡Tenemos Ganador! 🎉</h2>"
                                + "<p><b>Invitado:</b> " + nombreCompleto + "</p>"
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

    // Genera un reporte consolidado con métricas clave de un evento (Mapea el CU44).
    public String obtenerReporteConsolidadoEvento(String codigoEvento) {
        StringBuilder reporte = new StringBuilder();
        
        String sql = "SELECT rh.id, " +
                     "  (SELECT COUNT(*) FROM lista_invitados_previa lip WHERE lip.id_reserva = rh.id) as total_invitados, " +
                     "  (SELECT COUNT(DISTINCT ah.id_usuario) FROM asignaciones_habitaciones ah WHERE ah.id_reserva = rh.id) as total_checkins, " +
                     "  (SELECT COUNT(*) FROM actividades act " +
                     "   JOIN asistencias_actividades aa ON aa.id_actividad = act.id " +
                     "   WHERE act.id_reserva = rh.id AND aa.asistio = 'S') as total_asistencias " +
                     "FROM reservas_hotel rh " +
                     "WHERE rh.codigo_unico_evento = ?";

        Connection con = ConexionController.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoEvento);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int totalInvitados = rs.getInt("total_invitados");
                    int totalCheckins = rs.getInt("total_checkins");
                    int totalAsistencias = rs.getInt("total_asistencias");
                    
                    int porcentajeOcupacion = totalInvitados > 0 ? (totalCheckins * 100 / totalInvitados) : 0;

                    reporte.append("<html><body style='width: 300px;'>");
                    reporte.append("<h2 style='text-align: center; color: #2c3e50;'>📊 Reporte Consolidado</h2>");
                    reporte.append("<p style='text-align: center; margin-top:0;'><b>Evento:</b> ").append(codigoEvento).append("</p><hr>");
                    
                    reporte.append("<table style='width: 100%; border-collapse: collapse;'>");
                    reporte.append("<tr><td><b>📋 Invitados en Lista:</b></td><td style='text-align: right;'>").append(totalInvitados).append("</td></tr>");
                    reporte.append("<tr><td><b>🏨 Check-ins Exitosos:</b></td><td style='text-align: right;'>").append(totalCheckins).append("</td></tr>");
                    reporte.append("<tr><td><b>📉 Porcentaje Ocupación:</b></td><td style='text-align: right; color: green;'><b>").append(porcentajeOcupacion).append("%</b></td></tr>");
                    reporte.append("<tr><td colspan='2'><hr style='border-top: 1px dashed #ccc;'></td></tr>");
                    reporte.append("<tr><td><b>🎮 Asistencias Totales:</b></td><td style='text-align: right; color: #2980b9;'><b>").append(totalAsistencias).append("</b></td></tr>");
                    reporte.append("</table>");
                    
                    reporte.append("<br><p style='text-align: center; font-size: 9px; color: gray;'>HouseHunter Status Report • Generado Correctamente</p>");
                    reporte.append("</body></html>");
                    
                    return reporte.toString();
                } else {
                    return "<html><body>❌ El código de evento ingresado no corresponde a ninguna reserva activa.</body></html>";
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al generar el reporte consolidado: " + e.getMessage());
            return "<html><body>❌ Error técnico al procesar el reporte analítico en la BD.</body></html>";
        }
    }
 // Devuelve el estado actual de ocupación de las habitaciones en formato HTML
    public String obtenerEstadoHabitacionesHtml() {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT numero, estado FROM habitaciones ORDER BY numero ASC";
        Connection con = ConexionController.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            sb.append("<html><body style='width: 260px;'>");
            sb.append("<h2 style='text-align: center; color: #27ae60;'>🏨 Ocupación de Habitaciones</h2><hr>");
            sb.append("<table style='width: 100%; border-collapse: collapse;'>");

            boolean tieneHabitaciones = false;
            while (rs.next()) {
                tieneHabitaciones = true;
                String num = rs.getString("numero");
                String est = rs.getString("estado");
                
                // Definimos el color del badge según el Enum EstadoHabitacion
                String color = "green";
                if (est.equalsIgnoreCase("Half")) {
                    color = "orange";
                } else if (est.equalsIgnoreCase("Completa")) {
                    color = "red";
                }

                sb.append("<tr>")
                  .append("<td style='padding: 5px; border-bottom: 1px solid #eee;'><b>Habitación ").append(num).append("</b></td>")
                  .append("<td style='text-align: right; padding: 5px; border-bottom: 1px solid #eee; color: ").append(color).append(";'><b>").append(est).append("</b></td>")
                  .append("</tr>");
            }
            sb.append("</table>");
            
            if (!tieneHabitaciones) {
                sb.append("<p style='text-align: center; color: gray;'>No hay habitaciones cargadas en la BD.</p>");
            }
            
            sb.append("</body></html>");
            return sb.toString();
            
        } catch (SQLException e) {
            System.err.println("Error al listar habitaciones: " + e.getMessage());
            return "<html><body>❌ Error técnico al consultar el estado de habitaciones.</body></html>";
        }
    }
}