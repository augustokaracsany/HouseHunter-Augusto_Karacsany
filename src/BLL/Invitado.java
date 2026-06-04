package BLL;

import DLL.EventoController;
import DLL.InvitadoController;
import DLL.PremioController;
import DLL.ReporteController;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import java.time.LocalDateTime;
import java.util.List;

public class Invitado extends Persona {
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String tokenAcceso; 
    private boolean asistenciaConfirmada;
    private LocalDateTime fechaConfirmacion;
    private Reserva reserva;      
    private Habitacion habitacion;

    private EventoController eventoController = EventoController.getInstance();
    private InvitadoController invitadoController = InvitadoController.getInstance();
    private PremioController premioController = PremioController.getInstance(); 
    private ReporteController reporteController = new ReporteController(); 
    
    // Constructor 1: Básico
    public Invitado(String email, String password, String nombre, Rol rol) {
        super(email, password, rol); 
        this.nombre = nombre;
        this.apellido = ""; 
        this.asistenciaConfirmada = false;
    }

    // Constructor 2: Registro con Apellido
    public Invitado(String email, String password, String nombre, String apellido, Rol rol) {
        super(email, password, rol);
        this.nombre = nombre;
        this.apellido = apellido;
        this.asistenciaConfirmada = false;
    }

    public Invitado(int id, String email, String nombre, String apellido, String dni, String telefono, String tokenAcceso, boolean asistenciaConfirmada) {
        super(email, "", Rol.INVITADO); 
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.tokenAcceso = tokenAcceso;
        this.asistenciaConfirmada = asistenciaConfirmada;
    }

    @Override
    public String getNombre() {
        return nombre + (apellido != null && !apellido.isEmpty() ? " " + apellido : "");
    }

    public String getNombreSolo() {
        return this.nombre;
    }

