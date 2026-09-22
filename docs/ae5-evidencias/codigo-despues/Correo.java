package edu.uees.tutorias.domain;

/**
 * Value Object que representa una direccion de correo electronico valida.
 *
 * <p>Problema de diseño que resuelve (Refactorizacion 1 de Ae5):
 * {@link Usuario} guardaba el correo como un {@code String} y validaba
 * su forma en un metodo privado ({@code validarCorreo}). Eso es
 * <em>Primitive Obsession</em>: el tipo del atributo no dice nada sobre
 * la regla que lo hace valido, la validacion vive lejos del dato y
 * cualquier otra clase que reciba un {@code String} "correo" no tiene
 * forma de saber si ya fue validado. Con el tiempo, la misma regla
 * (contener una arroba, y mañana el dominio institucional o la
 * normalizacion a minusculas) se habria duplicado en cada punto de
 * entrada.</p>
 *
 * <p>Al convertirlo en Value Object, la regla queda en un solo lugar y
 * se aplica en el momento de construir el valor: si existe una
 * instancia de {@code Correo}, es valida por construccion, y el propio
 * tipo lo comunica en cada firma donde aparece. Es inmutable y su
 * identidad es su valor, no una referencia (de ahi {@code record}).</p>
 *
 * <p>El comportamiento observable no cambia: se sigue rechazando un
 * correo nulo o sin arroba con {@link IllegalArgumentException} y con el
 * mismo mensaje, y {@code toString()} devuelve el texto original, de modo
 * que los notificadores que lo interpolan siguen imprimiendo lo mismo.</p>
 */
public record Correo(String valor) {

    public Correo {
        if (valor == null || !valor.contains("@")) {
            throw new IllegalArgumentException("El correo del usuario no es valido");
        }
    }

    /** Parte local del correo (lo que precede a la arroba). */
    public String usuario() {
        return valor.substring(0, valor.indexOf('@'));
    }

    /** Dominio del correo (lo que sigue a la arroba). */
    public String dominio() {
        return valor.substring(valor.indexOf('@') + 1);
    }

    @Override
    public String toString() {
        return valor;
    }
}
