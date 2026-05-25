package BLL; 

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reserva {
    // ( Atributos Reales según SQL )
    private int id;
    private Empresa empresa;          
    private LocalDateTime createdAt;   // Cambiado para matchear con 'created_at' TIMESTAMP.
    private LocalDate fechaInicio;    // Cambiado: Ahora mapea con 'fecha_inicio'.
    private LocalDate fechaFin;       // Cambiado: Ahora mapea con 'fecha_fin'.
    private int cantidadEstimadaAsistentes; // Cambiado: Mapea con 'cantidad_estimada_asistentes'.
    private Plantilla plantilla; // Relación con la plantilla de la base de datos
    
    // ( Relaciones 1:N )
    private List<Actividad> actividades;

    public Reserva() {
        this.createdAt = LocalDateTime.now(); 
        this.actividades = new ArrayList<>(); 
    }
    
    // Constructor adaptado para el alta real desde el panel.
    public Reserva(Empresa empresa, LocalDate fechaInicio, LocalDate fechaFin, int cantidadEstimadaAsistentes) {
        this(); 
        this.empresa = empresa;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cantidadEstimadaAsistentes = cantidadEstimadaAsistentes;
    }
    
    // ( Getters y Setters actualizados )
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    
    public int getCantidadEstimadaAsistentes() { return cantidadEstimadaAsistentes; }
    public void setCantidadEstimadaAsistentes(int cantidadEstimadaAsistentes) { this.cantidadEstimadaAsistentes = cantidadEstimadaAsistentes; }
    
    public List<Actividad> getActividades() { return actividades; }
    public void setActividades(List<Actividad> actividades) { this.actividades = actividades; }
    
    public Plantilla getPlantilla() { return plantilla; }
    public void setPlantilla(Plantilla plantilla) { this.plantilla = plantilla; }
}