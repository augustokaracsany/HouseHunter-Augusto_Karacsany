package GUI;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import BLL.Actividad;
import BLL.Empresa;
import BLL.EmpresaService;
import BLL.Importancia;
import BLL.CategoriaActividad;
import BLL.Invitado;
import BLL.Plantilla;
import BLL.Reserva;
import BLL.Rol;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmpresaMenu {
    private Empresa empresa;
    private EmpresaService service;

    public EmpresaMenu(Empresa empresa) {
        this.empresa = empresa;
        this.service = new EmpresaService(empresa);
    }

    public void mostrarMenuPrincipal() {
        ImageIcon iconoMenu = new ImageIcon("src/img/HouseHunter_Menu-Empresa.png");
        
        // Modificado para mostrar dinámicamente cuál reserva está operando actualmente
        String reservaActivaTexto = (empresa.getReservaActual() != null) 
            ? "<br><b style='color:green;'>Reserva Activa: ID " + empresa.getReservaActual().getId() + "</b>" 
            : "<br><b style='color:red;'>Sin reserva activa seleccionada</b>";

        String tituloMenu = "<html><body style='width: 300px; text-align: center;'>"
                          + "<h2>🏢 Panel de Empresa</h2>"
                          + "<b>Entidad:</b> " + empresa.getNombre() 
                          + reservaActivaTexto
                          + "<hr>Seleccione un módulo de gestión:</body></html>";

        // 🟢 AGREGADO: Nueva opción "🔄 CAMBIAR DE RESERVA"
        String[] modulos = {
            "📁 GESTIÓN DE EVENTO", 
            "📅 PLANIFICACIÓN", 
            "📧 INVITACIONES", 
            "📊 REPORTES", 
            "🔄 CAMBIAR DE RESERVA", 
            "❌ CERRAR SESIÓN"
        };

        int seleccion;
        do {
            seleccion = JOptionPane.showOptionDialog(
                null, tituloMenu, "HouseHunter v1.0",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                iconoMenu, modulos, modulos[0]
            );

            switch (seleccion) {
                case 0: subMenuGestionEvento(); break;
                case 1: subMenuCronograma(); break; 
                case 2: subMenuInvitaciones(); break; 
                case 3: subMenuReportes(); break;
                case 4: cambiarReservaManual(); break; // 🟢 AGREGADO: Llama al selector interactivo
                case 5: JOptionPane.showMessageDialog(null, "Cerrando sesión de " + empresa.getNombre()); break;
            }
        } while (seleccion != 5 && seleccion != -1); // Ajustado el flag de salida a 5 por el nuevo botón
    }

    // 🟢 AGREGADO: Método para cambiar entre reservas en caliente
    private void cambiarReservaManual() {
        // Forzamos a borrar la reserva de la memoria temporal para que el asistente la vuelva a pedir
        empresa.setReservaActual(null); 
        
        if (asegurarReservaSeleccionada()) {
            JOptionPane.showMessageDialog(null, 
                "🔄 Cambiaste con éxito a la reserva ID: " + empresa.getReservaActual().getId() + 
                "\nAhora todos los módulos (Invitados, Cronogramas, Reportes) leerán estos datos.", 
                "Cambio de Reserva", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    // =========================================================================
    // 📁 GESTIÓN DE EVENTO
    // =========================================================================
    private void subMenuGestionEvento() {
        ImageIcon iconoGestion = new ImageIcon("src/img/HouseHunter_Menu-Empresa_Gestion.png");
        String[] opciones = {"Realizar Reserva", "Cargar Invitados", "Seleccionar Plantilla", "Volver"};
        
        int op = JOptionPane.showOptionDialog(
            null, "<html><body style='width:250px; text-align:center;'><h3>Módulo de Eventos</h3>Seleccione una acción:</body></html>", 
            "Gestión", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
            iconoGestion, opciones, opciones[0]
        );
        
        if (op == 0) {
            realizarReserva();
        } else if (op == 1) {
            cargarInvitadosMasivo();
        } else if (op == 2) {
            seleccionarPlantilla();
        }
    }

    private void realizarReserva() {
        String fechaInicioStr = JOptionPane.showInputDialog(null, "Fecha de Inicio / Check-In (YYYY-MM-DD):", "Nueva Reserva", JOptionPane.QUESTION_MESSAGE);
        if (fechaInicioStr == null) return; 
        
        String fechaFinStr = JOptionPane.showInputDialog(null, "Fecha de Fin / Check-Out (YYYY-MM-DD):", "Nueva Reserva", JOptionPane.QUESTION_MESSAGE);
        if (fechaFinStr == null) return; 

        try {
            LocalDate fechaInicio = LocalDate.parse(fechaInicioStr); 
            LocalDate fechaFin = LocalDate.parse(fechaFinStr);
            
            if (fechaFin.isBefore(fechaInicio)) {
                JOptionPane.showMessageDialog(null, "❌ La fecha de fin no puede ser anterior a la de inicio.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String numInvStr = JOptionPane.showInputDialog(null, "Número estimado de invitados:", "Cantidad", JOptionPane.QUESTION_MESSAGE);
            if (numInvStr == null) return;
            int numInvitados = Integer.parseInt(numInvStr);

            if (service.procesarReserva(fechaInicio, fechaFin, numInvitados)) {
                JOptionPane.showMessageDialog(null, "✅ Reserva creada exitosamente.\nID: " + empresa.getReservaActual().getId());
            } else {
                JOptionPane.showMessageDialog(null, "❌ No hay disponibilidad para el rango de fechas seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Datos inválidos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarInvitadosMasivo() {
        if (!asegurarReservaSeleccionada()) return; 

        String datos = JOptionPane.showInputDialog(null,
                "Ingrese los invitados en el siguiente formato:\n"
                + "nombre,email,dni,telefono\n"
                + "Separe cada invitado con punto y coma (;)\n\n"
                + "Ejemplo:\n"
                + "Juan Perez,juan@mail.com,12345678,555-1234;Maria Gomez,maria@mail.com,87654321,555-5678",
                "Carga Masiva de Invitados", JOptionPane.QUESTION_MESSAGE);
        if (datos == null || datos.trim().isEmpty()) return;

        String[] lineas = datos.split(";"); 
        List<Invitado> lista = new ArrayList<>();
        int errores = 0;

        for (String linea : lineas) {
            String[] campos = linea.split(","); 
            if (campos.length < 3) { 
                errores++;
                continue; 
            }
            String nombre = campos[0].trim();
            String email = campos[1].trim();
            String dni = campos[2].trim();
            String telefono = (campos.length > 3) ? campos[3].trim() : ""; 

            Invitado inv = new Invitado(email, "", nombre, Rol.INVITADO);
            inv.setDni(dni);
            inv.setTelefono(telefono);

            if (service.validarInvitado(inv)) {
                lista.add(inv);
            } else {
                errores++;
            }
        }

        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay invitados válidos para cargar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (service.guardarInvitados(lista)) {
            JOptionPane.showMessageDialog(null, "✅ Se cargaron " + lista.size() + " invitados.\n❌ " + errores + " registros inválidos.");
        } else {
            JOptionPane.showMessageDialog(null, "Error al guardar los invitados.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarPlantilla() {
        if (!asegurarReservaSeleccionada()) return;

        List<Plantilla> plantillas = service.obtenerPlantillas();
        if (plantillas.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay plantillas disponibles.");
            return;
        }

        String[] nombres = plantillas.stream().map(Plantilla::getNombre).toArray(String[]::new);
        int sel = JOptionPane.showOptionDialog(null, "Seleccione una plantilla para el evento:", "Plantillas",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, nombres, nombres[0]);
        if (sel >= 0) {
            Plantilla p = plantillas.get(sel);
            if (service.asignarPlantilla(p.getId())) {
                JOptionPane.showMessageDialog(null, "Plantilla '" + p.getNombre() + "' asignada correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "Error al asignar plantilla.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // 📅 PLANIFICACIÓN / CRONOGRAMA
    // =========================================================================
    private void subMenuCronograma() {
        if (!asegurarReservaSeleccionada()) return;

        ImageIcon iconoPlanif = new ImageIcon("src/img/HouseHunter_Menu-Empresa_Planificacion.png");
        String[] sub = {"Crear actividad", "Asignar importancia", "Guardar cronograma", "Volver"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(
                null, "<html><body style='width:250px; text-align:center;'><h3>Cronograma de Actividades</h3></body></html>", 
                "Submenú Planificación", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
                iconoPlanif, sub, sub[0]
            );
            switch (op) {
                case 0: crearActividad(); break;
                case 1: asignarImportancia(); break;
                case 2: guardarCronograma(); break;
            }
        } while (op != 3 && op != -1);
    }

    private void crearActividad() {
        String nombre = JOptionPane.showInputDialog("Nombre de la actividad:");
        if (nombre == null) return;
        
        String horaStr = JOptionPane.showInputDialog("Hora de la actividad (HH:MM o HH:MM:SS):");
        if (horaStr == null) return;
        
        String desc = JOptionPane.showInputDialog("Descripción de la actividad:");
        if (desc == null) desc = ""; 
        
        String importanciaStr = (String) JOptionPane.showInputDialog(null, "Importancia:", "Importancia",
                JOptionPane.QUESTION_MESSAGE, null, new String[]{"BAJA","MEDIA","ALTA"}, "MEDIA");
        if (importanciaStr == null) return;
        
        CategoriaActividad categoriaSeleccionada = (CategoriaActividad) JOptionPane.showInputDialog(null, "Categoría:", "Categoría",
                JOptionPane.QUESTION_MESSAGE, null, CategoriaActividad.values(), CategoriaActividad.CHARLAS);
        String categoria = (categoriaSeleccionada != null) ? categoriaSeleccionada.toString() : "OTROS";

        try {
            String[] partes = horaStr.trim().split(":");
            String horaFormateada = "";
            if (partes.length == 2) {
                horaFormateada = partes[0] + ":" + partes[1] + ":00";
            } else if (partes.length == 3) {
                horaFormateada = horaStr.trim();
            } else {
                throw new Exception("Formato de hora inválido. Use HH:MM");
            }
            
            if (service.guardarActividad(nombre, desc, importanciaStr, categoria, horaFormateada)) {
                JOptionPane.showMessageDialog(null, "✅ Actividad agregada al cronograma con éxito.");
            } else {
                JOptionPane.showMessageDialog(null, "❌ Error al insertar la actividad en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "❌ Error en datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void asignarImportancia() {
        List<Actividad> actividades = service.obtenerActividades();
        if (actividades.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay actividades aún. Cree una primero.");
            return;
        }
        String[] nombresActs = actividades.stream().map(Actividad::getNombre).toArray(String[]::new);
        int sel = JOptionPane.showOptionDialog(null, "Seleccione la actividad a modificar:", "Asignar importancia",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, nombresActs, nombresActs[0]);
        if (sel < 0) return;
        
        Actividad act = actividades.get(sel);
        String nuevaImp = (String) JOptionPane.showInputDialog(null, "Nueva importancia:", "Importancia",
                JOptionPane.QUESTION_MESSAGE, null, new String[]{"BAJA","MEDIA","ALTA"}, act.getImportancia().toString());
        if (nuevaImp != null) {
            act.setImportancia(Importancia.valueOf(nuevaImp));
            
            // Refrescamos y actualizamos la lista completa mediante el Service
            List<Actividad> todas = service.obtenerActividades();
            for (Actividad a : todas) {
                if (a.getId() == act.getId()) a.setImportancia(act.getImportancia());
            }
            
            if (service.actualizarCronograma(todas)) {
                JOptionPane.showMessageDialog(null, "Importancia actualizada.");
            } else {
                JOptionPane.showMessageDialog(null, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardarCronograma() {
        List<Actividad> actividades = service.obtenerActividades();
        if (actividades.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay actividades para guardar.");
            return;
        }
        JOptionPane.showMessageDialog(null, "Cronograma guardado correctamente.\nTotal actividades: " + actividades.size());
    }

    // =========================================================================
    // 📧 INVITACIONES
    // =========================================================================
 // =========================================================================
    // 📧 INVITACIONES (ENROQUE DE FLUJO CORREGIDO)
    // =========================================================================
    private void subMenuInvitaciones() {
        if (!asegurarReservaSeleccionada()) return;

        ImageIcon iconoInvit = new ImageIcon("src/img/HouseHunter_Menu-Empresa_Invitaciones.png");
        String textMenu = "<html><body style='width:250px; text-align:center;'><h3>Envío de Invitaciones</h3></body></html>";
        
        // 🔄 ENROQUE: Intercambiamos el orden y sentido de los botones para coincidir con la BD
        String[] sub = {"Listar invitados", "Generar tokens de acceso", "Enviar notificaciones por correo", "Volver"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(
                null, textMenu, "Submenú Invitaciones", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
                iconoInvit, sub, sub[0]
            );
            switch (op) {
                case 0: 
                    listarInvitados(); 
                    break;
                    
                case 1: 
                    // Ahora este botón ejecuta la lógica real que genera los tokens en la base de datos
                    if (service.enviarNotificaciones()) {
                        JOptionPane.showMessageDialog(null, "✅ Tokens generados exitosamente en la base de datos.\nRevise la consola para validar las claves generadas.");
                    } else {
                        JOptionPane.showMessageDialog(null, "❌ Error al generar tokens o no hay invitados en esta reserva.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                    
                case 2: 
                    // Este botón pasa a ser el simulador visual puro para el cliente corporativo
                    JOptionPane.showMessageDialog(null, "📩 Notificaciones enviadas correctamente.\nTodos los invitados han recibido su correo con el token de acceso correspondientemente.", "Simulador de Envíos", JOptionPane.INFORMATION_MESSAGE);
                    break;
            }
        } while (op != 3 && op != -1);
    }
    
    private void listarInvitados() {
        List<Invitado> invitados = service.obtenerInvitados();
        if (invitados.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay invitados cargados.");
            return;
        }
        StringBuilder sb = new StringBuilder("📋 Invitados:\n");
        for (Invitado i : invitados) {
            sb.append("- ").append(i.getNombre())
              .append(" | Email: ").append(i.getEmail())
              .append(" | Token: ").append(i.getTokenAcceso())
              .append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    private void enviarNotificaciones() {
        if (service.enviarNotificaciones()) {
            JOptionPane.showMessageDialog(null, "✅ Notificaciones enviadas (simulado).\nRevise la consola para ver los tokens.");
        } else {
            JOptionPane.showMessageDialog(null, "❌ No hay invitados cargados para notificar en esta reserva.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    // =========================================================================
    // 📊 REPORTES
    // =========================================================================
    private void subMenuReportes() {
        if (!asegurarReservaSeleccionada()) return;

        ImageIcon iconoReportes = new ImageIcon("src/img/HouseHunter_Menu-Empresa_Reportes.png");
        String[] sub = {"Ver estadísticas", "Exportar (simular)", "Volver"};
        int op = JOptionPane.showOptionDialog(
            null, "<html><body style='width:250px; text-align:center;'><h3>Reportes Corporativos</h3></body></html>", 
            "Reportes", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
            iconoReportes, sub, sub[0]
        );
        if (op == 0) {
            mostrarEstadisticas();
        } else if (op == 1) {
            JOptionPane.showMessageDialog(null, "Exportación a PDF/Excel (simulada).");
        }
    }

    private void mostrarEstadisticas() {
        Map<String, Object> stats = service.obtenerEstadisticas();
        String mensaje = String.format(
            "📊 REPORTE DEL EVENTO ID %d\n\n" +
            "Total invitados: %d\n" +
            "Confirmados: %d\n" +
            "Porcentaje confirmación: %.2f%%\n" +
            "Total actividades programadas: %d",
            empresa.getReservaActual().getId(),
            stats.getOrDefault("totalInvitados", 0), 
            stats.getOrDefault("confirmados", 0),
            stats.getOrDefault("porcentajeConfirmacion", 0.0),
            stats.getOrDefault("totalActividades", 0)
        );
        JOptionPane.showMessageDialog(null, mensaje);
    }

    // =========================================================================
    // 🛠️ CONTROL DE ESTADO DE SELECCIÓN
    // =========================================================================
    private boolean asegurarReservaSeleccionada() {
        if (empresa.getReservaActual() != null) return true;
        
        List<Reserva> reservas = service.obtenerReservas();
        if (reservas.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay reservas para esta empresa.\nPrimero debe crear una reserva.");
            return false;
        }
        
        String[] opciones = reservas.stream()
            .map(r -> "ID " + r.getId() + " - [" + r.getFechaInicio() + " al " + r.getFechaFin() + "]")
            .toArray(String[]::new);
            
        int sel = JOptionPane.showOptionDialog(null, "Seleccione la reserva a gestionar:", "Reservas",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (sel >= 0) {
            empresa.setReservaActual(reservas.get(sel));
            return true;
        }
        return false;
    }
}