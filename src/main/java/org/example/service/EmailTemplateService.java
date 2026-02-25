package org.example.service;

import org.example.model.Partido;
import org.example.model.Usuario;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailTemplateService {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Genera el asunto del email según el estado del partido
     */
    public String generarAsunto(String estadoPartido, String deporte) {
        switch (estadoPartido.toUpperCase()) {
            case "CONFIRMADO":
                return "UnoMás - Tu partido de " + deporte + " ha sido confirmado";
            case "CANCELADO":
                return "UnoMás - Tu partido de " + deporte + " ha sido cancelado";
            case "EN_JUEGO":
                return "UnoMás - Tu partido de " + deporte + " ha comenzado";
            case "FINALIZADO":
                return "UnoMás - Tu partido de " + deporte + " ha finalizado";
            default:
                return "UnoMás - Actualización de tu partido de " + deporte;
        }
    }
    
    /**
     * Genera el cuerpo del mensaje según el estado del partido
     */
    public String generarMensaje(Usuario usuario, Partido partido, String estadoAnterior, String estadoNuevo) {
        StringBuilder mensaje = new StringBuilder();
        
        mensaje.append("Hola ").append(usuario.getNombreUsuario()).append(",\n\n");
        
        switch (estadoNuevo.toUpperCase()) {
            case "CONFIRMADO":
                mensaje.append("¡Excelente noticia! Tu partido de ").append(partido.getDeporte().getNombre())
                       .append(" ha sido confirmado y está listo para jugar.\n\n")
                       .append("Detalles del partido:\n")
                       .append(generarDetallesPartido(partido))
                       .append("\n¡Nos vemos en la cancha!");
                break;
                
            case "CANCELADO":
                mensaje.append("Lamentamos informarte que tu partido de ").append(partido.getDeporte().getNombre())
                       .append(" ha sido cancelado.\n\n")
                       .append("Detalles del partido cancelado:\n")
                       .append(generarDetallesPartido(partido))
                       .append("\nNo te preocupes, puedes buscar otros partidos disponibles en la aplicación.");
                break;
                
            case "EN_JUEGO":
                mensaje.append("¡Tu partido de ").append(partido.getDeporte().getNombre())
                       .append(" ha comenzado!\n\n")
                       .append("Detalles del partido:\n")
                       .append(generarDetallesPartido(partido))
                       .append("\n¡Disfruta el juego y que tengas un excelente partido!");
                break;
                
            case "FINALIZADO":
                mensaje.append("Tu partido de ").append(partido.getDeporte().getNombre())
                       .append(" ha finalizado.\n\n")
                       .append("Detalles del partido:\n")
                       .append(generarDetallesPartido(partido))
                       .append("\n¡Esperamos que hayas disfrutado! No olvides calificar la experiencia en la aplicación.");
                break;
                
            default:
                mensaje.append("Tu partido de ").append(partido.getDeporte().getNombre())
                       .append(" ha cambiado de estado: ").append(estadoAnterior)
                       .append(" → ").append(estadoNuevo).append("\n\n")
                       .append("Detalles del partido:\n")
                       .append(generarDetallesPartido(partido));
                break;
        }
        
        mensaje.append("\n\n--\n")
               .append("Equipo UnoMás\n")
               .append("¡Tu plataforma favorita para encontrar compañeros de juego!");
        
        return mensaje.toString();
    }
    
    /**
     * Genera los detalles del partido para incluir en el mensaje
     */
    private String generarDetallesPartido(Partido partido) {
        StringBuilder detalles = new StringBuilder();
        
        detalles.append("• Fecha y hora: ").append(partido.getHorario().format(DATE_FORMAT)).append("\n");
        detalles.append("• Ubicación: ").append(partido.getUbicacion().getBarrio()).append("\n");
        detalles.append("• Deporte: ").append(partido.getDeporte().getNombre()).append("\n");
        detalles.append("• Duración: ").append(partido.getDuracionMinutos()).append(" minutos\n");
        detalles.append("• Jugadores: ").append(partido.getJugadores().size())
                .append("/").append(partido.getCantidadJugadores()).append("\n");
        
        if (partido.getNivelMinimo() != null) {
            detalles.append("• Nivel requerido: ").append(partido.getNivelMinimo().getClass().getSimpleName());
            if (partido.getNivelMaximo() != null && !partido.getNivelMinimo().equals(partido.getNivelMaximo())) {
                detalles.append(" - ").append(partido.getNivelMaximo().getClass().getSimpleName());
            }
            detalles.append("\n");
        }
        
        return detalles.toString();
    }
    
    /**
     * Valida que el email sea válido
     */
    public boolean validarEmail(String email) {
        return email != null && 
               email.contains("@") && 
               email.contains(".") && 
               email.length() > 5 &&
               !email.startsWith("@") &&
               !email.endsWith("@");
    }
}