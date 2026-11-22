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

	public JsonNode parse() {
		return parseValue();
	}

	private JsonObject parseObject() {
		final JsonObject jsonObject = new JsonObject();
		consumeToken(TokenType.LEFT_BRACE);
		while (this.currentToken.type() != TokenType.RIGHT_BRACE) {
			final String key = consumeToken(TokenType.STRING).value();
			consumeToken(TokenType.COLON);
			jsonObject.put(key, parseValue());
			if (this.currentToken.type() == TokenType.COMMA) {
				consumeToken(TokenType.COMMA);
			}
		}
		consumeToken(TokenType.RIGHT_BRACE);
		return jsonObject;
	}

	private JsonArray parseArray() {
		final JsonArray jsonArray = new JsonArray();
		consumeToken(TokenType.LEFT_BRACKET);
		while (this.currentToken.type() != TokenType.RIGHT_BRACKET) {
			jsonArray.add(parseValue());
			if (this.currentToken.type() == TokenType.COMMA) {
				consumeToken(TokenType.COMMA);
			}
		}
		consumeToken(TokenType.RIGHT_BRACKET);
		return jsonArray;
	}

	private JsonNode parseValue() {
		return switch (this.currentToken.type()) {
			case LEFT_BRACE -> new JsonNode(parseObject());
			case LEFT_BRACKET -> new JsonNode(parseArray());
			case STRING -> new JsonNode(consumeToken(TokenType.STRING).value());
			case INT -> new JsonNode(Integer.parseInt(consumeToken(TokenType.INT).value()));
			case FLOAT -> new JsonNode(Float.parseFloat(consumeToken(TokenType.FLOAT).value()));
			case BOOLEAN -> new JsonNode("true".equals(consumeToken(TokenType.BOOLEAN).value()));
			case NULL -> {
				consumeToken(TokenType.NULL);
				yield new JsonNode(null);
			}
			default -> throw new JsonParseException("Unexpected token: " + this.currentToken.type());
		};
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
