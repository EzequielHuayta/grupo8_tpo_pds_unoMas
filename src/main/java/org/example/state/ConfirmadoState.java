package org.example.state;

import org.example.model.Partido;

public class ConfirmadoState implements IPartidoState {

    @Override
    public void agregarJugador(Partido partido) {
        throw new IllegalStateException("No se pueden agregar jugadores: el partido está confirmado.");
    }

    @Override
    public void confirmar(Partido partido) {
        throw new IllegalStateException("El partido ya está confirmado.");
    }

    @Override
    public void iniciar(Partido partido) {
        partido.setEstado(new EnJuegoState());
        System.out.println("Partido #" + partido.getIdPartido() + " → En juego");
        partido.notificarObservers();
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
        return "Confirmado";
    }
}
