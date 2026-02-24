package org.example.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Adapter pattern: wraps Spring's JavaMailSender.
 * Si las credenciales SMTP no están configuradas (valor placeholder),
 * opera en modo "consola" y loguea el email sin intentar enviarlo.
 */
@Component
public class AdapterJavaEmail implements IAdapterEmail {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${spring.mail.username}")
    private String username;

    /** Credencial placeholder que viene por defecto en application.properties */
    private static final String PLACEHOLDER = "tu.email@gmail.com";

    public AdapterJavaEmail(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarEmail(String destinatario, String asunto, String cuerpo) {
        if (PLACEHOLDER.equals(username) || username == null || username.trim().isEmpty()) {
            // Modo consola: sin credenciales reales, solo loguear
            System.out.println("┌─────────────────────────────────────────────────");
            System.out.println("│ [EMAIL - MODO CONSOLA]");
            System.out.println("│  De      : " + from);
            System.out.println("│  Para    : " + destinatario);
            System.out.println("│  Asunto  : " + asunto);
            System.out.println("│  Mensaje : " + cuerpo);
            System.out.println("└─────────────────────────────────────────────────");
            return;
        }

        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(from);
            mensaje.setTo(destinatario);
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);
            mailSender.send(mensaje);
            System.out.println("[JavaMail] Email enviado a " + destinatario + " | Asunto: " + asunto);
        } catch (MailException e) {
            System.err.println("[JavaMail] Error al enviar email a " + destinatario + ": " + e.getMessage());
        }
    }
}
