package org.example.rest;

import org.example.model.*;
import org.example.nivel.Avanzado;
import org.example.nivel.Intermedio;
import org.example.nivel.NivelState;
import org.example.nivel.Principiante;
import org.example.service.PartidoService;
import org.example.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/partidos")
@CrossOrigin(origins = "*")
public class PartidoRestController {

    private final PartidoService partidoService;
    private final UsuarioService usuarioService;
    // Deportes pre-cargados (en memoria simple)
    private static final List<Deporte> DEPORTES = Arrays.asList(
            new Deporte(1L, "Fútbol"),
            new Deporte(2L, "Básquet"),
            new Deporte(3L, "Tenis"),
            new Deporte(4L, "Vóley"),
            new Deporte(5L, "Paddle")
    );

    public PartidoRestController(PartidoService partidoService, UsuarioService usuarioService) {
        this.partidoService = partidoService;
        this.usuarioService = usuarioService;
    }

    // GET /api/partidos
    @GetMapping
    public List<Map<String, Object>> listarPartidos() {
        return partidoService.getPartidos().stream()
                .map(this::toMap)
                .collect(Collectors.toList());
    }

    // GET /api/partidos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPartido(@PathVariable Long id) {
        try {
            Partido p = partidoService.buscarPorId(id);
            return ResponseEntity.ok(toMap(p));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/partidos
    @PostMapping
    public ResponseEntity<?> crearPartido(@RequestBody Map<String, Object> body) {
        try {
            Long deporteId = Long.valueOf(body.get("deporteId").toString());
            Deporte deporte = DEPORTES.stream()
                    .filter(d -> d.getIdDeporte().equals(deporteId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Deporte no encontrado"));

            int cantJugadores  = Integer.parseInt(body.get("cantidadJugadores").toString());
            int duracion       = Integer.parseInt(body.get("duracionMinutos").toString());
            String ciudad      = body.get("ciudad").toString();
            double latitud     = Double.parseDouble(body.get("latitud").toString());
            double longitud    = Double.parseDouble(body.get("longitud").toString());
            LocalDateTime horario = LocalDateTime.parse(body.get("horario").toString());

            // creadorId is required – the logged-in user who creates the match
            Long creadorId = body.containsKey("creadorId")
                    ? Long.valueOf(body.get("creadorId").toString()) : null;

            Ubicacion ubicacion = new Ubicacion(latitud, longitud, ciudad);
            NivelState nivelMin = parseNivel(body.getOrDefault("nivelMinimo", "Principiante").toString());
            NivelState nivelMax = parseNivel(body.getOrDefault("nivelMaximo", "Avanzado").toString());

            Partido partido = partidoService.crearPartido(deporte, cantJugadores, duracion,
                    ubicacion, horario, nivelMin, nivelMax);
            partido.setCreadorId(creadorId);
            // persist the creadorId
            partidoService.guardar(partido);

            return ResponseEntity.ok(toMap(partido));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // POST /api/partidos/{id}/jugadores/{usuarioId}
    @PostMapping("/{id}/jugadores/{usuarioId}")
    public ResponseEntity<?> agregarJugador(@PathVariable Long id, @PathVariable Long usuarioId) {
        try {
            Partido partido = partidoService.buscarPorId(id);
            Usuario usuario = usuarioService.buscarPorId(usuarioId);
            partidoService.agregarJugador(partido, usuario);
            return ResponseEntity.ok(toMap(partido));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // PUT /api/partidos/{id}/confirmar-jugador/{usuarioId}
    @PutMapping("/{id}/confirmar-jugador/{usuarioId}")
    public ResponseEntity<?> confirmarJugador(@PathVariable Long id, @PathVariable Long usuarioId) {
        try {
            Partido partido = partidoService.buscarPorId(id);
            Usuario usuario = usuarioService.buscarPorId(usuarioId);
            partidoService.confirmarJugador(partido, usuario);
            return ResponseEntity.ok(toMap(partido));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // PUT /api/partidos/{id}/iniciar?creadorId=1
    @PutMapping("/{id}/iniciar")
    public ResponseEntity<?> iniciarPartido(@PathVariable Long id,
                                             @RequestParam(required = false) Long creadorId) {
        try {
            Partido partido = partidoService.buscarPorId(id);
            verificarPropietario(partido, creadorId);
            partidoService.iniciarPartido(partido);
            return ResponseEntity.ok(toMap(partido));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // PUT /api/partidos/{id}/finalizar?creadorId=1
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizarPartido(@PathVariable Long id,
                                               @RequestParam(required = false) Long creadorId) {
        try {
            Partido partido = partidoService.buscarPorId(id);
            verificarPropietario(partido, creadorId);
            partidoService.finalizarPartido(partido);
            return ResponseEntity.ok(toMap(partido));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // PUT /api/partidos/{id}/cancelar?creadorId=1
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPartido(@PathVariable Long id,
                                              @RequestParam(required = false) Long creadorId) {
        try {
            Partido partido = partidoService.buscarPorId(id);
            verificarPropietario(partido, creadorId);
            partidoService.cancelarPartido(partido);
            return ResponseEntity.ok(toMap(partido));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // GET /api/partidos/deportes
    @GetMapping("/deportes")
    public List<Map<String, Object>> listarDeportes() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Deporte d : DEPORTES) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", d.getIdDeporte());
            m.put("nombre", d.getNombre());
            result.add(m);
        }
        return result;
    }

    // --- helpers ---

    private Map<String, Object> toMap(Partido p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getIdPartido());
        m.put("deporte", p.getDeporte().getNombre());
        m.put("deporteId", p.getDeporte().getIdDeporte());
        m.put("estado", p.getEstado().getNombre());
        m.put("cantidadJugadores", p.getCantidadJugadores());
        m.put("jugadoresActuales", p.getJugadores().size());
        m.put("duracionMinutos", p.getDuracionMinutos());
        m.put("ubicacion", p.getUbicacion().getCiudad());
        m.put("latitud", p.getUbicacion().getLatitud());
        m.put("longitud", p.getUbicacion().getLongitud());
        m.put("horario", p.getHorario().toString());
        m.put("nivelMinimo", p.getNivelMinimo().getNombre());
        m.put("nivelMaximo", p.getNivelMaximo().getNombre());

        List<Map<String, Object>> jugadores = new ArrayList<>();
        for (Jugador j : p.getJugadores()) {
            Map<String, Object> jm = new LinkedHashMap<>();
            jm.put("id", j.getIdParticipante());
            jm.put("nombre", j.getJugador().getNombreUsuario());
            jm.put("confirmado", j.isConfirmacion());
            jugadores.add(jm);
        }
        m.put("jugadores", jugadores);
        m.put("creadorId", p.getCreadorId());  // needed by frontend for owner check
        return m;
    }

    private NivelState parseNivel(String nivel) {
        switch (nivel.toLowerCase()) {
            case "intermedio": return new Intermedio();
            case "avanzado":   return new Avanzado();
            default:           return new Principiante();
        }
    }

    /**
     * Throws SecurityException if the requesting user is not the match creator.
     * If creadorId is missing or the match has no creator set, access is allowed
     * (to avoid locking out matches created before this feature was added).
     */
    private void verificarPropietario(Partido partido, Long creadorId) {
        Long owner = partido.getCreadorId();
        if (owner == null || owner == 0 || creadorId == null) return; // no owner set → open
        if (!owner.equals(creadorId)) {
            throw new SecurityException("Solo el creador del partido puede realizar esta acción.");
        }
    }
}
