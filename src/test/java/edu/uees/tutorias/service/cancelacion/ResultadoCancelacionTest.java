package edu.uees.tutorias.service.cancelacion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del resultado de evaluar una politica de cancelacion, con la
 * regla de composicion de la nota que la Refactorizacion 4 trajo desde
 * {@code ServicioReservas}.
 *
 * <p>Antes, para verificar el formato de la nota habia que montar un
 * repositorio, una politica y un servicio completos (asi lo hace
 * {@code ServicioReservasCaracterizacionTest}, que se mantiene como red
 * de seguridad). Ahora la misma regla se prueba sobre el objeto que la
 * contiene.</p>
 */
class ResultadoCancelacionTest {

    @Test
    void unaCancelacionPermitidaSinObservacionDejaElMotivoIntacto() {
        ResultadoCancelacion resultado = ResultadoCancelacion.permitida();

        assertTrue(resultado.isPermitida());
        assertFalse(resultado.tieneObservacion());
        assertEquals("Imprevisto del estudiante", resultado.componerNota("Imprevisto del estudiante"));
    }

    @Test
    void unaObservacionSeAnexaAlMotivoEntreCorchetes() {
        ResultadoCancelacion resultado =
                ResultadoCancelacion.permitidaConObservacion("cancelacion tardia");

        assertTrue(resultado.isPermitida());
        assertTrue(resultado.tieneObservacion());
        assertEquals("Imprevisto [cancelacion tardia]", resultado.componerNota("Imprevisto"));
    }

    @Test
    void unaCancelacionRechazadaLlevaElMotivoDelRechazoComoObservacion() {
        ResultadoCancelacion resultado = ResultadoCancelacion.rechazada("el horario ya inicio");

        assertFalse(resultado.isPermitida());
        assertEquals("el horario ya inicio", resultado.getObservacion());
    }
}
