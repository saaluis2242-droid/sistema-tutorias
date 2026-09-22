package edu.uees.tutorias.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Red de seguridad de Ae5 sobre el bloque de tiempo.
 *
 * <p>Protege lo que la Refactorizacion 3 (mover a {@code Horario} la
 * regla de disponibilidad que estaba en {@code ServicioReservas}) debe
 * preservar: la validacion de inicio/fin, el efecto de ocupar y liberar,
 * el calculo de solapamiento, la igualdad por intervalo y el formato de
 * {@code toString()}, del que depende el texto del recibo.</p>
 */
class HorarioTest {

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 8, 31, 10, 0);
    private static final LocalDateTime FIN = LocalDateTime.of(2026, 8, 31, 11, 0);

    @Test
    void unHorarioCuyoFinNoEsPosteriorAlInicioEsRechazado() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new Horario(FIN, INICIO));

        assertEquals("El horario debe tener un fin posterior al inicio", error.getMessage());
    }

    @Test
    void unHorarioNaceDisponible() {
        assertTrue(new Horario(INICIO, FIN).isDisponible());
    }

    @Test
    void ocuparUnHorarioLoDejaNoDisponibleYLiberarloLoDevuelveADisponible() {
        Horario horario = new Horario(INICIO, FIN);

        horario.marcarOcupado();
        assertFalse(horario.isDisponible());

        horario.liberar();
        assertTrue(horario.isDisponible());
    }

    @Test
    void ocuparDosVecesElMismoHorarioEsUnError() {
        Horario horario = new Horario(INICIO, FIN);
        horario.marcarOcupado();

        IllegalStateException error = assertThrows(IllegalStateException.class, horario::marcarOcupado);

        assertEquals("El horario ya se encuentra ocupado", error.getMessage());
    }

    @Test
    void reservarUnHorarioDisponibleLoOcupa() {
        Horario horario = new Horario(INICIO, FIN);

        horario.reservar();

        assertFalse(horario.isDisponible());
    }

    @Test
    void reservarUnHorarioYaTomadoFallaConElMensajeQueEmitiaElServicio() {
        Horario horario = new Horario(INICIO, FIN);
        horario.reservar();

        IllegalStateException error = assertThrows(IllegalStateException.class, horario::reservar);

        assertEquals("El horario seleccionado ya no esta disponible", error.getMessage());
    }

    @Test
    void dosHorariosQueComparteMinutosSeSolapan() {
        Horario diezAOnce = new Horario(INICIO, FIN);
        Horario diezYMediaADoce = new Horario(INICIO.plusMinutes(30), FIN.plusHours(1));
        Horario onceADoce = new Horario(FIN, FIN.plusHours(1));

        assertTrue(diezAOnce.seSolapaCon(diezYMediaADoce));
        assertTrue(diezYMediaADoce.seSolapaCon(diezAOnce));
        assertFalse(diezAOnce.seSolapaCon(onceADoce));
    }

    @Test
    void dosHorariosConElMismoIntervaloSonIguales() {
        assertEquals(new Horario(INICIO, FIN), new Horario(INICIO, FIN));
        assertEquals(new Horario(INICIO, FIN).hashCode(), new Horario(INICIO, FIN).hashCode());
    }

    @Test
    void elTextoDeUnHorarioEsElQueUsaElRecibo() {
        assertEquals("2026-08-31T10:00 - 2026-08-31T11:00", new Horario(INICIO, FIN).toString());
    }
}
