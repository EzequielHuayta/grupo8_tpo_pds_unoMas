package org.example.observer;

import org.example.model.Notificacion;

public interface IObserver {
    void recibirNotificacion(Notificacion notificacion);
}
