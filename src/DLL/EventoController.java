package DLL; // Capa de Acceso a Datos / Controladores de Infraestructura. < DLL.
	
import BLL.*;
import Repository.Hashing;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // Consulta la base de datos para ver si ya existe una reserva activa en esa misma fecha.
    public boolean verificarDisponibilidad(LocalDate fechaEvento, int numInvitados) {
        // Cuenta cuántas reservas confirmadas o pendientes coinciden con el día solicitado.
        String sql = "SELECT COUNT(*) FROM reservas WHERE fecha_evento = ? AND estado != 'CANCELADA'";
        Connection con = ConexionController.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fechaEvento)); // Convierte LocalDate de Java a Date de SQL.
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0; // Si el conteo da 0, la fecha está libre ( es decir, 'true'. ).
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verificando disponibilidad: " + e.getMessage());
        }
        return true;
    }

    // Registra una nueva reserva física en la tabla de MySQL.
    public Reserva crearReserva(Reserva reserva) throws SQLException {
        String sql = "INSERT INTO reservas (id_empresa, fecha_evento, num_invitados, estado, id_plantilla) VALUES (?, ?, ?, ?, ?)";
        Connection con = ConexionController.getInstance().getConnection();
        // RETURN_GENERATED_KEYS: Le ordena a MySQL que devuelva el ID auto-incremental que acaba de asignar.
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reserva.getEmpresa().getId());
            ps.setDate(2, Date.valueOf(reserva.getFechaEvento()));
            ps.setInt(3, reserva.getNumInvitados());
            ps.setString(4, reserva.getEstado());
            
            // Validación para evitar romper la base de datos si la reserva se crea sin molde.
            if (reserva.getPlantilla() != null)
                ps.setInt(5, reserva.getPlantilla().getId());
            else
                ps.setNull(5, Types.INTEGER); // Inyecta un valor NULL explícito en la columna de FK de MySQL.
            
            int affected = ps.executeUpdate();
            if (affected == 0) throw new SQLException("No se pudo crear la reserva.");
            
            // Recupera el ID numérico asignado por la base de datos y lo inyecta en el objeto Java.
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reserva.setId(generatedKeys.getInt(1));
                }
            }
        }
        return reserva; // Devuelve el objeto Reserva actualizado con su ID real de la BD.
    }

    // Recupera todo el historial de contrataciones asociadas a una empresa específica.
    public List<Reserva> listarReservasPorEmpresa(int idEmpresa) {
        List<Reserva> reservas = new ArrayList<>();
        // LEFT JOIN: 
        // Trae las reservas y, si tiene plantilla asignada, acopla su nombre descriptivo en la misma query.
        String sql = "SELECT r.*, p.nombre as plantilla_nombre FROM reservas r LEFT JOIN plantillas p ON r.id_plantilla = p.id WHERE r.id_empresa = ? ORDER BY r.fecha_evento DESC";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpresa);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Mapeo manual de las filas de la tabla MySQL a instancias de objetos Java.
                Reserva r = new Reserva();
                r.setId(rs.getInt("id"));
                r.setFechaEvento(rs.getDate("fecha_evento").toLocalDate());
                r.setFechaReserva(rs.getTimestamp("fecha_reserva").toLocalDateTime()); // Mapea fecha y hora del Timestamp.
                r.setNumInvitados(rs.getInt("num_invitados"));
                r.setEstado(rs.getString("estado"));
                
                // Reconstrucción del objeto interno Plantilla para mantener la integridad del modelo.
                Plantilla p = new Plantilla();
                p.setId(rs.getInt("id_plantilla"));
                p.setNombre(rs.getString("plantilla_nombre"));
                r.setPlantilla(p);
                
                reservas.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservas;
    }

    // Guarda el itinerario de un tirón mediante procesamiento por lotes.
    public boolean guardarCronograma(int idReserva, List<Actividad> actividades) {
        String deleteSql = "DELETE FROM actividades WHERE id_reserva = ?";
        String insertSql = "INSERT INTO actividades (id_reserva, nombre, descripcion, fecha_hora, duracion_minutos, cupo_maximo, importancia, categoria) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = ConexionController.getInstance().getConnection();
        try {
            // Desactiva el AutoCommit para abrir una transacción atómica. Si algo falla, no se guarda nada a medias.
        	// Espero que podamos usar esto, parece bastante útil, no sé si habrá una mejor manera de hacer algo así.
            con.setAutoCommit(false);
            
            // Paso 1: Limpia el itinerario viejo de la reserva para sobreescribir sin duplicar datos.
            try (PreparedStatement psDel = con.prepareStatement(deleteSql)) {
                psDel.setInt(1, idReserva);
                psDel.executeUpdate();
            }
            
            // Paso 2: Agrupa todas las inserciones en un lote/batch para mandarlas juntas al motor SQL en un solo viaje de red.
            try (PreparedStatement psIns = con.prepareStatement(insertSql)) {
                for (Actividad act : actividades) {
                    psIns.setInt(1, idReserva);
                    psIns.setString(2, act.getNombre());
                    psIns.setString(3, act.getDescripcion());
                    psIns.setTimestamp(4, Timestamp.valueOf(act.getFechaHora()));
                    psIns.setInt(5, act.getDuracionMinutos());
                    psIns.setInt(6, act.getCupoMaximo());
                    psIns.setString(7, act.getImportancia().toString()); // Convierte el Enum de Java a texto plano puro.
                    psIns.setString(8, act.getCategoria());
                    psIns.addBatch(); // Encola la query en memoria.
                }
                psIns.executeBatch(); // Impacta todas las inserciones juntas en el motor MySQL.
            }
            con.commit(); // Confirma los cambios y consolida la transacción física en disco.
            return true;
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } // Deshace todo el proceso si saltó un error técnico.
            e.printStackTrace();
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); } // Devuelve la conexión a su estado normal.
        }
    }

    // Trae cronológicamente todos los módulos horarios guardados bajo una reserva.
    // Para eso se usa el ORDER BY en la Consulta SQL.
    public List<Actividad> obtenerActividadesPorReserva(int idReserva) {
        List<Actividad> lista = new ArrayList<>();
        String sql = "SELECT * FROM actividades WHERE id_reserva = ? ORDER BY fecha_hora";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Actividad a = new Actividad();
                a.setId(rs.getInt("id"));
                a.setNombre(rs.getString("nombre"));
                a.setDescripcion(rs.getString("descripcion"));
                a.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
                a.setDuracionMinutos(rs.getInt("duracion_minutos"));
                a.setCupoMaximo(rs.getInt("cupo_maximo"));
                a.setImportancia(Importancia.valueOf(rs.getString("importancia"))); // Reconstruye el Enum desde el String de la BD.
                a.setCategoria(rs.getString("categoria"));
                lista.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Actualiza la relación de diseño de la reserva vinculándole una interfaz predefinida.
    public boolean asignarPlantilla(int idReserva, int idPlantilla) {
        String sql = "UPDATE reservas SET id_plantilla = ? WHERE id = ?";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPlantilla);
            ps.setInt(2, idReserva);
            return ps.executeUpdate() > 0; // Da true si el registro fue modificado efectivamente en el motor de persistencia.
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Recupera la lista completa de plantillas gráficas habilitadas para el front-end de la aplicación.
    public List<Plantilla> listarPlantillas() {
        List<Plantilla> plantas = new ArrayList<>();
        String sql = "SELECT * FROM plantillas WHERE activa = 1"; // Filtra ignorando los registros con baja lógica.
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
            e.printStackTrace();
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
                return rs.next(); // Retorna true si encontró al menos una coincidencia exacta de autorización.
            }
        } catch (SQLException e) {
            return false;
        }
    }

    // Compila subconsultas SQL complejas y formatea un string HTML nativo para inyectar directo en un JOptionPane.
    public String obtenerReporteConsolidadoEvento(String codigoEvento) {
    	// StringBuilder 'reporte' para no saturar concatenando Strings con +. 
    	// Los 'reporte.append' están abajo. < Esto ya lo habíamos usado el año pasado me parece.
        StringBuilder reporte = new StringBuilder();
        // Ejecuta tres conteos independientes mediante subconsultas indexadas en un solo golpe a la base de datos.
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
                    // Previene una división por cero si la lista de invitados fue cargada vacía en el sistema.
                    int porcentajeOcupacion = totalInvitados > 0 ? (totalCheckins * 100 / totalInvitados) : 0;

                    // Armado del maquetado HTML embed para su representación en cuadros de diálogo Swing.
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
                return "<html><body>❌ Código inválido.</body></html>";
            }
        } catch (SQLException e) {
            return "<html><body>❌ Error técnico.</body></html>";
        }
    }
}