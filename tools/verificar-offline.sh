#!/usr/bin/env bash
# Verificacion de la suite SIN Maven Central.
#
# Compila src/main y src/test contra stubs minimos de la API de JUnit 5
# (tools/verificacion-offline/stubs) y ejecuta las mismas pruebas JUnit
# del proyecto con un ejecutor propio basado en reflexion.
#
# Uso:  bash tools/verificar-offline.sh
# Salida: 0 si la suite queda verde, 1 si alguna prueba falla.
#
# La ejecucion oficial de la suite es `mvn clean test` (ver
# tools/verificar.sh); este script solo existe porque el entorno donde
# se prepararon las refactorizaciones no tiene acceso a repo.maven.apache.org.
set -euo pipefail

RAIZ="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SALIDA="$RAIZ/target-offline"

rm -rf "$SALIDA"
mkdir -p "$SALIDA/clases"

mapfile -t FUENTES < <(find "$RAIZ/src/main/java" "$RAIZ/src/test/java" \
                            "$RAIZ/tools/verificacion-offline" -name '*.java')

javac -nowarn -encoding UTF-8 -d "$SALIDA/clases" "${FUENTES[@]}"

CLASES_DE_PRUEBA=$(cd "$RAIZ/src/test/java" && find . -name '*Test.java' \
    | sed 's|^\./||; s|\.java$||; s|/|.|g' | sort)

# shellcheck disable=SC2086
java -cp "$SALIDA/clases" RunnerOffline $CLASES_DE_PRUEBA
