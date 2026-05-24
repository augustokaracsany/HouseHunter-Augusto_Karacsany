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
        
        // Armo la query cruzando las actividades con la reserva mediante un INNER JOIN 
        // usando el código único del evento corporativo, y lo ordeno cronológicamente.
        String sql = "SELECT a.nombre, a.descripcion, a.importancia, a.categoria, a.hora_actividad " +
                     "FROM actividades a " +
                     "JOIN reservas_hotel rh ON a.id_reserva = rh.id " +
                     "WHERE rh.codigo_unico_evento = ? " +
                     "ORDER BY a.hora_actividad ASC";

        Connection con = ConexionController.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoEvento);

            try (ResultSet rs = ps.executeQuery()) {
                // Inicio el armado de la estructura del documento HTML para que JOptionPane lo renderice estéticamente.
                cronograma.append("<html><body style='width: 320px;'>");
                cronograma.append("<h2 style='text-align: center; color: #1a5f7a;'>📅 Cronograma de Actividades</h2>");
                cronograma.append("<p style='text-align: center;'><b>Evento:</b> ").append(codigoEvento).append("</p><hr>");

                boolean tieneActividades = false;
                
                // Recorro los registros devueltos por la base de datos fila por fila.
                while (rs.next()) {
                    tieneActividades = true;
                    
                    // Recorto los segundos de la hora para quedarme sólo con el formato HH:mm.
                    String hora = rs.getTime("hora_actividad").toString().substring(0, 5);
                    String nombre = rs.getString("nombre");
                    String desc = rs.getString("descripcion");
                    String importancia = rs.getString("importancia");
                    String categoria = rs.getString("categoria");

                    // Defino un color dinámico para la etiqueta según el nivel de criticidad o importancia.
                    String colorImportancia = "gray";
                    if (importancia.equalsIgnoreCase("Alta")) {
                        colorImportancia = "red";
                    } else if (importancia.equalsIgnoreCase("Media")) {
                        colorImportancia = "orange";
                    }

                    // Inyecto los datos de la actividad actual en las etiquetas HTML correspondientes.
                    cronograma.append("<p style='margin-bottom: 2px;'><b>⏱️ ").append(hora).append(" hs</b> - ").append(nombre).append("</p>");
                    cronograma.append("<p style='margin-left: 15px; color: #555; margin-top: 0px;'><i>").append(desc != null ? desc : "Sin descripción").append("</i><br>");
                    cronograma.append("<small>📁 Cat: ").append(categoria)
                              .append(" | <font color='").append(colorImportancia).append("'>🔥 ").append(importancia).append("</font></small></p>");
                    cronograma.append("<hr style='border-top: 1px dashed #ccc;'>");
                }

                cronograma.append("</body></html>");

                // Valido si el evento no existe o simplemente carece de itinerario asignado en la BD.
                if (!tieneActividades) {
                    return "<html><body>❌ No se encontraron actividades registradas para el código de evento ingresado.</body></html>";
                }

                return cronograma.toString();
            }
        } catch (SQLException e) {
            // Registro el error de SQL de manera interna y devuelvo un aviso genérico pero seguro para la interfaz.
            System.err.println("Error al obtener el cronograma: " + e.getMessage());
            return "<html><body>❌ Error técnico al consultar la base de datos.</body></html>";
        }
    }
}