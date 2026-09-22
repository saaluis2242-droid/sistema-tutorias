package org.junit.jupiter.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stub minimo de la anotacion de JUnit 5, usado UNICAMENTE por el
 * verificador offline (tools/verificacion-offline) cuando el entorno no
 * tiene acceso a Maven Central para descargar junit-jupiter.
 *
 * No forma parte del codigo del proyecto (src/) ni se empaqueta: cuando
 * se ejecuta `mvn clean test` con acceso a internet, las pruebas usan la
 * anotacion real de JUnit 5 y estos stubs quedan fuera del classpath.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
}
