package org.example.state;

import org.example.model.Jugador;
import org.example.model.Partido;

public class ArmadoState implements IPartidoState {

    @Override
    public void agregarJugador(Partido partido) {
        throw new IllegalStateException("El partido ya tiene todos los jugadores.");
    }

    @Override
    public void confirmar(Partido partido) {
        boolean todosConfirmados = partido.getJugadores().stream()
                .allMatch(Jugador::isConfirmacion);
        if (todosConfirmados) {
            partido.setEstado(new ConfirmadoState());
            System.out.println("Partido #" + partido.getIdPartido() + " → Confirmado");
            partido.notificarObservers();
        } else {
            System.out.println("Aún faltan jugadores por confirmar.");
        }
    }

    @Override
    public void iniciar(Partido partido) {
        throw new IllegalStateException("No se puede iniciar: el partido no está confirmado.");
    }

    @Override
    public void finalizar(Partido partido) {
        throw new IllegalStateException("No se puede finalizar: el partido no está en juego.");
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
