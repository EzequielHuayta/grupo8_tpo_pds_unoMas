package org.example.model;

import org.example.enums.TipoNotificacion;
import java.time.LocalDateTime;

public class Notificacion {
    private String mensaje;
    private TipoNotificacion tipo;
    private LocalDateTime fecha;
    private Long partidoId;
    private Long destinatarioId;

    public Notificacion(String mensaje, TipoNotificacion tipo, Long partidoId) {
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.fecha = LocalDateTime.now();
        this.partidoId = partidoId;
    }

    public Notificacion(String mensaje, TipoNotificacion tipo, Long partidoId, Long destinatarioId) {
        this(mensaje, tipo, partidoId);
        this.destinatarioId = destinatarioId;
    }

    public String getMensaje() {
        return mensaje;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Long getPartidoId() {
        return partidoId;
    }

    public Long getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(Long destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    @Override
    public String toString() {
        return "[" + tipo + "] " + mensaje + " (Partido #" + partidoId + " - " + fecha + ")";
    }
}
