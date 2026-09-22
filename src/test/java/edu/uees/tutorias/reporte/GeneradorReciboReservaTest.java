package edu.uees.tutorias.reporte;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Red de seguridad de Ae5 para el generador de recibos.
 *
 * <p>Son pruebas de caracterizacion: no describen lo que el recibo
 * "deberia" decir, sino exactamente lo que dice hoy, caracter por
 * caracter, incluyendo el formato del total (`$13.5`) y el texto de
 * cada estado. Reemplazan al harness manual
 * {@link LineaBaseRecibos} que se uso en Ae4 cuando no habia JUnit
 * disponible, y cubren los 5 valores de EstadoReserva mas los dos
 * caminos del descuento por fidelidad.</p>
 *
 * <p>Estas pruebas son las que protegen la Refactorizacion 5 (extraer
 * el calculo de tarifa del armado del texto): si el reparto de
 * responsabilidades alterara un solo caracter del recibo o un centavo
 * del total, fallarian.</p>
 */
class GeneradorReciboReservaTest {

    private final GeneradorReciboReserva generador = new GeneradorReciboReserva();

    @Test
    void reciboDeUnaReservaSolicitadaCobraElPrecioBase() {
        assertEquals("""
                        Recibo de tutoria
                        Estudiante: Luis Perez
                        Docente: Ana Torres
                        Horario: 2026-08-31T10:00 - 2026-08-31T11:00
                        Estado: SOLICITADA
                        Total a pagar: $15.0
                        """,
                generador.generar(reservaSolicitada()));
    }

    @Test
    void reciboDeUnaReservaConfirmadaCobraElPrecioBase() {
        Reserva reserva = reservaSolicitada();
        reserva.confirmar();

        assertEquals("""
                        Recibo de tutoria
                        Estudiante: Luis Perez
                        Docente: Ana Torres
                        Horario: 2026-08-31T10:00 - 2026-08-31T11:00
                        Estado: CONFIRMADA
                        Total a pagar: $15.0
                        """,
                generador.generar(reserva));
    }

    @Test
    void reciboDeUnaReservaReprogramadaAplicaDiezPorCientoDeDescuento() {
        Reserva reserva = reservaSolicitada();
        reserva.reprogramar(new Horario(
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 11, 0)));

        assertEquals("""
                        Recibo de tutoria
                        Estudiante: Luis Perez
                        Docente: Ana Torres
                        Horario: 2026-09-01T10:00 - 2026-09-01T11:00
                        Estado: reprogramada (aplica 10% de descuento)
                        Total a pagar: $13.5
                        """,
                generador.generar(reserva));
    }

    @Test
    void reciboDeUnaReservaCanceladaNoCobraYMuestraElMotivo() {
        Reserva reserva = reservaSolicitada();
        reserva.cancelar("el estudiante ya no puede asistir");

        assertEquals("""
                        Recibo de tutoria
                        Estudiante: Luis Perez
                        Docente: Ana Torres
                        Horario: 2026-08-31T10:00 - 2026-08-31T11:00
                        Estado: cancelada. Motivo: el estudiante ya no puede asistir
                        Total a pagar: $0.0
                        """,
                generador.generar(reserva));
    }

    @Test
    void reciboDeUnaReservaCompletadaConCodigoUeesAplicaDescuentoDeFidelidad() {
        Reserva reserva = reservaCompletadaCon(
                new Estudiante("E2", "Maria Lopez", "maria.lopez@uees.edu.ec", "UEES-2023-045"));

        assertEquals("""
                        Recibo de tutoria
                        Estudiante: Maria Lopez
                        Docente: Ana Torres
                        Horario: 2026-08-31T10:00 - 2026-08-31T11:00
                        Estado: completada
                        Total a pagar: $12.0
                        """,
                generador.generar(reserva));
    }

    @Test
    void reciboDeUnaReservaCompletadaSinCodigoUeesNoAplicaDescuento() {
        Reserva reserva = reservaCompletadaCon(
                new Estudiante("E3", "Pedro Ruiz", "pedro.ruiz@otraentidad.edu.ec", "EXT-2023-009"));

        assertEquals("""
                        Recibo de tutoria
                        Estudiante: Pedro Ruiz
                        Docente: Ana Torres
                        Horario: 2026-08-31T10:00 - 2026-08-31T11:00
                        Estado: completada
                        Total a pagar: $15.0
                        """,
                generador.generar(reserva));
    }

    private Reserva reservaSolicitada() {
        return reservaPara(new Estudiante("E1", "Luis Perez", "luis.perez@uees.edu.ec", "UEES-2023-001"));
    }

    private Reserva reservaCompletadaCon(Estudiante estudiante) {
        Reserva reserva = reservaPara(estudiante);
        reserva.confirmar();
        reserva.completar();
        return reserva;
    }

    private Reserva reservaPara(Estudiante estudiante) {
        Docente docente = new Docente("D1", "Ana Torres", "ana.torres@uees.edu.ec", "Estructuras de Datos");
        Horario horario = new Horario(
                LocalDateTime.of(2026, 8, 31, 10, 0),
                LocalDateTime.of(2026, 8, 31, 11, 0));
        docente.publicarHorario(horario);
        horario.marcarOcupado();
        return new Reserva(estudiante, docente, horario);
    }
}
