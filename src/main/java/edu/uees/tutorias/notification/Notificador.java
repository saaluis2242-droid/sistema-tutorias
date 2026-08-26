package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.Usuario;

/**
 * Abstraccion para comunicar eventos relevantes a un usuario.
 *
 * ServicioReservas depende unicamente de esta interfaz (Dependency
 * Inversion Principle), no de un canal concreto. Esto permite agregar
 * nuevos canales de notificacion (SMS, push, etc.) sin modificar la
 * logica de negocio (Open/Closed Principle): basta con crear una nueva
 * implementacion de Notificador.
 */
public interface Notificador {

    void notificar(Usuario destinatario, String mensaje);
}
