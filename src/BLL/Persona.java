package BLL; // Capa de Lógica de Negocio. < BLL.

// ( Clase Abstracta. ) < Molde.
// Esta clase es el molde. No se puede instanciar con un 'new Persona()'
// porque no tiene una existencia real en el negocio, solo sirve como base para que 
// Empresa e Invitado hereden su estructura y no dupliquemos código de autenticación.

public abstract class Persona {
    // Atributos protegidos: invisibles para el resto del software, accesibles para las clases hijas.
    protected int id;           // PRIMARY KEY AUTO_INCREMENT espejada de MySQL.
    protected String email;     // Credencial única de login. < Esto debería ser Unique en la BD. ( Supongo, no recuerdo bien. )
    protected String password;  // Credencial de acceso ( Viaja en texto plano, a corregir con hash a futuro. ). < Ya no viaja en texto plano , ya está hecho el Hasheo. - Augusto.
    protected Rol rol;          // Enum para la redirección de paneles en el Login.

    // Constructor Fixeado.
    public Persona(String email, String password, Rol rol) {
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    // Getters y Setters encapsulados. < Los 4 Pilares de la POO.
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public Rol getRol() { return rol; }
    
    // Contratos Abstractos. ( El "Qué" sin el "Cómo" ).

    
     // Fuerza a las clases hijas a resolver la identidad del usuario.
     // Empresa devolverá la Razón Social, Invitado concatenará Nombre y Apellido.
    
    public abstract String getNombre(); 

    // 4 Pilares.
    // Polimorfismo en estado puro. Cada entidad maneja su propia interfaz visual ( JOptionPane. )
    // Así vamos rompiendo la dependencia de un menú centralizado con estructuras 'switch' infinitas.
     
    public abstract void mostrarMenu();
}