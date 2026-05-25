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
        // Instanciamos un HashMap para empaquetar las métricas calculadas y poder mandarlas directo a las tablas de la UI.
        Map<String, Object> reporte = new HashMap<>();
        
        // QUERY 1 CORREGIDA: Trae el total esperado de la lista previa para esa reserva.
        String sqlTotalExpectativa = "SELECT COUNT(*) as total_esperado FROM lista_invitados_previa WHERE id_reserva = ?";
        
        // QUERY 2 CORREGIDA: Cuenta los confirmados reales basándose en los que ya tienen una habitación asignada.
        String sqlConfirmadosReales = "SELECT COUNT(DISTINCT id_usuario) as total_confirmados FROM asignaciones_habitaciones WHERE id_reserva = ?";
        
        // Query 3: Cuenta de forma lineal cuántas actividades tiene asignadas este evento.
        String sqlActividades = "SELECT COUNT(*) as total_actividades FROM actividades WHERE id_reserva = ?";
        
        // Bloque Try-with-resources: Gestiona la apertura del caño de red a MySQL y asegura que la conexión se cierre sola al terminar.
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

            // 2. Obtener Invitados Confirmados (Con habitación asignada)
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
                        reporte.put("totalActivities", rs.getInt("total_actividades")); // Mantené el key que use tu UI
                        reporte.put("totalActividades", rs.getInt("total_actividades")); // Por las dudas si mapeás en español
                    }
                }
            }
            
        } catch (SQLException e) {
            // Ataja cualquier quilombo de sintaxis SQL, tablas caídas o fallos físicos de comunicación con el driver.
            e.printStackTrace(); // Imprime la traza completa del error en la consola de Eclipse para debuggear al toque.
        }
        
        return reporte; // Devuelve la bolsa del Map armada con los datos recolectados.
    }
}