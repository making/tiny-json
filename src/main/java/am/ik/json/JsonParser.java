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
			final Token token = consumeToken(TokenType.STRING);
			final String key = token.value();
			consumeToken(TokenType.COLON);
			final JsonNode value = parseValue();
			jsonObject.put(key, value);
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
			final JsonNode value = parseValue();
			jsonArray.add(value);
			if (this.currentToken.type() == TokenType.COMMA) {
				consumeToken(TokenType.COMMA);
			}
		}
		consumeToken(TokenType.RIGHT_BRACKET);
		return jsonArray;
	}

	private JsonNode parseValue() {
		final TokenType type = this.currentToken.type();
		switch (type) {
			case LEFT_BRACE:
				return new JsonNode(parseObject());
			case LEFT_BRACKET:
				return new JsonNode(parseArray());
			case STRING:
				final String stringValue = this.currentToken.value();
				consumeToken(TokenType.STRING);
				return new JsonNode(stringValue);
			case INT:
				final int intValue = Integer.parseInt(this.currentToken.value());
				consumeToken(TokenType.INT);
				return new JsonNode(intValue);
			case FLOAT:
				final float floatValue = Float.parseFloat(this.currentToken.value());
				consumeToken(TokenType.FLOAT);
				return new JsonNode(floatValue);
			case BOOLEAN:
				// TODO: `Boolean.parseBoolean` doesn't work with teavm-wasi 0.2.7
				final boolean booleanValue = "true".equals(this.currentToken.value());
				consumeToken(TokenType.BOOLEAN);
				return new JsonNode(booleanValue);
			case NULL:
				consumeToken(TokenType.NULL);
				return new JsonNode(null);
			default:
				throw new JsonParseException("Unexpected token: " + this.currentToken.type());
		}
	}

	private Token consumeToken(TokenType expectedType) {
		final Token token = this.currentToken;
		if (this.currentToken.type() == expectedType) {
			this.currentToken = this.lexer.nextToken();
		}
		else {
			throw new JsonParseException("Token mismatch. Expected: " + expectedType + ", found: " + this.currentToken.type());
		}
		return token;
	}
}
