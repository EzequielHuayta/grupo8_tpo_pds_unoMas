package org.example.controllers;

import org.example.model.Ubicacion;
import org.example.model.Usuario;
import org.example.nivel.Avanzado;
import org.example.nivel.Intermedio;
import org.example.nivel.NivelState;
import org.example.nivel.Principiante;
import org.example.notification.AdapterJavaEmail;
import org.example.notification.EmailNotificacionStrategy;
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

    public UsuarioController(UsuarioService usuarioService, AdapterJavaEmail adapterEmail,
            PartidoService partidoService) {
        this.usuarioService = usuarioService;
        this.adapterEmail = adapterEmail;
        this.partidoService = partidoService;
    }

    // GET /api/usuarios
    @GetMapping
    public List<Map<String, Object>> listarUsuarios() {
        return usuarioService.getUsuarios().stream()
                .map(this::toMap)
                .collect(Collectors.toList());
    }

    // POST /api/usuarios/registro
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

            // Nivel
            NivelState nivel;
            switch (nivelStr.toLowerCase()) {
                case "intermedio":
                    nivel = new Intermedio();
                    break;
                case "avanzado":
                    nivel = new Avanzado();
                    break;
                default:
                    nivel = new Principiante();
                    break;
            }
            usuarioService.setNivel(usuario, nivel);

            // Notificaciones por email
            //usuario.setEstrategiaNotificacion(new EmailNotificacionStrategy(adapterEmail));

            return ResponseEntity.ok(toMap(usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // POST /api/usuarios/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String contrasena = body.get("contrasena");
            Usuario usuario = usuarioService.iniciarSesion(email, contrasena);
            return ResponseEntity.ok(toMap(usuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // POST /api/usuarios/login-simple
    @PostMapping("/login-simple")
    public ResponseEntity<?> loginSimple(@RequestBody Map<String, String> body) {
        try {
            String nombre = body.get("nombreUsuario");
            Usuario usuario = usuarioService.loginSinContrasena(nombre);
            return ResponseEntity.ok(toMap(usuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // PUT /api/usuarios/{id}/nivel
    @PutMapping("/{id}/nivel")
    public ResponseEntity<?> cambiarNivel(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String nivelStr = body.get("nivel");
            NivelState nivel;
            switch (nivelStr.toLowerCase()) {
                case "intermedio":
                    nivel = new Intermedio();
                    break;
                case "avanzado":
                    nivel = new Avanzado();
                    break;
                default:
                    nivel = new Principiante();
                    break;
            }
            Usuario usuario = usuarioService.buscarPorId(id);
            usuarioService.setNivel(usuario, nivel);
            return ResponseEntity.ok(toMap(usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // helper
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
        }
        m.put("cantidadPartidosCompletados", u.getCantidadPartidosCompletados(partidoService.getPartidos()));
        return m;
    }

    // GET /api/usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerUsuario(@PathVariable Long id) {
        try {
            Usuario u = usuarioService.buscarPorId(id);
            return ResponseEntity.ok(toMap(u));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
