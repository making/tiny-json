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
package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import am.ik.json.Json;
import am.ik.json.JsonObject;

public class Main {
	public static void main(String[] args) throws Exception {
		final JsonObject request = Json.parse(copyToString(System.in)).asObject();
		final JsonObject response = new JsonObject()
				.put("data", "Hello " + request.get("body").asString() + "!")
				.put("status", 200)
				.put("base64", false)
				.put("headers", new JsonObject().put("X-Generated-By", "wasm-workers-server"))
				.put("kv", new JsonObject());
		System.out.println(Json.stringify(response));
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