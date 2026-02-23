package org.example.nivel;

public class Avanzado extends NivelState {
    public Avanzado() {
        super(3);
    }

    @Override
    public String getNombre() {
        return "Avanzado";
    }
}
