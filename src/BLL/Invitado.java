package BLL;

import javax.swing.JOptionPane;

public class Invitado extends Persona {

    public Invitado(String email, String password, String nombre, Rol rol) {
        super(email, password, nombre, rol);
    }

    @Override
    public void mostrarMenu() {
        String[] opciones = {
            "Validar token",
            "Visualizar cronograma",
            "Confirmar asistencia",
            "Consultar actividades",
            "Consultar habitacion",
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
                case 3: mostrarMensaje("Consultar actividades"); break;
                case 4: mostrarMensaje("Consultar datos habitacion"); break;
                case 5: subMenuPremios(); break;
                case 6: JOptionPane.showMessageDialog(null, "Sesion cerrada."); break;
            }
        } while (opcion != 6 && opcion != -1);
    }

    private void subMenuPremios() {
        String[] sub = {"Participar", "Verificar asistencia", "Obtener voucher", "Volver"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(null, "Premios", "Submenu", 0, JOptionPane.INFORMATION_MESSAGE, null, sub, sub[0]);
            switch (op) {
                case 0: mostrarMensaje("Participar en premios"); break;
                case 1: mostrarMensaje("Verificar asistencia mínima"); break;
                case 2: mostrarMensaje("Obtener voucher"); break;
            }
        } while (op != 3 && op != -1);
    }

    private void mostrarMensaje(String accion) {
        JOptionPane.showMessageDialog(null, "Funcion: " + accion + "\n(En desarrollo)");
    }
}