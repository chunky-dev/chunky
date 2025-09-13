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

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import se.llbit.util.interner.Interner;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A 3D chunk texture. This stores a column of {@link ChunkTexture}'s, each corresponding to a different
 * y-level in a chunk.
 */
public class ChunkTexture3d {
  protected final Int2ObjectOpenHashMap<ChunkTexture> data = new Int2ObjectOpenHashMap<>();
  protected transient boolean writeable = true;

  /**
   * Create new texture
   */
  public ChunkTexture3d() {
  }

  /**
   * Copy an existing chunk texture
   */
  public ChunkTexture3d(ChunkTexture3d ct) {
    for (Int2ObjectMap.Entry<ChunkTexture> entry : ct.data.int2ObjectEntrySet()) {
      data.put(entry.getIntKey(), new ChunkTexture(entry.getValue()));
    }
  }

  /**
   * Load a chunk texture from an input stream.
   */
  public static ChunkTexture3d load(DataInputStream in) throws IOException {
    ChunkTexture3d tex = new ChunkTexture3d();
    int count = in.readInt();
    for (int i = 0; i < count; i++) {
      int y = in.readInt();
      ChunkTexture ct = ChunkTexture.load(in);
      tex.data.put(y, ct);
    }
    return tex;
  }

  /**
   * Write this chunk texture to an output stream.
   */
  public void store(DataOutputStream out) throws IOException {
    out.writeInt(data.size());
    for (Int2ObjectMap.Entry<ChunkTexture> entry : data.int2ObjectEntrySet()) {
      out.writeInt(entry.getIntKey());
      entry.getValue().store(out);
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
  public ChunkTexture3d intern(Interner<ChunkTexture> interner2d, Interner<ChunkTexture3d> interner3d) {
    this.makeReadOnly();

    ChunkTexture3d interned = interner3d.intern(this);
    if (interned == this) {
      // This dosen't actually modify the visible data of this CT so it's safe to do despite being interned already
      this.compact(interner2d);
    }

    return interned;
  }

  /**
   * Compact this chunk texture.
   */
  public void compact(Interner<ChunkTexture> interner) {
    for (Int2ObjectMap.Entry<ChunkTexture> entry : data.int2ObjectEntrySet()) {
      entry.setValue(entry.getValue().intern(interner));
    }
  }

  /**
   * Set the color value at (x, y, z).
   */
  public ChunkTexture3d set(int x, int y, int z, float[] frgb) {
    if (writeable) {
      ChunkTexture ct = data.get(y);
      if (ct == null) {
        ct = new ChunkTexture();
      }

      ct = ct.set(x, z, frgb);
      data.put(y, ct);
      return this;
    } else {
      ChunkTexture3d tex = new ChunkTexture3d(this);
      return tex.set(x, y, z, frgb);
    }
  }

  /**
   * Get the color value at (x, y, z).
   */
  public float[] get(int x, int y, int z) {
    ChunkTexture ct = data.get(y);
    if (ct == null) {
      return null;
    }
    return ct.get(x, z);
  }

  @Override
  public int hashCode() {
    int hashCode = 1;
    for (Int2ObjectMap.Entry<ChunkTexture> entry : data.int2ObjectEntrySet()) {
      hashCode = 31 * hashCode + entry.getIntKey();
      hashCode = 31 * hashCode + entry.getValue().hashCode();
    }
    return hashCode;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ChunkTexture3d)) {
      return false;
    }
    ChunkTexture3d other = (ChunkTexture3d) obj;
    return data.equals(other.data);
  }
}
