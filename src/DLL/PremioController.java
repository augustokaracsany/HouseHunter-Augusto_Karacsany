package DLL; // Capa de Acceso a Datos / Módulo de Sorteos y Recompensas.

import BLL.Premio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.JOptionPane;

public class PremioController {
    // Estructura Singleton de Augusto.
    private static PremioController instance;

    private PremioController() {}

    public static PremioController getInstance() {
        if (instance == null) {
            instance = new PremioController();
        }
        return instance;
    }

    // ( Sorteo rápido del Administrador ).
    // Elige un ganador al azar en caliente basado en la asistencia real registrada.
    public boolean ejecutarSorteoPremio(String codigoEvento, String descripcionPremio) {
        Connection con = ConexionController.getInstance().getConnection();
        
        String sqlSorteo = "SELECT rh.id AS id_res_real, aa.id_usuario, dp.nombre, dp.apellido, dp.dni " +
                           "FROM asistencias_actividades aa " +
                           "JOIN actividades act ON aa.id_actividad = act.id " +
                           "JOIN reservas_hotel rh ON act.id_reserva = rh.id " +
                           "JOIN datos_personas dp ON aa.id_usuario = dp.id_usuario " +
                           "WHERE rh.codigo_unico_evento = ? AND aa.asistio = 'S' " +
                           "ORDER BY RAND() LIMIT 1";
        
        String sqlInsertPremio = "INSERT INTO premios (id_reserva, nombre_premio, entregado, id_ganador_usuario) VALUES (?, ?, 'S', ?)";

        try {
            int idReserva = 0, idUsuarioGanador = 0;
            String nombreCompleto = "", dniGanador = "";

            try (PreparedStatement ps = con.prepareStatement(sqlSorteo)) {
                ps.setString(1, codigoEvento);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idReserva = rs.getInt("id_res_real");
                        idUsuarioGanador = rs.getInt("id_usuario");
                        nombreCompleto = rs.getString("nombre") + " " + rs.getString("apellido");
                        dniGanador = rs.getString("dni");
                    }
                }
            }

            if (idUsuarioGanador == 0) {
                JOptionPane.showMessageDialog(null, "❌ No hay asistentes confirmados para este sorteo.", "Sorteo Vacío", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sqlInsertPremio)) {
                ps.setInt(1, idReserva);
                ps.setString(2, descripcionPremio);
                ps.setInt(3, idUsuarioGanador);
                ps.executeUpdate(); 
            }

            String mensajeExito = "<html><body style='width: 250px; text-align: center;'>"
                                + "<h2 style='color: #2ecc71;'>🎉 ¡Tenemos Ganador! 🎉</h2>"
                                + "<p><b>Invitado:</b> " + nombreCompleto + "</p>"
                                + "<p><b>DNI:</b> " + dniGanador + "</p><hr>"
                                + "<p><b>Premio:</b><br>" + descripcionPremio + "</p></body></html>";
            
