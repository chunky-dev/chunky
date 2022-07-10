/* Copyright (c) 2012-2014 Jesper Öqvist <jesper@llbit.se>
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

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * World texture.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class WorldTexture {

  private final Long2ObjectOpenHashMap<ChunkTexture> map = new Long2ObjectOpenHashMap<>();

  private boolean compacted = false;

  /**
   * Timestamp of last serialization.
   */
  private long timestamp = 0;

  /**
   * Set color at (x, z)
   *
   * @param frgb RGB color components
   * @throws IllegalStateException if this WorldTexture has already been finalized.
   */
  public void set(int x, int z, float[] frgb) {
    long cp = ((long) x >> 4) << 32 | ((z >> 4) & 0xffffffffL);
    ChunkTexture ct = map.get(cp);

    // This chunk texture might be used in other places so we must create a copy
    if (compacted) {
      ct = new ChunkTexture(ct);
    }

    if (ct == null) {
      ct = new ChunkTexture();
      map.put(cp, ct);
    }
    ct.set(x & 0xF, z & 0xF, frgb);
  }

  /**
   * @return True if this texture contains a RGB color components at (x, z)
   */
  public boolean contains(int x, int z) {
    long cp = ((long) x >> 4) << 32 | ((z >> 4) & 0xffffffffL);
    return map.containsKey(cp);
  }

  /**
   * @return RGB color components at (x, z)
   */
  public float[] get(int x, int z) {
    long cp = ((long) x >> 4) << 32 | ((z >> 4) & 0xffffffffL);
    ChunkTexture ct = map.get(cp);
    if (ct == null) {
      return null;
    }
    return ct.get(x & 0xF, z & 0xF);
  }

  /**
   * Write the world texture to the output stream
   *
   * @throws IOException
   */
  public void store(DataOutputStream out) throws IOException {
    out.writeInt(map.size());
    for (Long2ObjectMap.Entry<ChunkTexture> entry : map.long2ObjectEntrySet()) {
      long pos = entry.getLongKey();
      ChunkTexture texture = entry.getValue();
      out.writeInt((int) (pos >> 32));
      out.writeInt((int) pos);
      texture.store(out);
    }
  }

  /**
   * Load world texture from the input stream
   *
   * @return Loaded texture
   * @throws IOException
   */
  public static WorldTexture load(DataInputStream in) throws IOException {
    WorldTexture texture = new WorldTexture();
    HashMap<ChunkTexture, ChunkTexture> textureCache = new HashMap<>();
    int numTiles = in.readInt();
    for (int i = 0; i < numTiles; ++i) {
      int x = in.readInt();
      int z = in.readInt();
      ChunkTexture tile = ChunkTexture.load(in);
      tile = textureCache.computeIfAbsent(tile, t -> t);
      texture.map.put(((long) x) << 32 | (z & 0xffffffffL), tile);
    }
    return texture;
  }

  /**
   * Deduplicate this {@code WorldTexture} to save memory. This also makes this read-only.
   */
  public void compact() {
    if (compacted) return;
    compacted = true;

    HashMap<ChunkTexture, ChunkTexture> textureCache = new HashMap<>();
    for (Long2ObjectMap.Entry<ChunkTexture> entry : map.long2ObjectEntrySet()) {
      ChunkTexture tile = entry.getValue();

      if (textureCache.containsKey(tile)) {
        entry.setValue(textureCache.get(tile));
      } else {
        textureCache.put(tile, tile);
      }
    }
  }

  /**
   * @return last serialization timestamp
   */
  public long getTimestamp() {
    return timestamp;
  }

  /**
   * Set the serialization timestamp.
   */
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}
