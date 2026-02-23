package org.example.state;

import org.example.model.Partido;

public interface IPartidoState {
    void agregarJugador(Partido partido);
    void confirmar(Partido partido);
    void iniciar(Partido partido);
    void finalizar(Partido partido);
    void cancelar(Partido partido);
    String getNombre();
}
