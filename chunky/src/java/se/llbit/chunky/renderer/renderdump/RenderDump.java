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

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.log.Log;
import se.llbit.util.TaskTracker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Logic for loading render dumps using the correct strategy from AbstractDumpFormat for the given dump version. Automatically
 * falls back to classic format if no format version is found in the dump file.
 */
public class RenderDump {
  public static final byte[] DUMP_FORMAT_MAGIC_NUMBER = {0x44, 0x55, 0x4D, 0x50};
  private static final DumpFormat DEFAULT_DUMP_FORMAT = FloatingPointCompressorDumpFormat.INSTANCE;

  private static final HashMap<Integer, DumpFormat> RENDER_DUMP_FORMATS = new HashMap<>();

  public static void addRenderDumpFormat(DumpFormat format) {
    int version = format.getVersion();
    if (!RENDER_DUMP_FORMATS.containsKey(version)) {
      RENDER_DUMP_FORMATS.put(version, format);
    } else {
      Log.errorf("Render dump format with version %d already exists.", version);
    }
  }

  static {
    RenderDump.addRenderDumpFormat(ClassicDumpFormat.INSTANCE);
    RenderDump.addRenderDumpFormat(FloatingPointCompressorDumpFormat.INSTANCE);
  }

  private static DumpFormat getDumpFormat(int version) {
    if (!RENDER_DUMP_FORMATS.containsKey(version)) {
      Log.errorf("Cannot find render dump format with version %d.", version);
      return DEFAULT_DUMP_FORMAT;
    } else {
      return RENDER_DUMP_FORMATS.get(version);
    }
  }

  private static DumpFormat readDumpFormat(DataInputStream inputStream) throws IOException {
    assert inputStream.markSupported();
    inputStream.mark(4);
    int version;
    byte[] magicNumber = new byte[DUMP_FORMAT_MAGIC_NUMBER.length];
    if (DUMP_FORMAT_MAGIC_NUMBER.length == inputStream.read(magicNumber)
        && Arrays.equals(DUMP_FORMAT_MAGIC_NUMBER, magicNumber)) {
      version = inputStream.readInt();
    } else {
      inputStream.reset();
      version = 0;
    }
    return getDumpFormat(version);
  }

  /**
   * Load a scene dump from the given file into the scene. This overwrites ssp, renderTime and samples in the scene.
   *
   * @throws IllegalStateException If the width or height of the scene do not match the width or height in the dump.
   * @throws IOException           If the dump format is unknown or file access fails
   */
  public static void load(InputStream inputStream, Scene scene, TaskTracker taskTracker)
      throws IOException, IllegalStateException {
    DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));
    DumpFormat format = readDumpFormat(dataInputStream);
    format.load(dataInputStream, scene, taskTracker);
  }

  /**
   * Merge a scene dump from the given file into the scene. This overwrites ssp, renderTime and samples in the scene.
   *
   * @throws IllegalStateException If the width or height of the scene do not match the width or height in the dump.
   * @throws IOException           If the dump format is unknown or file access fails
   */
  public static void merge(InputStream inputStream, Scene scene, TaskTracker taskTracker)
      throws IOException, IllegalStateException {
    DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));
    DumpFormat format = readDumpFormat(dataInputStream);
    format.merge(dataInputStream, scene, taskTracker);
  }

  public static void save(OutputStream outputStream, Scene scene, TaskTracker taskTracker) throws IOException {
    save(outputStream, scene, taskTracker, DEFAULT_DUMP_FORMAT.getVersion());
  }

  public static void save(OutputStream outputStream, Scene scene, TaskTracker taskTracker, int version) throws IOException {
    DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream));
    DumpFormat format = getDumpFormat(version);
    if (version != 0) {
      dataOutputStream.write(DUMP_FORMAT_MAGIC_NUMBER);
      dataOutputStream.writeInt(version);
    }
    format.save(dataOutputStream, scene, taskTracker);
  }
}
