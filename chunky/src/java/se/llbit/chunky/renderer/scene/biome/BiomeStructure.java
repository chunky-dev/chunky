package se.llbit.chunky.renderer.scene.biome;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.WorldTexture;
import se.llbit.log.Log;
import se.llbit.math.structures.Position2IntStructure;
import se.llbit.math.structures.Position2ReferenceStructure;
import se.llbit.math.structures.Position2d2IntPackedArray;
import se.llbit.math.structures.Position3d2IntPackedArray;
import se.llbit.util.Registerable;
import se.llbit.util.annotation.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public interface BiomeStructure extends Position2ReferenceStructure<float[]> {
  Map<String, Factory> REGISTRY = new Object2ReferenceOpenHashMap<>();
  String DEFAULT_IMPLEMENTATION = WorldTexture2dBiomeStructure.ID;

  static void registerDefaults() {
    //TODO: create a plugin api interface for registering implementations, and move this to that
    BiomeStructure.register(new Trivial3dBiomeStructure());
    BiomeStructure.register(new Trivial2dBiomeStructure());
    BiomeStructure.register(new WorldTexture2dBiomeStructure());
  }

  /**
   * This is basically a reimplementation of {@link WorldTexture#load} but instead loading into an arbitrary
   * BiomeStructure implementation
   *
   * @param impl The implementation to load the legacy implementation into
   * @param in   The serialised legacy data in an input stream
   * @return The newly constructed {@link BiomeStructure} of the specified implementation
   */
  static BiomeStructure loadLegacy(Factory impl, DataInputStream in) throws IOException {
    BiomeStructure biomeStructure = impl.create();
    int numTiles = in.readInt();
    for (int i = 0; i < numTiles; ++i) {
      int chunkX = in.readInt();
      int chunkZ = in.readInt();
      for (int localX = 0; localX < Chunk.X_MAX; localX++) {
        for (int localZ = 0; localZ < Chunk.Z_MAX; localZ++) {
          biomeStructure.set(chunkX * Chunk.X_MAX + localX, 0, chunkZ * Chunk.Z_MAX + localZ, new float[]{
            in.readFloat(),
            in.readFloat(),
            in.readFloat()
          });
        }
      }
    }
    biomeStructure.compact();
    return biomeStructure;
  }

  /**
   * Register a new {@link Factory}
   *
   * @return Whether the supplied factory overwrote an existing one
   */
  static boolean register(@NotNull Factory factory) {
    return REGISTRY.put(factory.getId(), factory) != null;
  }

  /**
   * @return Get the specified implementation from the registry, if it doesn't exist, the default implementation is
   * returned
   */
  @NotNull
  static Factory get(@NotNull String key) throws NullPointerException {
    Factory factory = REGISTRY.get(key);
    if (factory == null) {
      Log.warnf("Implementation %s does not exist, using the default: %s", key, DEFAULT_IMPLEMENTATION);
      return REGISTRY.get(DEFAULT_IMPLEMENTATION);
    }
    return factory;
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

  interface Factory extends Registerable {
    /**
     * Create an empty {@link BiomeStructure} for loading a new scene
     */
    BiomeStructure create();

    /**
     * Create an empty {@link Position2IntStructure} for the biome palette indices
     * (used only for biome blending, does not need to be saved)
     */
    default Position2IntStructure createIndexStructure() {
      if (is3d()) {
        return new Position3d2IntPackedArray();
      } else {
        return new Position2d2IntPackedArray();
      }
    }

    /**
     * Load a saved {@link BiomeStructure} of this implementation from a {@link DataInputStream}
     */
    BiomeStructure load(@NotNull DataInputStream in) throws IOException;

    /**
     * @return Whether the implementation created is 3d
     */
    boolean is3d();
  }
}
