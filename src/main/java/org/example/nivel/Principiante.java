package org.example.nivel;

public class Principiante extends NivelState {
    public Principiante() {
        super(1);
    }

    @Override
    public String getNombre() {
        return "Principiante";
    }
}
