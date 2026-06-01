package BLL; // Defino que esta clase pertenece a la capa de Lógica de Negocio ( Business Logic Layer. ) < BLL.

// ( Imports. )
// Traigo los sub-controladores específicos de la capa DLL para interactuar con la base de datos de forma limpia.
import DLL.ActividadController; // Controlador para meter asistencias y cronogramas.
import DLL.EventoController;    // Controlador para reportes y listas previas.
import DLL.HabitacionController;// Controlador para check-ins y estados de habitaciones.
import DLL.PremioController;    // Controlador para ejecutar los sorteos de vouchers. < Hay que trabajar más en esa clase.
import javax.swing.ImageIcon;   // Clase de Java para levantar las imágenes estéticas de los menús desde el disco.
import javax.swing.JOptionPane; // Clase nativa de Java para pintar las ventanas emergentes ( diálogos, inputs, opciones. ).

public class Administrador extends Persona { 
    // 'extends Persona' > HERENCIA. Administrador hereda el mail, password y rol de Persona.java, evitando duplicar código.

    // ( Atributos Propios. )
    private String dni;            // DNI exclusivo del administrador.
    private String nombreCompleto; // Nombre y apellido del admin logueado para mostrar en los menús.

    // ( Constructor. )
    public Administrador(String email, String password, String nombreCompleto, String dni, Rol rol) {
        super(email, password, rol); 
        // 'super' para pasarle el mail, password y rol directamente al constructor de la clase padre ( Persona.java ). Esto inicializa la base heredada.
        
        this.dni = dni;
        this.nombreCompleto = nombreCompleto;
        // 'this' para guardar el DNI y nombre que entran en las variables privadas de arriba.
    }

    @Override // Le aviso a Java que estoy sobreescribiendo el método getNombre() que viene de Persona.
    public String getNombre() {
        return nombreCompleto; // Devuelvo el atributo propio de esta clase en lugar del genérico.
    }

    @Override // Sobreescribo el método abstracto mostrarMenu() de Persona. Acá arranca la visual del admin. 
    public void mostrarMenu() {
        // Levanto el banner principal del admin desde la carpeta de recursos del proyecto.
        ImageIcon iconoMenu = new ImageIcon("src/img/HouseHunter_Menu-Administrador.png");
        
        // String con etiquetas HTML para meterle estilo, negritas y centrado al texto del panel de recepción.
        String tituloHtml = "<html><body style='width: 300px; text-align: center;'>"
                          + "<h2>Panel de Recepción</h2>"
                          + "<b>Admin:</b> " + getNombre() 
                          + "<hr>Seleccione un área de gestión:</body></html>";

        // Vector de Strings ( Array. ) con los botones físicos que se van a renderizar en el JOptionPane.
        String[] modulos = {
            "RECEPCIÓN", 
            "ACTIVIDADES", 
            "REPORTES", 
            "CERRAR SESIÓN"
        };

        int seleccion; // Entero para guardar el botón que el usuario clickeé (0, 1, 2, 3).
        
        // ( Bucle do-while. )
        // Uso do-while para garantizar que el menú se pinte al menos UNA VEZ, y se repita cíclicamente hasta que se elija salir.
        do {
            // Pinto la ventana de opciones personalizada con los botones del vector 'modulos'.
            seleccion = JOptionPane.showOptionDialog(
                null, tituloHtml, "HouseHunter v1.0",
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.PLAIN_MESSAGE,
                iconoMenu, modulos, modulos[0]
            );

            // ( Switch. )
            // Evalúo 'seleccion' para mandar al submenú que corresponda según el botón clickeado.
            switch (seleccion) {
                case 0: subMenuRecepcion(); break;     // Presionó Recepción ( Es decir, Posición 0. ). Va a su submenú.
                case 1: subMenuActividades(); break;   // Presionó Actividades ( Posición 1. ). 
                case 2: subMenuAdminReportes(); break; // Presionó Reportes ( Posición 2. ). 
                case 3: JOptionPane.showMessageDialog(null, "Sesión cerrada de " + getNombre()); break; // Cierra sesión ( Posición 3. ).
            }
        } while (seleccion != 3 && seleccion != -1); 
        // El bucle sigue girando MIENTRAS no se elija la opción 3 ( Cerrar Sesión. ) y tampoco toque la "X" roja de la ventana ( Que devuelve el -1. ).
    }

