/*
 * SPDX-FileCopyrightText: 2023-2026 Toshiaki Maki <makingx@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package am.ik.json;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
	void parseFloatWithExponent() {
		final JsonArray array = Objects.requireNonNull(Json.parse("[1.50e3, -5E-1, 2e+2]").asArray());
		assertThat(array.values()).allMatch(JsonNode::isFloat);
		assertThat(Json.stringify(array)).isEqualTo("[1500.0,-0.5,200.0]");
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
	void parseStringEscapes() {
		final JsonNode node = Json.parse("\"\\u00e9\\u00C9 \\ud83d\\ude00 \\b\\f\"");
		assertThat(node.asString()).isEqualTo("\u00e9\u00c9 \ud83d\ude00 \b\f");
		assertThat(Json.stringify(node)).isEqualTo("\"\u00e9\u00c9 \ud83d\ude00 \\b\\f\"");
	}

	@Test
	void stringifyControlCharacters() {
		final JsonNode node = new JsonNode("\u0000\u0001\u001f\b\t\u007f");
		assertThat(Json.stringify(node)).isEqualTo("\"\\u0000\\u0001\\u001f\\b\\t\u007f\"");
		assertThat(Json.parse(Json.stringify(node)).asString()).isEqualTo(node.asString());
	}

	@Test
	void parseArray() {
		final JsonNode node = Json.parse("[100, \"hello\"]");
		assertThat(node.isArray()).isTrue();
		final JsonArray array = Objects.requireNonNull(node.asArray());
		assertThat(array.size()).isEqualTo(2);
		assertThat(array.get(0).isInteger()).isTrue();
		assertThat(array.get(0).asInteger()).isEqualTo(100);
		assertThat(array.get(1).isString()).isTrue();
		assertThat(array.get(1).asString()).isEqualTo("hello");
		assertThat(Json.stringify(array)).isEqualTo("[100,\"hello\"]");
	}

	@Test
	void parseObject() {
		final JsonNode node = Json.parse(
				"{\"url\":\"/classes\",\"method\":\"POST\",\"headers\":{\"user-agent\":\"curl/7.88.1\",\"host\":\"127.0.0.1:8080\",\"content-length\":\"3\",\"accept\":\"*/*\",\"content-type\":\"application/x-www-form-urlencoded\"},\"body\":\"{\\\"foo\\\":\\\"bar\\\"}\",\"kv\":{},\"params\":{}}");
		assertThat(node.isObject()).isTrue();
		final JsonObject json = Objects.requireNonNull(node.asObject());
		assertThat(json.asMap().keySet()).containsExactlyInAnyOrder("url", "method", "headers", "body", "kv", "params");
		assertThat(json.get("url").asString()).isEqualTo("/classes");
		assertThat(json.get("method").asString()).isEqualTo("POST");
		final JsonObject headers = Objects.requireNonNull(json.get("headers").asObject());
		assertThat(headers.asMap().keySet()).containsExactlyInAnyOrder("user-agent", "host", "content-length", "accept",
				"content-type");
		assertThat(headers.get("user-agent").asString()).isEqualTo("curl/7.88.1");
		assertThat(headers.get("host").asString()).isEqualTo("127.0.0.1:8080");
		assertThat(headers.get("content-length").asString()).isEqualTo("3");
		assertThat(headers.get("accept").asString()).isEqualTo("*/*");
		assertThat(headers.get("content-type").asString()).isEqualTo("application/x-www-form-urlencoded");
		assertThat(json.get("body").asString()).isEqualTo("{\"foo\":\"bar\"}");
		assertThat(Objects.requireNonNull(json.get("params").asObject()).isEmpty()).isTrue();
		assertThat(Objects.requireNonNull(json.get("kv").asObject()).isEmpty()).isTrue();
		assertThat(Json.stringify(json)).isEqualTo(
				"{\"url\":\"\\/classes\",\"method\":\"POST\",\"headers\":{\"user-agent\":\"curl\\/7.88.1\",\"host\":\"127.0.0.1:8080\",\"content-length\":\"3\",\"accept\":\"*\\/*\",\"content-type\":\"application\\/x-www-form-urlencoded\"},\"body\":\"{\\\"foo\\\":\\\"bar\\\"}\",\"kv\":{},\"params\":{}}");
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "[1 2]", "[1,]", "[,1]", "{\"a\":1,}", "{\"a\":1 \"b\":2}", "{} x", "1 2", "01", "-",
			"1.", ".5", "1e", "1.5e+", "+1", "\"\t\"", "\"\\x\"", "\"\\u12\"", "\u000b1", "\u0661" })
	void refuseWhatIsNoJson(String text) {
		assertThatThrownBy(() -> Json.parse(text)).isInstanceOfAny(JsonParseException.class, JsonLexerException.class);
	}

	@Test
	void readme() {
		final JsonObject json = Objects.requireNonNull(Json.parse("{\"foo\": \"bar\"}").asObject());
		assertThat(json.get("foo").asString()).isEqualTo("bar");
		assertThat(Json.stringify(json)).isEqualTo("{\"foo\":\"bar\"}");
	}

}