package org.example.repository;

import org.example.model.Partido;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PartidoRepositoryImpl implements IPartidoRepository {
    private final List<Partido> partidos;
    private Long nextId;
    private Long nextJugadorId;

    public PartidoRepositoryImpl() {
        this.partidos = new ArrayList<>();
        this.nextId = 1L;
        this.nextJugadorId = 1L;
    }

    @Override
    public Partido guardar(Partido partido) {
        partidos.add(partido);
        return partido;
    }

    @Override
    public Optional<Partido> buscarPorId(Long id) {
        return partidos.stream()
                .filter(p -> p.getIdPartido().equals(id))
                .findFirst();
    }

    @Override
    public List<Partido> listarTodos() {
        return new ArrayList<>(partidos);
    }

    @Override
    public Long generarId() {
        return nextId++;
    }

    @Override
    public Long generarIdJugador() {
        return nextJugadorId++;
    }
}
