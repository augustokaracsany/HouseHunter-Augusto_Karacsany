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


    // Sub-Menús
    // Vistas con Botones.

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

    // Lógica Interna
    // Eliminadas las Ventanas Extra hechas con JOptionPane ShowMessageDialog y HTML.

    /**
     * Crea una tabla nativa estilizada a partir de una matriz de datos y columnas,
     * incrustándola en el panel central dinámico con un botón Volver.
     */
    private void mostrarTablaEnPanel(String[] columnas, String[][] datos, Runnable accionVolver) {
        JPanel pnlContenedorTabla = new JPanel(new BorderLayout(0, 10));
        pnlContenedorTabla.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Crear la tabla nativa de Swing
        javax.swing.JTable tabla = new javax.swing.JTable(datos, columnas);
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(25); // Filas más espaciadas y legibles
        tabla.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        
        // Deshabilitar la edición directa de las celdas por el usuario
        tabla.setDefaultEditor(Object.class, null); 

        JScrollPane scrollPane = new JScrollPane(tabla);
        pnlContenedorTabla.add(scrollPane, BorderLayout.CENTER);

        // Botón Volver abajo integrado perfectamente al flujo
        JButton btnVolver = new JButton("VOLVER");
        btnVolver.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnVolver.addActionListener(e -> accionVolver.run());
        pnlContenedorTabla.add(btnVolver, BorderLayout.SOUTH);

        cambiarPanelDinamico(pnlContenedorTabla);
    }
    
    private void monitorearHabitaciones() {
        // Definimos las columnas de la tabla
        String[] columnas = {"Número de Habitación", "Estado actual de ocupación"};
        
        // Solicitamos la matriz limpia al controlador
        String[][] datos = HabitacionController.getInstance().obtenerEstadoHabitacionesMatriz();
        
        // Validamos si llegó vacía por algún error de conexión
        if (datos.length == 0) {
            JOptionPane.showMessageDialog(this, "⚠️ No se encontraron habitaciones registradas o error de base de datos.", "Error de carga", JOptionPane.WARNING_MESSAGE);
            mostrarSubMenuRecepcion();
            return;
        }
        
        // Mandamos a pintar la JTable en el panel dinámico con la acción para el botón Volver
        mostrarTablaEnPanel(columnas, datos, () -> mostrarSubMenuRecepcion());
    }

    private void visualizarCronograma() {
        String codEvento = JOptionPane.showInputDialog(this, "Ingrese el Código Único del Evento:", "Consultar Cronograma.", JOptionPane.QUESTION_MESSAGE);
        if (codEvento == null || codEvento.trim().isEmpty()) return;

        String[] columnas = {"Horario", "Actividad / Nombre", "Categoría", "Prioridad"};
        String[][] datos = ActividadController.getInstance().obtenerCronogramaMatriz(codEvento.trim());

        if (datos.length == 0) {
            JOptionPane.showMessageDialog(this, "❌ Sin actividades registradas para este evento.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        mostrarTablaEnPanel(columnas, datos, () -> mostrarSubMenuActividades());
    }

    private void generarReporteConsolidado() {
        
        String codEvento = JOptionPane.showInputDialog(this, "Ingrese el Código Único del Evento a auditar:", "Auditoría de Eventos", JOptionPane.QUESTION_MESSAGE);
        if (codEvento == null || codEvento.trim().isEmpty()) return;

        String[] columnas = {"Concepto / Métrica", "Valor Registrado"};
        String[][] datos = EventoController.getInstance().obtenerReporteConsolidadoMatriz(codEvento.trim());

        if (datos.length == 0) {
            JOptionPane.showMessageDialog(this, "❌ Código de evento inválido o sin datos de auditoría.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Se muestra directo en la JTable central con botón volver.
        mostrarTablaEnPanel(columnas, datos, () -> mostrarSubMenuReportes());
    }

    // Los inputs de carga rápida ( Cuadros de diálogo de entrada obligatorios. ) se mantienen por teclado.
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
        // 1. Instanciamos el panel interactivo continuo
        JPanel pnlAsistenciasContinuas = new JPanel(new BorderLayout(15, 15));
        pnlAsistenciasContinuas.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- SUB-PANEL 1: CONFIGURACIÓN DE CONTEXTO ---
        JPanel panelNorte = new JPanel(new java.awt.GridBagLayout());
        panelNorte.setBorder(javax.swing.BorderFactory.createTitledBorder("Contexto de la Actividad"));
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelNorte.add(new JLabel("Código Evento:"), gbc);
        
        gbc.gridx = 1; javax.swing.JTextField txtCod = new javax.swing.JTextField(12);
        panelNorte.add(txtCod, gbc);

        gbc.gridx = 2; JButton btnCargar = new JButton("Cargar Bloques");
        panelNorte.add(btnCargar, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelNorte.add(new JLabel("Actividad Activa:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        javax.swing.JComboBox<String> comboAct = new javax.swing.JComboBox<>();
        comboAct.setEnabled(false);
        panelNorte.add(comboAct, gbc);

        // --- SUB-PANEL 2: ACCIÓN CONTINUA (DNI + LOG) ---
        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        panelCentro.setBorder(javax.swing.BorderFactory.createTitledBorder("Control de Puerta (Fichado Rápido)"));

        JPanel panelFilaDni = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        panelFilaDni.add(new JLabel("Ingresar DNI: "));
        javax.swing.JTextField txtDni = new javax.swing.JTextField(15);
        txtDni.setFont(new Font("Arial", Font.BOLD, 14));
        txtDni.setEnabled(false);
        panelFilaDni.add(txtDni);
        panelFilaDni.add(new JLabel("💡 (Presione ENTER)"));

        javax.swing.JTextArea txtLog = new javax.swing.JTextArea(8, 40);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollLog = new JScrollPane(txtLog);

        panelCentro.add(panelFilaDni, BorderLayout.NORTH);
        panelCentro.add(scrollLog, BorderLayout.CENTER);

        // --- COMPONENTES AL CONTENEDOR PRINCIPAL ---
        pnlAsistenciasContinuas.add(panelNorte, BorderLayout.NORTH);
        pnlAsistenciasContinuas.add(panelCentro, BorderLayout.CENTER);

        // Botón Volver abajo integrado perfectamente a tu estilo de GUI
        JButton btnVolver = new JButton("VOLVER");
        btnVolver.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnVolver.addActionListener(e -> mostrarSubMenuActividades());
        pnlAsistenciasContinuas.add(btnVolver, BorderLayout.SOUTH);

        // --- LÓGICA DE CONTROLADORES ASOCIADA ---
        btnCargar.addActionListener(e -> {
            String cod = txtCod.getText().trim();
            if (cod.isEmpty()) return;
            
            String[] acts = ActividadController.getInstance().obtenerNombresActividades(cod);
            comboAct.removeAllItems();
            if (acts.length == 0) {
                txtLog.setText("❌ No se encontraron actividades para el evento: " + cod + "\n");
                comboAct.setEnabled(false);
                txtDni.setEnabled(false);
            } else {
                for (String a : acts) comboAct.addItem(a);
                comboAct.setEnabled(true);
                txtDni.setEnabled(true);
                txtLog.setText("✅ Lista de actividades cargada.\nSeleccione el bloque arriba y fije el cursor en 'Ingresar DNI'.\n\n");
                txtDni.requestFocusInWindow();
            }
        });

        txtDni.addActionListener(e -> {
            String dni = txtDni.getText().trim();
            String actSel = (String) comboAct.getSelectedItem();
            String cod = txtCod.getText().trim();
            if (dni.isEmpty() || actSel == null) return;

            // Llama a tu controlador físico para persistir en asistencias_actividades
            ActividadController.getInstance().registrarAsistenciaActividad(cod, actSel, dni);
            
            txtLog.append("🔹 [Fichado] DNI: " + dni + " -> Registrado en: " + actSel + "\n");
            txtDni.setText("");
            txtDni.requestFocusInWindow(); // Queda listo para el próximo de la fila
        });

        // Inyectamos todo el formulario directo en tu panel dinámico central
        cambiarPanelDinamico(pnlAsistenciasContinuas);
    }
}