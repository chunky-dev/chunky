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
package se.llbit.io;
import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class LookaheadReader extends FilterReader {

	/**
	 * Maximum read buffer size
	 */
	private static final int BUFF_SIZE = 1024;

	/**
	 * The lookahead
	 */
	private final int lookahead;

	private final char[] buffer = new char[BUFF_SIZE];

	/**
	 * Current position in the read buffer
	 */
	private int pos = 0;

	/**
	 * Number of valid characters in the read buffer
	 */
	private int length = 0;

	/**
	 * Create a new lookahead reader
	 * @param in
	 * @param nlook
	 */
	public LookaheadReader(Reader in, int nlook) {
		super(in);
		lookahead = nlook;
		if (lookahead >= BUFF_SIZE) {
			throw new IllegalArgumentException("Too large lookahead");
		}
	}

	/**
	 * Create a new lookahead reader
	 * @param in
	 * @param nlook
	 */
	public LookaheadReader(InputStream in, int nlook) {
		this(new InputStreamReader(in), nlook);
	}

	/**
	 * Skip some input.
	 * @param num Number of characters to skip forward
	 */
	public void consume(int num) {
		pos += num;
	}

	@Override
	public long skip(long num) throws IOException {
		consume((int) num);
		return num;
	}

	/**
	 * @return The next character, or -1 if the next character
	 * is past the end of the input stream
	 * @throws IOException
	 */
	public int peek() throws IOException {
		refill();
		if (pos < length)
			return buffer[pos];
		else
			return -1;
	}

	/**
	 * Look ahead in the input stream.
	 * @param index Number of characters to look ahead
	 * @return The character at the given position, or -1 if the given
	 * position is outside the read buffer
	 * @throws IOException
	 */
	public int peek(int index) throws IOException {
		refill();
		if ((pos+index) < length)
			return buffer[pos+index];
		else
			return -1;
	}

	/**
	 * Pop next character in the stream.
	 * @return The next character, or -1 if the end has been reached
	 * @throws IOException
	 */
	public int pop() throws IOException {
		refill();
		if (pos < length)
			return buffer[pos++];
		else
			return -1;
	}

	/**
	 * Refills the input buffer if it does not currently satisfy the required
	 * lookahead.
	 */
	private void refill() throws IOException {
		if (length - pos <= lookahead) {
			if (length-pos > 0) {
				System.arraycopy(buffer, pos, buffer, 0, length-pos);
				length = length-pos;
				pos = 0;
			} else {
				length = 0;
				pos = 0;
			}
			int i = super.read(buffer, length, BUFF_SIZE-length);
			length += (i != -1) ? i : 0;
		}
	}

	@Override
	public int read() throws java.io.IOException {
		return pop();
	}

	@Override
	public int read(char cbuf[], int off, int len) throws IOException {
		if (!ready())
			return -1;
		len += off;

		for (int i=off; i<len; i++) {
			int c = read();
			if (c < 0) return i-off;
			else cbuf[i] = (char) c;
		}
		return len-off;
	}

	@Override
	public boolean ready() throws IOException {
		return pos < length || super.ready();
	}

}

