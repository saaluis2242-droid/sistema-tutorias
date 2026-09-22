package edu.uees.tutorias.reporte;

import edu.uees.tutorias.domain.Dinero;
import edu.uees.tutorias.domain.EstadoReserva;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.domain.tarifa.TarifarioTutoria;

/**
 * Compone el texto del recibo de una tutoria.
 *
 * <p>Refactorizacion 6 de Ae5 (Extract Class + Value Object): esta clase
 * cargaba con dos razones de cambio. Decidia cuanto se cobra -- las
 * constantes PRECIO_BASE, DESCUENTO_REPROGRAMACION, DESCUENTO_FIDELIDAD,
 * el prefijo del codigo institucional y el switch que los aplicaba -- y
 * ademas decidia como se ve el recibo. Que el area academica cambie un
 * descuento y que se agregue una linea al recibo son cambios sin
 * relacion, y los dos llegaban a este archivo; peor aun, la regla de
 * precio solo podia probarse leyendo el texto impreso.</p>
 *
 * <p>La regla de cobro se movio a
 * {@link TarifarioTutoria} y el importe se representa con el Value
 * Object {@link Dinero}, que sabe formatearse. Lo que queda aqui es
 * solo presentacion: rotulos, orden de las lineas y saltos de linea. El
 * texto producido es identico, caracter por caracter, al de Ae4.</p>
 */
public class GeneradorReciboReserva {

    private final TarifarioTutoria tarifario;

    public GeneradorReciboReserva() {
        this(new TarifarioTutoria());
    }

    /** Permite inyectar otro tarifario (por ejemplo, uno de prueba o una tarifa promocional). */
    public GeneradorReciboReserva(TarifarioTutoria tarifario) {
        this.tarifario = tarifario;
    }

    public String generar(Reserva reserva) {
        Dinero total = tarifario.calcular(reserva);

        return construirEncabezado(reserva)
                + construirLineaEstado(reserva, reserva.getEstado())
                + "Total a pagar: " + total + "\n";
    }

    private String construirEncabezado(Reserva reserva) {
        return "Recibo de tutoria\n"
                + "Estudiante: " + reserva.getEstudiante().getNombre() + "\n"
                + "Docente: " + reserva.getDocente().getNombre() + "\n"
                + "Horario: " + reserva.getHorario() + "\n";
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
