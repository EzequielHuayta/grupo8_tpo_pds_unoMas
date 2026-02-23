package org.example.state;

import org.example.model.Partido;

public class FinalizadoState implements IPartidoState {

    @Override
    public void agregarJugador(Partido partido) {
        throw new IllegalStateException("No se pueden agregar jugadores: el partido finalizó.");
    }

    @Override
    public void confirmar(Partido partido) {
        throw new IllegalStateException("No se puede confirmar: el partido finalizó.");
    }

    @Override
    public void iniciar(Partido partido) {
        throw new IllegalStateException("No se puede iniciar: el partido finalizó.");
    }

    @Override
    public void finalizar(Partido partido) {
        throw new IllegalStateException("El partido ya finalizó.");
    }

    @Override
    public void cancelar(Partido partido) {
        throw new IllegalStateException("No se puede cancelar: el partido ya finalizó.");
    }

    @Override
    public String getNombre() {
        return "Finalizado";
    }
}
