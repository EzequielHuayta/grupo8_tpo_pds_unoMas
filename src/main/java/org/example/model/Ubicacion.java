package org.example.model;

public class Ubicacion {
    private String barrio;

    public Ubicacion(String barrio) {
        this.barrio = barrio;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    @Override
    public String toString() {
        return barrio;
    }
}
