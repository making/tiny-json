/*
 * SPDX-FileCopyrightText: 2023-2026 Toshiaki Maki <makingx@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package am.ik.json;

import java.util.Map.Entry;

/**
 * The layout {@link Json#stringify(JsonNode, JsonWriter)} writes a document with.
 * <p>
 * The compact layout of {@link JsonNode#toString()} is the default. A writer laid out by
 * {@link #pretty(int)} writes an object's members one a line, indented a level deeper,
 * and an array's elements one a line too, like
 * {@code JSON.stringify(value, null, indent)}. {@link #inlineArrays()} keeps arrays on
 * one line instead, elements separated by {@code ", "}.
 */
public final class JsonWriter {

	private final int indent;

	private final boolean arraysInline;

	private JsonWriter(int indent, boolean arraysInline) {
		this.indent = indent;
		this.arraysInline = arraysInline;
	}

	/**
	 * A writer that puts an object's members one a line, each level indented by
	 * {@code indent} spaces.
	 * @param indent the width of one level in spaces
	 * @return the writer
	 */
	public static JsonWriter pretty(int indent) {
		if (indent < 0) {
			throw new IllegalArgumentException("indent must not be negative: " + indent);
		}
		return new JsonWriter(indent, false);
	}

	/**
	 * A writer of {@link #pretty(int) the pretty layout} two spaces a level.
	 * @return the writer
	 */
	public static JsonWriter pretty() {
		return pretty(2);
	}

	/**
	 * A copy of this writer that writes arrays on one line, elements separated by
	 * {@code ", "}. An object inside such an array is written on that line in its compact
	 * form.
	 * @return the writer
	 */
	public JsonWriter inlineArrays() {
		return new JsonWriter(this.indent, true);
	}

	String write(JsonNode node) {
		final StringBuilder out = new StringBuilder();
		write(out, node, 0);
		return out.toString();
	}

	private void write(StringBuilder out, JsonNode node, int level) {
		final Object value = node.value();
		if (value instanceof JsonObject object) {
			writeObject(out, object, level);
		}
		else if (value instanceof JsonArray array) {
			if (this.arraysInline) {
				writeInlineArray(out, array);
			}
			else {
				writeArray(out, array, level);
			}
		}
		else {
			out.append(node);
		}
	}

	private void writeObject(StringBuilder out, JsonObject object, int level) {
		if (object.isEmpty()) {
			out.append("{}");
			return;
		}
		out.append("{\n");
		int i = 0;
		for (Entry<String, JsonNode> member : object.asMap().entrySet()) {
			indent(out, level + 1);
			out.append('"').append(JsonNode.escape(member.getKey())).append("\": ");
			write(out, member.getValue(), level + 1);
			out.append(i + 1 < object.size() ? ",\n" : "\n");
			i++;
		}
		indent(out, level);
		out.append('}');
	}

	private void writeArray(StringBuilder out, JsonArray array, int level) {
		if (array.size() == 0) {
			out.append("[]");
			return;
		}
		out.append("[\n");
		for (int i = 0; i < array.size(); i++) {
			indent(out, level + 1);
			write(out, array.get(i), level + 1);
			out.append(i + 1 < array.size() ? ",\n" : "\n");
		}
		indent(out, level);
		out.append(']');
	}

	private void writeInlineArray(StringBuilder out, JsonArray array) {
		out.append('[');
		for (int i = 0; i < array.size(); i++) {
			if (i > 0) {
				out.append(", ");
			}
			final JsonNode element = array.get(i);
			if (element.value() instanceof JsonArray nested) {
				writeInlineArray(out, nested);
			}
			else {
				out.append(element);
			}
		}
		out.append(']');
	}

	private void indent(StringBuilder out, int level) {
		for (int i = 0; i < level * this.indent; i++) {
			out.append(' ');
		}
	}

}