    // ( Submenú Recepción. )
    private void subMenuRecepcion() {
    	// Imagen de Administrador > Menú de Recepción ( No el menú principal, eso sería un 'Home'. )
        ImageIcon iconoRecepcion = new ImageIcon("src/img/HouseHunter_Menu-Administrador_Recepcion.png"); 
        // No voy a explicar lo mismo de recién dos veces.
        String[] opciones = {"Registrar Check-In", "Monitorear Habitaciones", "Volver"};
        
        int op = JOptionPane.showOptionDialog(
            null, 
            "<html><body style='width:250px; "
            + "text-align:center;'><h3>Gestión de Recepción</h3>Seleccione una operación:</body></html>", 
            "Módulo de Recepción", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.PLAIN_MESSAGE, 
            iconoRecepcion, 
            opciones, 
            opciones[0]
        );
        
        // ( Condicionales. )
        if (op == 0) { // Si se eligió la posición 0 > Registrar Check-In.
            ejecutarFlujoCheckInCompleto(); // Salta al método que pide los datos en cascada.
            
        } else if (op == 1) { // Eligió la posición 1 > Monitorear Habitaciones.
            // Uso el Singleton de HabitacionController para traerme la tabla estructurada en HTML directo desde la Base de Datos.
            String estadoHabitacionesHtml = HabitacionController.getInstance().obtenerEstadoHabitacionesHtml(); 
            
            // Le muestro al Administrador el estado real de ocupación de las habitaciones del hotel.
            JOptionPane.showMessageDialog(
                null, 
                estadoHabitacionesHtml, 
                "Control de Ocupación Real", 
                JOptionPane.PLAIN_MESSAGE, 
                iconoRecepcion
            );
        }
    }

    // ( Submenú de Actividades. )
    private void subMenuActividades() {
    	// Más imagenes.
        ImageIcon iconoActividades = new ImageIcon("src/img/HouseHunter_Menu-Administrador_Actividades.png");
        // Más arrays de opciones.
        String[] opciones = {"Monitorear actividades", "Visualizar cronograma", "Actualizar estado", "Entregar premio", "Volver"};
        
        int op = JOptionPane.showOptionDialog(
            null, "<html><body style='width:250px; text-align:center;'><h3>Control de Eventos</h3>Seleccione una opción:</body></html>", 
            "Actividades", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.PLAIN_MESSAGE, 
            iconoActividades, opciones, opciones[0]
        );
        
        // ( Inputs. )
        // Agrupo el caso 0 y 2 usando el operador OR (||) porque ambos caminos requieren identificar el evento y tomar asistencia.
        if (op == 0 || op == 2) { 
            // Pido el código del evento en input. ( Ej: 'EVENTO-1234' ).
            String codEvento = JOptionPane.showInputDialog(null, "Ingrese el Código Único del Evento:", "Control de Asistencias", JOptionPane.QUESTION_MESSAGE);
            if (codEvento == null || codEvento.trim().isEmpty()) return; // Corto el flujo con un 'return' si cancela o deja vacío, para evitar NullPointerException. < Esto pasó varias veces ya.
            
            // Casteo al Singleton de ActividadController para traerme un array de Strings con los nombres de las actividades de ese dichoso evento.
            String[] actividades = ActividadController.getInstance().obtenerNombresActividades(codEvento.trim());
            
            if (actividades.length == 0) { // Si el array vuelve vacío, es porque el código de evento no existe en MySQL o no tiene actividades asignadas o algo explotó por el camino.
                JOptionPane.showMessageDialog(null, "❌ No hay actividades cargadas o el evento no existe.", "Aviso.", JOptionPane.WARNING_MESSAGE);
                return; // Corto los inputs de forma segura.
            }
            
            // Menú desplegable para que el admin elija una actividad válida sin chance de errarle al tipeo.
            String actividadSeleccionada = (String) JOptionPane.showInputDialog(
                null, "Seleccione la actividad a gestionar:", "Monitoreo de Bloques.",
                JOptionPane.PLAIN_MESSAGE, iconoActividades, actividades, actividades[0]
            );
            
            if (actividadSeleccionada != null) { // Si seleccionó una actividad y no canceló...
                // Pido el DNI del invitado.
                String dniInvitado = JOptionPane.showInputDialog(null, "Ingrese el DNI del Invitado que asistió:", "Tomar Asistencia.", JOptionPane.QUESTION_MESSAGE);
                if (dniInvitado != null && !dniInvitado.trim().isEmpty()) {
                    // Impacto directo en las tablas relacionales mediante el Singleton de Actividades, registrando el presentismo.
                    ActividadController.getInstance().registrarAsistenciaActividad(codEvento.trim(), actividadSeleccionada, dniInvitado.trim());
                }
            }
            
        } else if (op == 1) { // Eligió la posición 1 > Visualizar cronograma.
            String codEvento = JOptionPane.showInputDialog(null, "Ingrese el Código Único del Evento:", "Consultar Cronograma.", JOptionPane.QUESTION_MESSAGE);
            if (codEvento != null && !codEvento.trim().isEmpty()) {
                // LLamo al Singleton de Actividades para traerme todo el itinerario formateado en HTML para la interfaz.
                String agendaHtml = ActividadController.getInstance().obtenerCronogramaEventos(codEvento.trim());
                JOptionPane.showMessageDialog(null, agendaHtml, "Agenda - Evento: " + codEvento.trim(), JOptionPane.PLAIN_MESSAGE, iconoActividades);
            }
        } else if (op == 3) { // Eligió la posición 3: Entregar premio.
            subMenuPremio(); // Derivo al submenú específico de sorteos.
        }
    }

