package org.example.strategy;

import org.example.model.Partido;
import org.example.model.Usuario;

import java.util.List;
import java.util.stream.Collectors;

public class EmparejadorUbicacionStrategy implements IEmparejadorStrategy {

    @Override
    public List<Partido> buscarPartido(Usuario usuario, List<Partido> partidosDisponibles) {
        if (usuario.getUbicacion() == null) return partidosDisponibles;
        String barrioUsuario = usuario.getUbicacion().getBarrio();
        return partidosDisponibles.stream()
                .filter(p -> p.getUbicacion() != null
                        && barrioUsuario.equalsIgnoreCase(p.getUbicacion().getBarrio()))
                .collect(Collectors.toList());
    }
}
