# tiny json

a tiny json parser that works with [teavm-wasi](https://github.com/fermyon/teavm-wasi)

```xml
<depdency>
	<groupId>am.ik.json</groupId>
	<artifactId>tiny-json</artifactId>
	<version>0.1.2</version>
</depdency>
```


```java
JsonObject json = Json.parse("{\"foo\": \"bar\"}").asObject();
json.get("foo").asString(); // bar
Json.stringify(json); // {"foo":"bar"}
```

### Required

* Java 8+

### License

Licensed under the Apache License, Version 2.0.