package se.llbit.chunky.renderer.scene.biome;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;

/**
 * Holds information relative to biome in a a chunk to help with biome blending at load time
 */
public class ChunkBiomeBlendingHelper {
  /// List of the y values at which a vertical biome transition occurs.
  /// A value of y means that the biome between height y-1 and height y is different
  /// (if null, no transition vertical biome transition exist for this chunk)
  /// Should stay sorted, but not checked by this class itself
  private IntArrayList transitions;
  /// The interval of y values for which the biome is relevant
  /// ie some block tint depend on the biome
  /// (both inclusive)
  private int yMinBiomeRelevant = Integer.MAX_VALUE;
  private int yMaxBiomeRelevant = Integer.MIN_VALUE;

  /**
   * Add a transition. Transitions should be inserted in sorted order
   * @param y The transition to add
   */
  public void addTransition(int y) {
    if(transitions == null)
      transitions = new IntArrayList();
    if(transitions.size() > 0 && transitions.getInt(transitions.size() - 1) == y)
      return;
    transitions.add(y);
  }

  public void makeBiomeRelevant(int y) {
    yMinBiomeRelevant = Math.min(yMinBiomeRelevant, y);
    yMaxBiomeRelevant = Math.max(yMaxBiomeRelevant, y);
  }

  public int[] combineAndTrimTransitions(ChunkBiomeBlendingHelper[] neighboringChunks, int blurRadius) {
    // merge sorted arrays and deduplication
    // Simple implementation for, new probably enough even later
    IntSortedSet set = new IntRBTreeSet();
    if(transitions != null) {
      for(int y : transitions) {
        if(y > yMinBiomeRelevant - blurRadius && y <= yMaxBiomeRelevant + blurRadius)
          set.add(y);
      }
    }
    for(ChunkBiomeBlendingHelper other : neighboringChunks) {
      if(other != null && other.transitions != null) {
        for(int y : other.transitions){
          if(y > yMinBiomeRelevant - blurRadius && y <= yMaxBiomeRelevant + blurRadius)
            set.add(y);
        }
      }
    }
    return set.toIntArray(); // sorted merge and deduplication done by set
  }

  public int getyMinBiomeRelevant() {
    return yMinBiomeRelevant;
  }

  public int getyMaxBiomeRelevant() {
    return yMaxBiomeRelevant;
  }
}
