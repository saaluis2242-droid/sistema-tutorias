package edu.uees.tutorias.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Red de seguridad de Ae5 sobre la agenda de un docente.
 *
 * <p>Es la prueba que protege la Refactorizacion 2 (Extract Class
 * {@code AgendaDocente}): el docente sigue publicando horarios con la
 * misma firma, la regla de no solapamiento sigue rechazando con el mismo
 * mensaje, la coleccion devuelta sigue siendo de solo lectura y el orden
 * de publicacion se conserva.</p>
 */
class DocenteTest {

    private Docente docente;

    @BeforeEach
    void setUp() {
        docente = new Docente("D1", "Ana Torres", "ana.torres@uees.edu.ec", "Estructuras de Datos");
    }

    @Test
    void unDocenteNuevoNoTieneHorariosPublicados() {
        assertEquals(0, docente.getHorarios().size());
    }

    @Test
    void losHorariosSePublicanYSeConservanEnOrden() {
        Horario lunes = horario(8, 31, 10);
        Horario martes = horario(9, 1, 10);

        docente.publicarHorario(lunes);
        docente.publicarHorario(martes);

        assertEquals(List.of(lunes, martes), docente.getHorarios());
    }

    @Test
    void unHorarioQueSeSolapaConOtroYaPublicadoEsRechazado() {
        docente.publicarHorario(horario(8, 31, 10));

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> docente.publicarHorario(new Horario(
                        LocalDateTime.of(2026, 8, 31, 10, 30),
                        LocalDateTime.of(2026, 8, 31, 11, 30))));

        assertEquals("El horario se solapa con uno ya publicado", error.getMessage());
        assertEquals(1, docente.getHorarios().size());
    }

    @Test
    void dosHorariosConsecutivosNoSeConsideranSolapados() {
        docente.publicarHorario(horario(8, 31, 10));
        docente.publicarHorario(horario(8, 31, 11));

        assertEquals(2, docente.getHorarios().size());
    }

    @Test
    void laAgendaExpuestaEsDeSoloLectura() {
        docente.publicarHorario(horario(8, 31, 10));

        assertThrows(UnsupportedOperationException.class,
                () -> docente.getHorarios().add(horario(9, 1, 10)));
    }

    private Horario horario(int mes, int dia, int hora) {
        return new Horario(
                LocalDateTime.of(2026, mes, dia, hora, 0),
                LocalDateTime.of(2026, mes, dia, hora + 1, 0));
    }
}
