package org.example.view;

import org.example.model.Usuario;

import java.util.List;

public class UsuarioView {

    public void mostrarUsuario(Usuario usuario) {
        System.out.println("=== Detalle del Usuario ===");
        System.out.println("ID: " + usuario.getIdUsuario());
        System.out.println("Nombre: " + usuario.getNombreUsuario());
        System.out.println("Email: " + usuario.getEmail());
        System.out.println("Nivel: " + usuario.getNivel());
        if (usuario.getDeporteFavorito() != null) {
            System.out.println("Deporte favorito: " + usuario.getDeporteFavorito().getNombre());
        }
        if (usuario.getUbicacion() != null) {
            System.out.println("Ubicación: " + usuario.getUbicacion());
        }
        System.out.println("============================");
    }

    public void mostrarListaUsuarios(List<Usuario> usuarios) {
        System.out.println("=== Lista de Usuarios ===");
        for (Usuario u : usuarios) {
            System.out.println("  " + u);
        }
        System.out.println("=========================");
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
}
