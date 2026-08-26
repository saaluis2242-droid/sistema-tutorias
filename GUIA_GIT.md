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

## 6. Antes de entregar

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
