package org.example.state;

import org.example.model.Partido;

/**
 * Estado: todos los jugadores confirmaron, el partido está listo para jugarse.
 * Transiciones:
 * - avanzar() → EnJuegoState
 * - cancelar() → CanceladoState
 */
public class ConfirmadoState implements IPartidoState {

    @Override
    public void avanzar(Partido partido) {
        partido.setEstado(new EnJuegoState());
        System.out.println("Partido #" + partido.getIdPartido() + " → En juego");
        partido.notificarObservers();
    }

    @Override
    public void cancelar(Partido partido) {
        partido.setEstado(new CanceladoState());
        System.out.println("Partido #" + partido.getIdPartido() + " → Cancelado");
        partido.notificarObservers();
    }

    @Override
    public String getNombre() {
        return "Confirmado";
    }
}
