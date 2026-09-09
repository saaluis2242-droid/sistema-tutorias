package edu.uees.tutorias.notification.factory;

import edu.uees.tutorias.notification.Notificador;
import edu.uees.tutorias.notification.NotificadorSmsConsola;

/** ConcreteCreator para el canal SMS. */
public class SmsNotificadorCreator extends NotificadorCreator {

    @Override
    protected Notificador crearNotificador() {
        return new NotificadorSmsConsola();
    }
}
