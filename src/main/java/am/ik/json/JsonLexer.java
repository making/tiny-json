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

public class JsonLexer implements Iterator<Token>, Iterable<Token> {
	private final String input;

	private int position;

	public JsonLexer(String input) {
		this.input = input;
		this.position = 0;
	}

	public Token nextToken() {
		Character current;
		do {
			current = this.consume();
			if (current == null) {
				return new Token(TokenType.EOF, "");
			}
		} while (Character.isWhitespace(current));

		if (current == '{') {
			return new Token(TokenType.LEFT_BRACE, "{");
		}
		else if (current == '}') {
			return new Token(TokenType.RIGHT_BRACE, "}");
		}
		else if (current == '[') {
			return new Token(TokenType.LEFT_BRACKET, "[");
		}
		else if (current == ']') {
			return new Token(TokenType.RIGHT_BRACKET, "]");
		}
		else if (current == ':') {
			return new Token(TokenType.COLON, ":");
		}
		else if (current == ',') {
			return new Token(TokenType.COMMA, ",");
		}
		else if (current == '"') {
			return this.stringToken();
		}
		else if (Character.isDigit(current) || current == '-') {
			return this.numberToken(current);
		}
		else if (Character.isLetter(current)) {
			return this.literalToken(current);
		}
		throw new JsonLexerException("Invalid Character: " + current);
	}

	private char current() {
		return this.input.charAt(this.position);
	}


	private boolean isEof() {
		return this.position >= this.input.length();
	}

	private Character consume() {
		if (this.isEof()) {
			return null;
		}
		final char current = this.current();
		this.position++;
		return current;
	}

	private Token stringToken() {
		final StringBuilder sb = new StringBuilder();
		Character current = this.consume();
		while (current != null) {
			if (current == '"') {
				return new Token(TokenType.STRING, sb.toString());
			}
			if (current != '\\') {
				sb.append(current);
			}
			else {
				final Character next = this.consume();
				if (next == null) {
					throw new JsonLexerException("Unterminated string: " + sb);
				}
				switch (next) {
					case '"':
					case '\\':
					case '/':
						sb.append(next);
						break;
					case 'f':
						sb.append('\f');
						break;
					case 'n':
						sb.append('\n');
						break;
					case 'r':
						sb.append('\r');
						break;
					case 't':
						sb.append('\t');
						break;
					default:
						sb.append('\\').append(next);
				}
			}
			current = this.consume();
		}
		throw new JsonLexerException("Unterminated string: " + sb);
	}

	private Token numberToken(char c) {
		final StringBuilder sb = new StringBuilder();
		sb.append(c);
		while (!this.isEof()) {
			final char current = this.current();
			if (Character.isDigit(current) || current == '.') {
				sb.append(this.consume());
			}
			else {
				break;
			}
		}
		final String number = sb.toString();
		if (number.contains(".")) {
			return new Token(TokenType.FLOAT, number);
		}
		else {
			return new Token(TokenType.INT, number);
		}
	}

	private Token literalToken(char c) {
		final StringBuilder sb = new StringBuilder();
		sb.append(c);
		while (!this.isEof()) {
			final char current = this.current();
			if (Character.isLetter(current)) {
				sb.append(this.consume());
			}
			else {
				break;
			}
		}
		final String literal = sb.toString();
		switch (literal) {
			case "true":
			case "false":
				return new Token(TokenType.BOOLEAN, literal);
			case "null":
				return new Token(TokenType.NULL, literal);
			default:
				throw new JsonLexerException("Invalid literal: " + literal);
		}
	}


	@Override
	public boolean hasNext() {
		return !this.isEof();
	}

	@Override
	public Token next() {
		if (this.isEof()) {
			throw new NoSuchElementException("EOF");
		}
		return this.nextToken();
	}

	@Override
	public Iterator<Token> iterator() {
		return this;
	}

	public Stream<Token> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}
}
