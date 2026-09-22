package edu.uees.tutorias.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas propias del Value Object introducido en la Refactorizacion 1.
 *
 * <p>Estas pruebas no protegen comportamiento heredado (eso lo hace
 * {@link UsuarioTest}), sino que documentan el contrato del nuevo tipo:
 * un {@code Correo} no puede existir invalido, es igual a otro por su
 * valor y se imprime como el texto original. Ganar este punto de
 * prueba tan pequeño es justamente lo que hace valioso el Value Object:
 * antes, la misma regla solo podia probarse construyendo un Usuario
 * completo.</p>
 */
class CorreoTest {

    @Test
    void unCorreoValidoConservaSuTexto() {
        Correo correo = new Correo("luis.perez@uees.edu.ec");

        assertEquals("luis.perez@uees.edu.ec", correo.valor());
        assertEquals("luis.perez@uees.edu.ec", correo.toString());
    }

    @Test
    void unCorreoNoPuedeExistirInvalido() {
        assertEquals("El correo del usuario no es valido",
                assertThrows(IllegalArgumentException.class, () -> new Correo("sin-arroba")).getMessage());
        assertThrows(IllegalArgumentException.class, () -> new Correo(null));
    }

    @Test
    void dosCorreosConElMismoValorSonIguales() {
        assertEquals(new Correo("ana@uees.edu.ec"), new Correo("ana@uees.edu.ec"));
        assertEquals(new Correo("ana@uees.edu.ec").hashCode(), new Correo("ana@uees.edu.ec").hashCode());
    }

    @Test
    void elCorreoSabeSeparaseEnUsuarioYDominio() {
        Correo correo = new Correo("ana.torres@uees.edu.ec");

        assertEquals("ana.torres", correo.usuario());
        assertEquals("uees.edu.ec", correo.dominio());
    }
}