    // ( Submenú de Reportes. )
    private void subMenuAdminReportes() {
        ImageIcon iconoReportes = new ImageIcon("src/img/HouseHunter_Menu-Administrador_Reportes.png");
        String[] opciones = {"Generar reporte de evento.", "Volver."};
        
        int op = JOptionPane.showOptionDialog(
            null, "<html><body style='width:250px; text-align:center;'><h3>Módulo de Reportes</h3>Seleccione una acción:</body></html>", 
            "Reportes", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
            iconoReportes, opciones, opciones[0]
        );
        
        if (op == 0) { // Generar reporte estadístico. < Esto estaba en el SRS chicos.
            String codEvento = JOptionPane.showInputDialog(null, "Ingrese el Código Único del Evento para consolidar:", "Generar Reporte.", JOptionPane.QUESTION_MESSAGE);
            if (codEvento != null && !codEvento.trim().isEmpty()) {
                // Llamo al EventoController mediante su Singleton para hacer un JOIN.
                String reporteHtml = EventoController.getInstance().obtenerReporteConsolidadoEvento(codEvento.trim());
                JOptionPane.showMessageDialog(null, reporteHtml, "Métricas del Evento: " + codEvento.trim(), JOptionPane.PLAIN_MESSAGE, iconoReportes);
            }
        }
    }

