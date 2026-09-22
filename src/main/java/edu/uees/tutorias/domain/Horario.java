package edu.uees.tutorias.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Bloque de tiempo que un docente publica como disponible para tutorias.
 *
 * Es un objeto de valor: encapsula sus propias reglas (que el fin sea
 * posterior al inicio, y el estado de disponibilidad) para que ninguna
 * otra clase manipule directamente sus atributos. Docente lo contiene
 * por composicion: un Horario no tiene sentido ni existe fuera de la
 * agenda de un Docente especifico.
 */
public class Horario {

    private final LocalDateTime inicio;
    private final LocalDateTime fin;
    private boolean disponible;

    public Horario(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null || !fin.isAfter(inicio)) {
            throw new IllegalArgumentException("El horario debe tener un fin posterior al inicio");
        }
        this.inicio = inicio;
        this.fin = fin;
        this.disponible = true;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public boolean isDisponible() {
        return disponible;
    }

    /**
     * Toma este horario para una reserva: comprueba que siga disponible
     * y lo ocupa en una sola operacion.
     *
     * <p>Refactorizacion 3 de Ae5 (Move Method): esta comprobacion vivia
     * en {@code ServicioReservas.solicitarReserva}, que preguntaba
     * {@code isDisponible()} y, si la respuesta era afirmativa, llamaba a
     * {@code marcarOcupado()}. Eso obligaba al servicio a conocer la
     * regla del horario y dejaba la pregunta y la accion separadas, de
     * modo que cualquier otro cliente podia olvidar la pregunta u operar
     * sobre un estado ya cambiado. La regla pertenece al objeto que
     * posee el dato, y aqui queda como una sola decision indivisible,
     * escrita con guard clause: se valida primero y solo despues se
     * muta el estado.</p>
     *
     * <p>El mensaje del error es exactamente el que emitia el servicio,
     * para no alterar el comportamiento observable.</p>
     */
    public void reservar() {
        if (!disponible) {
            throw new IllegalStateException("El horario seleccionado ya no esta disponible");
        }
        this.disponible = false;
    }

    /** Solo el propio Horario decide si puede marcarse como ocupado. */
    public void marcarOcupado() {
        if (!disponible) {
            throw new IllegalStateException("El horario ya se encuentra ocupado");
        }
        this.disponible = false;
    }

    public void liberar() {
        this.disponible = true;
    }

    public boolean seSolapaCon(Horario otro) {
        return this.inicio.isBefore(otro.fin) && otro.inicio.isBefore(this.fin);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Horario horario)) return false;
        return inicio.equals(horario.inicio) && fin.equals(horario.fin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inicio, fin);
    }

    @Override
    public String toString() {
        return inicio + " - " + fin;
    }
}
