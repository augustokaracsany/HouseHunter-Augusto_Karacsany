package DLL; // Capa de Acceso a Datos / Módulo de Métricas y Analítica. < DLL.

import BLL.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ReporteController {

    // Obtener estadísticas para una reserva.
    // Usamos un Map ( clave-valor ) para estructurar datos sueltos de tipos distintos ( Strings, Integers y Doubles ). s
	// Usamos Map para no tener que crear una clase entidad nueva en BLL. 
    public Map<String, Object> obtenerReporteEvento(int idReserva) {
        // Instanciamos un HashMap para empaquetar las métricas calculadas y poder mandarlas directo a las tablas de la UI.
        Map<String, Object> reporte = new HashMap<>();
        
        // Query 1: Cuenta las filas totales y acumula los bits o enteros de la columna 'asistencia_confirmada'.
        String sqlInvitados = "SELECT COUNT(*) as total, SUM(asistencia_confirmada) as confirmados FROM invitados WHERE id_reserva = ?";
        // Query 2: Cuenta de forma lineal cuántas actividades tiene asignadas este evento.
        String sqlActividades = "SELECT COUNT(*) as total_actividades FROM actividades WHERE id_reserva = ?";
        
        // Bloque Try-with-resources: Gestiona la apertura del caño de red a MySQL y asegura que la conexión se cierre sola al terminar.
        try (Connection con = ConexionController.getInstance().getConnection()) {
            
            // ( Total Invitados y Confirmados. )
            // Abrimos Statement seguro pasando el ID de evento para bloquear inyecciones maliciosas de SQL. < Para más Seguridad.
            try (PreparedStatement ps = con.prepareStatement(sqlInvitados)) {
                ps.setInt(1, idReserva); // Bindea el parámetro '?' de la primera query.
                ResultSet rs = ps.executeQuery(); // Dispara la consulta y guarda el puntero de los registros devueltos.
                
                // If de control: Se posiciona en la primera fila devuelta por la base de datos si es que existe.
                if (rs.next()) {
                    // Metemos las respuestas de MySQL adentro de la bolsa del Map con etiquetas de texto específicas.
                    reporte.put("totalInvitados", rs.getInt("total"));
                    reporte.put("confirmados", rs.getInt("confirmados"));
                    
                    // Si la lista de invitados está vacía, clava el porcentaje en cero de una para evitar el error de división por cero ( ArithmeticException. ). < Explota todo.
                    double porcentaje = rs.getInt("total") > 0 ? (rs.getInt("confirmados") * 100.0 / rs.getInt("total")) : 0;
                    
                    // Multiplicamos y dividimos por 100.0 usando Math.round para truncar los decimales flotantes y dejar un formato limpio (ej: 75.25%).
                    reporte.put("porcentajeConfirmacion", Math.round(porcentaje * 100.0) / 100.0);
                }
            } // Acá se destruye de forma automática el primer PreparedStatement de invitados en memoria.
            
            // ( Cantidad de Actividades. )
            // Reutilizamos la conexión abierta para armar la segunda declaración de consulta.
            try (PreparedStatement ps = con.prepareStatement(sqlActividades)) {
                ps.setInt(1, idReserva); // Vincula el ID de la reserva a la segunda query de actividades.
                ResultSet rs = ps.executeQuery();
                
                // Avanza a la fila única de conteo devuelta por el motor de base de datos.
                if (rs.next()) {
                    // Agrega el entero de actividades totales al diccionario del reporte final.
                    reporte.put("totalActividades", rs.getInt("total_actividades"));
                }
            } // Cierre automático del PreparedStatement de actividades.
            
            // También se pueden agregar más estadísticas ( Actividades por importancia, etc. )
            
        } catch (SQLException e) {
            // Ataja cualquier quilombo de sintaxis SQL, tablas caídas o fallos físicos de comunicación con el driver.
            e.printStackTrace(); // Imprime la traza completa del error en la consola de Eclipse para debuggear al toque.
        }
        
        return reporte; // Devuelve la bolsa del Map armada con los datos recolectados. 
        // ( O vacía si saltó directo al catch. ). En ambos casos bien.
    }
}