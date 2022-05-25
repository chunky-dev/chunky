package se.llbit.math.structures;

import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;

public class Position2d2IntPackedArray implements Position2IntStructure {

  protected final Long2ReferenceMap<int[]> structure = new Long2ReferenceOpenHashMap<>();

  private long packedSectionPos(int x, int z) {
    return (z & 0xFFFFFFFFL) | (x & 0xFFFFFFFFL) << 32;
  }

  private int packedIndex(int x, int y, int z) {
    x &= 0xf;
    z &= 0xf;
    return x + 16 * z;
  }

  @Override
  public void set(int x, int y, int z, int data) {
    this.structure.computeIfAbsent(packedSectionPos(x, z), sectionPos -> new int[16 * 16])[packedIndex(x, y, z)] = data;
  }

  @Override
  public int get(int x, int y, int z) {
    int[] ints = this.structure.get(packedSectionPos(x, z));
    if(ints == null) {
      return 0;
    }
    return ints[packedIndex(x, y ,z)];
  }
}