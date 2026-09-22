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

Este repositorio documenta la evolución del proyecto en cinco entregas:

- **Ae1** (Semana 2): análisis de dominio, diseño OO, cohesión/acoplamiento,
  SOLID y UML inicial.
- **Ae2** (Semana 3, `semana3-patrones`, repositorio aparte): práctica
  comparativa de **Factory Method** y **Builder** sobre el mismo dominio.
- **Ae3 — Incremento 1** (Semana 4): identificación de problemas reales de
  diseño e integración de **Strategy** y **Observer**, recuperando de Ae2
  el patrón que seguía justificado.
- **Ae4 — Kata de refactorización** (Semana 5): se agregó un
  generador de recibos con Code Smells deliberados y se refactorizó en 4
  pasos incrementales (Rename, Extract Method, Replace Magic Number,
  Simplify Conditional) verificando en cada paso que el comportamiento
  observable no cambia.
- **Ae5 — Refactorización respaldada por pruebas unitarias** (Semana 6,
  esta entrega): se amplió la suite JUnit hasta convertirla en una red de
  seguridad de caracterización y, con ella en verde, se aplicaron **6
  refactorizaciones avanzadas** sobre el diseño heredado de Ae1–Ae4
  (Value Object, Extract Class, Move Method, Guard Clauses, Decompose
  Conditional y agrupación de un Data Clump), una por commit y
  ejecutando la suite completa antes y después de cada cambio.

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
- Construir una red de seguridad de pruebas unitarias de caracterización
  y, respaldado por ella, refactorizar el diseño heredado con técnicas de
  mayor alcance (Value Object, Extract Class, Move Method, Guard Clauses,
  Decompose Conditional, Data Clumps), demostrando con pruebas y con el
  historial Git que el comportamiento preservado no se alteró.

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
│           │                              Reserva, EstadoReserva, CanalNotificacion,
│           │                              Correo, AgendaDocente, SolicitudTutoria,
│           │                              Dinero — los cuatro ultimos de Ae5;
│           │                              tarifa/TarifarioTutoria, Ae5)
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
            ├── ServicioReservasTest.java              (Ae4)
            ├── ServicioReservasCaracterizacionTest.java (Ae5)
            ├── domain/                                (Ae5: UsuarioTest, CorreoTest,
            │                                            HorarioTest, ReservaTest,
            │                                            DocenteTest, AgendaDocenteTest,
            │                                            SolicitudTutoriaTest,
            │                                            tarifa/TarifarioTutoriaTest)
            ├── service/cancelacion/ResultadoCancelacionTest.java (Ae5)
            ├── notification/factory/NotificadorCreatorFactoryTest.java
            └── reporte/
                ├── GeneradorReciboReservaTest.java    (Ae5: los 6 casos de Ae4 en JUnit)
                └── LineaBaseRecibos.java              (harness manual de Ae4)
tools/
├── verificar.sh                     (mvn clean test + captura del log)
├── verificar-offline.sh             (ejecuta la misma suite sin Maven Central)
└── verificacion-offline/            (ejecutor por reflexion + stubs de la API de JUnit)
docs/
├── modelo-clases.svg / .png         (UML de Ae1)
├── uml-incremento1.svg / .png       (UML actualizado de Ae3)
├── ae4-evidencias/                  (linea base antes/despues, git log — Ae4)
└── ae5-evidencias/                  (suite verde por commit, git log, diffs,
                                      codigo antes/despues, parches — Ae5)
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

## Refactorización respaldada por pruebas unitarias (Ae5)

Ae4 refactorizó una clase pequeña y autocontenida. Ae5 va sobre el
diseño heredado del propio sistema (Ae1–Ae4) con técnicas de mayor
alcance, y la condición para hacerlo con seguridad es tener primero una
red de pruebas que fije el comportamiento que debe preservarse.

### Paso 0 | Red de seguridad

Antes de tocar `src/main` se amplió la suite hasta **54 pruebas** que
describen lo que el sistema hace hoy, no lo que debería hacer:

| Clase de prueba | Qué fija |
|---|---|
| `GeneradorReciboReservaTest` | Los 6 recibos del harness de Ae4, migrados a JUnit 5 y comparados carácter por carácter (incluye el formato `$13.5` y los dos caminos del descuento de fidelidad). |
| `UsuarioTest` | Validación de nombre y correo con sus mensajes exactos, canal por defecto, igualdad por id, rol de cada subtipo. |
| `HorarioTest` | Validación de inicio/fin, ocupar y liberar, solapamiento, igualdad por intervalo y el `toString()` del que depende el recibo. |
| `ReservaTest` | Todas las transiciones de estado válidas e inválidas con sus mensajes, y el efecto de cada una sobre el horario y las notas. |
| `DocenteTest` | Publicación de horarios, orden, rechazo del solapamiento y colección de solo lectura. |
| `ServicioReservasCaracterizacionTest` | Mensajes exactos de cada rechazo, composición de la nota de cancelación, orden de los eventos publicados y ausencia de efectos a medias cuando una operación se rechaza. |
| `ServicioReservasTest` (de Ae4) | Se mantuvo **sin modificar**, como control independiente. |

