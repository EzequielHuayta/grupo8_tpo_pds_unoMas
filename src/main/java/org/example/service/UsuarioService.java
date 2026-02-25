package org.example.service;

import org.example.model.Deporte;
import org.example.model.Partido;
import org.example.model.Ubicacion;
import org.example.model.Usuario;
import org.example.nivel.NivelState;
import org.example.repository.IUsuarioRepository;
import org.example.strategy.EmparejadorHistorialStrategy;
import org.example.strategy.EmparejadorNivelStrategy;
import org.example.strategy.EmparejadorUbicacionStrategy;
import org.example.strategy.IEmparejadorStrategy;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {
    private final IUsuarioRepository usuarioRepository;

    public UsuarioService(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario registrarUsuario(String nombreUsuario, String email, String contrasena, Ubicacion ubicacion) {
        Usuario usuario = new Usuario(usuarioRepository.generarId(), nombreUsuario, email, contrasena);
        usuario.setUbicacion(ubicacion);
        usuarioRepository.guardar(usuario);
        System.out.println("Usuario registrado: " + usuario.getNombreUsuario());
        return usuario;
    }

    public Usuario iniciarSesion(String email, String contrasena) {
        return usuarioRepository.buscarPorEmail(email)
                .filter(u -> u.getContrasena().equals(contrasena))
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas."));
    }

    /** Login sin contraseña: solo por nombre de usuario */
    public Usuario loginSinContrasena(String nombreUsuario) {
        return usuarioRepository.buscarPorNombre(nombreUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + nombreUsuario));
    }

    public void setDeporteFavorito(Usuario usuario, Deporte deporte) {
        usuario.setDeporteFavorito(deporte);
        deporte.agregarUsuario(usuario);
        usuarioRepository.guardar(usuario);
        System.out.println(usuario.getNombreUsuario() + " → deporte favorito: " + deporte.getNombre());
    }

    public void setEstrategiaEmparejamiento(Usuario usuario, String estrategiaKey, List<Partido> todosLosPartidos) {
        IEmparejadorStrategy estrategia;
        switch (estrategiaKey.toUpperCase()) {
            case "UBICACION":
                estrategia = new EmparejadorUbicacionStrategy();
                break;
            case "HISTORIAL":
                estrategia = new EmparejadorHistorialStrategy(todosLosPartidos);
                break;
            default: // NIVEL o cualquier otro valor
                estrategia = new EmparejadorNivelStrategy();
                break;
        }
        // Obtener nombre legible de la estrategia anterior
        String estrategiaAnterior = "ninguna";
        if (usuario.getEstrategiaEmparejamiento() instanceof EmparejadorUbicacionStrategy)
            estrategiaAnterior = "Por Ubicación";
        else if (usuario.getEstrategiaEmparejamiento() instanceof EmparejadorHistorialStrategy)
            estrategiaAnterior = "Por Historial";
        else if (usuario.getEstrategiaEmparejamiento() instanceof EmparejadorNivelStrategy)
            estrategiaAnterior = "Por Nivel";

        usuario.cambiarEstrategiaEmparejamiento(estrategia);
        usuarioRepository.guardar(usuario);

        String estrategiaNueva = estrategiaKey.equalsIgnoreCase("UBICACION") ? "Por Ubicación"
                : estrategiaKey.equalsIgnoreCase("HISTORIAL") ? "Por Historial"
                        : "Por Nivel";

        System.out.println("┌─────────────────────────────────────────────────");
        System.out.println("│ [ESTRATEGIA DE BÚSQUEDA ACTUALIZADA]");
        System.out.println("│  Usuario ID : " + usuario.getIdUsuario());
        System.out.println("│  Usuario    : " + usuario.getNombreUsuario());
        System.out.println("│  Anterior   : " + estrategiaAnterior);
        System.out.println("│  Nueva      : " + estrategiaNueva
                + " (" + estrategia.getClass().getSimpleName() + ")");
        System.out.println("└─────────────────────────────────────────────────");
    }

    public void setNivel(Usuario usuario, NivelState nivel) {
        usuario.setNivel(nivel);
        usuarioRepository.guardar(usuario);
        System.out.println(usuario.getNombreUsuario() + " → nivel: " + nivel.getNombre());
    }

    public List<Usuario> getUsuarios() {
        return usuarioRepository.listarTodos();
    }

    public void guardar(Usuario usuario) {
        usuarioRepository.guardar(usuario);
    }

    public void guardarPartido(Usuario usuario, Partido partido) {
        usuario.agregarPartidoAlHistorial(partido);
        usuarioRepository.guardar(usuario);
        System.out.println(
                "Partido #" + partido.getIdPartido() + " guardado en historial de " + usuario.getNombreUsuario());
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
    }
}
