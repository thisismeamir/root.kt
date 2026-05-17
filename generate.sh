#!/usr/bin/env bash
# generate.sh — generates ROOT test fixtures and distributes them to all modules
# Usage: ./generate.sh [path/to/root/binary]
# Default assumes `root` is on PATH

set -e

ROOT_BIN="${1:-root}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
TMP_OUT="$SCRIPT_DIR/.fixtures_tmp"

mkdir -p "$TMP_OUT"

echo "Generating fixtures..."
"$ROOT_BIN" -l -b -q "$SCRIPT_DIR/generate_all.C(\"$TMP_OUT\")"

# List of test resource directories — fill in the missing paths
TARGETS=(
    "$SCRIPT_DIR/rootkt-format/src/test/resources"
    "$SCRIPT_DIR/rootkt-compression/src/test/resources"
    "$SCRIPT_DIR/rootkt-streamer/src/test/resources"
    # "$SCRIPT_DIR/rootkt-model/src/test/resources"
    # "$SCRIPT_DIR/rootkt-tree/src/test/resources"
    # "$SCRIPT_DIR/rootkt-io/src/test/resources"
    # "$SCRIPT_DIR/rootkt-runtime/src/test/resources"
)

for TARGET in "${TARGETS[@]}"; do
    mkdir -p "$TARGET"
    cp "$TMP_OUT"/*.root "$TARGET/"
    echo "Copied to $TARGET"
done

rm -rf "$TMP_OUT"
echo "Done."