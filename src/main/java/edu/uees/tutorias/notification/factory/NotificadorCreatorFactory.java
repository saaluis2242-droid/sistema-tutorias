package edu.uees.tutorias.notification.factory;

import edu.uees.tutorias.domain.CanalNotificacion;

import java.util.Map;

/**
 * Punto unico de registro que asocia cada {@link CanalNotificacion} con
 * su ConcreteCreator. Es el unico lugar del sistema que "sabe" que
 * clases concretas existen; el resto del codigo (incluido
 * NotificadorObservador) solo pide "el creador para este canal" y
 * recibe un {@link NotificadorCreator}.
 *
 * Agregar un canal nuevo implica: crear el Notificador concreto, crear
 * su Creator, y agregar una linea aqui. Ninguna otra clase del sistema
 * cambia.
 */
public final class NotificadorCreatorFactory {

    private static final Map<CanalNotificacion, NotificadorCreator> CREADORES = Map.of(
            CanalNotificacion.EMAIL, new EmailNotificadorCreator(),
            CanalNotificacion.SMS, new SmsNotificadorCreator(),
            CanalNotificacion.WHATSAPP, new WhatsAppNotificadorCreator()
    );

    private NotificadorCreatorFactory() {
    }

    public static NotificadorCreator obtenerCreador(CanalNotificacion canal) {
        NotificadorCreator creador = CREADORES.get(canal);
        if (creador == null) {
            throw new IllegalArgumentException("No existe un creador registrado para el canal " + canal);
        }
        return creador;
    }
}
