package Repository;

import BLL.*;
import DLL.ConexionController;
import java.sql.*;
import java.util.LinkedList;

public class UsuariosController extends UsuariosRepository {

    @Override
    public Persona login(String email, String password) {
        Persona usuario = null;
        // SQL con LEFT JOIN para traer datos de las 3 tablas
        String sql = "SELECT u.*, p.dni, p.nombre, p.apellido, e.cuit, e.razon_social " +
                     "FROM usuarios u " +
                     "LEFT JOIN datos_personas p ON u.id = p.id_usuario " +
                     "LEFT JOIN datos_empresas e ON u.id = e.id_usuario " +
                     "WHERE u.email = ? AND u.password = ?";
        
        Connection con = ConexionController.getInstance().getConnection();
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Rol rolEnum = Rol.valueOf(rs.getString("rol"));
                    String mail = rs.getString("email");
                    String pass = rs.getString("password");

                    if (rolEnum == Rol.EMPRESA) {
                        usuario = new Empresa(mail, pass, rs.getString("cuit"), rs.getString("razon_social"), rolEnum);
                    } else if (rolEnum == Rol.ADMINISTRADOR) {
                        String nombreCompleto = rs.getString("nombre") + " " + rs.getString("apellido");
                        usuario = new Administrador(mail, pass, nombreCompleto, rs.getString("dni"), rolEnum);
                    } else {
                        // Para el Invitado
                        usuario = new Invitado(mail, pass, rs.getString("nombre"), rolEnum);
                    }
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
        // El listar todos también necesita los JOINS para no traer objetos vacíos
        String sql = "SELECT u.*, p.dni, p.nombre, p.apellido, e.cuit, e.razon_social " +
                     "FROM usuarios u " +
                     "LEFT JOIN datos_personas p ON u.id = p.id_usuario " +
                     "LEFT JOIN datos_empresas e ON u.id = e.id_usuario";
                     
        Connection con = ConexionController.getInstance().getConnection();
        
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Rol rolEnum = Rol.valueOf(rs.getString("rol"));
                Persona p;
                String mail = rs.getString("email");
                String pass = rs.getString("password");

                if (rolEnum == Rol.EMPRESA) {
                    p = new Empresa(mail, pass, rs.getString("cuit"), rs.getString("razon_social"), rolEnum);
                } else if (rolEnum == Rol.ADMINISTRADOR) {
                    String nombreCompleto = rs.getString("nombre") + " " + rs.getString("apellido");
                    p = new Administrador(mail, pass, nombreCompleto, rs.getString("dni"), rolEnum);
                } else {
                    p = new Invitado(mail, pass, rs.getString("nombre"), rolEnum);
                }
                
                p.setId(rs.getInt("id"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar: " + e.getMessage());
        }
        return lista;
    }
}