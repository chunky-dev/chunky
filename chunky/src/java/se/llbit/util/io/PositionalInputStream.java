/* Copyright (c) 2021 Chunky Contributors
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
package se.llbit.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.LongConsumer;

/**
 * An input stream that keeps track of it's position and calls a callback whenever a read occurs.
 */
public class PositionalInputStream extends InputStream {
    private InputStream stream;
    private LongConsumer update;
    private long count = 0;
    private long mark = -1;
    private int readlimit;

    /**
     * Create a {@code PositionalInputStream} from an existing stream and a callback.
     */
    public PositionalInputStream(InputStream stream, LongConsumer update) {
        this.stream = stream;
        this.update = update;
    }

    /** Get the current position of this stream */
    public long getPosition() {
        return count;
    }

    @Override
    public int available() throws IOException {
        return stream.available();
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void mark(int readlimit) {
        stream.mark(readlimit);
        this.mark = count;
        this.readlimit = readlimit;
    }

    @Override
    public boolean markSupported() {
        return stream.markSupported();
    }

    @Override
    public int read() throws IOException {
        update.accept(++count);
        return stream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        int actual = stream.read(b);
        count += actual;
        update.accept(count);
        return actual;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int actual = stream.read(b, off, len);
        count += actual;
        update.accept(count);
        return actual;
    }

    /**
     * Reset this stream to the position when {@code mark} was last called.
     * This adheres to the strictest conditions under the {@code InputStream.reset} contract even if the underlying
     * stream does not.
     * An IOException is called if:
     *     - {@code markSupported} returns {@code false}
     *     - {@code mark} has not been called since the stream was created
     *     - The number of bytes read since {@code mark} was last called exceeds {@code readlimit}
     */
    @Override
    public void reset() throws IOException {
        if (!markSupported()) throw new IOException("mark/reset not supported");
        if (mark == -1) throw new IOException("mark must be called before reset");
        if (count > mark + readlimit) throw new IOException("readlimit exceeded");

        stream.reset();
        count = mark;
    }

    @Override
    public long skip(long n) throws IOException {
        long actual = stream.skip(n);
        count += actual;
        return actual;
    }
}
