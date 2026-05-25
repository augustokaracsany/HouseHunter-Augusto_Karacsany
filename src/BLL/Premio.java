package BLL; // Ya sabemos que es esto a esta altura.

public class Premio {
	// Atributos. 
    private int id;
    private String nombre;
    private String descripcion;
    private int cantidadDisponible;
    private boolean activo;

 // Constructor por defecto.
    public Premio() {}

    // Constructor completo para el alta de nuevos premios en el catálogo de sorteos.
    public Premio(String nombre, String descripcion, int cantidadDisponible, boolean activo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cantidadDisponible = cantidadDisponible; // Setea el stock inicial del sorteo.
        this.activo = activo; // Define si el premio entra directo al circuito o queda en un estado de stand-by.
    }

    // Encapsulamiento.
    // Getters y Setters.
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public int getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(int cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}