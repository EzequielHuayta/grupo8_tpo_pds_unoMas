package org.example.service;

import org.example.model.Deporte;
import org.example.model.Partido;
import org.example.model.Ubicacion;
import org.example.model.Usuario;
import org.example.nivel.NivelState;
import org.example.repository.IUsuarioRepository;

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

    public void setDeporteFavorito(Usuario usuario, Deporte deporte) {
        usuario.setDeporteFavorito(deporte);
        deporte.agregarUsuario(usuario);
        usuarioRepository.guardar(usuario);
        System.out.println(usuario.getNombreUsuario() + " → deporte favorito: " + deporte.getNombre());
    }

    public void setNivel(Usuario usuario, NivelState nivel) {
        usuario.setNivel(nivel);
        usuarioRepository.guardar(usuario);
        System.out.println(usuario.getNombreUsuario() + " → nivel: " + nivel.getNombre());
    }

    public List<Usuario> getUsuarios() {
        return usuarioRepository.listarTodos();
    }

    public void guardarPartido(Usuario usuario, Partido partido) {
        usuario.agregarPartidoAlHistorial(partido);
        System.out.println("Partido #" + partido.getIdPartido() + " guardado en historial de " + usuario.getNombreUsuario());
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
    }
}
