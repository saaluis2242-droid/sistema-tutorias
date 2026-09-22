package edu.uees.tutorias.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Reserva de tutoria entre un Estudiante y un Docente para un Horario
 * concreto.
 *
 * Es el objeto que registra el encuentro y protege las transiciones de
 * su propio estado (confirmar, cancelar, reprogramar): ninguna otra
 * clase del sistema cambia el estado de una Reserva directamente, solo
 * a traves de estos metodos. Esto mantiene la regla de negocio en un
 * unico lugar (alta cohesion) y evita que ServicioReservas termine
 * conociendo los detalles internos de que transiciones son validas.
 */
public class Reserva {

    private final String id;
    private final Estudiante estudiante;
    private final Docente docente;
    private Horario horario;
    private EstadoReserva estado;
    private String notas;

    /**
     * Crea una reserva a partir de la solicitud que la origina.
     *
     * <p>Refactorizacion 5 de Ae5 (agrupar Data Clump): este
     * constructor recibia los tres datos por separado y los validaba uno
     * a uno. Ahora la validacion del trio es responsabilidad de
     * {@link SolicitudTutoria} y este constructor solo copia lo que ya
     * es valido.</p>
     */
    public Reserva(SolicitudTutoria solicitud) {
        Objects.requireNonNull(solicitud, "La solicitud es obligatoria");
        this.id = UUID.randomUUID().toString();
        this.estudiante = solicitud.estudiante();
        this.docente = solicitud.docente();
        this.horario = solicitud.horario();
        this.estado = EstadoReserva.SOLICITADA;
        this.notas = "";
    }

    /**
     * Sobrecarga conservada por compatibilidad con el codigo de Ae1-Ae4:
     * delega en el constructor basado en {@link SolicitudTutoria}.
     */
    public Reserva(Estudiante estudiante, Docente docente, Horario horario) {
        this(new SolicitudTutoria(estudiante, docente, horario));
    }

    public void confirmar() {
        if (estado != EstadoReserva.SOLICITADA) {
            throw new IllegalStateException("Solo una reserva solicitada puede confirmarse");
        }
        estado = EstadoReserva.CONFIRMADA;
    }

    public void cancelar(String motivo) {
        if (estado == EstadoReserva.CANCELADA || estado == EstadoReserva.COMPLETADA) {
            throw new IllegalStateException("La reserva ya no puede cancelarse en su estado actual");
        }
        horario.liberar();
        estado = EstadoReserva.CANCELADA;
        this.notas = motivo == null ? "" : motivo;
    }

    /**
     * Reprograma la reserva a un nuevo horario disponible.
     *
     * <p>Refactorizacion 3 de Ae5 (Move Method + Guard Clauses): la
     * comprobacion de que el nuevo horario estuviera disponible la hacia
     * {@code ServicioReservas.reprogramarReserva} antes de llamar a este
     * metodo. Era una regla del dominio viviendo en la capa de
     * aplicacion: quien reprogramara una reserva por otra via podia
     * omitirla. Ahora las tres condiciones se validan aqui, como guard
     * clauses, y ninguna mutacion ocurre antes de que todas pasen; antes,
     * un nuevo horario nulo liberaba el horario anterior y recien
     * despues fallaba, dejando la reserva a medio camino.</p>
     */
    public void reprogramar(Horario nuevoHorario) {
        if (estado == EstadoReserva.CANCELADA || estado == EstadoReserva.COMPLETADA) {
            throw new IllegalStateException("La reserva ya no puede reprogramarse en su estado actual");
        }
        Objects.requireNonNull(nuevoHorario, "El nuevo horario es obligatorio");
        if (!nuevoHorario.isDisponible()) {
            throw new IllegalStateException("El nuevo horario no esta disponible");
        }

        horario.liberar();
        this.horario = nuevoHorario;
        nuevoHorario.marcarOcupado();
        this.estado = EstadoReserva.REPROGRAMADA;
    }

    public void completar() {
        if (estado != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException("Solo una reserva confirmada puede completarse");
        }
        estado = EstadoReserva.COMPLETADA;
    }

    public String getId() {
        return id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Docente getDocente() {
        return docente;
    }

    public Horario getHorario() {
        return horario;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public String getNotas() {
        return notas;
    }
}
