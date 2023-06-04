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
		byte[] buffer = new byte[256];
		int offset = 0;
		int count;
		while ((count = in.read(buffer, offset, buffer.length - offset)) != -1) {
			offset += count;
			if (offset >= buffer.length) {
				throw new IOException("buffer overflow");
			}
		}
		return new String(buffer, 0, offset, StandardCharsets.UTF_8).trim();
	}
}