package org.example.controllers;

import org.example.model.Ubicacion;
import org.example.model.Usuario;
import org.example.nivel.Avanzado;
import org.example.nivel.Intermedio;
import org.example.nivel.NivelState;
import org.example.nivel.Principiante;
import org.example.notification.AdapterJavaEmail;
import org.example.notification.EmailNotificacionStrategy;
import org.example.notification.FirebaseNotificacionStrategy;
import org.example.notification.InAppNotificacionStore;
import org.example.service.DeporteService;
import org.example.service.PartidoService;
import org.example.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AdapterJavaEmail adapterEmail;
    private final PartidoService partidoService;
    private final DeporteService deporteService;
    private final InAppNotificacionStore inAppStore;

    public UsuarioController(UsuarioService usuarioService, AdapterJavaEmail adapterEmail,
            PartidoService partidoService, DeporteService deporteService,
            InAppNotificacionStore inAppStore) {
        this.usuarioService = usuarioService;
        this.adapterEmail = adapterEmail;
        this.partidoService = partidoService;
        this.deporteService = deporteService;
        this.inAppStore = inAppStore;
    }

    // ─── Helpers privados ───────────────────────────────────────────────────

    private void aplicarEstrategiaNotificacion(Usuario usuario, String notifStr) {
        if (notifStr.equalsIgnoreCase("In-App")) {
            usuario.setEstrategiaNotificacion(
                    new FirebaseNotificacionStrategy(inAppStore, usuario.getIdUsuario(), usuario.getNombreUsuario()));
        } else {
            usuario.setEstrategiaNotificacion(
                    new EmailNotificacionStrategy(adapterEmail, usuario.getEmail(),
                            usuario.getIdUsuario(), usuario.getNombreUsuario()));
        }
    }

    private NivelState parsearNivel(String nivelStr) {
        switch (nivelStr.toLowerCase()) {
            case "intermedio":
                return new Intermedio();
            case "avanzado":
                return new Avanzado();
            default:
                return new Principiante();
        }
    }

    // ─── GET /api/usuarios ──────────────────────────────────────────────────

    @GetMapping
    public List<Map<String, Object>> listarUsuarios() {
        return usuarioService.getUsuarios().stream()
                .map(this::toMap)
                .collect(Collectors.toList());
    }

    // ─── GET /api/usuarios/{id} ─────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerUsuario(@PathVariable Long id) {
        try {
            Usuario u = usuarioService.buscarPorId(id);
            return ResponseEntity.ok(toMap(u));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ─── GET /api/usuarios/{id}/notificaciones ──────────────────────────────

    @GetMapping("/{id}/notificaciones")
    public ResponseEntity<List<String>> obtenerNotificaciones(@PathVariable Long id) {
        return ResponseEntity.ok(inAppStore.obtener(id));
    }

    // ─── DELETE /api/usuarios/{id}/notificaciones (marcar como leídas) ──────

    @DeleteMapping("/{id}/notificaciones")
    public ResponseEntity<Void> leerNotificaciones(@PathVariable Long id) {
        inAppStore.limpiar(id);
        return ResponseEntity.noContent().build();
    }

    // ─── POST /api/usuarios/registro ────────────────────────────────────────

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Map<String, String> body) {
        try {
            String nombre = body.get("nombreUsuario");
            String email = body.get("email");
            String contra = body.get("contrasena");
            String barrio = body.getOrDefault("barrio", "");
            String nivelStr = body.getOrDefault("nivel", "Principiante");

            Ubicacion ubicacion = new Ubicacion(barrio);
            Usuario usuario = usuarioService.registrarUsuario(nombre, email, contra, ubicacion);

            usuarioService.setNivel(usuario, parsearNivel(nivelStr));

            String notificacionStr = body.getOrDefault("notificacion", "Email");
            aplicarEstrategiaNotificacion(usuario, notificacionStr);

            if (body.containsKey("deporteFavoritoId") && !body.get("deporteFavoritoId").isEmpty()) {
                try {
                    Long deporteId = Long.parseLong(body.get("deporteFavoritoId"));
                    usuario.setDeporteFavorito(deporteService.buscarPorId(deporteId));
                } catch (Exception ignored) {
                }
            }

            usuarioService.guardar(usuario);
            return ResponseEntity.ok(toMap(usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // ─── POST /api/usuarios/login ────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String contrasena = body.get("contrasena");
            Usuario usuario = usuarioService.iniciarSesion(email, contrasena);
            return ResponseEntity.ok(toMap(usuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // ─── POST /api/usuarios/login-simple ────────────────────────────────────

    @PostMapping("/login-simple")
    public ResponseEntity<?> loginSimple(@RequestBody Map<String, String> body) {
        try {
            String nombreUsuario = body.get("nombreUsuario");
            if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
                throw new IllegalArgumentException("Nombre de usuario es requerido");
            }
            Usuario usuario = usuarioService.loginSinContrasena(nombreUsuario);
            return ResponseEntity.ok(toMap(usuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // ─── PUT /api/usuarios/{id} ──────────────────────────────────────────────

    @PutMapping("/{id}")
    public ResponseEntity<?> modificarUsuario(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);

            if (body.containsKey("nombreUsuario"))
                usuario.setNombreUsuario(body.get("nombreUsuario"));
            if (body.containsKey("email"))
                usuario.setEmail(body.get("email"));
            if (body.containsKey("barrio"))
                usuario.setUbicacion(new Ubicacion(body.get("barrio")));

            if (body.containsKey("nivel")) {
                usuarioService.setNivel(usuario, parsearNivel(body.get("nivel")));
            }

            if (body.containsKey("notificacion")) {
                aplicarEstrategiaNotificacion(usuario, body.get("notificacion"));
            }

            if (body.containsKey("deporteFavoritoId") && !body.get("deporteFavoritoId").isEmpty()) {
                try {
                    Long deporteId = Long.parseLong(body.get("deporteFavoritoId"));
                    usuario.setDeporteFavorito(deporteService.buscarPorId(deporteId));
                } catch (Exception ignored) {
                }
            }

            usuarioService.guardar(usuario);
            return ResponseEntity.ok(toMap(usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // ─── PUT /api/usuarios/{id}/nivel (delegado) ─────────────────────────────

    @PutMapping("/{id}/nivel")
    public ResponseEntity<?> cambiarNivel(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return modificarUsuario(id, body);
    }

    // ─── helper toMap ──────────────────────────────────────────────────────────

    private Map<String, Object> toMap(Usuario u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getIdUsuario());
        m.put("nombreUsuario", u.getNombreUsuario());
        m.put("email", u.getEmail());
        m.put("nivel", u.getNivel().getNombre());
        if (u.getUbicacion() != null) {
            m.put("barrio", u.getUbicacion().getBarrio());
        }
        if (u.getDeporteFavorito() != null) {
            m.put("deporteFavorito", u.getDeporteFavorito().getNombre());
            m.put("deporteFavoritoId", u.getDeporteFavorito().getIdDeporte());
        }

        String estrategiaStr = "Email";
        if (u.getEstrategiaNotificacion() instanceof FirebaseNotificacionStrategy) {
            estrategiaStr = "In-App";
        }
        m.put("notificacion", estrategiaStr);

        m.put("cantidadPartidosCompletados", u.getCantidadPartidosCompletados(partidoService.getPartidos()));
        return m;
    }
}
