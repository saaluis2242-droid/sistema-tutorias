package edu.uees.tutorias.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del parameter object introducido en la Refactorizacion 5.
 *
 * <p>Verifican que el trio de datos se valide una sola vez, al crear la
 * solicitud, con los mismos mensajes que antes emitia el constructor de
 * {@link Reserva}, y que dos solicitudes con los mismos datos sean
 * iguales por valor.</p>
 */
class SolicitudTutoriaTest {

    private Estudiante estudiante;
    private Docente docente;
    private Horario horario;

    @BeforeEach
    void setUp() {
        estudiante = new Estudiante("E1", "Luis Perez", "luis.perez@uees.edu.ec", "UEES-2023-001");
        docente = new Docente("D1", "Ana Torres", "ana.torres@uees.edu.ec", "Estructuras de Datos");
        horario = new Horario(LocalDateTime.of(2026, 8, 31, 10, 0), LocalDateTime.of(2026, 8, 31, 11, 0));
    }

    @Test
    void unaSolicitudConservaSusTresDatos() {
        SolicitudTutoria solicitud = new SolicitudTutoria(estudiante, docente, horario);

        assertEquals(estudiante, solicitud.estudiante());
        assertEquals(docente, solicitud.docente());
        assertEquals(horario, solicitud.horario());
    }

    @Test
    void ningunoDeLosTresDatosPuedeFaltarYElMensajeEsElDeSiempre() {
        assertEquals("El estudiante es obligatorio",
                assertThrows(NullPointerException.class,
                        () -> new SolicitudTutoria(null, docente, horario)).getMessage());
        assertEquals("El docente es obligatorio",
                assertThrows(NullPointerException.class,
                        () -> new SolicitudTutoria(estudiante, null, horario)).getMessage());
        assertEquals("El horario es obligatorio",
                assertThrows(NullPointerException.class,
                        () -> new SolicitudTutoria(estudiante, docente, null)).getMessage());
    }

    @Test
    void dosSolicitudesConLosMismosDatosSonIguales() {
        assertTrue(new SolicitudTutoria(estudiante, docente, horario)
                .equals(new SolicitudTutoria(estudiante, docente, horario)));
    }

    @Test
    void unaReservaCreadaDesdeLaSolicitudTomaSusTresDatos() {
        Reserva reserva = new Reserva(new SolicitudTutoria(estudiante, docente, horario));

        assertEquals(estudiante, reserva.getEstudiante());
        assertEquals(docente, reserva.getDocente());
        assertEquals(horario, reserva.getHorario());
        assertEquals(EstadoReserva.SOLICITADA, reserva.getEstado());
    }
}
