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

import java.util.function.LongConsumer;
import se.llbit.chunky.renderer.scene.Scene;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.IntConsumer;

class CompressedFloatDumpFormat extends DumpFormat {

  public static final DumpFormat INSTANCE = new CompressedFloatDumpFormat();

  private CompressedFloatDumpFormat() {
  }

  @Override
  public void readSamples(
    DataInputStream inputStream,
    Scene scene,
    PixelConsumer consumer,
    LongConsumer pixelProgress
  ) throws IOException {
    FloatingPointCompressor.decompress(
      inputStream,
      scene.getSampleBuffer().numberOfDoubles(),
      consumer,
      pixelProgress
    );
  }

  @Override
  public void writeSamples(
    DataOutputStream outputStream,
    Scene scene,
    LongConsumer pixelProgress
  ) throws IOException {
    FloatingPointCompressor.compress(
      scene.getSampleBuffer(),
      outputStream,
      pixelProgress
    );
  }
}
