package org.example.nivel;

public class Intermedio extends NivelState {
    public Intermedio() {
        super(2);
    }

    @Override
    public String getNombre() {
        return "Intermedio";
    }
}
