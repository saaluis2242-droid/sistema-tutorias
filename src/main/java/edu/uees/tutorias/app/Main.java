package edu.uees.tutorias.app;

import edu.uees.tutorias.domain.CanalNotificacion;
import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.observer.NotificadorObservador;
import edu.uees.tutorias.notification.observer.ObservadorReserva;
import edu.uees.tutorias.notification.observer.RegistroAuditoriaObservador;
import edu.uees.tutorias.repository.RepositorioReservasEnMemoria;
import edu.uees.tutorias.service.ServicioReservas;
import edu.uees.tutorias.service.cancelacion.PoliticaCancelacionConAntelacion;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Punto de entrada de demostracion del Incremento 1 (Ae3). Muestra:
 * <ul>
 *   <li>El flujo principal (solicitar, confirmar, reprogramar).</li>
 *   <li>Observer: dos observadores reaccionan a cada evento (notificacion
 *       por el canal preferido de cada usuario, via Factory Method; y un
 *       registro de auditoria).</li>
 *   <li>Strategy: una cancelacion tardia queda marcada por
 *       PoliticaCancelacionConAntelacion sin que ServicioReservas conozca
 *       esa regla.</li>
 * </ul>
 */
public class Main {

    public static void main(String[] args) {
        Docente docente = new Docente("D1", "Ana Torres", "ana.torres@uees.edu.ec", "Estructuras de Datos");
        Estudiante estudiante = new Estudiante("E1", "Luis Perez", "luis.perez@uees.edu.ec", "UEES-2023-001");
        // El docente prefiere WhatsApp; el estudiante se queda con el canal por defecto (EMAIL).
        docente.setCanalPreferido(CanalNotificacion.WHATSAPP);

        Horario horarioLunes = new Horario(
                LocalDateTime.of(2026, 8, 31, 10, 0),
                LocalDateTime.of(2026, 8, 31, 11, 0)
        );
        docente.publicarHorario(horarioLunes);

        RegistroAuditoriaObservador auditoria = new RegistroAuditoriaObservador();
        List<ObservadorReserva> observadores = List.of(new NotificadorObservador(), auditoria);

        ServicioReservas servicio = new ServicioReservas(
                new RepositorioReservasEnMemoria(),
                new PoliticaCancelacionConAntelacion(24),
                observadores
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

        // Cancelacion de ultima hora: la nueva reserva quedo para el 2026-09-01,
        // asi que "ahora" (fecha de esta demo) esta a menos de 24h -> la
        // PoliticaCancelacionConAntelacion debe marcarla como tardia.
        Reserva reservaTardia = servicio.solicitarReserva(estudiante, docente,
                new Horario(LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3)));
        servicio.cancelarReserva(reservaTardia.getId(), "El estudiante ya no puede asistir");
        System.out.println("Notas de la cancelacion tardia: " + reservaTardia.getNotas());

        System.out.println("\n--- Historial de auditoria ---");
        auditoria.getHistorial().forEach(System.out::println);
    }
}
