package edu.uees.tutorias.notification.factory;

import edu.uees.tutorias.domain.Usuario;
import edu.uees.tutorias.notification.Notificador;

/**
 * Creator del patron Factory Method (recuperado de Ae2 e integrado al
 * dominio real en este incremento).
 *
 * <p>Problema real que resuelve: en Ae1, el codigo cliente (por ejemplo
 * Main, o mas tarde NotificadorObservador) construia directamente
 * {@code new NotificadorEmailConsola()} o {@code new NotificadorSmsConsola()}.
 * Eso significa que, cada vez que un usuario prefiere un canal distinto
 * (SMS, WhatsApp), el codigo cliente tendria que decidir con un
 * if/else que clase concreta instanciar, mezclando esa decision con la
 * logica de negocio.</p>
 *
 * <p>Con Factory Method, el codigo cliente solo conoce esta clase y la
 * interfaz {@link Notificador}: cada subclase concreta (ConcreteCreator)
 * ya sabe que producto le corresponde. Agregar un canal nuevo significa
 * agregar una clase, no modificar las existentes (Open/Closed
 * Principle).</p>
 */
public abstract class NotificadorCreator {

    /** Factory Method: cada subclase decide que Notificador concreto crear. */
    protected abstract Notificador crearNotificador();

    /**
     * Operacion estable que usa el producto creado por el metodo de
     * fabrica. No necesita modificarse cuando se agrega un canal nuevo.
     */
    public void notificar(Usuario destinatario, String mensaje) {
        Notificador notificador = crearNotificador();
        notificador.notificar(destinatario, mensaje);
    }
}
