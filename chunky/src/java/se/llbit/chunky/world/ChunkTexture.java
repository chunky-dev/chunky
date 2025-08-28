/* Copyright (c) 2012-2013 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.math.ColorUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Chunk texture
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkTexture {

  protected final byte[] data = new byte[Chunk.X_MAX * Chunk.Z_MAX * 3];

  /**
   * Create new texture
   */
  public ChunkTexture() {
  }

  /**
   * Copy an existing chunk texture
   */
  public ChunkTexture(ChunkTexture ct) {
    System.arraycopy(ct.data, 0, data, 0, data.length);
  }

  /**
   * Set color value at (x, z).
   *
   * @param frgb RGB color components to set
   */
  public void set(int x, int z, float[] frgb) {
    int index = (x + z * Chunk.X_MAX) * 3;
    data[index] = ColorUtil.RGBComponentFromLinear(frgb[0]);
    data[index + 1] = ColorUtil.RGBComponentFromLinear(frgb[1]);
    data[index + 2] = ColorUtil.RGBComponentFromLinear(frgb[2]);
  }

  /**
   * @return RGB color components at (x, z)
   */
  public float[] get(int x, int z) {
    float[] result = new float[3];
    int index = (x + z * Chunk.X_MAX) * 3;
    result[0] = ColorUtil.RGBComponentToLinear(data[index]);
    result[1] = ColorUtil.RGBComponentToLinear(data[index + 1]);
    result[2] = ColorUtil.RGBComponentToLinear(data[index + 2]);
    return result;
  }

  /**
   * Write this chunk texture to an output stream.
   *
   * @throws IOException
   */
  public void store(DataOutputStream out) throws IOException {
    for (int i = 0; i < Chunk.X_MAX * Chunk.Z_MAX; ++i) {
      out.writeFloat(ColorUtil.RGBComponentToLinear(data[i*3]));
      out.writeFloat(ColorUtil.RGBComponentToLinear(data[i*3 + 1]));
      out.writeFloat(ColorUtil.RGBComponentToLinear(data[i*3 + 2]));
    }
  }

  /**
   * Load a chunk texture from an input stream.
   *
   * @return The loaded texture
   * @throws IOException
   */
  public static ChunkTexture load(DataInputStream in) throws IOException {
    ChunkTexture texture = new ChunkTexture();
    for (int i = 0; i < Chunk.X_MAX * Chunk.Z_MAX; ++i) {
      texture.data[i*3] = ColorUtil.RGBComponentFromLinear(in.readFloat());
      texture.data[i*3 + 1] = ColorUtil.RGBComponentFromLinear(in.readFloat());
      texture.data[i*3 + 2] = ColorUtil.RGBComponentFromLinear(in.readFloat());
    }
    return texture;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChunkTexture that = (ChunkTexture) o;
    return Arrays.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(data);
  }
}
