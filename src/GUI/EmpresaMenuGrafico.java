package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import BLL.Actividad;
import BLL.CategoriaActividad;
import BLL.Empresa;
import BLL.EmpresaService;
import BLL.Importancia;
import BLL.Invitado;
import BLL.Plantilla;
import BLL.Reserva;
import BLL.Rol;

public class EmpresaMenuGrafico extends JFrame {

    private static final long serialVersionUID = 1L;
    
    // Atributos de lógica corporativa
    private Empresa empresa;
    private EmpresaService service;

    // Componentes gráficos principales (Atributos de clase para WindowBuilder)
    private JPanel contentPane;
    private JPanel pnlDinamico;
    private JLabel lblEntidad;
    private JLabel lblReservaActiva;
    private JLabel lblCodigoEvento;

    /**
     * Constructor correcto que recibe la empresa logueada desde el Main.
     */
    public EmpresaMenuGrafico(Empresa empresa) {
        this.empresa = empresa;
        this.service = new EmpresaService(empresa);
        
        // Inicializamos los componentes de la interfaz gráfica
        initialize();
        
        // Sincronizamos los datos de la reserva apenas abre la ventana
        actualizarInfoReserva();
    }

    /**
     * Inicializa los componentes de la ventana.
     */
    private void initialize() {
        setTitle("HOUSEHUNTER - PANEL DE EMPRESA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 550);
        setLocationRelativeTo(null); // Centrar pantalla
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(10, 0));

        // Pánel Lateral (Menú de Navegación)
        JPanel pnlLateral = new JPanel();
        pnlLateral.setBackground(SystemColor.controlDkShadow);
        pnlLateral.setBorder(new LineBorder(new Color(0, 0, 0)));
        contentPane.add(pnlLateral, BorderLayout.WEST);
        pnlLateral.setLayout(new GridLayout(7, 1, 0, 10)); // 7 filas espaciadas

        JLabel lblTituloMenu = new JLabel("PANEL DE CONTROL");
        lblTituloMenu.setForeground(Color.WHITE);
        lblTituloMenu.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblTituloMenu.setHorizontalAlignment(SwingConstants.CENTER);
        pnlLateral.add(lblTituloMenu);

        JButton btnGestionEvento = new JButton("GESTIÓN DE EVENTO");
        btnGestionEvento.addActionListener(e -> mostrarSubMenuGestion());
        pnlLateral.add(btnGestionEvento);

        JButton btnPlanificacion = new JButton("PLANIFICACIÓN");
        btnPlanificacion.addActionListener(e -> mostrarSubMenuCronograma());
        pnlLateral.add(btnPlanificacion);

        JButton btnInvitaciones = new JButton("INVITACIONES");
        btnInvitaciones.addActionListener(e -> mostrarSubMenuInvitaciones());
        pnlLateral.add(btnInvitaciones);

        JButton btnReportes = new JButton("REPORTES");
        btnReportes.addActionListener(e -> mostrarSubMenuReportes());
        pnlLateral.add(btnReportes);

        JButton btnCambiarReserva = new JButton("CAMBIAR DE RESERVA");
        btnCambiarReserva.addActionListener(e -> cambiarReservaManual());
        pnlLateral.add(btnCambiarReserva);

        JButton btnCerrarSesion = new JButton("CERRAR SESIÓN");
        btnCerrarSesion.setForeground(Color.RED);
        btnCerrarSesion.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Cerrando sesión de " + empresa.getNombre());
            dispose();
        });
        pnlLateral.add(btnCerrarSesion);

        // Pánel Central
        JPanel pnlCentro = new JPanel();
        contentPane.add(pnlCentro, BorderLayout.CENTER);
        pnlCentro.setLayout(new BorderLayout(0, 10));

        // Cabecera de estado continuo
        JPanel pnlCabeceraInfo = new JPanel();
        pnlCabeceraInfo.setBackground(new Color(245, 245, 245));
        pnlCabeceraInfo.setBorder(new LineBorder(Color.LIGHT_GRAY));
        pnlCentro.add(pnlCabeceraInfo, BorderLayout.NORTH);
        pnlCabeceraInfo.setLayout(new GridLayout(1, 3, 10, 0));

        lblEntidad = new JLabel("Entidad: " + empresa.getNombre());
        lblEntidad.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblEntidad.setHorizontalAlignment(SwingConstants.CENTER);
        pnlCabeceraInfo.add(lblEntidad);

        lblReservaActiva = new JLabel("Sin reserva activa");
        lblReservaActiva.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblReservaActiva.setHorizontalAlignment(SwingConstants.CENTER);
        pnlCabeceraInfo.add(lblReservaActiva);

        lblCodigoEvento = new JLabel("Código Evento: -");
        lblCodigoEvento.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblCodigoEvento.setHorizontalAlignment(SwingConstants.CENTER);
        pnlCabeceraInfo.add(lblCodigoEvento);

        // Espacio Dinámico: Cambia según el módulo elegido
        pnlDinamico = new JPanel();
        pnlDinamico.setBorder(new LineBorder(Color.GRAY));
        pnlCentro.add(pnlDinamico, BorderLayout.CENTER);
        pnlDinamico.setLayout(new BorderLayout(0, 0));
        
        JLabel lblVistaPrevia = new JLabel("Seleccione un módulo del menú lateral para comenzar.");
        lblVistaPrevia.setHorizontalAlignment(SwingConstants.CENTER);
        pnlDinamico.add(lblVistaPrevia, BorderLayout.CENTER);
    }
    // Métodos de Control y Lógica de Paneles Dinámicos

    private void actualizarInfoReserva() {
        if (empresa.getReservaActual() != null) {
            Reserva r = empresa.getReservaActual();
            String cod = r.getCodigoUnicoEvento();
            String codTexto = (cod != null && !cod.trim().isEmpty()) ? cod : "Sin asignar";
            
            lblReservaActiva.setText("Reserva Activa: ID " + r.getId());
            lblReservaActiva.setForeground(new Color(0, 128, 0));
            lblCodigoEvento.setText("Código Evento: " + codTexto);
            lblCodigoEvento.setForeground(Color.BLUE);
        } else {
            lblReservaActiva.setText("Sin reserva activa seleccionada");
            lblReservaActiva.setForeground(Color.RED);
            lblCodigoEvento.setText("Código Evento: -");
            lblCodigoEvento.setForeground(Color.BLACK);
        }
    }

    private void cambiarPanelDinamico(JPanel nuevoContenido) {
        pnlDinamico.removeAll();
        pnlDinamico.add(nuevoContenido, BorderLayout.CENTER);
        pnlDinamico.revalidate();
        pnlDinamico.repaint();
    }

