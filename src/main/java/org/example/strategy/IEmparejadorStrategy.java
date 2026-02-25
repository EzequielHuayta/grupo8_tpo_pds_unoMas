package org.example.strategy;

import org.example.model.Partido;
import org.example.model.Usuario;
import java.util.List;

public interface IEmparejadorStrategy {
    List<Partido> buscarPartido(Usuario usuario, List<Partido> partidosDisponibles);
}
