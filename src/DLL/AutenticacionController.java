package DLL; // Capa de Acceso a Datos / Controladores de Flujo. < DLL.

import BLL.Persona;
import BLL.Reserva;
import Repository.Hashing; 
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

    //
     // Coordina el proceso de Login. Solicita los datos al repositorio
     // y ejecuta la validación lógica de seguridad ( Verificación del hash. ).
     //
    public Persona iniciarSesion(String email, String password) {
        // 1. Delegamos la búsqueda física a la capa de persistencia ( MySQL. )
        Persona usuarioBD = usuarioRepository.obtenerUsuarioPorEmail(email);

        // If de control: si el mail no existe en la base de datos, frena acá.
        if (usuarioBD == null) {
            System.out.println("Autenticacion: No se encontro ningun usuario con el email: " + email);
            return null;
        }

        // 2. Ejecuta la verificación del hash utilizando el método Getter público de Persona.
        if (Hashing.verificar(password, usuarioBD.getPassword())) {
            System.out.println("Autenticacion: Login exitoso para el usuario: " + email);
            
            if (usuarioBD instanceof BLL.Empresa) {
                BLL.Empresa emp = (BLL.Empresa) usuarioBD;
                
                // En lugar de pasar el texto fijo "GLOBANT-2026", usamos método listarReservasPorEmpresa. < ELIMINAMOS el Hardcodeo.
                // que ya existe en tu EventoController para obtener su historial de reservas.
                java.util.List<Reserva> reservas = EventoController.getInstance().listarReservasPorEmpresa(emp.getId());
                
                // Si la empresa tiene al menos una reserva registrada en la base de datos, le asignamos la última.
                if (reservas != null && !reservas.isEmpty()) {
                    Reserva ultimaReserva = reservas.get(0); // Al estar ordenadas por fecha DESC, la primera es la más reciente.

                    ultimaReserva.setActividades(EventoController.getInstance().obtenerActividadesPorReserva(ultimaReserva.getId()));
                    
                    emp.setReservaActual(ultimaReserva);
                    System.out.println("Autenticacion: Reserva '" + ultimaReserva.getCodigoUnicoEvento() + "' acoplada correctamente a la sesion corporativa.");
                }
            }

            return usuarioBD; // Retorna la instancia polimórfica lista para usar en el menú.
        } else {
            System.out.println("Autenticacion: Contrasena incorrecta para el usuario: " + email);
            return null;
        }
    }
}