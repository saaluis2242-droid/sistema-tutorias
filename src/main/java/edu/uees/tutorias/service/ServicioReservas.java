package edu.uees.tutorias.service;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.observer.ObservadorReserva;
import edu.uees.tutorias.repository.RepositorioReservas;
import edu.uees.tutorias.service.cancelacion.PoliticaCancelacion;
import edu.uees.tutorias.service.cancelacion.ResultadoCancelacion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Orquesta el caso de uso completo de una reserva de tutoria: valida
 * disponibilidad, delega la persistencia, aplica la politica de
 * cancelacion vigente y publica cada cambio de estado a sus
 * observadores.
 *
 * <p>Esta clase concentra SOLO la logica de aplicacion (el "cuando" y
 * el "que"); no sabe como se guarda una reserva (RepositorioReservas),
 * que regla de cancelacion aplica en cada caso (PoliticaCancelacion) ni
 * quien reacciona a un cambio de estado ni como (ObservadorReserva).
 * Es el ejemplo mas claro de SRP, DIP, Strategy y Observer en el
 * diseño: tiene una unica razon para cambiar (que cambien las reglas
 * del proceso de reserva en si) y depende solo de abstracciones.</p>
 *
 * <p>Evolucion respecto de Ae1: en el incremento anterior, esta clase
 * dependia de un unico {@code Notificador} inyectado por constructor.
 * Al identificar que (a) las reglas de cancelacion necesitaban variar y
 * (b) mas de un componente debia reaccionar a un mismo cambio de
 * estado, se introdujeron Strategy (PoliticaCancelacion) y Observer
 * (ObservadorReserva) para resolver ambos problemas sin acoplar esta
 * clase a una regla fija ni a un unico canal de notificacion.</p>
 */
public class ServicioReservas {

    private final RepositorioReservas repositorio;
    private final PoliticaCancelacion politicaCancelacion;
    private final List<ObservadorReserva> observadores;

    public ServicioReservas(RepositorioReservas repositorio,
                             PoliticaCancelacion politicaCancelacion,
                             List<ObservadorReserva> observadores) {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.politicaCancelacion = Objects.requireNonNull(politicaCancelacion);
        this.observadores = new ArrayList<>(Objects.requireNonNull(observadores));
    }

    /** Permite registrar un observador adicional en tiempo de ejecucion (Open/Closed). */
    public void agregarObservador(ObservadorReserva observador) {
        observadores.add(Objects.requireNonNull(observador));
    }

    public Reserva solicitarReserva(Estudiante estudiante, Docente docente, Horario horario) {
        horario.reservar();
        Reserva reserva = new Reserva(estudiante, docente, horario);
        repositorio.guardar(reserva);

        publicar(reserva, "SOLICITADA");
        return reserva;
    }

    public void confirmarReserva(String reservaId) {
        Reserva reserva = obtenerReserva(reservaId);
        reserva.confirmar();
        repositorio.guardar(reserva);
        publicar(reserva, "CONFIRMADA");
    }

    public void cancelarReserva(String reservaId, String motivo) {
        Reserva reserva = obtenerReserva(reservaId);

        ResultadoCancelacion resultado = politicaCancelacion.evaluar(reserva, LocalDateTime.now());
        if (!resultado.isPermitida()) {
            throw new IllegalStateException(resultado.getObservacion());
        }

        String notaFinal = resultado.getObservacion().isEmpty()
                ? motivo
                : motivo + " [" + resultado.getObservacion() + "]";

        reserva.cancelar(notaFinal);
        repositorio.guardar(reserva);
        publicar(reserva, "CANCELADA");
    }

    public void reprogramarReserva(String reservaId, Horario nuevoHorario) {
        Reserva reserva = obtenerReserva(reservaId);
        reserva.reprogramar(nuevoHorario);
        repositorio.guardar(reserva);
        publicar(reserva, "REPROGRAMADA");
    }

    private void publicar(Reserva reserva, String evento) {
        for (ObservadorReserva observador : observadores) {
            observador.onCambioEstado(reserva, evento);
        }
    }

    private Reserva obtenerReserva(String reservaId) {
        return repositorio.buscarPorId(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("No existe una reserva con id " + reservaId));
    }
}
