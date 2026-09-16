# Guía para subir el proyecto a GitHub con historial progresivo

Esta guía es solo para ti (no forma parte de la entrega); te sirve como
checklist para no olvidarte de ningún paso. Sigue la misma convención de
la Guía institucional (Guía 3 y 4 del PDF de entorno de desarrollo).

## 1. Configuración inicial de Git (si no la tienes ya)

```bash
git config --global user.name "Tu Nombre Apellido"
git config --global user.email "saaluis2242@gmail.com"
git config --global init.defaultBranch main
```

## 2. Inicializar el repositorio local

Desde la carpeta `sistema-tutorias/` (asegúrate de que `.gitignore` ya
está creado antes del primer commit):

```bash
git init
git status
```

## 3. Commits sugeridos (evidencia de proceso)

La rúbrica pide ver evolución, no un solo commit al final. Agrega los
archivos por bloques temáticos, en este orden, usando la convención
`tipo: descripción` (feat, fix, test, refactor, docs, chore):

```bash
# 1) Estructura base del proyecto
git add pom.xml .gitignore
git commit -m "chore: crear estructura inicial del proyecto Maven"

# 2) Clases del dominio
git add src/main/java/edu/uees/tutorias/domain/Usuario.java src/main/java/edu/uees/tutorias/domain/Estudiante.java src/main/java/edu/uees/tutorias/domain/Docente.java src/main/java/edu/uees/tutorias/domain/Horario.java
git commit -m "feat: crear clases iniciales del dominio (Usuario, Estudiante, Docente, Horario)"

git add src/main/java/edu/uees/tutorias/domain/Reserva.java src/main/java/edu/uees/tutorias/domain/EstadoReserva.java
git commit -m "feat: implementar modelo de reservas y estados"

# 3) Abstracciones de persistencia y notificación
git add src/main/java/edu/uees/tutorias/repository/
git commit -m "feat: agregar repositorio de reservas (interfaz + implementacion en memoria)"

git add src/main/java/edu/uees/tutorias/notification/
git commit -m "refactor: separar servicio de notificaciones en su propia abstraccion"

# 4) Servicio de aplicación
git add src/main/java/edu/uees/tutorias/service/
git commit -m "feat: implementar ServicioReservas orquestando el caso de uso"

# 5) Demo y pruebas
git add src/main/java/edu/uees/tutorias/app/
git commit -m "feat: agregar clase de demostracion del flujo principal"

git add src/test/
git commit -m "test: agregar pruebas unitarias de ServicioReservas"

# 6) Documentación y UML
git add docs/
git commit -m "docs: agregar diagrama UML de clases"

git add README.md GUIA_GIT.md
git commit -m "docs: completar documentacion inicial"
```

Ajusta los mensajes o el orden si prefieres reflejar tu propio proceso
real de trabajo; lo importante es que el historial no sea un único
commit.

## 4. Conectar con tu repositorio existente y subir

Como ya tienes el repositorio creado en GitHub, solo debes conectarlo:

```bash
git branch -M main
git remote add origin https://github.com/saaluis2242-droid/sistema-tutorias.git
git remote -v
git push -u origin main
```

Si el repositorio remoto ya tiene commits (por ejemplo, lo inicializaste
con un README en GitHub), primero trae los cambios para evitar un
rechazo por `non-fast-forward`:

```bash
git pull --rebase origin main
git push -u origin main
```

## 5. Revisar el historial

```bash
git log --oneline --decorate --graph
```

## 6. Commits sugeridos para el Incremento 1 (Ae3)

Como el repositorio ya existe y tiene el historial de Ae1, para Ae3 no
vuelvas a hacer `git init`: solo agrega los cambios nuevos en bloques,
igual que antes. Antes de empezar, ejecuta la línea base para dejar
registrado que partiste del código de Ae1:

```bash
git pull origin main
mvn clean compile
```

Luego, en este orden:

