package BLL;

import GUI.EmpresaMenu;

public class Empresa extends Persona {
    private String cuit; 
    private String razonSocial; 
    private Reserva reservaActual; // Memoria de la Reserva actual.
    
    public Empresa(String email, String password, String cuit, String razonSocial, Rol rol) {
        super(email, password, rol); 
        this.cuit = cuit;            
        this.razonSocial = razonSocial;
    }

    @Override 
    public String getNombre() {
        return razonSocial; 
    }

    @Override 
    public void mostrarMenu() {
        // Delegamos por completo el control visual a la nueva clase EmpresaMenu.java.
        new EmpresaMenu(this).mostrarMenuPrincipal();
    }

    // Encapsulamiento. 
    // ( Getters y Setters. )
    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public Reserva getReservaActual() { return reservaActual; }
    public void setReservaActual(Reserva reservaActual) { this.reservaActual = reservaActual; }
}