package edu.uees.tutorias.domain.tarifa;

import edu.uees.tutorias.domain.Dinero;
import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pruebas de la regla de cobro extraida en la Refactorizacion 6.
 *
 * <p>Es el beneficio directo de la extraccion: antes, cada una de estas
 * reglas solo podia comprobarse leyendo la linea "Total a pagar" del
 * texto del recibo; ahora se comprueban sobre el importe, sin pasar por
 * la presentacion. {@code GeneradorReciboReservaTest} sigue verificando,
 * en paralelo, que el recibo completo no cambio.</p>
 */
class TarifarioTutoriaTest {

    private final TarifarioTutoria tarifario = new TarifarioTutoria();

    @Test
    void unaReservaSolicitadaOConfirmadaPagaElPrecioBase() {
        assertEquals(new Dinero(15.0), tarifario.calcular(reserva()));

        Reserva confirmada = reserva();
        confirmada.confirmar();
        assertEquals(new Dinero(15.0), tarifario.calcular(confirmada));
    }

    @Test
    void unaReservaReprogramadaPagaDiezPorCientoMenos() {
        Reserva reserva = reserva();
        reserva.reprogramar(new Horario(
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 11, 0)));

        assertEquals(new Dinero(13.5), tarifario.calcular(reserva));
    }

    @Test
    void unaReservaCanceladaNoPaga() {
        Reserva reserva = reserva();
        reserva.cancelar("imprevisto");

        assertEquals(Dinero.CERO, tarifario.calcular(reserva));
    }

    @Test
    void unaReservaCompletadaConCodigoInstitucionalPagaVeintePorCientoMenos() {
        assertEquals(new Dinero(12.0),
                tarifario.calcular(completada("UEES-2023-045")));
    }

    @Test
    void unaReservaCompletadaSinCodigoInstitucionalPagaElPrecioBase() {
        assertEquals(new Dinero(15.0),
                tarifario.calcular(completada("EXT-2023-009")));
    }

    @Test
    void elImporteSeImprimeConElFormatoDelRecibo() {
        assertEquals("$15.0", new Dinero(15.0).toString());
        assertEquals("$13.5", new Dinero(15.0).menosDescuento(0.1).toString());
        assertEquals("$0.0", Dinero.CERO.toString());
    }

    private Reserva completada(String codigoEstudiantil) {
        Reserva reserva = reservaDe(new Estudiante("E9", "Maria Lopez", "maria@uees.edu.ec", codigoEstudiantil));
        reserva.confirmar();
        reserva.completar();
        return reserva;
    }

    private Reserva reserva() {
        return reservaDe(new Estudiante("E1", "Luis Perez", "luis.perez@uees.edu.ec", "UEES-2023-001"));
    }

    private Reserva reservaDe(Estudiante estudiante) {
        Docente docente = new Docente("D1", "Ana Torres", "ana.torres@uees.edu.ec", "Estructuras de Datos");
        Horario horario = new Horario(
                LocalDateTime.of(2026, 8, 31, 10, 0),
                LocalDateTime.of(2026, 8, 31, 11, 0));
        docente.publicarHorario(horario);
        horario.reservar();
        return new Reserva(estudiante, docente, horario);
    }
}
