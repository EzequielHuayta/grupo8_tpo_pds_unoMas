package org.example.model;

import org.example.observer.IObserver;

import java.util.ArrayList;
import java.util.List;

public class Deporte implements IObserver {
    private Long idDeporte;
    private String nombre;
    private List<Usuario> usuarios;

    public Deporte(Long idDeporte, String nombre) {
        this.idDeporte = idDeporte;
        this.nombre = nombre;
        this.usuarios = new ArrayList<>();
    }

    public void agregarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public void eliminarUsuario(Usuario usuario) {
        usuarios.remove(usuario);
    }

    @Override
    public void recibirNotificacion(Notificacion notificacion) {
        // Propaga la notificación a todos los usuarios interesados en este deporte
        for (Usuario usuario : usuarios) {
            usuario.recibirNotificacion(notificacion);
        }
    }

    public Long getIdDeporte() {
        return idDeporte;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deporte deporte = (Deporte) o;
        return idDeporte.equals(deporte.idDeporte);
    }

    @Override
    public int hashCode() {
        return idDeporte.hashCode();
    }

    @Override
    public String toString() {
        return nombre;
    }
}
