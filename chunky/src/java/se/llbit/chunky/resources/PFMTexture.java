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
package se.llbit.chunky.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

import se.llbit.log.Log;

public class PFMTexture extends AbstractHdriTexture {

  public PFMTexture(File file) {
    try (
      FileInputStream in = new FileInputStream(file);
      Scanner scan = new Scanner(in)
    ) {
      String fmt = scan.next();
      int components = 3;
      switch (fmt) {
        case "PF":
          components = 3;
          break;
        case "Pf":
          components = 1;
          break;
        default:
          Log.warn("Unknown PFM format!");
          break;
      }
      width = Integer.parseInt(scan.next());
      height = Integer.parseInt(scan.next());
      float endianScale = Float.parseFloat(scan.next());
      boolean bigEndian = true;
      //			float scale;// not used yet
      if (endianScale < 0) {
        //				scale = -endianScale;
        bigEndian = false;
      } else {
        //				scale = endianScale;
      }
      scan.close();
      try (RandomAccessFile f = new RandomAccessFile(file, "r")) {
        long len = f.length();
        long start = len - (long) width * height * components * 4;
        buf = new float[width * height * 3];
        int offset = 0;

        FileChannel channel = f.getChannel();
        MappedByteBuffer byteBuf = channel.map(FileChannel.MapMode.READ_ONLY, start, buf.length * 4L);
        if (bigEndian) {
          byteBuf.order(ByteOrder.BIG_ENDIAN);
        } else {
          byteBuf.order(ByteOrder.LITTLE_ENDIAN);
        }
        while (offset < buf.length) {
          if (components == 3) {
            buf[offset + 0] = byteBuf.getFloat();
            buf[offset + 1] = byteBuf.getFloat();
            buf[offset + 2] = byteBuf.getFloat();
          } else {
            buf[offset + 0] = buf[offset + 1] = buf[offset + 2] = byteBuf.getFloat();
          }
          offset += 3;
        }
      }
    } catch (IOException e) {
      Log.error("Error loading PFM image:", e);
      e.printStackTrace();
    }
  }
}