Este paso es un commit aparte (`test: red de seguridad…`) que no cambia
ni una línea de `src/main`.

### Ciclo aplicado

    PRUEBA VERDE → CAMBIO PEQUEÑO → PRUEBA VERDE → COMMIT → SIGUIENTE CAMBIO

Ninguna refactorización se acumuló con otra: cada una tiene su commit,
y la suite completa se ejecutó antes y después de cada cambio. Los logs
por commit están en [`docs/ae5-evidencias/`](docs/ae5-evidencias/) y se
regeneraron ejecutando la suite sobre cada commit del historial, de modo
que cada archivo corresponde exactamente a ese estado del código.

### Las 6 refactorizaciones

| # | Técnica | Problema de diseño | Qué se hizo | Prueba que la protege | Commit |
|---|---|---|---|---|---|
| 1 | **Introduce Value Object** | `Usuario` guardaba el correo como `String` y validaba su forma en un método privado (*Primitive Obsession*): el tipo no comunicaba la regla, la validación vivía lejos del dato y cualquier clase que recibiera un "String correo" no podía saber si ya estaba validado. | Se crea el record `Correo`, válido por construcción; pasa a ser el tipo del atributo. `toString()` devuelve el texto original, por lo que los notificadores imprimen lo mismo. | `UsuarioTest` (correo nulo y sin arroba, mismo mensaje; correo leído de vuelta igual), `NotificadorCreatorFactoryTest`. | `refactor: introduce el Value Object Correo…` |
| 2 | **Extract Class** + Move Method | `Docente` tenía dos razones de cambio: ser un `Usuario` y administrar la lista de horarios con su regla de no solapamiento. Toda evolución de la disponibilidad engordaba una clase de identidad. | La colección y la regla se mueven a `AgendaDocente`; `Docente` delega y conserva su interfaz pública. | `DocenteTest` (orden, mensaje del rechazo, colección inmodificable) y el nuevo `AgendaDocenteTest`. | `refactor: extrae AgendaDocente de Docente…` |
| 3 | **Move Method + Guard Clauses** | `ServicioReservas` validaba reglas del dominio: preguntaba `isDisponible()` y luego llamaba a `marcarOcupado()` (pregunta y acción separadas), y comprobaba la disponibilidad del nuevo horario antes de delegar en `Reserva.reprogramar`, de modo que reprogramar por otra vía salteaba la validación. | `Horario.reservar()` valida y ocupa en una operación indivisible; `Reserva.reprogramar()` concentra sus tres validaciones como guard clauses y no muta nada antes de que todas pasen. | `ServicioReservasCaracterizacionTest` (mensajes exactos, sin eventos publicados, reserva intacta) + nuevas pruebas de `Horario.reservar()` y de atomicidad. | `refactor: mueve la regla de disponibilidad al dominio…` |
| 4 | **Decompose Conditional** + Move Method | La nota de cancelación se armaba con un ternario que interrogaba `resultado.getObservacion().isEmpty()` dentro del servicio (*Feature Envy*): una condición sin nombre mezclada con la orquestación. | La regla pasa a `ResultadoCancelacion.componerNota(motivo)`, con la condición nombrada `tieneObservacion()`. | `ServicioReservasCaracterizacionTest` (nota con y sin observación), `ServicioReservasTest` de Ae4, nuevo `ResultadoCancelacionTest`. | `refactor: mueve la composicion de la nota…` |
| 5 | **Agrupar Data Clump** | El trío `(estudiante, docente, horario)` viajaba junto y suelto por toda la cadena; dos de los tres parámetros son `Usuario`, así que invertirlos compilaba sin queja, la validación estaba repetida y un cuarto dato habría cambiado todas las firmas. | Se introduce el record `SolicitudTutoria`, que agrupa y valida los tres datos una sola vez. Las firmas anteriores quedan como sobrecargas que delegan. | Toda la suite de `ServicioReservas` y `ReservaTest` (que siguen usando las firmas de tres argumentos) + `SolicitudTutoriaTest`. | `refactor: agrupa el data clump…` |
| 6 | **Extract Class + Value Object** | `GeneradorReciboReserva` decidía *cuánto se cobra* y además *cómo se ve* el recibo: dos motivos de cambio sin relación en el mismo archivo, y la regla de precio solo podía probarse leyendo el texto impreso. El precio circulaba como un `double` suelto, con su formato escrito a mano. | La regla de cobro se mueve a `TarifarioTutoria`; el importe se representa con el Value Object `Dinero`, que lleva su operación de descuento y su formato. El generador queda como formateador. | `GeneradorReciboReservaTest` (6 recibos carácter por carácter) + `TarifarioTutoriaTest` (cada regla sin pasar por el texto). | `refactor: extrae TarifarioTutoria y el Value Object Dinero…` |

