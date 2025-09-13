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
import se.llbit.chunky.renderer.scene.biome.BiomeStructure;
import se.llbit.util.annotation.NotNull;
import se.llbit.util.interner.Interner;
import se.llbit.util.interner.StrongInterner;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WorldTexture2dBiomeStructure implements BiomeStructure {
  public static final String ID = "WORLD_TEXTURE_2D";

  private final Long2ObjectOpenHashMap<ChunkTexture> map = new Long2ObjectOpenHashMap<>();

  private static long chunkPos(int x, int z) {
    return (((long) x) << 32) | ((long) z);
  }

  public static WorldTexture2dBiomeStructure load(DataInputStream in) throws IOException {
    WorldTexture2dBiomeStructure texture = new WorldTexture2dBiomeStructure();
    Interner<ChunkTexture> interner = new StrongInterner<>();

    int numTiles = in.readInt();
    for (int i = 0; i < numTiles; i++) {
      int x = in.readInt();
      int z = in.readInt();
      ChunkTexture tile = ChunkTexture.load(in).intern(interner);
      texture.map.put(chunkPos(x, z), tile);
    }

    return texture;
  }

  @Override
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

  @Override
  public void endFinalization() {
    Interner<ChunkTexture> interner = new StrongInterner<>();
    for (Long2ObjectMap.Entry<ChunkTexture> entry : map.long2ObjectEntrySet()) {
      entry.setValue(entry.getValue().intern(interner));
    }
  }

  @Override
  public String biomeFormat() {
    return ID;
  }

  @Override
  public void set(int x, int y, int z, float[] data) {
    long cp = chunkPos(x >> 4, z >> 4);
    ChunkTexture texture = map.get(cp);

    if (texture == null) {
      texture = new ChunkTexture();
      map.put(cp, texture);
    }

    ChunkTexture newTex = texture.set(x & 0xF, z & 0xF, data);
    if (newTex != texture) {
      map.put(cp, newTex);
    }
  }

  @Override
  public float[] get(int x, int y, int z) {
    ChunkTexture texture = map.get(chunkPos(x >> 4, z >> 4));
    if (texture == null) {
      return null;
    }
    return texture.get(x & 0xF, z & 0xF);
  }


  public static class Factory implements BiomeStructure.Factory {
    @Override
    public BiomeStructure create() {
      return new WorldTexture2dBiomeStructure();
    }

    @Override
    public BiomeStructure load(@NotNull DataInputStream in) throws IOException {
      return WorldTexture2dBiomeStructure.load(in);
    }

    @Override
    public boolean is3d() {
      return false;
    }

    @Override
    public String getName() {
      return "World texture 2d";
    }

    @Override
    public String getDescription() {
      return "A 2d biome format that uses de-duplicated bitmaps per chunk.";
    }

    @Override
    public String getId() {
      return ID;
    }
  }
}
