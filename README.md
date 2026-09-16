# Diseño de Software - Proyecto integrador

## Información general

- **Universidad:** Universidad Espíritu Santo
- **Carrera:** Computación
- **Asignatura:** Diseño de Software
- **Código:** UCOM0310
- **Periodo:** PEL 4 - 2026
- **Estudiante:** Luis Saa
- **Docente:** Ph.D. Jaime Paul Sayago Heredia

## Descripción

Sistema de gestión de tutorías académicas. Un docente publica bloques de
horario disponibles y un estudiante solicita una tutoría para uno de esos
bloques; la solicitud queda registrada como una reserva que puede
confirmarse, cancelarse o reprogramarse, y cuyos eventos relevantes se
comunican a los usuarios involucrados. El diseño mantiene la lógica de
negocio independiente de la tecnología de persistencia, de la regla de
cancelación vigente y del canal de notificación, para que las tres
puedan cambiar sin afectar las reglas del dominio.

Este repositorio documenta la evolución del proyecto en cuatro entregas:

- **Ae1** (Semana 2): análisis de dominio, diseño OO, cohesión/acoplamiento,
  SOLID y UML inicial.
- **Ae2** (Semana 3, `semana3-patrones`, repositorio aparte): práctica
  comparativa de **Factory Method** y **Builder** sobre el mismo dominio.
- **Ae3 — Incremento 1** (Semana 4): identificación de problemas reales de
  diseño e integración de **Strategy** y **Observer**, recuperando de Ae2
  el patrón que seguía justificado.
- **Ae4 — Kata de refactorización** (Semana 5, esta entrega): se agregó un
  generador de recibos con Code Smells deliberados y se refactorizó en 4
  pasos incrementales (Rename, Extract Method, Replace Magic Number,
  Simplify Conditional) verificando en cada paso que el comportamiento
  observable no cambia.

## Objetivos

- Modelar el dominio del sistema de gestión de tutorías aplicando
  abstracción, encapsulación, herencia y polimorfismo.
- Diseñar clases con alta cohesión y bajo acoplamiento, separando la
  lógica de negocio de la persistencia, la política de cancelación y la
  comunicación con los usuarios.
- Aplicar y justificar principios SOLID (SRP, DIP, OCP) sobre decisiones
  concretas del diseño, incluidas las introducidas por los patrones.
- Integrar Strategy y Observer para resolver problemas reales
  identificados en el incremento anterior, y recuperar de Ae2 el patrón
  creacional (Factory Method) que seguía aportando valor.
- Mantener el diagrama UML de clases coherente con la implementación en
  Java en cada incremento.
- Aplicar un proceso seguro de refactorización (Rename, Extract Method,
  Replace Magic Number, Simplify Conditional) sobre código con Code
  Smells reales, verificando en cada paso que el comportamiento
  observable se conserva.

## Tecnologías

- Java 21
- Apache Maven 3.9.x
- Git y GitHub
- JUnit 5

## Requisitos previos

- JDK 21 instalado.
- Maven disponible en PATH.
- Git configurado.

## Instalación

```bash
git clone https://github.com/saaluis2242-droid/sistema-tutorias.git
cd sistema-tutorias
mvn clean test
```

## Ejecución

```bash
mvn package
java -cp target/classes edu.uees.tutorias.app.Main
```

`Main` ejecuta una demostración del incremento 1: publica un horario,
solicita una reserva, la confirma, la reprograma y finalmente cancela una
segunda reserva a último momento para mostrar cómo la política de
cancelación con antelación la marca como tardía. Cada cambio de estado se
publica a dos observadores (notificación por el canal preferido del
usuario, vía Factory Method, y un registro de auditoría en memoria).

## Estructura del proyecto

```text
src/
├── main/
│   └── java/
│       └── edu/uees/tutorias/
│           ├── domain/                  (Usuario, Estudiante, Docente, Horario,
│           │                              Reserva, EstadoReserva, CanalNotificacion)
│           ├── service/
│           │   ├── ServicioReservas.java
│           │   └── cancelacion/         (Strategy: PoliticaCancelacion + 2 implementaciones,
│           │                              ResultadoCancelacion)
│           ├── repository/              (RepositorioReservas, RepositorioReservasEnMemoria)
│           ├── notification/
│           │   ├── Notificador.java + 3 implementaciones (Email/Sms/WhatsApp)
│           │   ├── factory/             (Factory Method: NotificadorCreator + 3 creators
│           │   │                          + NotificadorCreatorFactory)
│           │   └── observer/            (Observer: ObservadorReserva + NotificadorObservador
│           │                              + RegistroAuditoriaObservador)
│           ├── reporte/                 (GeneradorReciboReserva, Ae4)
│           └── app/                     (Main)
└── test/
    └── java/
        └── edu/uees/tutorias/
            ├── ServicioReservasTest.java
            ├── notification/factory/NotificadorCreatorFactoryTest.java
            └── reporte/LineaBaseRecibos.java   (linea base y verificacion de Ae4)
docs/
├── modelo-clases.svg / .png         (UML de Ae1)
├── uml-incremento1.svg / .png       (UML actualizado de Ae3)
└── ae4-evidencias/                  (linea base antes/despues, git log — Ae4)
pom.xml
README.md
GUIA_GIT.md
```

