package am.ik.json;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonLexerTest {

	@Test
	void nextToken_LEFT_EOF() {
		final JsonLexer lexer = new JsonLexer("");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.EOF);
	}

	@Test
	void nextToken_LEFT_BRACE() {
		final JsonLexer lexer = new JsonLexer("{");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.LEFT_BRACE);
	}

	@Test
	void nextToken_RIGHT_BRACE() {
		final JsonLexer lexer = new JsonLexer("}");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.RIGHT_BRACE);
	}

	@Test
	void nextToken_LEFT_BRACKET() {
		final JsonLexer lexer = new JsonLexer("[");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.LEFT_BRACKET);
	}

	@Test
	void nextToken_RIGHT_BRACKET() {
		final JsonLexer lexer = new JsonLexer("]");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.RIGHT_BRACKET);
	}

	@Test
	void nextToken_COLON() {
		final JsonLexer lexer = new JsonLexer(":");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.COLON);
	}

	@Test
	void nextToken_COMMA() {
		final JsonLexer lexer = new JsonLexer(",");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.COMMA);
	}

	@Test
	void nextToken_STRING() {
		final JsonLexer lexer = new JsonLexer("\"hello\"");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.STRING);
		assertThat(token.value()).isEqualTo("hello");
	}

	@Test
	void nextToken_STRING_doublequote() {
		final JsonLexer lexer = new JsonLexer("\"\\\"\"");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.STRING);
		assertThat(token.value()).isEqualTo("\"");
	}

	@Test
	void nextToken_STRING_n() {
		final JsonLexer lexer = new JsonLexer("\"\n\"");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.STRING);
		assertThat(token.value()).isEqualTo("\n");
	}

	@Test
	void nextToken_INT() {
		final JsonLexer lexer = new JsonLexer("100");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.INT);
		assertThat(token.value()).isEqualTo("100");
	}

	@Test
	void nextToken_INT_single() {
		final JsonLexer lexer = new JsonLexer("1");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.INT);
		assertThat(token.value()).isEqualTo("1");
	}

	@Test
	void nextToken_INT_negative() {
		final JsonLexer lexer = new JsonLexer("-100");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.INT);
		assertThat(token.value()).isEqualTo("-100");
	}

	@Test
	void nextToken_FLOAT() {
		final JsonLexer lexer = new JsonLexer("10.2");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.FLOAT);
		assertThat(token.value()).isEqualTo("10.2");
	}

	@Test
	void nextToken_FLOAT_negative() {
		final JsonLexer lexer = new JsonLexer("-10.2");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.FLOAT);
		assertThat(token.value()).isEqualTo("-10.2");
	}

	@Test
	void nextToken_TRUE() {
		final JsonLexer lexer = new JsonLexer("true");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.BOOLEAN);
		assertThat(token.value()).isEqualTo("true");
	}

	@Test
	void nextToken_FALSE() {
		final JsonLexer lexer = new JsonLexer("false");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.BOOLEAN);
		assertThat(token.value()).isEqualTo("false");
	}

	@Test
	void nextToken_NULL() {
		final JsonLexer lexer = new JsonLexer("null");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.NULL);
	}

	@Test
	void nextToken_STRING_empty_json() {
		final JsonLexer lexer = new JsonLexer("\"{}\"");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.STRING);
		assertThat(token.value()).isEqualTo("{}");
	}

	@Test
	void nextToken_STRING3_json() {
		final JsonLexer lexer = new JsonLexer("\"{\\\"key\\\":\\\"value\\\"}\"");
		final Token token = lexer.nextToken();
		assertThat(token.type()).isEqualTo(TokenType.STRING);
		assertThat(token.value()).isEqualTo("{\"key\":\"value\"}");
	}
}