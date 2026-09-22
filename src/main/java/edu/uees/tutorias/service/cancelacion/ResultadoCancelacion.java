package edu.uees.tutorias.service.cancelacion;

/**
 * Resultado de evaluar una {@link PoliticaCancelacion} sobre una
 * reserva concreta: si la cancelacion esta permitida y una observacion
 * opcional (por ejemplo, una advertencia de penalizacion) que
 * ServicioReservas anexa a las notas de la reserva.
 */
public final class ResultadoCancelacion {

    private final boolean permitida;
    private final String observacion;

    private ResultadoCancelacion(boolean permitida, String observacion) {
        this.permitida = permitida;
        this.observacion = observacion;
    }

    public static ResultadoCancelacion permitida() {
        return new ResultadoCancelacion(true, "");
    }

    public static ResultadoCancelacion permitidaConObservacion(String observacion) {
        return new ResultadoCancelacion(true, observacion);
    }

    public static ResultadoCancelacion rechazada(String motivo) {
        return new ResultadoCancelacion(false, motivo);
    }

    public boolean isPermitida() {
        return permitida;
    }

    /**
     * Compone la nota que quedara registrada en la reserva a partir del
     * motivo que indico quien cancela y de la observacion que agrego la
     * politica.
     *
     * <p>Refactorizacion 4 de Ae5 (Decompose Conditional + Move Method):
     * esta regla estaba escrita como un operador ternario dentro de
     * {@code ServicioReservas.cancelarReserva}, que preguntaba
     * {@code resultado.getObservacion().isEmpty()} y armaba el texto con
     * corchetes segun la respuesta. Era el servicio interrogando los
     * datos de otro objeto para decidir por el (<em>Feature Envy</em>) y
     * una condicion sin nombre mezclada con la orquestacion del caso de
     * uso. Ahora la decision vive en el objeto que posee la observacion,
     * con un nombre que dice que produce, y el servicio solo pide la
     * nota.</p>
     *
     * <p>El texto resultante es identico al anterior: el motivo solo, o
     * el motivo seguido de la observacion entre corchetes.</p>
     */
    public String componerNota(String motivo) {
        return tieneObservacion() ? motivo + " [" + observacion + "]" : motivo;
    }

    public boolean tieneObservacion() {
        return !observacion.isEmpty();
    }

    public String getObservacion() {
        return observacion;
    }
}
