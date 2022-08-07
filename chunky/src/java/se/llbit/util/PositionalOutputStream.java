/*
 * Copyright (c) 2022 Chunky contributors
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

import se.llbit.util.annotation.NotNull;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.LongConsumer;

/**
 * An output stream that keeps track of it's position and calls a callback whenever a write occurs.
 */
public class PositionalOutputStream extends FilterOutputStream {
  private final LongConsumer update;
  private long count = 0;

  public PositionalOutputStream(OutputStream out, LongConsumer update) {
    super(out);
    this.update = update;
  }

  public long getPosition() {
    return count;
  }

  @Override
  public void write(int b) throws IOException {
    super.write(b);
    count++;
    update.accept(count);
  }

  @Override
  public void write(@NotNull byte[] b) throws IOException {
    super.write(b);
    count += b.length;
    update.accept(count);
  }

  @Override
  public void write(@NotNull byte[] b, int off, int len) throws IOException {
    super.write(b, off, len);
    count += len;
    update.accept(count);
  }
}
