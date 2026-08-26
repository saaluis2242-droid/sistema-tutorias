package edu.uees.tutorias.repository;

import edu.uees.tutorias.domain.Reserva;

import java.util.List;
import java.util.Optional;

/**
 * Abstraccion de persistencia para Reserva.
 *
 * ServicioReservas depende de esta interfaz y no de una tecnologia de
 * almacenamiento concreta (Dependency Inversion Principle). Si manana
 * el sistema cambia de una implementacion en memoria a una base de
 * datos relacional o a un servicio externo, solo se necesita una nueva
 * clase que implemente RepositorioReservas: la logica de negocio en
 * ServicioReservas no cambia.
 */
public interface RepositorioReservas {

    void guardar(Reserva reserva);

    Optional<Reserva> buscarPorId(String id);

    List<Reserva> listarPorEstudiante(String estudianteId);

    List<Reserva> listarPorDocente(String docenteId);
}
