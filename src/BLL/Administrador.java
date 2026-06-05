package BLL; // Defino que esta clase pertenece a la capa de Lógica de Negocio ( Business Logic Layer. ) < BLL.

// ( Imports. )
// Traigo los sub-controladores específicos de la capa DLL para interactuar con la base de datos de forma limpia.
import DLL.ActividadController; // Controlador para meter asistencias y cronogramas.
import DLL.EventoController;    // Controlador para reportes y listas previas.
import DLL.HabitacionController;// Controlador para check-ins y estados de habitaciones.
import DLL.PremioController;    // Controlador para ejecutar los sorteos de vouchers. < Hay que trabajar más en esa clase.

import java.util.List;

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

    // ( Submenú de Actividades. ).
 // ( Submenú de Actividades - ACTUALIZADO ).
    private void subMenuActividades() {
        ImageIcon iconoActividades = new ImageIcon("src/img/HouseHunter_Menu-Administrador_Actividades.png");
        
        // BORRADO: Saqué "Entregar premio" de acá adentro.
        String[] opciones = {"Monitorear actividades", "Visualizar cronograma", "Actualizar estado", "Volver"};
        
        int op = JOptionPane.showOptionDialog(
            null, "<html><body style='width:250px; text-align:center;'><h3>Control de Eventos</h3>Seleccione una opción:</body></html>", 
            "Actividades", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.PLAIN_MESSAGE, 
            iconoActividades, opciones, opciones[0]
        );
        
        // El caso 0 y 2 se mantienen igual para tomar asistencia.
        if (op == 0 || op == 2) { 
            String codEvento = JOptionPane.showInputDialog(null, "Ingrese el Código Único del Evento:", "Control de Asistencias", JOptionPane.QUESTION_MESSAGE);
            if (codEvento == null || codEvento.trim().isEmpty()) return; 
            
            String[] actividades = ActividadController.getInstance().obtenerNombresActividades(codEvento.trim());
            
            if (actividades.length == 0) { 
                JOptionPane.showMessageDialog(null, "❌ No hay actividades cargadas o el evento no existe.", "Aviso.", JOptionPane.WARNING_MESSAGE);
                return; 
            }
            
            String actividadSeleccionada = (String) JOptionPane.showInputDialog(
                null, "Seleccione la actividad a gestionar:", "Monitoreo de Bloques.",
                JOptionPane.PLAIN_MESSAGE, iconoActividades, actividades, actividades[0]
            );
            
            if (actividadSeleccionada != null) { 
                String dniInvitado = JOptionPane.showInputDialog(null, "Ingrese el DNI del Invitado que asistió:", "Tomar Asistencia.", JOptionPane.QUESTION_MESSAGE);
                if (dniInvitado != null && !dniInvitado.trim().isEmpty()) {
                    ActividadController.getInstance().registrarAsistenciaActividad(codEvento.trim(), actividadSeleccionada, dniInvitado.trim());
                }
            }
            
        } else if (op == 1) { // Visualizar cronograma ( Bien hecho. )
            String codEvento = JOptionPane.showInputDialog(null, "Ingrese el Código Único del Evento:", "Consultar Cronograma.", JOptionPane.QUESTION_MESSAGE);
            if (codEvento != null && !codEvento.trim().isEmpty()) {
                String agendaHtml = ActividadController.getInstance().obtenerCronogramaEventos(codEvento.trim());
                JOptionPane.showMessageDialog(null, agendaHtml, "Agenda - Evento: " + codEvento.trim(), JOptionPane.PLAIN_MESSAGE, iconoActividades);
            }
        }
        // BORRADO: El 'else if (op == 3)' pasó a mejor vida.
    }


    // ( Submenú de Reportes. )
 // ( Submenú de Reportes - MODIFICADO PARA NO PEDIR CÓDIGO POR TECLADO )
    private void subMenuAdminReportes() {
        ImageIcon iconoReportes = new ImageIcon("src/img/HouseHunter_Menu-Administrador_Reportes.png");
        String[] opciones = {"Generar reporte consolidado.", "Volver."};
        
        int op = JOptionPane.showOptionDialog(
            null, "<html><body style='width:250px; text-align:center;'><h3>Módulo de Reportes Globales</h3>Seleccione una acción:</body></html>", 
            "Reportes", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
            iconoReportes, opciones, opciones[0]
        );
        
        if (op == 0) { 
            // 1. Traemos todas las reservas de la base de datos
            List<String[]> listaReservas = EventoController.getInstance().listarTodasLasReservas();
            
            if (listaReservas.isEmpty()) {
                JOptionPane.showMessageDialog(null, "❌ No hay ninguna reserva registrada en el hotel.", "Reportes Vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 2. Mapeamos las reservas a un formato lindo para el menú desplegable
            String[] opcionesDesplegable = new String[listaReservas.size()];
            for (int i = 0; i < listaReservas.size(); i++) {
                String[] res = listaReservas.get(i);
                // Ejemplo: "[ID: 1] - Empresa: Accenture (Ref: EVENTO-4512)"
                opcionesDesplegable[i] = "[ID: " + res[0] + "] - " + res[1] + " (Ref: " + res[2] + ")";
            }
            
            // 3. Mostramos el combo desplegable seguro
            String seleccionReserva = (String) JOptionPane.showInputDialog(
                null, "Seleccione la reserva que desea auditar:", "Selector de Informes",
                JOptionPane.PLAIN_MESSAGE, iconoReportes, opcionesDesplegable, opcionesDesplegable[0]
            );
            
            // Si el admin cancela o cierra la ventana, salimos elegantemente
            if (seleccionReserva == null) return;
            
            // 4. Averiguamos qué índice seleccionó para extraer el código_unico_evento real
            int indexSeleccionado = -1;
            for (int i = 0; i < opcionesDesplegable.length; i++) {
                if (opcionesDesplegable[i].equals(seleccionReserva)) {
                    indexSeleccionado = i;
                    break;
                }
            }
            
            // Extraemos el código único del evento oculto en el array del índice seleccionado (posición 2)
            String codEventoReal = listaReservas.get(indexSeleccionado)[2];
            
            // 5. Invocamos al reporte consolidado usando el código mapeado de forma automática
            String reporteHtml = EventoController.getInstance().obtenerReporteConsolidadoAdministrador(codEventoReal);
            
            // Pintamos el Dashboard final en la pantalla del administrador
            JOptionPane.showMessageDialog(null, reporteHtml, "Métricas Consolidadas - Reserva ID: " + listaReservas.get(indexSeleccionado)[0], JOptionPane.PLAIN_MESSAGE, iconoReportes);
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

    
    // ( Método de Alerta Genérica. )
    private void mostrarMensaje(String accion) {
        // Método genérico por si quedó alguna opción vieja cableada o en desarrollo. Muestra un aviso informativo en pantalla. Se eliminará cuando corresponda, por las dudas. - Augusto.
        JOptionPane.showMessageDialog(null, "Función: " + accion + "\n(En desarrollo)", "Módulo en Construcción", JOptionPane.INFORMATION_MESSAGE);
    }
}