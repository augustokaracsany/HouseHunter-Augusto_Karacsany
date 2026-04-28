package BLL;

import javax.swing.JOptionPane;

public class Empresa extends Persona {

    public Empresa(String email, String password, String nombre, Rol rol) {
        super(email, password, nombre, rol);
    }

    @Override
    public void mostrarMenu() {
        String[] opciones = {
            "Realizar reserva",
            "Cargar listado de invitados",
            "Seleccionar plantilla",
            "Definir cronograma",
            "Generar invitaciones masivas",
            "Consultar reportes",
            "Cerrar sesión"
        };
        int opcion;
        do {
            opcion = JOptionPane.showOptionDialog(
                null,
                "Menu Empresa - " + nombre,
                "Sistema HousHunter",
                0,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]
            );

            switch (opcion) {
                case 0: mostrarMensaje("Realizar reserva"); break;
                case 1: mostrarMensaje("Cargar listado de invitados"); break;
                case 2: mostrarMensaje("Seleccionar plantilla"); break;
                case 3: subMenuCronograma(); break;
                case 4: subMenuInvitaciones(); break;
                case 5: subMenuReportes(); break;
                case 6: JOptionPane.showMessageDialog(null, "Sesion cerrada."); break;
            }
        } while (opcion != 6 && opcion != -1);
    }

    private void subMenuCronograma() {
        String[] sub = {"Crear actividad", "Asignar importancia", "Guardar cronograma", "Volver"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(null, "Cronograma", "Submenu", 0, JOptionPane.INFORMATION_MESSAGE, null, sub, sub[0]);
            switch (op) {
                case 0: mostrarMensaje("Crear actividad"); break;
                case 1: mostrarMensaje("Asignar importancia"); break;
                case 2: mostrarMensaje("Guardar cronograma"); break;
            }
        } while (op != 3 && op != -1);
    }

    private void subMenuInvitaciones() {
        String[] sub = {"Seleccionar asistentes", "Generar token", "Notificar", "Volver"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(null, "Invitaciones", "Submenu", 0, JOptionPane.INFORMATION_MESSAGE, null, sub, sub[0]);
            switch (op) {
                case 0: mostrarMensaje("Seleccionar asistentes"); break;
                case 1: mostrarMensaje("Generar token"); break;
                case 2: mostrarMensaje("Notificar"); break;
            }
        } while (op != 3 && op != -1);
    }

    private void subMenuReportes() {
        String[] sub = {"Seleccionar evento", "Filtrar desempeño", "Estadisticas", "Exportar", "Volver"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(null, "Reportes", "Submenu", 0, JOptionPane.INFORMATION_MESSAGE, null, sub, sub[0]);
            switch (op) {
                case 0: mostrarMensaje("Seleccionar evento"); break;
                case 1: mostrarMensaje("Filtrar asistencia"); break;
                case 2: mostrarMensaje("Procesar estadísticas"); break;
                case 3: mostrarMensaje("Exportar a PDF/Excel"); break;
            }
        } while (op != 4 && op != -1);
    }

    private void mostrarMensaje(String accion) {
        JOptionPane.showMessageDialog(null, "Funcion: " + accion + "\n(En desarrollo)");
    }
}