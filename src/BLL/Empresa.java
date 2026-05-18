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
                null, tituloMenu, "HouseHunter v1.0",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                iconoMenu, modulos, modulos[0]
            );

            switch (seleccion) {
                case 0: subMenuGestionEvento(); break;
                case 1: subMenuCronograma(); break; 
                case 2: subMenuInvitaciones(); break; 
                case 3: subMenuReportes(); break;
                case 4: JOptionPane.showMessageDialog(null, "Cerrando sesión de " + getNombre()); break;
            }
        } while (seleccion != 4 && seleccion != -1);
    }

    private void subMenuGestionEvento() {
        ImageIcon iconoGestion = new ImageIcon("src/img/HouseHunter_Menu-Empresa_Gestion.png");
        String[] opciones = {"Realizar Reserva", "Cargar Invitados", "Seleccionar Plantilla", "Volver"};
        
        int op = JOptionPane.showOptionDialog(
            null, "<html><body style='width:250px; text-align:center;'><h3>Módulo de Eventos</h3>Seleccione una acción:</body></html>", 
            "Gestión", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
            iconoGestion, opciones, opciones[0]
        );
        
        if(op != 3 && op != -1) mostrarMensaje(opciones[op]);
    }

    private void subMenuCronograma() {
        ImageIcon iconoPlanif = new ImageIcon("src/img/HouseHunter_Menu-Empresa_Planificacion.png");
        String[] sub = {"Crear actividad", "Asignar importancia", "Guardar cronograma", "Volver"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(
                null, "<html><body style='width:250px; text-align:center;'><h3>Cronograma de Actividades</h3></body></html>", 
                "Submenú Planificación", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
                iconoPlanif, sub, sub[0]
            );
            switch (op) {
                case 0: mostrarMensaje("Crear actividad"); break;
                case 1: mostrarMensaje("Asignar importancia"); break;
                case 2: mostrarMensaje("Guardar cronograma"); break;
            }
        } while (op != 3 && op != -1);
    }

    private void subMenuInvitaciones() {
        ImageIcon iconoInvit = new ImageIcon("src/img/HouseHunter_Menu-Empresa_Invitaciones.png");
        String[] sub = {"Seleccionar asistentes", "Generar token", "Notificar", "Volver"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(
                null, "<html><body style='width:250px; text-align:center;'><h3>Envío de Invitaciones</h3></body></html>", 
                "Submenú Invitaciones", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
                iconoInvit, sub, sub[0]
            );
            switch (op) {
                case 0: mostrarMensaje("Seleccionar asistentes"); break;
                case 1: mostrarMensaje("Generar token"); break;
                case 2: mostrarMensaje("Notificar"); break;
            }
        } while (op != 3 && op != -1);
    }

    private void subMenuReportes() {
        ImageIcon iconoReportes = new ImageIcon("src/img/HouseHunter_Menu-Empresa_Reportes.png");
        String[] sub = {"Seleccionar evento", "Filtrar desempeño", "Estadísticas", "Exportar", "Volver"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(
                null, "<html><body style='width:250px; text-align:center;'><h3>Reportes Corporativos</h3></body></html>", 
                "Submenú Reportes", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
                iconoReportes, sub, sub[0]
            );
            switch (op) {
                case 0: mostrarMensaje("Seleccionar evento"); break;
                case 1: mostrarMensaje("Filtrar asistencia"); break;
                case 2: mostrarMensaje("Procesar estadísticas"); break;
                case 3: mostrarMensaje("Exportar a PDF/Excel"); break;
            }
        } while (op != 4 && op != -1);
    }

    private void mostrarMensaje(String accion) {
        JOptionPane.showMessageDialog(null, "Función: " + accion + "\n(En desarrollo)", "Módulo en Construcción", JOptionPane.INFORMATION_MESSAGE);
    }
}