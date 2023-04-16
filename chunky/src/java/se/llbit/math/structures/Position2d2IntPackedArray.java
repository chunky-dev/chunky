package se.llbit.math.structures;

import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;

public class Position2d2IntPackedArray implements Position2IntStructure {

  protected final Long2ReferenceMap<int[]> structure = new Long2ReferenceOpenHashMap<>();

  private long packedSectionPos(int cx, int cz) {
    return (cz & 0xFFFFFFFFL) | (cx & 0xFFFFFFFFL) << 32;
  }

  private int packedIndex(int x, int y, int z) {
    x &= 0xf;
    z &= 0xf;
    return x + 16 * z;
  }

  @Override
  public void set(int x, int y, int z, int data) {
    int cx = x >> 4;
    int cz = z >> 4;
    long sp = packedSectionPos(cx, cz);
    this.structure.computeIfAbsent(sp, sectionPos -> new int[16 * 16])[packedIndex(x, y, z)] = data;
  }

  @Override
  public int get(int x, int y, int z) {
    int cx = x >> 4;
    int cz = z >> 4;
    long sp = packedSectionPos(cx, cz);
    int[] ints = this.structure.get(sp);
    if(ints == null) {
      return 0;
    }
    return ints[packedIndex(x, y ,z)];
  }
}