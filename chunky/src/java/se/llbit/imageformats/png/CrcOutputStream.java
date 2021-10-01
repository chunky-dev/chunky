/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.imageformats.png;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;

/**
 * Calculate CRC of the written data.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class CrcOutputStream extends OutputStream {

  CRC32 crc = new CRC32();

  @Override public void write(int b) throws IOException {
    crc.update(b);
  }

  @Override public void write(byte[] b, int off, int len) throws IOException {
    crc.update(b, off, len);
  }

  @Override public void write(byte[] b) throws IOException {
    crc.update(b);
  }

  /**
   * @return The calculated CRC value
   */
  public int getCRC() {
    return (int) crc.getValue();
  }

}
