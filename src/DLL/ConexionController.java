package DLL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionController {
    private static Connection conect;
    private static ConexionController instance;

    private ConexionController() {
        try {
            conect = DriverManager.getConnection("jdbc:mysql://localhost:3306/househunter", "root", "");
            System.out.println(">> Conexión Establecida con Éxito a la Base de Datos.");
        } catch (SQLException e) {
            System.err.println(">> ERROR de conexión: " + e.getMessage());
        }
    }

    public static ConexionController getInstance() {
        if (instance == null) instance = new ConexionController();
        return instance;
    }

    public Connection getConnection() { return conect; }
}