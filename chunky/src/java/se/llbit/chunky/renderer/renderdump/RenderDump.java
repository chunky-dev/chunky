/* Copyright (c) 2021 Chunky contributors
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
package se.llbit.chunky.renderer.renderdump;

import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.util.TaskTracker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

/**
 * Logic for loading render dumps using the correct strategy from DumpFormat for the given dump version. Automatically
 * falls back to classic format if no format version is found in the dump file.
 * <p>
 * TODO for diligent contributors: clean up code duplication
 */
public class RenderDump {
  static final byte[] DUMP_FORMAT_MAGIC_NUMBER = {0x44, 0x55, 0x4D, 0x50}; // ASCII for "DUMP"

  static final int CURRENT_DUMP_VERSION = 2;

  private static DumpFormat getDumpFormatForVersion(int version) {
    switch (version) {
      case 1:
        return CompressedFloatDumpFormat.INSTANCE;
      case 2:
        return IntegratedSppDumpFormat.INSTANCE;
      default:
        return ClassicDumpFormat.INSTANCE;
    }
  }

  /**
   * Load a scene dump from the given file into the scene. This overwrites ssp, renderTime and samples in the scene.
   *
   * @throws IllegalStateException If the width or height of the scene do not match the width or height in the dump.
   * @throws IOException           If the dump format is unknown or file access fails
   */
  public static void load(InputStream inputStream, Scene scene, TaskTracker taskTracker)
      throws IOException, IllegalStateException {
    int magicNumberLength = DUMP_FORMAT_MAGIC_NUMBER.length;

    PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream, magicNumberLength);
    byte[] magicNumber = new byte[magicNumberLength];

    // If the file starts with the magic number, it is the new format containing a version number
    if (magicNumberLength == pushbackInputStream.read(magicNumber, 0, magicNumberLength)
        && Arrays.equals(DUMP_FORMAT_MAGIC_NUMBER, magicNumber)) {

      DataInputStream dataInputStream = new DataInputStream(pushbackInputStream);
      int dumpVersion = dataInputStream.readInt();
      getDumpFormatForVersion(dumpVersion).load(dataInputStream, scene, taskTracker);
    } else {
      // Old format that is a gzipped stream, the header needs to be pushed back
      pushbackInputStream.unread(magicNumber, 0, 4);
      DataInputStream dataInputStream = new DataInputStream(new GZIPInputStream(pushbackInputStream));
      getDumpFormatForVersion(0).load(dataInputStream, scene, taskTracker);
    }
  }

  /**
   * Merge a scene dump from the given file into the scene. This overwrites ssp, renderTime and samples in the scene.
   *
   * @throws IllegalStateException If the width or height of the scene do not match the width or height in the dump.
   * @throws IOException           If the dump format is unknown or file access fails
   */
  public static void merge(InputStream inputStream, Scene scene, TaskTracker taskTracker)
      throws IOException, IllegalStateException {
    int magicNumberLength = DUMP_FORMAT_MAGIC_NUMBER.length;

    PushbackInputStream pushbackInputStream = new PushbackInputStream(new FastBufferedInputStream(inputStream), magicNumberLength);
    byte[] magicNumber = new byte[magicNumberLength];

    // If the file starts with the magic number, it is the new format containing a version number
    if (magicNumberLength == pushbackInputStream.read(magicNumber, 0, magicNumberLength)
        && Arrays.equals(DUMP_FORMAT_MAGIC_NUMBER, magicNumber)) {

      DataInputStream dataInputStream = new DataInputStream(pushbackInputStream);
      int dumpVersion = dataInputStream.readInt();
      getDumpFormatForVersion(dumpVersion).merge(dataInputStream, scene, taskTracker);
    } else {
      // Old format that is a gzipped stream, the header needs to be pushed back
      pushbackInputStream.unread(magicNumber, 0, 4);
      DataInputStream dataInputStream = new DataInputStream(new GZIPInputStream(pushbackInputStream));
      getDumpFormatForVersion(0).merge(dataInputStream, scene, taskTracker);
    }
  }

  public static void save(OutputStream outputStream, Scene scene, TaskTracker taskTracker) throws IOException {
    outputStream.write(DUMP_FORMAT_MAGIC_NUMBER);
    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
    DumpFormat format = getDumpFormatForVersion(CURRENT_DUMP_VERSION);
    dataOutputStream.writeInt(CURRENT_DUMP_VERSION);
    format.save(dataOutputStream, scene, taskTracker);
  }
}
