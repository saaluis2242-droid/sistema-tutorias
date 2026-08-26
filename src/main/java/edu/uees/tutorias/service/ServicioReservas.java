package edu.uees.tutorias.service;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.Notificador;
import edu.uees.tutorias.repository.RepositorioReservas;

import java.util.Objects;

/**
 * Orquesta el caso de uso completo de una reserva de tutoria: valida
 * disponibilidad, delega la persistencia y notifica a los usuarios
 * involucrados.
 *
 * Esta clase concentra SOLO la logica de aplicacion (el "cuando" y el
 * "que"); no sabe como se guarda una reserva ni como se envia un
 * mensaje: esas responsabilidades estan detras de RepositorioReservas
 * y Notificador. Es el ejemplo mas claro de SRP y DIP en el diseño:
 * ServicioReservas tiene una unica razon para cambiar (que cambien las
 * reglas del proceso de reserva) y depende de abstracciones, no de
 * implementaciones concretas.
 */
public class ServicioReservas {

    private final RepositorioReservas repositorio;
    private final Notificador notificador;

    public ServicioReservas(RepositorioReservas repositorio, Notificador notificador) {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.notificador = Objects.requireNonNull(notificador);
    }

    public Reserva solicitarReserva(Estudiante estudiante, Docente docente, Horario horario) {
        if (!horario.isDisponible()) {
            throw new IllegalStateException("El horario seleccionado ya no esta disponible");
        }
        horario.marcarOcupado();
        Reserva reserva = new Reserva(estudiante, docente, horario);
        repositorio.guardar(reserva);

        notificador.notificar(docente,
                "Nueva solicitud de tutoria de " + estudiante.getNombre() + " para " + horario);
        return reserva;
    }

    public void confirmarReserva(String reservaId) {
        Reserva reserva = obtenerReserva(reservaId);
        reserva.confirmar();
        repositorio.guardar(reserva);
        notificador.notificar(reserva.getEstudiante(),
                "Tu tutoria con " + reserva.getDocente().getNombre() + " fue confirmada");
    }

    public void cancelarReserva(String reservaId, String motivo) {
        Reserva reserva = obtenerReserva(reservaId);
        reserva.cancelar(motivo);
        repositorio.guardar(reserva);
        notificador.notificar(reserva.getDocente(),
                "La reserva de " + reserva.getEstudiante().getNombre() + " fue cancelada. Motivo: " + motivo);
    }

    public void reprogramarReserva(String reservaId, Horario nuevoHorario) {
        Reserva reserva = obtenerReserva(reservaId);
        if (!nuevoHorario.isDisponible()) {
            throw new IllegalStateException("El nuevo horario no esta disponible");
        }
        reserva.reprogramar(nuevoHorario);
        repositorio.guardar(reserva);
        notificador.notificar(reserva.getEstudiante(),
                "Tu tutoria fue reprogramada para " + nuevoHorario);
        notificador.notificar(reserva.getDocente(),
                "La tutoria con " + reserva.getEstudiante().getNombre() + " fue reprogramada para " + nuevoHorario);
    }

    private Reserva obtenerReserva(String reservaId) {
        return repositorio.buscarPorId(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("No existe una reserva con id " + reservaId));
    }
}
