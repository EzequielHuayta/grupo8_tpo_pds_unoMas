package org.example.model;

public class Jugador {
    private Long idParticipante;
    private Usuario jugador;
    private boolean confirmacion;

    public Jugador(Long idParticipante, Usuario jugador) {
        this.idParticipante = idParticipante;
        this.jugador = jugador;
        this.confirmacion = false;
    }

    public void confirmar() {
        this.confirmacion = true;
        System.out.println("Jugador " + jugador.getNombreUsuario() + " confirmó participación.");
    }

    public Long getIdParticipante() {
        return idParticipante;
    }

    public Usuario getJugador() {
        return jugador;
    }

    public boolean isConfirmacion() {
        return confirmacion;
    }

    @Override
    public String toString() {
        return jugador.getNombreUsuario() + (confirmacion ? " [Confirmado]" : " [Pendiente]");
    }
}
