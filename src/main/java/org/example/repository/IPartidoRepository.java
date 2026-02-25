package org.example.repository;

import org.example.model.Partido;
import java.util.List;
import java.util.Optional;

public interface IPartidoRepository {
    Partido guardar(Partido partido);
    Optional<Partido> buscarPorId(Long id);
    List<Partido> listarTodos();
    Long generarId();
    Long generarIdJugador();
}
