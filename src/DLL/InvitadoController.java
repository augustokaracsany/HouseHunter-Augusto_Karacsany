package DLL;

import BLL.Habitacion;
import BLL.EstadoHabitacion; 
import BLL.Invitado;
import BLL.Reserva;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvitadoController {
    private static InvitadoController instance;

    private InvitadoController() {}

    public static InvitadoController getInstance() {
        if (instance == null) {
            instance = new InvitadoController();
        }
        return instance;
    }

    public boolean cargarInvitados(int idReserva, List<Invitado> invitados) {
        String sql = "INSERT INTO lista_invitados_previa (id_reserva, nombre, apellido, celular, dni, dni_companero) VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = ConexionController.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (Invitado inv : invitados) {
                    ps.setInt(1, idReserva);
                    ps.setString(2, inv.getNombreSolo()); // Usá getNombreSolo() para no concatenar apellido acá
                    ps.setString(3, inv.getApellido());
                    
                    String tel = inv.getTelefono();
                    ps.setString(4, (tel == null) ? "" : tel.trim());
                    
                    ps.setString(5, inv.getDni());
                    ps.setString(6, ""); 
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            con.commit();
            return true;
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean validarDatosInvitado(Invitado inv) {
        return inv.getNombre() != null && !inv.getNombre().trim().isEmpty()
                && inv.getDni() != null && !inv.getDni().trim().isEmpty();
    }

    public String generarTokenUnico() {
        int pin = 1000 + (int)(Math.random() * 9000);
        return String.valueOf(pin);
    }

    public List<Invitado> listarInvitadosPorReserva(int idReserva) {
        List<Invitado> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido, dni, celular, token_acceso FROM lista_invitados_previa WHERE id_reserva = ?";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Invitado inv = new Invitado(
                            rs.getInt("id"),
                            "", 
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("dni"),
                            rs.getString("celular"),
                            rs.getString("token_acceso"), 
                            false
                    );
                    lista.add(inv);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    // =========================================================================
    // REFACTOR CRÍTICO: Sincroniza el ID de lista previa con el ID de Usuario Real
    // =========================================================================
    public Invitado validarToken(String token) {
        String sql = "SELECT lip.*, rh.fecha_inicio, rh.fecha_fin, dp.id_usuario AS id_usuario_real " +
                     "FROM lista_invitados_previa lip " +
                     "JOIN reservas_hotel rh ON lip.id_reserva = rh.id " +
                     "LEFT JOIN datos_personas dp ON lip.dni = dp.dni " + // Cruzamos por DNI para buscar si ya existe el usuario formal
                     "WHERE lip.token_acceso = ?";
                     
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    
                    boolean yaConfirmo = "S".equalsIgnoreCase(rs.getString("asistencia_confirmada"));

                    Invitado inv = new Invitado(
                        rs.getInt("id"), // Conservamos el ID de lista_invitados_previa por compatibilidad de UI
                        "", 
                        rs.getString("nombre"),
                        rs.getString("apellido"), 
                        rs.getString("dni"), 
                        rs.getString("celular"),
                        token, 
                        yaConfirmo 
                    );
                    
                    // COMODÍN DE IDENTIDAD: Si el usuario ya está registrado en el sistema,
                    // sobreescribimos el ID con su ID real de la tabla 'usuarios' para evitar errores en cascada (Sorteos).
                    int idUsuarioReal = rs.getInt("id_usuario_real");
                    if (idUsuarioReal > 0) {
                        inv.setId(idUsuarioReal);
                    }
                    
                    Reserva r = new Reserva();
                    r.setId(rs.getInt("id_reserva"));
                    r.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                    r.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                    inv.setReserva(r);
                    
                    return inv;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean confirmarAsistencia(int idUsuarioReal) {
        // Este query mágico actualiza 'usuarios' y 'lista_invitados_previa' cruzándolas por el DNI de datos_personas
        String sql = "UPDATE usuarios u " +
                     "JOIN datos_personas dp ON u.id = dp.id_usuario " +
                     "JOIN lista_invitados_previa lip ON dp.dni = lip.dni " +
                     "SET u.asistencia_confirmada = 'S', " +
                     "    u.fecha_confirmacion = NOW(), " +
                     "    lip.asistencia_confirmada = 'S' " +
                     "WHERE u.id = ?";
                     
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuarioReal);
            int filasAfectadas = ps.executeUpdate();
            
            // Si modificó registros, significa que la sincronización fue un éxito total
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error crítico al confirmar asistencia en BD: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Habitacion obtenerHabitacionInvitado(int idUsuarioReal) {
        String sql = "SELECT h.* FROM habitaciones h " +
                     "JOIN asignaciones_habitaciones ah ON ah.id_habitacion = h.id " +
                     "WHERE ah.id_usuario = ?";
                     
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuarioReal);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Habitacion(
                        rs.getInt("id"),
                        rs.getString("numero"),
                        "STANDARD", // Colocamos un default o un valor genérico según tu constructor
                        0, 
                        EstadoHabitacion.valueOf(rs.getString("estado"))
                    );
                }
            }
        } catch (SQLException e) { 
            System.err.println("Error al obtener la habitación del invitado: " + e.getMessage());
            e.printStackTrace(); 
        }
        return null;
    }

    public boolean enviarNotificaciones(List<Invitado> listaInvitados) {
        if (listaInvitados == null || listaInvitados.isEmpty()) {
            return false;
        }
        
        String sqlUpdate = "UPDATE lista_invitados_previa SET token_acceso = ? WHERE id = ?";
        
        System.out.println("====== ENVÍO REAL Y PERSISTENCIA DE TOKENS ======");
        try (Connection con = ConexionController.getInstance().getConnection()) {
            con.setAutoCommit(false);
            
            try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                for (Invitado inv : listaInvitados) {
                    String tokenPin = generarTokenUnico(); 
                    
                    ps.setString(1, tokenPin);
                    ps.setInt(2, inv.getId());
                    ps.addBatch();
                    
                    System.out.println(">> Correo simulado a: " + inv.getNombre() + " [DNI: " + inv.getDni() + "]");
                    System.out.println("   PIN Real guardado en BD: " + tokenPin);
                    System.out.println("----------------------------------------");
                }
                ps.executeBatch();
            }
            con.commit();
            System.out.println("=================================================");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al persistir los tokens: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}