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
package se.llbit.png;

import java.io.DataOutputStream;
import java.io.IOException;

public class ITXT extends PngChunk {

  public static final int CHUNK_TYPE = 0x69545874;

  private int crc;
  private final String keyword;
  private final String text;

  public ITXT(String keyword, String text) {
    this.keyword = keyword;
    this.text = text;
  }

  @Override protected void writeChunkData(DataOutputStream out) throws IOException {
    CrcOutputStream crcOutputStream = new CrcOutputStream();
    DataOutputStream crcOut = new DataOutputStream(crcOutputStream);

    crcOut.writeInt(CHUNK_TYPE);

    crcOut.writeBytes(keyword);
    out.writeBytes(keyword);

    // null separator
    crcOut.writeByte(0);
    out.writeByte(0);

    // compression flag
    crcOut.writeByte(0);
    out.writeByte(0);

    // compression method
    crcOut.writeByte(0);
    out.writeByte(0);

    // language tag + null sep
    crcOut.writeByte(0);
    out.writeByte(0);

    // translated keyword + null sep
    crcOut.writeByte(0);
    out.writeByte(0);

    crcOut.writeBytes(text);
    out.writeBytes(text);

    crc = crcOutputStream.getCRC();
    crcOut.close();
  }

  @Override public int getChunkLength() {
    return keyword.length() + 5 + text.length();
  }

  @Override public int getChunkType() {
    return CHUNK_TYPE;
  }

  @Override public int getChunkCRC() {
    return crc;
  }

}
