package se.llbit.chunky.block.legacy;

import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.world.Material;

import java.util.Arrays;

/**
 * Class containing some utility classes and methods for legacy blocks.
 */
public class LegacyBlockUtils {
  private LegacyBlockUtils() {}

  /**
   * Get the name of a block. This will strip the `minecraft:` namespace id and anything before it.
   *  * {@code getName("#legacy_minecraft:iron_bars")} -> {@code "iron_bars"}
   */
  public static String getName(Material block) {
    return block.name.substring(block.name.indexOf("minecraft:")+10);
  }

  /**
   * A simple cache for FinalizationState.
   */
  public static class FinalizationStateCache extends FinalizationState {
    private final Material[] cache;
    private final FinalizationState state;

    public FinalizationStateCache(FinalizationState state) {
      this(state, new Material[27]);
    }

    public FinalizationStateCache(FinalizationState state, Material[] cache) {
      super(state.getPalette());
      this.state = state;
      this.cache = cache;
    }

    private static int getCacheIndex(int rx, int ry, int rz) {
      return (rx + 1) + (ry + 1)*3 + (ry + 1)*9;
    }

    @Override
    public Material getMaterial() {
      return state.getMaterial();
    }

    @Override
    public Material getMaterial(int rx, int ry, int rz) {
      int index = getCacheIndex(rx, ry, rz);

      if (cache[index] != null) return cache[index];
      cache[index] = state.getMaterial(rx, ry, rz);
      return cache[index];
    }

    @Override
    public void replaceCurrentBlock(int newBlock) {
      state.replaceCurrentBlock(newBlock);
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
