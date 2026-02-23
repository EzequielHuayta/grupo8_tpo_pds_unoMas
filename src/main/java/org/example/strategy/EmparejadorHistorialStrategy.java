package org.example.strategy;

import org.example.model.Partido;
import org.example.model.Usuario;

import java.util.List;
import java.util.stream.Collectors;

public class EmparejadorHistorialStrategy implements IEmparejadorStrategy {

    @Override
    public List<Partido> buscarPartido(Usuario usuario, List<Partido> partidosDisponibles) {
        List<Partido> historial = usuario.getHistorialPartidos();
        return partidosDisponibles.stream()
                .filter(p -> historial.stream().anyMatch(
                        h -> h.getDeporte().equals(p.getDeporte())))
                .collect(Collectors.toList());
    }
}
