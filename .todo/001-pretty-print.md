# Pretty print

Difficulty: Medium

Add an indented writer to tiny-json so consumers such as rontolisp's httpbin example
can drop their own layout code.

- API: a `JsonWriter` settings object with `pretty(int indent)` and `inlineArrays()`,
  passed to `Json.stringify(node, writer)`.
- Objects one member a line, arrays either multiline or on one line.
- Keep `Json.stringify` / `toString` output unchanged.
- Compile the new code through rontolisp's Java front end (integration-tests/hello-wasm).
- Extend existing tests, update README and the hello-wasm example.
