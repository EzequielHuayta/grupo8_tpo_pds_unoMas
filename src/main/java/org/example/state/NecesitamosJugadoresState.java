package org.example.state;

import org.example.model.Partido;

/**
 * Estado: el partido fue creado pero aún no tiene todos los jugadores.
 * Transiciones:
 * - avanzar() → ArmadoState (cuando se llena el cupo de jugadores)
 * - cancelar() → CanceladoState
 */
public class NecesitamosJugadoresState implements IPartidoState {

    /**
     * Avanza al estado Armado si se completó el cupo de jugadores.
     * Si aún faltan jugadores, no cambia el estado pero lo reporta.
     */
    @Override
    public void avanzar(Partido partido) {
        if (partido.getJugadores().size() >= partido.getCantidadJugadores()) {
            partido.setEstado(new ArmadoState());
            System.out.println("Partido #" + partido.getIdPartido() + " → Armado");
            partido.notificarJugadores("Tu partido con ID#" + partido.getIdPartido()
                    + " ya se encuentra completo, ¡confirma tu asistencía!");
        } else {
            System.out.println("Partido #" + partido.getIdPartido()
                    + ": aún faltan jugadores ("
                    + partido.getJugadores().size() + "/" + partido.getCantidadJugadores() + ")");
        }
    }

    @Override
    public void cancelar(Partido partido) {
        partido.setEstado(new CanceladoState());
        System.out.println("Partido #" + partido.getIdPartido() + " → Cancelado");
        partido.notificarObservers();
    }

    @Override
    public String getNombre() {
        return "Necesitamos jugadores";
    }
}
