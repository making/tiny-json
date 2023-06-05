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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JsonArray {
	private final List<JsonNode> values;

	public JsonArray() {
		this.values = new ArrayList<>();
	}

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
		return Objects.toString(this.values);
	}
}
