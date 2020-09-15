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
package se.llbit.png;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A PNG IEND chunk.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class IEND extends PngChunk {

  /**
   * The PNG chunk type identifier
   */
  public static final int CHUNK_TYPE = 0x49454E44;
  private int crc;

  /**
   * @throws IOException
   */
  public IEND() throws IOException {
    try (
      CrcOutputStream crcOutputStream = new CrcOutputStream();
      DataOutputStream crcOut = new DataOutputStream(crcOutputStream)
    ) {
      crcOut.writeInt(CHUNK_TYPE);
      crc = crcOutputStream.getCRC();
    }
  }

  @Override public int getChunkType() {
    return CHUNK_TYPE;
  }

  @Override protected void writeChunkData(DataOutputStream out) throws IOException {
  }

  @Override public int getChunkLength() {
    return 0;
  }

  @Override public int getChunkCRC() {
    return crc;
  }


}
