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

    public Reserva(Estudiante estudiante, Docente docente, Horario horario) {
        this.id = UUID.randomUUID().toString();
        this.estudiante = Objects.requireNonNull(estudiante, "El estudiante es obligatorio");
        this.docente = Objects.requireNonNull(docente, "El docente es obligatorio");
        this.horario = Objects.requireNonNull(horario, "El horario es obligatorio");
        this.estado = EstadoReserva.SOLICITADA;
        this.notas = "";
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

    public void reprogramar(Horario nuevoHorario) {
        if (estado == EstadoReserva.CANCELADA || estado == EstadoReserva.COMPLETADA) {
            throw new IllegalStateException("La reserva ya no puede reprogramarse en su estado actual");
        }
        horario.liberar();
        this.horario = Objects.requireNonNull(nuevoHorario, "El nuevo horario es obligatorio");
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
