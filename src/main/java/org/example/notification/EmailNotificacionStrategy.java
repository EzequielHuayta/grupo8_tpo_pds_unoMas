package org.example.notification;

import org.example.model.Notificacion;

/**
 * Strategy: envía la notificación por email usando el adapter de JavaMail.
 * El recipientEmail se fija al crear la estrategia (cuando el usuario
 * elige este canal de notificación).
 */
public class EmailNotificacionStrategy implements INotificacionStrategy {

    private final IAdapterEmail adapterEmail;
    private final String recipientEmail;

    public EmailNotificacionStrategy(IAdapterEmail adapterEmail, String recipientEmail) {
        this.adapterEmail = adapterEmail;
        this.recipientEmail = recipientEmail;
    }

    @Override
    public void notificar(Notificacion notificacion) {
        adapterEmail.enviarEmail(
                recipientEmail,
                "UnoMás – Notificación de partido",
                notificacion.getMensaje()
        );
    }
}
