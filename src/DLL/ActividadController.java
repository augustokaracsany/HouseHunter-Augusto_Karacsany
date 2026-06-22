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
        // CORRECCIÓN: Agregada la columna a.duracion_minutos a la consulta
        String sql = "SELECT a.nombre, a.descripcion, a.importancia, a.categoria, a.hora_actividad, a.duracion_minutos " +
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
                    int duracion = rs.getInt("duracion_minutos"); // CORRECCIÓN: Captura de la duración física real

                    // AJUSTADO: Lógica de renderizado condicional con ENUMs en mayúsculas
                    String colorImportancia = "gray";
                    if (importancia.equalsIgnoreCase("ALTA")) colorImportancia = "red";
                    else if (importancia.equalsIgnoreCase("MEDIA")) colorImportancia = "orange";

                    cronograma.append("<p style='margin-bottom: 2px;'><b>⏱️ ").append(hora).append(" hs</b> (").append(duracion).append(" min) - ").append(nombre).append("</p>");
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
     
// Transacción: Valida la existencia cruzada del invitado y de la actividad.
    // Si pasa los filtros, inserta o actualiza la Asistencia y activa la ELEGIBILIDAD para sorteos de forma automática.
    public boolean registrarAsistenciaActividad(String codigoEvento, String nombreActividad, String dniInvitado) {
        Connection con = ConexionController.getInstance().getConnection();
        
        // Query de verificación con un cuádruple JOIN para asegurar consistencia e integridad referencial.
        String sqlIds = "SELECT a.id AS id_act_real, dp.id_usuario AS id_user_real, rh.id AS id_reserva_real " +
                        "FROM reservas_hotel rh " +
                        "JOIN actividades a ON a.id_reserva = rh.id " +
                        "JOIN lista_invitados_previa lip ON lip.id_reserva = rh.id " +
                        "JOIN datos_personas dp ON lip.dni = dp.dni " +
                        "WHERE rh.codigo_unico_evento = ? AND a.nombre = ? AND lip.dni = ?";
        
        // Mecanismo "Upsert" ( INSERT ... ON DUPLICATE KEY UPDATE ) para evitar duplicaciones físicas en la tabla intermedia. 
        String sqlInsertAsistencia = "INSERT INTO asistencias_actividades (id_actividad, id_usuario, asistio) VALUES (?, ?, 'S') " +
                                     "ON DUPLICATE KEY UPDATE asistio = 'S'";

        // QUERY AUTOMÁTICO: Activa la elegibilidad en los sorteos para este usuario en todos los premios de esta reserva
        String sqlHabilitarSorteo = "UPDATE participaciones_premios pp " +
                                    "JOIN premios p ON pp.id_premio = p.id " +
                                    "SET pp.elegible = 1 " +
                                    "WHERE pp.id_invitado = ? AND p.id_reserva = ?";

        try {
            int idActividad = 0, idUsuario = 0, idReserva = 0;
            try (PreparedStatement ps = con.prepareStatement(sqlIds)) {
                ps.setString(1, codigoEvento);
                ps.setString(2, nombreActividad);
                ps.setString(3, dniInvitado);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idActividad = rs.getInt("id_act_real");
                        idUsuario = rs.getInt("id_user_real");
                        idReserva = rs.getInt("id_reserva_real");
                    }
                }
            }

            // Flag de control: si las PK lógicas se mantienen en cero, el usuario no pertenece a la lista autorizada.
            if (idActividad == 0 || idUsuario == 0) {
                JOptionPane.showMessageDialog(null, "❌ El DNI no corresponde a un invitado autorizado.", "Validación Fallida.", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Iniciamos una pequeña transacción local para asegurar que se hagan ambos pasos o ninguno
            con.setAutoCommit(false);

            // Paso 1: Registrar el presente en la actividad
            try (PreparedStatement ps = con.prepareStatement(sqlInsertAsistencia)) {
                ps.setInt(1, idActividad);
                ps.setInt(2, idUsuario);
                ps.executeUpdate();
            }
            
            // Paso 2: Activar la elegibilidad del sorteo para los premios de esta reserva
            try (PreparedStatement psSorteo = con.prepareStatement(sqlHabilitarSorteo)) {
                psSorteo.setInt(1, idUsuario);
                psSorteo.setInt(2, idReserva);
                psSorteo.executeUpdate();
            }

            con.commit(); // Guardamos los dos cambios de forma segura
            JOptionPane.showMessageDialog(null, "✅ Asistencia registrada. ¡El invitado ya está habilitado para los sorteos!");
            return true;
            
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("Error en transacción de asistencia/elegibilidad: " + e.getMessage());
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
   // ( CRUD / ABM para Empresa. )
    // Inserción directa de un nuevo registro en la tabla 'actividades'.
 // Inserción directa de un nuevo registro incluyendo la duración en minutos
    public boolean guardarActividad(int idReserva, String nombre, String desc, String importancia, String categoria, String hora, int duracionMinutos) {
        String sql = "INSERT INTO actividades (id_reserva, nombre, descripcion, hora_actividad, importancia, categoria, duracion_minutos) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        String importanciaFormateada = "MEDIA";
        if (importancia != null && !importancia.trim().isEmpty()) {
            importanciaFormateada = importancia.trim().toUpperCase();
        }
        
        String categoriaFormateada = "OTROS";
        if (categoria != null && !categoria.trim().isEmpty()) {
            String limpia = categoria.trim().toUpperCase();
            if (limpia.equals("CHARLA")) limpia = "CHARLAS";
            if (limpia.equals("RECREATIVA") || limpia.equals("RECREACIÓN")) limpia = "RECREACION";
            if (limpia.equals("OTRO")) limpia = "OTROS";
            
            categoriaFormateada = limpia;
        }
        
        Connection con = ConexionController.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setString(2, nombre);
            ps.setString(3, desc);
            ps.setString(4, hora); 
            ps.setString(5, importanciaFormateada);
            ps.setString(6, categoriaFormateada);
            ps.setInt(7, duracionMinutos);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en transacción de guardado de cronograma: " + e.getMessage());
            return false;
        }
    }

    public String[][] obtenerCronogramaMatriz(String codigoEvento) {
        String sqlContar = "SELECT COUNT(*) FROM actividades a JOIN reservas_hotel rh ON a.id_reserva = rh.id WHERE rh.codigo_unico_evento = ?";
        // CORRECCIÓN: Agregado a.duracion_minutos al SELECT de la matriz por si tu tabla de JTable la necesita en el futuro.
        String sqlDatos = "SELECT a.nombre, a.descripcion, a.importancia, a.categoria, a.hora_actividad, a.duracion_minutos " +
                          "FROM actividades a " +
                          "JOIN reservas_hotel rh ON a.id_reserva = rh.id " +
                          "WHERE rh.codigo_unico_evento = ? " +
                          "ORDER BY a.hora_actividad ASC";

        Connection con = ConexionController.getInstance().getConnection();
        try {
            int filas = 0;
            try (PreparedStatement ps = con.prepareStatement(sqlContar)) {
                ps.setString(1, codigoEvento);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) filas = rs.getInt(1);
                }
            }

            String[][] matriz = new String[filas][4];

            try (PreparedStatement ps = con.prepareStatement(sqlDatos)) {
                ps.setString(1, codigoEvento);
                try (ResultSet rs = ps.executeQuery()) {
                    int i = 0;
                    while (rs.next()) {
                        String hora = rs.getTime("hora_actividad").toString().substring(0, 5) + " hs";
                        String nombre = rs.getString("nombre");
                        String categoria = rs.getString("categoria");
                        String importancia = rs.getString("importancia");

                        matriz[i][0] = hora;
                        matriz[i][1] = nombre;
                        matriz[i][2] = categoria;
                        matriz[i][3] = importancia;
                        i++;
                    }
                }
            }
            return matriz;

        } catch (SQLException e) {
            System.err.println("Error al generar matriz de cronograma: " + e.getMessage());
            return new String[0][4];
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