    // ( Subemnú de Check-In. )
    private void subMenuCheckIn() {
        ImageIcon iconoRecepcion = new ImageIcon("src/img/HouseHunter_Menu-Administrador_Recepcion.png");
        String[] sub = {"Validar invitado autorizado.", "Registrar check-in exitoso.", "Volver."};
        String op;
        
        // Otro bucle do-while para mantener al admin dentro de las operaciones de Check-In hasta que decida presionar "Volver".
        do {
            op = (String)JOptionPane.showInputDialog(
                null, "Seleccione la operación de check-in:", "Submenú Check-in.", 
                JOptionPane.PLAIN_MESSAGE, iconoRecepcion, sub, sub[0]
            );
            
            if(op == null || op.equals("Volver.")) break; // Si cierra la ventana o elije Volver, rompo el bucle con un 'break'.
            
            if(op.equals("Validar invitado autorizado")) {
                String codEvento = JOptionPane.showInputDialog(null, "Ingrese el Código Único del Evento:", "Validación Previa", JOptionPane.QUESTION_MESSAGE);
                if (codEvento != null && !codEvento.trim().isEmpty()) {
                    String dniInvitado = JOptionPane.showInputDialog(null, "Ingrese el DNI del Invitado:", "Validación Previa", JOptionPane.QUESTION_MESSAGE);
                    if (dniInvitado != null && !dniInvitado.trim().isEmpty()) {
                        
                        // Uso EventoController para comprobar si la persona está pre-cargada en la lista del evento.
                        boolean autorizado = EventoController.getInstance().validarInvitadoPrevia(codEvento.trim(), dniInvitado.trim());
                        if (autorizado) {
                            JOptionPane.showMessageDialog(null, "✅ El invitado con DNI " + dniInvitado + " se encuentra AUTORIZADO en la lista previa.", "Verificación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "❌ El invitado NO figura en la lista previa de este evento o el código es incorrecto.", "Verificación Fallida", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else if(op.equals("Registrar check-in exitoso")) {
                ejecutarFlujoCheckInCompleto(); // Si está autorizado, procedo a asignarle la habitación en el sistema.
            }
            
        } while (!op.equals("Volver")); // Se repite mientras la opción seleccionada no sea el texto "Volver".
    }

    // ( Submenú Obsoleto de Habitación. ) < Borrar esto cuanto antes. TNT
    private void subMenuHabitacion() {
        ImageIcon iconoRecepcion = new ImageIcon("src/img/HouseHunter_Menu-Administrador_Recepcion.png");
        String[] sub = {"Validar disponibilidad (Ver Estado)", "Asignar habitación directa", "Volver"};
        
        int op = JOptionPane.showOptionDialog(
            null, "<html><body style='width:250px; text-align:center;'><h3>Gestión Habitaciones</h3></body></html>", 
            "Submenú Habitaciones", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
            iconoRecepcion, sub, sub[0]
        );
        
        if (op == 0) {
            JOptionPane.showMessageDialog(null, "Para validar disponibilidad en tiempo real o modificar estados,\nutilice la opción 'Registrar check-in exitoso' del menú anterior.", "Control de Habitaciones", JOptionPane.INFORMATION_MESSAGE);
        } else if (op == 1) {
            ejecutarFlujoCheckInCompleto(); // Redirecciono directo a los Inputs.
        }
    }

    // ( Inputs Encadenados. )
    // Método privado auxiliar que centraliza la recolección de los tres datos requeridos para meter un Check-In en la BD.
    private void ejecutarFlujoCheckInCompleto() {
        String codEvento = JOptionPane.showInputDialog(null, "Ingrese el Código Único del Evento:", "Procesar Asignación", JOptionPane.QUESTION_MESSAGE);
        if (codEvento == null || codEvento.trim().isEmpty()) return; // Validación de escape rápido.

        String dniInvitado = JOptionPane.showInputDialog(null, "Ingrese el DNI del Huésped:", "Procesar Asignación", JOptionPane.QUESTION_MESSAGE);
        if (dniInvitado == null || dniInvitado.trim().isEmpty()) return; // Validación.

        String numHabitacion = JOptionPane.showInputDialog(null, "Ingrese el Número de Habitación (Ej: 101, 102, 201):", "Procesar Asignación", JOptionPane.QUESTION_MESSAGE);
        if (numHabitacion == null || numHabitacion.trim().isEmpty()) return; // Validación.

        // Cuando tengo los tres datos limpios, llamo al Singleton de Habitaciones para ejecutar el procedimiento en la Base de Datos MySQL.
        HabitacionController.getInstance().procesarCheckInHabitacion(codEvento.trim(), dniInvitado.trim(), numHabitacion.trim());
    }

    // ( Submenú de Premios & Sorteos. )
    private void subMenuPremio() {
        ImageIcon iconoActividades = new ImageIcon("src/img/HouseHunter_Menu-Administrador_Actividades.png");
        String[] sub = {"Lanzar Sorteo de Evento", "Volver"}; 
        
        int op = JOptionPane.showOptionDialog( 
            null, "<html><body style='width:250px; text-align:center;'><h3>Entrega de Premios</h3>Seleccione una operación:</body></html>", 
            "Submenú Premios", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
            iconoActividades, sub, sub[0]
        );
        
        if (op == 0) {  // Lanzar Sorteo de Evento.
            String codEvento = JOptionPane.showInputDialog(null, "Ingrese el Código Único del Evento:", "Configurar Sorteo", JOptionPane.QUESTION_MESSAGE);
            if (codEvento == null || codEvento.trim().isEmpty()) return;
            
            String descripcionPremio = JOptionPane.showInputDialog(null, "Ingrese la descripción del Premio (Ej: Voucher Estadía 5 Estrellas):", "Detalle del Premio", JOptionPane.QUESTION_MESSAGE);
            if (descripcionPremio == null || descripcionPremio.trim().isEmpty()) return;
            
            // Casteo al PremioController mediante su Singleton para correr la consulta SQL aleatoria ( ORDER BY RAND() ) que elige al ganador.
            PremioController.getInstance().ejecutarSorteoPremio(codEvento.trim(), descripcionPremio.trim());
        }
    }

    // ( Método de Alerta Genérica. )
    private void mostrarMensaje(String accion) {
        // Método genérico por si quedó alguna opción vieja cableada o en desarrollo. Muestra un aviso informativo en pantalla. Se eliminará cuando corresponda, por las dudas. - Augusto.
        JOptionPane.showMessageDialog(null, "Función: " + accion + "\n(En desarrollo)", "Módulo en Construcción", JOptionPane.INFORMATION_MESSAGE);
    }
}