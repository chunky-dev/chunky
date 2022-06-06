package se.llbit.chunky.block.legacy;

import se.llbit.chunky.block.BlockFace;
import se.llbit.chunky.block.FenceGate;
import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.Stairs;
import se.llbit.chunky.block.legacy.blocks.LegacyFenceGate;
import se.llbit.chunky.block.legacy.blocks.LegacyStairs;
import se.llbit.chunky.world.Material;

/**
 * Class containing some utility classes and methods for legacy blocks.
 */
public class LegacyBlockUtils {

  private LegacyBlockUtils() {
  }

  /**
   * Get the name of a block. This will strip the `minecraft:` namespace id and anything before it.
   * * {@code getName("#legacy_minecraft:iron_bars")} -> {@code "iron_bars"}
   */
  public static String getName(Material block) {
    return block.name.substring(block.name.indexOf("minecraft:") + 10);
  }

  public static BlockFace getStairsFacing(Material block) {
    if (block instanceof LegacyStairs) {
      return ((LegacyStairs) block).getFacing();
    } else if (block instanceof Stairs) {
      return ((Stairs) block).getFacing();
    }
    return null;
  }

  public static BlockFace getFenceGateFacing(Material block) {
    if (block instanceof LegacyFenceGate) {
      return ((LegacyFenceGate) block).getFacing();
    } else if (block instanceof FenceGate) {
      return ((FenceGate) block).getFacing();
    }
    return null;
  }

  /**
   * A simple cache for FinalizationState.
   */
  public static class FinalizationStateCache extends FinalizationState {

    private final Material[] cache;
    private final Material[] waterCache;
    private final FinalizationState state;

    public FinalizationStateCache(FinalizationState state) {
      this(state, new Material[27], new Material[27]);
    }

    public FinalizationStateCache(FinalizationState state, Material[] cache, Material[] waterCache) {
      super(state.getPalette());
      this.state = state;
      this.cache = cache;
      this.waterCache = waterCache;
    }

    private static int getCacheIndex(int rx, int ry, int rz) {
      return (rx + 1) + (ry + 1) * 3 + (rz + 1) * 9;
    }

    @Override
    public Material getMaterial() {
      return state.getMaterial();
    }

    @Override
    public Material getMaterial(int rx, int ry, int rz) {
      int index = getCacheIndex(rx, ry, rz);

      if (cache[index] != null) {
        return cache[index];
      }
      cache[index] = state.getMaterial(rx, ry, rz);
      return cache[index];
    }

    @Override
    public Material getWaterMaterial() {
      return state.getWaterMaterial();
    }

    @Override
    public Material getWaterMaterial(int rx, int ry, int rz) {
      int index = getCacheIndex(rx, ry, rz);

      if (waterCache[index] != null) {
        return waterCache[index];
      }
      waterCache[index] = state.getWaterMaterial(rx, ry, rz);
      return waterCache[index];
    }

    @Override
    public void replaceCurrentBlock(int newBlock) {
      state.replaceCurrentBlock(newBlock);
    }

    @Override
    public void replaceCurrentWaterBlock(int newPaletteId) {
      state.replaceCurrentWaterBlock(newPaletteId);
    }

    @Override
    public int getX() {
      return state.getX();
    }

    @Override
    public int getY() {
      return state.getY();
    }

    @Override
    public int getZ() {
      return state.getZ();
    }

    @Override
    public int getYMin() {
      return state.getYMin();
    }

    @Override
    public int getYMax() {
      return state.getYMax();
    }
  }
}
