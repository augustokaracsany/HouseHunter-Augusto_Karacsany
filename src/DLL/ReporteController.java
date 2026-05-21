package DLL;

import BLL.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ReporteController {

    // Obtener estadísticas para una reserva (CU13)
    public Map<String, Object> obtenerReporteEvento(int idReserva) {
        Map<String, Object> reporte = new HashMap<>();
        String sqlInvitados = "SELECT COUNT(*) as total, SUM(asistencia_confirmada) as confirmados FROM invitados WHERE id_reserva = ?";
        String sqlActividades = "SELECT COUNT(*) as total_actividades FROM actividades WHERE id_reserva = ?";
        
        try (Connection con = ConexionController.getInstance().getConnection()) {
            // Total invitados y confirmados
            try (PreparedStatement ps = con.prepareStatement(sqlInvitados)) {
                ps.setInt(1, idReserva);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    reporte.put("totalInvitados", rs.getInt("total"));
                    reporte.put("confirmados", rs.getInt("confirmados"));
                    double porcentaje = rs.getInt("total") > 0 ? (rs.getInt("confirmados") * 100.0 / rs.getInt("total")) : 0;
                    reporte.put("porcentajeConfirmacion", Math.round(porcentaje * 100.0) / 100.0);
                }
            }
            // Cantidad de actividades
            try (PreparedStatement ps = con.prepareStatement(sqlActividades)) {
                ps.setInt(1, idReserva);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    reporte.put("totalActividades", rs.getInt("total_actividades"));
                }
            }
            // También se pueden agregar más estadísticas (actividades por importancia, etc.)
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reporte;
    }
}