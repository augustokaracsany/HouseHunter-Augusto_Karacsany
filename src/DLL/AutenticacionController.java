package DLL;

// Este Controller lo dejamos olvidado muy fuerte hasta ahora.
import BLL.Persona;
import Repository.Hashing; 
import Repository.UsuariosController;

public class AutenticacionController {
    private static AutenticacionController instance;
    private final UsuariosController usuarioRepository;

    private AutenticacionController() {
        // Inyectamos el repositorio de usuarios para poder delegarle las consultas a la BD.
        this.usuarioRepository = new UsuariosController();
    }

    public static AutenticacionController getInstance() {
        if (instance == null) {
            instance = new AutenticacionController();
        }
        return instance;
    }

     // Coordina el proceso de Login. Solicita los datos al repositorio.
     // Ejecuta la validación lógica de seguridad ( La Verificación del hash ).
     
    public Persona iniciarSesion(String email, String password) {
        // 1. Delegamos la búsqueda física a la capa de datos.
        Persona usuarioBD = usuarioRepository.obtenerUsuarioPorEmail(email);

        if (usuarioBD == null) {
            System.out.println("❌ Autenticación: No se encontró ningún usuario con el email: " + email);
            return null;
        }

        // 2. La lógica de negocio de seguridad se ejecuta en el controlador de Autenticación.
        // 'usuarioBD.getPassword()' contiene el hash recuperado de MySQL.
        if (Hashing.verificar(password, usuarioBD.getPassword())) { // < FIXeado. - Augusto.
            System.out.println("ℹ️ Autenticación: Login exitoso para: " + email);
            return usuarioBD; // Retornamos la instancia polimórfica lista para usar.
        } else {
            System.out.println("❌ Autenticación: Contraseña incorrecta para: " + email);
            return null;
        }
    }
}