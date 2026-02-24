package org.example.controllers;

import org.example.model.*;
import org.example.nivel.Avanzado;
import org.example.nivel.Intermedio;
import org.example.nivel.NivelState;
import org.example.nivel.Principiante;
import org.example.service.PartidoService;
import org.example.service.UsuarioService;
import org.example.service.DeporteService;
import org.example.strategy.EmparejadorHistorialStrategy;
import org.example.strategy.EmparejadorNivelStrategy;
import org.example.strategy.EmparejadorUbicacionStrategy;
import org.example.strategy.IEmparejadorStrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/partidos")
@CrossOrigin(origins = "*")
public class PartidoController {

    private final PartidoService partidoService;
    private final UsuarioService usuarioService;
    private final DeporteService deporteService;

    private static final String BARRIOS_FILE = "data/barrios.txt";

    private static final List<String> BARRIOS = Arrays.asList(
            "Agronomía", "Almagro", "Balvanera", "Barracas", "Belgrano",
            "Boedo", "Caballito", "Chacarita", "Coghlan", "Colegiales",
            "Constitución", "Flores", "Floresta", "La Boca", "La Paternal",
            "Liniers", "Mataderos", "Monte Castro", "Monserrat", "Nueva Pompeya",
            "Núñez", "Palermo", "Parque Avellaneda", "Parque Chacabuco",
            "Parque Chas", "Parque Patricios", "Paternal", "Puerto Madero",
            "Recoleta", "Retiro", "Saavedra", "San Cristóbal", "San Nicolás",
            "San Telmo", "Versalles", "Villa Crespo", "Villa del Parque",
            "Villa Devoto", "Villa General Mitre", "Villa Lugano", "Villa Luro",
            "Villa Ortúzar", "Villa Pueyrredón", "Villa Real", "Villa Riachuelo",
            "Villa Santa Rita", "Villa Soldati", "Villa Urquiza", "Vélez Sársfield");

    public PartidoController(PartidoService partidoService, UsuarioService usuarioService,
            DeporteService deporteService) {
        this.partidoService = partidoService;
        this.usuarioService = usuarioService;
        this.deporteService = deporteService;
    }

    @PostConstruct
    public void inicializar() {
        // Sincronizar el txt con la lista hardcodeada (siempre sobreescribe para
        // mantener UTF-8)
        new File("data").mkdirs();
        try (PrintWriter pw = new PrintWriter(
                new java.io.OutputStreamWriter(
                        new java.io.FileOutputStream(BARRIOS_FILE), java.nio.charset.StandardCharsets.UTF_8))) {
            for (String b : BARRIOS)
                pw.println(b);
            System.out.println("[BarriosRepo] " + BARRIOS.size() + " barrios disponibles.");
        } catch (IOException e) {
            System.err.println("[BarriosRepo] Error al escribir: " + e.getMessage());
        }
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
            Deporte deporte = deporteService.buscarPorId(deporteId);

            int cantJugadores = Integer.parseInt(body.get("cantidadJugadores").toString());
            int duracion = Integer.parseInt(body.get("duracionMinutos").toString());
            String barrio = body.get("barrio").toString();
            LocalDateTime horario = LocalDateTime.parse(body.get("horario").toString());

            Long creadorId = body.containsKey("creadorId")
                    ? Long.valueOf(body.get("creadorId").toString())
                    : null;

            Ubicacion ubicacion = new Ubicacion(barrio);
            NivelState nivelMin = parseNivel(body.getOrDefault("nivelMinimo", "Principiante").toString());
            NivelState nivelMax = parseNivel(body.getOrDefault("nivelMaximo", "Avanzado").toString());

            Partido partido = partidoService.crearPartido(deporte, cantJugadores, duracion,
                    ubicacion, horario, nivelMin, nivelMax);
            partido.setCreadorId(creadorId);
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
            // Registrar partido en historial del jugador y persistirlo
            usuario.agregarPartidoAlHistorial(partido);
            usuarioService.guardar(usuario);
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

    // GET /api/partidos/buscar?usuarioId=1&estrategia=NIVEL
    // estrategia: NIVEL | UBICACION | HISTORIAL
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPartidos(
            @RequestParam Long usuarioId,
            @RequestParam(defaultValue = "NIVEL") String estrategia) {
        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioId);

            // Solo partidos disponibles (aceptan jugadores) y donde el usuario aún no está
            // inscripto
            List<Partido> disponibles = partidoService.getPartidos().stream()
                    .filter(p -> p.getEstado().getNombre().equals("Necesitamos jugadores"))
                    .filter(p -> p.getJugadores().stream()
                            .noneMatch(j -> j.getJugador().getIdUsuario().equals(usuarioId)))
                    .filter(p -> p.getCreadorId() == null || !p.getCreadorId().equals(usuarioId))
                    .collect(java.util.stream.Collectors.toList());

            // --- DEBUG ---
            System.out.println("[buscarPartidos] usuarioId=" + usuarioId
                    + " estrategia=" + estrategia
                    + " barrio=" + (usuario.getUbicacion() != null ? usuario.getUbicacion().getBarrio() : "NULL")
                    + " nivel=" + usuario.getNivel().getNombre()
                    + " disponibles=" + disponibles.size());
            disponibles.forEach(p -> System.out.println(
                    "  → Partido #" + p.getIdPartido()
                    + " barrio=[" + (p.getUbicacion() != null ? p.getUbicacion().getBarrio() : "NULL") + "]"
                    + " nivelMin=" + p.getNivelMinimo().getNombre()
                    + " nivelMax=" + p.getNivelMaximo().getNombre()));
            // --- FIN DEBUG ---

            // Seleccionar estrategia
            IEmparejadorStrategy strategy;
            switch (estrategia.toUpperCase()) {
                case "UBICACION":
                    strategy = new EmparejadorUbicacionStrategy();
                    break;
                case "HISTORIAL":
                    strategy = new EmparejadorHistorialStrategy(partidoService.getPartidos());
                    break;
                default:
                    strategy = new EmparejadorNivelStrategy();
                    break;
            }

            List<Partido> resultado = strategy.buscarPartido(usuario, disponibles);

            return ResponseEntity.ok(resultado.stream().map(this::toMap).collect(java.util.stream.Collectors.toList()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // GET /api/partidos/deportes
    @GetMapping("/deportes")
    public List<Map<String, Object>> listarDeportes() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Deporte d : deporteService.getDeportes()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", d.getIdDeporte());
            m.put("nombre", d.getNombre());
            result.add(m);
        }
        return result;
    }

    // GET /api/barrios
    @GetMapping("/barrios")
    public List<String> listarBarrios() {
        return new ArrayList<>(BARRIOS);
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
        m.put("barrio", p.getUbicacion() != null ? p.getUbicacion().getBarrio() : "");
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
        m.put("creadorId", p.getCreadorId()); // needed by frontend for owner check
        return m;
    }

    private NivelState parseNivel(String nivel) {
        switch (nivel.toLowerCase()) {
            case "intermedio":
                return new Intermedio();
            case "avanzado":
                return new Avanzado();
            default:
                return new Principiante();
        }
    }

    /**
     * Throws SecurityException if the requesting user is not the match creator.
     * If creadorId is missing or the match has no creator set, access is allowed
     * (to avoid locking out matches created before this feature was added).
     */
    private void verificarPropietario(Partido partido, Long creadorId) {
        Long owner = partido.getCreadorId();
        if (owner == null || owner == 0 || creadorId == null)
            return; // no owner set → open
        if (!owner.equals(creadorId)) {
            throw new SecurityException("Solo el creador del partido puede realizar esta acción.");
        }
    }
}
