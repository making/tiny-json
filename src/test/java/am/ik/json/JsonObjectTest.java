/*
 * Copyright (C) 2023 Toshiaki Maki <makingx@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package am.ik.json;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonObjectTest {

	@Test
	void testToString() {
		final JsonObject json = new JsonObject()
				.put("data", "Hello World!")
				.put("status", 200)
				.put("base64", false)
				.put("headers", new JsonObject().put("X-Generated-By", "wasm-workers-server"))
				.put("kv", new JsonObject());
		assertThat(json.toString()).isEqualTo("{\"data\":\"Hello World!\",\"status\":200,\"base64\":false,\"headers\":{\"X-Generated-By\":\"wasm-workers-server\"},\"kv\":{}}");
	}
}