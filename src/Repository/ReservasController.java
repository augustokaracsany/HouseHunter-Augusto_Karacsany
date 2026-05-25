package Repository;

import DLL.ConexionController;
import java.sql.*;

public class ReservasController extends ReservasRepository {

    // Registra el evento corporativo impactando de forma directa la tabla relacional reservas_hotel.
    @Override
    public boolean registrarReservaHotel(int idEmpresa, String codigoEvento, String fechaInicio, String fechaFin, int asistentes) throws SQLException {
        String sql = "INSERT INTO reservas_hotel (id_empresa, codigo_unico_evento, fecha_inicio, fecha_fin, cantidad_estimada_asistentes) VALUES (?, ?, ?, ?, ?)";
        Connection con = ConexionController.getInstance().getConnection();
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpresa);
            ps.setString(2, codigoEvento);
            ps.setDate(3, Date.valueOf(fechaInicio)); // Parsea la cadena de texto a tipo Date nativo de SQL.
            ps.setDate(4, Date.valueOf(fechaFin));
            ps.setInt(5, asistentes);
            
            return ps.executeUpdate() > 0; // Si altera filas en MySQL, devuelve verdadero.
        } catch (SQLException e) {
            System.err.println("Error al insertar en reservas_hotel: " + e.getMessage());
            throw e;
        }
    }
}