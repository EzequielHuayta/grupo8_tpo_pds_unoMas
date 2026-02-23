package org.example.rest;

import org.example.model.Ubicacion;
import org.example.model.Usuario;
import org.example.nivel.Avanzado;
import org.example.nivel.Intermedio;
import org.example.nivel.NivelState;
import org.example.nivel.Principiante;
import org.example.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioRestController {

    private final UsuarioService usuarioService;

    public UsuarioRestController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // GET /api/usuarios
    @GetMapping
    public List<Map<String, Object>> listarUsuarios() {
        return usuarioService.getUsuarios().stream()
                .map(this::toMap)
                .collect(Collectors.toList());
    }

    // GET /api/usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerUsuario(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(toMap(usuarioService.buscarPorId(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/usuarios/registro
    // Body: { "nombreUsuario": "Juan", "email": "juan@mail.com", "contrasena": "pass",
    //         "ciudad": "Buenos Aires", "latitud": -34.6, "longitud": -58.38 }
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Map<String, Object> body) {
        try {
            String nombre    = body.get("nombreUsuario").toString();
            String email     = body.get("email").toString();
            String contrasena = body.get("contrasena").toString();
            String ciudad    = body.get("ciudad").toString();
            double latitud   = Double.parseDouble(body.get("latitud").toString());
            double longitud  = Double.parseDouble(body.get("longitud").toString());

            Ubicacion ubicacion = new Ubicacion(latitud, longitud, ciudad);
            Usuario usuario = usuarioService.registrarUsuario(nombre, email, contrasena, ubicacion);

            if (body.containsKey("nivel")) {
                usuarioService.setNivel(usuario, parseNivel(body.get("nivel").toString()));
            }

            return ResponseEntity.ok(toMap(usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // POST /api/usuarios/login
    // Body: { "email": "juan@mail.com", "contrasena": "pass" }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body) {
        try {
            String email     = body.get("email").toString();
            String contrasena = body.get("contrasena").toString();
            Usuario usuario = usuarioService.iniciarSesion(email, contrasena);
            return ResponseEntity.ok(toMap(usuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", "Credenciales inválidas"));
        }
    }

    // PUT /api/usuarios/{id}/nivel
    // Body: { "nivel": "Avanzado" }
    @PutMapping("/{id}/nivel")
    public ResponseEntity<?> cambiarNivel(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            usuarioService.setNivel(usuario, parseNivel(body.get("nivel").toString()));
            return ResponseEntity.ok(toMap(usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // --- helpers ---

    private Map<String, Object> toMap(Usuario u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getIdUsuario());
        m.put("nombreUsuario", u.getNombreUsuario());
        m.put("email", u.getEmail());
        m.put("nivel", u.getNivel().getNombre());
        if (u.getUbicacion() != null) {
            m.put("ciudad", u.getUbicacion().getCiudad());
            m.put("latitud", u.getUbicacion().getLatitud());
            m.put("longitud", u.getUbicacion().getLongitud());
        }
        if (u.getDeporteFavorito() != null) {
            m.put("deporteFavorito", u.getDeporteFavorito().getNombre());
        }
        return m;
    }

    private NivelState parseNivel(String nivel) {
        switch (nivel.toLowerCase()) {
            case "intermedio": return new Intermedio();
            case "avanzado":   return new Avanzado();
            default:           return new Principiante();
        }
    }
}