## Funcionalidades

- Publicación de horarios disponibles por parte de un docente, con
  validación de solapamiento.
- Solicitud de una reserva de tutoría por parte de un estudiante.
- Confirmación, cancelación y reprogramación de una reserva, con
  validación de las transiciones de estado permitidas.
- Política de cancelación configurable (Strategy): estándar (sin
  restricciones, comportamiento de Ae1) o con antelación mínima
  (rechaza cancelar horarios ya iniciados y marca como tardías las
  cancelaciones de último momento).
- Reacciones desacopladas a cada cambio de estado de una reserva
  (Observer): notificación a los usuarios por su canal preferido y
  registro de auditoría; se pueden agregar más observadores sin tocar
  `ServicioReservas`.
- Construcción del `Notificador` adecuado según el canal preferido de
  cada usuario sin que el código cliente conozca las clases concretas
  (Factory Method, recuperado y adaptado de Ae2).

## Problemas de diseño identificados en este incremento

1. **Regla de cancelación fija.** En Ae1, `Reserva.cancelar()` aplicaba
   una única regla sin condiciones. Cualquier variación futura (penalizar
   cancelaciones tardías, exigir anticipación mínima) habría significado
   agregar condicionales dentro de `Reserva` o `ServicioReservas`,
   violando OCP. Se resolvió con **Strategy**
   (`PoliticaCancelacion`).
2. **Notificación acoplada a un único canal.** En Ae1, `ServicioReservas`
   dependía de un solo `Notificador` inyectado por constructor. Agregar
   una segunda reacción a un mismo evento (por ejemplo, un registro de
   auditoría además del correo) habría obligado a modificar el
   constructor y cada método de `ServicioReservas`. Se resolvió con
   **Observer** (`ObservadorReserva`), y el propio observer de
   notificación reutiliza el **Factory Method** recuperado de Ae2 para
   elegir el canal según la preferencia de cada usuario.

## Patrones recuperados de Ae2

| Patrón | ¿Se mantiene? | Justificación |
|---|---|---|
| **Factory Method** | Sí | Sigue resolviendo un problema real: decidir qué `Notificador` concreto construir (Email, SMS, WhatsApp) sin que el código cliente conozca las clases concretas. Se migró del proyecto de práctica (`semana3-patrones`) al dominio real y se integró como el mecanismo que usa `NotificadorObservador` para elegir canal. |
| **Builder** | No | La `Reserva` del dominio real (Ae1) solo requiere 3 parámetros obligatorios (estudiante, docente, horario) sin combinaciones opcionales complejas. Aplicar Builder aquí no resolvería ningún problema real y sería sobre-ingeniería; el patrón queda documentado y demostrado en `semana3-patrones` como ejercicio, pero no se traslada a este repositorio. |

## Principios SOLID relevantes en este incremento

- **SRP:** `PoliticaCancelacion`, `ObservadorReserva` y `NotificadorCreator`
  separan tres responsabilidades que antes vivían implícitamente dentro
  de `ServicioReservas` (la regla de cancelación, la reacción a un
  evento y la construcción de un canal de notificación).
- **OCP:** se puede agregar una política de cancelación, un observador o
  un canal de notificación nuevos creando una clase (más una línea de
  registro en `NotificadorCreatorFactory` para el caso de canales) sin
  modificar `ServicioReservas` ni las clases existentes.
- **DIP:** `ServicioReservas` depende únicamente de las interfaces
  `RepositorioReservas`, `PoliticaCancelacion` y `ObservadorReserva`;
  nunca de una implementación concreta.

## Kata de refactorización (Ae4)

Para esta actividad se agregó una clase pequeña y autocontenida,
`GeneradorReciboReserva` (paquete `reporte`), que genera el texto de un
recibo a partir de una `Reserva` ya existente. Se escribió deliberadamente
rápido y sin cuidar la estructura interna (como suele pasar con una
utilidad que "solo iba a usarse una vez"), y fue el sujeto de la Kata.
**No se le agregó ninguna funcionalidad nueva**: las 4 refactorizaciones
solo cambian la estructura interna del código ya existente.

