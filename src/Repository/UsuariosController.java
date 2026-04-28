package Repository;
import BLL.*;
import DLL.ConexionController;
import java.sql.*;
import java.util.LinkedList;

public class UsuariosController extends UsuariosRepository {

    @Override
    public Persona login(String email, String password) {
        Persona usuario = null;
        String sql = "SELECT * FROM usuarios WHERE email = ? AND password = ?";
        
        // Obtenemos la conexión fuera del try
        Connection con = ConexionController.getInstance().getConnection();
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String rolStr = rs.getString("rol");
                    Rol rolEnum = Rol.valueOf(rolStr);
                    
                    // Instanciamos según el rol
                    if (rolEnum == Rol.ADMINISTRADOR) usuario = new Administrador(rs.getString("email"), rs.getString("password"), rs.getString("nombre"), rolEnum);
                    else if (rolEnum == Rol.EMPRESA) usuario = new Empresa(rs.getString("email"), rs.getString("password"), rs.getString("nombre"), rolEnum);
                    else usuario = new Invitado(rs.getString("email"), rs.getString("password"), rs.getString("nombre"), rolEnum);
                    
                    usuario.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en login: " + e.getMessage());
        }
        return usuario;
    }

    @Override
    public LinkedList<Persona> listarTodos() {
        LinkedList<Persona> lista = new LinkedList<>();
        String sql = "SELECT * FROM usuarios";
        Connection con = ConexionController.getInstance().getConnection();
        
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Rol rolEnum = Rol.valueOf(rs.getString("rol"));
                Persona p;
                if (rolEnum == Rol.ADMINISTRADOR) p = new Administrador(rs.getString("email"), rs.getString("password"), rs.getString("nombre"), rolEnum);
                else if (rolEnum == Rol.EMPRESA) p = new Empresa(rs.getString("email"), rs.getString("password"), rs.getString("nombre"), rolEnum);
                else p = new Invitado(rs.getString("email"), rs.getString("password"), rs.getString("nombre"), rolEnum);
                
                p.setId(rs.getInt("id"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar: " + e.getMessage());
        }
        return lista;
    }
}