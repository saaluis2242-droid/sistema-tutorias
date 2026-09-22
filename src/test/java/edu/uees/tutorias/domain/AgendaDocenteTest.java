package edu.uees.tutorias.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas propias de la clase extraida en la Refactorizacion 2.
 *
 * <p>Son el beneficio concreto de la extraccion: la regla de
 * disponibilidad ahora se prueba sin construir un Docente (sin id,
 * nombre, correo ni especialidad), lo que deja la prueba enfocada en lo
 * unico que importa aqui. {@link DocenteTest} sigue verificando, en
 * paralelo, que la delegacion conserva el comportamiento que ya existia.</p>
 */
class AgendaDocenteTest {

    @Test
    void unaAgendaNuevaEstaVacia() {
        assertEquals(List.of(), new AgendaDocente().getHorarios());
    }

    @Test
    void publicaLosHorariosEnOrden() {
        AgendaDocente agenda = new AgendaDocente();
        Horario lunes = horario(31, 10);
        Horario martes = horario(31, 15);

        agenda.publicar(lunes);
        agenda.publicar(martes);

        assertEquals(List.of(lunes, martes), agenda.getHorarios());
    }

    @Test
    void rechazaUnHorarioSolapadoSinAgregarlo() {
        AgendaDocente agenda = new AgendaDocente();
        agenda.publicar(horario(31, 10));

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> agenda.publicar(new Horario(
                        LocalDateTime.of(2026, 8, 31, 10, 30),
                        LocalDateTime.of(2026, 8, 31, 11, 30))));

        assertEquals("El horario se solapa con uno ya publicado", error.getMessage());
        assertEquals(1, agenda.getHorarios().size());
    }

    @Test
    void laColeccionExpuestaNoSePuedeModificarDesdeAfuera() {
        AgendaDocente agenda = new AgendaDocente();

        assertThrows(UnsupportedOperationException.class,
                () -> agenda.getHorarios().add(horario(31, 10)));
    }

    private Horario horario(int dia, int hora) {
        return new Horario(
                LocalDateTime.of(2026, 8, dia, hora, 0),
                LocalDateTime.of(2026, 8, dia, hora + 1, 0));
    }
}
