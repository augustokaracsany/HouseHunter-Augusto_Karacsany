package BLL;

import java.time.LocalDateTime;

public class Invitado extends Persona {
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String tokenAcceso;
    private boolean asistenciaConfirmada;
    private LocalDateTime fechaConfirmacion;
    private Reserva reserva;
    private Habitacion habitacion;  // si ya tienes esta clase

    // ========== CONSTRUCTORES ==========
    // 1. Constructor original (usado por login)
    public Invitado(String email, String password, String nombre, Rol rol) {
        super(email, password, rol);
        this.nombre = nombre;
        this.apellido = "";
        this.asistenciaConfirmada = false;
    }

    // 2. Constructor completo para nuevos registros (nombre + apellido)
    public Invitado(String email, String password, String nombre, String apellido, Rol rol) {
        super(email, password, rol);
        this.nombre = nombre;
        this.apellido = apellido;
        this.asistenciaConfirmada = false;
    }

    // 3. Constructor para recuperar desde BD (sin password)
    public Invitado(int id, String email, String nombre, String apellido, String dni, String telefono, String tokenAcceso, boolean asistenciaConfirmada) {
        super(email, "", Rol.INVITADO);
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.tokenAcceso = tokenAcceso;
        this.asistenciaConfirmada = asistenciaConfirmada;
    }

    // ========== MÉTODOS ==========
    @Override
    public String getNombre() {
        return nombre + (apellido != null && !apellido.isEmpty() ? " " + apellido : "");
    }

    @Override
    public void mostrarMenu() {
        // Aquí va el menú original del invitado (el que ya tenías con JOptionPane)
        // Si no lo tienes, puedes copiarlo del archivo original que me mostraste al principio.
        // Por ahora, llamamos a la versión anterior para no romper.
        // (Si el original existe, se ejecutará; si no, habrá que implementarlo)
        // Para evitar error, pongo una implementación mínima:
        javax.swing.JOptionPane.showMessageDialog(null, "Menú de invitado (pendiente de implementar)");
    }

    // ========== GETTERS Y SETTERS ==========
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getTokenAcceso() { return tokenAcceso; }
    public void setTokenAcceso(String tokenAcceso) { this.tokenAcceso = tokenAcceso; }
    public boolean isAsistenciaConfirmada() { return asistenciaConfirmada; }
    public void setAsistenciaConfirmada(boolean asistenciaConfirmada) { this.asistenciaConfirmada = asistenciaConfirmada; }
    public LocalDateTime getFechaConfirmacion() { return fechaConfirmacion; }
    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) { this.fechaConfirmacion = fechaConfirmacion; }
    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    public Habitacion getHabitacion() { return habitacion; }
    public void setHabitacion(Habitacion habitacion) { this.habitacion = habitacion; }
}