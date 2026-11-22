# tiny json

A tiny JSON parser. It stays within the subset of Java that
[rontolisp](https://github.com/making/rontolisp)'s experimental Java front end compiles,
so the same code runs on a JDK and as WebAssembly without one.

```xml
<dependency>
	<groupId>am.ik.json</groupId>
	<artifactId>tiny-json</artifactId>
	<version>0.3.0</version>
</dependency>
```

```java
JsonObject json = Json.parse("{\"foo\": \"bar\"}").asObject();
json.get("foo").asString(); // bar
Json.stringify(json); // {"foo":"bar"}
```

For an indented document, pass a `JsonWriter`:

```java
JsonObject post = new JsonObject().put("method", "POST").put("path", "/post");
Json.stringify(post, JsonWriter.pretty(2));
// {
//   "method": "POST",
//   "path": "/post"
// }
```

`JsonWriter.pretty(2).inlineArrays()` writes arrays on one line (`[1, 2]`) instead of
one element a line, which suits request and response dumps.

The API is null-marked with [JSpecify](https://jspecify.dev): accessors such as
`asString()` return `@Nullable String`, and the build enforces it with NullAway.

## Requirements

* Java 21.0.8+ or 22+ (NullAway's JSpecify mode reads type annotations)

## Build

```bash
./mvnw verify
```

`integration-tests/hello-wasm` compiles a consumer to WebAssembly; see its README.

## License

Licensed under the Apache License, Version 2.0.
