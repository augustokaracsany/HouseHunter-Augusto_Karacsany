package BLL;

import DLL.ActividadController;
import java.sql.PreparedStatement;
import DLL.EventoController;
import DLL.InvitadoController;
import DLL.ReporteController;
import DLL.PremioController; 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
public class EmpresaService {
    // Instancias únicas de la capa de datos (DLL)
    private EventoController eventoController = EventoController.getInstance();
    private InvitadoController invitadoController = InvitadoController.getInstance();
    private ReporteController reporteController = new ReporteController(); 
    private ActividadController actividadController = ActividadController.getInstance(); 
    private PremioController premioController = PremioController.getInstance(); 
    
    private Empresa empresa;

    public EmpresaService(Empresa empresa) {
        this.empresa = empresa;
    }

    //
     // ( Módulo: Gestión de Eventos. )
     // Verifica disponibilidad y crea la reserva en la base de datos usando el CUIT.
     //
    public boolean procesarReserva(LocalDate inicio, LocalDate fin, int invitados) throws SQLException {
        if (eventoController.verificarDisponibilidad(inicio, fin)) {
            Reserva nueva = new Reserva(empresa, inicio, fin, invitados);
            eventoController.crearReserva(nueva, empresa.getCuit());
            empresa.setReservaActual(nueva); // Setea en memoria la activa
            return true;
        }
        return false;
    }

    //
     // ( Módulo: Gestión de Eventos. / Selección de reservas. )
     // Recupera las reservas filtradas por el ID único de la empresa ( Usa empresa.getId() heredado de Persona. ).
     //
    public List<Reserva> obtenerReservas() {
        return eventoController.listarReservasPorEmpresa(empresa.getId());
    }

    //
     // ( Módulo: Gestión de Eventos. ) 
     // Lista de plantillas de diseño de invitaciones disponibles.
     //
    public List<Plantilla> obtenerPlantillas() {
        return eventoController.listarPlantillas();
    }

    //
     // ( Módulo: Gestión de Eventos. )
     // Vincula la plantilla seleccionada a la reserva activa actual.
     //
    public boolean asignarPlantilla(int plantillaId) {
        return eventoController.asignarPlantilla(empresa.getReservaActual().getId(), plantillaId);
    }

    //
     // ( Módulo: Gestión de Eventos. )
     // Soluciona el error de compilación. Actualiza el código único personalizado en la BD.
     //
    public boolean actualizarCodigoEvento(int reservaId, String nuevoCodigo) {
        return eventoController.actualizarCodigoUnicoEvento(reservaId, nuevoCodigo);
    }

    //
     // Módulo: ( Cronograma. / Planificación. )
     // Inserta una actividad específica mapeando directamente a la tabla relacional.
     //
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

    ///
     // ( Módulo: Cronograma. / Planificación. ) - 1
     // Obtiene el listado de actividades asociadas al evento en ejecución.
     //
    public List<Actividad> obtenerActividades() {
        return eventoController.obtenerActividadesPorReserva(empresa.getReservaActual().getId());
    }

    //
     // ( Módulo: Cronograma. / Planificación. ) - 2
     // Actualiza estados o importancias dentro del listado del cronograma.
     //
    public boolean actualizarCronograma(List<Actividad> actividades) {
        return eventoController.guardarCronograma(empresa.getReservaActual().getId(), actividades);
    }

    //
     // ( Módulo: Cronograma. / Planificación. ) - 3
     // Validación del guardado/confirmación final del cronograma.
     //
    public boolean guardarCronogramaEfectivo(List<Actividad> actividades) {
        if (actividades.isEmpty()) {
            return false;
        }
        return eventoController.guardarCronograma(empresa.getReservaActual().getId(), actividades);
    }

    //
     // ( Módulo: Invitaciones. )
     // Trae todos los destinatarios asignados a la reserva actual.
     //
    public List<Invitado> obtenerInvitados() {
        return invitadoController.listarInvitadosPorReserva(empresa.getReservaActual().getId());
    }

    //
     // ( Módulo: Gestión de Eventos. )
     // Guarda la lista de invitados procesada masivamente en la base de datos.
     //
    public boolean guardarInvitados(List<Invitado> lista) {
        return invitadoController.cargarInvitados(empresa.getReservaActual().getId(), lista);
    }

    //
     // ( Módulo: Gestión de Eventos. )
     // Valida la estructura lógica de los datos de un invitado ( DNI, mail, etc ).
     //
    public boolean validarInvitado(Invitado inv) {
        return invitadoController.validarDatosInvitado(inv);
    }
    //
     // Guarda un nuevo premio o sorteo asociado a la reserva activa de la empresa.
     //
    public boolean guardarPremio(String nombrePremio, String descripcion, int cantidad) {
        if (empresa.getReservaActual() == null) {
            System.err.println("Error: No hay una reserva activa seleccionada para esta empresa.");
            return false;
        }
        
        // Usamos el premioController que ya tenías declarado arriba en los atributos.
        return premioController.guardarPremio(
            empresa.getReservaActual().getId(), 
            nombrePremio, 
            descripcion, 
            cantidad
        );
    }
 // Dentro de BLL.EmpresaService.java.

 

    public List<String> obtenerPremios() {
        List<String> listaPremios = new ArrayList<>();
        
        // 1. Obtenemos el ID de la reserva activa desde tu objeto de sesión/contexto.
        int idReservaActiva = empresa.getReservaActual().getId();
        
        // 2. Consulta SQL apuntando a la tabla física real del dump.
        String sql = "SELECT nombre_premio, cantidad_disponible FROM premios " +
                     "WHERE id_reserva = ? AND activo = 1";
                     
        // 3. Conexión y ejecución segura con PreparedStatement.
        Connection con = DLL.ConexionController.getInstance().getConnection();
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReservaActiva);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nombre = rs.getString("nombre_premio");
                    int stock = rs.getInt("cantidad_disponible");
                    
