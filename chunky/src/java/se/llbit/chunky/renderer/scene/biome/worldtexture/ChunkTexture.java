/*
 * Copyright (c) 2023 Chunky contributors
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

package se.llbit.chunky.renderer.scene.biome.worldtexture;

import se.llbit.chunky.world.Chunk;
import se.llbit.math.ColorUtil;
import se.llbit.util.interner.Interner;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * A 2D chunk texture. This stores a pixel for each block in a chunk, with 8 bits per channel.
 */
public class ChunkTexture  {
  protected final byte[] data = new byte[Chunk.X_MAX * Chunk.Z_MAX * 3];
  protected transient boolean writeable = true;

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
   * Load a chunk texture from an input stream.
   */
  public static ChunkTexture load(DataInputStream in) throws IOException {
    ChunkTexture tex = new ChunkTexture();
    for (int i = 0; i < Chunk.X_MAX * Chunk.Z_MAX; i++) {
      tex.data[i*3 + 0] = ColorUtil.RGBComponentFromLinear(in.readFloat());
      tex.data[i*3 + 1] = ColorUtil.RGBComponentFromLinear(in.readFloat());
      tex.data[i*3 + 2] = ColorUtil.RGBComponentFromLinear(in.readFloat());
    }
    return tex;
  }

  /**
   * Write this chunk texture to an output stream.
   */
  public void store(DataOutputStream out) throws IOException {
    for (int i = 0; i < Chunk.X_MAX * Chunk.Z_MAX; i++) {
      out.writeFloat(ColorUtil.RGBComponentToLinear(data[i*3]));
      out.writeFloat(ColorUtil.RGBComponentToLinear(data[i*3 + 1]));
      out.writeFloat(ColorUtil.RGBComponentToLinear(data[i*3 + 2]));
    }
  }

  /**
   * Make this chunk texture read-only.
   */
  public void makeReadOnly() {
    writeable = false;
  }

  /**
   * Intern this chunk texture.
   */
  public ChunkTexture intern(Interner<ChunkTexture> interner) {
    this.makeReadOnly();
    return interner.intern(this);
  }

  /**
   * Set the color value at (x, z).
   * @param frgb RGB color components to set
   * @return The chunk texture with the color value set
   */
  public ChunkTexture set(int x, int z, float[] frgb) {
    if (writeable) {
      int index = (x + z * Chunk.X_MAX) * 3;
      data[index + 0] = ColorUtil.RGBComponentFromLinear(frgb[0]);
      data[index + 1] = ColorUtil.RGBComponentFromLinear(frgb[1]);
      data[index + 2] = ColorUtil.RGBComponentFromLinear(frgb[2]);
      return this;
    } else {
      ChunkTexture tex = new ChunkTexture(this);
      return tex.set(x, z, frgb);
    }
  }

  /**
   * Get the color value at (x, z).
   * @return RGB color components at (x, z)
   */
  public float[] get(int x, int z) {
    float[] result = new float[3];
    int index = (x + z * Chunk.X_MAX) * 3;
    result[0] = ColorUtil.RGBComponentToLinear(data[index + 0]);
    result[1] = ColorUtil.RGBComponentToLinear(data[index + 1]);
    result[2] = ColorUtil.RGBComponentToLinear(data[index + 2]);
    return result;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(data);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ChunkTexture)) return false;
    ChunkTexture that = (ChunkTexture) o;
    return Arrays.equals(data, that.data);
  }
}
