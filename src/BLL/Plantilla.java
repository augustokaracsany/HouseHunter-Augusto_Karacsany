package BLL; // Capa de Lógica de Negocio. < BLL. < Ya no lo vuelvo a poner esto, imagino que quedó claro.


 // Esta clase mapea las plantillas visuales que las empresas eligen para sus eventos.
 // Maneja la identidad gráfica del front-end ( Los vouchers, mails, invitaciones, etc. ) sin
 // acoplar la lógica dura del sistema a un diseño fijo.

public class Plantilla {
    // ( Atributos. )
    private int id;             // Primary Key en la tabla 'plantilla' de househunter.sql en MySQL.
    private String nombre;      // Nombre descriptivo. 
    private String descripcion; // Detalle de qué incluye la hoja de estilos o layout visual. < No lo va a leer nadie esto.
    private String urlImagen;   // Path local o URL del render de previsualización para los JOptionPane.
    private boolean activa;     // Flag de control para el borrado lógico. Evita romper eventos viejos si la plantilla se da de baja.

    // Constructor por defecto. 
     
    public Plantilla() {}

    // Constructor de inicialización para nuevas maquetas.
    // Setea el estado activo por defecto para su disponibilidad inmediata en el sistema.
     
    public Plantilla(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activa = true; // Por defecto nace viva en el catálogo.
    }

    // Los Pilares. > Encapsulamiento. 

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getUrlImagen() { return urlImagen; } // Apunta al asset visual en disco.
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
    
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    
}