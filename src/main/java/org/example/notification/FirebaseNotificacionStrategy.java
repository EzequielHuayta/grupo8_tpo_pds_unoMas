package org.example.notification;

import org.example.model.Notificacion;

/**
 * Strategy de notificación In-App.
 * Deposita el mensaje en InAppNotificacionStore y lo loguea por consola.
 */
public class FirebaseNotificacionStrategy implements INotificacionStrategy {

    private final InAppNotificacionStore store;
    private final Long userId;
    private final String nombreUsuario;

    public FirebaseNotificacionStrategy(InAppNotificacionStore store, Long userId, String nombreUsuario) {
        this.store = store;
        this.userId = userId;
        this.nombreUsuario = nombreUsuario;
    }

    @Override
    public void notificar(Notificacion notificacion) {
        store.agregar(userId, notificacion.getMensaje());
        System.out.println("┌─────────────────────────────────────────────────");
        System.out.println("│ [IN-APP NOTIFICATION]");
        System.out.println("│  Estrategia : In-App (Firebase)");
        System.out.println("│  Usuario ID : " + userId);
        System.out.println("│  Usuario    : " + nombreUsuario);
        System.out.println("│  Mensaje    : " + notificacion.getMensaje());
        System.out.println("└─────────────────────────────────────────────────");
    }
}
