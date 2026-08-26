package edu.uees.tutorias.domain;

/**
 * Estudiante que solicita tutorias. No conoce como se persisten sus
 * reservas ni como se envian las notificaciones: esas responsabilidades
 * pertenecen a otras clases (bajo acoplamiento).
 */
public class Estudiante extends Usuario {

    private final String codigoEstudiantil;

    public Estudiante(String id, String nombre, String correo, String codigoEstudiantil) {
        super(id, nombre, correo);
        if (codigoEstudiantil == null || codigoEstudiantil.isBlank()) {
            throw new IllegalArgumentException("El codigo estudiantil es obligatorio");
        }
        this.codigoEstudiantil = codigoEstudiantil;
    }

    public String getCodigoEstudiantil() {
        return codigoEstudiantil;
    }

    @Override
    public String describirRol() {
        return "Estudiante";
    }
}
