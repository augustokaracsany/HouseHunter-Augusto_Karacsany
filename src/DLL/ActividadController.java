package DLL; // Capa de Acceso a Datos / Controladores directos a la BD. < DLL.

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;


 // Centraliza las consultas SQL y las modificaciones sobre las actividades del hotel.
 // ( Singleton. )

public class ActividadController {
    private static ActividadController instance;

    // ( Constructor privado. )
    private ActividadController() {}

    // Punto de acceso global único para el controlador en toda la ejecución del Sistema.
    public static ActividadController getInstance() {
        if (instance == null) {
            instance = new ActividadController();
        }
        return instance;
    }

     // Construye un bloque HTML dinámico para inyectar directo en un JOptionPane.
     // Realiza un JOIN relacional para aislar las actividades mediante el token único del evento.
     
    public String obtenerCronogramaEventos(String codigoEvento) {
        StringBuilder cronograma = new StringBuilder();
        String sql = "SELECT a.nombre, a.descripcion, a.importancia, a.categoria, a.hora_actividad " +
                     "FROM actividades a " +
                     "JOIN reservas_hotel rh ON a.id_reserva = rh.id " +
                     "WHERE rh.codigo_unico_evento = ? " +
                     "ORDER BY a.hora_actividad ASC";

        Connection con = ConexionController.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoEvento); // Sanitización de entrada contra ataques de SQL Injection.
            try (ResultSet rs = ps.executeQuery()) {
                cronograma.append("<html><body style='width: 320px;'>");
                cronograma.append("<h2 style='text-align: center; color: #1a5f7a;'>📅 Cronograma de Actividades</h2>");
                cronograma.append("<p style='text-align: center;'><b>Evento:</b> ").append(codigoEvento).append("</p><hr>");

                boolean tieneActividades = false;
                while (rs.next()) {
                    tieneActividades = true;
                    // Recorta los segundos del Time de SQL para no ensuciar la interfaz visual ( HH:MM ).
                    String hora = rs.getTime("hora_actividad").toString().substring(0, 5);
                    String nombre = rs.getString("nombre");
                    String desc = rs.getString("descripcion");
                    String importancia = rs.getString("importancia");
                    String categoria = rs.getString("categoria");

                    // Lógica de renderizado condicional según el nivel de prioridad del evento.
                    String colorImportancia = "gray";
                    if (importancia.equalsIgnoreCase("Alta")) colorImportancia = "red";
                    else if (importancia.equalsIgnoreCase("Media")) colorImportancia = "orange";

                    cronograma.append("<p style='margin-bottom: 2px;'><b>⏱️ ").append(hora).append(" hs</b> - ").append(nombre).append("</p>");
                    cronograma.append("<p style='margin-left: 15px; color: #555; margin-top: 0px;'><i>").append(desc != null ? desc : "Sin descripción").append("</i><br>");
                    cronograma.append("<small>📁 Cat: ").append(categoria).append(" | <font color='").append(colorImportancia).append("'>🔥 ").append(importancia).append("</font></small></p>");
                    cronograma.append("<hr style='border-top: 1px dashed #ccc;'>");
                }
                cronograma.append("</body></html>");
                // Operador para resolver el flujo de retorno si la query vino vacía.
                return tieneActividades ? cronograma.toString() : "<html><body>❌ Sin actividades en este evento.</body></html>";
            }
        } catch (SQLException e) {
            return "<html><body>❌ Error técnico al consultar cronograma.</body></html>";
        }
    }

     // Recupera una lista plana de nombres para poblar los vectores de opciones de la UI.
     
    public String[] obtenerNombresActividades(String codigoEvento) {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT a.nombre FROM actividades a JOIN reservas_hotel rh ON a.id_reserva = rh.id WHERE rh.codigo_unico_evento = ?";
        Connection con = ConexionController.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoEvento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getString("nombre")); // Almacenamiento.
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nombres de actividades: " + e.getMessage());
        }
        return lista.toArray(new String[0]); // Convierte la lista flexible a un array estático clásico de Java.
    }

    
     // Transacción: Valida la existencia cruzada del invitado y de la actividad.
     // Si pasa los filtros, inserta o actualiza la Asistencia usando sintaxis nativa de MySQL.
     
    public boolean registrarAsistenciaActividad(String codigoEvento, String nombreActividad, String dniInvitado) {
        Connection con = ConexionController.getInstance().getConnection();
        
        // Query de verificación con un cuádruple JOIN para asegurar consistencia e integridad referencial.
        String sqlIds = "SELECT a.id AS id_act_real, dp.id_usuario AS id_user_real " +
                        "FROM reservas_hotel rh " +
                        "JOIN actividades a ON a.id_reserva = rh.id " +
                        "JOIN lista_invitados_previa lip ON lip.id_reserva = rh.id " +
                        "JOIN datos_personas dp ON lip.dni = dp.dni " +
                        "WHERE rh.codigo_unico_evento = ? AND a.nombre = ? AND lip.dni = ?";
        
        // Mecanismo "Upsert" ( INSERT ... ON DUPLICATE KEY UPDATE ) para evitar duplicaciones físicas en la tabla intermedia. 
        String sqlInsertAsistencia = "INSERT INTO asistencias_actividades (id_actividad, id_usuario, asistio) VALUES (?, ?, 'S') " +
                                     "ON DUPLICATE KEY UPDATE asistio = 'S'";

        try {
            int idActividad = 0, idUsuario = 0;
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

            // Flag de control: si las PK lógicas se mantienen en cero, el usuario no pertenece a la lista autorizada.
            if (idActividad == 0 || idUsuario == 0) {
                JOptionPane.showMessageDialog(null, "❌ El DNI no corresponde a un invitado autorizado.", "Validación Fallida.", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Ejecución del impacto en la base de datos tras superar las validaciones de negocio.
            try (PreparedStatement ps = con.prepareStatement(sqlInsertAsistencia)) {
                ps.setInt(1, idActividad);
                ps.setInt(2, idUsuario);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(null, "✅ Asistencia registrada con éxito.");
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

   // ( CRUD / ABM para Empresa. )
    // Inserción directa de un nuevo registro en la tabla 'actividades'.
    public boolean guardarActividad(int idReserva, String nombre, String desc, String importancia, String categoria, String hora) {
        String sql = "INSERT INTO actividades (id_reserva, nombre, descripcion, hora_actividad, importancia, categoria) VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = ConexionController.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setString(2, nombre);
            ps.setString(3, desc);
            ps.setString(4, hora); // Recibe el formato HH:MM como String y la BD lo castea implícitamente a TIME.
            ps.setString(5, importancia);
            ps.setString(6, categoria);
            return ps.executeUpdate() > 0; // Retorna true si la fila fue afectada con éxito en el motor de almacenamiento.
        } catch (SQLException e) {
            return false;
        }
    }

    // Eliminación física de una actividad por su identificador único ID.
    public boolean eliminarActividad(int idActividad) {
        String sql = "DELETE FROM actividades WHERE id = ?";
        Connection con = ConexionController.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idActividad);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}