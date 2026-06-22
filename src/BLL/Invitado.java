package BLL;

import java.time.LocalDateTime;

import GUI.InvitadoMenuGrafico;

public class Invitado extends Persona {
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String tokenAcceso; 
    private boolean asistenciaConfirmada;
    private LocalDateTime fechaConfirmacion;
    private Reserva reserva;      
    private Habitacion habitacion;
    
    // Constructor 1: Básico
    public Invitado(String email, String password, String nombre, Rol rol) {
        super(email, password, rol); 
        this.nombre = nombre;
        this.apellido = ""; 
        this.asistenciaConfirmada = false;
    }

    // Constructor 2: Registro con Apellido
    public Invitado(String email, String password, String nombre, String apellido, Rol rol) {
        super(email, password, rol);
        this.nombre = nombre;
        this.apellido = apellido;
        this.asistenciaConfirmada = false;
    }

    // Constructor 3: Desde Base de Datos
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

    @Override
    public String getNombre() {
        return nombre + (apellido != null && !apellido.isEmpty() ? " " + apellido : "");
    }

    public String getNombreSolo() {
        return this.nombre;
    }

    @Override
    public void mostrarMenu() {
        // SRP Cumplido: Delegamos la responsabilidad visual por completo a la GUI
        java.awt.EventQueue.invokeLater(() -> {
            try {
                InvitadoMenuGrafico ventana = new InvitadoMenuGrafico(this);
                ventana.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // --- Getters & Setters ---
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTokenAcceso() { 
        return (tokenAcceso == null || tokenAcceso.trim().isEmpty()) ? "Sin generar" : tokenAcceso; 
    }
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