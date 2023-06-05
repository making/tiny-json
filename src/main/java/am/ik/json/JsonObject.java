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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class JsonObject {
	private final Map<String, JsonNode> properties;

	public JsonObject() {
		this.properties = new LinkedHashMap<>();
	}

	public JsonObject put(String key, JsonNode value) {
		this.properties.put(key, value);
		return this;
	}

	public JsonObject put(String key, JsonArray value) {
		this.properties.put(key, new JsonNode(value));
		return this;
	}

	public JsonObject put(String key, JsonObject value) {
		this.properties.put(key, new JsonNode(value));
		return this;
	}

	public JsonObject put(String key, String value) {
		this.properties.put(key, new JsonNode(value));
		return this;
	}

	public JsonObject put(String key, int value) {
		this.properties.put(key, new JsonNode(value));
		return this;
	}

	public JsonObject put(String key, float value) {
		this.properties.put(key, new JsonNode(value));
		return this;
	}

	public JsonObject put(String key, boolean value) {
		this.properties.put(key, new JsonNode(value));
		return this;
	}

	public JsonNode get(String key) {
		final JsonNode value = this.properties.get(key);
		if (value == null) {
			return new JsonNode(null);
		}
		return value;
	}

	public boolean containsKey(String key) {
		return this.properties.containsKey(key);
	}


	public Set<String> keySet() {
		return this.properties.keySet();
	}

	public int size() {
		return this.properties.size();
	}

	public boolean isEmpty() {
		return this.properties.isEmpty();
	}

	public Map<String, JsonNode> asMap() {
		return Collections.unmodifiableMap(this.properties);
	}

	public JsonNode toNode() {
		return new JsonNode(this);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		final int size = this.properties.size();
		int i = 0;
		for (Entry<String, JsonNode> entry : this.properties.entrySet()) {
			String k = entry.getKey();
			JsonNode v = entry.getValue();
			sb.append("\"").append(k).append("\"").append(":").append(v);
			if (++i != size) {
				sb.append(",");
			}
		}
		sb.replace(sb.length(), sb.length(), "}");
		return sb.toString();
	}
}
