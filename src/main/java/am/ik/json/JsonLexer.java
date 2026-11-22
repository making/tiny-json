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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jspecify.annotations.Nullable;

public class JsonLexer implements Iterator<Token>, Iterable<Token> {

	private final String input;

	private int position;

	public JsonLexer(String input) {
		this.input = input;
	}

	public Token nextToken() {
		char current;
		while (true) {
			final Character next = consume();
			if (next == null) {
				return new Token(TokenType.EOF, "");
			}
			if (!Character.isWhitespace(next)) {
				current = next;
				break;
			}
		}
		return switch (current) {
			case '{' -> new Token(TokenType.LEFT_BRACE, "{");
			case '}' -> new Token(TokenType.RIGHT_BRACE, "}");
			case '[' -> new Token(TokenType.LEFT_BRACKET, "[");
			case ']' -> new Token(TokenType.RIGHT_BRACKET, "]");
			case ':' -> new Token(TokenType.COLON, ":");
			case ',' -> new Token(TokenType.COMMA, ",");
			case '"' -> stringToken();
			default -> {
				if (Character.isDigit(current) || current == '-') {
					yield numberToken(current);
				}
				if (Character.isLetter(current)) {
					yield literalToken(current);
				}
				throw new JsonLexerException("Invalid Character: " + current);
			}
		};
	}

	private boolean isEof() {
		return this.position >= this.input.length();
	}

	private @Nullable Character consume() {
		if (isEof()) {
			return null;
		}
		return this.input.charAt(this.position++);
	}

	private Token stringToken() {
		final StringBuilder sb = new StringBuilder();
		Character current = consume();
		while (current != null) {
			if (current == '"') {
				return new Token(TokenType.STRING, sb.toString());
			}
			if (current != '\\') {
				sb.append(current);
			}
			else {
				final Character next = consume();
				if (next == null) {
					throw new JsonLexerException("Unterminated string: " + sb);
				}
				switch (next) {
					case '"', '\\', '/' -> sb.append(next);
					case 'f' -> sb.append('\f');
					case 'n' -> sb.append('\n');
					case 'r' -> sb.append('\r');
					case 't' -> sb.append('\t');
					default -> sb.append('\\').append(next);
				}
			}
			current = consume();
		}
		throw new JsonLexerException("Unterminated string: " + sb);
	}

	private Token numberToken(char c) {
		final StringBuilder sb = new StringBuilder();
		sb.append(c);
		while (!isEof()) {
			final char current = this.input.charAt(this.position);
			if (Character.isDigit(current) || current == '.') {
				sb.append(this.input.charAt(this.position++));
			}
			else {
				break;
			}
		}
		final String number = sb.toString();
		if (number.contains(".")) {
			return new Token(TokenType.FLOAT, number);
		}
		return new Token(TokenType.INT, number);
	}

	private Token literalToken(char c) {
		final StringBuilder sb = new StringBuilder();
		sb.append(c);
		while (!isEof()) {
			final char current = this.input.charAt(this.position);
			if (Character.isLetter(current)) {
				sb.append(this.input.charAt(this.position++));
			}
			else {
				break;
			}
		}
		final String literal = sb.toString();
		return switch (literal) {
			case "true", "false" -> new Token(TokenType.BOOLEAN, literal);
			case "null" -> new Token(TokenType.NULL, literal);
			default -> throw new JsonLexerException("Invalid literal: " + literal);
		};
	}

	@Override
	public boolean hasNext() {
		return !isEof();
	}

	@Override
	public Token next() {
		if (isEof()) {
			throw new NoSuchElementException("EOF");
		}
		return nextToken();
	}

	@Override
	public Iterator<Token> iterator() {
		return this;
	}

	public Stream<Token> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

}
