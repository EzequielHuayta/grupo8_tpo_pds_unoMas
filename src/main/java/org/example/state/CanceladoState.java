package org.example.state;

import org.example.model.Partido;

public class CanceladoState implements IPartidoState {

    @Override
    public void agregarJugador(Partido partido) {
        throw new IllegalStateException("No se pueden agregar jugadores: el partido fue cancelado.");
    }

    @Override
    public void confirmar(Partido partido) {
        throw new IllegalStateException("No se puede confirmar: el partido fue cancelado.");
    }

    @Override
    public void iniciar(Partido partido) {
        throw new IllegalStateException("No se puede iniciar: el partido fue cancelado.");
    }

    @Override
    public void finalizar(Partido partido) {
        throw new IllegalStateException("No se puede finalizar: el partido fue cancelado.");
    }

    @Override
    public void cancelar(Partido partido) {
        throw new IllegalStateException("El partido ya fue cancelado.");
    }

    @Override
    public String getNombre() {
        return "Cancelado";
    }
}
