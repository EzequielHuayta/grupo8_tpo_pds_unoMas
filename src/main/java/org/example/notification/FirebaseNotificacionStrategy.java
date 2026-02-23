package org.example.notification;

import org.example.model.Notificacion;

public class FirebaseNotificacionStrategy implements INotificacionStrategy {

    @Override
    public void notificar(Notificacion notificacion) {
        // Aquí se integraría con Firebase Cloud Messaging
        System.out.println("[Firebase Push] " + notificacion.getMensaje());
    }
}
