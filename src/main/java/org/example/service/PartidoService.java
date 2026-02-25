package org.example.service;

import org.example.model.*;
import org.example.nivel.NivelState;
import org.example.repository.IPartidoRepository;
import org.example.state.NecesitamosJugadoresState;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartidoService {
    private final IPartidoRepository partidoRepository;
    private final DeporteService deporteService;
    
    @Autowired
    private PartidoEstadoNotifier partidoEstadoNotifier;

    public PartidoService(IPartidoRepository partidoRepository, DeporteService deporteService) {
        this.partidoRepository = partidoRepository;
        this.deporteService = deporteService;
    }

    public Partido crearPartido(Deporte deporte, int cantidadJugadores, int duracionMinutos,
            Ubicacion ubicacion, LocalDateTime horario, Long creadorId) {
        Partido partido = new Partido(partidoRepository.generarId(), deporte, cantidadJugadores,
                duracionMinutos, ubicacion, horario);
        partidoRepository.guardar(partido);
        deporteService.notificarNuevoPartido(partido, creadorId);
        System.out.println("Partido creado: " + partido);
        return partido;
    }

    public Partido crearPartido(Deporte deporte, int cantidadJugadores, int duracionMinutos,
            Ubicacion ubicacion, LocalDateTime horario,
            NivelState nivelMinimo, NivelState nivelMaximo, Long creadorId) {
        if (nivelMinimo.getPesoNivel() > nivelMaximo.getPesoNivel()) {
            throw new IllegalArgumentException(
                    "El nivel mínimo (" + nivelMinimo.getNombre() + ") no puede ser mayor que el máximo ("
                            + nivelMaximo.getNombre() + ").");
        }
        Partido partido = crearPartido(deporte, cantidadJugadores, duracionMinutos, ubicacion, horario, creadorId);
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
        String estadoAnterior = partido.getEstado().getNombre();
        partido.cancelar();
        String estadoNuevo = partido.getEstado().getNombre();
        
        partidoRepository.guardar(partido);
        
        // Notificar cambio de estado si es necesario
        if (partidoEstadoNotifier.debeNotificar(estadoAnterior, estadoNuevo)) {
            partidoEstadoNotifier.notificarCambioEstado(partido, estadoAnterior, estadoNuevo);
        }
    }

    public void iniciarPartido(Partido partido) {
        String estadoAnterior = partido.getEstado().getNombre();
        partido.iniciar();
        String estadoNuevo = partido.getEstado().getNombre();
        
        partidoRepository.guardar(partido);
        
        // Notificar cambio de estado si es necesario
        if (partidoEstadoNotifier.debeNotificar(estadoAnterior, estadoNuevo)) {
            partidoEstadoNotifier.notificarCambioEstado(partido, estadoAnterior, estadoNuevo);
        }
    }

    public void finalizarPartido(Partido partido) {
        String estadoAnterior = partido.getEstado().getNombre();
        partido.finalizar();
        String estadoNuevo = partido.getEstado().getNombre();
        
        partidoRepository.guardar(partido);
        
        // Notificar cambio de estado si es necesario
        if (partidoEstadoNotifier.debeNotificar(estadoAnterior, estadoNuevo)) {
            partidoEstadoNotifier.notificarCambioEstado(partido, estadoAnterior, estadoNuevo);
        }
    }

    
    public void confirmarPartido(Partido partido) {
        String estadoAnterior = partido.getEstado().getNombre();
        partido.confirmar();
        String estadoNuevo = partido.getEstado().getNombre();
        
        partidoRepository.guardar(partido);
        
        // Notificar cambio de estado si es necesario
        if (partidoEstadoNotifier.debeNotificar(estadoAnterior, estadoNuevo)) {
            partidoEstadoNotifier.notificarCambioEstado(partido, estadoAnterior, estadoNuevo);
        }
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
