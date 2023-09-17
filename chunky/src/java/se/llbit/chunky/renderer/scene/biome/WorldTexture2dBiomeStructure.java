package se.llbit.chunky.renderer.scene.biome;

import se.llbit.chunky.world.WorldTexture;

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
    public void endFinalization() {
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
}
