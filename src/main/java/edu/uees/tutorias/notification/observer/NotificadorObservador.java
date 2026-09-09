package edu.uees.tutorias.notification.observer;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.domain.Usuario;
import edu.uees.tutorias.notification.factory.NotificadorCreator;
import edu.uees.tutorias.notification.factory.NotificadorCreatorFactory;

/**
 * ConcreteObserver que traduce un cambio de estado de una Reserva en
 * uno o mas mensajes para los usuarios involucrados.
 *
 * <p>Para enviar cada mensaje, no construye un Notificador concreto
 * directamente: le pide al Factory Method ({@link NotificadorCreatorFactory})
 * el creador adecuado segun el canal preferido de cada destinatario.
 * Asi, Observer (Ae3) y Factory Method (Ae2) quedan integrados: este
 * observer no conoce NotificadorEmailConsola, NotificadorSmsConsola ni
 * NotificadorWhatsAppConsola, solo la fabrica y la interfaz
 * Notificador.</p>
 */
public class NotificadorObservador implements ObservadorReserva {

    @Override
    public void onCambioEstado(Reserva reserva, String evento) {
        Estudiante estudiante = reserva.getEstudiante();
        Docente docente = reserva.getDocente();

        switch (evento) {
            case "SOLICITADA" -> enviar(docente,
                    "Nueva solicitud de tutoria de " + estudiante.getNombre() + " para " + reserva.getHorario());
            case "CONFIRMADA" -> enviar(estudiante,
                    "Tu tutoria con " + docente.getNombre() + " fue confirmada");
            case "CANCELADA" -> enviar(docente,
                    "La reserva de " + estudiante.getNombre() + " fue cancelada. Motivo: " + reserva.getNotas());
            case "REPROGRAMADA" -> {
                enviar(estudiante, "Tu tutoria fue reprogramada para " + reserva.getHorario());
                enviar(docente, "La tutoria con " + estudiante.getNombre() + " fue reprogramada para " + reserva.getHorario());
            }
            default -> { /* eventos no reconocidos se ignoran: este observer solo reacciona a los que conoce */ }
        }
    }

    private void enviar(Usuario destinatario, String mensaje) {
        NotificadorCreator creador = NotificadorCreatorFactory.obtenerCreador(destinatario.getCanalPreferido());
        creador.notificar(destinatario, mensaje);
    }
}
