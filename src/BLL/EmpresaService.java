package BLL;

import DLL.ActividadController;
import DLL.EventoController;
import DLL.InvitadoController;
import DLL.ReporteController;
import DLL.PremioController; // 🟢 Recuperado: Controlador de premios faltante

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class EmpresaService {
    // Instancias únicas de la capa de datos (DLL)
    private EventoController eventoController = EventoController.getInstance();
    private InvitadoController invitadoController = InvitadoController.getInstance();
    private ReporteController reporteController = new ReporteController(); 
    private ActividadController actividadController = ActividadController.getInstance(); 
    private PremioController premioController = PremioController.getInstance(); // 🟢 Recuperado
    
    private Empresa empresa;

    public EmpresaService(Empresa empresa) {
        this.empresa = empresa;
    }

    /**
     * Módulo: Gestión de Eventos
     * Verifica disponibilidad y crea la reserva en la base de datos usando el CUIT.
     */
    public boolean procesarReserva(LocalDate inicio, LocalDate fin, int invitados) throws SQLException {
        if (eventoController.verificarDisponibilidad(inicio, fin)) {
            Reserva nueva = new Reserva(empresa, inicio, fin, invitados);
            eventoController.crearReserva(nueva, empresa.getCuit());
            empresa.setReservaActual(nueva); // Setea en memoria la activa
            return true;
        }
        return false;
    }

    /**
     * Módulo: Gestión de Eventos / Selección de reservas
     * Recupera las reservas filtradas por el ID único de la empresa (Usa empresa.getId() heredado de Persona).
     */
    public List<Reserva> obtenerReservas() {
        return eventoController.listarReservasPorEmpresa(empresa.getId());
    }

    /**
     * Módulo: Gestión de Eventos
     * Lista de plantillas de diseño de invitaciones disponibles.
     */
    public List<Plantilla> obtenerPlantillas() {
        return eventoController.listarPlantillas();
    }

    /**
     * Módulo: Gestión de Eventos
     * Vincula la plantilla seleccionada a la reserva activa actual.
     */
    public boolean asignarPlantilla(int plantillaId) {
        return eventoController.asignarPlantilla(empresa.getReservaActual().getId(), plantillaId);
    }

    /**
     * Módulo: Cronograma / Planificación
     * Inserta una actividad específica mapeando directamente a la tabla relacional.
     */
    public boolean guardarActividad(String nombre, String desc, String importancia, String categoria, String hora) {
        return actividadController.guardarActividad(
            empresa.getReservaActual().getId(), 
            nombre, 
            desc, 
            importancia, 
            categoria, 
            hora
        );
    }

    /**
     * Módulo: Cronograma / Planificación
     * Obtiene el listado de actividades asociadas al evento en ejecución.
     */
    public List<Actividad> obtenerActividades() {
        return eventoController.obtenerActividadesPorReserva(empresa.getReservaActual().getId());
    }

    /**
     * Módulo: Cronograma / Planificación
     * Actualiza estados o importancias dentro del listado del cronograma.
     */
    public boolean actualizarCronograma(List<Actividad> actividades) {
        return eventoController.guardarCronograma(empresa.getReservaActual().getId(), actividades);
    }

    /**
     * Módulo: Cronograma / Planificación
     * 🟢 Recuperado: Validación del guardado/confirmación final del cronograma.
     */
    public boolean guardarCronogramaEfectivo(List<Actividad> actividades) {
        if (actividades.isEmpty()) {
            return false;
        }
        return eventoController.guardarCronograma(empresa.getReservaActual().getId(), actividades);
    }

    /**
     * Módulo: Invitaciones
     * Trae todos los destinatarios asignados a la reserva actual.
     */
    public List<Invitado> obtenerInvitados() {
        return invitadoController.listarInvitadosPorReserva(empresa.getReservaActual().getId());
    }

    /**
     * Módulo: Gestión de Eventos
     * Guarda la lista de invitados procesada masivamente en la base de datos.
     */
    public boolean guardarInvitados(List<Invitado> lista) {
        return invitadoController.cargarInvitados(empresa.getReservaActual().getId(), lista);
    }

    /**
     * Módulo: Gestión de Eventos
     * Valida la estructura lógica de los datos de un invitado (DNI, mail, etc).
     */
    public boolean validarInvitado(Invitado inv) {
        return invitadoController.validarDatosInvitado(inv);
    }

    /**
     * Módulo: Invitaciones
     * Envía de forma simulada las notificaciones disparando los tokens correspondientes.
     */
    public boolean enviarNotificaciones() {
        List<Invitado> invitadosAEnviar = obtenerInvitados();
        if (invitadosAEnviar.isEmpty()) {
            return false;
        }
        return invitadoController.enviarNotificaciones(invitadosAEnviar);
    }

    /**
     * Módulo: Reportes
     * Extrae el mapa asociativo con los contadores de asistencia, confirmaciones y actividades.
     */
    public Map<String, Object> obtenerEstadisticas() {
        return reporteController.obtenerReporteEvento(empresa.getReservaActual().getId());
    }
    
    /**
     * 🟢 Adicional de Infraestructura / Extensibilidad futura
     * Acceso al controlador de premios por si necesitás listarlos en el cronograma.
     */
    public PremioController getPremioController() {
        return this.premioController;
    }
}