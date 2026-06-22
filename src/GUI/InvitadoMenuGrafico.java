package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import BLL.Actividad;
import BLL.Habitacion;
import BLL.Invitado;
import BLL.Premio;
import BLL.Reserva;
import DLL.EventoController;
import DLL.InvitadoController;
import DLL.PremioController;

public class InvitadoMenuGrafico extends JFrame {

    private static final long serialVersionUID = 1L;
    
    private Invitado invitado;
    
    // Controladores delegados de la lógica de datos
    private EventoController eventoController = EventoController.getInstance();
    private InvitadoController invitadoController = InvitadoController.getInstance();
    private PremioController premioController = PremioController.getInstance(); 

    // Componentes de Interfaz
    private JPanel contentPane;
    private JPanel pnlDinamico;
    private JLabel lblHeaderEstado;
    private JLabel lblHeaderEvento;

    public InvitadoMenuGrafico(Invitado invitado) {
        this.invitado = invitado;
        
        // Ejecución obligatoria de la validación inicial de Token en el arranque
        if (this.invitado.getReserva() == null) {
            boolean validado = ejecutarValidacionToken();
            if (!validado) {
                // Si cancela o falla el token, destruimos inmediatamente la ventana sin arrancar la UI
                this.dispose();
                return;
            }
        }
        
        initialize();
    }

    private boolean ejecutarValidacionToken() {
        String token = JOptionPane.showInputDialog(null, 
            "Para acceder a su evento, ingrese el token que recibió por correo:\n( Ej: XXXX )", 
            "Validación de acceso.", JOptionPane.QUESTION_MESSAGE);
            
        if (token == null || token.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Acceso denegado. No se proporcionó token.", "Error.", JOptionPane.ERROR_MESSAGE);
            volverAlMainLogin();
            return false;
        }
        
        Invitado invitadoValidado = invitadoController.validarToken(token.trim());
        if (invitadoValidado == null) {
            JOptionPane.showMessageDialog(null, "Token inválido o expirado. No puede acceder al sistema.", "Error.", JOptionPane.ERROR_MESSAGE);
            volverAlMainLogin();
            return false;
        }
        
        // Sincronización del modelo de datos
        invitado.setId(invitadoValidado.getId()); 
        invitado.setReserva(invitadoValidado.getReserva());
        invitado.setTokenAcceso(invitadoValidado.getTokenAcceso());
        invitado.setAsistenciaConfirmada(invitadoValidado.isAsistenciaConfirmada());

        if (invitado.getReserva() != null) {
            Reserva reservaCompleta = eventoController.obtenerReservaPorId(invitado.getReserva().getId());
            if (reservaCompleta != null) {
                invitado.setReserva(reservaCompleta);
            }
        }

        JOptionPane.showMessageDialog(null, "Acceso concedido. Bienvenido al evento del: " + invitado.getReserva().getFechaInicio());
        return true;
    }

