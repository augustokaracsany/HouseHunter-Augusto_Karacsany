package DLL; // Capa de Acceso a Datos / Controladores de Flujo. < DLL.

import BLL.Rol;
import Repository.UsuariosController;

public class RegistroController {
    private static RegistroController instance; // Instancia única global (Singleton).
    private final UsuariosController usuarioRepository; // Conector directo a la capa de datos.

    // Constructor privado: inicializa la conexión con el repositorio de usuarios.
    private RegistroController() {
        this.usuarioRepository = new UsuariosController();
    }

    // Punto de acceso global único para el proceso de alta.
    public static RegistroController getInstance() {
        if (instance == null) {
            instance = new RegistroController();
        }
        return instance;
    }

    /**
     * Procesa la lógica de negocio previa al guardado físico de un usuario.
     * @return true si la inserción en cascada y la transacción SQL fueron exitosas.
     */
    public boolean procesarRegistro(String email, String password, Rol rol, String datoPrincipal, String datoSecundario, String datoTerciario) {
        
        // 1. Filtros de validación lógica previa (Campos vacíos o nulos)
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("⚠️ Registro: Intento de alta con credenciales vacías.");
            return false;
        }

        if (rol == null) {
            System.out.println("⚠️ Registro: No se definió un Rol válido.");
            return false;
        }

        // 2. Delegación a la capa de persistencia
        // Llama al método 'registrar' que encripta la clave y maneja los commits/rollbacks en MySQL.
        boolean resultadoExitoso = usuarioRepository.registrar(email, password, rol, datoPrincipal, datoSecundario, datoTerciario);

        if (resultadoExitoso) {
            System.out.println("ℹ️ Registro: Transacción completada con éxito para: " + email);
            return true;
        } else {
            System.out.println("❌ Registro: El repositorio rechazó la operación (Email/DNI duplicado o caída de BD).");
            return false;
        }
    }
}