package org.example.service;

import org.example.model.Deporte;
import org.example.model.Notificacion;
import org.example.model.Partido;
import org.example.model.Usuario;
import org.example.repository.IUsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DeporteService {

    private final IUsuarioRepository usuarioRepository;

    private static final List<Deporte> DEPORTES = Arrays.asList(
            new Deporte(1L, "Fútbol"),
            new Deporte(2L, "Básquet"),
            new Deporte(3L, "Tenis"),
            new Deporte(4L, "Vóley"),
            new Deporte(5L, "Paddle"));

    public DeporteService(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Deporte> getDeportes() {
        return DEPORTES;
    }

    public Deporte buscarPorId(Long id) {
        return DEPORTES.stream()
                .filter(d -> d.getIdDeporte().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Deporte no encontrado con ID: " + id));
    }

    public void notificarNuevoPartido(Partido partido, Long creadorId) {
        Deporte deporte = partido.getDeporte();
        String mensaje = "¡Hay un nuevo partido con ID#" + partido.getIdPartido()
                + " para tu deporte favorito " + deporte.getNombre() + "!";
        Notificacion notificacion = new Notificacion(mensaje);

        System.out.println("[DeporteService] Nuevo partido #" + partido.getIdPartido()
                + " de " + deporte.getNombre() + ". Notificando usuarios interesados...");

        List<Usuario> usuarios = usuarioRepository.listarTodos();
        int notificados = 0;
        for (Usuario usuario : usuarios) {
            // No notificar al creador del partido
            if (creadorId != null && usuario.getIdUsuario().equals(creadorId)) continue;
            Deporte fav = usuario.getDeporteFavorito();
            if (fav != null && fav.getIdDeporte().equals(deporte.getIdDeporte())) {
                System.out.println("  → Notificando a " + usuario.getNombreUsuario()
                        + " (ID " + usuario.getIdUsuario() + ") | Deporte fav: " + fav.getNombre());
                usuario.recibirNotificacion(notificacion);
                notificados++;
            }
        }
        System.out.println("[DeporteService] Notificación enviada a " + notificados + " usuario(s).");
    }
}
