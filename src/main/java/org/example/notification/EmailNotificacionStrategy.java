package org.example.notification;

import org.example.model.Notificacion;

public class EmailNotificacionStrategy implements INotificacionStrategy {

    private final IAdapterEmail adapterEmail;

    public EmailNotificacionStrategy(IAdapterEmail adapterEmail) {
        this.adapterEmail = adapterEmail;
    }

    @Override
    public void notificar(Notificacion notificacion) {
        adapterEmail.enviarEmail(
                "usuario@email.com",
                notificacion.getTipo().name(),
                notificacion.getMensaje()
        );
    }
}
