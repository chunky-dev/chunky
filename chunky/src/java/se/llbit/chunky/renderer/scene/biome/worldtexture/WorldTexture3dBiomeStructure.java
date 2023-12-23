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

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import se.llbit.chunky.renderer.scene.biome.BiomeStructure;
import se.llbit.util.annotation.NotNull;
import se.llbit.util.interner.Interner;
import se.llbit.util.interner.StrongInterner;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WorldTexture3dBiomeStructure implements BiomeStructure {
  public static final String ID = "WORLD_TEXTURE_3D";

  /**
   * Map from chunk position to chunk texture.
   */
  private final Long2ObjectOpenHashMap<ChunkTexture3d> map = new Long2ObjectOpenHashMap<>();

  private LongOpenHashSet live = null;
  private Interner<ChunkTexture> interner2d = null;
  private Interner<ChunkTexture3d> interner3d = null;

  protected WorldTexture3dBiomeStructure(boolean doIntern) {
    if (doIntern) {
      live = new LongOpenHashSet();
      interner2d = new StrongInterner<>();
      interner3d = new StrongInterner<>();
    }
  }

  private static long chunkPos(int x, int z) {
    return (((long) x) << 32) | ((long) z);
  }

  public static WorldTexture3dBiomeStructure load(DataInputStream in) throws IOException {
    // Set doIntern to false since we can do it better
    WorldTexture3dBiomeStructure texture = new WorldTexture3dBiomeStructure(false);

    Interner<ChunkTexture> interner2d = new StrongInterner<>();
    Interner<ChunkTexture3d> interner3d = new StrongInterner<>();

    int numTiles = in.readInt();
    for (int i = 0; i < numTiles; i++) {
      int x = in.readInt();
      int z = in.readInt();

      ChunkTexture3d column = ChunkTexture3d.load(in).intern(interner2d, interner3d);
      texture.map.put(chunkPos(x, z), column);
    }

    return texture;
  }

  @Override
  public void store(DataOutputStream out) throws IOException {
    out.writeInt(map.size());
    for (Long2ObjectOpenHashMap.Entry<ChunkTexture3d> entry : map.long2ObjectEntrySet()) {
      long pos = entry.getLongKey();
      ChunkTexture3d texture = entry.getValue();

      int x = (int) (pos >> 32);
      int z = (int) pos;
      out.writeInt(x);
      out.writeInt(z);
      texture.store(out);
    }
  }

  @Override
  public void compact() {
    if (live == null || interner2d == null || interner3d == null) {
      return;
    }

    for (long cp : live) {
      map.computeIfPresent(cp, (k, v) -> v.intern(interner2d, interner3d));
    }
    live.clear();
  }

  @Override
  public void endFinalization() {
    for (Long2ObjectMap.Entry<ChunkTexture3d> entry : map.long2ObjectEntrySet()) {
      ChunkTexture3d value = entry.getValue();
      entry.setValue(value.intern(interner2d, interner3d));
    }

    live = null;
    interner2d = null;
    interner3d = null;
  }

  @Override
  public String biomeFormat() {
    return ID;
  }

  @Override
  public void set(int x, int y, int z, float[] data) {
    long cp = chunkPos(x >> 4, z >> 4);
    ChunkTexture3d texture = map.get(cp);

    if (texture == null) {
      texture = new ChunkTexture3d();
      map.put(cp, texture);
    }

    ChunkTexture3d newTex = texture.set(x & 0xF, y, z & 0xF, data);
    if (newTex != texture) {
      map.put(cp, newTex);
    }

    if (live != null) {
      live.add(cp);
    }
  }

  @Override
  public float[] get(int x, int y, int z) {
    ChunkTexture3d texture = map.get(chunkPos(x >> 4, z >> 4));
    if (texture == null) {
      return null;
    }
    return texture.get(x & 0xF, y, z & 0xF);
  }

  public static class Factory implements BiomeStructure.Factory {

    @Override
    public BiomeStructure create() {
      return new WorldTexture3dBiomeStructure(true);
    }

    @Override
    public BiomeStructure load(@NotNull DataInputStream in) throws IOException {
      return WorldTexture3dBiomeStructure.load(in);
    }

    @Override
    public boolean is3d() {
      return true;
    }

    @Override
    public String getName() {
      return "World Texture 3D";
    }

    @Override
    public String getDescription() {
      return "A 3d biome format that uses de-duplicated bitmaps per chunk.";
    }

    @Override
    public String getId() {
      return ID;
    }
  }
}
