package edu.uees.tutorias;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.EstadoReserva;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.observer.ObservadorReserva;
import edu.uees.tutorias.notification.observer.RegistroAuditoriaObservador;
import edu.uees.tutorias.repository.RepositorioReservasEnMemoria;
import edu.uees.tutorias.service.ServicioReservas;
import edu.uees.tutorias.service.cancelacion.PoliticaCancelacionConAntelacion;
import edu.uees.tutorias.service.cancelacion.PoliticaCancelacionEstandar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicioReservasTest {

    private ServicioReservas servicio;
    private Docente docente;
    private Estudiante estudiante;
    private Horario horario;
    private List<String> eventosCapturados;

    @BeforeEach
    void setUp() {
        docente = new Docente("D1", "Ana Torres", "ana@uees.edu.ec", "Bases de Datos");
        estudiante = new Estudiante("E1", "Luis Perez", "luis@uees.edu.ec", "UEES-001");
        horario = new Horario(LocalDateTime.of(2026, 8, 31, 10, 0), LocalDateTime.of(2026, 8, 31, 11, 0));
        docente.publicarHorario(horario);

        eventosCapturados = new ArrayList<>();
        ObservadorReserva espia = (reserva, evento) -> eventosCapturados.add(evento);

        servicio = new ServicioReservas(
                new RepositorioReservasEnMemoria(),
                new PoliticaCancelacionEstandar(),
                List.of(espia)
        );
    }

    @Test
    void solicitarReservaDejaElHorarioOcupadoYPublicaEvento() {
        Reserva reserva = servicio.solicitarReserva(estudiante, docente, horario);

        assertEquals(EstadoReserva.SOLICITADA, reserva.getEstado());
        assertFalse(horario.isDisponible());
        assertEquals(List.of("SOLICITADA"), eventosCapturados);
    }

    @Test
    void noSePuedeReservarUnHorarioYaOcupado() {
        servicio.solicitarReserva(estudiante, docente, horario);

        assertThrows(IllegalStateException.class,
                () -> servicio.solicitarReserva(estudiante, docente, horario));
    }

    @Test
    void confirmarReservaCambiaSuEstadoYPublicaEvento() {
        Reserva reserva = servicio.solicitarReserva(estudiante, docente, horario);

        servicio.confirmarReserva(reserva.getId());

        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
        assertEquals(List.of("SOLICITADA", "CONFIRMADA"), eventosCapturados);
    }

    @Test
    void cancelarReservaConPoliticaEstandarLiberaElHorarioSinObservaciones() {
        Reserva reserva = servicio.solicitarReserva(estudiante, docente, horario);

        servicio.cancelarReserva(reserva.getId(), "El estudiante ya no puede asistir");

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertTrue(horario.isDisponible());
        assertEquals("El estudiante ya no puede asistir", reserva.getNotas());
    }

    @Test
    void cancelarReservaTardiaQuedaMarcadaPorLaPoliticaConAntelacion() {
        ServicioReservas servicioConAntelacion = new ServicioReservas(
                new RepositorioReservasEnMemoria(),
                new PoliticaCancelacionConAntelacion(24),
                List.of()
        );
        Horario horarioProximo = new Horario(LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3));
        docente.publicarHorario(horarioProximo);
        Reserva reserva = servicioConAntelacion.solicitarReserva(estudiante, docente, horarioProximo);

        servicioConAntelacion.cancelarReserva(reserva.getId(), "Imprevisto");

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertTrue(reserva.getNotas().contains("cancelacion tardia"));
    }

    @Test
    void cancelarReservaCuyoHorarioYaPasoEsRechazadaPorLaPolitica() {
        ServicioReservas servicioConAntelacion = new ServicioReservas(
                new RepositorioReservasEnMemoria(),
                new PoliticaCancelacionConAntelacion(24),
                List.of()
        );
        Horario horarioPasado = new Horario(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusMinutes(30));
        docente.publicarHorario(horarioPasado);
        Reserva reserva = servicioConAntelacion.solicitarReserva(estudiante, docente, horarioPasado);

        assertThrows(IllegalStateException.class,
                () -> servicioConAntelacion.cancelarReserva(reserva.getId(), "Tarde"));
    }

    @Test
    void reprogramarReservaCambiaElHorarioAsociadoYPublicaEvento() {
        Reserva reserva = servicio.solicitarReserva(estudiante, docente, horario);
        Horario nuevoHorario = new Horario(
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 11, 0)
        );

        servicio.reprogramarReserva(reserva.getId(), nuevoHorario);

        assertEquals(EstadoReserva.REPROGRAMADA, reserva.getEstado());
        assertEquals(nuevoHorario, reserva.getHorario());
        assertTrue(horario.isDisponible());
        assertEquals(List.of("SOLICITADA", "REPROGRAMADA"), eventosCapturados);
    }

    @Test
    void agregarObservadorEnTiempoDeEjecucionRecibeEventosPosteriores() {
        RegistroAuditoriaObservador auditoria = new RegistroAuditoriaObservador();
        servicio.agregarObservador(auditoria);

        Reserva reserva = servicio.solicitarReserva(estudiante, docente, horario);
        servicio.confirmarReserva(reserva.getId());

        assertEquals(2, auditoria.getHistorial().size());
    }
}
