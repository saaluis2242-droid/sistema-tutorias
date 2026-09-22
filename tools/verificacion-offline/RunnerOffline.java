import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ejecutor minimo de pruebas JUnit 5 para entornos SIN acceso a Maven
 * Central (donde no se puede descargar junit-jupiter ni surefire).
 *
 * Recibe como argumentos los nombres completos de las clases de prueba,
 * y por cada una: instancia la clase, ejecuta los metodos anotados con
 * @BeforeEach y luego cada metodo anotado con @Test en su propia
 * instancia (misma semantica de aislamiento que JUnit 5), reportando
 * OK/FAIL por prueba y un resumen final. El codigo de salida es 1 si
 * alguna prueba falla, de modo que puede usarse en un script como
 * criterio de "suite verde".
 *
 * Las pruebas del proyecto NO se modifican para este ejecutor: son
 * pruebas JUnit 5 normales. Este ejecutor solo existe para poder
 * demostrar el ciclo PRUEBA VERDE -> CAMBIO PEQUENO -> PRUEBA VERDE
 * dentro del entorno de trabajo; la ejecucion oficial es
 * `mvn clean test`.
 */
public class RunnerOffline {

    public static void main(String[] args) throws Exception {
        int ok = 0;
        List<String> fallos = new ArrayList<>();

        for (String nombreClase : args) {
            Class<?> claseDePrueba = Class.forName(nombreClase);
            System.out.println("=== " + claseDePrueba.getSimpleName() + " ===");

            List<Method> pruebas = Arrays.stream(claseDePrueba.getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(Test.class))
                    .sorted((a, b) -> a.getName().compareTo(b.getName()))
                    .toList();

            for (Method prueba : pruebas) {
                String etiqueta = claseDePrueba.getSimpleName() + "." + prueba.getName();
                try {
                    Constructor<?> constructor = claseDePrueba.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    Object instancia = constructor.newInstance();

                    for (Method antes : claseDePrueba.getDeclaredMethods()) {
                        if (antes.isAnnotationPresent(BeforeEach.class)) {
                            antes.setAccessible(true);
                            antes.invoke(instancia);
                        }
                    }

                    prueba.setAccessible(true);
                    prueba.invoke(instancia);

                    ok++;
                    System.out.println("  [OK]   " + prueba.getName());
                } catch (InvocationTargetException e) {
                    Throwable causa = e.getCause();
                    fallos.add(etiqueta + " -> " + causa);
                    System.out.println("  [FAIL] " + prueba.getName() + " -> " + causa);
                } catch (Exception e) {
                    fallos.add(etiqueta + " -> " + e);
                    System.out.println("  [ERROR] " + prueba.getName() + " -> " + e);
                }
            }
            System.out.println();
        }

        System.out.println("-----------------------------------------------");
        System.out.println("Pruebas ejecutadas: " + (ok + fallos.size())
                + ", correctas: " + ok + ", fallidas: " + fallos.size());
        if (fallos.isEmpty()) {
            System.out.println("RESULTADO: SUITE VERDE");
        } else {
            System.out.println("RESULTADO: SUITE ROJA");
            fallos.forEach(f -> System.out.println("  - " + f));
            System.exit(1);
        }
    }
}
