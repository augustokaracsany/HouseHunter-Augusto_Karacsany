package BLL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Administrador extends Persona {
    private String dni;
    private String nombreCompleto;

    public Administrador(String email, String password, String nombreCompleto, String dni, Rol rol) {
        super(email, password, rol);
        this.dni = dni;
        this.nombreCompleto = nombreCompleto;
    }

    @Override
    public String getNombre() {
        return nombreCompleto;
    }

    @Override
    public void mostrarMenu() {
        ImageIcon iconoMenu = new ImageIcon("src/img/HouseHunter_Menu-Administrador.png");
        
        String tituloHtml = "<html><body style='width: 300px; text-align: center;'>"
                          + "<h2>🔑 Panel de Recepción</h2>"
                          + "<b>Admin:</b> " + getNombre() 
                          + "<hr>Seleccione un área de gestión:</body></html>";

        String[] modulos = {
            "🏨 RECEPCIÓN", 
            "🎮 ACTIVIDADES", 
            "📊 REPORTES", 
            "❌ CERRAR SESIÓN"
        };

        int seleccion;
        do {
            seleccion = JOptionPane.showOptionDialog(
                null, tituloHtml, "HouseHunter v1.0",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                iconoMenu, modulos, modulos[0]
            );

            switch (seleccion) {
                case 0: subMenuRecepcion(); break; 
                case 1: subMenuActividades(); break; 
                case 2: subMenuAdminReportes(); break; 
                case 3: JOptionPane.showMessageDialog(null, "Sesión cerrada de " + getNombre()); break;
            }
        } while (seleccion != 3 && seleccion != -1);
    }

    private void subMenuRecepcion() {
        String[] opciones = {"Check-in de invitado", "Asignar habitación", "Volver"};
        int op = JOptionPane.showOptionDialog(null, "<html><b style='width:250px'>Gestión de Recepción</b></html>", "Recepción", 0, 1, null, opciones, opciones[0]);
        if(op == 0) subMenuCheckIn();
        if(op == 1) subMenuHabitacion();
    }

    private void subMenuActividades() {
        String[] opciones = {"Monitorear actividades", "Visualizar cronograma", "Actualizar estado", "Entregar premio", "Volver"};
        int op = JOptionPane.showOptionDialog(null, "<html><b style='width:250px'>Control de Evento</b></html>", "Actividades", 0, 1, null, opciones, opciones[0]);
        if(op != 4 && op != -1) mostrarMensaje(opciones[op]);
    }

    private void subMenuAdminReportes() {
        mostrarMensaje("Generar reporte de evento");
    }

    private void subMenuCheckIn() {
        String[] sub = {"Validar invitado autorizado", "Registrar check-in exitoso", "Volver"};
        String op;
        do {
            op = (String)JOptionPane.showInputDialog(null, "Check-in", "Submenu", 0, null, sub, sub[0]);
            if(op == null) break;
            if(!op.equals("Volver")) mostrarMensaje(op);
        } while (op != null && !op.equals("Volver"));
    }

    private void subMenuHabitacion() {
        String[] sub = {"Validar disponibilidad", "Asignar habitación", "Volver"};
        int op = JOptionPane.showOptionDialog(null, "Gestión Habitaciones", "Submenu", 0, 1, null, sub, sub[0]);
        if(op != 2 && op != -1) mostrarMensaje(sub[op]);
    }

    private void subMenuPremio() {
        String[] sub = {"Obtener un ganador", "Entregar premio", "Volver"};
        int op = JOptionPane.showOptionDialog(null, "Entrega de premios", "Submenu", 0, 1, null, sub, sub[0]);
        if(op != 2 && op != -1) mostrarMensaje(sub[op]);
    }

    private void mostrarMensaje(String accion) {
        JOptionPane.showMessageDialog(null, "Funcion: " + accion + "\n(En desarrollo)");
    }
}