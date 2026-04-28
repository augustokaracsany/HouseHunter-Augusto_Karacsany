package BLL;

public abstract class Persona {
    protected int id;
    protected String email;
    protected String password;
    protected String nombre;
    protected Rol rol;

    public Persona(String email, String password, String nombre, Rol rol) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.rol = rol;
    }

    // --- GETTERS ---
    public int getId() { return id; } // <--- ESTO FALTABA
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getNombre() { return nombre; }
    public Rol getRol() { return rol; }

    // --- SETTERS ---
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRol(Rol rol) { this.rol = rol; }

    public abstract void mostrarMenu();
}