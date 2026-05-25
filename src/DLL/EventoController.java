package DLL; // Capa de Acceso a Datos / Controladores de Infraestructura. < DLL.
	
import BLL.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventoController {
    private static EventoController instance; // Instancia única global del controlador.

    // ( Constructor privado. )
    // Bloquea la instanciación con 'new' para respetar el patrón Singleton.
    private EventoController() {}

    // Retorna la instancia única del controlador para coordinar reservas y cronogramas.
    public static EventoController getInstance() {
        if (instance == null) {
            instance = new EventoController();
        }
        return instance;
    }

    // ( Validación de Rango de Fechas )
    // Consulta la base de datos para ver si ya existe una reserva activa que colisione con el rango solicitado.
    public boolean verificarDisponibilidad(LocalDate fechaInicio, LocalDate fechaFin) {
        String sql = "SELECT COUNT(*) FROM reservas_hotel WHERE NOT (fecha_fin < ? OR fecha_inicio > ?)";
        Connection con = ConexionController.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fechaInicio)); // Convierte LocalDate de Java a Date de SQL.
            ps.setDate(2, Date.valueOf(fechaFin));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0; // Si el conteo da 0, el hotel está completamente disponible.
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verificando disponibilidad de hotel: " + e.getMessage());
        }
        return false;
    }

    // ( Alta Física de Evento )
    // Registra una nueva reserva real en la tabla 'reservas_hotel'.
    public Reserva crearReserva(Reserva reserva, String codigoEvento) throws SQLException {
        String sql = "INSERT INTO reservas_hotel (id_empresa, codigo_unico_evento, fecha_inicio, fecha_fin, cantidad_estimada_asistentes, id_plantilla) VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = ConexionController.getInstance().getConnection();
        
        // RETURN_GENERATED_KEYS: Recupera el ID numérico asignado de forma incremental por MySQL.
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reserva.getEmpresa().getId());
            ps.setString(2, codigoEvento);
            ps.setDate(3, Date.valueOf(reserva.getFechaInicio()));
            ps.setDate(4, Date.valueOf(reserva.getFechaFin()));
            ps.setInt(5, reserva.getCantidadEstimadaAsistentes());
            
            if (reserva.getPlantilla() != null) {
                ps.setInt(6, reserva.getPlantilla().getId());
            } else {
                ps.setNull(6, Types.INTEGER); // Seteo de FK nula explícita si arranca sin interfaz gráfica.
            }
            
            int affected = ps.executeUpdate();
            if (affected == 0) throw new SQLException("No se pudo crear la reserva corporativa.");
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reserva.setId(generatedKeys.getInt(1));
                }
            }
        }
        return reserva; 
    }

    // ( Consultas )
    // Recupera todo el historial de contrataciones asociadas a una empresa específica desde la tabla real.
    public List<Reserva> listarReservasPorEmpresa(int idEmpresa) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT r.*, p.nombre as plantilla_nombre FROM reservas_hotel r " +
                     "LEFT JOIN plantillas p ON r.id_plantilla = p.id " +
                     "WHERE r.id_empresa = ? ORDER BY r.fecha_inicio DESC";
                     
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpresa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reserva r = new Reserva();
                    r.setId(rs.getInt("id"));
                    r.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                    r.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                    r.setCantidadEstimadaAsistentes(rs.getInt("cantidad_estimada_asistentes"));
                    r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    
                    int idPlantilla = rs.getInt("id_plantilla");
                    if (!rs.wasNull()) {
                        Plantilla p = new Plantilla();
                        p.setId(idPlantilla);
                        p.setNombre(rs.getString("plantilla_nombre"));
                        r.setPlantilla(p);
                    }
                    reservas.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar historial de reservas: " + e.getMessage());
        }
        return reservas;
    }

    // ( Transacciones Atómicas por Lote )
    // Modifica y guarda el itinerario completo asegurando la consistencia e integridad de los datos.
    public boolean guardarCronograma(int idReserva, List<Actividad> actividades) {
        String deleteSql = "DELETE FROM actividades WHERE id_reserva = ?";
        String insertSql = "INSERT INTO actividades (id_reserva, nombre, descripcion, importancia, categoria, hora_actividad) VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = ConexionController.getInstance().getConnection();
        try {
            con.setAutoCommit(false); // Apertura de transacción manual para seguridad.
            
            // Paso 1: Limpieza preventiva para evitar registros duplicados.
            try (PreparedStatement psDel = con.prepareStatement(deleteSql)) {
                psDel.setInt(1, idReserva);
                psDel.executeUpdate();
            }
            
            // Paso 2: Ejecución masiva optimizada por lotes (Batch processing).
            try (PreparedStatement psIns = con.prepareStatement(insertSql)) {
                for (Actividad act : actividades) {
                    psIns.setInt(1, idReserva);
                    psIns.setString(2, act.getNombre());
                    psIns.setString(3, act.getDescripcion());
                    psIns.setString(4, act.getImportancia().toString()); // Inyecta el String del Enum.
                    psIns.setString(5, act.getCategoria());
                    
                    // Extrae la hora exacta (Time) para guardar correctamente según tu ENUM/Estructura SQL.
                    Time horaSql = Time.valueOf(act.getFechaHora().toLocalTime());
                    psIns.setTime(6, horaSql);
                    
                    psIns.addBatch(); 
                }
                psIns.executeBatch(); 
            }
            con.commit(); // Consolidación física de la información en disco.
            return true;
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } 
            System.err.println("Error en transacción de guardado de cronograma: " + e.getMessage());
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); } 
        }
    }

    // Trae de forma ordenada las actividades vinculadas a una reserva corporativa específica.
    public List<Actividad> obtenerActividadesPorReserva(int idReserva) {
        List<Actividad> lista = new ArrayList<>();
        String sql = "SELECT * FROM actividades WHERE id_reserva = ? ORDER BY hora_actividad";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Actividad a = new Actividad();
                    a.setId(rs.getInt("id"));
                    a.setNombre(rs.getString("nombre"));
                    a.setDescripcion(rs.getString("descripcion"));
                    
                    // Reconstrucción del campo temporal mapeando la hora de la BD sobre el día de hoy.
                    Time hora = rs.getTime("hora_actividad");
                    a.setFechaHora(LocalDate.now().atTime(hora.toLocalTime()));
                    
                    a.setImportancia(Importancia.valueOf(rs.getString("importancia").toUpperCase())); 
                    a.setCategoria(rs.getString("categoria"));
                    lista.add(a);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error consultando cronograma: " + e.getMessage());
        }
        return lista;
    }

    // Modifica visualmente el estilo del evento asignando la FK de una plantilla.
    public boolean asignarPlantilla(int idReserva, int idPlantilla) {
        String sql = "UPDATE reservas_hotel SET id_plantilla = ? WHERE id = ?";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPlantilla);
            ps.setInt(2, idReserva);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en asignación visual de plantilla: " + e.getMessage());
            return false;
        }
    }

    // Levanta todos los diseños visuales activos de la base de datos.
    public List<Plantilla> listarPlantillas() {
        List<Plantilla> plantas = new ArrayList<>();
        String sql = "SELECT * FROM plantillas WHERE activa = 1";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Plantilla p = new Plantilla();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setUrlImagen(rs.getString("url_imagen"));
                p.setActiva(rs.getBoolean("activa"));
                plantas.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar plantillas base: " + e.getMessage());
        }
        return plantas;
    }

    // Cruza los datos para asegurar que el DNI figure en la nómina precargada para ese token de evento.
    public boolean validarInvitadoPrevia(String codigoEvento, String dni) {
        String sql = "SELECT lip.* FROM lista_invitados_previa lip " +
                     "JOIN reservas_hotel rh ON lip.id_reserva = rh.id " +
                     "WHERE rh.codigo_unico_evento = ? AND lip.dni = ?";
        Connection con = ConexionController.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoEvento);
            ps.setString(2, dni);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); 
            }
        } catch (SQLException e) {
            System.err.println("Error en validación cruzada de credenciales: " + e.getMessage());
            return false;
        }
    }

    // Compila subconsultas SQL complejas y formatea un string HTML nativo para inyectar directo en un JOptionPane.
    public String obtenerReporteConsolidadoEvento(String codigoEvento) {
        StringBuilder reporte = new StringBuilder();
        String sql = "SELECT rh.id, " +
                     "  (SELECT COUNT(*) FROM lista_invitados_previa lip WHERE lip.id_reserva = rh.id) as total_invitados, " +
                     "  (SELECT COUNT(DISTINCT ah.id_usuario) FROM asignaciones_habitaciones ah WHERE ah.id_reserva = rh.id) as total_checkins, " +
                     "  (SELECT COUNT(*) FROM actividades act " +
                     "   JOIN asistencias_actividades aa ON aa.id_actividad = act.id " +
                     "   WHERE act.id_reserva = rh.id AND aa.asistio = 'S') as total_asistencias " +
                     "FROM reservas_hotel rh " +
                     "WHERE rh.codigo_unico_evento = ?";
        Connection con = ConexionController.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoEvento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int totalInvitados = rs.getInt("total_invitados");
                    int totalCheckins = rs.getInt("total_checkins");
                    int totalAsistencias = rs.getInt("total_asistencias");
                    int porcentajeOcupacion = totalInvitados > 0 ? (totalCheckins * 100 / totalInvitados) : 0;

                    // Armado del maquetado HTML embed para Swing.
                    reporte.append("<html><body style='width: 300px;'>");
                    reporte.append("<h2 style='text-align: center; color: #2c3e50;'>📊 Reporte Consolidado</h2>");
                    reporte.append("<p style='text-align: center; margin-top:0;'><b>Evento:</b> ").append(codigoEvento).append("</p><hr>");
                    reporte.append("<table style='width: 100%; border-collapse: collapse;'>");
                    reporte.append("<tr><td><b>📋 Invitados en Lista:</b></td><td style='text-align: right;'>").append(totalInvitados).append("</td></tr>");
                    reporte.append("<tr><td><b>🏨 Check-ins Exitosos:</b></td><td style='text-align: right;'>").append(totalCheckins).append("</td></tr>");
                    reporte.append("<tr><td><b>📉 Porcentaje Ocupación:</b></td><td style='text-align: right; color: green;'><b>").append(porcentajeOcupacion).append("%</b></td></tr>");
                    reporte.append("<tr><td colspan='2'><hr style='border-top: 1px dashed #ccc;'></td></tr>");
                    reporte.append("<tr><td><b>🎮 Asistencias Totales:</b></td><td style='text-align: right; color: #2980b9;'><b>").append(totalAsistencias).append("</b></td></tr>");
                    reporte.append("</table></body></html>");
                    return reporte.toString();
                }
                return "<html><body>❌ Código de evento inválido.</body></html>";
            }
        } catch (SQLException e) {
            return "<html><body>❌ Error técnico al compilar reportes: " + e.getMessage() + "</body></html>";
        }
    }
 // Recupera una reserva específica mediante el ID de usuario de la empresa y su código único, cargando sus actividades
    public Reserva obtenerReservaActivaPorCodigo(int idUsuarioEmpresa, String codigoEvento) {
        String sqlReserva = "SELECT * FROM reservas_hotel WHERE id_empresa = ? AND codigo_unico_evento = ?";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sqlReserva)) {
            
            ps.setInt(1, idUsuarioEmpresa);
            ps.setString(2, codigoEvento);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Reserva r = new Reserva();
                    r.setId(rs.getInt("id"));
                    r.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                    r.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                    r.setCantidadEstimadaAsistentes(rs.getInt("cantidad_estimada_asistentes"));
                    r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    
                    // Inyectamos las actividades vinculadas automáticamente usando tu método existente
                    r.setActividades(obtenerActividadesPorReserva(r.getId()));
                    return r;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al recuperar la reserva activa por código: " + e.getMessage());
        }
        return null;
    }
}