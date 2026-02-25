package org.example.observer;

public interface ISubject {
    void agregarObserver(IObserver observer);
    void eliminarObserver(IObserver observer);
    void notificarObservers();
}