    private void initialize() {
        setTitle("HOUSEHUNTER - PANEL DEL INVITADO");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 550);
        setLocationRelativeTo(null);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(10, 0));

        // PANEL LATERAL (Navegación del Huésped)
        JPanel pnlLateral = new JPanel();
        pnlLateral.setBackground(new Color(43, 48, 53)); // Gris oscuro moderno
        pnlLateral.setBorder(new LineBorder(Color.BLACK));
        contentPane.add(pnlLateral, BorderLayout.WEST);
        pnlLateral.setLayout(new GridLayout(6, 1, 0, 12));

        JLabel lblTituloMenu = new JLabel("MI PORTAL");
        lblTituloMenu.setForeground(Color.WHITE);
        lblTituloMenu.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblTituloMenu.setHorizontalAlignment(SwingConstants.CENTER);
        pnlLateral.add(lblTituloMenu);

        JButton btnCronograma = new JButton("VER CRONOGRAMA");
        btnCronograma.addActionListener(e -> verCronograma());
        pnlLateral.add(btnCronograma);

        JButton btnAsistencia = new JButton("CONFIRMAR ASISTENCIA");
        btnAsistencia.addActionListener(e -> confirmarAsistencia());
        pnlLateral.add(btnAsistencia);

        JButton btnHabitacion = new JButton("MI HABITACIÓN");
        btnHabitacion.addActionListener(e -> consultarHabitacion());
        pnlLateral.add(btnHabitacion);

        JButton btnSorteos = new JButton("SORTEOS Y PREMIOS");
        btnSorteos.addActionListener(e -> gestionarPremiosYSorteos());
        pnlLateral.add(btnSorteos);

        JButton btnCerrarSesion = new JButton("CERRAR SESIÓN");
        btnCerrarSesion.setForeground(Color.RED);
        btnCerrarSesion.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Sesión cerrada. ¡Hasta pronto!", "Logout", JOptionPane.INFORMATION_MESSAGE);
            volverAlMainLogin();
        });
        pnlLateral.add(btnCerrarSesion);

        // PANEL CENTRAL Y CABECERA INFORMATIVA
        JPanel pnlCentro = new JPanel();
        contentPane.add(pnlCentro, BorderLayout.CENTER);
        pnlCentro.setLayout(new BorderLayout(0, 10));

        JPanel pnlCabeceraInfo = new JPanel();
        pnlCabeceraInfo.setBackground(new Color(240, 244, 248));
        pnlCabeceraInfo.setBorder(new LineBorder(Color.LIGHT_GRAY));
        pnlCentro.add(pnlCabeceraInfo, BorderLayout.NORTH);
        pnlCabeceraInfo.setLayout(new GridLayout(1, 2, 10, 0));

        JLabel lblInvitadoNombre = new JLabel("Huésped: " + invitado.getNombre());
        lblInvitadoNombre.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblInvitadoNombre.setForeground(new Color(0, 102, 204));
        lblInvitadoNombre.setHorizontalAlignment(SwingConstants.CENTER);
        pnlCabeceraInfo.add(lblInvitadoNombre);

        lblHeaderEstado = new JLabel("Asistencia: " + (invitado.isAsistenciaConfirmada() ? "Confirmada" : "Pendiente"));
        lblHeaderEstado.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblHeaderEstado.setHorizontalAlignment(SwingConstants.CENTER);
        pnlCabeceraInfo.add(lblHeaderEstado);

        pnlDinamico = new JPanel();
        pnlDinamico.setBorder(new LineBorder(Color.GRAY));
        pnlCentro.add(pnlDinamico, BorderLayout.CENTER);
        pnlDinamico.setLayout(new BorderLayout(0, 0));
        
        mostrarPantallaBienvenida();
    }

    private void mostrarPantallaBienvenida() {
        pnlDinamico.removeAll();
        Reserva res = invitado.getReserva();
        String txtReserva = (res != null) ? "Estadía pactada del " + res.getFechaInicio() + " al " + res.getFechaFin() : "";
        
        JLabel lblBienvenida = new JLabel("<html><body style='text-align: center;'>"
                + "<h3>¡Hola, Bienvenido a tu espacio de Huésped!</h3>"
                + "<p>Selecciona una pestaña del panel izquierdo para explorar los servicios de tu estadía.</p><br>"
                + "<b>" + txtReserva + "</b>"
                + "</body></html>");
        lblBienvenida.setHorizontalAlignment(SwingConstants.CENTER);
        pnlDinamico.add(lblBienvenida, BorderLayout.CENTER);
        pnlDinamico.revalidate();
        pnlDinamico.repaint();
    }

    private void cambiarPanelDinamico(JPanel nuevoContenido) {
        pnlDinamico.removeAll();
        pnlDinamico.add(nuevoContenido, BorderLayout.CENTER);
        pnlDinamico.revalidate();
        pnlDinamico.repaint();
    }

    private void volverAlMainLogin() {
        MainMenuGrafico main = new MainMenuGrafico();
        main.setVisible(true);
        dispose();
    }

    // --- MÓDULO 1: CRONOGRAMA EN TABLA ---
    private void verCronograma() {
        if (invitado.getReserva() == null) { return; }
        List<Actividad> actividades = eventoController.obtenerActividadesPorReserva(invitado.getReserva().getId());
        
        String[] columnas = {"Actividad / Módulo", "Horario", "Duración", "Categoría", "Prioridad"};
        String[][] datos = new String[actividades.size()][5];
        
        for (int i = 0; i < actividades.size(); i++) {
            Actividad a = actividades.get(i);
            datos[i][0] = a.getNombre();
            datos[i][1] = a.getFechaHora().toLocalTime().toString();
            datos[i][2] = a.getDuracionMinutos() + " min";
            datos[i][3] = a.getCategoria();
            datos[i][4] = a.getImportancia().name(); // o getImportancia() 
        }

        mostrarTablaEnPanel(columnas, datos, () -> mostrarPantallaBienvenida());
    }

    // --- MÓDULO 2: CONFIRMACIÓN DE ASISTENCIA ---
    private void confirmarAsistencia() {
        if (invitado.isAsistenciaConfirmada()) {
            JOptionPane.showMessageDialog(this, "Su asistencia ya se encuentra confirmada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this, 
            "¿Desea confirmar su asistencia definitiva al evento corporativo?", 
            "Confirmación", JOptionPane.YES_NO_OPTION);
            
        if (opcion == JOptionPane.YES_OPTION) {
            boolean ok = invitadoController.confirmarAsistencia(invitado.getId());
            if (ok) {
                invitado.setAsistenciaConfirmada(true);
                invitado.setFechaConfirmacion(LocalDateTime.now());
                lblHeaderEstado.setText("Asistencia: Confirmada");
                
                String cod = (invitado.getReserva() != null) ? invitado.getReserva().getCodigoUnicoEvento() : "N/A";
                String cartelHtml = "<html><body style='text-align: center; width: 220px;'>"
                        + "<h4 style='color:green;'>¡Asistencia Confirmada!</h4>"
                        + "<p>Presente este código único en la recepción física al ingresar al establecimiento:</p>"
                        + "<h2 style='color:#0055ff;'>" + cod + "</h2>"
                        + "</body></html>";
                
                JOptionPane.showMessageDialog(this, cartelHtml, "Código de Acceso", JOptionPane.INFORMATION_MESSAGE);
                mostrarPantallaBienvenida();
            } else {
                JOptionPane.showMessageDialog(this, "Error de enlace con la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // MÓDULO 3: CONSULTAR HABITACIÓN ASIGNADA ---
    private void consultarHabitacion() {
        Habitacion hab = invitadoController.obtenerHabitacionInvitado(invitado.getId());
        JPanel pnlHab = new JPanel(new BorderLayout());
        pnlHab.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        String textoInfo;
        if (hab == null) {
            textoInfo = "<html><body style='text-align:center;'><h3>Aún no posee una Habitación asignada.</h3>"
                      + "<p>Por favor, efectúe el Check-In formal en el mostrador de Recepción.</p></body></html>";
        } else {
            textoInfo = "<html><body style='text-align:center; color: #155724;'>"
                      + "<h2>Su Habitación Asignada</h2><hr>"
                      + "<h3>NÚMERO DE HABITACIÓN: <b style='font-size:20px; color:#0055ff;'>" + hab.getNumero() + "</b></h3>"
                      + "<p><b>Estado de plaza:</b> " + hab.getEstado() + "</p>"
                      + "</body></html>";
        }
        
        JLabel lblInfo = new JLabel(textoInfo);
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        pnlHab.add(lblInfo, BorderLayout.CENTER);
        
        JButton btnVolver = new JButton("VOLVER");
        btnVolver.addActionListener(e -> mostrarPantallaBienvenida());
        pnlHab.add(btnVolver, BorderLayout.SOUTH);
        
        cambiarPanelDinamico(pnlHab);
    }

    // MÓDULO 4: PREMIOS Y SORTEOS ---
    private void gestionarPremiosYSorteos() {
        JPanel pnlPremios = new JPanel(new GridLayout(2, 1, 0, 20));
        pnlPremios.setBorder(new EmptyBorder(60, 100, 60, 100));

        JButton btnInscribirse = new JButton("INSCRIBIRSE EN SORTEOS COMPLEMENTARIOS");
        btnInscribirse.addActionListener(e -> participarSorteo());

        JButton btnVoucher = new JButton("RECLAMAR MI VOUCHER DE GANADOR");
        btnVoucher.addActionListener(e -> obtenerVoucher());

        pnlPremios.add(btnInscribirse);
        pnlPremios.add(btnVoucher);

        cambiarPanelDinamico(pnlPremios);
    }

    private void participarSorteo() {
        if (!invitado.isAsistenciaConfirmada()) {
            JOptionPane.showMessageDialog(this, "Debe confirmar su asistencia al evento antes de participar en sorteos.", "Requisito", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Premio> premios = premioController.listarPremiosDisponibles();
        if (premios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay premios activos en este momento.", "Premios", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] nombresPremios = premios.stream().map(Premio::getNombre).toArray(String[]::new);
        int sel = JOptionPane.showOptionDialog(this, "Seleccione el premio al que desea participar:", 
                "Participar en sorteo", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, nombresPremios, nombresPremios[0]);
        if (sel >= 0) {
            boolean exito = premioController.participarEnSorteo(invitado.getId(), premios.get(sel).getId());
            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Has participado correctamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar. Quizás ya estás anotado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void obtenerVoucher() {
        List<Premio> premios = premioController.listarPremiosDisponibles();
        if (premios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay premios activos.", "Voucher", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] nombresPremios = premios.stream().map(Premio::getNombre).toArray(String[]::new);
        int sel = JOptionPane.showOptionDialog(this, "¿Para qué premio desea obtener su Voucher?", 
                "Obtener Voucher", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, nombresPremios, nombresPremios[0]);
        if (sel >= 0) {
            Premio p = premios.get(sel);
            String voucher = premioController.obtenerVoucher(invitado.getId(), p.getId()); // o obtenerVoucher()
            if (voucher != null && !voucher.isEmpty()) {
                JOptionPane.showMessageDialog(this, "¡Felicidades!\n\nVoucher: " + voucher + "\nPreséntelo en recepción.", "Ganador", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No es ganador o el sorteo no se realizó.", "Resultado", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // Método utilitario para inyectar JTables estilizadas en el panel dinámico
    private void mostrarTablaEnPanel(String[] columnas, String[][] datos, Runnable accionVolver) {
        JPanel pnlContenedorTabla = new JPanel(new BorderLayout(0, 10));
        pnlContenedorTabla.setBorder(new EmptyBorder(10, 10, 10, 10));

        javax.swing.JTable tabla = new javax.swing.JTable(datos, columnas);
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(25);
        tabla.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        tabla.setDefaultEditor(Object.class, null); 

        JScrollPane scrollPane = new JScrollPane(tabla);
        pnlContenedorTabla.add(scrollPane, BorderLayout.CENTER);

        JButton btnVolver = new JButton("VOLVER");
        btnVolver.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnVolver.addActionListener(e -> accionVolver.run());
        pnlContenedorTabla.add(btnVolver, BorderLayout.SOUTH);

        cambiarPanelDinamico(pnlContenedorTabla);
    }
}