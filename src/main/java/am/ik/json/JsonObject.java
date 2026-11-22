/*
 * SPDX-FileCopyrightText: 2023-2026 Toshiaki Maki <makingx@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package am.ik.json;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class JsonObject {

	private final Map<String, JsonNode> properties = new LinkedHashMap<>();

	public JsonObject put(String key, JsonNode value) {
		this.properties.put(key, value);
		return this;
	}

	public JsonObject put(String key, JsonArray value) {
		return put(key, new JsonNode(value));
	}

	public JsonObject put(String key, JsonObject value) {
		return put(key, new JsonNode(value));
	}

	public JsonObject put(String key, String value) {
		return put(key, new JsonNode(value));
	}

	public JsonObject put(String key, int value) {
		return put(key, new JsonNode(value));
	}

	public JsonObject put(String key, long value) {
		return put(key, new JsonNode(value));
	}

	public JsonObject put(String key, BigDecimal value) {
		return put(key, new JsonNode(value));
	}

	public JsonObject put(String key, boolean value) {
		return put(key, new JsonNode(value));
	}

	public JsonNode get(String key) {
		final JsonNode value = this.properties.get(key);
		return value != null ? value : new JsonNode(null);
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
		final StringBuilder sb = new StringBuilder("{");
		int i = 0;
		for (Entry<String, JsonNode> entry : this.properties.entrySet()) {
			if (i++ > 0) {
				sb.append(',');
			}
			sb.append('"').append(JsonNode.escape(entry.getKey())).append("\":").append(entry.getValue());
		}
		return sb.append('}').toString();
	}

}
