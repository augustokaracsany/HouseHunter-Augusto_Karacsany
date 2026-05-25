package DLL; // Capa de Acceso a Datos / Controladores de Flujo. < DLL.

import BLL.Persona;
import Repository.Hashing; // Ajustar importación según dónde esté tu clase Hashing
import Repository.UsuariosController;

public class AutenticacionController {
    private static AutenticacionController instance; // Instancia única global (Singleton).
    private final UsuariosController usuarioRepository; // Conector directo a la capa de datos.

    // Constructor privado: bloquea el 'new' desde afuera para evitar múltiples gestores de sesión.
    private AutenticacionController() {
        this.usuarioRepository = new UsuariosController();
    }

    // Punto de acceso único para invocar la autenticación desde el Main.
    public static AutenticacionController getInstance() {
        if (instance == null) {
            instance = new AutenticacionController();
        }
        return instance;
    }

    /**
     * Coordina el proceso de Login. Solicita los datos al repositorio
     * y ejecuta la validación lógica de seguridad (verificación del hash).
     */
    public Persona iniciarSesion(String email, String password) {
        // 1. Delegamos la búsqueda física a la capa de persistencia (MySQL)
        Persona usuarioBD = usuarioRepository.obtenerUsuarioPorEmail(email);

        // If de control: si el mail no existe en la base de datos, frena acá.
        if (usuarioBD == null) {
            System.out.println("❌ Autenticación: No se encontró ningún usuario con el email: " + email);
            return null;
        }

        // 2. Ejecuta la verificación del hash utilizando el método Getter público de Persona
        if (Hashing.verificar(password, usuarioBD.getPassword())) {
            System.out.println("ℹ️ Autenticación: Login exitoso para el usuario: " + email);
            return usuarioBD; // Retorna la instancia polimórfica (Empresa, Administrador, Invitado).
        } else {
            System.out.println("❌ Autenticación: Contraseña incorrecta para el usuario: " + email);
            return null;
        }
    }
}