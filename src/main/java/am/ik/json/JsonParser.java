/*
 * SPDX-FileCopyrightText: 2023-2026 Toshiaki Maki <makingx@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package am.ik.json;

import java.math.BigDecimal;
import java.math.BigInteger;

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
			case FLOAT -> new JsonNode(decimal(consumeToken(TokenType.FLOAT).value()));
			case BOOLEAN -> new JsonNode("true".equals(consumeToken(TokenType.BOOLEAN).value()));
			case NULL -> {
				consumeToken(TokenType.NULL);
				yield new JsonNode(null);
			}
			default -> throw new JsonParseException("Unexpected token: " + this.currentToken.type());
		};
	}

	/**
	 * An integer as the smallest of {@code Integer}, {@code Long} and {@code BigInteger}.
	 */
	private static Number integer(String text) {
		final int digits = (text.charAt(0) == '-') ? text.length() - 1 : text.length();
		if (digits < 10) {
			return Integer.parseInt(text);
		}
		final BigInteger value = new BigInteger(text);
		if (value.bitLength() < 32) {
			return value.intValue();
		}
		if (value.bitLength() < 64) {
			return value.longValue();
		}
		return value;
	}

	/** A number with a fraction or an exponent, with the digits it is written with. */
	private static BigDecimal decimal(String text) {
		try {
			return new BigDecimal(text);
		}
		catch (NumberFormatException ex) {
			throw new JsonParseException("Number out of range: " + text);
		}
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
