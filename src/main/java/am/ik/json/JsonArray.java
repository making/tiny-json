/*
 * SPDX-FileCopyrightText: 2023-2026 Toshiaki Maki <makingx@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package am.ik.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonArray {

	private final List<JsonNode> values = new ArrayList<>();

	public JsonArray add(JsonNode value) {
		this.values.add(value);
		return this;
	}

	public JsonNode get(int index) {
		return this.values.get(index);
	}

	public int size() {
		return this.values.size();
	}

	public List<JsonNode> values() {
		return Collections.unmodifiableList(this.values);
	}

	public JsonNode toNode() {
		return new JsonNode(this);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("[");
		int i = 0;
		for (JsonNode value : this.values) {
			if (i++ > 0) {
				sb.append(',');
			}
			sb.append(value);
		}
		return sb.append(']').toString();
	}

}
