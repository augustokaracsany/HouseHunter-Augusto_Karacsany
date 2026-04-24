package model;

import javax.swing.JOptionPane;

public class Empresa extends Persona {

    public Empresa(String email, String password, String nombre) {
        super(email, password, nombre, "Empresa Cliente");
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
                "Menu principal - " + nombre,
                "Sistema HousHunter",
                0,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]
            );

            switch (opcion) {
                case 0:
                    mostrarMensaje("Realizar reserva (incluye Verificar disponibilidad y Visualizar reservas)");
                    break;	
                case 1:
                    mostrarMensaje("Cargar listado de invitados");
                    break;
                case 2:
                    mostrarMensaje("Seleccionar plantilla");
                    break;
                case 3:
                    subMenuCronograma();
                    break;
                case 4:
                    subMenuInvitaciones();
                    break;
                case 5:
                    subMenuReportes();
                    break;
                case 6:
                    JOptionPane.showMessageDialog(null, "Sesion cerrada.");
                    break;
                default:
                    break;
            }
        } while (opcion != 6);
    }

    private void subMenuCronograma() {
        String[] subOpciones = {
            "Crear actividad",
            "Asignar importancia a actividad",
            "Guardar cronograma",
            "Volver"
        };
        int op;
        do {
            op = JOptionPane.showOptionDialog(null, "Definir cronograma", "Submenu",
                    0, JOptionPane.INFORMATION_MESSAGE, null, subOpciones, subOpciones[0]);
            switch (op) {
                case 0: mostrarMensaje("Crear actividad"); break;
                case 1: mostrarMensaje("Asignar importancia"); break;
                case 2: mostrarMensaje("Guardar cronograma"); break;
                case 3: break;
            }
        } while (op != 3);
    }

    private void subMenuInvitaciones() {
        String[] subOpciones = {
            "Seleccionar asistentes",
            "Generar token de acceso",
            "Notificar via mensaje",
            "Volver"
        };
        int op;
        do {
            op = JOptionPane.showOptionDialog(null, "Generar invitaciones masivas", "Submenu",
                    0, JOptionPane.INFORMATION_MESSAGE, null, subOpciones, subOpciones[0]);
            switch (op) {
                case 0: mostrarMensaje("Seleccionar asistentes"); break;
                case 1: mostrarMensaje("Generar token de acceso"); break;
                case 2: mostrarMensaje("Notificar via mensaje"); break;
                case 3: break;
            }
        } while (op != 3);
    }

    private void subMenuReportes() {
        String[] subOpciones = {
            "Seleccionar evento finalizado",
            "Filtrar asistencia/desempeño",
            "Procesar estadisticas",
            "Exportar a PDF/Excel",
            "Volver"
        };
        int op;
        do {
            op = JOptionPane.showOptionDialog(null, "Consultar reportes", "Submenu",
                    0, JOptionPane.INFORMATION_MESSAGE, null, subOpciones, subOpciones[0]);
            switch (op) {
                case 0: mostrarMensaje("Seleccionar evento finalizado"); break;
                case 1: mostrarMensaje("Filtrar asistencia/desempeño"); break;
                case 2: mostrarMensaje("Procesar estadisticas"); break;
                case 3: mostrarMensaje("Exportar PDF/Excel"); break;
                case 4: break;
            }
        } while (op != 4);
    }

    private void mostrarMensaje(String accion) {
        JOptionPane.showMessageDialog(null, "Funcion: " + accion + "\n(En desarrollo)");
    }
}
