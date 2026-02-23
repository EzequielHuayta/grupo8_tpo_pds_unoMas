package org.example.state;

import org.example.model.Partido;

public class EnJuegoState implements IPartidoState {

    @Override
    public void agregarJugador(Partido partido) {
        throw new IllegalStateException("No se pueden agregar jugadores: el partido está en juego.");
    }

    @Override
    public void confirmar(Partido partido) {
        throw new IllegalStateException("No se puede confirmar: el partido está en juego.");
    }

    @Override
    public void iniciar(Partido partido) {
        throw new IllegalStateException("El partido ya está en juego.");
    }

    @Override
    public void finalizar(Partido partido) {
        partido.setEstado(new FinalizadoState());
        System.out.println("Partido #" + partido.getIdPartido() + " → Finalizado");
        partido.notificarObservers();
    }

    @Override
    public void cancelar(Partido partido) {
        throw new IllegalStateException("No se puede cancelar: el partido está en juego.");
    }

    @Override
    public String getNombre() {
        return "En juego";
    }
}
