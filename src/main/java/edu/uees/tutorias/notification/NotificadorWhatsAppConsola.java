package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.Usuario;

/**
 * Tercera implementacion de Notificador (ademas de Email y SMS), para el
 * canal WhatsApp. Se agrega en el Incremento 1 junto con el Factory
 * Method que decide, segun el canal preferido de cada Usuario, cual de
 * estas tres clases instanciar.
 */
public class NotificadorWhatsAppConsola implements Notificador {

    @Override
    public void notificar(Usuario destinatario, String mensaje) {
        System.out.printf("[WHATSAPP] Para: %s | Mensaje: %s%n", destinatario.getNombre(), mensaje);
    }
}