// Sub-Menús Visuales en Panel Central

    private void mostrarSubMenuGestion() {
        JPanel pnlGestion = new JPanel(new GridLayout(5, 1, 10, 10));
        pnlGestion.setBorder(new EmptyBorder(20, 50, 20, 50));

        JButton btnReserva = new JButton("REALIZAR RESERVA");
        btnReserva.addActionListener(e -> realizarReserva());
        
        JButton btnCargarInv = new JButton("CARGAR INVITADOS MASIVO");
        btnCargarInv.addActionListener(e -> cargarInvitadosMasivo());

        JButton btnPlantilla = new JButton("SELECCIONAR PLANTILLA");
        btnPlantilla.addActionListener(e -> seleccionarPlantilla());

        JButton btnEditarCod = new JButton("EDITAR CÓDIGO ÚNICO");
        btnEditarCod.addActionListener(e -> {
            editarCodigoUnicoEvento();
            actualizarInfoReserva();
        });

        JButton btnPremios = new JButton("GESTIONAR PREMIOS del EVENTO");
        btnPremios.addActionListener(e -> mostrarSubMenuPremios());

        pnlGestion.add(btnReserva);
        pnlGestion.add(btnCargarInv);
        pnlGestion.add(btnPlantilla);
        pnlGestion.add(btnEditarCod);
        pnlGestion.add(btnPremios);

        cambiarPanelDinamico(pnlGestion);
    }

    private void mostrarSubMenuPremios() {
        if (!asegurarReservaSeleccionada()) return;

        JPanel pnlPremios = new JPanel(new GridLayout(3, 1, 10, 10));
        pnlPremios.setBorder(new EmptyBorder(40, 50, 40, 50));

        JButton btnCrear = new JButton("Crear Premio");
        btnCrear.addActionListener(e -> crearPremio());

        JButton btnListar = new JButton("Listar Premios");
        btnListar.addActionListener(e -> listarPremios());

        JButton btnSorteo = new JButton("💥 REALIZAR SORTEO DE PREMIO 💥");
        btnSorteo.addActionListener(e -> ejecutarSorteoDePremio());

        pnlPremios.add(btnCrear);
        pnlPremios.add(btnListar);
        pnlPremios.add(btnSorteo);

        cambiarPanelDinamico(pnlPremios);
    }

    private void mostrarSubMenuCronograma() {
        if (!asegurarReservaSeleccionada()) return;

        JPanel pnlCronograma = new JPanel(new GridLayout(4, 1, 10, 10));
        pnlCronograma.setBorder(new EmptyBorder(30, 50, 30, 50));

        JButton btnCrearAct = new JButton("Crear Actividad");
        btnCrearAct.addActionListener(e -> crearActividad());

        JButton btnAsignarImp = new JButton("Asignar Importancia");
        btnAsignarImp.addActionListener(e -> asignarImportancia());

        JButton btnGuardar = new JButton("Guardar Cronograma");
        btnGuardar.addActionListener(e -> guardarCronograma());

        JButton btnVerAct = new JButton("Ver Detalle de Actividades");
        btnVerAct.addActionListener(e -> verDetalleActividadesEmpresa());

        pnlCronograma.add(btnCrearAct);
        pnlCronograma.add(btnAsignarImp);
        pnlCronograma.add(btnGuardar);
        pnlCronograma.add(btnVerAct);

        cambiarPanelDinamico(pnlCronograma);
    }

    private void mostrarSubMenuInvitaciones() {
        if (!asegurarReservaSeleccionada()) return;

        JPanel pnlInvitaciones = new JPanel(new GridLayout(3, 1, 10, 10));
        pnlInvitaciones.setBorder(new EmptyBorder(40, 50, 40, 50));

        JButton btnListar = new JButton("Listar Invitados");
        btnListar.addActionListener(e -> listarInvitados());

        JButton btnTokens = new JButton("Generar Tokens de Acceso");
        btnTokens.addActionListener(e -> {
            if (service.enviarNotificaciones()) {
                JOptionPane.showMessageDialog(this, "Tokens generados exitosamente en la BD.");
            } else {
                JOptionPane.showMessageDialog(this, "Error o sin invitados asignados.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnMail = new JButton("Enviar Notificaciones por Correo");
        btnMail.addActionListener(e -> JOptionPane.showMessageDialog(this, "Notificaciones enviadas por correo (Simulado)."));

        pnlInvitaciones.add(btnListar);
        pnlInvitaciones.add(btnTokens);
        pnlInvitaciones.add(btnMail);

        cambiarPanelDinamico(pnlInvitaciones);
    }

    private void mostrarSubMenuReportes() {
        if (!asegurarReservaSeleccionada()) return;

        JPanel pnlReportes = new JPanel(new GridLayout(2, 1, 10, 10));
        pnlReportes.setBorder(new EmptyBorder(50, 50, 50, 50));

        JButton btnStats = new JButton("Ver Estadísticas del Evento");
        btnStats.addActionListener(e -> mostrarEstadisticas());

        JButton btnExportar = new JButton("Exportar Reporte Corporativo");
        btnExportar.addActionListener(e -> JOptionPane.showMessageDialog(this, "Exportación simula salida .txt"));

        pnlReportes.add(btnStats);
        pnlReportes.add(btnExportar);

        cambiarPanelDinamico(pnlReportes);
    }
    
    private void cambiarReservaManual() {
        empresa.setReservaActual(null); 
        if (asegurarReservaSeleccionada()) {
            JOptionPane.showMessageDialog(this, "Cambiaste con éxito de reserva.");
        }
    }

    private boolean asegurarReservaSeleccionada() {
        if (empresa.getReservaActual() != null) {
            actualizarInfoReserva();
            return true;
        }
        
        List<Reserva> reservas = service.obtenerReservas();
        if (reservas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay reservas para esta empresa.\nPrimero debe crear una.");
            actualizarInfoReserva();
            return false;
        }
        
        String[] opciones = reservas.stream()
            .map(r -> "ID " + r.getId() + " - [" + r.getFechaInicio() + " al " + r.getFechaFin() + "]")
            .toArray(String[]::new);
            
        int sel = JOptionPane.showOptionDialog(this, "Seleccione la reserva a gestionar:", "Reservas",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (sel >= 0) {
            empresa.setReservaActual(reservas.get(sel));
            actualizarInfoReserva();
            return true;
        }
        actualizarInfoReserva();
        return false;
    }

    // Métodos Operacionales (Llamadas a Capa BLL / Service)
    private void crearPremio() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del premio:", "Nuevo Premio", JOptionPane.QUESTION_MESSAGE);
        if (nombre == null || nombre.trim().isEmpty()) return;
        String desc = JOptionPane.showInputDialog(this, "Descripción:", "Nuevo Premio", JOptionPane.QUESTION_MESSAGE);
        if (desc == null) desc = "";
        String stockStr = JOptionPane.showInputDialog(this, "Stock:", "Nuevo Premio", JOptionPane.QUESTION_MESSAGE);
        if (stockStr == null) return;

        try {
            int stock = Integer.parseInt(stockStr.trim());
            if (stock < 0) return;
            if (service.guardarPremio(nombre.trim(), desc.trim(), stock)) {
                JOptionPane.showMessageDialog(this, "Premio registrado.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarPremios() {
        List<String> premios = service.obtenerPremios();
        if (premios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay premios cargados.");
            return;
        }
        StringBuilder sb = new StringBuilder("Premios:\n");
        premios.forEach(p -> sb.append(p).append("\n"));
        JOptionPane.showMessageDialog(this, sb.toString());
    }

    private void ejecutarSorteoDePremio() {
        List<BLL.Premio> premiosDisponibles = service.listarPremiosObjetosDisponibles();
        if (premiosDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay premios registrados o disponibles para sortear.", "Sorteo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String[] opcionesPremios = premiosDisponibles.stream()
            .map(p -> "ID: " + p.getId() + " - " + p.getNombre()).toArray(String[]::new);

        String seleccion = (String) JOptionPane.showInputDialog(this, "Seleccione qué premio desea sortear en este momento:", "Módulo de Sorteos",
            JOptionPane.QUESTION_MESSAGE, null, opcionesPremios, opcionesPremios[0]);
        if (seleccion == null) return;

        int idPremioSeleccionado = Integer.parseInt(seleccion.split(" - ")[0].replace("ID: ", "").trim());
        BLL.Premio premioElegido = premiosDisponibles.stream().filter(p -> p.getId() == idPremioSeleccionado).findFirst().orElse(null);

        List<Invitado> postulados = service.obtenerPostuladosPorPremio(idPremioSeleccionado);
        if (postulados == null || postulados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay ningún invitado postulado y verificado como elegible para el premio:\n" + premioElegido.getNombre(), "Sin participantes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Collections.shuffle(postulados);
        Invitado ganador = postulados.get(0);

        if (service.registrarGanadorSorteo(ganador.getId(), premioElegido.getId())) {
            String mensajeGanador = String.format(
                "¡¡ TENEMOS UN GANADOR !!\n\n" +
                "Premio sorteado: %s\n" +
                "Ganador: %s\n\n" +
                "El voucher y estado de entrega ya han sido asentados.",
                premioElegido.getNombre(), ganador.getNombre()
            );
            JOptionPane.showMessageDialog(this, mensajeGanador, "SORTEO EXITOSO", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error al procesar el ganador en la BD.", "Error de Consistencia", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarCodigoUnicoEvento() {
        if (!asegurarReservaSeleccionada()) return;
        Reserva r = empresa.getReservaActual();
        String nuevo = JOptionPane.showInputDialog(this, "Ingrese el nuevo código único:");
        if (nuevo == null || nuevo.trim().isEmpty()) return;
        
        if (service.actualizarCodigoEvento(r.getId(), nuevo.trim().toUpperCase())) {
            r.setCodigoUnicoEvento(nuevo.trim().toUpperCase());
            JOptionPane.showMessageDialog(this, "Código actualizado.");
        }
    }

    private void realizarReserva() {
        try {
            String fi = JOptionPane.showInputDialog(this, "Check-In (YYYY-MM-DD):");
            if (fi == null) return;
            String ff = JOptionPane.showInputDialog(this, "Check-Out (YYYY-MM-DD):");
            if (ff == null) return;
            int cant = Integer.parseInt(JOptionPane.showInputDialog(this, "Invitados estimados:"));
            
            if (service.procesarReserva(LocalDate.parse(fi), LocalDate.parse(ff), cant)) {
                JOptionPane.showMessageDialog(this, "Reserva Creada ID: " + empresa.getReservaActual().getId());
                actualizarInfoReserva();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Datos erróneos.");
        }
    }

    private void cargarInvitadosMasivo() {
        String datos = JOptionPane.showInputDialog(this, "Formato: nombre,email,dni,telefono (Separe con ';')");
        if (datos == null || datos.trim().isEmpty()) return;
        
        String[] lineas = datos.split(";");
        List<Invitado> lista = new ArrayList<>();
        for (String l : lineas) {
            String[] c = l.split(",");
            if (c.length < 3) continue;
            Invitado inv = new Invitado(c[1].trim(), "123456", c[0].trim(), Rol.INVITADO);
            inv.setDni(c[2].trim());
            if (service.validarInvitado(inv)) lista.add(inv);
        }
        if (service.guardarInvitados(lista)) JOptionPane.showMessageDialog(this, "Cargados: " + lista.size());
    }

    private void seleccionarPlantilla() {
        List<Plantilla> pts = service.obtenerPlantillas();
        if (pts.isEmpty()) return;
        String[] noms = pts.stream().map(Plantilla::getNombre).toArray(String[]::new);
        int sel = JOptionPane.showOptionDialog(this, "Seleccione plantilla:", "Plantillas",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, noms, noms[0]);
        if (sel >= 0 && service.asignarPlantilla(pts.get(sel).getId())) {
            JOptionPane.showMessageDialog(this, "Plantilla asignada.");
        }
    }

    private void crearActividad() {
        try {
            String nom = JOptionPane.showInputDialog("Nombre:");
            String h = JOptionPane.showInputDialog("Hora (HH:MM):") + ":00";
            String d = JOptionPane.showInputDialog("Descripción:");
            String imp = (String) JOptionPane.showInputDialog(this, "Importancia:", "Imp", 1, null, new String[]{"BAJA","MEDIA","ALTA"}, "MEDIA");
            CategoriaActividad cat = (CategoriaActividad) JOptionPane.showInputDialog(this, "Cat:", "Cat", 1, null, CategoriaActividad.values(), CategoriaActividad.CHARLAS);
            
            if (service.guardarActividad(nom, d, imp, cat.toString(), h)) {
                JOptionPane.showMessageDialog(this, "Actividad guardada.");
            }
        } catch(Exception e) { }
    }

    private void asignarImportancia() {
        List<Actividad> acts = service.obtenerActividades();
        if (acts.isEmpty()) return;
        String[] noms = acts.stream().map(Actividad::getNombre).toArray(String[]::new);
        int sel = JOptionPane.showOptionDialog(this, "Modificar importancia:", "Importancia", 0, 3, null, noms, noms[0]);
        if (sel < 0) return;
        Actividad act = acts.get(sel);
        String nImp = (String) JOptionPane.showInputDialog(this, "Nueva:", "Imp", 3, null, new String[]{"BAJA","MEDIA","ALTA"}, act.getImportancia().toString());
        if (nImp != null) {
            act.setImportancia(Importancia.valueOf(nImp));
            if (service.actualizarCronograma(acts)) JOptionPane.showMessageDialog(this, "Actualizado.");
        }
    }

    private void guardarCronograma() {
        JOptionPane.showMessageDialog(this, "Cronograma sincronizado con la BD.");
    }

    private void verDetalleActividadesEmpresa() {
        List<Actividad> acts = service.obtenerActividades();
        if (acts.isEmpty()) return;
        String[] noms = acts.stream().map(Actividad::getNombre).toArray(String[]::new);
        int idx = JOptionPane.showOptionDialog(this, "Consultar:", "Cronograma", 0, 3, null, noms, noms[0]);
        if (idx >= 0) {
            Actividad a = acts.get(idx);
            JOptionPane.showMessageDialog(this, a.getNombre() + "\n" + a.getDescripcion() + "\nCat: " + a.getCategoria());
        }
    }

    private void listarInvitados() {
        List<Invitado> invs = service.obtenerInvitados();
        if (invs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay invitados cargados.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        invs.forEach(i -> sb.append("- ").append(i.getNombre())
                           .append(" (").append(i.getEmail()).append(")\n"));
        JOptionPane.showMessageDialog(this, sb.toString());
    }

    private void mostrarEstadisticas() {
        Map<String, Object> stats = service.obtenerEstadisticas();
        JOptionPane.showMessageDialog(this, "Total Invitados: " + stats.getOrDefault("totalInvitados", 0));
    }
}