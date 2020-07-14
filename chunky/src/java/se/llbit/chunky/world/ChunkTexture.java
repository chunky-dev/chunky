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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Chunk texture
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkTexture {

  float[] data = new float[Chunk.X_MAX * Chunk.Z_MAX * 3];

  /**
   * Create new texture
   */
  public ChunkTexture() {
  }

  /**
   * Set color value at (x, z).
   *
   * @param frgb RGB color components to set
   */
  public void set(int x, int z, float[] frgb) {
    int index = x + z * Chunk.X_MAX;
    data[index*3] = frgb[0];
    data[index*3 + 1] = frgb[1];
    data[index*3 + 2] = frgb[2];
  }

  /**
   * @return RGB color components at (x, z)
   */
  public float[] get(int x, int z) {
    float[] result = new float[3];
    int index = x + z * Chunk.X_MAX;
    System.arraycopy(data, index*3, result, 0, 3);
    return result;
  }

  /**
   * Write this chunk texture to an output stream.
   *
   * @throws IOException
   */
  public void store(DataOutputStream out) throws IOException {
    for (int i = 0; i < Chunk.X_MAX * Chunk.Z_MAX; ++i) {
      out.writeFloat(data[i*30]);
      out.writeFloat(data[i*3 + 1]);
      out.writeFloat(data[i*3 + 2]);
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
      texture.data[i*3] = in.readFloat();
      texture.data[i*3 + 1] = in.readFloat();
      texture.data[i*3 + 2] = in.readFloat();
    }
    return texture;
  }

}
