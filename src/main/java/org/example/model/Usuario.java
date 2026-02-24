package org.example.model;

import org.example.nivel.NivelState;
import org.example.nivel.Principiante;
import org.example.notification.INotificacionStrategy;
import org.example.observer.IObserver;
import org.example.strategy.IEmparejadorStrategy;

import java.util.ArrayList;
import java.util.List;

public class Usuario implements IObserver {
    private final Long idUsuario;
    private String nombreUsuario;
    private String email;
    private String contrasena;
    private Deporte deporteFavorito;
    private NivelState nivel;
    private INotificacionStrategy estrategiaNotificacion;
    private IEmparejadorStrategy estrategiaEmparejamiento;
    private Ubicacion ubicacion;
    private List<Long> historialPartidoIds;

    public Usuario(Long idUsuario, String nombreUsuario, String email, String contrasena) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.contrasena = contrasena;
        this.nivel = new Principiante();
        this.historialPartidoIds = new ArrayList<>();
    }

    @Override
    public void recibirNotificacion(Notificacion notificacion) {
        if (estrategiaNotificacion != null) {
            estrategiaNotificacion.notificar(notificacion);
        }
        System.out.println("Usuario " + nombreUsuario + " recibió: " + notificacion.getMensaje());
    }

    public List<Partido> buscarPartido(List<Partido> partidosDisponibles) {
        if (estrategiaEmparejamiento == null) {
            return partidosDisponibles;
        }
        return estrategiaEmparejamiento.buscarPartido(this, partidosDisponibles);
    }

    public void setEstrategiaNotificacion(INotificacionStrategy estrategia) {
        this.estrategiaNotificacion = estrategia;
    }

    public void cambiarEstrategiaEmparejamiento(IEmparejadorStrategy estrategia) {
        this.estrategiaEmparejamiento = estrategia;
    }

    // Getters y Setters
    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Deporte getDeporteFavorito() {
        return deporteFavorito;
    }

    public void setDeporteFavorito(Deporte deporteFavorito) {
        this.deporteFavorito = deporteFavorito;
    }

    public NivelState getNivel() {
        return nivel;
    }

    public void setNivel(NivelState nivel) {
        this.nivel = nivel;
    }

    public INotificacionStrategy getEstrategiaNotificacion() {
        return estrategiaNotificacion;
    }

    public IEmparejadorStrategy getEstrategiaEmparejamiento() {
        return estrategiaEmparejamiento;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public List<Long> getHistorialPartidoIds() {
        return historialPartidoIds;
    }

    public void agregarPartidoAlHistorial(Partido partido) {
        Long pid = partido.getIdPartido();
        if (!historialPartidoIds.contains(pid)) {
            historialPartidoIds.add(pid);
        }
    }

    /**
     * Cuenta cuántos partidos del historial están en estado "Finalizado".
     * 
     * @param todosLosPartidos lista completa de partidos del sistema
     */
    public int getCantidadPartidosCompletados(List<Partido> todosLosPartidos) {
        return (int) todosLosPartidos.stream()
                .filter(p -> historialPartidoIds.contains(p.getIdPartido()))
                .filter(p -> "Finalizado".equals(p.getEstado().getNombre()))
                .count();
    }

    @Override
    public String toString() {
        return nombreUsuario + " (" + email + ") - Nivel: " + nivel;
    }
}
