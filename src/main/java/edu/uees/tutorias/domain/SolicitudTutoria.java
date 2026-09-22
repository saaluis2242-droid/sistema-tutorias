package edu.uees.tutorias.domain;

import java.util.Objects;

/**
 * Los tres datos que definen una solicitud de tutoria: quien la pide,
 * con quien y en que bloque de tiempo.
 *
 * <p>Problema de diseño que resuelve (Refactorizacion 5 de Ae5): el
 * trio {@code (estudiante, docente, horario)} viajaba junto por todo el
 * sistema -- el codigo cliente lo pasaba a
 * {@code ServicioReservas.solicitarReserva(estudiante, docente, horario)},
 * que lo pasaba tal cual a {@code new Reserva(estudiante, docente, horario)} --
 * y cada firma repetia los mismos tres parametros en el mismo orden.
 * Es un <em>Data Clump</em>: tres datos que nunca aparecen por separado
 * y que, sin embargo, no tienen nombre como concepto. Sus consecuencias
 * concretas eran que el orden de los tres argumentos se podia invertir
 * sin que el compilador dijera nada (dos de ellos son Usuario), que la
 * validacion de los tres estaba repetida en el constructor de Reserva, y
 * que agregar un cuarto dato de la solicitud -- modalidad presencial o
 * virtual, asignatura, motivo -- habria obligado a modificar todas las
 * firmas de la cadena.</p>
 *
 * <p>Al agruparlos en un tipo con nombre propio, la solicitud se valida
 * una sola vez, al crearse, y pasa a ser el vocabulario del caso de uso:
 * los metodos reciben "una solicitud de tutoria" en lugar de tres
 * objetos sueltos. Es inmutable, de ahi el {@code record}.</p>
 *
 * <p>Se conservan las firmas anteriores como sobrecargas que delegan en
 * esta, para no romper a ningun cliente existente: el comportamiento
 * observable, incluidos los mensajes de los tres requireNonNull, es el
 * mismo.</p>
 */
public record SolicitudTutoria(Estudiante estudiante, Docente docente, Horario horario) {

    public SolicitudTutoria {
        Objects.requireNonNull(estudiante, "El estudiante es obligatorio");
        Objects.requireNonNull(docente, "El docente es obligatorio");
        Objects.requireNonNull(horario, "El horario es obligatorio");
    }
}