    @Override
    public void mostrarMenu() {
        if (reserva == null) {
            String token = JOptionPane.showInputDialog(null, 
                "Para acceder a su evento, ingrese el token que recibió por correo:\n( Ej: XXXX )", 
                "Validación de acceso.", JOptionPane.QUESTION_MESSAGE);
            if (token == null || token.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Acceso denegado. No se proporcionó token.", "Error.", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Invitado invitadoValidado = InvitadoController.getInstance().validarToken(token.trim());
            if (invitadoValidado == null) {
                JOptionPane.showMessageDialog(null, "Token inválido o expirado. No puede acceder al sistema.", "Error.", JOptionPane.ERROR_MESSAGE);
                return;
            }
            this.id = invitadoValidado.getId();
            this.reserva = invitadoValidado.getReserva();
            this.tokenAcceso = invitadoValidado.getTokenAcceso();
            this.asistenciaConfirmada = invitadoValidado.isAsistenciaConfirmada();

            // Sincronización limpia utilizando el ID directo de la reserva
            if (this.reserva != null) {
                Reserva reservaCompleta = eventoController.obtenerReservaPorId(this.reserva.getId());
                if (reservaCompleta != null) {
                    this.reserva = reservaCompleta;
                }
            }

            JOptionPane.showMessageDialog(null, "Acceso concedido. Bienvenido al evento del: " + reserva.getFechaInicio());
        }

        int seleccion;
        do {
            String estadoAsistenciaTexto = asistenciaConfirmada ? "Confirmada." : "Pendiente.";
            String codigoEventoTexto = "<br><b style='color:red;'>Sin código de evento asignado</b>";
            
            // Se muestra el código siempre que la reserva esté disponible, tal como en el menú de la empresa
            if (reserva != null) {
                String cod = reserva.getCodigoUnicoEvento();
                String codTexto = (cod != null && !cod.trim().isEmpty()) ? cod : "Sin asignar";
                codigoEventoTexto = "<br><b style='color:#0055ff;'>Código Único del Evento: " + codTexto + "</b>";
            }

            ImageIcon iconoInvitado = new ImageIcon("src/img/HouseHunter_Menu-Invitado.png");
            String tituloHtml = "<html><body style='width: 350px; text-align: center;'>"
                              + "<h2>Panel del Invitado</h2>"
                              + "<b>Hola, " + getNombre() + "</b><br>"
                              + "<b>Estadía:</b> " + reserva.getFechaInicio() + " al " + reserva.getFechaFin() + "<br>"
                              + "<b>Estado asistencia:</b> " + estadoAsistenciaTexto
                              + codigoEventoTexto
                              + "<hr>Seleccione una opción:</body></html>";

            String[] opciones = {
                "VER CRONOGRAMA COMPLETO.",
                "CONFIRMAR MI ASISTENCIA.", 
                "EXPLORAR ACTIVIDADES.",
                "CONSULTAR MI HABITACIÓN.",
                "PARTICIPAR EN SORTEOS / PREMIOS.",
                "CERRAR SESIÓN."
            };

            seleccion = JOptionPane.showOptionDialog(null, tituloHtml, "HOUSEHUNTER - INVITADO",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    iconoInvitado, opciones, opciones[0]);

            switch (seleccion) {
                case 0: verCronograma(); break;
                case 1: confirmarAsistencia(); break;
                case 2: verDetalleActividad(); break;
                case 3: consultarHabitacion(); break;
                case 4: gestionarPremiosYSorteos(); break; 
                case 5: JOptionPane.showMessageDialog(null, "Sesión cerrada. ¡Hasta pronto!"); break;
            }
        } while (seleccion != 5 && seleccion != -1);
    }

    private void verCronograma() {
        if (reserva == null) { mostrarErrorSinReserva(); return; }
        List<Actividad> actividades = eventoController.obtenerActividadesPorReserva(reserva.getId());
        if (actividades.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay Actividades programadas para este evento aún.", "Cronograma.", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder("CRONOGRAMA DEL EVENTO\n\n");
        for (Actividad a : actividades) {
            sb.append("- ").append(a.getNombre())
              .append("\n    Hora: ").append(a.getFechaHora().toLocalTime())
              .append("\n    Duración: ").append(a.getDuracionMinutos()).append(" min")
              .append("\n    Categoría: ").append(a.getCategoria())
              .append("\n    Importancia: ").append(a.getImportancia())
              .append("\n\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Cronograma.", JOptionPane.PLAIN_MESSAGE);
    }

    private void confirmarAsistencia() {
        if (asistenciaConfirmada) {
            JOptionPane.showMessageDialog(null, "Ya has confirmado tu Asistencia anteriormente.", "Información.", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(null, 
            "¿Confirmas tu Asistencia para la estadía del " + reserva.getFechaInicio() + " al " + reserva.getFechaFin() + "?\nEsta acción no se puede deshacer.", 
            "Confirmar Asistencia.", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            
        if (opcion == JOptionPane.YES_OPTION) {
            boolean ok = invitadoController.confirmarAsistencia(this.id); 
            if (ok) {
                this.asistenciaConfirmada = true;
                this.fechaConfirmacion = LocalDateTime.now();
                
                String codigoDeBD = (reserva != null && reserva.getCodigoUnicoEvento() != null) ? reserva.getCodigoUnicoEvento() : "N/A";

                String cartelExito = String.format(
                    "<html><body style='text-align: center; width: 260px;'>"
                    + "<h3 style='color: green;'>¡Confirmación Exitosa!</h3>"
                    + "<p>Gracias por tu colaboración.</p>"
                    + "<hr>"
                    + "<p>Presentá este código en la recepción del hotel para que te asignen tu habitación:</p>"
                    + "<h2 style='color: #0055ff; letter-spacing: 1px;'>%s</h2>"
                    + "</body></html>",
                    codigoDeBD
                );

                JOptionPane.showMessageDialog(null, cartelExito, "HouseHunter - Asistencia Registrada", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al confirmar Asistencia. Intente más tarde.", "Error.", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void verDetalleActividad() {
        if (reserva == null) { mostrarErrorSinReserva(); return; }
        List<Actividad> actividades = eventoController.obtenerActividadesPorReserva(reserva.getId());
        if (actividades.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay Actividades disponibles.", "Actividades.", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] nombres = actividades.stream().map(Actividad::getNombre).toArray(String[]::new);
        int idx = JOptionPane.showOptionDialog(null, "Seleccione la Actividad que desea consultar:", 
                "Explorar Actividades.", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, nombres, nombres[0]);
        if (idx >= 0) {
            Actividad a = actividades.get(idx);
            String detalle = String.format(
                "NOMBRE: %s\n\nDescripción: %s\n\nHora actividad: %s\nCategoría: %s\nImportancia: %s",
                a.getNombre(),
                a.getDescripcion() != null ? a.getDescripcion() : "( Sin descripción. )",
                a.getFechaHora().toLocalTime(),
                a.getCategoria(),
                a.getImportancia()
            );
            JOptionPane.showMessageDialog(null, detalle, "Detalle de la Actividad.", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void consultarHabitacion() {
        Habitacion hab = invitadoController.obtenerHabitacionInvitado(this.id);
        if (hab == null) {
            JOptionPane.showMessageDialog(null, "Aún no se le ha asignado una habitación. Consulte con recepción.", 
                    "Habitación.", JOptionPane.WARNING_MESSAGE);
        } else {
            String info = String.format("Su habitación asignada:\n\nNúmero: %s\nEstado actual: %s",
                    hab.getNumero(), hab.getEstado());
            JOptionPane.showMessageDialog(null, info, "Mi habitación.", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void gestionarPremiosYSorteos() {
        String[] opcionesPremios = {
            "Inscribirse en un Sorteo.",
            "Obtener Voucher de Premio.",
            "Volver al Menú Principal."
        };

        int seleccion = JOptionPane.showOptionDialog(null, "Seleccione la acción que desea realizar:", 
                "Gestión de Premios y Sorteos.", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, opcionesPremios, opcionesPremios[0]);

        if (seleccion == 0) {
            participarSorteo();
        } else if (seleccion == 1) {
            obtenerVoucher();
        }
    }

    private void participarSorteo() {
        if (!asistenciaConfirmada) {
            JOptionPane.showMessageDialog(null, "Debe confirmar su asistencia al evento antes de participar en sorteos.", 
                    "Requisito.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Premio> premios = premioController.listarPremiosDisponibles();
        if (premios.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay premios activos en este momento.", "Premios.", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] nombresPremios = premios.stream().map(Premio::getNombre).toArray(String[]::new);
        int sel = JOptionPane.showOptionDialog(null, "Seleccione the premio al que desea participar:", 
                "Participar en sorteo.", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, nombresPremios, nombresPremios[0]);
        if (sel >= 0) {
            Premio p = premios.get(sel);
            boolean exito = premioController.participarEnSorteo(this.id, p.getId());
            if (exito) {
                JOptionPane.showMessageDialog(null, "¡Has participado correctamente!", 
                        "Participación registrada.", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo registrar. Quizás ya estás anotado.", "Error.", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void obtenerVoucher() {
        List<Premio> premios = premioController.listarPremiosDisponibles();
        if (premios.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay premios activos.", "Voucher.", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] nombresPremios = premios.stream().map(Premio::getNombre).toArray(String[]::new);
        int sel = JOptionPane.showOptionDialog(null, "¿Para qué premio desea obtener su Voucher?", 
                "Obtener Voucher.", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, nombresPremios, nombresPremios[0]);
        if (sel >= 0) {
            Premio p = premios.get(sel);
            String voucher = premioController.obtenerVoucher(this.id, p.getId()); 
            if (voucher != null && !voucher.isEmpty()) {
                JOptionPane.showMessageDialog(null, "¡Felicidades!\n\nVoucher: " + voucher + "\nPreséntelo en recepción.", 
                        "Voucher.", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No es ganador o el sorteo no se realizó.", 
                        "No es ganador.", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void mostrarErrorSinReserva() {
        JOptionPane.showMessageDialog(null, "No hay información de reserva asociada. Contacte al organizador.", 
                "Error.", JOptionPane.ERROR_MESSAGE);
    }

    // Getters & Setters
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTokenAcceso() { 
        return (tokenAcceso == null || tokenAcceso.trim().isEmpty()) ? "Sin generar" : tokenAcceso; 
    }
    public void setTokenAcceso(String tokenAcceso) { this.tokenAcceso = tokenAcceso; }
    
    public boolean isAsistenciaConfirmada() { return asistenciaConfirmada; }
    public void setAsistenciaConfirmada(boolean asistenciaConfirmada) { this.asistenciaConfirmada = asistenciaConfirmada; }
    public LocalDateTime getFechaConfirmacion() { return fechaConfirmacion; }
    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) { this.fechaConfirmacion = fechaConfirmacion; }
    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    public Habitacion getHabitacion() { return habitacion; }
    public void setHabitacion(Habitacion habitacion) { this.habitacion = habitacion; }
}