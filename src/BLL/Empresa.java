package BLL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Empresa extends Persona {
    private String cuit;
    private String razonSocial;

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
    	ImageIcon iconoMenu = new ImageIcon("src/img/HouseHunter_Menu-Empresa.png");
        // Texto con ancho fijo para que la ventana no se deforme
        String tituloMenu = "<html><body style='width: 300px; text-align: center;'>"
                          + "<h2>🏢 Panel de Empresa</h2>"
                          + "<b>Entidad:</b> " + getNombre() 
                          + "<hr>Seleccione un módulo de gestión:</body></html>";

        String[] modulos = {
            "📁 GESTIÓN DE EVENTO", 
            "📅 PLANIFICACIÓN", 
            "📧 INVITACIONES", 
            "📊 REPORTES", 
            "❌ CERRAR SESIÓN"
        };

        int seleccion;
        do {
            seleccion = JOptionPane.showOptionDialog(
                null,
                tituloMenu,
                "HouseHunter v1.0",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                iconoMenu, // Aquí podrías poner el logo de HouseHunter si lo cargás como Icon
                modulos,
                modulos[0]
            );

            switch (seleccion) {
                case 0: subMenuGestionEvento(); break;
                case 1: subMenuCronograma(); break; // El que ya tenías
                case 2: subMenuInvitaciones(); break; // El que ya tenías
                case 3: subMenuReportes(); break;
                case 4: JOptionPane.showMessageDialog(null, "Cerrando sesión de " + getNombre()); break;
            }
        } while (seleccion != 4 && seleccion != -1);
    }

    private void subMenuGestionEvento() {
        String[] opciones = {"Realizar Reserva", "Cargar Invitados", "Seleccionar Plantilla", "Volver"};
        JOptionPane.showOptionDialog(null, "<html><b style='width:250px'>Módulo de Eventos</b></html>", "Gestión", 0, 1, null, opciones, opciones[0]);
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