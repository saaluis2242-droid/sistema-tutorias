package edu.uees.tutorias.domain;

/**
 * Value Object que representa un importe monetario.
 *
 * <p>Problema de diseño que resuelve (Refactorizacion 6 de Ae5): el
 * precio de una tutoria circulaba como un {@code double} suelto. Nada
 * distinguia ese numero de una cantidad de horas o de un porcentaje, el
 * formato con el que se imprime ("$15.0") estaba escrito a mano en el
 * generador de recibos, y la operacion "descontar un porcentaje" se
 * repetia como la expresion {@code precio - (precio * descuento)} en dos
 * ramas distintas del calculo.</p>
 *
 * <p>Como Value Object, el importe lleva consigo su formato y su unica
 * operacion de negocio. Se conserva deliberadamente {@code double} como
 * representacion interna, y la expresion aritmetica exacta que ya
 * existia, para que el total impreso no cambie ni en el ultimo decimal:
 * el objetivo de esta refactorizacion es reubicar responsabilidades, no
 * corregir el tipo numerico. Migrar a {@code BigDecimal} es un cambio de
 * comportamiento y queda registrado como trabajo posterior.</p>
 */
public record Dinero(double monto) {

    public static final Dinero CERO = new Dinero(0.0);

    /** Aplica un descuento expresado como fraccion (0.1 = 10%). */
    public Dinero menosDescuento(double fraccion) {
        return new Dinero(monto - (monto * fraccion));
    }

    /** Formato con el que el importe aparece en el recibo. */
    @Override
    public String toString() {
        return "$" + monto;
    }
}
