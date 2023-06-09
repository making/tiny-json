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

import java.util.Objects;
import java.util.Optional;

public class JsonNode {
	private final Object value;

	public JsonNode(Object value) {
		this.value = value;
	}

	public Object value() {
		return this.value;
	}

	public boolean isNull() {
		return this.value == null;
	}

	public Optional<Object> optional() {
		return Optional.ofNullable(this.value);
	}

	public boolean isBoolean() {
		return this.value instanceof Boolean;
	}

	public boolean isString() {
		return this.value instanceof String;
	}

	public boolean isNumber() {
		return this.value instanceof Integer || this.value instanceof Float;
	}

	public boolean isInteger() {
		return this.value instanceof Integer;
	}

	public boolean isFloat() {
		return this.value instanceof Float;
	}

	public boolean isArray() {
		return this.value instanceof JsonArray;
	}

	public boolean isObject() {
		return this.value instanceof JsonObject;
	}

	public Boolean asBoolean() {
		if (this.value == null) {
			return null;
		}
		if (isBoolean()) {
			return (Boolean) this.value;
		}
		throw new IllegalStateException("Value is not a boolean");
	}

	public String asString() {
		if (this.value == null) {
			return null;
		}
		if (isString()) {
			return (String) this.value;
		}
		throw new IllegalStateException("Value is not a string");
	}

	public Number asNumber() {
		if (this.value == null) {
			return null;
		}
		if (isNumber()) {
			return (Number) this.value;
		}
		throw new IllegalStateException("Value is not a number");
	}

	public Integer asInteger() {
		if (this.value == null) {
			return null;
		}
		if (isInteger()) {
			return (Integer) this.value;
		}
		throw new IllegalStateException("Value is not an integer");
	}

	public Float asFloat() {
		if (this.value == null) {
			return null;
		}
		if (isFloat()) {
			return (Float) this.value;
		}
		throw new IllegalStateException("Value is not a float");
	}

	public JsonArray asArray() {
		if (this.value == null) {
			return null;
		}
		if (isArray()) {
			return (JsonArray) this.value;
		}
		throw new IllegalStateException("Value is not an array");
	}

	public JsonObject asObject() {
		if (this.value == null) {
			return null;
		}
		if (isObject()) {
			return (JsonObject) this.value;
		}
		throw new IllegalStateException("Value is not an object");
	}

	@Override
	public String toString() {
		if (this.value == null) {
			return "null";
		}
		if (isString()) {
			return "\"" + stringify((String) this.value) + "\"";
		}
		return Objects.toString(this.value);
	}

	private static String stringify(String value) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			final char c = value.charAt(i);
			switch (c) {
				case '\f':
					sb.append("\\f");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\t':
					sb.append("\\t");
					break;
				case '\\':
				case '"':
				case '/':
					sb.append('\\').append(c);
					break;
				default:
					sb.append(c);
			}
		}
		return sb.toString();
	}
}
