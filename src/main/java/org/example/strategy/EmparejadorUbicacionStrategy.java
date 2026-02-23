package org.example.strategy;

import org.example.model.Partido;
import org.example.model.Usuario;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EmparejadorUbicacionStrategy implements IEmparejadorStrategy {

    private static final double RADIO_MAXIMO_KM = 50.0;

    @Override
    public List<Partido> buscarPartido(Usuario usuario, List<Partido> partidosDisponibles) {
        return partidosDisponibles.stream()
                .filter(p -> usuario.getUbicacion().calcularDistancia(p.getUbicacion()) <= RADIO_MAXIMO_KM)
                .sorted(Comparator.comparingDouble(
                        p -> usuario.getUbicacion().calcularDistancia(p.getUbicacion())))
                .collect(Collectors.toList());
    }
}
