package edu.uees.tutorias.reporte;

import edu.uees.tutorias.domain.EstadoReserva;
import edu.uees.tutorias.domain.Reserva;

/**
 * Genera el texto del recibo de una tutoria.
 *
 * Refactorizacion 4 (Simplify Conditional): {@code calcularPrecio} y
 * {@code construirLineaEstado} comparaban {@code reserva.getEstado().toString()}
 * contra literales de texto ("REPROGRAMADA", "CANCELADA", "COMPLETADA")
 * con if/else anidados en tres niveles. Eso duplicaba, con texto suelto,
 * una informacion que ya existe como tipo en el dominio: el enum
 * {@link EstadoReserva}. Se reemplazaron ambas cadenas de if/else por un
 * {@code switch} sobre el enum (sin comparar Strings) y se aplano el
 * anidamiento. El resultado de cada caso es exactamente el mismo que
 * antes.
 */
public class GeneradorReciboReserva {

    private static final double PRECIO_BASE = 15.0;
    private static final double DESCUENTO_REPROGRAMACION = 0.1;
    private static final double DESCUENTO_FIDELIDAD = 0.2;
    private static final String PREFIJO_CODIGO_FIDELIDAD = "UEES";

    public String generar(Reserva reserva) {
        EstadoReserva estado = reserva.getEstado();
        double precio = calcularPrecio(reserva, estado);

        String recibo = "";
        recibo = recibo + construirEncabezado(reserva);
        recibo = recibo + construirLineaEstado(reserva, estado);
        recibo = recibo + "Total a pagar: $" + precio + "\n";
        return recibo;
    }

    private double calcularPrecio(Reserva reserva, EstadoReserva estado) {
        return switch (estado) {
            case REPROGRAMADA -> PRECIO_BASE - (PRECIO_BASE * DESCUENTO_REPROGRAMACION);
            case CANCELADA -> 0.0;
            case COMPLETADA -> tieneCodigoFidelidad(reserva)
                    ? PRECIO_BASE - (PRECIO_BASE * DESCUENTO_FIDELIDAD)
                    : PRECIO_BASE;
            case SOLICITADA, CONFIRMADA -> PRECIO_BASE;
        };
    }

    private boolean tieneCodigoFidelidad(Reserva reserva) {
        String codigo = reserva.getEstudiante().getCodigoEstudiantil();
        return codigo != null && codigo.startsWith(PREFIJO_CODIGO_FIDELIDAD);
    }

    private String construirEncabezado(Reserva reserva) {
        String encabezado = "";
        encabezado = encabezado + "Recibo de tutoria\n";
        encabezado = encabezado + "Estudiante: " + reserva.getEstudiante().getNombre() + "\n";
        encabezado = encabezado + "Docente: " + reserva.getDocente().getNombre() + "\n";
        encabezado = encabezado + "Horario: " + reserva.getHorario() + "\n";
        return encabezado;
    }

    private String construirLineaEstado(Reserva reserva, EstadoReserva estado) {
        return switch (estado) {
            case CANCELADA -> "Estado: cancelada. Motivo: " + reserva.getNotas() + "\n";
            case REPROGRAMADA -> "Estado: reprogramada (aplica 10% de descuento)\n";
            case COMPLETADA -> "Estado: completada\n";
            case SOLICITADA, CONFIRMADA -> "Estado: " + estado + "\n";
        };
    }
}
