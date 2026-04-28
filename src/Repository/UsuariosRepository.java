package Repository;
import BLL.Persona;
import java.util.LinkedList;

public abstract class UsuariosRepository {
    // Definimos los métodos que todos los controladores deben tener
    public abstract Persona login(String email, String password);
    public abstract LinkedList<Persona> listarTodos();
}