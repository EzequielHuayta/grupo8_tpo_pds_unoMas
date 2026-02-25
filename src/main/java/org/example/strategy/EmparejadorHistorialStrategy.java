package org.example.strategy;

import org.example.model.Partido;
import org.example.model.Usuario;

import java.util.List;
import java.util.stream.Collectors;

public class EmparejadorHistorialStrategy implements IEmparejadorStrategy {

    private final List<Partido> todosLosPartidos;

    public EmparejadorHistorialStrategy(List<Partido> todosLosPartidos) {
        this.todosLosPartidos = todosLosPartidos;
    }

    @Override
    public List<Partido> buscarPartido(Usuario usuario, List<Partido> partidosDisponibles) {
        int misPartidos = usuario.getCantidadPartidosCompletados(todosLosPartidos);

        return partidosDisponibles.stream()
                .filter(p -> {
                    if (p.getJugadores().isEmpty()) {
                        // Si el partido está completamente vacío (muy raro), se podría
                        // asumir promedio 0, que permitirá que usuarios con 0 o 1 entren.
                        return misPartidos <= 1;
                    }

                    double promedio = p.getJugadores().stream()
                            .mapToInt(j -> j.getJugador().getCantidadPartidosCompletados(todosLosPartidos))
                            .average()
                            .orElse(0.0);

                    long promedioRedondeado = Math.round(promedio);

                    // Aceptar si el promedio está en el rango [misPartidos - 1, misPartidos + 1]
                    return promedioRedondeado >= (misPartidos - 1) && promedioRedondeado <= (misPartidos + 1);
                })
                .collect(Collectors.toList());
    }
}
