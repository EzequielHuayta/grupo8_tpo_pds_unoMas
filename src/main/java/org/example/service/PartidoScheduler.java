package org.example.service;

import org.example.model.Partido;
import org.example.repository.IPartidoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Dos cron jobs que corren cada 60 segundos:
 *
 * Job 1 — verificarEstados():
 *   - "Necesitamos jugadores" o "Partido armado" cuyo horario ya pasó → Cancelado
 *   - "Confirmado" cuyo horario ya llegó                              → En juego
 *
 * Job 2 — finalizarEnJuego():
 *   - "En juego" cuyo horario + duracionMinutos ya pasó              → Finalizado
 */
@Component
public class PartidoScheduler {

    private final PartidoService partidoService;
    private final IPartidoRepository partidoRepository;

    public PartidoScheduler(PartidoService partidoService, IPartidoRepository partidoRepository) {
        this.partidoService = partidoService;
        this.partidoRepository = partidoRepository;
    }

    @Scheduled(fixedDelay = 60_000)
    public void verificarEstados() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Partido> todos = partidoRepository.listarTodos();

        for (Partido p : todos) {
            String estado = p.getEstado().getNombre();
            try {
                switch (estado) {
                    case "Necesitamos jugadores":
                    case "Partido armado":
                        if (p.getHorario().isBefore(ahora)) {
                            partidoService.cancelarPartido(p);
                            System.out.println("[Scheduler] Partido #" + p.getIdPartido()
                                    + " (" + estado + ") → Cancelado automaticamente");
                        }
                        break;

                    case "Confirmado":
                        if (!p.getHorario().isAfter(ahora)) {
                            partidoService.iniciarPartido(p);
                            System.out.println("[Scheduler] Partido #" + p.getIdPartido()
                                    + " → En juego (inicio automatico)");
                        }
                        break;

                    default:
                        break;
                }
            } catch (Exception e) {
                System.err.println("[Scheduler] Error en partido #"
                        + p.getIdPartido() + ": " + e.getMessage());
            }
        }
    }

    @Scheduled(fixedDelay = 60_000)
    public void finalizarEnJuego() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Partido> todos = partidoRepository.listarTodos();

        for (Partido p : todos) {
            if (!"En juego".equals(p.getEstado().getNombre())) continue;

            // horario = cuando empieza; horario + duracion = cuando termina
            LocalDateTime finEsperado = p.getHorario().plusMinutes(p.getDuracionMinutos());

            if (ahora.isAfter(finEsperado)) {
                try {
                    partidoService.finalizarPartido(p);
                    System.out.println("[Scheduler] Partido #" + p.getIdPartido()
                            + " → Finalizado (duracion cumplida)");
                } catch (Exception e) {
                    System.err.println("[Scheduler] Error finalizando partido #"
                            + p.getIdPartido() + ": " + e.getMessage());
                }
            }
        }
    }
}
