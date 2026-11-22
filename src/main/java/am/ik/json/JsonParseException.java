/*
 * SPDX-FileCopyrightText: 2023-2026 Toshiaki Maki <makingx@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package am.ik.json;

public class JsonParseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JsonParseException(String message) {
		super(message);
	}

}
