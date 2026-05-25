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

    // Apunta a la tabla física real: 'lista_invitados_previa'
    public boolean cargarInvitados(int idReserva, List<Invitado> invitados) {
        String sql = "INSERT INTO lista_invitados_previa (id_reserva, nombre, apellido, celular, dni, dni_companero) VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = ConexionController.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (Invitado inv : invitados) {
                    ps.setInt(1, idReserva);
                    ps.setString(2, inv.getNombre());
                    ps.setString(3, inv.getApellido());
                    ps.setString(4, inv.getTelefono());
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

    // CAMBIADO: Ahora genera un PIN numérico de 4 dígitos fácil de tipear
    public String generarTokenUnico() {
        int pin = 1000 + (int)(Math.random() * 9000);
        return String.valueOf(pin);
    }

    // MODIFICADO: Ahora el listado levanta el token real de la base de datos si existe
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
                            rs.getString("token_acceso"), // <--- Mapea el token real de la BD
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

    // REHECHO: Ahora busca el PIN de 4 dígitos directamente en tu columna física
    public Invitado validarToken(String token) {
        String sql = "SELECT lip.*, rh.fecha_inicio, rh.fecha_fin " +
                     "FROM lista_invitados_previa lip " +
                     "JOIN reservas_hotel rh ON lip.id_reserva = rh.id " +
                     "WHERE lip.token_acceso = ?";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Instanciamos el invitado con la info de la lista previa
                    Invitado inv = new Invitado(
                        rs.getInt("id"), 
                        "", // Email de la lista previa si no tiene
                        rs.getString("nombre"),
                        rs.getString("apellido"), 
                        rs.getString("dni"), 
                        rs.getString("celular"),
                        token, 
                        true
                    );
                    
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

    public boolean confirmarAsistencia(int idInvitado) {
        return true;
    }

    public Habitacion obtenerHabitacionInvitado(int idUsuario) {
        String sql = "SELECT h.* FROM habitaciones h " +
                     "JOIN asignaciones_habitaciones ah ON ah.id_habitacion = h.id WHERE ah.id_usuario = ?";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Habitacion(
                        rs.getInt("id"),
                        rs.getString("numero"),
                        rs.getString("tipo"),
                        rs.getInt("capacidad"),
                        EstadoHabitacion.valueOf(rs.getString("estado"))
                    );
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // PERSISTENTE: Ahora genera los PINs de 4 dígitos y los guarda en MySQL
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
                    String tokenPin = generarTokenUnico(); // Genera el PIN de 4 números
                    
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