### Evidencia de que el comportamiento se preservó

- La suite pasó de 54 a **80 pruebas** y quedó **verde en los 7
  commits**: 54 → 58 → 62 → 66 → 69 → 74 → 80, sin una sola prueba roja
  y sin modificar ninguna aserción existente para "acomodarla" al nuevo
  diseño.
- El harness manual de Ae4 (`LineaBaseRecibos`) se volvió a ejecutar
  contra el código final: `diff` contra la salida capturada en Ae4 **no
  reporta ninguna diferencia** (`docs/ae5-evidencias/07-linea-base-recibos-ae5.txt`).
- Ninguna refactorización necesitó cambiar `ServicioReservasTest` (la
  suite de Ae4), que actúa como control independiente.

### Comparación final antes/después

| Dimensión | Antes (cierre de Ae4) | Después (Ae5) | Evidencia |
|---|---|---|---|
| Responsabilidades | `Docente` era identidad + agenda; `GeneradorReciboReserva` era tarifa + formato; `ServicioReservas` orquestaba y además validaba reglas del dominio. | Cada una con una sola razón de cambio: `AgendaDocente`, `TarifarioTutoria`, y un servicio reducido a orquestación. | `diff-src-main-ae4-vs-ae5.patch`; `codigo-antes/` vs `codigo-despues/` |
| Cohesión | Reglas de negocio repartidas entre la capa de aplicación y el dominio. | Cada regla vive en el objeto que posee el dato (`Horario.reservar`, `Reserva.reprogramar`, `ResultadoCancelacion.componerNota`). | Refactorizaciones 3 y 4 |
| Acoplamiento | El servicio conocía reglas internas de `Horario` y el formato de la nota; el generador conocía la política de precios. | El servicio depende solo de abstracciones y de operaciones con intención; el generador depende de un tarifario inyectable. | Refactorizaciones 3, 4 y 6 |
| Datos del dominio | `String correo`, `double precio`, tres parámetros sueltos por firma. | Value Objects `Correo`, `Dinero` y el parameter object `SolicitudTutoria`, válidos por construcción. | Refactorizaciones 1, 5 y 6 |
| Condicionales | Ternario de composición de nota en el servicio; pregunta + acción separadas; validaciones intercaladas con mutaciones. | Condiciones con nombre, guard clauses al inicio y ninguna mutación antes de validar. | Refactorizaciones 3 y 4 |
| Pruebas | 10 pruebas JUnit + un harness manual sin JUnit. | 80 pruebas JUnit; las reglas extraídas se prueban de forma aislada, sin montar el servicio ni leer texto formateado. | Logs `01`–`07` en `docs/ae5-evidencias/` |
| Git | 1 commit de línea base + 4 de la kata. | 1 commit de línea base + 1 de red de seguridad + 6 de refactorización, cada uno con su justificación y el estado de la suite. | `git-log-ae5.txt`, `git-log-ae5-detallado.txt` |

### Decisiones deliberadas y sus costos

- **`Dinero` sigue usando `double`.** La expresión aritmética se
  conservó intacta para que el total no cambie ni en el último decimal.
  Migrar a `BigDecimal` es un cambio de comportamiento, no una
  refactorización, y queda como trabajo posterior declarado.
- **Al mover `publicarHorario` se detectó que no valida el nulo.** Se
  dejó tal cual: refactorizar no es corregir comportamiento. Queda
  registrado en el javadoc de `AgendaDocente` como pendiente.
- **Se conservaron las firmas antiguas como sobrecargas** en
  `ServicioReservas` y `Reserva`. El costo es una vía de entrada
  duplicada que habrá que retirar cuando ya no queden clientes viejos;
  el beneficio fue que la suite existente siguió sirviendo como control
  sin tocar una sola aserción.
- **Efecto colateral positivo de la Refactorización 5:** como la
  solicitud se valida antes de tocar el horario, una solicitud
  incompleta ya no deja el bloque ocupado e inutilizable. Está cubierto
  por `unaSolicitudIncompletaNoConsumeElHorario`.

## Pruebas

Para ejecutar las pruebas:

```bash
mvn clean test
```

o, capturando el log como evidencia:

```bash
bash tools/verificar.sh     # deja la salida en docs/ae5-evidencias/mvn-clean-test.txt
```

