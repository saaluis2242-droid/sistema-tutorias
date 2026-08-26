package edu.uees.tutorias;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.EstadoReserva;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.NotificadorEmailConsola;
import edu.uees.tutorias.repository.RepositorioReservasEnMemoria;
import edu.uees.tutorias.service.ServicioReservas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicioReservasTest {

    private ServicioReservas servicio;
    private Docente docente;
    private Estudiante estudiante;
    private Horario horario;

    @BeforeEach
    void setUp() {
        servicio = new ServicioReservas(new RepositorioReservasEnMemoria(), new NotificadorEmailConsola());
        docente = new Docente("D1", "Ana Torres", "ana@uees.edu.ec", "Bases de Datos");
        estudiante = new Estudiante("E1", "Luis Perez", "luis@uees.edu.ec", "UEES-001");
        horario = new Horario(LocalDateTime.of(2026, 8, 31, 10, 0), LocalDateTime.of(2026, 8, 31, 11, 0));
        docente.publicarHorario(horario);
    }

    @Test
    void solicitarReservaDejaElHorarioOcupado() {
        Reserva reserva = servicio.solicitarReserva(estudiante, docente, horario);

        assertEquals(EstadoReserva.SOLICITADA, reserva.getEstado());
        assertTrue(!horario.isDisponible());
    }

    @Test
    void noSePuedeReservarUnHorarioYaOcupado() {
        servicio.solicitarReserva(estudiante, docente, horario);

        assertThrows(IllegalStateException.class,
                () -> servicio.solicitarReserva(estudiante, docente, horario));
    }

    @Test
    void confirmarReservaCambiaSuEstado() {
        Reserva reserva = servicio.solicitarReserva(estudiante, docente, horario);

        servicio.confirmarReserva(reserva.getId());

        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
    }

    @Test
    void cancelarReservaLiberaElHorario() {
        Reserva reserva = servicio.solicitarReserva(estudiante, docente, horario);

        servicio.cancelarReserva(reserva.getId(), "El estudiante ya no puede asistir");

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertTrue(horario.isDisponible());
    }

    @Test
    void reprogramarReservaCambiaElHorarioAsociado() {
        Reserva reserva = servicio.solicitarReserva(estudiante, docente, horario);
        Horario nuevoHorario = new Horario(
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 11, 0)
        );

        servicio.reprogramarReserva(reserva.getId(), nuevoHorario);

        assertEquals(EstadoReserva.REPROGRAMADA, reserva.getEstado());
        assertEquals(nuevoHorario, reserva.getHorario());
        assertTrue(horario.isDisponible());
    }
}