```bash
# 1) Ajuste de dominio para soportar el canal de notificacion preferido
git add src/main/java/edu/uees/tutorias/domain/CanalNotificacion.java src/main/java/edu/uees/tutorias/domain/Usuario.java
git commit -m "refactor: agregar canal de notificacion preferido a Usuario"

# 2) Factory Method recuperado de Ae2 e integrado al dominio real
git add src/main/java/edu/uees/tutorias/notification/NotificadorWhatsAppConsola.java src/main/java/edu/uees/tutorias/notification/factory/
git commit -m "feat: integrar Factory Method de notificadores (recuperado de Ae2)"

# 3) Strategy: politica de cancelacion
git add src/main/java/edu/uees/tutorias/service/cancelacion/
git commit -m "feat: aplicar strategy a la politica de cancelacion"

# 4) Observer: reacciones a cambios de estado de una reserva
git add src/main/java/edu/uees/tutorias/notification/observer/
git commit -m "feat: integrar observer para reaccionar a cambios de estado de una reserva"

# 5) ServicioReservas evoluciona para usar Strategy y Observer
git add src/main/java/edu/uees/tutorias/service/ServicioReservas.java src/main/java/edu/uees/tutorias/app/Main.java
git commit -m "refactor: mejorar responsabilidades de ServicioReservas con strategy y observer"

# 6) Pruebas actualizadas y nuevas
git add src/test/
git commit -m "test: actualizar pruebas de ServicioReservas y agregar pruebas de factory method"

# 7) UML actualizado del incremento
git add docs/uml-incremento1.svg docs/uml-incremento1.png gen_uml_incremento1.py
git commit -m "docs: actualizar UML del incremento 1"

# 8) Documentacion
git add README.md GUIA_GIT.md
git commit -m "docs: actualizar README y decisiones de diseño del incremento 1"
```

```bash
git push origin main
```

## 7. Commits del ciclo de refactorización para la Kata (Ae4)

Igual que en el Incremento 1, no vuelvas a hacer `git init`. La Kata de
Ae4 sigue el ciclo obligatorio: **refactorización → compilar → ejecutar →
comparar → commit**, uno por cada técnica aplicada. Antes de empezar:

```bash
git pull origin main
mvn clean compile
```

Luego, en este orden (cada commit corresponde a un paso ya compilado y
verificado con `LineaBaseRecibos`, no a un cambio sin probar):

```bash
# 0) Linea base: el codigo con Code Smells, tal como se escribio la primera vez
git add src/main/java/edu/uees/tutorias/reporte/Rpt.java src/test/java/edu/uees/tutorias/reporte/LineaBaseRecibos.java docs/ae4-evidencias/linea-base-antes.txt
git commit -m "chore: registrar linea base de Ae4 (Rpt.proc genera recibo con 6 casos representativos)"

# 1) Refactorizacion 1: Rename
git add src/main/java/edu/uees/tutorias/reporte/GeneradorReciboReserva.java src/test/java/edu/uees/tutorias/reporte/LineaBaseRecibos.java
git rm src/main/java/edu/uees/tutorias/reporte/Rpt.java
git commit -m "refactor: renombrar Rpt a GeneradorReciboReserva y clarificar nombres (proc->generar, e/d/p/s/flag1)"

# 2) Refactorizacion 2: Extract Method
git add src/main/java/edu/uees/tutorias/reporte/GeneradorReciboReserva.java
git commit -m "refactor: extraer calcularPrecio, construirEncabezado y construirLineaEstado de generar()"

# 3) Refactorizacion 3: Replace Magic Number with Constant
git add src/main/java/edu/uees/tutorias/reporte/GeneradorReciboReserva.java
git commit -m "refactor: reemplazar numeros magicos (15.0, 0.1, 0.2, \"UEES\") por constantes con nombre"

# 4) Refactorizacion 4: Simplify Conditional
git add src/main/java/edu/uees/tutorias/reporte/GeneradorReciboReserva.java docs/ae4-evidencias/linea-despues.txt
git commit -m "refactor: simplificar condicionales usando switch sobre el enum EstadoReserva (sin comparar Strings)"

# 5) Documentacion del resultado
git add README.md GUIA_GIT.md docs/ae4-evidencias/
git commit -m "docs: documentar resultado de la kata de refactorizacion (Ae4)"
```

```bash
git push origin main
```

Verifica el historial antes de subir:

```bash
git log --oneline --decorate --graph
```

## 8. Antes de entregar

- Abre la URL del repositorio en una ventana privada (sin sesión
  iniciada) y confirma que se puede ver sin permisos especiales, o
  compártelo con el docente vía Settings → Collaborators si lo dejaste
  privado.
- Verifica que aparezcan `pom.xml`, `src/`, `docs/`, `README.md` y
  `.gitignore`.
- Confirma que el README se renderiza correctamente debajo de la lista
  de archivos en GitHub.
- Verifica que el enlace correcto esté copiado en el PDF y en el README
  (reemplaza `USUARIO/REPOSITORIO` en la sección "Instalación").
- No incluyas contraseñas, tokens ni claves en ningún archivo ni
  captura.
