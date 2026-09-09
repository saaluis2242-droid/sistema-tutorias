package edu.uees.tutorias.domain;

/**
 * Canales de notificacion soportados por el sistema. Cada Usuario indica
 * cual prefiere; la fabrica de notificadores (Factory Method, paquete
 * notification.factory) decide, a partir de este valor, que
 * implementacion concreta de Notificador construir.
 */
public enum CanalNotificacion {
    EMAIL,
    SMS,
    WHATSAPP
}
