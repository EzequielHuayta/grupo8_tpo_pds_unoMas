package org.example.controller;

import org.example.model.Deporte;
import org.example.model.Ubicacion;
import org.example.model.Usuario;
import org.example.nivel.NivelState;
import org.example.service.UsuarioService;

public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public Usuario registrarUsuario(String nombreUsuario, String email,
                                     String contrasena, Ubicacion ubicacion) {
        return usuarioService.registrarUsuario(nombreUsuario, email, contrasena, ubicacion);
    }

    public Usuario iniciarSesion(String email, String contrasena) {
        return usuarioService.iniciarSesion(email, contrasena);
    }

    public void setDeporteFavorito(Usuario usuario, Deporte deporte) {
        usuarioService.setDeporteFavorito(usuario, deporte);
    }

    public void setNivel(Usuario usuario, NivelState nivel) {
        usuarioService.setNivel(usuario, nivel);
    }
}
