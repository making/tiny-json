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

import java.util.Optional;

import org.jspecify.annotations.Nullable;

/**
 * A JSON value. The accessors return {@code null} when the value is JSON {@code null}.
 */
public class JsonNode {

	private final @Nullable Object value;

	public JsonNode(@Nullable Object value) {
		this.value = value;
	}

	/**
	 * Returns the underlying Java value, or {@code null} for JSON {@code null}.
	 * @return the underlying value
	 */
	public @Nullable Object value() {
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

	public @Nullable Boolean asBoolean() {
		if (this.value instanceof Boolean booleanValue) {
			return booleanValue;
		}
		return nullOr("boolean");
	}

	public @Nullable String asString() {
		if (this.value instanceof String stringValue) {
			return stringValue;
		}
		return nullOr("string");
	}

	public @Nullable Number asNumber() {
		if (this.value instanceof Number numberValue) {
			return numberValue;
		}
		return nullOr("number");
	}

	public @Nullable Integer asInteger() {
		if (this.value instanceof Integer integerValue) {
			return integerValue;
		}
		return nullOr("integer");
	}

	public @Nullable Float asFloat() {
		if (this.value instanceof Float floatValue) {
			return floatValue;
		}
		return nullOr("float");
	}

	public @Nullable JsonArray asArray() {
		if (this.value instanceof JsonArray arrayValue) {
			return arrayValue;
		}
		return nullOr("array");
	}

	public @Nullable JsonObject asObject() {
		if (this.value instanceof JsonObject objectValue) {
			return objectValue;
		}
		return nullOr("object");
	}

	private <T> @Nullable T nullOr(String expected) {
		if (this.value == null) {
			return null;
		}
		throw new IllegalStateException("Value is not a " + expected);
	}

	@Override
	public String toString() {
		return switch (this.value) {
			case null -> "null";
			case String stringValue -> "\"" + escape(stringValue) + "\"";
			case Object objectValue -> objectValue.toString();
		};
	}

	static String escape(String value) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			final char c = value.charAt(i);
			sb.append(switch (c) {
				case '\f' -> "\\f";
				case '\n' -> "\\n";
				case '\r' -> "\\r";
				case '\t' -> "\\t";
				case '\\', '"', '/' -> "\\" + c;
				default -> String.valueOf(c);
			});
		}
		return sb.toString();
	}

}
