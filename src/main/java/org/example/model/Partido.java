package org.example.model;

import org.example.nivel.NivelState;
import org.example.nivel.Principiante;
import org.example.nivel.Avanzado;
import org.example.observer.IObserver;
import org.example.observer.ISubject;
import org.example.state.IPartidoState;
import org.example.state.NecesitamosJugadoresState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Partido implements ISubject {
    private Long idPartido;
    private Deporte deporte;
    private List<Jugador> jugadores;
    private int cantidadJugadores;
    private int duracionMinutos;
    private Ubicacion ubicacion;
    private LocalDateTime horario;
    private IPartidoState estado;
    private NivelState nivelMinimo;
    private NivelState nivelMaximo;
    private List<IObserver> observers;
    private Long creadorId;   // ID del usuario que creó el partido

    public Partido(Long idPartido, Deporte deporte, int cantidadJugadores,
                   int duracionMinutos, Ubicacion ubicacion, LocalDateTime horario) {
        this.idPartido = idPartido;
        this.deporte = deporte;
        this.cantidadJugadores = cantidadJugadores;
        this.duracionMinutos = duracionMinutos;
        this.ubicacion = ubicacion;
        this.horario = horario;
        this.estado = new NecesitamosJugadoresState();
        this.jugadores = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.nivelMinimo = new Principiante();
        this.nivelMaximo = new Avanzado();
    }

    // ISubject implementation
    @Override
    public void agregarObserver(IObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void eliminarObserver(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notificarObservers() {
        String mensaje = "Partido #" + idPartido + " de " + deporte.getNombre()
                + " - Estado: " + estado.getNombre();
        Notificacion notificacion = new Notificacion(mensaje);
        for (IObserver observer : observers) {
            observer.recibirNotificacion(notificacion);
        }
    }

    // Métodos que delegan al estado
    public void agregarJugador(Jugador jugador) {
        jugadores.add(jugador);
        agregarObserver(jugador.getJugador());
        estado.agregarJugador(this);
    }

    public void confirmar() {
        estado.confirmar(this);
    }

    public void iniciar() {
        estado.iniciar(this);
    }

    public void finalizar() {
        estado.finalizar(this);
    }

    public void cancelar() {
        estado.cancelar(this);
    }

    // Getters y Setters
    public Long getIdPartido() {
        return idPartido;
    }

    public Deporte getDeporte() {
        return deporte;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public int getCantidadJugadores() {
        return cantidadJugadores;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public IPartidoState getEstado() {
        return estado;
    }

    public void setEstado(IPartidoState estado) {
        this.estado = estado;
    }

    public NivelState getNivelMinimo() {
        return nivelMinimo;
    }

    public void setNivelMinimo(NivelState nivelMinimo) {
        this.nivelMinimo = nivelMinimo;
    }

    public NivelState getNivelMaximo() {
        return nivelMaximo;
    }

    public void setNivelMaximo(NivelState nivelMaximo) {
        this.nivelMaximo = nivelMaximo;
    }

    public List<IObserver> getObservers() {
        return observers;
    }

    public Long getCreadorId() {
        return creadorId;
    }

    public void setCreadorId(Long creadorId) {
        this.creadorId = creadorId;
    }

    @Override
    public String toString() {
        return "Partido #" + idPartido + " [" + deporte.getNombre() + "] - " + estado.getNombre()
                + " (" + jugadores.size() + "/" + cantidadJugadores + " jugadores)";
    }
}
