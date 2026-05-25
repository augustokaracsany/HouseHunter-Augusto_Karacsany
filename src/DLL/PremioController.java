package DLL; // Capa de Acceso a Datos / Módulo de Sorteos y Recompensas. < DLL.

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
        // Query de Sorteo: Une cuatro tablas para filtrar por código de evento único, matchea los datos personales del usuario y usa ORDER BY RAND() LIMIT 1 para aislar una fila aleatoria que cumpla el presentismo.
        String sqlSorteo = "SELECT rh.id AS id_res_real, aa.id_usuario, dp.nombre, dp.apellido, dp.dni " +
                           "FROM asistencias_actividades aa " +
                           "JOIN actividades act ON aa.id_actividad = act.id " +
                           "JOIN reservas_hotel rh ON act.id_reserva = rh.id " +
                           "JOIN datos_personas dp ON aa.id_usuario = dp.id_usuario " +
                           "WHERE rh.codigo_unico_evento = ? AND aa.asistio = 'S' " +
                           "ORDER BY RAND() LIMIT 1";
        
        // Query de Inserción: Registra de forma persistente la adjudicación del premio asignando el ID de la reserva y el ID del ganador en la tabla premios.
        String sqlInsertPremio = "INSERT INTO premios (id_reserva, nombre_premio, entregado, id_ganador_usuario) VALUES (?, ?, 'S', ?)";

        try {
            int idReserva = 0, idUsuarioGanador = 0;
            String nombreCompleto = "", dniGanador = "";

            // Preparamos la consulta del sorteo inyectando el código del evento en el primer marcador de posición.
            try (PreparedStatement ps = con.prepareStatement(sqlSorteo)) {
                ps.setString(1, codigoEvento);
                try (ResultSet rs = ps.executeQuery()) {
                    // Evaluamos si el ResultSet contiene al menos un registro mapeado por el motor de base de datos.
                    if (rs.next()) {
                        idReserva = rs.getInt("id_res_real");
                        idUsuarioGanador = rs.getInt("id_usuario");
                        nombreCompleto = rs.getString("nombre") + " " + rs.getString("apellido");
                        dniGanador = rs.getString("dni");
                    }
                }
            }

            // Validación por variable bandera: Si el ID no cambió de cero, significa que nadie asistió a las actividades y corta el flujo retornando un valor booleano falso.
            if (idUsuarioGanador == 0) {
                JOptionPane.showMessageDialog(null, "❌ No hay asistentes confirmados para este sorteo.", "Sorteo Vacío", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Ejecución de la escritura: Bindeamos los tipos de datos correspondientes (enteros y cadenas) para impactar el registro en MySQL.
            try (PreparedStatement ps = con.prepareStatement(sqlInsertPremio)) {
                ps.setInt(1, idReserva);
                ps.setString(2, descripcionPremio);
                ps.setInt(3, idUsuarioGanador);
                ps.executeUpdate(); // Dispara la sentencia DML de inserción física.
            }

            // Armamos un layout nativo en HTML para formatear la ventana emergente de la interfaz gráfica de usuario.
            String mensajeExito = "<html><body style='width: 250px; text-align: center;'>"
                                + "<h2 style='color: #2ecc71;'>🎉 ¡Tenemos Ganador! 🎉</h2>"
                                + "<p><b>Invitado:</b> " + nombreCompleto + "</p>"
                                + "<p><b>DNI:</b> " + dniGanador + "</p><hr>"
                                + "<p><b>Premio:</b><br>" + descripcionPremio + "</p></body></html>";
            
            JOptionPane.showMessageDialog(null, mensajeExito, "Sorteo Exitoso", JOptionPane.INFORMATION_MESSAGE);
            return true; // Retorna verdadero confirmando que todo el proceso transaccional terminó de diez.
        } catch (SQLException e) {
            // Ataja fallos de comunicación con el motor SQL o violaciones de claves foráneas.
            return false;
        }
    }

    
    // Casos de Uso del Invitado. ( Traídos de la branch 'feat/luca'. )
  

    // ( CU29: Listar premios activos. )
    // Trae de la base de datos los registros que tienen stock disponible y la bandera lógica activada.
    public List<Premio> listarPremiosDisponibles() {
        List<Premio> premios = new ArrayList<>();
        String sql = "SELECT * FROM premios WHERE activo = 1 AND cantidad_disponible > 0";
        // Estructura Try-with-resources anidada para asegurar la desconexión del pool y liberar la memoria RAM de los buffers de consulta.
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // Ciclo iterativo: Recorre las filas devueltas una por una hasta que el puntero llege al final del conjunto de datos.
            while (rs.next()) {
                // Instanciamos el objeto de negocio Premio y cargamos sus propiedades mediante encapsulamiento Setter.
                Premio p = new Premio();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                p.setActivo(rs.getBoolean("activo"));
                premios.add(p); // Añadimos la entidad cargada a la colección dinámica List.
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Registra en la consola de depuración el rastro del error.
        }
        return premios; // Retorna la lista con los objetos de negocio mapeados.
    }

    // ( CU29: Participar en sorteo postulándose manualmente )
    // Registra la intención del invitado de participar, previniendo duplicados mediante una consulta previa.
    public boolean participarEnSorteo(int idInvitado, int idPremio) {
        String checkSql = "SELECT id FROM participaciones_premios WHERE id_invitado = ? AND id_premio = ?";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement psCheck = con.prepareStatement(checkSql)) {
            psCheck.setInt(1, idInvitado);
            psCheck.setInt(2, idPremio);
            try (ResultSet rs = psCheck.executeQuery()) {
                // Control redundante: Si el cursor halla una coincidencia, frena el flujo para evitar que se anote dos veces al mismo sorteo.
                if (rs.next()) {
                    System.out.println("El invitado ya participó en este premio.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Ejecución del Insert: Si pasó el filtro de arriba, inserta la postulación seteando los estados iniciales por defecto.
        String insertSql = "INSERT INTO participaciones_premios (id_invitado, id_premio, elegible, ganador, voucher) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(insertSql)) {
            ps.setInt(1, idInvitado);
            ps.setInt(2, idPremio);
            ps.setBoolean(3, false); // Arranca como no elegible hasta que se verifiquen los datos.
            ps.setBoolean(4, false); // Arranca sin ganar por obvias razones.
            ps.setString(5, null);  // El campo alfanumérico del voucher queda nulo hasta la asignación.
            // Evalúa el entero devuelto por executeUpdate: si es mayor a cero significa que modificó filas y la operación fue exitosa.
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ( CU30: Verificar si el invitado cumple los requisitos de asistencia )
    // Evalúa si el registro del invitado cuenta con la bandera de asistencia confirmada en la tabla principal.
    public boolean esElegible(int idInvitado) {
        String sql = "SELECT asistencia_confirmada FROM invitados WHERE id = ?";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idInvitado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Retorna el estado booleano directo de la columna de la base de datos.
                    return rs.getBoolean("asistencia_confirmada");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ( CU31: Obtener o generar el voucher único si salió ganador de la postulación )
    // Busca un código existente o genera uno nuevo de forma dinámica si corresponde.
    public String obtenerVoucher(int idInvitado, int idPremio) {
        String selectSql = "SELECT voucher, ganador FROM participaciones_premios WHERE id_invitado = ? AND id_premio = ?";
        try (Connection con = ConexionController.getInstance().getConnection();
             // Operador de control ternario para prevenir excepciones por referencias nulas en el objeto Prepared Statement.
             PreparedStatement ps = selectSql != null ? con.prepareStatement(selectSql) : null) {
            ps.setInt(1, idInvitado);
            ps.setInt(2, idPremio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String voucherExistente = rs.getString("voucher");
                    // Evalúa la cadena: si el voucher ya fue creado y persistido con anterioridad, lo retorna directamente salteando la lógica de generación.
                    if (voucherExistente != null && !voucherExistente.isEmpty()) {
                        return voucherExistente;
                    }
                    
                    boolean esGanador = rs.getBoolean("ganador");
                    // Validación de negocio: si el usuario no ganó el sorteo del premio, la app bloquea la creación del código alfanumérico devolviendo null.
                    if (!esGanador) {
                        return null;
                    }
                    
                    // Si ganó y no tiene voucher, se dispara el método interno y se actualiza la tabla relacional.
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Encapsula la generación del token de validación truncando el string nativo de Java para dejar una clave limpia.
    private String generarVoucherUnico() {
        return "VCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // ( Método de Luca para sortear sobre los postulados de la tabla intermedia )
    // Ejecuta las queries concurrentes para validar existencias en stock, actualizar elegibilidad, seleccionar ganador aleatorio y descontar inventario.
    public boolean realizarSorteo(int idPremio) {
        String checkStock = "SELECT cantidad_disponible FROM premios WHERE id = ?";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement psCheck = con.prepareStatement(checkStock)) {
            psCheck.setInt(1, idPremio);
            try (ResultSet rsStock = psCheck.executeQuery()) {
                // Evaluación lógica: si el contador físico de stock es menor o igual a cero, se cancela el proceso de sorteo por falta de unidades.
                if (rsStock.next() && rsStock.getInt("cantidad_disponible") <= 0) {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Bloque Transaccional 1: Actualiza en lote a todos los invitados que se postularon originalmente poniéndolos en estado elegible (1).
        String updateElegibles = "UPDATE participaciones_premios SET elegible = ? WHERE id_premio = ? AND elegible = 0";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement psUpd = con.prepareStatement(updateElegibles)) {
            psUpd.setBoolean(1, true);
            psUpd.setInt(2, idPremio);
            psUpd.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Bloque Transaccional 2: Filtra los postulados aptos que todavía no ganaron nada y extrae una ID única al azar usando RAND() del motor MySQL.
        String seleccionarGanador = "SELECT id_invitado FROM participaciones_premios WHERE id_premio = ? AND elegible = 1 AND ganador = 0 ORDER BY RAND() LIMIT 1";
        try (Connection con = ConexionController.getInstance().getConnection();
             PreparedStatement psSel = con.prepareStatement(seleccionarGanador)) {
            psSel.setInt(1, idPremio);
            try (ResultSet rs = psSel.executeQuery()) {
                if (rs.next()) {
                    int idGanador = rs.getInt("id_invitado");
                    
                    // Bloque Transaccional 3: Modifica la fila intermedia del invitado sorteado para setear su bandera de ganador en 1.
                    String marcar = "UPDATE participaciones_premios SET ganador = 1 WHERE id_invitado = ? AND id_premio = ?";
                    try (PreparedStatement psGan = con.prepareStatement(marcar)) {
                        psGan.setInt(1, idGanador);
                        psGan.setInt(2, idPremio);
                        psGan.executeUpdate();
                    }
                    
                    // Bloque Transaccional 4: Resta una unidad de forma matemática de la columna inventario en la tabla premios.
                    String reducirStock = "UPDATE premios SET cantidad_disponible = cantidad_disponible - 1 WHERE id = ?";
                    try (PreparedStatement psStock = con.prepareStatement(reducirStock)) {
                        psStock.setInt(1, idPremio);
                        psStock.executeUpdate();
                    }
                    return true; // Retorna verdadero indicando el fin del flujo del sorteo con asignación exitosa.
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}