package org.example.strategy;

import org.example.model.Partido;
import org.example.model.Usuario;

import java.util.List;
import java.util.stream.Collectors;

public class EmparejadorNivelStrategy implements IEmparejadorStrategy {

    @Override
    public List<Partido> buscarPartido(Usuario usuario, List<Partido> partidosDisponibles) {
        int nivelUsuario = usuario.getNivel().getPesoNivel();
        return partidosDisponibles.stream()
                .filter(p -> nivelUsuario >= p.getNivelMinimo().getPesoNivel()
                          && nivelUsuario <= p.getNivelMaximo().getPesoNivel())
                .collect(Collectors.toList());
    }
}
