package edu.uees.tutorias.notification.observer;

import edu.uees.tutorias.domain.Reserva;

/**
 * Observer del patron Observer: cualquier componente que deba
 * reaccionar cuando cambia el estado de una Reserva implementa esta
 * interfaz y se registra en ServicioReservas.
 *
 * <p>Problema real que resuelve: en Ae1, ServicioReservas dependia de
 * un unico {@code Notificador} inyectado por constructor. Agregar una
 * segunda reaccion a un mismo evento (por ejemplo, un registro de
 * auditoria ademas del correo) hubiera obligado a modificar el
 * constructor y cada metodo de ServicioReservas. Con Observer,
 * ServicioReservas (el Subject) mantiene una lista de interesados y
 * los notifica a todos por igual, sin conocer cuantos son ni que hacen
 * con el evento.</p>
 *
 * @param evento identifica que paso: SOLICITADA, CONFIRMADA, CANCELADA
 *               o REPROGRAMADA (coincide con los valores relevantes de
 *               EstadoReserva).
 */
public interface ObservadorReserva {

    void onCambioEstado(Reserva reserva, String evento);
}