            JOptionPane.showMessageDialog(null, mensajeExito, "Sorteo Exitoso", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ALTA de Premio desde Empresa.
    public boolean guardarPremio(int idReserva, String nombre, String descripcion, int cantidad) {
        String sql = "INSERT INTO premios (id_reserva, nombre_premio, descripcion, cantidad_disponible, activo, entregado) VALUES (?, ?, ?, ?, 1, 'N')";
        Connection con = ConexionController.getInstance().getConnection();
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setString(2, nombre);
            ps.setString(3, descripcion);
            ps.setInt(4, cantidad);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Listar Premios filtrados por Evento.
    public List<Premio> listarPremiosPorReserva(int idReserva) {
        List<Premio> premios = new ArrayList<>();
        String sql = "SELECT id, nombre_premio, descripcion, cantidad_disponible, activo FROM premios WHERE id_reserva = ? AND activo = 1";
        Connection con = ConexionController.getInstance().getConnection();
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Premio p = new Premio();
                    p.setId(rs.getInt("id"));
                    p.setNombre(rs.getString("nombre_premio"));
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                    p.setActivo(rs.getBoolean("activo"));
                    premios.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return premios;
    }

    // Listar Premios Disponibles en general.
    public List<Premio> listarPremiosDisponibles() {
        List<Premio> premios = new ArrayList<>();
        String sql = "SELECT id, nombre_premio, descripcion, cantidad_disponible, activo FROM premios WHERE activo = 1 AND cantidad_disponible > 0";
        Connection con = ConexionController.getInstance().getConnection();
        
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Premio p = new Premio();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre_premio")); 
                p.setDescripcion(rs.getString("descripcion"));
                p.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                p.setActivo(rs.getBoolean("activo"));
                premios.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return premios;
    }

    public boolean participarEnSorteo(int idUsuarioReal, int idPremio) {
        // Primero verificamos si ya existe la postulación para evitar duplicados.
        String sqlCheck = "SELECT COUNT(*) FROM participaciones_premios WHERE id_invitado = ? AND id_premio = ?";
        String sqlInsert = "INSERT INTO participaciones_premios (id_invitado, id_premio, elegible, ganador) VALUES (?, ?, 1, 0)";
        
        Connection con = ConexionController.getInstance().getConnection();
        
        try {
            // 1. Validar duplicación.
            try (PreparedStatement psCheck = con.prepareStatement(sqlCheck)) {
                psCheck.setInt(1, idUsuarioReal);
                psCheck.setInt(2, idPremio);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return false; // Ya está postulado.
                    }
                }
            }
            
            // 2. Insertar postulación limpia vinculada al ID de Usuario Real.
            try (PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, idUsuarioReal);
                psInsert.setInt(2, idPremio);
                int filas = psInsert.executeUpdate();
                return filas > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error en PremioController.participarEnSorteo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    // Verificar Requisitos de Asistencia ( Corregido. ).

    public boolean esElegible(int idUsuario) {
        // CORRECCIÓN: Esto debería apuntar a la tabla 'usuarios' o 'asistencias_actividades'.
        String sql = "SELECT id FROM usuarios WHERE id = ?"; 
        Connection con = ConexionController.getInstance().getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true; // Si el usuario existe y su login está activo. Tira TRUE.
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // ( Obtener Voucher. )

    public String obtenerVoucher(int idInvitado, int idPremio) {
        String selectSql = "SELECT voucher, ganador FROM participaciones_premios WHERE id_invitado = ? AND id_premio = ?";
        String updateSql = "UPDATE participaciones_premios SET voucher = ? WHERE id_invitado = ? AND id_premio = ?";
        Connection con = ConexionController.getInstance().getConnection();
        
        try (PreparedStatement ps = con.prepareStatement(selectSql)) {
            ps.setInt(1, idInvitado);
            ps.setInt(2, idPremio);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String voucherExistente = rs.getString("voucher");
                    if (voucherExistente != null && !voucherExistente.isEmpty()) {
                        return voucherExistente;
                    }
                    
                    boolean esGanador = rs.getBoolean("ganador");
                    if (!esGanador) {
                        return null; 
                    }
                    
                    String nuevoVoucher = generarVoucherUnico();
                    try (PreparedStatement psUpd = con.prepareStatement(updateSql)) {
                        psUpd.setString(1, nuevoVoucher);
                        psUpd.setInt(2, idInvitado);
                        psUpd.setInt(3, idPremio);
                        if (psUpd.executeUpdate() > 0) {
                            return nuevoVoucher;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generarVoucherUnico() {
        return "VCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


    // Ejecutar Quini6
    
    public boolean realizarSorteo(int idPremio) {
        String checkStock = "SELECT cantidad_disponible FROM premios WHERE id = ?";
        String updateElegibles = "UPDATE participaciones_premios SET elegible = ? WHERE id_premio = ? AND elegible = 0";
        String seleccionarGanador = "SELECT id_invitado FROM participaciones_premios WHERE id_premio = ? AND elegible = 1 AND ganador = 0 ORDER BY RAND() LIMIT 1";
        String marcarGanador = "UPDATE participaciones_premios SET ganador = 1 WHERE id_invitado = ? AND id_premio = ?";
        String reducirStock = "UPDATE premios SET cantidad_disponible = cantidad_disponible - 1 WHERE id = ?";
        
        Connection con = ConexionController.getInstance().getConnection();
        
        try {
            // Transacción Manual.
            con.setAutoCommit(false); 

            try {
                // 1. Validar el Stock disponible.
                try (PreparedStatement psCheck = con.prepareStatement(checkStock)) {
                    psCheck.setInt(1, idPremio);
                    try (ResultSet rsStock = psCheck.executeQuery()) {
                        if (rsStock.next() && rsStock.getInt("cantidad_disponible") <= 0) {
                            con.rollback(); 
                            return false;
                        }
                    }
                }

                // 2. Actualizar en lote el estado de elegibilidad de los participantes.
                try (PreparedStatement psUpd = con.prepareStatement(updateElegibles)) {
                    psUpd.setBoolean(1, true);
                    psUpd.setInt(2, idPremio);
                    psUpd.executeUpdate();
                }

                // 3. Seleccionar un ganador aleatorio de la lista.
                int idGanador = 0;
                try (PreparedStatement psSel = con.prepareStatement(seleccionarGanador)) {
                    psSel.setInt(1, idPremio);
                    try (ResultSet rs = psSel.executeQuery()) {
                        if (rs.next()) {
                            idGanador = rs.getInt("id_invitado");
                        }
                    }
                }

                // Si no hay postulados elegibles válidos, salimos limpiamente.
                if (idGanador == 0) {
                    con.rollback();
                    return false;
                }

                // 4. Marcar al ganador seleccionado.
                try (PreparedStatement psGan = con.prepareStatement(marcarGanador)) {
                    psGan.setInt(1, idGanador);
                    psGan.setInt(2, idPremio);
                    psGan.executeUpdate();
                }
                
                // Faltaba que se reduzca el Stock también me parece.
                // Cuando hice el MERGE de feat/luca no había nada de reducirStock, no me suena.
                // 5. Reducir el stock del inventario.
                try (PreparedStatement psStock = con.prepareStatement(reducirStock)) {
                    psStock.setInt(1, idPremio);
                    psStock.executeUpdate();
                }

                con.commit(); 
                return true;

            } catch (SQLException ex) {
                con.rollback();
                throw ex; 
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}