package BLL; // Capa de Lógica de Negocio. < BLL.


// Business Logic Layer. = BLL.
public class Habitacion {
    // ( Atributos. )
    private int id;
    private String numero;
    private String tipo;          // Traído de la branch de 'feat/luca'.
    private int capacidad;        // Traído de 'feat/luca'.
    private EstadoHabitacion estado; // Diseño de Augusto. ( Enum. )

    // ( Constructor completo unificado. )
    public Habitacion(int id, String numero, String tipo, int capacidad, EstadoHabitacion estado) {
        this.id = id; // 'this' diferencia el atributo de la clase del parámetro que entra.
        this.numero = numero;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.estado = estado; // Inyecta el Enum con el estado inicial ( Libre, Half, Completa. ).
    }

    // ( Getters y Setters unificados y ordenados. )
    // ( Encapsulamiento. ) Los atributos son privados, se accede o modifica sí o sí por acá.
    // Los 4 Pilares de la Programación Orientada a Objetos. > Encapsulación, Herencia, Polimorfismo y Abstracción.
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getTipo() { return tipo; } // Ej: "Suite", "Standard", "Compartida".
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getCapacidad() { return capacidad; } // Cantidad máxima de huéspedes que entran.
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public EstadoHabitacion getEstado() { return estado; }
    public void setEstado(EstadoHabitacion estado) { this.estado = estado; }
}