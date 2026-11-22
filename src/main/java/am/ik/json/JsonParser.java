/*
 * SPDX-FileCopyrightText: 2023-2026 Toshiaki Maki <makingx@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package am.ik.json;

public class JsonParser {

	private final JsonLexer lexer;

	private Token currentToken;

	public JsonParser(JsonLexer lexer) {
		this.lexer = lexer;
		this.currentToken = lexer.nextToken();
	}

	/**
	 * Parses the one value of the input: anything but whitespace after it is refused.
	 * @return the value
	 */
	public JsonNode parse() {
		final JsonNode value = parseValue();
		if (this.currentToken.type() != TokenType.EOF) {
			throw new JsonParseException("Unexpected token after the value: " + this.currentToken.type());
		}
		return value;
	}

	private JsonObject parseObject() {
		final JsonObject jsonObject = new JsonObject();
		consumeToken(TokenType.LEFT_BRACE);
		if (this.currentToken.type() != TokenType.RIGHT_BRACE) {
			do {
				final String key = consumeToken(TokenType.STRING).value();
				consumeToken(TokenType.COLON);
				jsonObject.put(key, parseValue());
			}
			while (consumeComma());
		}
		consumeToken(TokenType.RIGHT_BRACE);
		return jsonObject;
	}

	private JsonArray parseArray() {
		final JsonArray jsonArray = new JsonArray();
		consumeToken(TokenType.LEFT_BRACKET);
		if (this.currentToken.type() != TokenType.RIGHT_BRACKET) {
			do {
				jsonArray.add(parseValue());
			}
			while (consumeComma());
		}
		consumeToken(TokenType.RIGHT_BRACKET);
		return jsonArray;
	}

	private JsonNode parseValue() {
		return switch (this.currentToken.type()) {
			case LEFT_BRACE -> new JsonNode(parseObject());
			case LEFT_BRACKET -> new JsonNode(parseArray());
			case STRING -> new JsonNode(consumeToken(TokenType.STRING).value());
			case INT -> new JsonNode(integer(consumeToken(TokenType.INT).value()));
			case FLOAT -> new JsonNode(NumberText.decimal(consumeToken(TokenType.FLOAT).value()));
			case BOOLEAN -> new JsonNode("true".equals(consumeToken(TokenType.BOOLEAN).value()));
			case NULL -> {
				consumeToken(TokenType.NULL);
				yield new JsonNode(null);
			}
			default -> throw new JsonParseException("Unexpected token: " + this.currentToken.type());
		};
	}

	/**
	 * An integer as the smallest of {@code Integer} and {@code Long} that holds it, else
	 * as its text, which a {@code BigInteger} is made of when it is read.
	 */
	private static Object integer(String text) {
		final boolean negative = text.charAt(0) == '-';
		final int digits = negative ? text.length() - 1 : text.length();
		if (digits < 10) {
			return Integer.parseInt(text);
		}
		// The lexer lets no leading zero through: more digits are a larger number, and
		// nineteen of them compare as text.
		if (digits > 19
				|| (digits == 19 && text.compareTo(negative ? "-9223372036854775808" : "9223372036854775807") > 0)) {
			return NumberText.integer(text);
		}
		final long value = Long.parseLong(text);
		if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
			return (int) value;
		}
		return value;
	}

	/** Whether a comma is next, consumed: another member or element follows it. */
	private boolean consumeComma() {
		if (this.currentToken.type() != TokenType.COMMA) {
			return false;
		}
		consumeToken(TokenType.COMMA);
		return true;
	}

	private Token consumeToken(TokenType expectedType) {
		final Token token = this.currentToken;
		if (this.currentToken.type() != expectedType) {
			throw new JsonParseException(
					"Token mismatch. Expected: " + expectedType + ", found: " + this.currentToken.type());
		}
		this.currentToken = this.lexer.nextToken();
		return token;
	}

}