La suite tiene **80 pruebas JUnit 5** repartidas en 11 clases: el
dominio completo (`Usuario`, `Correo`, `Horario`, `Reserva`, `Docente`,
`AgendaDocente`, `SolicitudTutoria`), las reglas de cobro
(`TarifarioTutoria`, `Dinero`), el generador de recibos carácter por
carácter, la política de cancelación (`ResultadoCancelacion`), el
servicio de reservas (suite de Ae4 más la de caracterización de Ae5) y
el Factory Method de notificadores.

> Nota de verificación: en el entorno donde se prepararon las
> refactorizaciones de Ae5 no hubo acceso de red a Maven Central
> (`repo.maven.apache.org` respondió 403 por política de egreso), por lo
> que no fue posible descargar `junit-jupiter` ni Surefire. Para no
> renunciar al ciclo PRUEBA VERDE → CAMBIO → PRUEBA VERDE, se añadió
> `tools/verificar-offline.sh`: compila **las mismas pruebas JUnit 5 del
> proyecto** contra stubs mínimos de la API (`@Test`, `@BeforeEach`,
> `Assertions`) y las ejecuta con un ejecutor propio por reflexión
> (`tools/verificacion-offline/RunnerOffline.java`), que respeta el
> aislamiento por instancia de JUnit y devuelve código de salida 1 si
> alguna prueba falla. Las pruebas no se modificaron para este ejecutor
> y los stubs viven fuera de `src/`, de modo que `mvn clean test` las
> compila contra el JUnit real. Los logs de las 7 ejecuciones (una por
> commit) están en `docs/ae5-evidencias/`. Se recomienda ejecutar
> `bash tools/verificar.sh` en un entorno con acceso normal a internet
> para obtener el reporte oficial `BUILD SUCCESS`.

## Control de versiones

El proyecto utiliza la rama `main`. El historial de commits documenta la
evolución del análisis, el modelo y la implementación en cada incremento,
incluyendo el ciclo refactorización → compilar → ejecutar → comparar →
commit de la Kata de Ae4 y el ciclo prueba verde → cambio pequeño →
prueba verde → commit de las seis refactorizaciones de Ae5 (ver
[`GUIA_GIT.md`](GUIA_GIT.md) para el detalle de los commits de cada
entrega). Cada commit de Ae5 describe, en su propio mensaje, el problema
de diseño que resuelve, el cambio aplicado, las pruebas que lo protegen
y el estado de la suite tras el cambio.

## Evidencias

- Diagrama UML de clases (Ae1): [`docs/modelo-clases.svg`](docs/modelo-clases.svg) / [`docs/modelo-clases.png`](docs/modelo-clases.png).
- Diagrama UML actualizado (Ae3 — Incremento 1): [`docs/uml-incremento1.svg`](docs/uml-incremento1.svg) / [`docs/uml-incremento1.png`](docs/uml-incremento1.png).
- Evidencia de pruebas: salida de `mvn clean test` (BUILD SUCCESS) y verificación manual descrita arriba.
- Evidencia de la Kata de refactorización (Ae4): [`docs/ae4-evidencias/`](docs/ae4-evidencias/) (línea base antes/después y `git log`).
- Evidencia de Ae5: [`docs/ae5-evidencias/`](docs/ae5-evidencias/):
  - `01`–`07`: la suite completa ejecutada sobre **cada commit** del historial (siempre verde).
  - `07-linea-base-recibos-ae5.txt`: el harness de Ae4 re-ejecutado contra el código final, sin diferencias.
  - `codigo-antes/` y `codigo-despues/`: las clases afectadas en su estado inicial y final.
  - `diff-src-main-ae4-vs-ae5.patch` y `diff-resumen-ae4-vs-ae5.txt`: el cambio completo de `src/main`.
  - `git-log-ae5.txt` y `git-log-ae5-detallado.txt`: el historial con los mensajes completos.
  - `parches/`: los 7 commits como parches aplicables con `git am`.
- Reporte técnico de Ae5: [`docs/ae5-reporte-tecnico.docx`](docs/ae5-reporte-tecnico.docx) / [`docs/ae5-reporte-tecnico.pdf`](docs/ae5-reporte-tecnico.pdf).
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

En Ae5 utilicé asistencia de IA para: ampliar la suite hasta convertirla
en una red de seguridad de caracterización a partir del comportamiento
que yo definí como el que debía preservarse, redactar las clases y las
pruebas de las seis refactorizaciones que seleccioné, construir el
ejecutor offline que permitió mantener el ciclo de verificación sin
acceso a Maven Central, y redactar la documentación (tabla de
refactorizaciones, comparación antes/después y reporte técnico). La
selección de los problemas de diseño a atacar, el orden de las
refactorizaciones, la decisión de conservar `double` en `Dinero` y la de
no corregir el defecto latente detectado en `publicarHorario` son
decisiones mías, y puedo justificarlas junto con cada línea del código
y de las pruebas entregadas.

## Autor

Luis Saa - saaluis2242@gmail.com
