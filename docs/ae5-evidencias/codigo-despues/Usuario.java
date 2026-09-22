package edu.uees.tutorias.domain;

import java.util.Objects;

/**
 * Clase base abstracta para los usuarios del sistema.
 *
 * Concentra la informacion y las reglas comunes a cualquier persona que
 * participa en el sistema (identidad, nombre y correo). Estudiante y
 * Docente heredan de esta clase porque ambos SON usuarios y comparten
 * este mismo conjunto de atributos y reglas de validacion; no se trata
 * de una herencia usada solo para reutilizar codigo.
 *
 * <p>Refactorizacion 1 de Ae5 (introducir Value Object): el correo dejo
 * de ser un {@code String} validado por un metodo privado de esta clase
 * y paso a ser un {@link Correo}, que se valida a si mismo al
 * construirse. Usuario ya no tiene que conocer que hace valido a un
 * correo: solo pide uno. El comportamiento observable es el mismo (un
 * correo nulo o sin arroba sigue lanzando IllegalArgumentException con
 * el mismo mensaje, y el texto del correo se lee de vuelta igual).</p>
 */
public abstract class Usuario {

    private final String id;
    private final String nombre;
    private final Correo correo;
    private CanalNotificacion canalPreferido;

    protected Usuario(String id, String nombre, String correo) {
        this(id, nombre, correo, CanalNotificacion.EMAIL);
    }

    protected Usuario(String id, String nombre, String correo, CanalNotificacion canalPreferido) {
        this.id = Objects.requireNonNull(id, "El id no puede ser nulo");
        this.nombre = validarNombre(nombre);
        this.correo = new Correo(correo);
        this.canalPreferido = Objects.requireNonNull(canalPreferido, "El canal preferido no puede ser nulo");
    }

    private String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del usuario es obligatorio");
        }
        return nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Correo getCorreo() {
        return correo;
    }

    public CanalNotificacion getCanalPreferido() {
        return canalPreferido;
    }

    /**
     * Permite que un usuario cambie su canal de notificacion preferido
     * (por ejemplo, desde EMAIL a WHATSAPP) sin afectar a ninguna otra
     * clase: quien decide como se construye el Notificador adecuado es
     * la fabrica de notificadores (Factory Method), no Usuario.
     */
    public void setCanalPreferido(CanalNotificacion canalPreferido) {
        this.canalPreferido = Objects.requireNonNull(canalPreferido, "El canal preferido no puede ser nulo");
    }

    /**
     * Cada tipo de usuario define su propio rol dentro del sistema.
     * Este metodo es un ejemplo de polimorfismo: cada subclase lo
     * implementa de forma distinta sin que el resto del sistema
     * necesite conocer de que subtipo se trata.
     */
    public abstract String describirRol();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return id.equals(usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
