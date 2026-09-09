package edu.uees.tutorias.notification.factory;

import edu.uees.tutorias.notification.Notificador;
import edu.uees.tutorias.notification.NotificadorEmailConsola;

/** ConcreteCreator para el canal EMAIL. */
public class EmailNotificadorCreator extends NotificadorCreator {

    @Override
    protected Notificador crearNotificador() {
        return new NotificadorEmailConsola();
    }
}
