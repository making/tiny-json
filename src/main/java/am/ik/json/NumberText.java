/*
 * SPDX-FileCopyrightText: 2023-2026 Toshiaki Maki <makingx@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package am.ik.json;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A parsed number that needs {@code java.math}, kept as the text it was written with: an
 * integer no {@code long} holds, or a number with a fraction or an exponent. It becomes a
 * {@code BigInteger} or a {@code BigDecimal} only when it is read, so a program that
 * parses JSON but reads no such number calls nothing of {@code java.math}, and a compiler
 * that leaves out what is never called leaves {@code java.math} out.
 */
final class NumberText {

	// Larger than any scale's distance from an int: an exponent saturates here.
	private static final long EXPONENT_BOUND = 10_000_000_000L;

	private final String text;

	private final boolean decimal;

	private NumberText(String text, boolean decimal) {
		this.text = text;
		this.decimal = decimal;
	}

	/**
	 * An integer no {@code long} holds.
	 * @param text the integer as RFC 8259 writes one
	 * @return the number
	 */
	static NumberText integer(String text) {
		return new NumberText(text, false);
	}

	/**
	 * A number with a fraction or an exponent, refused as {@code new BigDecimal(text)}
	 * refuses it: when its scale is no {@code int}.
	 * @param text the number as RFC 8259 writes one
	 * @return the number
	 */
	static NumberText decimal(String text) {
		final long scale = scale(text);
		if (scale != (int) scale) {
			throw new JsonParseException("Number out of range: " + text);
		}
		return new NumberText(text, true);
	}

	boolean isDecimal() {
		return this.decimal;
	}

	Number toNumber() {
		return this.decimal ? toBigDecimal() : toBigInteger();
	}

	BigInteger toBigInteger() {
		return new BigInteger(this.text);
	}

	BigDecimal toBigDecimal() {
		return new BigDecimal(this.text);
	}

	/**
	 * The text of the number as {@code toNumber().toString()} writes it, made without
	 * {@code java.math}: an integer's own text, a decimal in the canonical form
	 * {@link BigDecimal#toString()} specifies.
	 */
	@Override
	public String toString() {
		return this.decimal ? canonical(this.text) : this.text;
	}

	/**
	 * The scale {@code BigDecimal} gives the text: the digits of its fraction less its
	 * exponent.
	 */
	private static long scale(String text) {
		final int exponentAt = exponentAt(text);
		final int point = text.indexOf('.');
		long scale = (point < 0) ? 0 : exponentAt - point - 1;
		if (exponentAt < text.length()) {
			int i = exponentAt + 1;
			final boolean negative = text.charAt(i) == '-';
			if (negative || text.charAt(i) == '+') {
				i++;
			}
			long exponent = 0;
			for (; i < text.length(); i++) {
				exponent = Math.min(exponent * 10 + (text.charAt(i) - '0'), EXPONENT_BOUND);
			}
			scale += negative ? exponent : -exponent;
		}
		return scale;
	}

	private static int exponentAt(String text) {
		for (int i = 0; i < text.length(); i++) {
			final char c = text.charAt(i);
			if (c == 'e' || c == 'E') {
				return i;
			}
		}
		return text.length();
	}

	/**
	 * The canonical form of {@link BigDecimal#toString()}: the unscaled value's digits
	 * with no leading zero, a point inserted when the scale is not negative and the
	 * adjusted exponent is -6 or more, else one digit before the point and an {@code E}
	 * with the signed adjusted exponent. A zero has no minus.
	 */
	private static String canonical(String text) {
		final boolean negative = text.charAt(0) == '-';
		final int exponentAt = exponentAt(text);
		final StringBuilder digits = new StringBuilder();
		for (int i = negative ? 1 : 0; i < exponentAt; i++) {
			final char c = text.charAt(i);
			if (c != '.') {
				digits.append(c);
			}
		}
		int leading = 0;
		while (leading < digits.length() - 1 && digits.charAt(leading) == '0') {
			leading++;
		}
		final String unscaled = digits.substring(leading);
		final long scale = scale(text);
		final long adjusted = -scale + (unscaled.length() - 1);
		final StringBuilder out = new StringBuilder();
		if (negative && !unscaled.equals("0")) {
			out.append('-');
		}
		if (scale >= 0 && adjusted >= -6) {
			final int point = unscaled.length() - (int) scale;
			if (point > 0) {
				out.append(unscaled.substring(0, point));
				if (scale > 0) {
					out.append('.').append(unscaled.substring(point));
				}
			}
			else {
				out.append("0.");
				for (int i = point; i < 0; i++) {
					out.append('0');
				}
				out.append(unscaled);
			}
		}
		else {
			out.append(unscaled.charAt(0));
			if (unscaled.length() > 1) {
				out.append('.').append(unscaled.substring(1));
			}
			out.append('E');
			if (adjusted > 0) {
				out.append('+');
			}
			out.append(adjusted);
		}
		return out.toString();
	}

}
