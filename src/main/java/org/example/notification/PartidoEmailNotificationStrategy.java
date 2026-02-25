package org.example.notification;

import org.example.model.Notificacion;

/**
 * Strategy mejorado para notificaciones de email con asunto personalizable
 * Especializado para notificaciones de cambio de estado de partidos
 */
public class PartidoEmailNotificationStrategy implements INotificacionStrategy {
    
    private final IAdapterEmail adapterEmail;
    private final String recipientEmail;
    private final String userName;
    
    public PartidoEmailNotificationStrategy(IAdapterEmail adapterEmail, String recipientEmail, String userName) {
        this.adapterEmail = adapterEmail;
        this.recipientEmail = recipientEmail;
        this.userName = userName;
    }
    
    @Override
    public void notificar(Notificacion notificacion) {
        // Extraer el asunto del mensaje (primera línea hasta el primer salto de línea)
        String mensaje = notificacion.getMensaje();
        String asunto = "UnoMás - Actualización de partido";
        String cuerpo = mensaje;
        
        // Intentar extraer asunto si el mensaje tiene formato específico
        if (mensaje.contains(" ha sido ")) {
            if (mensaje.contains("confirmado")) {
                asunto = "UnoMás - Tu partido ha sido confirmado";
            } else if (mensaje.contains("cancelado")) {
                asunto = "UnoMás - Tu partido ha sido cancelado";
            } else if (mensaje.contains("comenzado")) {
                asunto = "UnoMás - Tu partido ha comenzado";
            } else if (mensaje.contains("finalizado")) {
                asunto = "UnoMás - Tu partido ha finalizado";
            }
        }
        
        System.out.println("┌─────────────────────────────────────────────────");
        System.out.println("│ [PARTIDO EMAIL NOTIFICATION]");
        System.out.println("│  Usuario    : " + userName);
        System.out.println("│  Destinat.  : " + recipientEmail);
        System.out.println("│  Asunto     : " + asunto);
        System.out.println("│  Estado     : Enviando...");
        System.out.println("└─────────────────────────────────────────────────");
        
        adapterEmail.enviarEmail(recipientEmail, asunto, cuerpo);
    }
}