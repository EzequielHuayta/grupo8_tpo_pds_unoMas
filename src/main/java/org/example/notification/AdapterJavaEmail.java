package org.example.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Adapter pattern: wraps Spring's JavaMailSender (which uses javax.mail internally)
 * so the rest of the app only depends on IAdapterEmail, not on JavaMail directly.
 */
@Component
public class AdapterJavaEmail implements IAdapterEmail {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    public AdapterJavaEmail(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarEmail(String destinatario, String asunto, String cuerpo) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(from);
            mensaje.setTo(destinatario);
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);
            mailSender.send(mensaje);
            System.out.println("[JavaMail] Email enviado a " + destinatario + " | Asunto: " + asunto);
        } catch (MailException e) {
            // Loguea el error pero no interrumpe el flujo de la app
            System.err.println("[JavaMail] Error al enviar email a " + destinatario + ": " + e.getMessage());
        }
    }
}
