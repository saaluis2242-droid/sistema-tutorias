package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.Usuario;

/**
 * Implementacion de Notificador que simula el envio de un correo
 * electronico imprimiendo el mensaje en consola. En un entorno real
 * esta clase seria la unica que sabria hablar con un servidor SMTP o
 * una API de correo; el resto del sistema no depende de ese detalle.
 */
public class NotificadorEmailConsola implements Notificador {

    @Override
    public void notificar(Usuario destinatario, String mensaje) {
        System.out.printf(
                "[EMAIL] Para: %s <%s> | Mensaje: %s%n",
                destinatario.getNombre(), destinatario.getCorreo(), mensaje
        );
    }
}
