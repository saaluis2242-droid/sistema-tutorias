package edu.uees.tutorias.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Agenda de disponibilidad de un docente: la coleccion de horarios
 * publicados y la regla que impide que dos de ellos se solapen.
 *
 * <p>Problema de diseño que resuelve (Refactorizacion 2 de Ae5):
 * {@link Docente} tenia dos razones de cambio conviviendo en la misma
 * clase. Por un lado era un {@link Usuario} (identidad, nombre, correo,
 * canal preferido, rol); por otro administraba una estructura de datos
 * con su propia regla de negocio (la lista de horarios, la validacion de
 * solapamiento y la exposicion de solo lectura). Cualquier evolucion de
 * la agenda -- buscar el proximo horario libre, limitar cupos por
 * semana, retirar un horario ya publicado, replicar la agenda de una
 * semana -- habria engordado a Docente sin relacion con lo que hace a un
 * docente ser un usuario del sistema.</p>
 *
 * <p>Al extraer la clase, cada una queda con una unica razon de cambio:
 * Docente representa a la persona y delega en su agenda; AgendaDocente
 * es el lugar natural donde crecen las reglas de disponibilidad, y puede
 * probarse sin construir un Docente completo.</p>
 *
 * <p>El comportamiento observable no cambia: se sigue rechazando un
 * horario solapado con {@link IllegalStateException} y el mismo mensaje,
 * se conserva el orden de publicacion y la coleccion expuesta sigue
 * siendo inmodificable.</p>
 */
public class AgendaDocente {

    private final List<Horario> horarios = new ArrayList<>();

    /** Horarios publicados, en orden de publicacion y de solo lectura. */
    public List<Horario> getHorarios() {
        return Collections.unmodifiableList(horarios);
    }

    /**
     * Publica un nuevo horario, evitando que se solape con uno existente.
     *
     * <p>Nota de disciplina de refactorizacion: al mover este metodo se
     * detecto que no valida el nulo (publicar(null) sobre una agenda
     * vacia lo agregaria a la lista). Ese defecto latente se dejo tal
     * como estaba a proposito: una refactorizacion no debe corregir
     * comportamiento, solo estructura. Queda registrado como trabajo
     * pendiente, con su propia prueba, para un cambio posterior que si
     * sea un cambio funcional declarado.</p>
     */
    public void publicar(Horario horario) {
        if (seSolapaConAlguno(horario)) {
            throw new IllegalStateException("El horario se solapa con uno ya publicado");
        }
        horarios.add(horario);
    }

    private boolean seSolapaConAlguno(Horario horario) {
        return horarios.stream().anyMatch(publicado -> publicado.seSolapaCon(horario));
    }
}
