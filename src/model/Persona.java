package model;

public abstract class Persona {


    protected String email;
    protected String password;
    protected String nombre;
    protected String rol;

    public Persona(String email, String password, String nombre, String rol) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getNombre() { return nombre; }
    public String getRol() { return rol; }

    // Metodo abstracto para cada subclase
    public abstract void mostrarMenu();

    @Override
    public String toString() {
        return rol + ": " + nombre + " (" + email + ")";
    }
}

