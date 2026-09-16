package edu.uees.tutorias.reporte;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;

import java.time.LocalDateTime;

/**
 * Linea base de comportamiento para la Kata de refactorizacion (Ae4).
 *
 * No usa JUnit (en este entorno de verificacion no hay acceso a Maven
 * Central para descargarlo); en su lugar construye 5 casos
 * representativos, imprime el recibo generado por
 * {@link GeneradorReciboReserva} y lo
 * compara contra el texto exacto capturado como linea base ANTES de
 * refactorizar. Los mismos 5 casos se vuelven a correr contra la clase
 * final (ya refactorizada) para demostrar que el comportamiento
 * observable no cambio.
 */
public class LineaBaseRecibos {

    static int ok = 0;
    static int fail = 0;

    public static void main(String[] args) {
        GeneradorReciboReserva generador = new GeneradorReciboReserva();

        caso("Caso 1 - SOLICITADA", generador.generar(reservaSolicitada()),
                "Recibo de tutoria\n" +
                "Estudiante: Luis Perez\n" +
                "Docente: Ana Torres\n" +
                "Horario: 2026-08-31T10:00 - 2026-08-31T11:00\n" +
                "Estado: SOLICITADA\n" +
                "Total a pagar: $15.0\n");

        caso("Caso 2 - CONFIRMADA", generador.generar(reservaConfirmada()),
                "Recibo de tutoria\n" +
                "Estudiante: Luis Perez\n" +
                "Docente: Ana Torres\n" +
                "Horario: 2026-08-31T10:00 - 2026-08-31T11:00\n" +
                "Estado: CONFIRMADA\n" +
                "Total a pagar: $15.0\n");

        caso("Caso 3 - REPROGRAMADA", generador.generar(reservaReprogramada()),
                "Recibo de tutoria\n" +
                "Estudiante: Luis Perez\n" +
                "Docente: Ana Torres\n" +
                "Horario: 2026-09-01T10:00 - 2026-09-01T11:00\n" +
                "Estado: reprogramada (aplica 10% de descuento)\n" +
                "Total a pagar: $13.5\n");

        caso("Caso 4 - CANCELADA", generador.generar(reservaCancelada()),
                "Recibo de tutoria\n" +
                "Estudiante: Luis Perez\n" +
                "Docente: Ana Torres\n" +
                "Horario: 2026-08-31T10:00 - 2026-08-31T11:00\n" +
                "Estado: cancelada. Motivo: el estudiante ya no puede asistir\n" +
                "Total a pagar: $0.0\n");

        caso("Caso 5 - COMPLETADA con codigo UEES (descuento fidelidad)", generador.generar(reservaCompletadaConCodigoUees()),
                "Recibo de tutoria\n" +
                "Estudiante: Maria Lopez\n" +
                "Docente: Ana Torres\n" +
                "Horario: 2026-08-31T10:00 - 2026-08-31T11:00\n" +
                "Estado: completada\n" +
                "Total a pagar: $12.0\n");

        caso("Caso 6 - COMPLETADA sin codigo UEES (sin descuento)", generador.generar(reservaCompletadaSinCodigoUees()),
                "Recibo de tutoria\n" +
                "Estudiante: Pedro Ruiz\n" +
                "Docente: Ana Torres\n" +
                "Horario: 2026-08-31T10:00 - 2026-08-31T11:00\n" +
                "Estado: completada\n" +
                "Total a pagar: $15.0\n");

        System.out.println();
        System.out.println(ok + " OK, " + fail + " FAIL");
        if (fail > 0) {
            System.exit(1);
        }
    }

    private static void caso(String nombre, String obtenido, String esperado) {
        System.out.println("--- " + nombre + " ---");
        System.out.print(obtenido);
        if (obtenido.equals(esperado)) {
            ok++;
            System.out.println("OK   - " + nombre);
        } else {
            fail++;
            System.out.println("FAIL - " + nombre);
            System.out.println("Esperado:\n" + esperado);
        }
        System.out.println();
    }

    private static Docente docente() {
        return new Docente("D1", "Ana Torres", "ana.torres@uees.edu.ec", "Estructuras de Datos");
    }

    private static Reserva reservaSolicitada() {
        Docente d = docente();
        Estudiante e = new Estudiante("E1", "Luis Perez", "luis.perez@uees.edu.ec", "UEES-2023-001");
        Horario h = new Horario(LocalDateTime.of(2026, 8, 31, 10, 0), LocalDateTime.of(2026, 8, 31, 11, 0));
        d.publicarHorario(h);
        h.marcarOcupado();
        return new Reserva(e, d, h);
    }

    private static Reserva reservaConfirmada() {
        Reserva r = reservaSolicitada();
        r.confirmar();
        return r;
    }

    private static Reserva reservaReprogramada() {
        Docente d = docente();
        Estudiante e = new Estudiante("E1", "Luis Perez", "luis.perez@uees.edu.ec", "UEES-2023-001");
        Horario original = new Horario(LocalDateTime.of(2026, 8, 31, 10, 0), LocalDateTime.of(2026, 8, 31, 11, 0));
        d.publicarHorario(original);
        original.marcarOcupado();
        Reserva r = new Reserva(e, d, original);
        Horario nuevo = new Horario(LocalDateTime.of(2026, 9, 1, 10, 0), LocalDateTime.of(2026, 9, 1, 11, 0));
        r.reprogramar(nuevo);
        return r;
    }

    private static Reserva reservaCancelada() {
        Reserva r = reservaSolicitada();
        r.cancelar("el estudiante ya no puede asistir");
        return r;
    }

    private static Reserva reservaCompletadaConCodigoUees() {
        Docente d = docente();
        Estudiante e = new Estudiante("E2", "Maria Lopez", "maria.lopez@uees.edu.ec", "UEES-2023-045");
        Horario h = new Horario(LocalDateTime.of(2026, 8, 31, 10, 0), LocalDateTime.of(2026, 8, 31, 11, 0));
        d.publicarHorario(h);
        h.marcarOcupado();
        Reserva r = new Reserva(e, d, h);
        r.confirmar();
        r.completar();
        return r;
    }

    private static Reserva reservaCompletadaSinCodigoUees() {
        Docente d = docente();
        Estudiante e = new Estudiante("E3", "Pedro Ruiz", "pedro.ruiz@otraentidad.edu.ec", "EXT-2023-009");
        Horario h = new Horario(LocalDateTime.of(2026, 8, 31, 10, 0), LocalDateTime.of(2026, 8, 31, 11, 0));
        d.publicarHorario(h);
        h.marcarOcupado();
        Reserva r = new Reserva(e, d, h);
        r.confirmar();
        r.completar();
        return r;
    }
}
