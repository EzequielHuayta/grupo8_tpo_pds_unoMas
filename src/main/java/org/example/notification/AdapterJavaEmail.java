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
 * Incluye lógica de reintento y mejor manejo de errores.
 */
@Component
public class AdapterJavaEmail implements IAdapterEmail {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${spring.mail.username}")
    private String username;
    
    @Value("${app.notifications.email.retry.maxAttempts:3}")
    private int maxRetryAttempts;
    
    @Value("${app.notifications.email.retry.delay:1000}")
    private long retryDelayMs;

    /** Credencial placeholder que viene por defecto en application.properties */
    private static final String PLACEHOLDER = "tu.email@gmail.com";

    public AdapterJavaEmail(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarEmail(String destinatario, String asunto, String cuerpo) {
        // Validar email destino
        if (!validarEmail(destinatario)) {
            System.err.println("[JavaMail] Email inválido: " + destinatario);
            return;
        }
        
        if (esModoConsola()) {
            enviarEmailModoConsola(destinatario, asunto, cuerpo);
            return;
        }

        // Intentar envío real con reintentos
        enviarEmailConReintentos(destinatario, asunto, cuerpo);
    }
    
    /**
     * Determina si está en modo consola (sin credenciales reales)
     */
    private boolean esModoConsola() {
        return PLACEHOLDER.equals(username) || username == null || username.trim().isEmpty();
    }
    
    /**
     * Envía email en modo consola (solo logging)
     */
    private void enviarEmailModoConsola(String destinatario, String asunto, String cuerpo) {
        System.out.println("┌─────────────────────────────────────────────────");
        System.out.println("│ [EMAIL - MODO CONSOLA]");
        System.out.println("│  De      : " + from);
        System.out.println("│  Para    : " + destinatario);
        System.out.println("│  Asunto  : " + asunto);
        System.out.println("│  Mensaje : " + cuerpo);
        System.out.println("└─────────────────────────────────────────────────");
    }
    
    /**
     * Envía email real con lógica de reintentos
     */
    private void enviarEmailConReintentos(String destinatario, String asunto, String cuerpo) {
        int intentos = 0;
        Exception ultimaExcepcion = null;
        
        while (intentos < maxRetryAttempts) {
            try {
                SimpleMailMessage mensaje = new SimpleMailMessage();
                mensaje.setFrom(from);
                mensaje.setTo(destinatario);
                mensaje.setSubject(asunto);
                mensaje.setText(cuerpo);
                
                mailSender.send(mensaje);
                System.out.println("[JavaMail] Email enviado exitosamente a " + destinatario + 
                                 " | Asunto: " + asunto + " | Intento: " + (intentos + 1));
                return; // Éxito, salir
                
            } catch (MailException e) {
                intentos++;
                ultimaExcepcion = e;
                
                if (intentos < maxRetryAttempts) {
                    System.out.println("[JavaMail] Error en intento " + intentos + " para " + destinatario + 
                                     ": " + e.getMessage() + " | Reintentando en " + retryDelayMs + "ms...");
                    try {
                        Thread.sleep(retryDelayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        System.err.println("[JavaMail] Reintento interrumpido: " + ie.getMessage());
                        break;
                    }
                } else {
                    System.err.println("[JavaMail] Error definitivo al enviar email a " + destinatario + 
                                     " después de " + maxRetryAttempts + " intentos: " + e.getMessage());
                }
            }
        }
        
        // Si llegamos aquí, todos los intentos fallaron
        if (ultimaExcepcion != null) {
            System.err.println("[JavaMail] Email no enviado después de " + maxRetryAttempts + 
                             " intentos a " + destinatario + ". Última excepción: " + ultimaExcepcion.getMessage());
        }
    }
    
    /**
     * Validación básica de email
     */
    private boolean validarEmail(String email) {
        return email != null && 
               email.contains("@") && 
               email.contains(".") && 
               email.length() > 5 &&
               !email.startsWith("@") &&
               !email.endsWith("@");
    }
}
