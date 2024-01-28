/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world;

import it.unimi.dsi.fastutil.io.FastBufferedInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * Container representing a handle to a singular chunk's data with its last modified timestamp.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkDataSource {
  public final int timestamp;
  private final byte[] data;
  private final CompressionScheme compressionScheme;

  public ChunkDataSource(int timestamp) {
    this(timestamp, new byte[0], null);
  }

  public ChunkDataSource(int timestamp, byte[] data, CompressionScheme compressionScheme) {
    this.timestamp = timestamp;
    this.data = data;
    this.compressionScheme = compressionScheme;
  }

  public boolean hasData() {
    return data != null && data.length > 0;
  }

  public InputStream getInputStream() throws IOException {
    return new FastBufferedInputStream(
      compressionScheme.wrapInputStream(
        new ByteArrayInputStream(data)
      )
    );
  }

  public enum CompressionScheme {
    GZIP(GZIPInputStream::new),
    ZLIB(InflaterInputStream::new),
    UNCOMPRESSED((inputStream) -> inputStream);

    private final WrapStream wrapper;

    CompressionScheme(WrapStream wrapper) {
      this.wrapper = wrapper;
    }

    @FunctionalInterface
    interface WrapStream {
      InputStream wrap(InputStream in) throws IOException;
    }

    public InputStream wrapInputStream(InputStream in) throws IOException {
      return wrapper.wrap(in);
    }
  }
}
