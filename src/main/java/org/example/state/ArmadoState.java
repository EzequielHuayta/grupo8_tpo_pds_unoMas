package org.example.state;

import org.example.model.Jugador;
import org.example.model.Partido;

/**
 * Estado: el partido tiene todos sus jugadores pero aún no todos confirmaron.
 * Transiciones:
 * - avanzar() → ConfirmadoState (cuando todos los jugadores confirmaron
 * asistencia)
 * - cancelar() → CanceladoState
 */
public class ArmadoState implements IPartidoState {

    /**
     * Avanza a Confirmado si todos los jugadores confirmaron su asistencia.
     * Si aún faltan confirmaciones, no cambia el estado.
     */
    @Override
    public void avanzar(Partido partido) {
        boolean todosConfirmados = partido.getJugadores().stream()
                .allMatch(Jugador::isConfirmacion);
        if (todosConfirmados) {
            partido.setEstado(new ConfirmadoState());
            System.out.println("Partido #" + partido.getIdPartido() + " → Confirmado");
            partido.notificarObservers();
        } else {
            System.out.println("Partido #" + partido.getIdPartido()
                    + ": aún faltan jugadores por confirmar.");
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
        return "Partido armado";
    }
}
