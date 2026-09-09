package edu.uees.tutorias.service.cancelacion;

import edu.uees.tutorias.domain.Reserva;

import java.time.LocalDateTime;

/**
 * Strategy que encapsula la regla de negocio para decidir si una
 * reserva puede cancelarse en un momento dado, y con que observacion.
 *
 * <p>Problema real que resuelve: en Ae1, {@code Reserva.cancelar()}
 * aplicaba una unica regla fija (se puede cancelar si el estado actual
 * lo permite, sin mas condiciones). Distintos escenarios del negocio
 * necesitan reglas distintas -- por ejemplo, penalizar cancelaciones de
 * ultima hora, o exigir una anticipacion minima para reservas
 * grupales -- y agregar cada regla nueva como un if/else dentro de
 * Reserva o ServicioReservas los haria crecer indefinidamente y violaria
 * el principio Open/Closed.</p>
 *
 * <p>Con Strategy, ServicioReservas depende solo de esta interfaz; que
 * regla se aplica se decide al construir el servicio (o puede
 * cambiarse en tiempo de ejecucion), sin tocar ServicioReservas ni
 * Reserva.</p>
 */
public interface PoliticaCancelacion {

    ResultadoCancelacion evaluar(Reserva reserva, LocalDateTime momento);
}
