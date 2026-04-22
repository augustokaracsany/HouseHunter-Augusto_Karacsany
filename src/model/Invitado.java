package model;
import javax.swing.JOptionPane;

public class Invitado extends Persona {

    public Invitado(String email, String password, String nombre) {
        super(email, password, nombre, "Invitado");
    }

    @Override
    public void mostrarMenu() {
        String[] opciones = {
            "Validar token",
            "Visualizar cronograma",
            "Confirmar asistencia ",
            "Consultar actividades",
            "Consultar datos de habitacion",
            "Participar en premios",
            "Cerrar sesion"
        };
        int opcion;
        do {
            opcion = JOptionPane.showOptionDialog(
                null,
                "Menu Invitado - " + nombre,
                "Sistema HousHunter",
                0,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]
            );

            switch (opcion) {
                case 0: mostrarMensaje("Validar token"); break;
                case 1: mostrarMensaje("Visualizar cronograma"); break;
                case 2: mostrarMensaje("Confirmar asistencia"); break;
                case 3: mostrarMensaje("Consultar actividades ( Incluye Ver detalles de Actividad )"); break;
                case 4: mostrarMensaje("Consultar datos de habitacion"); break;
                case 5:
                    subMenuPremios();
                    break;
                case 6:
                    JOptionPane.showMessageDialog(null, "Sesion cerrada.");
                    break;
            }
        } while (opcion != 6);
    }

    private void subMenuPremios() {
        String[] subOpciones = {
            "Participar en premios",
            "Verificar asistencia minima",
            "Obtener voucher",
            "Volver"
        };
        int op;
        do {
            op = JOptionPane.showOptionDialog(null, "Premios", "Submenu",
                    0, JOptionPane.INFORMATION_MESSAGE, null, subOpciones, subOpciones[0]);
            switch (op) {
                case 0: mostrarMensaje("Participar en premios"); break;
                case 1: mostrarMensaje("Verificar asistencia minima"); break;
                case 2: mostrarMensaje("Obtener voucher"); break;
                case 3: break;
            }
        } while (op != 3);
    }

    private void mostrarMensaje(String accion) {
        JOptionPane.showMessageDialog(null, "Funcion: " + accion + "\n(En desarrollo)");
    }
}