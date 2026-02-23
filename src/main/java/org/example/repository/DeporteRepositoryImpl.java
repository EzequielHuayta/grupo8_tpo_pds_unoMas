package org.example.repository;

import org.example.model.Deporte;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class DeporteRepositoryImpl {

    private final List<Deporte> deportes = new ArrayList<>(Arrays.asList(
            new Deporte(1L, "Fútbol"),
            new Deporte(2L, "Básquet"),
            new Deporte(3L, "Tenis"),
            new Deporte(4L, "Vóley"),
            new Deporte(5L, "Paddle")
    ));

    public List<Deporte> listarTodos() {
        return new ArrayList<>(deportes);
    }

    public Optional<Deporte> buscarPorId(Long id) {
        return deportes.stream().filter(d -> d.getIdDeporte().equals(id)).findFirst();
    }
}
