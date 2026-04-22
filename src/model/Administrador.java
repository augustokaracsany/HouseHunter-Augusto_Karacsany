package model;
import model.Persona;
import model.Usuarios;
import javax.swing.JOptionPane;

public class Administrador extends Persona {

    public Administrador(String email, String password, String nombre) {
        super(email, password, nombre, "Admin Hotel");
    }

    
    public void mostrarMenu() {
        String[] opciones = {
            "Check-in de invitado",
            "Asignar habitacion",
            "Monitorear actividades",
            "Visualizar cronograma",
            "Entregar premio",
            "Actualizar estado actividad",
            "Generar reporte de evento",
            "Cerrar sesion"
        };
        int opcion;
        do {
            opcion = JOptionPane.showOptionDialog(
                null,
                "Menu Admin Hotel - " + nombre,
                "Sistema HousHunter",
                0,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]
            );

            switch (opcion) {
                case 0: subMenuCheckIn(); break;
                case 1: subMenuHabitacion(); break;
                case 2: mostrarMensaje("Monitorear actividades"); break;
                case 3: mostrarMensaje("Visualizar cronograma"); break;
                case 4: subMenuPremio(); break;
                case 5: mostrarMensaje("Actualizar estado actividad"); break;
                case 6: mostrarMensaje("Generar reporte de evento"); break;
                case 7: JOptionPane.showMessageDialog(null, "Sesion cerrada."); break;
            }
        } while (opcion != 7);
    }

    private void subMenuCheckIn() {
        String[] sub = {"Validar invitado autorizado", "Registrar check-in exitoso ", "Volver"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(null, "Check-in", "Submenu", 0, JOptionPane.INFORMATION_MESSAGE, null, sub, sub[0]);
            switch (op) {
                case 0: mostrarMensaje("Validar invitado autorizado"); break;
                case 1: mostrarMensaje("Check-in exitoso"); break;
                case 2: break;
            }
        } while (op != 2);
    }

    private void subMenuHabitacion() {
        String[] sub = {"Validar disponibilidad de habitacion", "Asignar habitacion)", "Volver"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(null, "Asignar habitacion", "Submenu", 0, JOptionPane.INFORMATION_MESSAGE, null, sub, sub[0]);
            switch (op) {
                case 0: mostrarMensaje("Validar habitacion"); break;
                case 1: mostrarMensaje("Asignar habitacion"); break;
                case 2: break;
            }
        } while (op != 2);
    }

    private void subMenuPremio() {
        String[] sub = {"Obtener un ganador", "Entregar premio", "Volver"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(null, "Entrega de premios", "Submenu", 0, JOptionPane.INFORMATION_MESSAGE, null, sub, sub[0]);
            switch (op) {
                case 0: mostrarMensaje("Obtener un ganador"); break;
                case 1: mostrarMensaje("Entregar premio"); break;
                case 2: break;
            }
        } while (op != 2);
    }

    private void mostrarMensaje(String accion) {
        JOptionPane.showMessageDialog(null, "Funcion: " + accion + "\n(En desarrollo)");
    }
}