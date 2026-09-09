package edu.uees.tutorias.service.cancelacion;

import edu.uees.tutorias.domain.Reserva;

import java.time.LocalDateTime;

/**
 * Politica sin restricciones adicionales: reproduce el comportamiento
 * que tenia el sistema en Ae1 (se puede cancelar en cualquier momento,
 * sin penalizacion), ahora expresado como una ConcreteStrategy.
 */
public class PoliticaCancelacionEstandar implements PoliticaCancelacion {

    @Override
    public ResultadoCancelacion evaluar(Reserva reserva, LocalDateTime momento) {
        return ResultadoCancelacion.permitida();
    }
}
