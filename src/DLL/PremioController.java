package DLL;

import BLL.Premio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PremioController {

    // CU29: Listar premios activos (los que tienen stock > 0 y están activos)
    public List<Premio> listarPremiosDisponibles() {
        List<Premio> premios = new ArrayList<>();
        String sql = "SELECT * FROM premios WHERE activo = 1 AND cantidad_disponible > 0";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Premio p = new Premio();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
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

    // CU29: Participar en sorteo (registra la participación)
    // También verifica si ya participó antes (evita duplicados)
    public boolean participarEnSorteo(int idInvitado, int idPremio) {
        // Primero verificar si ya participó en este premio
        String checkSql = "SELECT id FROM participaciones_premios WHERE id_invitado = ? AND id_premio = ?";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement psCheck = con.prepareStatement(checkSql)) {
            psCheck.setInt(1, idInvitado);
            psCheck.setInt(2, idPremio);
            ResultSet rs = psCheck.executeQuery();
            if (rs.next()) {
                System.out.println("El invitado ya participó en este premio.");
                return false; // Ya participó
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Insertar participación, inicialmente NO elegible hasta verificar asistencia mínima
        String insertSql = "INSERT INTO participaciones_premios (id_invitado, id_premio, elegible, ganador, voucher) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(insertSql)) {
            ps.setInt(1, idInvitado);
            ps.setInt(2, idPremio);
            ps.setBoolean(3, false); // elegible se actualizará después con CU30
            ps.setBoolean(4, false); // ganador por defecto
            ps.setString(5, null);   // voucher nulo
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // CU30: Verificar si el invitado es elegible para participar en sorteos
    // Condición: asistencia confirmada (asistencia_confirmada = TRUE)
    // (Además, se puede agregar que haya participado en al menos una actividad, etc. – lo dejamos simple)
    public boolean esElegible(int idInvitado) {
        String sql = "SELECT asistencia_confirmada FROM invitados WHERE id = ?";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idInvitado);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("asistencia_confirmada");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // CU31: Obtener voucher si el invitado resultó ganador de un premio específico
    // Si es ganador, genera un voucher único (o devuelve el ya generado)
    public String obtenerVoucher(int idInvitado, int idPremio) {
        // Buscar si existe participación y si ya tiene voucher
        String selectSql = "SELECT voucher, ganador FROM participaciones_premios WHERE id_invitado = ? AND id_premio = ?";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(selectSql)) {
            ps.setInt(1, idInvitado);
            ps.setInt(2, idPremio);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String voucherExistente = rs.getString("voucher");
                if (voucherExistente != null && !voucherExistente.isEmpty()) {
                    return voucherExistente; // Ya tenía voucher
                }
                boolean esGanador = rs.getBoolean("ganador");
                if (!esGanador) {
                    return null; // No es ganador
                }
                // Es ganador pero aún no tiene voucher → lo generamos ahora
                String nuevoVoucher = generarVoucherUnico();
                String updateSql = "UPDATE participaciones_premios SET voucher = ? WHERE id_invitado = ? AND id_premio = ?";
                try (PreparedStatement psUpd = con.prepareStatement(updateSql)) {
                    psUpd.setString(1, nuevoVoucher);
                    psUpd.setInt(2, idInvitado);
                    psUpd.setInt(3, idPremio);
                    if (psUpd.executeUpdate() > 0) {
                        return nuevoVoucher;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método auxiliar para generar un voucher único
    private String generarVoucherUnico() {
        return "VCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // **Opcional: Método para que el administrador realice el sorteo**
    // Esto no es parte de los CU del invitado, pero es necesario para que existan ganadores.
    // Selecciona un ganador aleatorio entre los participantes elegibles de un premio.
    public boolean realizarSorteo(int idPremio) {
        // Verificar que el premio tenga stock disponible
        String checkStock = "SELECT cantidad_disponible FROM premios WHERE id = ?";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement psCheck = con.prepareStatement(checkStock)) {
            psCheck.setInt(1, idPremio);
            ResultSet rsStock = psCheck.executeQuery();
            if (rsStock.next() && rsStock.getInt("cantidad_disponible") <= 0) {
                return false; // Sin stock
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Primero actualizar elegibilidad de todos los participantes (CU30)
        String updateElegibles = "UPDATE participaciones_premios SET elegible = ? WHERE id_premio = ? AND elegible = 0";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement psUpd = con.prepareStatement(updateElegibles)) {
            psUpd.setBoolean(1, true);
            psUpd.setInt(2, idPremio);
            psUpd.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Seleccionar un participante elegible al azar que aún no sea ganador
        String seleccionarGanador = "SELECT id_invitado FROM participaciones_premios WHERE id_premio = ? AND elegible = 1 AND ganador = 0 ORDER BY RAND() LIMIT 1";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement psSel = con.prepareStatement(seleccionarGanador)) {
            psSel.setInt(1, idPremio);
            ResultSet rs = psSel.executeQuery();
            if (rs.next()) {
                int idGanador = rs.getInt("id_invitado");
                // Marcar como ganador
                String marcar = "UPDATE participaciones_premios SET ganador = 1 WHERE id_invitado = ? AND id_premio = ?";
                try (PreparedStatement psGan = con.prepareStatement(marcar)) {
                    psGan.setInt(1, idGanador);
                    psGan.setInt(2, idPremio);
                    psGan.executeUpdate();
                }
                // Reducir stock del premio
                String reducirStock = "UPDATE premios SET cantidad_disponible = cantidad_disponible - 1 WHERE id = ?";
                try (PreparedStatement psStock = con.prepareStatement(reducirStock)) {
                    psStock.setInt(1, idPremio);
                    psStock.executeUpdate();
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
