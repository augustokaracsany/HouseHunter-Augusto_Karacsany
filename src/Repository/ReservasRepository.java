package Repository;

import BLL.Reserva;
import java.sql.SQLException;

public abstract class ReservasRepository {
    // Contrato para obligar la implementación del guardado de eventos.
    public abstract boolean registrarReservaHotel(int idEmpresa, String codigoEvento, String fechaInicio, String fechaFin, int asistentes) throws SQLException;
}