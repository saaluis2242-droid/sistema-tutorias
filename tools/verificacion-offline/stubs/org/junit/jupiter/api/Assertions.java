package org.junit.jupiter.api;

import java.util.Objects;

/**
 * Stub minimo de org.junit.jupiter.api.Assertions con la misma
 * semantica que la clase real para las aserciones que usa la suite de
 * este proyecto. Sirve UNICAMENTE al verificador offline
 * (tools/verificacion-offline) cuando no hay acceso a Maven Central.
 *
 * Cuando se ejecuta `mvn clean test` con acceso a internet, las pruebas
 * se compilan contra la clase real de JUnit 5 y este archivo queda
 * fuera del classpath.
 */
public final class Assertions {

    private Assertions() {
    }

    public static void assertEquals(Object esperado, Object obtenido) {
        if (!Objects.equals(esperado, obtenido)) {
            throw new AssertionError("esperado: <" + esperado + "> pero fue: <" + obtenido + ">");
        }
    }

    public static void assertEquals(int esperado, int obtenido) {
        assertEquals(Integer.valueOf(esperado), Integer.valueOf(obtenido));
    }

    public static void assertEquals(long esperado, long obtenido) {
        assertEquals(Long.valueOf(esperado), Long.valueOf(obtenido));
    }

    public static void assertEquals(double esperado, double obtenido) {
        if (Double.compare(esperado, obtenido) != 0) {
            throw new AssertionError("esperado: <" + esperado + "> pero fue: <" + obtenido + ">");
        }
    }

    public static void assertEquals(Object esperado, Object obtenido, String mensaje) {
        if (!Objects.equals(esperado, obtenido)) {
            throw new AssertionError(mensaje + " ==> esperado: <" + esperado + "> pero fue: <" + obtenido + ">");
        }
    }

    public static void assertTrue(boolean condicion) {
        if (!condicion) {
            throw new AssertionError("esperado: <true> pero fue: <false>");
        }
    }

    public static void assertTrue(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    public static void assertFalse(boolean condicion) {
        if (condicion) {
            throw new AssertionError("esperado: <false> pero fue: <true>");
        }
    }

    public static void assertFalse(boolean condicion, String mensaje) {
        if (condicion) {
            throw new AssertionError(mensaje);
        }
    }

    public static void assertNotNull(Object valor) {
        if (valor == null) {
            throw new AssertionError("esperado: no nulo");
        }
    }

    public static void assertSame(Object esperado, Object obtenido) {
        if (esperado != obtenido) {
            throw new AssertionError("esperado la misma instancia");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T assertThrows(Class<T> tipoEsperado, Ejecutable ejecutable) {
        try {
            ejecutable.execute();
        } catch (Throwable lanzada) {
            if (tipoEsperado.isInstance(lanzada)) {
                return (T) lanzada;
            }
            throw new AssertionError("esperado " + tipoEsperado.getName()
                    + " pero se lanzo " + lanzada.getClass().getName() + ": " + lanzada.getMessage());
        }
        throw new AssertionError("esperado " + tipoEsperado.getName() + " pero no se lanzo ninguna excepcion");
    }

    public static void assertDoesNotThrow(Ejecutable ejecutable) {
        try {
            ejecutable.execute();
        } catch (Throwable lanzada) {
            throw new AssertionError("no se esperaba excepcion pero se lanzo "
                    + lanzada.getClass().getName() + ": " + lanzada.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T assertInstanceOf(Class<T> tipoEsperado, Object valor) {
        if (!tipoEsperado.isInstance(valor)) {
            throw new AssertionError("esperado instancia de " + tipoEsperado.getName()
                    + " pero fue: <" + (valor == null ? "null" : valor.getClass().getName()) + ">");
        }
        return (T) valor;
    }

    /** Equivalente al org.junit.jupiter.api.function.Executable real. */
    @FunctionalInterface
    public interface Ejecutable {
        void execute() throws Throwable;
    }
}
