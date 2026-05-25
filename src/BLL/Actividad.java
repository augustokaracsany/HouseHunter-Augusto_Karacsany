package BLL; // Defino que esta clase pertenece a la capa de Lógica de Negocio ( La Business Logic Layer. ) para mantener separada la estructura de datos de la persistencia.

import java.time.LocalDateTime; // API nativa de Java, maneja fechas y horas.

public class Actividad {
    // ( Atributos. ) 
    // 'Private' en todos para aplicar el Encapsulamiento: protejo los datos para que ninguna clase externa los toque directamente sin pasar por validaciones.

    private int id; 
    // Entero para el identificador único de la actividad. Mapea directo con la clave primaria ( PK ) AUTO_INCREMENT de la tabla en MySQL.

    private Reserva reserva; 
    // En lugar de guardar solo un 'id_reserva' como un entero, guardo el objeto completo 'Reserva'. < Reserva.java.

    private String nombre; 
    // Variable para el nombre de la activida dentro del evento.

    private String descripcion; 
    // Atributo de tipo String pensado para detallar de qué se trata la actividad, mapeando con un VARCHAR en la base de datos.

    private LocalDateTime fechaHora; 
    // Uso LocalDateTime para registrar exactamente cuándo inicia la actividad, permitiendo procesar el calendario y las alertas del cronograma eficientemente.

    private int duracionMinutos; 
    // Entero simple acá porque representar el tiempo de la actividad en minutos nos simplifica los cálculos matemáticos cuando tengo que validar solapamientos de horarios.

    private int cupoMaximo; 
    // Otro entero para establecer el límite de asistentes permitidos, clave para validar las inscripciones antes de romper el stock de espacios.

    private Importancia importancia; 
    // Importancia es un Enum para restringir los valores posibles a categorías fijas ( ALTA, MEDIA, BAJA. ), evitando que se carguen textos inválidos de forma libre. < Importancia.java.

    private String categoria; 
    // Variable String para agrupar las actividades según su tipo, facilitando los filtros y las consultas de cronogramas en la interfaz.


    // ( Constructores. )

    public Actividad() {} 
    // Hice este constructor vacío a propósito porque frameworks o ciertas operaciones de inicialización rápida necesitan instanciar el objeto "en blanco" para luego llenarlo con los setters.

    public Actividad(String nombre, LocalDateTime fechaHora, int duracionMinutos, int cupoMaximo, Importancia importancia, String categoria) {
        // Diseñé este constructor completo pero sin ID ni Reserva principalmente para cuando el usuario crea una actividad nueva desde la interfaz gráfica. El ID no se lo pido porque todavía no se generó en la base de datos MySQL, no tendría sentido.
        
    	// ( This. )
        this.nombre = nombre;
        this.fechaHora = fechaHora;
        this.duracionMinutos = duracionMinutos;
        this.cupoMaximo = cupoMaximo;
        this.importancia = importancia;
        this.categoria = categoria;
        // Uso 'this' y le digo a Java que asigne el valor del parámetro recibido al atributo de instancia de la clase que se está creando.
    }
    
    // ( Getters y Setters. )
    // Como mis atributos son privados, implemento estos métodos públicos como los únicos canales autorizados para consultar ( get ) o modificar ( set ) las variables del objeto de forma segura.

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    
    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    
    public int getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(int cupoMaximo) { this.cupoMaximo = cupoMaximo; }
    
    public Importancia getImportancia() { return importancia; }
    public void setImportancia(Importancia importancia) { this.importancia = importancia; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}