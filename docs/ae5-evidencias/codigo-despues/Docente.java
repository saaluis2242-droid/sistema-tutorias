package edu.uees.tutorias.domain;

import java.util.List;

/**
 * Docente que publica y administra su propia disponibilidad de horarios.
 *
 * La agenda es una composicion: los Horario de un Docente viven y mueren
 * con el (si se elimina el Docente, sus horarios dejan de tener
 * sentido).
 *
 * <p>Refactorizacion 2 de Ae5 (Extract Class): la lista de horarios y la
 * regla de no solapamiento vivian aqui, mezclando la identidad del
 * usuario con la administracion de una estructura de datos de negocio.
 * Ahora esa responsabilidad es de {@link AgendaDocente} y esta clase
 * delega en ella, conservando la misma interfaz publica
 * ({@code publicarHorario}, {@code getHorarios}) para no afectar a
 * ningun cliente.</p>
 */
public class Docente extends Usuario {

    private final String especialidad;
    private final AgendaDocente agenda = new AgendaDocente();

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

    /** Agenda del docente, para operar sobre su disponibilidad directamente. */
    public AgendaDocente getAgenda() {
        return agenda;
    }

    public List<Horario> getHorarios() {
        return agenda.getHorarios();
    }

    /** Publica un nuevo horario, evitando que se solape con uno existente. */
    public void publicarHorario(Horario horario) {
        agenda.publicar(horario);
    }

    @Override
    public String describirRol() {
        return "Docente";
    }
}
