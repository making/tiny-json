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
import java.util.Objects;

public class JsonObject {
	private final Map<String, JsonNode> properties;

	public JsonObject() {
		this.properties = new LinkedHashMap<>();
	}

	public void put(String key, JsonNode value) {
		this.properties.put(key, value);
	}

	public JsonNode get(String key) {
		return this.properties.get(key);
	}

	public boolean containsKey(String key) {
		return this.properties.containsKey(key);
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

	@Override
	public String toString() {
		return Objects.toString(this.properties);
	}
}
