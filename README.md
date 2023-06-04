# tiny json

World's Smallest JSON Parser in Java

```xml
<depdency>
	<groupId>am.ik.json</groupId>
	<artifactId>tiny-json</artifactId>
	<version>0.1.1</version>
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