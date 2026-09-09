package edu.uees.tutorias.notification.factory;

import edu.uees.tutorias.domain.CanalNotificacion;
import edu.uees.tutorias.domain.Estudiante;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class NotificadorCreatorFactoryTest {

    @Test
    void devuelveElCreadorCorrectoParaCadaCanal() {
        assertInstanceOf(EmailNotificadorCreator.class,
                NotificadorCreatorFactory.obtenerCreador(CanalNotificacion.EMAIL));
        assertInstanceOf(SmsNotificadorCreator.class,
                NotificadorCreatorFactory.obtenerCreador(CanalNotificacion.SMS));
        assertInstanceOf(WhatsAppNotificadorCreator.class,
                NotificadorCreatorFactory.obtenerCreador(CanalNotificacion.WHATSAPP));
    }

    @Test
    void elCreadorObtenidoPuedeNotificarSinConocerLaClaseConcreta() {
        Estudiante estudiante = new Estudiante("E1", "Luis Perez", "luis@uees.edu.ec", "UEES-001");
        NotificadorCreator creador = NotificadorCreatorFactory.obtenerCreador(estudiante.getCanalPreferido());

        assertDoesNotThrow(() -> creador.notificar(estudiante, "mensaje de prueba"));
    }
}
