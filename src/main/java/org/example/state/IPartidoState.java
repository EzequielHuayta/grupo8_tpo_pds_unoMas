package org.example.state;

import org.example.model.Partido;

/**
 * Interfaz del patrón STATE para el ciclo de vida de un Partido.
 *
 * Según Refactoring Guru, la interfaz declara sólo los métodos que representan
 * comportamientos que varían entre estados. Aquí:
 * - avanzar() → transición al siguiente estado lógico
 * - cancelar() → cancelar el partido desde cualquier estado válido
 *
 * Los métodos tienen implementación por defecto que lanza
 * IllegalStateException,
 * así los estados terminales (Finalizado, Cancelado) no repiten código
 * boilerplate.
 */
public interface IPartidoState {

    default void avanzar(Partido partido) {
        throw new IllegalStateException(
                "No se puede avanzar el estado desde: " + getNombre());
    }

    default void cancelar(Partido partido) {
        throw new IllegalStateException(
                "No se puede cancelar el partido en estado: " + getNombre());
    }

    String getNombre();
}
