package view;
import model.Persona;
import model.Usuarios;

import javax.swing.JOptionPane;

public class Main {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		        JOptionPane.showMessageDialog(null, "*Bienvenido al sistema HouseHunter*");

		        boolean salir = false;
		        while (!salir) {
		            // solicitar credenciales
		            String email = JOptionPane.showInputDialog("Ingrese su email:");
		            if (email == null) break; // cancelar
		            String password = JOptionPane.showInputDialog("Ingrese su contraseña:");

		            Persona usuario = Usuarios.login(email, password);
		            if (usuario != null) {
		                JOptionPane.showMessageDialog(null, "Login exitoso. Bienvenido " + usuario.getNombre());
		                usuario.mostrarMenu();  // cada subclase muestra su propio menu
		            } else {
		                JOptionPane.showMessageDialog(null, "Credenciales invalidas. Intente nuevamente.");
		                int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea intentar de nuevo?", "Error", JOptionPane.YES_NO_OPTION);
		                if (respuesta != JOptionPane.YES_OPTION) {
		                    salir = true;
		                }
		            }
		        }
		        JOptionPane.showMessageDialog(null, "Sistema cerrado. \n¡Gracias por elegirnos!");
		    }
		
}
