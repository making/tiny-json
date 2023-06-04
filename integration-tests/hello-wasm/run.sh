#!/bin/sh
DIR=$(dirname $0)
set -e
mvn clean package -f ${DIR} --no-transfer-progress
RESPONSE=$(echo '{"body":"WASM"}' | wasmtime ${DIR}/target/generated/wasm/teavm-wasm/classes.wasm)
if [ "${RESPONSE}" = '{"data":"Hello WASM!","status":200,"base64":false,"headers":{"X-Generated-By":"wasm-workers-server"},"kv":{}}' ]; then
  echo "SUCCESS: ${RESPONSE}"
  exit 0
else
  echo "FAILURE: ${RESPONSE}"
  exit 1
fi