### Código inicial (`Rpt`)

```java
public class Rpt {
    public String proc(Reserva r) {
        String s = "";
        String e = r.getEstudiante().getNombre();
        String d = r.getDocente().getNombre();
        double p = 15.0;
        String flag1 = r.getEstado().toString();
        if (flag1.equals("REPROGRAMADA")) {
            p = p - (p * 0.1);
        } else {
            if (flag1.equals("CANCELADA")) {
                p = 0.0;
            } else {
                if (flag1.equals("COMPLETADA")) {
                    if (r.getEstudiante().getCodigoEstudiantil() != null
                            && r.getEstudiante().getCodigoEstudiantil().startsWith("UEES")) {
                        p = p - (p * 0.2);
                    }
                }
            }
        }
        // ... construcción del String de salida con la misma lógica anidada
    }
}
```

### Matriz de Code Smells

| Código / ubicación | Smell | Evidencia | Impacto |
|---|---|---|---|
| Clase `Rpt`, método `proc` | Nombres poco significativos (Mysterious Name) | `Rpt`, `proc`, variables `e`, `d`, `p`, `s`, `flag1` | Nadie puede saber qué hace la clase ni sus variables sin leer todo el cuerpo del método. |
| `proc` completo | Long Method / múltiples responsabilidades | Un único método de ~35 líneas calcula el precio, decide el descuento y arma todo el texto del recibo | Cualquier cambio en una de las tres cosas obliga a releer y tocar el método completo; dificulta las pruebas de cada regla por separado. |
| Cálculo de precio y de línea de estado | Conditional Complexity (if/else anidados en 3 niveles) | Dos bloques `if/else` anidados que comparan el mismo `flag1` contra los mismos literales | Duplica la lógica de "qué estado es" en dos lugares distintos; agregar un estado nuevo obliga a tocar ambos bloques y es fácil olvidar uno. |
| `15.0`, `0.1`, `0.2`, `"UEES"` dentro de `proc` | Magic Number / Magic String | Literales sueltos sin nombre que representan reglas de negocio (precio base, % de descuento, prefijo de fidelidad) | No queda registrado en el código *por qué* esos valores son esos; cambiarlos exige ubicar el literal exacto sin romper otro cálculo parecido. |
| `flag1.equals("REPROGRAMADA")`, etc. | Primitive Obsession | Se compara el `String` que produce `estado.toString()` en vez de usar el enum `EstadoReserva` que ya existe en el dominio | El compilador no puede detectar un typo en el literal ni avisar si falta cubrir un estado nuevo del enum. |

### Plan de refactorización

| Prioridad | Problema | Refactorización | Justificación |
|---|---|---|---|
| 1 | Nombres no comunican intención | **Rename** (`Rpt`→`GeneradorReciboReserva`, `proc`→`generar`, variables) | Es el cambio de menor riesgo y hace legibles los pasos siguientes antes de tocar la lógica. |
| 2 | Método con 3 responsabilidades mezcladas | **Extract Method** (`calcularPrecio`, `construirEncabezado`, `construirLineaEstado`) | Separa "qué calculo" de "qué texto arma", permitiendo razonar y (a futuro) probar cada regla por separado. |
| 3 | Literales sin nombre | **Replace Magic Number/String with Constant** | Documenta en el propio código las reglas de negocio (precio base, descuentos, prefijo de fidelidad). |
| 4 | Comparación de Strings duplicada en dos métodos | **Simplify Conditional** (switch sobre `EstadoReserva`) | Aprovecha un tipo que ya existía en el dominio; el compilador exige cubrir todos los valores del enum, evitando el olvido de un caso. |

### Evidencia de verificación

Se construyeron 6 casos representativos que cubren los 5 valores de
`EstadoReserva` (incluyendo el caso "completada con código de fidelidad"
y "completada sin código de fidelidad") en
[`LineaBaseRecibos`](src/test/java/edu/uees/tutorias/reporte/LineaBaseRecibos.java).
Después de **cada** una de las 4 refactorizaciones se recompiló y se
volvió a ejecutar exactamente el mismo harness; los 6 casos dieron el
mismo texto de recibo, carácter por carácter, en los cinco momentos
(línea base + 4 refactorizaciones):

```
6 OK, 0 FAIL
```

