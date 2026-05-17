package Repository;

import BLL.*;
import DLL.ConexionController;
import java.sql.*;
import java.util.LinkedList;

public class UsuariosController extends UsuariosRepository {

    @Override
    public Persona login(String email, String password) {
        Persona usuario = null;
        
        // Buscamos al usuario únicamente por su email para traer su información y el hash.
        String sql = "SELECT u.*, p.dni, p.nombre, p.apellido, e.cuit, e.razon_social " +
                     "FROM usuarios u " +
                     "LEFT JOIN datos_personas p ON u.id = p.id_usuario " +
                     "LEFT JOIN datos_empresas e ON u.id = e.id_usuario " +
                     "WHERE u.email = ?";
        
        Connection con = ConexionController.getInstance().getConnection();
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // 1. Extraemos el hash guardado en la base de datos.
                    String passHasheadaBD = rs.getString("password");

                    // 2. Usamos la interfaz de Gamaliel para verificar la Clave.
                    if (Hashing.verificar(password, passHasheadaBD)) {
                        
                        Rol rolEnum = Rol.valueOf(rs.getString("rol"));
                        String mail = rs.getString("email");

                        // 3. Si coincide, instanciamos la subclase correspondiente.
                        if (rolEnum == Rol.EMPRESA) {
                            usuario = new Empresa(mail, passHasheadaBD, rs.getString("cuit"), rs.getString("razon_social"), rolEnum);
                        } else if (rolEnum == Rol.ADMINISTRADOR) {
                            String nombreCompleto = rs.getString("nombre") + " " + rs.getString("apellido");
                            usuario = new Administrador(mail, passHasheadaBD, nombreCompleto, rs.getString("dni"), rolEnum);
                        } else {
                            usuario = new Invitado(mail, passHasheadaBD, rs.getString("nombre"), rolEnum);
                        }
                        
                        usuario.setId(rs.getInt("id"));
                        System.out.println("ℹ️ Hashing: Login exitoso para el usuario: " + mail);
                        
                    } else {
                        System.out.println("❌ Hashing: Contraseña incorrecta para el usuario: " + email);
                    }
                } else {
                    System.out.println("❌ Hashing: No se encontró ningún usuario con el email: " + email);
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