/*
 * SPDX-FileCopyrightText: 2023-2026 Toshiaki Maki <makingx@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package am.ik.json;

import org.jspecify.annotations.Nullable;

/**
 * Entry point for parsing and serializing JSON documents.
 */
public final class Json {

	private Json() {
	}

	public static JsonNode parse(String json) {
		return new JsonParser(new JsonLexer(json)).parse();
	}

	public static String stringify(@Nullable JsonNode json) {
		return json == null ? "null" : json.toString();
	}

	public static String stringify(@Nullable JsonArray json) {
		return json == null ? "null" : json.toString();
	}

	public static String stringify(@Nullable JsonObject json) {
		return json == null ? "null" : json.toString();
	}

}
