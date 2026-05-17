package GUI;

import javax.swing.JOptionPane;
import BLL.Persona;
import Repository.Hashing;
import Repository.UsuariosController;
import Repository.UsuariosRepository;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        // Usamos la interfaz ( Ojo el Polimorfismo. )
        UsuariosRepository repo = new UsuariosController();

        JOptionPane.showMessageDialog(null, "*Bienvenido al sistema HouseHunter*");

        boolean salir = false;
        while (!salir) {
            String email = JOptionPane.showInputDialog("Ingrese su email:");
            if (email == null) break; // Si presiona cancelar.
            System.out.println("Tu nuevo hash compatible: " + Hashing.hash("1234"));
            String password = JOptionPane.showInputDialog("Ingrese su contraseña:");
            
            Persona usuario = repo.login(email, password);

            if (usuario != null) {
                JOptionPane.showMessageDialog(null, "Login exitoso. Bienvenido " + usuario.getNombre());
                usuario.mostrarMenu();
                // --- Verificación de conexión y datos en Consola ---
                System.out.println("--- LISTA DE USUARIOS EN BASE DE DATOS ---");
                LinkedList<Persona> todos = repo.listarTodos();
                
                for (Persona p : todos) {
                    System.out.println("ID: " + p.getId() + " | Nombre: " + p.getNombre() + 
                                       " | Email: " + p.getEmail() + " | Rol: " + p.getRol());
                }
                System.out.println("------------------------------------------");
                
               
            } else {
                JOptionPane.showMessageDialog(null, "Credenciales inválidas. Intente nuevamente.");
            }
        }
    }
}