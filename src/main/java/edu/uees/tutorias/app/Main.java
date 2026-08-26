package edu.uees.tutorias.app;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.NotificadorEmailConsola;
import edu.uees.tutorias.repository.RepositorioReservasEnMemoria;
import edu.uees.tutorias.service.ServicioReservas;

import java.time.LocalDateTime;

/**
 * Punto de entrada de demostracion. Muestra el flujo principal: publicar
 * un horario, solicitar una reserva, confirmarla y luego reprogramarla.
 */
public class Main {

    public static void main(String[] args) {
        Docente docente = new Docente("D1", "Ana Torres", "ana.torres@uees.edu.ec", "Estructuras de Datos");
        Estudiante estudiante = new Estudiante("E1", "Luis Perez", "luis.perez@uees.edu.ec", "UEES-2023-001");

        Horario horarioLunes = new Horario(
                LocalDateTime.of(2026, 8, 31, 10, 0),
                LocalDateTime.of(2026, 8, 31, 11, 0)
        );
        docente.publicarHorario(horarioLunes);

        ServicioReservas servicio = new ServicioReservas(
                new RepositorioReservasEnMemoria(),
                new NotificadorEmailConsola()
        );

        Reserva reserva = servicio.solicitarReserva(estudiante, docente, horarioLunes);
        System.out.println("Estado inicial: " + reserva.getEstado());

        servicio.confirmarReserva(reserva.getId());
        System.out.println("Estado tras confirmar: " + reserva.getEstado());

        Horario horarioMartes = new Horario(
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 11, 0)
        );
        servicio.reprogramarReserva(reserva.getId(), horarioMartes);
        System.out.println("Estado tras reprogramar: " + reserva.getEstado());
    }
}
