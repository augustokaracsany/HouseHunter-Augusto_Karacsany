package BLL; 

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reserva {
    // ( Atributos. )
    private int id;
    private Empresa empresa;          // Objeto empresa ( Dueño de la reserva. )
    private LocalDateTime fechaReserva; // Fecha y hora exacta en la que se registra la operación.
    private LocalDate fechaEvento;    // Día del evento (AAAA-MM-DD), clave para verificar la disponibilidad en el hotel.
    private int numInvitados;         // Cantidad estimada para dimensionar salones y catering.
    private String estado;            // PENDIENTE, CONFIRMADA, CANCELADA ( Estado de la transacción. ) < Esto lo deberia hacer Enum. - Augusto.
    private Plantilla plantilla;      // Maqueta visual seleccionada para el branding del evento.
    
    // ( Relaciones 1:N )
    // Colecciones en memoria para manejar el grafo de objetos vinculados a esta reserva.
    private List<Actividad> actividades;
    private List<Invitado> invitados;

    // Constructor por defecto. Inicializa marcas de tiempo y colecciones para evitar un NullPointerException.
    public Reserva() {
        this.fechaReserva = LocalDateTime.now(); // Marca temporal automática del sistema.
        this.estado = "PENDIENTE"; // Nace en stand-by hasta que la administración verifique el pago.
        this.actividades = new ArrayList<>(); // Instancia obligatoria de las listas para poder usar el método .add() después. 
        this.invitados = new ArrayList<>();
    }
    
    // Constructor completo para el alta rápida desde el panel de Menu Empresa.
    public Reserva(Empresa empresa, LocalDate fechaEvento, int numInvitados) {
        this(); // Reutiliza el constructor por defecto de arriba para heredar las inicializaciones de las listas y la fecha.
        this.empresa = empresa;
        this.fechaEvento = fechaEvento;
        this.numInvitados = numInvitados;
    }
    
    
    // ( Getters y Setters unificados. )
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    
    public LocalDateTime getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDateTime fechaReserva) { this.fechaReserva = fechaReserva; }
    
    public LocalDate getFechaEvento() { return fechaEvento; }
    public void setFechaEvento(LocalDate fechaEvento) { this.fechaEvento = fechaEvento; }
    
    public int getNumInvitados() { return numInvitados; }
    public void setNumInvitados(int numInvitados) { this.numInvitados = numInvitados; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public Plantilla getPlantilla() { return plantilla; }
    public void setPlantilla(Plantilla plantilla) { this.plantilla = plantilla; }
    
    public List<Actividad> getActividades() { return actividades; }
    public void setActividades(List<Actividad> actividades) { this.actividades = actividades; }
    
    public List<Invitado> getInvitados() { return invitados; }
    public void setInvitados(List<Invitado> invitados) { this.invitados = invitados; }
}