package org.example.notification;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Store in-memory de notificaciones In-App.
 * El backend deposita mensajes aquí; el frontend los consulta y los elimina.
 */
@Component
public class InAppNotificacionStore {

    /** userId → lista de mensajes pendientes (no leídos) */
    private final Map<Long, List<String>> pendientes = new ConcurrentHashMap<>();

    /** Deposita un nuevo mensaje para el usuario dado. */
    public void agregar(Long userId, String mensaje) {
        pendientes.computeIfAbsent(userId, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(mensaje);
    }

    /** Devuelve los mensajes pendientes del usuario (sin borrarlos). */
    public List<String> obtener(Long userId) {
        return new ArrayList<>(pendientes.getOrDefault(userId, Collections.emptyList()));
    }

    /** Marca todos los mensajes del usuario como leídos (los elimina). */
    public void limpiar(Long userId) {
        pendientes.remove(userId);
    }
}
