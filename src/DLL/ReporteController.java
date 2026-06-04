package DLL; // Capa de Acceso a Datos / Módulo de Métricas y Analítica.

import BLL.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ReporteController {

    // Obtener estadísticas para una reserva.
    // Usamos un Map ( clave-valor ) para estructurar datos sueltos de tipos distintos.
    // Usamos Map para no tener que crear una clase entidad nueva en BLL. 
	public Map<String, Object> obtenerReporteEvento(int idReserva) {
        Map<String, Object> reporte = new HashMap<>();
        
        // QUERY 1: Trae el total esperado de la lista previa para esa reserva.
        String sqlTotalExpectativa = "SELECT COUNT(*) as total_esperado FROM lista_invitados_previa WHERE id_reserva = ?";
        
        // QUERY 2 MODIFICADA: Cuenta los confirmados reales desde la columna física de la lista previa.
        String sqlConfirmadosReales = "SELECT COUNT(*) as total_confirmados FROM lista_invitados_previa WHERE id_reserva = ? AND asistencia_confirmada = 'S'";
        
        // Query 3: Cuenta de forma lineal cuántas actividades tiene asignadas este evento.
        String sqlActividades = "SELECT COUNT(*) as total_actividades FROM actividades WHERE id_reserva = ?";
        
        try (Connection con = ConexionController.getInstance().getConnection()) {
            
            int totalEsperado = 0;
            int totalConfirmados = 0;

            // 1. Obtener Total Invitados Esperados
            try (PreparedStatement ps = con.prepareStatement(sqlTotalExpectativa)) {
                ps.setInt(1, idReserva);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalEsperado = rs.getInt("total_esperado");
                    }
                }
            }

            // 2. Obtener Invitados Confirmados Reales (Los que marcaron 'S')
            try (PreparedStatement ps = con.prepareStatement(sqlConfirmadosReales)) {
                ps.setInt(1, idReserva);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalConfirmados = rs.getInt("total_confirmados");
                    }
                }
            }

            // Guardamos los totales en el Map de retorno
            reporte.put("totalInvitados", totalEsperado);
            reporte.put("confirmados", totalConfirmados);

            // Calcular porcentaje de confirmación real evitando la división por cero
            double porcentaje = totalEsperado > 0 ? (totalConfirmados * 100.0 / totalEsperado) : 0;
            reporte.put("porcentajeConfirmacion", Math.round(porcentaje * 100.0) / 100.0);
            
            // 3. Cantidad de Actividades Programadas
            try (PreparedStatement ps = con.prepareStatement(sqlActividades)) {
                ps.setInt(1, idReserva);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        reporte.put("totalActivities", rs.getInt("total_actividades")); 
                        reporte.put("totalActividades", rs.getInt("total_actividades")); 
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al generar el reporte de evento: " + e.getMessage());
            e.printStackTrace(); 
        }
        
        return reporte; 
    }
}