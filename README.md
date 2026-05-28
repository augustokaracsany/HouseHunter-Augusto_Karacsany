# HouseHunter 

## 📝 Temática del Proyecto
HouseHunter es un sistema de **gestión hotelera orientado a eventos corporativos**. La plataforma permite centralizar la interacción entre la administración del hotel, las empresas organizadoras (clientes que reservan el hotel completo) y los invitados/huéspedes. 

El sistema optimiza los flujos de control de acceso, cronogramas de actividades, asignación manual de habitaciones dobles y el registro de asistencia gamificado mediante un sistema de premios integrados.

## Equipo de Desarrollo
* **Augusto Abel Karacsany** (Backend, Arquitectura y BdD)
* **Martin Ignacio Waltar Zunino** (Backend y Controladores)
* **Luca Giménez Borrelli** (Frontend & UI)

---

## Arquitectura del Sistema
El proyecto está construido bajo una arquitectura limpia en **capas desacopladas**, garantizando la separación de responsabilidades:

* **`GUI` (Capa de Presentación):** Interfaces gráficas dinámicas basadas en `Swing` optimizadas para flujos de gestión rápidos y visualización de datos.
* **`BLL` (Business Logic Layer):** Clases entidad que contienen las reglas de negocio puras, validación de estados y manejo de enums (`Importancia`, `EstadoHabitacion`, `CategoriaActividad`).
* **`DLL` (Data Logic Layer / Controllers):** Controladores intermedios encargados de orquestar la comunicación con la base de datos, mapeo de objetos, transacciones atómicas y cálculos de métricas en tiempo real.
* **`Repository`:** Capa de persistencia enfocada en el aislamiento de las consultas a las tablas base de datos (`UsuariosRepository`, `ReservasRepository`) y algoritmos de seguridad de hashing.

---

## Estado Actual del Desarrollo

### Base de Datos e Infraestructura Core
* **Conexión Centralizada:** Implementation del patrón *Singleton* en `ConexionController` para una gestión eficiente y segura del pool de conexiones a MySQL.
* **Esquema Relacional (`househunter.sql`):** Estructura relacional sólida con restricciones de integridad referencial (`FOREIGN KEY`) y borrados en cascada (`ON DELETE CASCADE`) para mantener la consistencia de los datos en transacciones concurrentes.

### Módulo de Autenticación & Seguridad
* **Hashing de Contraseñas:** Seguridad del lado del servidor implementada mediante el algoritmo de encriptación **BCrypt** para mitigar ataques de diccionario en la tabla de credenciales.
* **Hidratación Dinámica de Sesión:** El sistema detecta el rol del usuario (`ADMINISTRADOR`, `EMPRESA`, `INVITADO`) en el login. Si ingresa una empresa (ej: Globant), el sistema acopla en memoria automáticamente los datos de su evento activo (`GLOBANT-2026`) y su cronograma sin requerir búsquedas manuales intermedias.

### Estructura de Paneles por Roles
* **Panel de Empresa (Cliente Corporativo):** Gestión automatizada de la reserva hotelera. Permite la carga e importación de la `lista_invitados_previa`, visualización de cronogramas y acceso a un **módulo de analíticas en tiempo real** que contrasta las expectativas de asistencia contra las asignaciones físicas de habitaciones.
* **Panel de Administrador del Hotel:** Control de check-in mediante DNI, gestión incremental del estado de habitaciones, asignación y persistencia de cronogramas con transacciones seguras y control de asistencia a actividades.
* **Panel de Invitado / Huésped:** Interfaz simplificada para la consulta del número de habitación y acompañante asignado, visualización del itinerario de actividades diarias y control de cupones para el sorteo de premios.

---

## Reglas de Negocio Clave (Implementadas en Código)
* **Lógica Incremental de Check-in:** Las habitaciones son dobles y transicionan de estado de forma dinámica mediante la concurrencia en la recepción: `Libre` ➡️ `Half` (al ingresar el primer huésped) ➡️ `Completa` (al completarse el cupo por su compañero asignado).
* **Módulo de Analítica Avanzado:** El `ReporteController` unifica mediante estructuras de datos clave-valor (`Map`) consultas paralelas para calcular totales esperados, confirmados reales en base a ocupación de camas, y porcentajes de confirmación con truncamiento decimal limpio evitando excepciones aritméticas de división por cero.
* **Gamaficación de Premios:** El sistema audita de forma lineal las asistencias perfectas a actividades catalogadas bajo la etiqueta de importancia `ALTA`. Al cumplirse la condición, el motor habilita al usuario como candidato apto para la obtención de premios al final de la jornada.

---

## 🗺️ Estructura del Árbol de Navegación (Flujo de UI)

A continuación se detalla el mapa de navegación jerárquico del sistema, mapeado directamente desde los componentes dinámicos de la interfaz gráfica (`Swing`):

