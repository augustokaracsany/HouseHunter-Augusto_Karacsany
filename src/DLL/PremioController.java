package DLL; // Capa de Acceso a Datos / Módulo de Sorteos y Recompensas.

import BLL.Premio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.JOptionPane;

public class PremioController {
    // Estructura Singleton de Augusto
    private static PremioController instance;

    private PremioController() {}

    public static PremioController getInstance() {
        if (instance == null) {
            instance = new PremioController();
        }
        return instance;
    }

    // ( Sorteo rápido del Administrador )
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

    // Casos de Uso del Invitado (Branch 'feat/luca')
    // ( Listar premios activos. )
    public List<Premio> listarPremiosDisponibles() {
        List<Premio> premios = new ArrayList<>();
        // Ajustamos la query para evitar colisiones de nombres según las columnas de la BD.
        String sql = "SELECT id, nombre_premio, descripcion, cantidad_disponible, activo FROM premios WHERE activo = 1 AND cantidad_disponible > 0";
        
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Premio p = new Premio();
                p.setId(rs.getInt("id"));
                // Salvamos la diferencia de nombres ( nombre_premio en la BD -> setNombre en el objeto. )
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

    // ( Participar en sorteo postulándose manualmente. )
    public boolean participarEnSorteo(int idInvitado, int idPremio) {
        String checkSql = "SELECT id FROM participaciones_premios WHERE id_invitado = ? AND id_premio = ?";
        String insertSql = "INSERT INTO participaciones_premios (id_invitado, id_premio, elegible, ganador, voucher) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection con = ConexionController.getInstance().getConnection()) {
            
            // 1. Verificación previa de duplicados.
            try (PreparedStatement psCheck = con.prepareStatement(checkSql)) {
                psCheck.setInt(1, idInvitado);
                psCheck.setInt(2, idPremio);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("El invitado ya participó en este premio.");
                        return false;
                    }
                }
            }
            
            // 2. Inserción directa si pasó el filtro.
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setInt(1, idInvitado);
                ps.setInt(2, idPremio);
                ps.setBoolean(3, false); 
                ps.setBoolean(4, false); 
                ps.setString(5, null);  
                return ps.executeUpdate() > 0;
            }
            
        } catch (SQLException e) {
            // Maneja de forma segura errores como la clave duplicada. 
        	// ( El "Error 1062" que sale en MySQL. )
            e.printStackTrace();
            return false;
        }
    }

    // ( Verificar si el invitado cumple los requisitos de asistencia. )
    public boolean esElegible(int idInvitado) {
        String sql = "SELECT asistencia_confirmada FROM invitados WHERE id = ?";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idInvitado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("asistencia_confirmada");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ( Obtener o generar el voucher único si salió ganador )
    public String obtenerVoucher(int idInvitado, int idPremio) {
        String selectSql = "SELECT voucher, ganador FROM participaciones_premios WHERE id_invitado = ? AND id_premio = ?";
        String updateSql = "UPDATE participaciones_premios SET voucher = ? WHERE id_invitado = ? AND id_premio = ?";
        
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(selectSql)) {
            
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
                        return null; // No ganó, no se le da voucher.
                    }
                    
                    // Si ganó y no lo tiene, se lo generamos de manera atómica
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

    // ( Método de Luca para sortear sobre los postulados - REFACTORIZADO TRANSACCIONAL )
    public boolean realizarSorteo(int idPremio) {
        String checkStock = "SELECT cantidad_disponible FROM premios WHERE id = ?";
        String updateElegibles = "UPDATE participaciones_premios SET elegible = ? WHERE id_premio = ? AND elegible = 0";
        String seleccionarGanador = "SELECT id_invitado FROM participaciones_premios WHERE id_premio = ? AND elegible = 1 AND ganador = 0 ORDER BY RAND() LIMIT 1";
        String marcarGanador = "UPDATE participaciones_premios SET ganador = 1 WHERE id_invitado = ? AND id_premio = ?";
        String reducirStock = "UPDATE premios SET cantidad_disponible = cantidad_disponible - 1 WHERE id = ?";
        
        // Abrimos una única conexión para controlar toda la operación en bloque continuo
        try (Connection con = ConexionController.getInstance().getConnection()) {
            
            // ACTIVAMOS LA TRANSACCIÓN MANUAL
            con.setAutoCommit(false); 

            try {
                // 1. Validar Stock disponible
                try (PreparedStatement psCheck = con.prepareStatement(checkStock)) {
                    psCheck.setInt(1, idPremio);
                    try (ResultSet rsStock = psCheck.executeQuery()) {
                        if (rsStock.next() && rsStock.getInt("cantidad_disponible") <= 0) {
                            con.rollback(); // Cancelamos todo por falta de stock
                            return false;
                        }
                    }
                }

                // 2. Actualizar en lote el estado de elegibilidad de los participantes
                try (PreparedStatement psUpd = con.prepareStatement(updateElegibles)) {
                    psUpd.setBoolean(1, true);
                    psUpd.setInt(2, idPremio);
                    psUpd.executeUpdate();
                }

                // 3. Seleccionar un ganador aleatorio de la lista
                int idGanador = 0;
                try (PreparedStatement psSel = con.prepareStatement(seleccionarGanador)) {
                    psSel.setInt(1, idPremio);
                    try (ResultSet rs = psSel.executeQuery()) {
                        if (rs.next()) {
                            idGanador = rs.getInt("id_invitado");
                        }
                    }
                }

                // Si no hay postulados elegibles válidos, salimos limpiamente
                if (idGanador == 0) {
                    con.rollback();
                    return false;
                }

                // 4. Marcar al ganador seleccionado
                try (PreparedStatement psGan = con.prepareStatement(marcarGanador)) {
                    psGan.setInt(1, idGanador);
                    psGan.setInt(2, idPremio);
                    psGan.executeUpdate();
                }

                // 5. Reducir el stock del inventario
                try (PreparedStatement psStock = con.prepareStatement(reducirStock)) {
                    psStock.setInt(1, idPremio);
                    psStock.executeUpdate();
                }

                // SI TODO SALIÓ BIEN, IMPACTAMOS LOS CAMBIOS EN LA BD JUNTOS
                con.commit(); 
                return true;

            } catch (SQLException ex) {
                // Si cualquiera de los 5 pasos falla en runtime, el motor deshace todo automáticamente
                con.rollback();
                throw ex; 
            } finally {
                // Restauramos el comportamiento por defecto de la conexión
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}