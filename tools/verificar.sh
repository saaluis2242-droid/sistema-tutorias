#!/usr/bin/env bash
# Verificacion oficial de la suite: requiere acceso a Maven Central.
#
# Uso:  bash tools/verificar.sh
#
# Guarda la salida completa en docs/ae5-evidencias/mvn-clean-test.txt para
# adjuntarla como evidencia de la entrega.
set -euo pipefail

RAIZ="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
mkdir -p "$RAIZ/docs/ae5-evidencias"

cd "$RAIZ"
mvn -B clean test | tee "docs/ae5-evidencias/mvn-clean-test.txt"
