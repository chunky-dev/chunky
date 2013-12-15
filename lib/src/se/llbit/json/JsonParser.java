/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.json;

import static se.llbit.json.JsonConstants.BEGIN_ARRAY;
import static se.llbit.json.JsonConstants.BEGIN_OBJECT;
import static se.llbit.json.JsonConstants.END_ARRAY;
import static se.llbit.json.JsonConstants.END_OBJECT;
import static se.llbit.json.JsonConstants.ESCAPE;
import static se.llbit.json.JsonConstants.FALSE;
import static se.llbit.json.JsonConstants.NAME_SEPARATOR;
import static se.llbit.json.JsonConstants.NULL;
import static se.llbit.json.JsonConstants.QUOTE_MARK;
import static se.llbit.json.JsonConstants.TRUE;
import static se.llbit.json.JsonConstants.VALUE_SEPARATOR;

import java.io.IOException;
import java.io.InputStream;

import se.llbit.io.LookaheadReader;

/**
 * Parses JSON
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class JsonParser {

	/**
	 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
	 */
	@SuppressWarnings("serial")
	public static class SyntaxError extends Exception {
		/**
		 * @param message
		 */
		public SyntaxError(String message) {
			super("Syntax Error: " + message);
		}
	}

	private final LookaheadReader in;

	/**
	 * @param input
	 */
	public JsonParser(InputStream input) {
		in = new LookaheadReader(input, 8);
	}

	/**
	 * Parses a JSON object or array.
	 * @return JsonObject or JsonArray, not null
	 * @throws IOException
	 * @throws SyntaxError
	 */
	public JsonValue parse() throws IOException, SyntaxError {
		JsonValue value;
		skipWhitespace();
		switch(in.peek()) {
			case BEGIN_OBJECT:
				value = parseObject();
				break;
			case BEGIN_ARRAY:
				value = parseArray();
				break;
			default:
				throw new SyntaxError("expected object or array");
		}
		skipWhitespace();
		if (in.peek() != -1) {
			throw new SyntaxError("garbage at end of input");
		}
		// attach value to tree
		JsonRoot root = new JsonRoot(value);
		return root.getValue();
	}

	private JsonArray parseArray() throws IOException, SyntaxError {
		accept(BEGIN_ARRAY);
		JsonArray array = new JsonArray();
		do {
			skipWhitespace();
			JsonValue value = parseValue();
			if (value == null) {
				if (array.hasElement() || in.peek() == VALUE_SEPARATOR)
					throw new SyntaxError("missing element in array");
				break;
			}
			array.addElement(value);
			skipWhitespace();
		} while (skip(VALUE_SEPARATOR));
		accept(END_ARRAY);
		return array;
	}

	private JsonValue parseValue() throws IOException, SyntaxError {
		switch (in.peek()) {
			case BEGIN_OBJECT:
				return parseObject();
			case BEGIN_ARRAY:
				return parseArray();
			case '0': case '1': case '2': case '3': case '4': case '5':
			case '6': case '7': case '8': case '9': case '-': case '+':
				return parseNumber();
			case QUOTE_MARK:
				return parseString();
			case 't':
				acceptLiteral(TRUE);
				return new JsonTrue();
			case 'f':
				acceptLiteral(FALSE);
				return new JsonFalse();
			case 'n':
				acceptLiteral(NULL);
				return new JsonNull();
			default:
				// TODO use Null Object pattern
				return null; // not a JSON value
		}
	}

	private JsonString parseString() throws IOException, SyntaxError {
		accept(QUOTE_MARK);
		StringBuilder sb = new StringBuilder();
		while (true) {
			int next = in.pop();
			if (next == -1) {
				throw new SyntaxError("EOF while parsing JSON string");
			} else if (next == ESCAPE) {
				sb.append(unescapeStringChar());
			} else if (next == QUOTE_MARK) {
				break;
			} else {
				sb.append((char) next);
			}
		}
		return new JsonString(sb.toString());
	}

	private char unescapeStringChar() throws IOException, SyntaxError {
		int next = in.pop();
		switch (next) {
		case QUOTE_MARK:
		case ESCAPE:
		case '/':
			return (char) next;
		case 'b':
			return '\b';
		case 'f':
			return '\f';
		case 'n':
			return '\n';
		case 'r':
			return '\r';
		case 't':
			return '\t';
		case 'u':
			int[] u = { hexdigit(), hexdigit(), hexdigit(), hexdigit() };
			return (char) ((u[0] << 12) | (u[1] << 8) | (u[2] << 4) | u[3]);
		case -1:
			throw new SyntaxError("end of input while parsing JSON string");
		default:
			throw new SyntaxError("illegal escape sequence in JSON string");
		}
	}

	private int hexdigit() throws IOException, SyntaxError {
		int next = in.pop();
		int v1 = next - '0';
		int v2 = next - 'A' + 0xA;
		int v3 = next - 'a' + 0xA;
		if (v1 >= 0 && v1 <= 9) return v1;
		if (v2 >= 0xA && v2 <= 0xF) return v2;
		if (v3 >= 0xA && v3 <= 0xF) return v3;
		throw new SyntaxError("non-hexadecimal digit in unicode escape sequence");
	}

	private JsonValue parseNumber() throws IOException, SyntaxError {
		StringBuilder sb = new StringBuilder();
		while (true) {
			switch (in.peek()) {
				case -1:
					throw new SyntaxError("EOF while parsing JSON number");
				case '0': case '1': case '2': case '3': case '4': case '5':
				case '6': case '7': case '8': case '9': case '-': case '+':
				case '.': case 'e': case 'E':
					sb.append((char) in.pop());
					break;
				default:
					return new JsonNumber(sb.toString());
			}
		}
	}

	private void skipWhitespace() throws IOException {
		while (isWhitespace(in.peek())) in.pop();
	}

	private boolean isWhitespace(int chr) {
		return chr == 0x20 || chr == 0x09 || chr == 0x0A || chr == 0x0D;
	}

	private void acceptLiteral(char[] literal) throws IOException, SyntaxError {
		for (char c: literal) {
			if (in.pop() != c)
				throw new SyntaxError("encountered invalid JSON literal");
		}
	}

	private JsonObject parseObject() throws IOException, SyntaxError {
		accept(BEGIN_OBJECT);
		JsonObject object = new JsonObject();
		do {
			skipWhitespace();
			JsonMember member = parseMember();
			if (member == null) {
				if (object.hasMember() || in.peek() == VALUE_SEPARATOR)
					throw new SyntaxError("missing member in object");
				break;
			}
			object.addMember(member);
			skipWhitespace();
		} while (skip(VALUE_SEPARATOR));
		accept(END_OBJECT);
		return object;
	}

	private boolean skip(char c) throws IOException, SyntaxError {
		boolean skip = in.peek() == c;
		if (skip) in.pop();
		return skip;
	}

	private void accept(char c) throws IOException, SyntaxError {
		int next = in.pop();
		if (next == -1)
			throw new SyntaxError("unexpected end of input (expected '" + c + "')");
		if (next != c)
			throw new SyntaxError("unexpected character (was '" +
				((char) next) + "', expected '" + c + "')");
	}

	private JsonMember parseMember() throws IOException, SyntaxError {
		if (in.peek() == QUOTE_MARK) {
			JsonString name = parseString();
			skipWhitespace();
			accept(NAME_SEPARATOR);
			skipWhitespace();
			JsonValue value = parseValue();
			if (value == null)
				throw new SyntaxError("missing value for object member");
			return new JsonMember(name.getValue(), value);
		}
		return null;
	}
}
