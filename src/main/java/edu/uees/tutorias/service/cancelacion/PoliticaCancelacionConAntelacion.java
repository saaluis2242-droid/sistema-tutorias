package edu.uees.tutorias.service.cancelacion;

import edu.uees.tutorias.domain.Reserva;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Politica que exige una anticipacion minima para cancelar sin
 * observaciones: si ya paso la hora de inicio del horario, la
 * cancelacion se rechaza (la tutoria ya ocurrio o esta ocurriendo); si
 * faltan menos de {@link #horasMinimas} horas, se permite pero se marca
 * como tardia (por ejemplo, para que el negocio decida aplicar una
 * penalizacion); en cualquier otro caso, se permite sin observaciones.
 */
public class PoliticaCancelacionConAntelacion implements PoliticaCancelacion {

    private final long horasMinimas;

    public PoliticaCancelacionConAntelacion(long horasMinimas) {
        if (horasMinimas < 0) {
            throw new IllegalArgumentException("Las horas minimas no pueden ser negativas");
        }
        this.horasMinimas = horasMinimas;
    }

    @Override
    public ResultadoCancelacion evaluar(Reserva reserva, LocalDateTime momento) {
        LocalDateTime inicio = reserva.getHorario().getInicio();

        if (!momento.isBefore(inicio)) {
            return ResultadoCancelacion.rechazada(
                    "No se puede cancelar una reserva cuyo horario ya inicio o finalizo");
        }

        long horasRestantes = Duration.between(momento, inicio).toHours();
        if (horasRestantes < horasMinimas) {
            return ResultadoCancelacion.permitidaConObservacion(
                    "cancelacion tardia (a menos de " + horasMinimas + "h del horario): puede aplicar penalizacion");
        }

        return ResultadoCancelacion.permitida();
    }
}
