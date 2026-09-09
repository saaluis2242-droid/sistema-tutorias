package edu.uees.tutorias.notification.factory;

import edu.uees.tutorias.notification.Notificador;
import edu.uees.tutorias.notification.NotificadorWhatsAppConsola;

/**
 * ConcreteCreator para el canal WHATSAPP. Es la unica clase nueva que
 * hizo falta para soportar este canal: ni NotificadorCreator ni el
 * codigo cliente que ya usaba EmailNotificadorCreator o
 * SmsNotificadorCreator necesitaron cambios (evidencia de OCP, igual
 * que se demostro en Ae2).
 */
public class WhatsAppNotificadorCreator extends NotificadorCreator {

    @Override
    protected Notificador crearNotificador() {
        return new NotificadorWhatsAppConsola();
    }
}
