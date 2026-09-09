package edu.uees.tutorias.notification.observer;

import edu.uees.tutorias.domain.Reserva;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Segundo ConcreteObserver: en lugar de notificar a un usuario, deja un
 * registro de auditoria en memoria de cada cambio de estado. Representa,
 * en pequeno, al "panel" o historial que un sistema real expondria.
 *
 * Se agrego sin modificar ServicioReservas ni NotificadorObservador: solo
 * se registra una instancia mas en la lista de observadores (evidencia
 * de Open/Closed Principle).
 */
public class RegistroAuditoriaObservador implements ObservadorReserva {

    private final List<String> historial = new ArrayList<>();

    @Override
    public void onCambioEstado(Reserva reserva, String evento) {
        historial.add(String.format("%s | reserva=%s | evento=%s | estudiante=%s | docente=%s",
                LocalDateTime.now(), reserva.getId(), evento,
                reserva.getEstudiante().getNombre(), reserva.getDocente().getNombre()));
    }

    public List<String> getHistorial() {
        return Collections.unmodifiableList(historial);
    }
}
