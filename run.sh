#!/bin/bash
# ─────────────────────────────────────────────────────────────
# Script de compilation et lancement du jeu Gaufre Empoisonnée
# ─────────────────────────────────────────────────────────────

SRC="src"
OUT="bin"
MAIN="Main"

mkdir -p "$OUT"

echo "==> Compilation..."
find "$SRC" -name "*.java" | xargs javac -d "$OUT" -sourcepath "$SRC"

if [ $? -ne 0 ]; then
    echo "Erreur de compilation."
    exit 1
fi

echo "==> Lancement du jeu..."
java -cp "$OUT" "$MAIN"
