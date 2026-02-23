package org.example.notification;

import org.example.model.Notificacion;

public interface IAdapterEmail {
    void enviarEmail(String destinatario, String asunto, String cuerpo);
}
