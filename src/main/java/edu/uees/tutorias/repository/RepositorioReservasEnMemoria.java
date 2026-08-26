package edu.uees.tutorias.repository;

import edu.uees.tutorias.domain.Reserva;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementacion de RepositorioReservas que guarda las reservas en un
 * mapa en memoria. Util para pruebas y para la etapa inicial del
 * proyecto; puede sustituirse por una implementacion con base de datos
 * sin afectar a ServicioReservas, que solo conoce la interfaz.
 */
public class RepositorioReservasEnMemoria implements RepositorioReservas {

    private final Map<String, Reserva> reservas = new LinkedHashMap<>();

    @Override
    public void guardar(Reserva reserva) {
        reservas.put(reserva.getId(), reserva);
    }

    @Override
    public Optional<Reserva> buscarPorId(String id) {
        return Optional.ofNullable(reservas.get(id));
    }

    @Override
    public List<Reserva> listarPorEstudiante(String estudianteId) {
        return reservas.values().stream()
                .filter(r -> r.getEstudiante().getId().equals(estudianteId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reserva> listarPorDocente(String docenteId) {
        return reservas.values().stream()
                .filter(r -> r.getDocente().getId().equals(docenteId))
                .collect(Collectors.toList());
    }
}
