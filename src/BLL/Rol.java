package BLL; 

public enum Rol {
    // ( Enum con Constructor. )
    // Define los roles del sistema asignándoles una etiqueta fija para evitar strings sueltos.
    ADMINISTRADOR("Admin Hotel"),
    EMPRESA("Empresa Cliente"),
    INVITADO("Invitado");

    private final String descripcion; // Variable inmutable que guarda el texto legible de cada rol.
    // Que sea private final significa que el valor no se puede cambiar después de haber sido establecido.
    // Los 'final' son una constante.

    // Constructor interno del Enum. Se ejecuta automáticamente para asociar cada constante con su string.
    Rol(String descripcion) {
        this.descripcion = descripcion;
    }

    // Getter para recuperar la etiqueta limpia al pintar los paneles o validar permisos.
    public String getDescripcion() {
        return descripcion;
    }
}