package DLL;

import BLL.EstadoHabitacion;
import java.sql.*;
import javax.swing.JOptionPane;

public class HabitacionController {
    private static HabitacionController instance;

    private HabitacionController() {}

    public static HabitacionController getInstance() {
        if (instance == null) {
            instance = new HabitacionController();
        }
        return instance;
    }

    public boolean procesarCheckInHabitacion(String codigoEvento, String dniHuesped, String numeroHabitacion) {
        Connection con = ConexionController.getInstance().getConnection();
        String sqlReserva = "SELECT id FROM reservas_hotel WHERE codigo_unico_evento = ?";
        String sqlUsuario = "SELECT id_usuario FROM datos_personas WHERE dni = ?";
        String sqlHabitacion = "SELECT id, estado FROM habitaciones WHERE numero = ?";
        
        try {
            con.setAutoCommit(false);
            int idReserva = 0;
            try (PreparedStatement ps = con.prepareStatement(sqlReserva)) {
                ps.setString(1, codigoEvento);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) idReserva = rs.getInt("id");
                }
            }

            int idUsuario = 0;
            try (PreparedStatement ps = con.prepareStatement(sqlUsuario)) {
                ps.setString(1, dniHuesped);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) idUsuario = rs.getInt("id_usuario");
                }
            }

            if (idReserva == 0 || idUsuario == 0) {
                JOptionPane.showMessageDialog(null, "Error: Evento o DNI no encontrados.", "Error de Check-in", JOptionPane.ERROR_MESSAGE);
                con.rollback();
                return false;
            }

            int idHabitacion = 0;
            String estadoStr = "";
            try (PreparedStatement ps = con.prepareStatement(sqlHabitacion)) {
                ps.setString(1, numeroHabitacion);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idHabitacion = rs.getInt("id");
                        estadoStr = rs.getString("estado");
                    }
                }
            }

            EstadoHabitacion estadoActual = EstadoHabitacion.valueOf(estadoStr);
            EstadoHabitacion nuevoEstado;
            String casillero = "";

            if (estadoActual == EstadoHabitacion.Libre) {
                nuevoEstado = EstadoHabitacion.Half;
                casillero = "1";
            } else if (estadoActual == EstadoHabitacion.Half) {
                nuevoEstado = EstadoHabitacion.Completa;
                casillero = "2";
            } else {
                JOptionPane.showMessageDialog(null, "Error: Habitación Completa o SOBRE-ASIGNACIÓN.", "Límite Excedido", JOptionPane.WARNING_MESSAGE);
                con.rollback();
                return false;
            }

            String sqlAsignar = "INSERT INTO asignaciones_habitaciones (id_reserva, id_habitacion, id_usuario, casillero_checkin) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlAsignar)) {
                ps.setInt(1, idReserva);
                ps.setInt(2, idHabitacion);
                ps.setInt(3, idUsuario);
                ps.setString(4, casillero);
                ps.executeUpdate();
            }

            String sqlUpdateHab = "UPDATE habitaciones SET estado = ? WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlUpdateHab)) {
                ps.setString(1, nuevoEstado.toString());
                ps.setInt(2, idHabitacion);
                ps.executeUpdate();
            }

            con.commit();
            JOptionPane.showMessageDialog(null, "Check-in Exitoso! Habitación " + numeroHabitacion + " pasó a: " + nuevoEstado);
            return true;

        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            if (e.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(null, "Error: El invitado ya cuenta con un Check-in registrado.", "DNI Duplicado", JOptionPane.ERROR_MESSAGE);
            } else {
                System.err.println("Error en transacción de Check-in: " + e.getMessage());
            }
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public String[][] obtenerEstadoHabitacionesMatriz() {
        // 1. Primero contamos cuántas habitaciones hay para inicializar la matriz con el tamaño exacto
        String sqlContar = "SELECT COUNT(*) FROM habitaciones";
        String sqlDatos = "SELECT numero, estado FROM habitaciones ORDER BY numero ASC";
        Connection con = ConexionController.getInstance().getConnection();
        
        try {
            int cantidadFilas = 0;
            try (PreparedStatement ps = con.prepareStatement(sqlContar);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cantidadFilas = rs.getInt(1);
                }
            }
            
            // Inicializamos la matriz: cantidadFilas x 2 columnas (Número y Estado)
            String[][] matrizDatos = new String[cantidadFilas][2];
            
            try (PreparedStatement ps = con.prepareStatement(sqlDatos);
                 ResultSet rs = ps.executeQuery()) {
                
                int i = 0;
                while (rs.next()) {
                    matrizDatos[i][0] = rs.getString("numero");
                    matrizDatos[i][1] = rs.getString("estado");
                    i++;
                }
            }
            
            return matrizDatos;
            
        } catch (SQLException e) {
            System.err.println("Error al generar matriz de habitaciones: " + e.getMessage());
            // En caso de error, devolvemos una matriz vacía para que la interfaz no tire NullPointerException
            return new String[0][2];
        }
    }
}