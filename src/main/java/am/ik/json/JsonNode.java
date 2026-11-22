/*
 * SPDX-FileCopyrightText: 2023-2026 Toshiaki Maki <makingx@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package am.ik.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import org.jspecify.annotations.Nullable;

/**
 * A JSON value. The accessors return {@code null} when the value is JSON {@code null}.
 * <p>
 * A number is parsed without losing a digit: an integer as the smallest of
 * {@code Integer}, {@code Long} and {@code BigInteger} that holds it, a number with a
 * fraction or an exponent as a {@code BigDecimal}.
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
		return this.value instanceof Number;
	}

	public boolean isInteger() {
		return this.value instanceof Integer;
	}

	public boolean isLong() {
		return this.value instanceof Long;
	}

	public boolean isBigInteger() {
		return this.value instanceof BigInteger;
	}

	public boolean isDecimal() {
		return this.value instanceof BigDecimal;
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

	public @Nullable Long asLong() {
		if (this.value instanceof Long longValue) {
			return longValue;
		}
		return nullOr("long");
	}

	public @Nullable BigInteger asBigInteger() {
		if (this.value instanceof BigInteger bigIntegerValue) {
			return bigIntegerValue;
		}
		return nullOr("big integer");
	}

	public @Nullable BigDecimal asDecimal() {
		if (this.value instanceof BigDecimal decimalValue) {
			return decimalValue;
		}
		return nullOr("decimal");
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
				case '\b' -> "\\b";
				case '\f' -> "\\f";
				case '\n' -> "\\n";
				case '\r' -> "\\r";
				case '\t' -> "\\t";
				case '\\', '"' -> "\\" + c;
				default -> (c < 0x20) ? controlEscape(c) : String.valueOf(c);
			});
		}
		return sb.toString();
	}

	/** A control character with no escape of its own, as a {@code u} escape. */
	private static String controlEscape(char c) {
		final String hex = Integer.toHexString(c);
		return "\\u" + "0000".substring(hex.length()) + hex;
	}

}
