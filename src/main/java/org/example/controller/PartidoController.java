package org.example.controller;

import org.example.model.*;
import org.example.nivel.NivelState;
import org.example.service.PartidoService;

import java.time.LocalDateTime;
import java.util.List;

public class PartidoController {
    private final PartidoService partidoService;

    public PartidoController(PartidoService partidoService) {
        this.partidoService = partidoService;
    }

    public Partido crearPartido(Deporte deporte, int cantidadJugadores, int duracionMinutos,
                                 Ubicacion ubicacion, LocalDateTime horario) {
        return partidoService.crearPartido(deporte, cantidadJugadores, duracionMinutos, ubicacion, horario);
    }

    public Partido crearPartido(Deporte deporte, int cantidadJugadores, int duracionMinutos,
                                 Ubicacion ubicacion, LocalDateTime horario,
                                 NivelState nivelMinimo, NivelState nivelMaximo) {
        return partidoService.crearPartido(deporte, cantidadJugadores, duracionMinutos,
                ubicacion, horario, nivelMinimo, nivelMaximo);
    }

    public void agregarJugador(Partido partido, Usuario usuario) {
        partidoService.agregarJugador(partido, usuario);
    }

    public void confirmarJugador(Partido partido, Usuario usuario) {
        partidoService.confirmarJugador(partido, usuario);
    }

    public void cancelarPartido(Partido partido) {
        partidoService.cancelarPartido(partido);
    }

    public void iniciarPartido(Partido partido) {
        partidoService.iniciarPartido(partido);
    }

    public void finalizarPartido(Partido partido) {
        partidoService.finalizarPartido(partido);
    }

    public List<Partido> buscarPartidos(Usuario usuario) {
        return partidoService.buscarPartidos(usuario);
    }
}
