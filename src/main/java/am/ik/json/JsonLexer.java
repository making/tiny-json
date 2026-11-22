/*
 * SPDX-FileCopyrightText: 2023-2026 Toshiaki Maki <makingx@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
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
			// the four characters RFC 8259 counts as whitespace
			if (next != ' ' && next != '\t' && next != '\n' && next != '\r') {
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
				if (isDigit(current) || current == '-') {
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
			if (current < 0x20) {
				throw new JsonLexerException("Unescaped control character in a string: " + sb);
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
					case 'b' -> sb.append('\b');
					case 'f' -> sb.append('\f');
					case 'n' -> sb.append('\n');
					case 'r' -> sb.append('\r');
					case 't' -> sb.append('\t');
					case 'u' -> sb.append(unicodeEscape());
					default -> throw new JsonLexerException("Invalid escape: \\" + next);
				}
			}
			current = consume();
		}
		throw new JsonLexerException("Unterminated string: " + sb);
	}

	/**
	 * The character of a {@code u} escape, its four hexadecimal digits read. A character
	 * outside the Basic Multilingual Plane is two such escapes, a surrogate pair, each
	 * read as the {@code char} it is.
	 */
	private char unicodeEscape() {
		if (this.position + 4 > this.input.length()) {
			throw new JsonLexerException("Invalid unicode escape: " + this.input.substring(this.position));
		}
		int code = 0;
		for (int i = 0; i < 4; i++) {
			final int digit = Character.digit(this.input.charAt(this.position + i), 16);
			if (digit < 0) {
				throw new JsonLexerException(
						"Invalid unicode escape: " + this.input.substring(this.position, this.position + 4));
			}
			code = code * 16 + digit;
		}
		this.position += 4;
		return (char) code;
	}

	/**
	 * A number as RFC 8259 writes one: an optional minus, an integer part with no leading
	 * zero, then an optional fraction and an optional exponent, each with at least one
	 * digit. A fraction or an exponent makes it a {@link TokenType#FLOAT}.
	 */
	private Token numberToken(char c) {
		final int start = this.position - 1;
		final char first = (c == '-') ? nextDigit() : c;
		if (first != '0') {
			skipDigits();
		}
		boolean fraction = false;
		if (!isEof() && this.input.charAt(this.position) == '.') {
			fraction = true;
			this.position++;
			nextDigit();
			skipDigits();
		}
		if (!isEof() && (this.input.charAt(this.position) == 'e' || this.input.charAt(this.position) == 'E')) {
			fraction = true;
			this.position++;
			if (!isEof() && (this.input.charAt(this.position) == '+' || this.input.charAt(this.position) == '-')) {
				this.position++;
			}
			nextDigit();
			skipDigits();
		}
		if (!isEof() && isDigit(this.input.charAt(this.position))) {
			throw new JsonLexerException("Invalid number: " + this.input.substring(start, this.position + 1));
		}
		final String number = this.input.substring(start, this.position);
		return new Token(fraction ? TokenType.FLOAT : TokenType.INT, number);
	}

	private char nextDigit() {
		if (isEof() || !isDigit(this.input.charAt(this.position))) {
			throw new JsonLexerException("Invalid number: a digit expected at " + this.position);
		}
		return this.input.charAt(this.position++);
	}

	private void skipDigits() {
		while (!isEof() && isDigit(this.input.charAt(this.position))) {
			this.position++;
		}
	}

	private static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
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
