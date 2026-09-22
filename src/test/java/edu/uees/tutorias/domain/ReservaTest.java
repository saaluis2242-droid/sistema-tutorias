package edu.uees.tutorias.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Red de seguridad de Ae5 sobre las transiciones de estado de una
 * reserva, que son el nucleo del comportamiento que ninguna
 * refactorizacion puede alterar.
 *
 * <p>Protege en particular lo que tocan las Refactorizaciones 3 (guard
 * clauses en la reprogramacion) y 4 (agrupar estudiante/docente/horario
 * en {@code SolicitudTutoria}): que transiciones son validas, que pasa
 * con el horario en cada una y como quedan las notas.</p>
 */
class ReservaTest {

    private Estudiante estudiante;
    private Docente docente;
    private Horario horario;

    @BeforeEach
    void setUp() {
        estudiante = new Estudiante("E1", "Luis Perez", "luis.perez@uees.edu.ec", "UEES-2023-001");
        docente = new Docente("D1", "Ana Torres", "ana.torres@uees.edu.ec", "Estructuras de Datos");
        horario = new Horario(LocalDateTime.of(2026, 8, 31, 10, 0), LocalDateTime.of(2026, 8, 31, 11, 0));
        docente.publicarHorario(horario);
    }

    @Test
    void unaReservaNaceSolicitadaConIdYSinNotas() {
        Reserva reserva = new Reserva(estudiante, docente, horario);

        assertNotNull(reserva.getId());
        assertEquals(EstadoReserva.SOLICITADA, reserva.getEstado());
        assertEquals("", reserva.getNotas());
        assertEquals(estudiante, reserva.getEstudiante());
        assertEquals(docente, reserva.getDocente());
        assertEquals(horario, reserva.getHorario());
    }

    @Test
    void unaReservaExigeEstudianteDocenteYHorario() {
        assertThrows(NullPointerException.class, () -> new Reserva(null, docente, horario));
        assertThrows(NullPointerException.class, () -> new Reserva(estudiante, null, horario));
        assertThrows(NullPointerException.class, () -> new Reserva(estudiante, docente, null));
    }

    @Test
    void soloUnaReservaSolicitadaPuedeConfirmarse() {
        Reserva reserva = new Reserva(estudiante, docente, horario);
        reserva.confirmar();
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());

        IllegalStateException error = assertThrows(IllegalStateException.class, reserva::confirmar);
        assertEquals("Solo una reserva solicitada puede confirmarse", error.getMessage());
    }

    @Test
    void cancelarLiberaElHorarioYGuardaElMotivo() {
        horario.marcarOcupado();
        Reserva reserva = new Reserva(estudiante, docente, horario);

        reserva.cancelar("el estudiante ya no puede asistir");

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertTrue(horario.isDisponible());
        assertEquals("el estudiante ya no puede asistir", reserva.getNotas());
    }

    @Test
    void cancelarSinMotivoDejaLasNotasVacias() {
        Reserva reserva = new Reserva(estudiante, docente, horario);

        reserva.cancelar(null);

        assertEquals("", reserva.getNotas());
    }

    @Test
    void unaReservaYaCanceladaNoPuedeCancelarseNiReprogramarse() {
        Reserva reserva = new Reserva(estudiante, docente, horario);
        reserva.cancelar("motivo");

        assertEquals("La reserva ya no puede cancelarse en su estado actual",
                assertThrows(IllegalStateException.class, () -> reserva.cancelar("otro")).getMessage());
        assertEquals("La reserva ya no puede reprogramarse en su estado actual",
                assertThrows(IllegalStateException.class, () -> reserva.reprogramar(otroHorario())).getMessage());
    }

    @Test
    void reprogramarLiberaElHorarioAnteriorYOcupaElNuevo() {
        horario.marcarOcupado();
        Reserva reserva = new Reserva(estudiante, docente, horario);
        Horario nuevo = otroHorario();

        reserva.reprogramar(nuevo);

        assertEquals(EstadoReserva.REPROGRAMADA, reserva.getEstado());
        assertEquals(nuevo, reserva.getHorario());
        assertTrue(horario.isDisponible());
        assertFalse(nuevo.isDisponible());
    }

    @Test
    void reprogramarHaciaUnHorarioOcupadoFallaSinAlterarLaReserva() {
        horario.marcarOcupado();
        Reserva reserva = new Reserva(estudiante, docente, horario);
        Horario ocupado = otroHorario();
        ocupado.marcarOcupado();

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> reserva.reprogramar(ocupado));

        assertEquals("El nuevo horario no esta disponible", error.getMessage());
        assertEquals(EstadoReserva.SOLICITADA, reserva.getEstado());
        assertEquals(horario, reserva.getHorario());
        assertFalse(horario.isDisponible());
    }

    @Test
    void reprogramarHaciaUnHorarioNuloFallaSinLiberarElHorarioActual() {
        horario.marcarOcupado();
        Reserva reserva = new Reserva(estudiante, docente, horario);

        assertThrows(NullPointerException.class, () -> reserva.reprogramar(null));

        assertFalse(horario.isDisponible());
        assertEquals(EstadoReserva.SOLICITADA, reserva.getEstado());
    }

    @Test
    void soloUnaReservaConfirmadaPuedeCompletarse() {
        Reserva reserva = new Reserva(estudiante, docente, horario);

        IllegalStateException error = assertThrows(IllegalStateException.class, reserva::completar);
        assertEquals("Solo una reserva confirmada puede completarse", error.getMessage());

        reserva.confirmar();
        reserva.completar();
        assertEquals(EstadoReserva.COMPLETADA, reserva.getEstado());
    }

    @Test
    void unaReservaCompletadaNoPuedeCancelarse() {
        Reserva reserva = new Reserva(estudiante, docente, horario);
        reserva.confirmar();
        reserva.completar();

        assertThrows(IllegalStateException.class, () -> reserva.cancelar("motivo"));
    }

    private Horario otroHorario() {
        return new Horario(LocalDateTime.of(2026, 9, 1, 10, 0), LocalDateTime.of(2026, 9, 1, 11, 0));
    }
}
