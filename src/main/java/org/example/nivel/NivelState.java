package org.example.nivel;

public abstract class NivelState {
    private int peso;

    public NivelState(int peso) {
        this.peso = peso;
    }

    public int getPesoNivel() {
        return peso;
    }

    public abstract String getNombre();

    @Override
    public String toString() {
        return getNombre() + " (peso: " + peso + ")";
    }
}
