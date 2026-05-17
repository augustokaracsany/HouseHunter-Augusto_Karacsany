package BLL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Invitado extends Persona {
    private String nombre;

    public Invitado(String email, String password, String nombre, Rol rol) {
        super(email, password, rol);
        this.nombre = nombre;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public void mostrarMenu() {
        ImageIcon iconoMenu = new ImageIcon("src/img/HouseHunter_Menu-Invitado.png");

        String tituloHtml = "<html><body style='width: 300px; text-align: center;'>"
                          + "<h2>👋 Bienvenido, Invitado</h2>"
                          + "<b>Nombre:</b> " + getNombre() 
                          + "<hr>¿Qué desea consultar hoy?</body></html>";

        String[] modulos = {
            "🎫 MI ACCESO", 
            "📅 EVENTO", 
            "🎁 PREMIOS", 
            "❌ SALIR"
        };

        int seleccion;
        do {
            seleccion = JOptionPane.showOptionDialog(
                null, tituloHtml, "HouseHunter v1.0",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                iconoMenu, modulos, modulos[0]
            );

            switch (seleccion) {
                case 0: subMenuMiAcceso(); break;
                case 1: subMenuConsultaEvento(); break;
                case 2: subMenuPremios(); break;
                case 3: JOptionPane.showMessageDialog(null, "¡Gracias por visitarnos, " + getNombre() + "!"); break;
            }
        } while (seleccion != 3 && seleccion != -1);
    }

    private void subMenuMiAcceso() {
        String[] opciones = {"Validar token", "Confirmar asistencia", "Consultar habitación", "Volver"};
        int op = JOptionPane.showOptionDialog(null, "<html><b style='width:250px'>Mi Información</b></html>", "Acceso", 0, 1, null, opciones, opciones[0]);
        if(op != 3 && op != -1) mostrarMensaje(opciones[op]);
    }

    private void subMenuConsultaEvento() {
        String[] opciones = {"Visualizar cronograma", "Consultar actividades", "Volver"};
        int op = JOptionPane.showOptionDialog(null, "<html><b style='width:250px'>Información del Evento</b></html>", "Evento", 0, 1, null, opciones, opciones[0]);
        if(op != 2 && op != -1) mostrarMensaje(opciones[op]);
    }

    private void subMenuPremios() {
        String[] sub = {"Participar", "Verificar asistencia", "Obtener voucher", "Volver"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(null, "Módulo de Premios", "Submenu", 0, 1, null, sub, sub[0]);
            if (op != 3 && op != -1) mostrarMensaje(sub[op]);
        } while (op != 3 && op != -1);
    }

    private void mostrarMensaje(String accion) {
        JOptionPane.showMessageDialog(null, "Funcion: " + accion + "\n(En desarrollo)");
    }
}