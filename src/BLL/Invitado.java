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
        ImageIcon iconoAcceso = new ImageIcon("src/img/HouseHunter_Menu-Invitado_Mi-Acceso.png");
        String[] opciones = {"Validar token", "Confirmar asistencia", "Consultar habitación", "Volver"};
        
        int op = JOptionPane.showOptionDialog(
            null, "<html><body style='width:250px; text-align:center;'><h3>Mi Información</h3>Seleccione una acción:</body></html>", 
            "Acceso", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
            iconoAcceso, opciones, opciones[0]
        );
        
        if(op != 3 && op != -1) mostrarMensaje(opciones[op]);
    }

    private void subMenuConsultaEvento() {
        ImageIcon iconoEvento = new ImageIcon("src/img/HouseHunter_Menu-Invitado_Evento.png");
        String[] opciones = {"Visualizar cronograma", "Consultar actividades", "Volver"};
        
        int op = JOptionPane.showOptionDialog(
            null, "<html><body style='width:250px; text-align:center;'><h3>Información del Evento</h3>Seleccione una opción:</body></html>", 
            "Evento", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
            iconoEvento, opciones, opciones[0]
        );
        
        if(op != 2 && op != -1) mostrarMensaje(opciones[op]);
    }

    private void subMenuPremios() {
        ImageIcon iconoPremios = new ImageIcon("src/img/HouseHunter_Menu-Invitado_Premios.png");
        String[] sub = {"Participar", "Verificar asistencia", "Obtener voucher", "Volver"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(
                null, "<html><body style='width:250px; text-align:center;'><h3>Módulo de Premios</h3>Seleccione una opción:</body></html>", 
                "Submenú Premios", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
                iconoPremios, sub, sub[0]
            );
            if (op != 3 && op != -1) mostrarMensaje(sub[op]);
        } while (op != 3 && op != -1);
    }

    private void mostrarMensaje(String accion) {
        JOptionPane.showMessageDialog(null, "Función: " + accion + "\n(En desarrollo)", "Módulo en Construcción", JOptionPane.INFORMATION_MESSAGE);
    }
}