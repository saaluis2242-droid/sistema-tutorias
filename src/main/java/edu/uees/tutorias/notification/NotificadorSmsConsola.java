package edu.uees.tutorias.notification;

import edu.uees.tutorias.domain.Usuario;

/**
 * Segunda implementacion de Notificador, para demostrar que el sistema
 * puede extenderse con nuevos canales sin tocar ServicioReservas
 * (Open/Closed Principle). Simula un SMS imprimiendo en consola.
 */
public class NotificadorSmsConsola implements Notificador {

    @Override
    public void notificar(Usuario destinatario, String mensaje) {
        System.out.printf("[SMS] Para: %s | Mensaje: %s%n", destinatario.getNombre(), mensaje);
    }
}
