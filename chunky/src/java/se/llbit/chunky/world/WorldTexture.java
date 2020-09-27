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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * World texture.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class WorldTexture {

  private final Map<ChunkPosition, ChunkTexture> map = new HashMap<>();

  /**
   * Timestamp of last serialization.
   */
  private long timestamp = 0;

  /**
   * Set color at (x, z)
   *
   * @param frgb RGB color components
   */
  public void set(int x, int z, float[] frgb) {
    ChunkPosition cp = ChunkPosition.get(x >> 4, z >> 4);
    ChunkTexture ct = map.get(cp);
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
    ChunkPosition cp = ChunkPosition.get(x >> 4, z >> 4);
    return map.containsKey(cp);
  }

  /**
   * @return RGB color components at (x, z)
   */
  public float[] get(int x, int z) {
    ChunkPosition cp = ChunkPosition.get(x >> 4, z >> 4);
    ChunkTexture ct = map.get(cp);
    if (ct == null) {
      ct = new ChunkTexture();
      map.put(cp, ct);
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
    for (Map.Entry<ChunkPosition, ChunkTexture> entry : map.entrySet()) {
      ChunkPosition pos = entry.getKey();
      ChunkTexture texture = entry.getValue();
      out.writeInt(pos.x);
      out.writeInt(pos.z);
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
    int numTiles = in.readInt();
    for (int i = 0; i < numTiles; ++i) {
      int x = in.readInt();
      int z = in.readInt();
      ChunkTexture tile = ChunkTexture.load(in);
      texture.map.put(ChunkPosition.get(x, z), tile);
    }
    return texture;
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
