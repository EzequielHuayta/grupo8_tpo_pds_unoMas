package org.example.state;

import org.example.model.Partido;

/**
 * Estado: el partido está actualmente en curso.
 * Transiciones:
 * - avanzar() → FinalizadoState
 * - cancelar() → CanceladoState [BUG FIX: antes tiraba exception, ahora es una
 * transición válida]
 */
public class EnJuegoState implements IPartidoState {

    @Override
    public void avanzar(Partido partido) {
        partido.setEstado(new FinalizadoState());
        System.out.println("Partido #" + partido.getIdPartido() + " → Finalizado");
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
        return "En juego";
    }
}
