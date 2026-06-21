package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import BLL.Administrador;
import DLL.ActividadController;
import DLL.EventoController;
import DLL.HabitacionController;

public class AdministradorMenuGrafico extends JFrame {

    private static final long serialVersionUID = 1L;
    
    private Administrador admin;

    // Componentes gráficos principales
    private JPanel contentPane;
    private JPanel pnlDinamico;
    private JLabel lblAdminNombre;
    private JLabel lblAdminDni;

    public AdministradorMenuGrafico(Administrador admin) {
        this.admin = admin;
        initialize();
    }

    private void initialize() {
        setTitle("HOUSEHUNTER - PANEL DE ADMINISTRACIÓN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 550);
        setLocationRelativeTo(null); 
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(10, 0));

        // PANEL LATERAL (Menú de Navegación)
        JPanel pnlLateral = new JPanel();
        pnlLateral.setBackground(SystemColor.controlDkShadow);
        pnlLateral.setBorder(new LineBorder(new Color(0, 0, 0)));
        contentPane.add(pnlLateral, BorderLayout.WEST);
        pnlLateral.setLayout(new GridLayout(5, 1, 0, 15));

        JLabel lblTituloMenu = new JLabel("CONTROL PANEL");
        lblTituloMenu.setForeground(Color.WHITE);
        lblTituloMenu.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblTituloMenu.setHorizontalAlignment(SwingConstants.CENTER);
        pnlLateral.add(lblTituloMenu);

        JButton btnRecepcion = new JButton("RECEPCIÓN");
        btnRecepcion.addActionListener(e -> mostrarSubMenuRecepcion());
        pnlLateral.add(btnRecepcion);

        JButton btnActividades = new JButton("ACTIVIDADES");
        btnActividades.addActionListener(e -> mostrarSubMenuActividades());
        pnlLateral.add(btnActividades);

        JButton btnReportes = new JButton("REPORTES");
        btnReportes.addActionListener(e -> mostrarSubMenuReportes());
        pnlLateral.add(btnReportes);

        JButton btnCerrarSesion = new JButton("CERRAR SESIÓN");
        btnCerrarSesion.setForeground(Color.RED);
        btnCerrarSesion.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Sesión cerrada de " + admin.getNombre());
            dispose();
        });
        pnlLateral.add(btnCerrarSesion);

        // PANEL CENTRAL Y CABECERA
        JPanel pnlCentro = new JPanel();
        contentPane.add(pnlCentro, BorderLayout.CENTER);
        pnlCentro.setLayout(new BorderLayout(0, 10));

        JPanel pnlCabeceraInfo = new JPanel();
        pnlCabeceraInfo.setBackground(new Color(245, 245, 245));
        pnlCabeceraInfo.setBorder(new LineBorder(Color.LIGHT_GRAY));
        pnlCentro.add(pnlCabeceraInfo, BorderLayout.NORTH);
        pnlCabeceraInfo.setLayout(new GridLayout(1, 2, 10, 0));

        lblAdminNombre = new JLabel("Admin Activo: " + admin.getNombre());
        lblAdminNombre.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblAdminNombre.setForeground(new Color(0, 102, 204));
        lblAdminNombre.setHorizontalAlignment(SwingConstants.CENTER);
        pnlCabeceraInfo.add(lblAdminNombre);

        lblAdminDni = new JLabel("DNI: " + admin.getDni());
        lblAdminDni.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblAdminDni.setHorizontalAlignment(SwingConstants.CENTER);
        pnlCabeceraInfo.add(lblAdminDni);

        pnlDinamico = new JPanel();
        pnlDinamico.setBorder(new LineBorder(Color.GRAY));
        pnlCentro.add(pnlDinamico, BorderLayout.CENTER);
        pnlDinamico.setLayout(new BorderLayout(0, 0));
        
        mostrarPantallaBienvenida();
    }

    private void mostrarPantallaBienvenida() {
        pnlDinamico.removeAll();
        JLabel lblVistaPrevia = new JLabel("Seleccione un módulo del menú izquierdo para gestionar el hotel.");
        lblVistaPrevia.setHorizontalAlignment(SwingConstants.CENTER);
        pnlDinamico.add(lblVistaPrevia, BorderLayout.CENTER);
        pnlDinamico.revalidate();
        pnlDinamico.repaint();
    }

    private void cambiarPanelDinamico(JPanel nuevoContenido) {
        pnlDinamico.removeAll();
        pnlDinamico.add(nuevoContenido, BorderLayout.CENTER);
        pnlDinamico.revalidate();
        pnlDinamico.repaint();
    }

    /**
     * Encapsula cualquier HTML devuelto por los controladores y le añade un botón Volver abajo.
     */
    private void mostrarContenidoHtmlEnPanel(String htmlContenido, Runnable accionVolver) {
        JPanel pnlContenedorHtml = new JPanel(new BorderLayout(0, 10));
        pnlContenedorHtml.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Usamos JTextPane para que interprete el HTML nativamente sin romper layouts
        JTextPane txtPane = new JTextPane();
        txtPane.setContentType("text/html");
        txtPane.setText(htmlContenido);
        txtPane.setEditable(false);
        txtPane.setCaretPosition(0); // Scroll arriba de todo por defecto

        JScrollPane scrollPane = new JScrollPane(txtPane);
        pnlContenedorHtml.add(scrollPane, BorderLayout.CENTER);

        // Botón Volver abajo
        JButton btnVolver = new JButton("VOLVER");
        btnVolver.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnVolver.addActionListener(e -> accionVolver.run());
        pnlContenedorHtml.add(btnVolver, BorderLayout.SOUTH);

        cambiarPanelDinamico(pnlContenedorHtml);
    }

    // ==========================================
    // SUB-MENÚS (VISTAS DE BOTONES)
    // ==========================================

    private void mostrarSubMenuRecepcion() {
        JPanel pnlRecepcion = new JPanel(new GridLayout(3, 1, 10, 15));
        pnlRecepcion.setBorder(new EmptyBorder(40, 60, 40, 60));

        JButton btnValidarPrevia = new JButton("VALIDAR INVITADO AUTORIZADO");
        btnValidarPrevia.addActionListener(e -> validarInvitadoPrevia());

        JButton btnProcesarCheckIn = new JButton("REGISTRAR CHECK-IN EXITOSO");
        btnProcesarCheckIn.addActionListener(e -> ejecutarFlujoCheckInCompleto());

        JButton btnMonitorearHab = new JButton("MONITOREAR OCUPACIÓN DE HABITACIONES");
        btnMonitorearHab.addActionListener(e -> monitorearHabitaciones());

        pnlRecepcion.add(btnValidarPrevia);
        pnlRecepcion.add(btnProcesarCheckIn);
        pnlRecepcion.add(btnMonitorearHab);

        cambiarPanelDinamico(pnlRecepcion);
    }

    private void mostrarSubMenuActividades() {
        JPanel pnlActividades = new JPanel(new GridLayout(2, 1, 10, 15));
        pnlActividades.setBorder(new EmptyBorder(60, 60, 60, 60));

        JButton btnAsistencia = new JButton("MONITOREAR ACTIVIDADES / ASISTENCIAS");
        btnAsistencia.addActionListener(e -> tomarAsistenciaInvitados());

        JButton btnCronograma = new JButton("VISUALIZAR CRONOGRAMA DE EVENTO");
        btnCronograma.addActionListener(e -> visualizarCronograma());

        pnlActividades.add(btnAsistencia);
        pnlActividades.add(btnCronograma);

        cambiarPanelDinamico(pnlActividades);
    }

    private void mostrarSubMenuReportes() {
        JPanel pnlReportes = new JPanel(new GridLayout(1, 1, 10, 15));
        pnlReportes.setBorder(new EmptyBorder(80, 60, 80, 60));

        JButton btnReporteConsolidado = new JButton("GENERAR REPORTE CONSOLIDADO GLOBAL");
        btnReporteConsolidado.addActionListener(e -> generarReporteConsolidado());

        pnlReportes.add(btnReporteConsolidado);

        cambiarPanelDinamico(pnlReportes);
    }

    // ==========================================
    // LÓGICA INTERNA SIN VENTANAS EXTRA
    // ==========================================

    private void monitorearHabitaciones() {
        String estadoHabitacionesHtml = HabitacionController.getInstance().obtenerEstadoHabitacionesHtml(); 
        // En vez de showMessageDialog, lo pintamos adentro con el botón volver
        mostrarContenidoHtmlEnPanel(estadoHabitacionesHtml, () -> mostrarSubMenuRecepcion());
    }

    private void visualizarCronograma() {
        String codEvento = JOptionPane.showInputDialog(this, "Ingrese el Código Único del Evento:", "Consultar Cronograma.", JOptionPane.QUESTION_MESSAGE);
        if (codEvento != null && !codEvento.trim().isEmpty()) {
            String agendaHtml = ActividadController.getInstance().obtenerCronogramaEventos(codEvento.trim());
            mostrarContenidoHtmlEnPanel(agendaHtml, () -> mostrarSubMenuActividades());
        }
    }

    private void generarReporteConsolidado() {
        List<String[]> listaReservas = EventoController.getInstance().listarTodasLasReservas();
        
        if (listaReservas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ No hay ninguna reserva registrada en el hotel.", "Reportes Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String[] opcionesDesplegable = new String[listaReservas.size()];
        for (int i = 0; i < listaReservas.size(); i++) {
            String[] res = listaReservas.get(i);
            opcionesDesplegable[i] = "[ID: " + res[0] + "] - " + res[1] + " (Ref: " + res[2] + ")";
        }
        
        String seleccionReserva = (String) JOptionPane.showInputDialog(
            this, "Seleccione la reserva que desea auditar:", "Selector de Informes",
            JOptionPane.PLAIN_MESSAGE, null, opcionesDesplegable, opcionesDesplegable[0]
        );
        
        if (seleccionReserva == null) return;
        
        int indexSeleccionado = -1;
        for (int i = 0; i < opcionesDesplegable.length; i++) {
            if (opcionesDesplegable[i].equals(seleccionReserva)) {
                indexSeleccionado = i;
                break;
            }
        }
        
        String codEventoReal = listaReservas.get(indexSeleccionado)[2];
        String reporteHtml = EventoController.getInstance().obtenerReporteConsolidadoAdministrador(codEventoReal);
        
        // Renderizado directo en el panel del medio del administrador
        mostrarContenidoHtmlEnPanel(reporteHtml, () -> mostrarSubMenuReportes());
    }

    // Los inputs de carga rápida (cuadros de diálogo de entrada obligatorios) se mantienen por teclado
    private void validarInvitadoPrevia() {
        String codEvento = JOptionPane.showInputDialog(this, "Ingrese el Código Único del Evento:", "Validación Previa", JOptionPane.QUESTION_MESSAGE);
        if (codEvento == null || codEvento.trim().isEmpty()) return;
        
        String dniInvitado = JOptionPane.showInputDialog(this, "Ingrese el DNI del Invitado:", "Validación Previa", JOptionPane.QUESTION_MESSAGE);
        if (dniInvitado == null || dniInvitado.trim().isEmpty()) return;
        
        boolean autorizado = EventoController.getInstance().validarInvitadoPrevia(codEvento.trim(), dniInvitado.trim());
        if (autorizado) {
            JOptionPane.showMessageDialog(this, "✅ El invitado con DNI " + dniInvitado + " se encuentra AUTORIZADO.", "Verificación Exitosa", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "❌ El invitado NO figura en la lista previa de este evento.", "Verificación Fallida", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ejecutarFlujoCheckInCompleto() {
        String codEvento = JOptionPane.showInputDialog(this, "Ingrese el Código Único del Evento:", "Procesar Asignación", JOptionPane.QUESTION_MESSAGE);
        if (codEvento == null || codEvento.trim().isEmpty()) return;

        String dniInvitado = JOptionPane.showInputDialog(this, "Ingrese el DNI del Huésped:", "Procesar Asignación", JOptionPane.QUESTION_MESSAGE);
        if (dniInvitado == null || dniInvitado.trim().isEmpty()) return;

        String numHabitacion = JOptionPane.showInputDialog(this, "Ingrese el Número de Habitación (Ej: 101, 102):", "Procesar Asignación", JOptionPane.QUESTION_MESSAGE);
        if (numHabitacion == null || numHabitacion.trim().isEmpty()) return;

        HabitacionController.getInstance().procesarCheckInHabitacion(codEvento.trim(), dniInvitado.trim(), numHabitacion.trim());
    }

    private void tomarAsistenciaInvitados() {
        String codEvento = JOptionPane.showInputDialog(this, "Ingrese el Código Único del Evento:", "Control de Asistencias", JOptionPane.QUESTION_MESSAGE);
        if (codEvento == null || codEvento.trim().isEmpty()) return; 
        
        String[] actividades = ActividadController.getInstance().obtenerNombresActividades(codEvento.trim());
        if (actividades.length == 0) { 
            JOptionPane.showMessageDialog(this, "❌ No hay actividades cargadas o el evento no existe.", "Aviso.", JOptionPane.WARNING_MESSAGE);
            return; 
        }
        
        String actividadSeleccionada = (String) JOptionPane.showInputDialog(
            this, "Seleccione la actividad a gestionar:", "Monitoreo de Bloques.",
            JOptionPane.PLAIN_MESSAGE, null, actividades, actividades[0]
        );
        
        if (actividadSeleccionada != null) { 
            String dniInvitado = JOptionPane.showInputDialog(this, "Ingrese el DNI del Invitado que asistió:", "Tomar Asistencia.", JOptionPane.QUESTION_MESSAGE);
            if (dniInvitado != null && !dniInvitado.trim().isEmpty()) {
                ActividadController.getInstance().registrarAsistenciaActividad(codEvento.trim(), actividadSeleccionada, dniInvitado.trim());
            }
        }
    }
}