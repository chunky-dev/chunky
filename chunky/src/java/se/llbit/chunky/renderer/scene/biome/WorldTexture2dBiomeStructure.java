package se.llbit.chunky.renderer.scene.biome;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import se.llbit.chunky.world.WorldTexture;
import se.llbit.math.structures.Position2IntStructure;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WorldTexture2dBiomeStructure implements BiomeStructure.Factory {
  static final String ID = "WORLD_TEXTURE_2D";

  @Override
  public BiomeStructure create() {
    return new Impl(new WorldTexture());
  }

  @Override
  public BiomeStructure load(DataInputStream in) throws IOException {
    return new Impl(WorldTexture.load(in));
  }

  @Override
  public Position2IntStructure createIndexStructure() {
    return new StructureImpl();
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

  static class Impl implements BiomeStructure {
    private final WorldTexture texture;

    public Impl(WorldTexture texture) {
      this.texture = texture;
    }

    @Override
    public void store(DataOutputStream out) throws IOException {
      texture.store(out);
    }

    @Override
    public void compact() {
      texture.compact();
    }

    @Override
    public String biomeFormat() {
      return ID;
    }

    @Override
    public void set(int x, int y, int z, float[] data) {
      texture.set(x, z, data);
    }

    @Override
    public float[] get(int x, int y, int z) {
      return texture.get(x, z);
    }
  }

  private static class StructureImpl implements Position2IntStructure {
    private final Long2IntMap biomeMap = new Long2IntOpenHashMap();

    @Override
    public void set(int x, int y, int z, int data) {
      long key = ((long) x >> 4) << 32 | ((z >> 4) & 0xffffffffL);
      biomeMap.put(key, data);
    }

    @Override
    public int get(int x, int y, int z) {
      long key = ((long) x >> 4) << 32 | ((z >> 4) & 0xffffffffL);
      return biomeMap.get(key);
    }
  }
}
