/*
 * Copyright (c) 2021 Chunky Contributors
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
package se.llbit.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.LongConsumer;

public class PositionalInputStream extends InputStream {
    private InputStream stream;
    private LongConsumer update;
    private long count = 0;
    private long mark;
    private long markLimit;
    private boolean canMark;

    public PositionalInputStream(InputStream stream, LongConsumer update) {
        this.stream = stream;
        this.update = update;
        this.canMark = stream.markSupported();
    }

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
        if (canMark) {
            stream.mark(readlimit);
            mark = count;
            markLimit = mark + readlimit;
        }
    }

    @Override
    public boolean markSupported() {
        return canMark;
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

    @Override
    public void reset() throws IOException {
        if (canMark && count <= markLimit) {
            stream.reset();
            count = mark;
        }
    }

    @Override
    public long skip(long n) throws IOException {
        long actual = stream.skip(n);
        count += actual;
        return actual;
    }
}
