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

class JsonParserTest {

	@Test
	void parseInteger() {
		final JsonNode node = Json.parse("100");
		assertThat(node.isInteger()).isTrue();
		assertThat(node.asInteger()).isEqualTo(100);
		assertThat(Json.stringify(node)).isEqualTo("100");
	}

	@Test
	void parseFloat() {
		final JsonNode node = Json.parse("10.2");
		assertThat(node.isFloat()).isTrue();
		assertThat(node.asFloat()).isEqualTo(10.2f);
		assertThat(Json.stringify(node)).isEqualTo("10.2");
	}

	@Test
	void parseBoolean() {
		final JsonNode node = Json.parse("true");
		assertThat(node.isBoolean()).isTrue();
		assertThat(node.asBoolean()).isEqualTo(true);
		assertThat(Json.stringify(node)).isEqualTo("true");
	}

	@Test
	void parseString() {
		final JsonNode node = Json.parse("\"hello\"");
		assertThat(node.isString()).isTrue();
		assertThat(node.asString()).isEqualTo("hello");
		assertThat(Json.stringify(node)).isEqualTo("\"hello\"");
	}

	@Test
	void parseStringDoubleQuote() {
		final JsonNode node = Json.parse("\"\\\"hello\\\"\"");
		assertThat(node.isString()).isTrue();
		assertThat(node.asString()).isEqualTo("\"hello\"");
		assertThat(Json.stringify(node)).isEqualTo("\"\\\"hello\\\"\"");
	}

	@Test
	void parseStringTab() {
		final JsonNode node = Json.parse("\"hello\\tworld\"");
		assertThat(node.isString()).isTrue();
		assertThat(node.asString()).isEqualTo("hello\tworld");
		assertThat(Json.stringify(node)).isEqualTo("\"hello\\tworld\"");
	}

	@Test
	void parseStringLineBreak() {
		final JsonNode node = Json.parse("\"hello\\nworld\"");
		assertThat(node.isString()).isTrue();
		assertThat(node.asString()).isEqualTo("hello\nworld");
		assertThat(Json.stringify(node)).isEqualTo("\"hello\\nworld\"");
	}

	@Test
	void parseArray() {
		final JsonNode node = Json.parse("[100, \"hello\"]");
		assertThat(node.isArray()).isTrue();
		final JsonArray array = node.asArray();
		assertThat(array.size()).isEqualTo(2);
		assertThat(array.get(0).isInteger()).isTrue();
		assertThat(array.get(0).asInteger()).isEqualTo(100);
		assertThat(array.get(1).isString()).isTrue();
		assertThat(array.get(1).asString()).isEqualTo("hello");
		assertThat(Json.stringify(array)).isEqualTo("[100, \"hello\"]");
	}

	@Test
	void parseObject() {
		final JsonNode node = Json.parse("{\"url\":\"/classes\",\"method\":\"POST\",\"headers\":{\"user-agent\":\"curl/7.88.1\",\"host\":\"127.0.0.1:8080\",\"content-length\":\"3\",\"accept\":\"*/*\",\"content-type\":\"application/x-www-form-urlencoded\"},\"body\":\"{\\\"foo\\\":\\\"bar\\\"}\",\"kv\":{},\"params\":{}}");
		assertThat(node.isObject()).isTrue();
		final JsonObject json = node.asObject();
		assertThat(json.asMap().keySet()).containsExactlyInAnyOrder("url", "method", "headers", "body", "kv", "params");
		assertThat(json.get("url").asString()).isEqualTo("/classes");
		assertThat(json.get("method").asString()).isEqualTo("POST");
		final JsonObject headers = json.get("headers").asObject();
		assertThat(headers.asMap().keySet()).containsExactlyInAnyOrder("user-agent", "host", "content-length", "accept", "content-type");
		assertThat(headers.get("user-agent").asString()).isEqualTo("curl/7.88.1");
		assertThat(headers.get("host").asString()).isEqualTo("127.0.0.1:8080");
		assertThat(headers.get("content-length").asString()).isEqualTo("3");
		assertThat(headers.get("accept").asString()).isEqualTo("*/*");
		assertThat(headers.get("content-type").asString()).isEqualTo("application/x-www-form-urlencoded");
		assertThat(json.get("body").asString()).isEqualTo("{\"foo\":\"bar\"}");
		assertThat(json.get("params").asObject().isEmpty()).isTrue();
		assertThat(json.get("kv").asObject().isEmpty()).isTrue();
		assertThat(Json.stringify(json)).isEqualTo("{\"url\":\"\\/classes\",\"method\":\"POST\",\"headers\":{\"user-agent\":\"curl\\/7.88.1\",\"host\":\"127.0.0.1:8080\",\"content-length\":\"3\",\"accept\":\"*\\/*\",\"content-type\":\"application\\/x-www-form-urlencoded\"},\"body\":\"{\\\"foo\\\":\\\"bar\\\"}\",\"kv\":{},\"params\":{}}");
	}
}