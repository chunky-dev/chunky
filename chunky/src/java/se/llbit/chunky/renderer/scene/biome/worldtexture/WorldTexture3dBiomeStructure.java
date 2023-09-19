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
import se.llbit.util.interner.WeakInterner;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WorldTexture3dBiomeStructure implements BiomeStructure {
  public static final String ID = "WORLD_TEXTURE_3D";

  private final Long2ObjectOpenHashMap<ChunkTexture3d> map = new Long2ObjectOpenHashMap<>();

  private static long chunkPos(int x, int z) {
    return (((long) x) << 32) | ((long) z);
  }

  public static WorldTexture3dBiomeStructure load(DataInputStream in) throws IOException {
    WorldTexture3dBiomeStructure texture = new WorldTexture3dBiomeStructure();
    Interner<ChunkTexture3d> ct3dInterner = new StrongInterner<>();
    Interner<ChunkTexture> ct2dInterner = new StrongInterner<>();

    int numTiles = in.readInt();
    for (int i = 0; i < numTiles; i++) {
      int x = in.readInt();
      int z = in.readInt();

      ChunkTexture3d column = ChunkTexture3d.load(in, ct2dInterner);
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
  public void endFinalization() {
    Interner<ChunkTexture3d> interner3D = new StrongInterner<>();
    Interner<ChunkTexture> interner2D = new StrongInterner<>();
    for (Long2ObjectMap.Entry<ChunkTexture3d> entry : map.long2ObjectEntrySet()) {
      ChunkTexture3d value = entry.getValue();
      value.makeReadOnly();

      ChunkTexture3d interned = interner3D.maybeIntern(value);
      if (interned != null) {
        entry.setValue(interned);
      } else {
        value.compact(interner2D);
      }
    }
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
      return new WorldTexture3dBiomeStructure();
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
