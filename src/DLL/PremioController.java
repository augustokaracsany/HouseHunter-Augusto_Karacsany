package DLL;

import java.sql.*;
import javax.swing.JOptionPane;

public class PremioController {
    private static PremioController instance;

    private PremioController() {}

    public static PremioController getInstance() {
        if (instance == null) {
            instance = new PremioController();
        }
        return instance;
    }

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
            return false;
        }
    }
}