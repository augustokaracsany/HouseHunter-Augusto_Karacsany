package DLL; // Capa de Acceso a Datos / Controladores directos a la BD. < DLL.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionController {
    private static Connection conect; // Objeto de conexión nativo de JDBC que se comparte en toda el Sistema HouseHunter.
    private static ConexionController instance; // Instancia única del controlador 
    // ( Patrón Singleton ).

    // ( Constructor Privado. ) 
    // Bloquea el 'new' desde afuera y fuerza la conexión inicial automática al instanciarse.
    private ConexionController() {
        conectar();
    }
 // En teoría con una sola conexión alcanza para el TP.
    // Gestiona el puente de comunicación físico con el motor MySQL mediante el Driver JDBC.
    private void conectar() {
        try {
            // String de conexión apuntando al entorno local. Base de datos: househunter. Usuario: root sin password.
            conect = DriverManager.getConnection("jdbc:mysql://localhost:3306/househunter", "root", "");
            System.out.println(">> Conexión Establecida con Éxito a la Base de Datos.");
        } catch (SQLException e) {
            System.err.println(">> ERROR de conexión: " + e.getMessage()); // No tocar mucho esto que después JDBC se pone en modo violento.
            conect = null; // Resetea el puntero a null para que los métodos de control reconozcan el estado fallido.
        }
    }
// Reemplazar con BD de Clever Cloud. > clever.cloud
    // Usar Vercel. > vercel.com
    // Actualizar la librería de la conexión. El mysql-connector-java-5.1.13 no funciona con la conexión a la base de
    // datos remota, y el mysql-connector-java-nuevo no funciona con la conexión vieja.
    // Garantiza que exista una sola instancia de ConexionController en memoria.
    // Click der. > Export. > Java. > Runable .JAR file.
    // Runnear el proyecto en Main antes de Exportar a .jar.
    // Investigar que es PWA.
    // Stitch.
    // v0.app.
    // Aprender javascript.
    // midudev. 
    // mouredev.
    // dot dager.
    // soy dalto.
    // gentleman programming.
    // nextjs nodejs react 3djs
    public static ConexionController getInstance() { 
        if (instance == null) instance = new ConexionController();
        return instance;
    }

    // Provee la instancia activa de la conexión. 
    // Incluye una validación de ciclo de vida para evitar caídas por timeout.
    public Connection getConnection() {
        try {
            // Si la conexión es nula o está cerrada, reconectar.
            // Mecanismo de tolerancia a fallos > Reabre la tubería si MySQL mató la sesión por inactividad.
            if (conect == null || conect.isClosed()) {
                System.out.println(">> Conexión cerrada o nula. Reconectando...");
                conectar();
            }
        } catch (SQLException e) {
            System.err.println(">> Error verificando estado de conexión: " + e.getMessage());
            conectar(); // Intento de recuperación forzado ante excepciones de red.
        }
        return conect; // Retorna el descriptor de conexión listo para inyectarse en los PreparedStatement.
    }
}