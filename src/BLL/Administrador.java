package BLL;

import GUI.AdministradorMenuGrafico;

public class Administrador extends Persona { 
    // ( Atributos Propios. )
    private String dni;            
    private String nombreCompleto; 

    // ( Constructor. )
    public Administrador(String email, String password, String nombreCompleto, String dni, Rol rol) {
        super(email, password, rol); 
        this.dni = dni;
        this.nombreCompleto = nombreCompleto;
    }

    @Override 
    public String getNombre() {
        return nombreCompleto; 
    }

    public String getDni() {
        return dni;
    }

    @Override 
    public void mostrarMenu() {
        // Delegamos la responsabilidad de la vista por completo abriendo la ventana gráfica
        java.awt.EventQueue.invokeLater(() -> {
            try {
                AdministradorMenuGrafico ventana = new AdministradorMenuGrafico(this);
                ventana.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}