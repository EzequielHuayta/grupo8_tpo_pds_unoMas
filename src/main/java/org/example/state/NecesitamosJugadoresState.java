package org.example.state;

import org.example.model.Partido;

public class NecesitamosJugadoresState implements IPartidoState {

    @Override
    public void agregarJugador(Partido partido) {
        System.out.println("Jugador agregado al partido #" + partido.getIdPartido());
        if (partido.getJugadores().size() >= partido.getCantidadJugadores()) {
            partido.setEstado(new ArmadoState());
            System.out.println("Partido #" + partido.getIdPartido() + " → Armado");
            partido.notificarObservers();
        }
    }

    @Override
    public void confirmar(Partido partido) {
        throw new IllegalStateException("No se puede confirmar: faltan jugadores.");
    }

    @Override
    public void iniciar(Partido partido) {
        throw new IllegalStateException("No se puede iniciar: faltan jugadores.");
    }

    @Override
    public void finalizar(Partido partido) {
        throw new IllegalStateException("No se puede finalizar: faltan jugadores.");
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
