package org.example.test;

import org.example.service.EmailTemplateService;
import org.example.service.PartidoEstadoNotifier;
import org.example.model.*;

import java.time.LocalDateTime;

/**
 * Clase para probar la funcionalidad de notificaciones JavaMail
 * Ejecutar manualmente para validar configuración
 */
public class EmailNotificationTest {
    
    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBA DE NOTIFICACIONES EMAIL ===");
        
        // Test simple sin Spring context para evitar dependencias
        testEmailTemplateService();
    }
    
    private static void testEmailTemplateService() {
        EmailTemplateService service = new EmailTemplateService();
        
        // Test 1: Validar emails
        testValidacionEmails(service);
        
        // Test 2: Generar plantillas
        testGeneracionPlantillas(service);
        
        System.out.println("\n=== PRUEBAS BÁSICAS COMPLETADAS ===");
    }
    
    private static void testValidacionEmails(EmailTemplateService service) {
        System.out.println("\n--- Test 1: Validación de Emails ---");
        
        String[] emailsValidos = {"test@gmail.com", "usuario@hotmail.com", "player@example.org"};
        String[] emailsInvalidos = {"", null, "@gmail.com", "test@", "test.com", "t@t"};
        
        System.out.println("Emails válidos:");
        for (String email : emailsValidos) {
            boolean valido = service.validarEmail(email);
            System.out.println("  " + email + " -> " + (valido ? "✓ VÁLIDO" : "✗ INVÁLIDO"));
        }
        
        System.out.println("Emails inválidos:");
        for (String email : emailsInvalidos) {
            boolean valido = service.validarEmail(email);
            System.out.println("  " + email + " -> " + (valido ? "✗ DEBERÍA SER INVÁLIDO" : "✓ INVÁLIDO"));
        }
    }
    
    private static void testGeneracionPlantillas(EmailTemplateService service) {
        System.out.println("\n--- Test 2: Generación de Plantillas ---");
        
        try {
            // Crear objetos de prueba
            Usuario usuario = new Usuario(1L, "testUser", "test@example.com", "password");
            Deporte futbol = new Deporte(1L, "Fútbol");
            Ubicacion palermo = new Ubicacion("Palermo");
            
            Partido partido = new Partido(1L, futbol, 10, 90, palermo, LocalDateTime.now().plusDays(1));
            
            String[] estados = {"CONFIRMADO", "CANCELADO", "EN_JUEGO", "FINALIZADO"};
            
            for (String estado : estados) {
                System.out.println("\nEstado: " + estado);
                String asunto = service.generarAsunto(estado, futbol.getNombre());
                String mensaje = service.generarMensaje(usuario, partido, "ARMADO", estado);
                
                System.out.println("Asunto: " + asunto);
                System.out.println("Mensaje (primeras 100 chars): " + 
                                 mensaje.substring(0, Math.min(100, mensaje.length())) + "...");
            }
            
        } catch (Exception e) {
            System.err.println("Error en test de plantillas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}