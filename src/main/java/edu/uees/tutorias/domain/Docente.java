package edu.uees.tutorias.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Docente que publica y administra su propia disponibilidad de horarios.
 *
 * La lista de horarios es una composicion: los Horario de un Docente
 * viven y mueren con el (si se elimina el Docente, sus horarios dejan
 * de tener sentido). Por eso la coleccion se expone solo de lectura y
 * las reglas de publicacion (no solapar horarios) las protege esta
 * misma clase.
 */
public class Docente extends Usuario {

    private final String especialidad;
    private final List<Horario> horarios = new ArrayList<>();

    public Docente(String id, String nombre, String correo, String especialidad) {
        super(id, nombre, correo);
        if (especialidad == null || especialidad.isBlank()) {
            throw new IllegalArgumentException("La especialidad es obligatoria");
        }
        this.especialidad = especialidad;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public List<Horario> getHorarios() {
        return Collections.unmodifiableList(horarios);
    }

    /** Publica un nuevo horario, evitando que se solape con uno existente. */
    public void publicarHorario(Horario horario) {
        boolean seSolapa = horarios.stream().anyMatch(h -> h.seSolapaCon(horario));
        if (seSolapa) {
            throw new IllegalStateException("El horario se solapa con uno ya publicado");
        }
        horarios.add(horario);
    }

    @Override
    public String describirRol() {
        return "Docente";
    }
}
