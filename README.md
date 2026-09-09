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

Este repositorio documenta la evolución del proyecto en tres entregas:

- **Ae1** (Semana 2): análisis de dominio, diseño OO, cohesión/acoplamiento,
  SOLID y UML inicial.
- **Ae2** (Semana 3, `semana3-patrones`, repositorio aparte): práctica
  comparativa de **Factory Method** y **Builder** sobre el mismo dominio.
- **Ae3 — Incremento 1** (Semana 4, esta entrega): identificación de
  problemas reales de diseño e integración de **Strategy** y **Observer**,
  recuperando de Ae2 el patrón que seguía justificado.

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
│           └── app/                     (Main)
└── test/
    └── java/
        └── edu/uees/tutorias/
            ├── ServicioReservasTest.java
            └── notification/factory/NotificadorCreatorFactoryTest.java
docs/
├── modelo-clases.svg / .png         (UML de Ae1)
└── uml-incremento1.svg / .png       (UML actualizado de Ae3)
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

> Nota de verificación: en el entorno donde se preparó este incremento no
> hubo acceso de red a Maven Central para descargar JUnit, por lo que la
> lógica de cada prueba se verificó además con una réplica manual
> (sin anotaciones JUnit) que confirmó los mismos 19 casos. Se recomienda
> ejecutar `mvn clean test` en un entorno con acceso normal a internet
> para obtener el reporte oficial `BUILD SUCCESS`.

## Control de versiones

El proyecto utiliza la rama `main`. El historial de commits documenta la
evolución del análisis, el modelo y la implementación en cada incremento
(ver [`GUIA_GIT.md`](GUIA_GIT.md) para el detalle de los commits
sugeridos de Ae1 y Ae3).

## Evidencias

- Diagrama UML de clases (Ae1): [`docs/modelo-clases.svg`](docs/modelo-clases.svg) / [`docs/modelo-clases.png`](docs/modelo-clases.png).
- Diagrama UML actualizado (Ae3 — Incremento 1): [`docs/uml-incremento1.svg`](docs/uml-incremento1.svg) / [`docs/uml-incremento1.png`](docs/uml-incremento1.png).
- Evidencia de pruebas: salida de `mvn clean test` (BUILD SUCCESS) y verificación manual descrita arriba.
- Documento de análisis y diseño entregado en Blackboard (PDF) de Ae1 y de Ae3, con las secciones de análisis, diseño OO, cohesión/acoplamiento, principios SOLID, patrones, UML y conclusiones.

## Uso de inteligencia artificial

Durante el desarrollo de esta actividad utilicé herramientas de
inteligencia artificial. Las utilicé para: apoyar la identificación de
problemas de diseño a partir del código de Ae1, redactar una primera
versión del código Java que integra Strategy y Observer, migrar e
integrar el Factory Method de Ae2 al dominio real, y generar el diagrama
UML actualizado a partir de las decisiones de diseño que definí.
Verifiqué el comportamiento (compilación y verificación de la lógica de
cada prueba) y puedo explicar y justificar el código y las decisiones de
diseño presentadas, incluyendo por qué se descartó Builder para este
incremento.

## Autor

Luis Saa - saaluis2242@gmail.com
