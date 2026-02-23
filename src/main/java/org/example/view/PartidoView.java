package org.example.view;

import org.example.model.Jugador;
import org.example.model.Partido;

import java.util.List;

public class PartidoView {

    public void mostrarPartido(Partido partido) {
        System.out.println("=== Detalle del Partido ===");
        System.out.println("ID: " + partido.getIdPartido());
        System.out.println("Deporte: " + partido.getDeporte().getNombre());
        System.out.println("Estado: " + partido.getEstado().getNombre());
        System.out.println("Jugadores: " + partido.getJugadores().size() + "/" + partido.getCantidadJugadores());
        System.out.println("Duración: " + partido.getDuracionMinutos() + " min");
        System.out.println("Ubicación: " + partido.getUbicacion());
        System.out.println("Horario: " + partido.getHorario());
        System.out.println("Nivel: " + partido.getNivelMinimo().getNombre()
                + " - " + partido.getNivelMaximo().getNombre());
        if (!partido.getJugadores().isEmpty()) {
            System.out.println("--- Jugadores ---");
            for (Jugador j : partido.getJugadores()) {
                System.out.println("  " + j);
            }
        }
        System.out.println("===========================");
    }

    public void mostrarListaPartidos(List<Partido> partidos) {
        if (partidos.isEmpty()) {
            System.out.println("No se encontraron partidos.");
            return;
        }
        System.out.println("=== Lista de Partidos ===");
        for (Partido p : partidos) {
            System.out.println("  " + p);
        }
        System.out.println("=========================");
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
}
