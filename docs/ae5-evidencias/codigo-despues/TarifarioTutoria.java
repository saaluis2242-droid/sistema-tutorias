package edu.uees.tutorias.domain.tarifa;

import edu.uees.tutorias.domain.Dinero;
import edu.uees.tutorias.domain.EstadoReserva;
import edu.uees.tutorias.domain.Reserva;

/**
 * Reglas de cobro de una tutoria: precio base, descuento por
 * reprogramacion, descuento de fidelidad para estudiantes con codigo
 * institucional y gratuidad de las reservas canceladas.
 *
 * <p>Problema de diseño que resuelve (Refactorizacion 6 de Ae5):
 * {@code GeneradorReciboReserva} tenia dos razones de cambio. Por un
 * lado decidia <em>cuanto se cobra</em> (las constantes de precio y
 * descuento y el switch que las aplica); por otro armaba <em>como se
 * ve</em> el recibo (los rotulos, los saltos de linea, el orden de las
 * lineas). Son dos motivos de cambio completamente independientes: que
 * el area academica cambie el descuento no tiene relacion con que se
 * agregue una linea al recibo, y sin embargo ambos cambios llegaban al
 * mismo archivo. Ademas, la regla de precio solo podia probarse leyendo
 * el texto del recibo, es decir, a traves de su presentacion.</p>
 *
 * <p>Al extraer la clase, la regla de cobro queda aislada y verificable
 * por si misma, y el generador de recibos vuelve a ser lo que su nombre
 * dice: un formateador.</p>
 *
 * <p>El calculo se mantiene identico, con las mismas constantes y la
 * misma expresion aritmetica, de modo que el total de cada recibo no
 * cambia.</p>
 */
public class TarifarioTutoria {

    private static final Dinero PRECIO_BASE = new Dinero(15.0);
    private static final double DESCUENTO_REPROGRAMACION = 0.1;
    private static final double DESCUENTO_FIDELIDAD = 0.2;
    private static final String PREFIJO_CODIGO_FIDELIDAD = "UEES";

    /** Importe a pagar por una reserva segun su estado actual. */
    public Dinero calcular(Reserva reserva) {
        EstadoReserva estado = reserva.getEstado();
        return switch (estado) {
            case REPROGRAMADA -> PRECIO_BASE.menosDescuento(DESCUENTO_REPROGRAMACION);
            case CANCELADA -> Dinero.CERO;
            case COMPLETADA -> tieneCodigoFidelidad(reserva)
                    ? PRECIO_BASE.menosDescuento(DESCUENTO_FIDELIDAD)
                    : PRECIO_BASE;
            case SOLICITADA, CONFIRMADA -> PRECIO_BASE;
        };
    }

    private boolean tieneCodigoFidelidad(Reserva reserva) {
        String codigo = reserva.getEstudiante().getCodigoEstudiantil();
        return codigo != null && codigo.startsWith(PREFIJO_CODIGO_FIDELIDAD);
    }
}
