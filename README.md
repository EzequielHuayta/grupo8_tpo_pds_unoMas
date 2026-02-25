# ⚽ UnoMás – Sistema de Gestión de Encuentros Deportivos

> **TPO – Proceso de Desarrollo de Software – ADOO**
> Grupo 8 | Entrega: 24/02/2026

---

## 📋 Descripción

**UnoMás** es una aplicación web para organizar encuentros deportivos. Permite a los usuarios registrarse, crear partidos de distintos deportes (Fútbol, Básquet, Tenis, Vóley, Paddle), buscar partidos donde falten jugadores y recibir notificaciones en tiempo real sobre el estado de sus partidos.

## 🛠 Tecnologías

| Componente | Tecnología |
|---|---|
| **Backend** | Java 17 · Spring Boot |
| **Frontend** | React · Vite |
| **Persistencia** | Archivos de texto (`data/*.txt`) con encoding UTF-8 |
| **Email** | Spring Mail (JavaMail) |

## 🏗 Arquitectura

El sistema sigue el patrón arquitectónico **MVC**:

```
┌──────────────────────────────────────────────────────────────┐
│  FRONTEND (React)                                            │
└────────────────────── HTTP REST API ─────────────────────────┘
                           │
┌──────────────────────────┼───────────────────────────────────┐
│  BACKEND (Spring Boot)   │                                   │
│                          ▼                                   │
│  ┌─────────────────────────────────────┐                     │
│  │         CONTROLLERS                 │                     │
│  │  PartidoController                  │                     │
│  │  UsuarioController                  │                     │
│  │  DeporteController                  │                     │
│  └─────────────┬───────────────────────┘                     │
│                │                                             │
│  ┌─────────────▼───────────────────────┐                     │
│  │          SERVICES                   │                     │
│  │  PartidoService · UsuarioService    │                     │
│  │  DeporteService · PartidoScheduler  │                     │
│  └─────────────┬───────────────────────┘                     │
│                │                                             │
│  ┌─────────────▼───────────────────────┐                     │
│  │        REPOSITORIES                 │                     │
│  │  IPartidoRepository ← Impl         │                     │
│  │  IUsuarioRepository ← Impl         │                     │
│  └─────────────────────────────────────┘                     │
└──────────────────────────────────────────────────────────────┘
```

## 🎨 Patrones de Diseño (4 implementados)

### 1. Strategy
- **Emparejamiento**: `IEmparejadorStrategy` → `EmparejadorNivelStrategy`, `EmparejadorUbicacionStrategy`, `EmparejadorHistorialStrategy`
- **Notificaciones**: `INotificacionStrategy` → `EmailNotificacionStrategy`, `FirebaseNotificacionStrategy`

### 2. Adapter
- `IAdapterEmail` → `AdapterJavaEmail` adapta `JavaMailSender` de Spring a la interfaz propia del sistema.

### 3. State
- `IPartidoState` → 6 estados: `NecesitamosJugadoresState` → `ArmadoState` → `ConfirmadoState` → `EnJuegoState` → `FinalizadoState` | `CanceladoState`

### 4. Observer
- `ISubject` / `IObserver`: `Partido` (subject) notifica automáticamente a `Usuario` y `Deporte` (observers) cuando cambia de estado.


## 📂 Estructura del Proyecto

```
grupo8_tpo_pds_unoMas/
├── src/main/java/org/example/
│   ├── controllers/          # Capa Controller (MVC)
│   │   ├── PartidoController.java
│   │   ├── UsuarioController.java
│   │   └── DeporteController.java
│   ├── model/                # Capa Model
│   │   ├── Partido.java      # Subject (Observer) + Context (State)
│   │   ├── Usuario.java      # Observer + Context (Strategy)
│   │   ├── Jugador.java
│   │   ├── Deporte.java      # Observer
│   │   ├── Ubicacion.java
│   │   └── Notificacion.java
│   ├── state/                # Patrón State
│   │   ├── IPartidoState.java
│   │   ├── NecesitamosJugadoresState.java
│   │   ├── ArmadoState.java
│   │   ├── ConfirmadoState.java
│   │   ├── EnJuegoState.java
│   │   ├── FinalizadoState.java
│   │   └── CanceladoState.java
│   ├── strategy/             # Patrón Strategy (Emparejamiento)
│   │   ├── IEmparejadorStrategy.java
│   │   ├── EmparejadorNivelStrategy.java
│   │   ├── EmparejadorUbicacionStrategy.java
│   │   └── EmparejadorHistorialStrategy.java
│   ├── notification/         # Patrón Strategy (Notificaciones) + Adapter
│   │   ├── INotificacionStrategy.java
│   │   ├── EmailNotificacionStrategy.java
│   │   ├── FirebaseNotificacionStrategy.java
│   │   ├── IAdapterEmail.java
│   │   ├── AdapterJavaEmail.java
│   │   └── InAppNotificacionStore.java
│   ├── observer/             # Patrón Observer
│   │   ├── IObserver.java
│   │   └── ISubject.java
│   ├── nivel/                # Niveles de juego (State pattern)
│   │   ├── NivelState.java
│   │   ├── Principiante.java
│   │   ├── Intermedio.java
│   │   └── Avanzado.java
│   ├── service/              # Capa de Servicios
│   │   ├── PartidoService.java
│   │   ├── UsuarioService.java
│   │   ├── DeporteService.java
│   │   └── PartidoScheduler.java
│   └── repository/           # Capa de Persistencia
│       ├── IPartidoRepository.java
│       ├── PartidoRepositoryImpl.java
│       ├── IUsuarioRepository.java
│       └── UsuarioRepositoryImpl.java
├── frontend/                 # Capa View (MVC)
│   └── src/
│       ├── App.jsx
│       ├── api.js
│       └── components/
│           ├── PartidosList.jsx
│           ├── UsuariosList.jsx
│           ├── BuscarPartidos.jsx
│           ├── CreatePartidoModal.jsx
│           ├── Dashboard.jsx
│           └── ...
├── docs/
│   └── TPO_Patrones_de_Diseno.html   # Documento de patrones (imprimible a PDF)
└── data/                     # Datos persistidos
    ├── usuarios.txt
    └── partidos.txt
```

## 🚀 Ejecución

### Prerrequisitos
- Java 17+
- Node.js 18+
- Maven

### Backend
```bash
# Desde la raíz del proyecto
./mvnw spring-boot:run
```
El servidor arranca en `http://localhost:8080`

### Frontend
```bash
cd frontend
npm install
npm run dev
```
La app abre en `http://localhost:5173`

## 📖 Funcionalidades

| # | Funcionalidad | Descripción |
|---|---|---|
| 1 | **Registro** | Nombre, email, contraseña, deporte favorito y nivel opcionales |
| 2 | **Búsqueda de partidos** | Por nivel, ubicación o historial |
| 3 | **Creación de partido** | Deporte, jugadores, duración, ubicación, horario, rango de nivel |
| 4 | **Ciclo de vida** | Necesitamos jugadores → Armado → Confirmado → En juego → Finalizado / Cancelado |
| 5 | **Emparejamiento** | 3 algoritmos: nivel de habilidad, cercanía geográfica, historial |
| 6 | **Notificaciones** | Email (JavaMail) e In-App (push simulado), en cada cambio de estado |
| 7 | **Transiciones automáticas** | Scheduler cada 60s: Confirmado → En juego, En juego → Finalizado |