```text
[ ARRANQUE DE LA APLICACIÓN ] (GUI.Main)
 │
 └── 👤 Menú Principal (HouseHunter Principal)
      ├── 📝 Registrarse (Creación de nuevos usuarios con rol básico)
      ├── ➡️ Iniciar Sesión (Validación y bifurcación según rol asignado)
      │     │
      │     ├─ 🛡️ 1. ROL: ADMINISTRADOR ──► [ PANEL DE ADMINISTRADOR ] (Panel de Recepción)
      │     │                                │
      │     │                                ├── 🛏️ [ BOTÓN: RECEPCIÓN ] (Módulo de Recepción)
      │     │                                │     ├── Registrar Check-In (Validación y asignación física)
      │     │                                │     ├── Monitorear Habitaciones (Control de estados: Libre ➔ Half ➔ Completa)
      │     │                                │     └── Volver (Regresa al Panel de Administrador)
      │     │                                │
      │     │                                ├── 🎯 [ BOTÓN: ACTIVIDADES ] (Control de Eventos)
      │     │                                │     ├── Monitorear actividades (Visualización del itinerario global)
      │     │                                │     ├── Visualizar cronograma (Filtros por jornada)
      │     │                                │     ├── Actualizar estado (Toggle de ejecución en salas)
      │     │                                │     ├── Entregar premio (Motor de sorteo basado en asistencia ALTA)
      │     │                                │     └── Volver (Regresa al Panel de Administrador)
      │     │                                │
      │     │                                ├── 📊 [ BOTÓN: REPORTES ] (Módulo de Reportes Admin)
      │     │                                │     ├── Generar reporte de evento (Auditoría cruzada de métricas)
      │     │                                │     └── Volver (Regresa al Panel de Administrador)
      │     │                                │
      │     │                                └── ❌ [ BOTÓN: CERRAR SESIÓN ] (Retorno al Login Principal)
      │     │
      │     ├── 🏢 2. ROL: EMPRESA ──► [ PANEL DE EMPRESA ] (Manejo de Sesión Hidratada)
      │     │                            │
      │     │                            ├── 📂 [ BOTÓN: GESTIÓN DE EVENTO ] (Submenú Eventos)
      │     │                            │     ├── Realizar Reserva (Confirmación de fechas y asistentes)
      │     │                            │     ├── Cargar Invitados (Volcado manual a persistencia)
      │     │                            │     ├── Seleccionar Plantilla (src/img/ assets visuales)
      │     │                            │     └── Volver (Regresa al Panel de Empresa)
      │     │                            │
      │     │                            ├── 📅 [ BOTÓN: PLANIFICACIÓN ] (Submenú Planificación)
      │     │                            │     ├── Crear actividad (Especificar categoría, combo de opciones y hora)
      │     │                            │     ├── Asignar importancia (ALTA / MEDIA / BAJA)
      │     │                            │     ├── Guardar cronograma (Confirmar transacción atómica)
      │     │                            │     └── Volver (Regresa al Panel de Empresa)
      │     │                            │
      │     │                            ├── 📧 [ BOTÓN: INVITACIONES ] (Submenú Invitaciones)
      │     │                            │     ├── Importar nómina previa (lista_invitados_previa)
      │     │                            │     ├── Enviar notificaciones (Token único sim/envío)
      │     │                            │     └── Volver (Regresa al Panel de Empresa)
      │     │                            │
      │     │                            ├── 📊 [ BOTÓN: REPORTES ] (Submenú Reportes)
      │     │                            │     ├── Ver estadísticas (Lanzamiento del JOptionPane analítico)
      │     │                            │     ├── Exportar (simular) (Volcado local de métricas)
      │     │                            │     └── Volver (Regresa al Panel de Empresa)
      │     │                            │
      │     │                            └── ❌ [ BOTÓN: CERRAR SESIÓN ] (Retorno limpio al Login)
      │     │
      │     └── 👥 3. ROL: INVITADO ──► [ PANEL DE INVITADO ] (Vistas de autogestión de huéspedes)
      │                                  │
      │                                  ├── 📅 [ BOTÓN ] Ver cronograma completo (Itinerario estructurado)
      │                                  ├── ✔️ [ BOTÓN ] Confirmar mi asistencia (Registro dinámico)
      │                                  ├── 🎯 [ BOTÓN ] Explorar actividades (Mapeo relacional de opciones)
      │                                  ├── 🔍 [ BOTÓN ] Ver detalle de una actividad (Metadatos de sala)
      │                                  ├── 🛏️ [ BOTÓN ] Consultar mi habitación (Mapeo de cuarto y compañero)
      │                                  ├── 🎟️ [ BOTÓN ] Obtener voucher (Cupones activos y gamificación de premios)
      │                                  └── ❌ [ BOTÓN ] Cerrar Sesión (Desacople de sesión y retorno)
      │
      └── ❌ Botón Salir