La comparación (`diff`) entre la salida capturada ANTES de refactorizar
([`docs/ae4-evidencias/linea-base-antes.txt`](docs/ae4-evidencias/linea-base-antes.txt))
y la salida DESPUÉS de las 4 refactorizaciones
([`docs/ae4-evidencias/linea-despues.txt`](docs/ae4-evidencias/linea-despues.txt))
no reporta ninguna diferencia.

### Comparación técnica antes/después

| Dimensión | Antes | Después |
|---|---|---|
| Nombres | `Rpt`, `proc`, `e`, `d`, `p`, `s`, `flag1` | `GeneradorReciboReserva`, `generar`, `nombreEstudiante`, `precio`, `estado` |
| Métodos / responsabilidades | 1 método hace todo | 4 métodos, cada uno con una sola razón para cambiar |
| Condicionales / flujo | 2 cadenas de `if/else` anidadas en 3 niveles, comparando Strings | 2 `switch` exhaustivos sobre el enum `EstadoReserva`, sin anidamiento |
| Constantes / reglas | `15.0`, `0.1`, `0.2`, `"UEES"` sueltos en el código | `PRECIO_BASE`, `DESCUENTO_REPROGRAMACION`, `DESCUENTO_FIDELIDAD`, `PREFIJO_CODIGO_FIDELIDAD` |
| Comportamiento observable | — | Idéntico: los 6 casos producen el mismo texto de recibo antes y después |
| Git | 1 commit de línea base | 4 commits de refactorización, uno por técnica aplicada |

## Pruebas

Para ejecutar las pruebas:

```bash
mvn clean test
```

Se incluyen pruebas unitarias de `ServicioReservas` (solicitud, rechazo
de horario ocupado, confirmación, cancelación estándar, cancelación
tardía y rechazada según `PoliticaCancelacionConAntelacion`,
reprogramación, y registro de un observador en tiempo de ejecución) y de
`NotificadorCreatorFactory` (Factory Method).

> Nota de verificación: en el entorno donde se preparó este incremento y
> esta kata no hubo acceso de red a Maven Central para descargar JUnit,
> por lo que la lógica de cada prueba y de cada caso de la Kata de
> refactorización se verificó además con réplicas manuales (sin
> anotaciones JUnit) que confirmaron los mismos resultados. Se recomienda
> ejecutar `mvn clean test` en un entorno con acceso normal a internet
> para obtener el reporte oficial `BUILD SUCCESS`.

## Control de versiones

El proyecto utiliza la rama `main`. El historial de commits documenta la
evolución del análisis, el modelo y la implementación en cada incremento,
incluyendo el ciclo refactorización → compilar → ejecutar → comparar →
commit de la Kata de Ae4 (ver [`GUIA_GIT.md`](GUIA_GIT.md) para el detalle
de los commits sugeridos de cada entrega).

## Evidencias

- Diagrama UML de clases (Ae1): [`docs/modelo-clases.svg`](docs/modelo-clases.svg) / [`docs/modelo-clases.png`](docs/modelo-clases.png).
- Diagrama UML actualizado (Ae3 — Incremento 1): [`docs/uml-incremento1.svg`](docs/uml-incremento1.svg) / [`docs/uml-incremento1.png`](docs/uml-incremento1.png).
- Evidencia de pruebas: salida de `mvn clean test` (BUILD SUCCESS) y verificación manual descrita arriba.
- Evidencia de la Kata de refactorización (Ae4): [`docs/ae4-evidencias/`](docs/ae4-evidencias/) (línea base antes/después y `git log`).
- Documento de análisis y diseño entregado en Blackboard (PDF) de Ae1, Ae3 y Ae4, con las secciones de análisis, diseño OO, cohesión/acoplamiento, principios SOLID, patrones/refactorización, UML y conclusiones.

## Uso de inteligencia artificial

Durante el desarrollo de esta actividad utilicé herramientas de
inteligencia artificial. Las utilicé para: apoyar la identificación de
problemas de diseño a partir del código de Ae1, redactar una primera
versión del código Java que integra Strategy y Observer, migrar e
integrar el Factory Method de Ae2 al dominio real, generar el diagrama
UML actualizado a partir de las decisiones de diseño que definí, y en
Ae4, para ayudarme a construir un ejemplo representativo de Code Smells
sobre el cual practicar el proceso de refactorización y a redactar la
matriz de smells y la comparación antes/después. Verifiqué el
comportamiento (compilación, ejecución y verificación de la lógica de
cada prueba y de cada refactorización) y puedo explicar y justificar el
código y las decisiones presentadas en cada entrega, incluyendo por qué
se descartó Builder en Ae3 y por qué se priorizó cada refactorización de
Ae4 en el orden elegido.

## Autor

Luis Saa - saaluis2242@gmail.com
