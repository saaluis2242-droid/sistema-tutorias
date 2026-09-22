package edu.uees.tutorias;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.EstadoReserva;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.Horario;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.observer.ObservadorReserva;
import edu.uees.tutorias.repository.RepositorioReservas;
import edu.uees.tutorias.repository.RepositorioReservasEnMemoria;
import edu.uees.tutorias.service.ServicioReservas;
import edu.uees.tutorias.service.cancelacion.PoliticaCancelacion;
import edu.uees.tutorias.service.cancelacion.PoliticaCancelacionConAntelacion;
import edu.uees.tutorias.service.cancelacion.PoliticaCancelacionEstandar;
import edu.uees.tutorias.service.cancelacion.ResultadoCancelacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Red de seguridad de Ae5 sobre el servicio de reservas.
 *
 * <p>Complementa a {@link ServicioReservasTest} (que viene de Ae4 y no
 * se modifico) fijando detalles observables que la suite anterior no
 * afirmaba y que las refactorizaciones de Ae5 podrian romper sin que
 * nadie se diera cuenta:</p>
 *
 * <ul>
 *   <li>los <em>mensajes</em> exactos de cada rechazo, no solo el tipo de
 *       excepcion (protege la Refactorizacion 3, que mueve esas
 *       validaciones a {@code Horario} y a {@code Reserva});</li>
 *   <li>la composicion de la nota de cancelacion cuando la politica
 *       devuelve una observacion;</li>
 *   <li>el orden y la cantidad de eventos publicados a los observadores,
 *       y que un rechazo no publique ningun evento ni deje efectos a
 *       medias (protege la Refactorizacion 4, que agrupa los parametros
 *       de la solicitud);</li>
 *   <li>que la reserva quede realmente persistida en el repositorio y
 *       que se busque por id.</li>
 * </ul>
 */
class ServicioReservasCaracterizacionTest {

    private RepositorioReservas repositorio;
    private ServicioReservas servicio;
    private Docente docente;
    private Estudiante estudiante;
    private Horario horario;
    private List<String> eventos;

    @BeforeEach
    void setUp() {
        docente = new Docente("D1", "Ana Torres", "ana.torres@uees.edu.ec", "Estructuras de Datos");
        estudiante = new Estudiante("E1", "Luis Perez", "luis.perez@uees.edu.ec", "UEES-2023-001");
        horario = new Horario(LocalDateTime.of(2026, 8, 31, 10, 0), LocalDateTime.of(2026, 8, 31, 11, 0));
        docente.publicarHorario(horario);

        eventos = new ArrayList<>();
        ObservadorReserva espia = (reserva, evento) -> eventos.add(evento);
        repositorio = new RepositorioReservasEnMemoria();
        servicio = new ServicioReservas(repositorio, new PoliticaCancelacionEstandar(), List.of(espia));
    }

    @Test
    void solicitarUnHorarioOcupadoFallaConSuMensajeYSinPublicarEventos() {
        horario.marcarOcupado();

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> servicio.solicitarReserva(estudiante, docente, horario));

        assertEquals("El horario seleccionado ya no esta disponible", error.getMessage());
        assertEquals(List.of(), eventos);
        assertEquals(List.of(), repositorio.listarPorEstudiante("E1"));
    }

    @Test
    void laReservaSolicitadaQuedaPersistidaYSePuedeRecuperarPorId() {
        Reserva reserva = servicio.solicitarReserva(estudiante, docente, horario);

        assertTrue(repositorio.buscarPorId(reserva.getId()).isPresent());
        assertEquals(List.of(reserva), repositorio.listarPorEstudiante("E1"));
        assertEquals(List.of(reserva), repositorio.listarPorDocente("D1"));
    }

    @Test
    void operarSobreUnaReservaInexistenteFallaConSuMensaje() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.confirmarReserva("no-existe"));

        assertEquals("No existe una reserva con id no-existe", error.getMessage());
    }

    @Test
    void reprogramarHaciaUnHorarioOcupadoFallaConSuMensajeYNoAlteraLaReserva() {
        Reserva reserva = servicio.solicitarReserva(estudiante, docente, horario);
        Horario ocupado = new Horario(LocalDateTime.of(2026, 9, 1, 10, 0), LocalDateTime.of(2026, 9, 1, 11, 0));
        ocupado.marcarOcupado();

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> servicio.reprogramarReserva(reserva.getId(), ocupado));

        assertEquals("El nuevo horario no esta disponible", error.getMessage());
        assertEquals(EstadoReserva.SOLICITADA, reserva.getEstado());
        assertEquals(horario, reserva.getHorario());
        assertFalse(horario.isDisponible());
        assertEquals(List.of("SOLICITADA"), eventos);
    }

    @Test
    void laObservacionDeLaPoliticaSeAnexaAlMotivoEntreCorchetes() {
        ServicioReservas servicioConObservacion = new ServicioReservas(
                new RepositorioReservasEnMemoria(),
                politicaQueObserva("cancelacion tardia: puede aplicar penalizacion"),
                List.of());
        Reserva reserva = servicioConObservacion.solicitarReserva(estudiante, docente, horario);

        servicioConObservacion.cancelarReserva(reserva.getId(), "Imprevisto del estudiante");

        assertEquals("Imprevisto del estudiante [cancelacion tardia: puede aplicar penalizacion]",
                reserva.getNotas());
    }

    @Test
    void sinObservacionLaNotaEsExactamenteElMotivo() {
        Reserva reserva = servicio.solicitarReserva(estudiante, docente, horario);

        servicio.cancelarReserva(reserva.getId(), "Imprevisto del estudiante");

        assertEquals("Imprevisto del estudiante", reserva.getNotas());
    }

    @Test
    void unaCancelacionRechazadaPorLaPoliticaNoCambiaElEstadoNiPublicaEvento() {
        ServicioReservas servicioConAntelacion = new ServicioReservas(
                new RepositorioReservasEnMemoria(),
                new PoliticaCancelacionConAntelacion(24),
                List.of((reserva, evento) -> eventos.add(evento)));
        Horario yaIniciado = new Horario(LocalDateTime.now().minusMinutes(30), LocalDateTime.now().plusMinutes(30));
        Reserva reserva = servicioConAntelacion.solicitarReserva(estudiante, docente, yaIniciado);
        eventos.clear();

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> servicioConAntelacion.cancelarReserva(reserva.getId(), "Tarde"));

        assertEquals("No se puede cancelar una reserva cuyo horario ya inicio o finalizo", error.getMessage());
        assertEquals(EstadoReserva.SOLICITADA, reserva.getEstado());
        assertEquals("", reserva.getNotas());
        assertEquals(List.of(), eventos);
    }

    @Test
    void cadaCambioDeEstadoPublicaSuEventoEnOrden() {
        Reserva reserva = servicio.solicitarReserva(estudiante, docente, horario);
        servicio.confirmarReserva(reserva.getId());
        servicio.reprogramarReserva(reserva.getId(),
                new Horario(LocalDateTime.of(2026, 9, 1, 10, 0), LocalDateTime.of(2026, 9, 1, 11, 0)));
        servicio.cancelarReserva(reserva.getId(), "Fin de la demostracion");

        assertEquals(List.of("SOLICITADA", "CONFIRMADA", "REPROGRAMADA", "CANCELADA"), eventos);
    }

    private PoliticaCancelacion politicaQueObserva(String observacion) {
        return (reserva, momento) -> ResultadoCancelacion.permitidaConObservacion(observacion);
    }
}
