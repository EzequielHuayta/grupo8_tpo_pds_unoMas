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
    private final Long userId;
    private final String nombreUsuario;

    public EmailNotificacionStrategy(IAdapterEmail adapterEmail, String recipientEmail,
            Long userId, String nombreUsuario) {
        this.adapterEmail = adapterEmail;
        this.recipientEmail = recipientEmail;
        this.userId = userId;
        this.nombreUsuario = nombreUsuario;
    }

    /** Compat: constructor sin userId/nombre (logs menos detallados) */
    public EmailNotificacionStrategy(IAdapterEmail adapterEmail, String recipientEmail) {
        this(adapterEmail, recipientEmail, null, null);
    }

    @Override
    public void notificar(Notificacion notificacion) {
        System.out.println("┌─────────────────────────────────────────────────");
        System.out.println("│ [EMAIL NOTIFICATION]");
        System.out.println("│  Estrategia : Email");
        if (userId != null)
            System.out.println("│  Usuario ID : " + userId);
        if (nombreUsuario != null)
            System.out.println("│  Usuario    : " + nombreUsuario);
        System.out.println("│  Destinat.  : " + recipientEmail);
        System.out.println("│  Mensaje    : " + notificacion.getMensaje());
        System.out.println("└─────────────────────────────────────────────────");
        adapterEmail.enviarEmail(
                recipientEmail,
                "UnoMás – Notificación de partido",
                notificacion.getMensaje());
    }
}
