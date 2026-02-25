package org.example.service;

import org.example.model.Partido;
import org.example.model.Usuario;
import org.example.notification.INotificacionStrategy;
import org.example.notification.EmailNotificacionStrategy;
import org.example.notification.PartidoEmailNotificationStrategy;
import org.example.notification.IAdapterEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio especializado para manejar notificaciones de cambios de estado de partidos
 */
@Service
public class PartidoEstadoNotifier {
    
    @Autowired
    private EmailTemplateService emailTemplateService;
    
    @Autowired
    private IAdapterEmail adapterEmail;
    
    @Value("${app.notifications.email.enabled:true}")
    private boolean emailNotificationsEnabled;
    
    /**
     * Notifica a todos los usuarios de un partido sobre el cambio de estado
     */
    public void notificarCambioEstado(Partido partido, String estadoAnterior, String estadoNuevo) {
        if (!emailNotificationsEnabled) {
            return;
        }
        
        List<Usuario> usuarios = obtenerUsuariosDelPartido(partido);
        
        for (Usuario usuario : usuarios) {
            notificarUsuario(usuario, partido, estadoAnterior, estadoNuevo);
        }
    }
    
    /**
     * Notifica a un usuario específico sobre el cambio de estado
     */
    private void notificarUsuario(Usuario usuario, Partido partido, String estadoAnterior, String estadoNuevo) {
        try {
            // Validar email del usuario
            if (!emailTemplateService.validarEmail(usuario.getEmail())) {
                System.out.println("Email inválido para usuario: " + usuario.getNombreUsuario() + 
                                 " (" + usuario.getEmail() + ")");
                return;
            }
            
            // Generar contenido del email
            String mensaje = emailTemplateService.generarMensaje(usuario, partido, estadoAnterior, estadoNuevo);
            
            // Crear notificación con el mensaje personalizado
            org.example.model.Notificacion notificacion = new org.example.model.Notificacion(mensaje);
            
            // Usar la estrategia de notificación del usuario o crear una temporal para emails
            INotificacionStrategy estrategia = usuario.getEstrategiaNotificacion();
            
            // Si no tiene estrategia o no es de email, crear una estrategia temporal para este envío
            if (estrategia == null || !(estrategia instanceof EmailNotificacionStrategy)) {
                estrategia = new PartidoEmailNotificationStrategy(
                    adapterEmail, 
                    usuario.getEmail(), 
                    usuario.getNombreUsuario()
                );
            }
            
            estrategia.notificar(notificacion);
            System.out.println("Notificación enviada a " + usuario.getEmail() + 
                             " - Estado: " + estadoAnterior + " → " + estadoNuevo);
            
        } catch (Exception e) {
            System.err.println("Error al enviar notificación a " + usuario.getEmail() + ": " + e.getMessage());
        }
    }
    
    /**
     * Obtiene la lista de usuarios que participan en el partido
     */
    private List<Usuario> obtenerUsuariosDelPartido(Partido partido) {
        return partido.getJugadores().stream()
                     .map(jugador -> jugador.getJugador())
                     .distinct()
                     .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Verifica si se debe enviar notificación para el cambio de estado
     */
    public boolean debeNotificar(String estadoAnterior, String estadoNuevo) {
        // Estados que generan notificaciones
        String[] estadosNotificables = {"CONFIRMADO", "CANCELADO", "EN_JUEGO", "FINALIZADO"};
        
        for (String estado : estadosNotificables) {
            if (estado.equals(estadoNuevo.toUpperCase())) {
                return true;
            }
        }
        
        return false;
    }
}