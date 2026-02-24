package org.example.service;

import org.example.model.*;
import org.example.nivel.NivelState;
import org.example.repository.IPartidoRepository;
import org.example.state.NecesitamosJugadoresState;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartidoService {
    private final IPartidoRepository partidoRepository;

    public PartidoService(IPartidoRepository partidoRepository) {
        this.partidoRepository = partidoRepository;
    }

    public Partido crearPartido(Deporte deporte, int cantidadJugadores, int duracionMinutos,
            Ubicacion ubicacion, LocalDateTime horario) {
        Partido partido = new Partido(partidoRepository.generarId(), deporte, cantidadJugadores,
                duracionMinutos, ubicacion, horario);
        partidoRepository.guardar(partido);
        partido.agregarObserver(deporte);
        partido.notificarObservers();
        System.out.println("Partido creado: " + partido);
        return partido;
    }

    public Partido crearPartido(Deporte deporte, int cantidadJugadores, int duracionMinutos,
            Ubicacion ubicacion, LocalDateTime horario,
            NivelState nivelMinimo, NivelState nivelMaximo) {
        if (nivelMinimo.getPesoNivel() > nivelMaximo.getPesoNivel()) {
            throw new IllegalArgumentException(
                    "El nivel mínimo (" + nivelMinimo.getNombre() + ") no puede ser mayor que el máximo ("
                            + nivelMaximo.getNombre() + ").");
        }
        Partido partido = crearPartido(deporte, cantidadJugadores, duracionMinutos, ubicacion, horario);
        partido.setNivelMinimo(nivelMinimo);
        partido.setNivelMaximo(nivelMaximo);
        partidoRepository.guardar(partido); // persist nivel changes
        return partido;
    }

    public void agregarJugador(Partido partido, Usuario usuario) {
        Jugador jugador = new Jugador(partidoRepository.generarIdJugador(), usuario);
        partido.agregarJugador(jugador);
        usuario.agregarPartidoAlHistorial(partido);
        partidoRepository.guardar(partido); // persist new jugador + possible state change
    }

    public void confirmarJugador(Partido partido, Usuario usuario) {
        partido.getJugadores().stream()
                .filter(j -> j.getJugador().equals(usuario))
                .findFirst()
                .ifPresent(Jugador::confirmar);
        partido.confirmar();
        partidoRepository.guardar(partido);
    }

    public void cancelarPartido(Partido partido) {
        partido.cancelar();
        partidoRepository.guardar(partido);
    }

    public void iniciarPartido(Partido partido) {
        partido.iniciar();
        partidoRepository.guardar(partido);
    }

    public void finalizarPartido(Partido partido) {
        partido.finalizar();
        partidoRepository.guardar(partido);
    }

    public List<Partido> buscarPartidos(Usuario usuario) {
        List<Partido> disponibles = partidoRepository.listarTodos().stream()
                .filter(p -> p.getEstado() instanceof NecesitamosJugadoresState)
                .collect(Collectors.toList());
        return usuario.buscarPartido(disponibles);
    }

    public List<Partido> getPartidos() {
        return partidoRepository.listarTodos();
    }

    public Partido buscarPorId(Long id) {
        return partidoRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado: " + id));
    }

    public Partido guardar(Partido partido) {
        return partidoRepository.guardar(partido);
    }
}
