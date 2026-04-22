package model;
import java.util.LinkedList;

public class Usuarios {
    private static LinkedList<Persona> listaUsuarios = null;

    private Usuarios() {
        listaUsuarios = new LinkedList<>();
        // usuarios de prueba
        listaUsuarios.add(new Administrador("admin@hotel.com", "admin123", "Carlos Gomez"));
        listaUsuarios.add(new Empresa("empresa@test.com", "emp123", "Eventos S.A."));
        listaUsuarios.add(new Invitado("invitado@test.com", "inv123", "Laura Fernandez"));
    }

    public static LinkedList<Persona> getInstance() {
        if (listaUsuarios == null) {
            new Usuarios();
        }
        return listaUsuarios;
    }

    // metodo para buscar un usuario por email y contraseña
    public static Persona login(String email, String password) {
        for (Persona p : getInstance()) {
            if (p.getEmail().equals(email) && p.getPassword().equals(password)) {
                return p;
            }
        }
        return null;
    }
}