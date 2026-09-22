package edu.uees.tutorias.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Red de seguridad de Ae5 sobre el contrato de identidad y validacion de
 * los usuarios.
 *
 * <p>Fija el comportamiento observable que la Refactorizacion 1 (Value
 * Object {@code Correo}) no puede alterar: que un correo sin arroba sea
 * rechazado con {@code IllegalArgumentException} y con el mismo mensaje,
 * que el correo se lea de vuelta con el mismo texto con el que se
 * construyo, que el canal por defecto sea EMAIL y que la igualdad de
 * usuarios siga dependiendo unicamente del id.</p>
 */
class UsuarioTest {

    @Test
    void unCorreoSinArrobaEsRechazadoConElMismoMensajeDeSiempre() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new Estudiante("E1", "Luis Perez", "luis.perez.uees.edu.ec", "UEES-001"));

        assertEquals("El correo del usuario no es valido", error.getMessage());
    }

    @Test
    void unCorreoNuloEsRechazado() {
        assertThrows(IllegalArgumentException.class,
                () -> new Docente("D1", "Ana Torres", null, "Bases de Datos"));
    }

    @Test
    void elCorreoSeLeeDeVueltaComoElMismoTexto() {
        Estudiante estudiante = new Estudiante("E1", "Luis Perez", "luis.perez@uees.edu.ec", "UEES-001");

        assertEquals("luis.perez@uees.edu.ec", String.valueOf(estudiante.getCorreo()));
    }

    @Test
    void unNombreEnBlancoEsRechazado() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new Estudiante("E1", "   ", "luis@uees.edu.ec", "UEES-001"));

        assertEquals("El nombre del usuario es obligatorio", error.getMessage());
    }

    @Test
    void elCodigoEstudiantilEsObligatorio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Estudiante("E1", "Luis Perez", "luis@uees.edu.ec", " "));
    }

    @Test
    void laEspecialidadDelDocenteEsObligatoria() {
        assertThrows(IllegalArgumentException.class,
                () -> new Docente("D1", "Ana Torres", "ana@uees.edu.ec", ""));
    }

    @Test
    void elCanalPreferidoPorDefectoEsEmailYPuedeCambiarse() {
        Docente docente = new Docente("D1", "Ana Torres", "ana@uees.edu.ec", "Bases de Datos");
        assertEquals(CanalNotificacion.EMAIL, docente.getCanalPreferido());

        docente.setCanalPreferido(CanalNotificacion.WHATSAPP);

        assertEquals(CanalNotificacion.WHATSAPP, docente.getCanalPreferido());
    }

    @Test
    void dosUsuariosConElMismoIdSonElMismoUsuario() {
        Estudiante uno = new Estudiante("E1", "Luis Perez", "luis@uees.edu.ec", "UEES-001");
        Estudiante otro = new Estudiante("E1", "Luis P.", "luis.perez@uees.edu.ec", "UEES-002");
        Estudiante distinto = new Estudiante("E2", "Maria Lopez", "maria@uees.edu.ec", "UEES-003");

        assertTrue(uno.equals(otro));
        assertEquals(uno.hashCode(), otro.hashCode());
        assertFalse(uno.equals(distinto));
    }

    @Test
    void cadaTipoDeUsuarioDescribeSuPropioRol() {
        assertEquals("Estudiante",
                new Estudiante("E1", "Luis Perez", "luis@uees.edu.ec", "UEES-001").describirRol());
        assertEquals("Docente",
                new Docente("D1", "Ana Torres", "ana@uees.edu.ec", "Bases de Datos").describirRol());
    }
}
