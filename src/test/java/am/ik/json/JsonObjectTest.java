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
	void nullSafe() {
		final JsonObject json = new JsonObject();
		final JsonNode foo = json.get("foo");
		assertThat(foo).isNotNull();
		assertThat(foo.isNull()).isTrue();
	}

}