package org.example.repository;

import org.example.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository {
    Usuario guardar(Usuario usuario);
    Optional<Usuario> buscarPorId(Long id);
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorNombre(String nombreUsuario);
    List<Usuario> listarTodos();
    Long generarId();
}
