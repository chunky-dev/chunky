package se.llbit.chunky.renderer.scene.biome;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.WorldTexture;
import se.llbit.math.structures.Position2ReferenceStructure;
import se.llbit.util.annotation.NotNull;
import se.llbit.util.annotation.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.BitSet;
import java.util.Map;

public interface BiomeStructure extends Position2ReferenceStructure<float[]> {
  Map<String, Builder> REGISTRY = new Object2ReferenceOpenHashMap<>();

  static void registerDefaults() {
    //TODO: create a plugin api interface for registering implementations, and move this to that
    BiomeStructure.register("TRIVIAL_3D", new BiomeStructure.Builder() {
      @Override
      public BiomeStructure create() {
        return new Trivial3dBiomeStructureImpl();
      }

      @Override
      public BiomeStructure load(@NotNull DataInputStream in) throws IOException {
        /**
         * Stored as:
         * (int) size
         * (int) x, y, z
         * (long) Length of present bitset in longs
         * (BitSet as longs) Present values bitset
         * (int) number of values stored
         * (float[][]) The internal data of each packed x,y,z position
         */

        Trivial3dBiomeStructureImpl impl = new Trivial3dBiomeStructureImpl();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
          int x = in.readInt();
          int y = in.readInt();
          int z = in.readInt();

          long[] longs = new long[in.readInt()];
          for (int bitsetIdx = 0; bitsetIdx < longs.length; bitsetIdx++) {
            longs[bitsetIdx] = in.readLong();
          }

          BitSet presentValues = BitSet.valueOf(longs);

          int count = in.readInt();
          float[][] floats = new float[count][];
          for (int idx = 0; idx < count; idx++) {
            if(presentValues.get(idx)) {
              float[] farray = new float[3];
              farray[0] = in.readFloat();
              farray[1] = in.readFloat();
              farray[2] = in.readFloat();
              floats[idx] = farray;
            }
          }
          impl.setCube(x, y, z, floats);
        }
        return impl;
      }
    });

    BiomeStructure.register("TRIVIAL_2D", new BiomeStructure.Builder() {
      @Override
      public BiomeStructure create() {
        return new Trivial2dBiomeStructureImpl();
      }

      /**
       * Stored as:
       * (int) size
       * (int) long packed key
       * (long) Length of present bitset in longs
       * (BitSet as longs) Present values bitset
       * (int) number of values stored
       * (float[][]) The internal data of each packed x,z position
       */
      @Override
      public BiomeStructure load(@NotNull DataInputStream in) throws IOException {
        Trivial2dBiomeStructureImpl impl = new Trivial2dBiomeStructureImpl();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
          long key = in.readLong();

          long[] longs = new long[in.readInt()];
          for (int bitsetIdx = 0; bitsetIdx < longs.length; bitsetIdx++) {
            longs[bitsetIdx] = in.readLong();
          }

          BitSet presentValues = BitSet.valueOf(longs);

          int count = in.readInt();
          float[][] floats = new float[count][];
          for (int idx = 0; idx < count; idx++) {
            if(presentValues.get(idx)) {
              float[] farray = new float[3];
              farray[0] = in.readFloat();
              farray[1] = in.readFloat();
              farray[2] = in.readFloat();
              floats[idx] = farray;
            }
          }
          impl.setCube(key, floats);
        }
        return impl;
      }
    });
  }

  /**
   * This is basically a reimplementation of {@link WorldTexture#load} but instead loading into an arbitrary
   * BiomeStructure implementation
   *
   * @param impl The implementation to load the legacy implementation into
   * @param in The serialised legacy data in an input stream
   * @return The newly constructed {@link BiomeStructure} of the specified implementation
   */
  static BiomeStructure loadLegacy(String impl, DataInputStream in) {
    BiomeStructure biomeStructure = REGISTRY.get(impl).create();
    try {
      int numTiles = in.readInt();
      for (int i = 0; i < numTiles; ++i) {
        int chunkX = in.readInt();
        int chunkZ = in.readInt();
        for (int localX = 0; localX < Chunk.X_MAX; localX++) {
          for (int localZ = 0; localZ < Chunk.Z_MAX; localZ++) {
            biomeStructure.set(chunkX * Chunk.X_MAX + localX, 0, chunkZ * Chunk.Z_MAX + localZ, new float[] {
              in.readFloat(),
              in.readFloat(),
              in.readFloat()
            });
          }
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException("Could not parse legacy WorldTexture", e);
    }
    biomeStructure.compact();
    return biomeStructure;
  }

  /**
   * Register a new {@link BiomeStructure.Builder}
   * @param key The key to register the builder under <b>(MUST BE UNIQUE)</b>
   * @return Whether the supplied builder overwrote an existing one
   */
  static boolean register(@NotNull String key, @NotNull Builder builder) {
    return REGISTRY.put(key, builder) != null;
  }

  /**
   * @return Get the specified implementation from the registry
   */
  @Nullable
  static Builder get(@NotNull String key) {
    return REGISTRY.get(key);
  }

  /**
   * Store the {@link BiomeStructure} to a data output stream
   */
  void store(DataOutputStream out) throws IOException;

  /**
   * This method is called to tell the implementation to shrink its size. (Node-tree optimisation, etc.)
   * Called when throughout insertion of new biomes, and on completion
   */
  void compact();

  /**
   * @return The registry key this biome format uses. Must be unique
   */
  String biomeFormat();

  interface Builder {
    /**
     * Create an empty {@link BiomeStructure} for loading a new scene
     */
    BiomeStructure create();

    /**
     * Load a saved {@link BiomeStructure} of this implementation from a {@link DataInputStream}
     */
    BiomeStructure load(@NotNull DataInputStream in) throws IOException;
  }
}
