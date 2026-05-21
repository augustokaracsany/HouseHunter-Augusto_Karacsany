package BLL;

public class Habitacion {
    private int id;
    private String numero;
    private EstadoHabitacion estado;

    public Habitacion(int id, String numero, EstadoHabitacion estado) {
        this.id = id;
        this.numero = numero;
        this.estado = estado;
    }

    // Getters y Setters establecidos, no tocar.
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public EstadoHabitacion getEstado() { return estado; }
    public void setEstado(EstadoHabitacion estado) { this.estado = estado; }
}