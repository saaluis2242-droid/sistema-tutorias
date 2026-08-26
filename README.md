# Diseño de Software - Proyecto integrador

## Información general

- **Universidad:** Universidad Estudios Espíritu Santo
- **Carrera:** Ciencias de la Computacion
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
negocio independiente de la tecnología de persistencia y del canal de
notificación, para que ambas puedan cambiar sin afectar las reglas del
dominio. Corresponde a la actividad Ae1 (Semana 2, Unidad 1) de Diseño
Orientado a Objetos.

## Objetivos

- Modelar el dominio del sistema de gestión de tutorías aplicando
  abstracción, encapsulación, herencia y polimorfismo.
- Diseñar clases con alta cohesión y bajo acoplamiento, separando la
  lógica de negocio de la persistencia y de la comunicación con los
  usuarios.
- Aplicar y justificar los principios SOLID (SRP, DIP y OCP) sobre
  decisiones concretas del diseño.
- Representar el modelo mediante un diagrama UML de clases coherente con
  la implementación en Java.

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

`Main` ejecuta una demostración del flujo principal (publicar un horario,
solicitar una reserva, confirmarla y reprogramarla) imprimiendo en consola
las notificaciones simuladas y los cambios de estado.

## Estructura del proyecto

```text
src/
├── main/
│   └── java/
│       └── edu/uees/tutorias/
│           ├── domain/        (Usuario, Estudiante, Docente, Horario, Reserva, EstadoReserva)
│           ├── service/       (ServicioReservas)
│           ├── repository/    (RepositorioReservas, RepositorioReservasEnMemoria)
│           ├── notification/  (Notificador, NotificadorEmailConsola, NotificadorSmsConsola)
│           └── app/           (Main)
└── test/
    └── java/
        └── edu/uees/tutorias/ (ServicioReservasTest)
docs/
├── modelo-clases.svg
└── modelo-clases.png
pom.xml
README.md
```

## Funcionalidades

- Publicación de horarios disponibles por parte de un docente, con
  validación de solapamiento.
- Solicitud de una reserva de tutoría por parte de un estudiante.
- Confirmación, cancelación y reprogramación de una reserva, con
  validación de las transiciones de estado permitidas.
- Notificación de eventos relevantes a estudiante y docente a través de
  una abstracción de notificación (con dos canales de ejemplo).
- Persistencia de reservas abstraída mediante una interfaz de
  repositorio (implementación en memoria para esta etapa del proyecto).

## Pruebas

Para ejecutar las pruebas:

```bash
mvn clean test
```

Se incluyen pruebas unitarias de `ServicioReservas` que cubren la
solicitud de una reserva, el rechazo de una reserva sobre un horario ya
ocupado, la confirmación, la cancelación (con liberación del horario) y
la reprogramación.

## Control de versiones

El proyecto utiliza la rama `main`. El historial de commits documenta la
evolución del análisis, el modelo y la implementación (ver
[`GUIA_GIT.md`](GUIA_GIT.md) para el detalle de los commits sugeridos).

## Evidencias

- Diagrama UML de clases: [`docs/modelo-clases.svg`](docs/modelo-clases.svg) / [`docs/modelo-clases.png`](docs/modelo-clases.png).
- Evidencia de pruebas: salida de `mvn clean test` (BUILD SUCCESS, 5 pruebas de `ServicioReservasTest`).
- Documento de análisis y diseño entregado en Blackboard (PDF), con las secciones de análisis del dominio, diseño OO, cohesión/acoplamiento, principios SOLID, UML y conclusiones.

## Uso de inteligencia artificial

Durante el desarrollo de esta actividad utilicé herramientas de
inteligencia artificial. Las utilicé para: apoyar la estructuración del
diseño de clases, redactar una primera versión del código Java y generar
el diagrama UML a partir de las decisiones de diseño que definí.
Verifiqué y adapté las respuestas obtenidas (compilé el proyecto y
ejecuté las pruebas unitarias) y puedo explicar y justificar el código y
las decisiones de diseño presentadas.

## Autor

[NOMBRE Y APELLIDO] - saaluis2242@gmail.com
