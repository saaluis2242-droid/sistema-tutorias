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

    public String getObservacion() {
        return observacion;
    }
}
