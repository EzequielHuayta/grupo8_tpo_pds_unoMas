package org.example.state;

/**
 * Estado terminal: el partido fue cancelado. No acepta más transiciones.
 * Hereda los default methods de IPartidoState que lanzan IllegalStateException.
 */
public class CanceladoState implements IPartidoState {

    // avanzar() y cancelar() usan el default de la interfaz → lanzan
    // IllegalStateException

    @Override
    public String getNombre() {
        return "Cancelado";
    }
}
