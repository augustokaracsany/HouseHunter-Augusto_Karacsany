package BLL; // Capa de Lógica de Negocio. < BLL.

public enum EstadoHabitacion {
    // ( Enum. ) < Creo que era obligatorio usar los Enums e Interface.  
    // Clavo los tres estados posibles de una habitación en el hotel para que nadie cargue cualquier verdura en la BD.
    
    Libre,    // Vacía. Lista para que entre un cliente.
    Half,     // A media capacidad. 
    // Como todas son dobles pero los invitados se cargan de a uno, tiene que haber un estado intermedio. 
    Completa  // Ocupada al 100%. No entra nadie más.
}