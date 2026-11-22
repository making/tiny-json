# Hello WASM (Java)

Compiles a tiny-json consumer to WebAssembly with
[rontolisp](https://github.com/making/rontolisp)'s experimental Java front end and runs
the module with [wasmtime](https://wasmtime.dev). It shows that tiny-json stays inside
the subset of Java that the front end compiles and that it works without a JDK.

## Requirements

- Java 21+ and rontolisp (`RONTOLISP_JAR` or `rontolisp` on `PATH`)
- wasmtime 47+

## Run

```bash
./run.sh
```

```bash
$ echo '{"body":"WASM"}' | wasmtime target/main.wasm
{"data":"Hello WASM!","status":200,"base64":false,"headers":{"X-Generated-By":"wasm-workers-server"},"kv":{}}
```
