/*
 * SPDX-FileCopyrightText: 2023-2026 Toshiaki Maki <makingx@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import am.ik.json.Json;
import am.ik.json.JsonArray;
import am.ik.json.JsonNode;
import am.ik.json.JsonObject;
import am.ik.json.JsonWriter;

public class Main {
	public static void main(String[] args) throws Exception {
		final JsonObject request = Json.parse(copyToString(System.in)).asObject();
		final JsonObject response = new JsonObject()
				.put("data", "Hello " + request.get("body").asString() + "!")
				.put("status", 200)
				.put("base64", false)
				.put("headers", new JsonObject().put("X-Generated-By", "wasm-workers-server"))
				.put("kv", new JsonObject())
				.put("tags", new JsonArray().add(new JsonNode("wasm")).add(new JsonNode("json")));
		System.out.println(Json.stringify(response, JsonWriter.pretty(2).inlineArrays()));
	}

	private static String copyToString(InputStream in) throws IOException {
		if (in == null) {
			return "";
		}
		final StringBuilder out = new StringBuilder();
		final InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
		final char[] buffer = new char[256];
		int charsRead;
		while ((charsRead = reader.read(buffer)) != -1) {
			out.append(buffer, 0, charsRead);
		}
		return out.toString();
	}
}