                    // Formateamos el String replicando exactamente tu lógica visual previa.
                    listaPremios.add("• " + nombre + " - Stock: " + stock);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los premios desde la BD: " + e.getMessage());
            e.printStackTrace();
        }
        
        return listaPremios;
    }
    // ( Premios. )
     // Devuelve los premios que pertenecen únicamente al evento de esta empresa.
     //
    public List<Premio> obtenerPremiosPorEvento() {
        if (empresa.getReservaActual() == null) {
            return java.util.Collections.emptyList();
        }
        return premioController.listarPremiosPorReserva(empresa.getReservaActual().getId());
    }

    //
    // ( Módulo: Invitaciones. )
     // Envía de forma simulada las notificaciones disparando los tokens correspondientes.
     //
    public boolean enviarNotificaciones() {
        List<Invitado> invitadosAEnviar = obtenerInvitados();
        if (invitadosAEnviar.isEmpty()) {
            return false;
        }
        return invitadoController.enviarNotificaciones(invitadosAEnviar);
    }

    //
     // ( Módulo: Reportes. )
     // Extrae el mapa asociativo con los contadores de asistencia, confirmaciones y actividades.
    //
    public Map<String, Object> obtenerEstadisticas() {
        return reporteController.obtenerReporteEvento(empresa.getReservaActual().getId());
    }
    
    //
     // Acceso al controlador de premios por si necesitás listarlos en el cronograma.
     //
    public PremioController getPremioController() {
        return this.premioController;
    }
    //
     // Obtiene la lista de invitados postulados y elegibles para un premio específico.
     // basado en el nombre del premio y la reserva activa actual.
     //
    public List<Invitado> obtenerPostuladosPorPremio(int idPremio) {
        List<Invitado> participantes = new ArrayList<>();
        int idReservaActiva = empresa.getReservaActual().getId();

        // Filtramos directamente usando el ID único del premio ( p.id = ? ).
        String sql = "SELECT u.id, dp.nombre, dp.apellido, dp.dni, u.email " +
                     "FROM participaciones_premios pp " +
                     "JOIN usuarios u ON pp.id_invitado = u.id " +
                     "JOIN datos_personas dp ON u.id = dp.id_usuario " +
                     "JOIN premios p ON pp.id_premio = p.id " +
                     "WHERE p.id = ? AND p.id_reserva = ? AND pp.elegible = 1 AND pp.ganador = 0";

        // REFACTOR CRÍTICO: Incluimos la 'con' dentro del try para que se cierre sola y limpie el hilo de MySQL.
        try (Connection con = DLL.ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPremio);
            ps.setInt(2, idReservaActiva);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Invitado inv = new Invitado(
                        rs.getString("email"), 
                        "xxxxxx", 
                        rs.getString("nombre"), 
                        Rol.INVITADO
                    );
                    inv.setId(rs.getInt("id")); // Seteamos el id_usuario_real.
                    inv.setApellido(rs.getString("apellido"));
                    inv.setDni(rs.getString("dni"));
                    
                    participantes.add(inv);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error crítico en obtenerPostuladosPorPremio: " + e.getMessage());
            e.printStackTrace();
        }

        return participantes;
    }

    //
     // Registra al ganador del sorteo: descuenta stock, setea el ganador en la tabla 'premios',
    // y actualiza su estado en 'participaciones_premios' generando un código de voucher.
     //
    public boolean registrarGanadorSorteo(int idUsuarioReal, int idPremio) {
        // 1. Query para marcar al ganador y restarle 1 al stock del premio de forma atómica.
        String sqlGanador = "UPDATE participaciones_premios SET ganador = 1, voucher = ? WHERE id_invitado = ? AND id_premio = ?";
        String sqlStock = "UPDATE premios SET cantidad_disponible = cantidad_disponible - 1 WHERE id = ? AND cantidad_disponible > 0";
        
        // Generamos un código de voucher único ( Como por ejemplo: VOU-ID_USUARIO-RANDOM ).
        String codigoVoucher = "VOU-" + idUsuarioReal + "-" + (100 + (int)(Math.random() * 900));

        Connection con = DLL.ConexionController.getInstance().getConnection();
        
        try {
            con.setAutoCommit(false); // Iniciamos transacción para que si falla un paso, no se rompa la BD.
            
            // Paso A: Registrar el ganador en las participaciones.
            try (PreparedStatement ps1 = con.prepareStatement(sqlGanador)) {
                ps1.setString(1, codigoVoucher);
                ps1.setInt(2, idUsuarioReal);
                ps1.setInt(3, idPremio);
                ps1.executeUpdate();
            }
            
            // Paso B: Descontar el stock del premio.
            int filasStock = 0;
            try (PreparedStatement ps2 = con.prepareStatement(sqlStock)) {
                ps2.setInt(1, idPremio);
                filasStock = ps2.executeUpdate();
            }
            
            // Si no había stock suficiente, hacemos rollback y cancelamos.
            if (filasStock == 0) {
                con.rollback();
                System.err.println("Sorteo cancelado: No queda stock disponible para el premio ID: " + idPremio);
                return false;
            }
            
            con.commit(); // Todo salió impecable, guardamos los cambios.
            return true;
            
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("Error crítico en registrarGanadorSorteo: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { con.setAutoCommit(true); con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    public List<BLL.Premio> listarPremiosObjetosDisponibles() {
        // LLamamos al método del controlador de premios usando su Singleton.
        return DLL.PremioController.getInstance().listarPremiosDisponibles();
    }
}