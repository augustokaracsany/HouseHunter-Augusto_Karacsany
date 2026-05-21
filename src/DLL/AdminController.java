package DLL;

import java.sql.*;
import javax.swing.JOptionPane;

public class AdminController {
    private static AdminController instance;

    private AdminController() {}

    public static AdminController getInstance() {
        if (instance == null) {
            instance = new AdminController();
        }
        return instance;
    }

    
     // Valida si un DNI pertenece a la lista de invitados previa cargada por la Empresa.
     // Devuelve 'true' si está Autorizado, y 'false' si no figura en la lista.
     
    public boolean validarInvitadoPrevia(String codigoEvento, String dni) {
        String sql = "SELECT lip.* FROM lista_invitados_previa lip " +
                     "JOIN reservas_hotel rh ON lip.id_reserva = rh.id " +
                     "WHERE rh.codigo_unico_evento = ? AND lip.dni = ?";
        
        Connection con = ConexionController.getInstance().getConnection();
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoEvento);
            ps.setString(2, dni);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Retorna true si encontró al invitado en la lista de la empresa
            }
        } catch (SQLException e) {
            System.err.println("Error al validar lista previa: " + e.getMessage());
            return false;
        }
    }
}