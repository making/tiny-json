#!/bin/sh
# Compiles the Java program with rontolisp's experimental Java front end and runs the
# resulting WASM module with wasmtime. tiny-json is read from this checkout, so the test
# fails when the library stops fitting that front end.
set -eu

DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
ROOT=$(CDPATH= cd -- "$DIR/../.." && pwd)
OUTPUT="$DIR/target/main.wasm"

if [ -z "${RONTOLISP_JAR:-}" ] && ! command -v rontolisp >/dev/null 2>&1; then
	# CI checks rontolisp out at ./rontolisp; a local checkout is a sibling.
	for candidate in "$ROOT"/rontolisp/target/rontolisp-*-exec.jar "$ROOT"/../rontolisp/target/rontolisp-*-exec.jar; do
		if [ -f "$candidate" ]; then
			RONTOLISP_JAR=$candidate
			break
		fi
	done
fi

if [ -n "${RONTOLISP_JAR:-}" ]; then
	run_rontolisp() { java -jar "$RONTOLISP_JAR" "$@"; }
elif command -v rontolisp >/dev/null 2>&1; then
	run_rontolisp() { rontolisp "$@"; }
else
	echo "rontolisp not found: set RONTOLISP_JAR or put rontolisp on PATH" >&2
	exit 1
fi

if ! command -v wasmtime >/dev/null 2>&1; then
	echo "wasmtime not found: install it from https://wasmtime.dev" >&2
	exit 1
fi

mkdir -p "$DIR/target"
run_rontolisp "$DIR/src/main/java/com/example/Main.java" --java-source-path "$ROOT/src/main/java" -o "$OUTPUT"

RESPONSE=$(echo '{"body":"WASM"}' | wasmtime "$OUTPUT")
EXPECTED='{"data":"Hello WASM!","status":200,"base64":false,"headers":{"X-Generated-By":"wasm-workers-server"},"kv":{}}'
if [ "$RESPONSE" = "$EXPECTED" ]; then
	echo "SUCCESS: $RESPONSE"
	exit 0
fi
echo "FAILURE: $RESPONSE" >&2
exit 1
