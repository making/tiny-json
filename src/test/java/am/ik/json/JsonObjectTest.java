/*
 * SPDX-FileCopyrightText: 2023-2026 Toshiaki Maki <makingx@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package am.ik.json;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonObjectTest {

	@Test
	void testToString() {
		final JsonObject json = new JsonObject().put("data", "Hello World!")
			.put("status", 200)
			.put("base64", false)
			.put("headers", new JsonObject().put("X-Generated-By", "wasm-workers-server"))
			.put("kv", new JsonObject());
		assertThat(json.toString()).isEqualTo(
				"{\"data\":\"Hello World!\",\"status\":200,\"base64\":false,\"headers\":{\"X-Generated-By\":\"wasm-workers-server\"},\"kv\":{}}");
		assertThat(new JsonObject().put("a\"b\n", 1).toString()).isEqualTo("{\"a\\\"b\\n\":1}");
	}

	@Test
	void testToStringPretty() {
		final JsonObject json = new JsonObject().put("data", "Hello World!")
			.put("status", 200)
			.put("base64", false)
			.put("headers", new JsonObject().put("X-Generated-By", "wasm-workers-server"))
			.put("kv", new JsonObject());
		assertThat(Json.stringify(json, JsonWriter.pretty(2).inlineArrays())).isEqualTo("""
				{
				  "data": "Hello World!",
				  "status": 200,
				  "base64": false,
				  "headers": {
				    "X-Generated-By": "wasm-workers-server"
				  },
				  "kv": {}
				}""");
		final JsonObject arrays = new JsonObject()
			.put("list", new JsonArray().add(new JsonNode(1)).add(new JsonNode(2)))
			.put("empty", new JsonArray())
			.put("objects", new JsonArray().add(new JsonObject().put("a", 1).toNode()));
		assertThat(Json.stringify(arrays, JsonWriter.pretty(2))).isEqualTo("""
				{
				  "list": [
				    1,
				    2
				  ],
				  "empty": [],
				  "objects": [
				    {
				      "a": 1
				    }
				  ]
				}""");
		assertThat(Json.stringify(arrays, JsonWriter.pretty(2).inlineArrays())).isEqualTo("""
				{
				  "list": [1, 2],
				  "empty": [],
				  "objects": [{"a":1}]
				}""");
		assertThat(Json.stringify(new JsonObject().put("a\"b\n", 1), JsonWriter.pretty())).isEqualTo("""
				{
				  "a\\"b\\n": 1
				}""");
		assertThat(Json.stringify((JsonNode) null, JsonWriter.pretty())).isEqualTo("null");
	}

	@Test
	void nullSafe() {
		final JsonObject json = new JsonObject();
		final JsonNode foo = json.get("foo");
		assertThat(foo).isNotNull();
		assertThat(foo.isNull()).isTrue();
	}

}