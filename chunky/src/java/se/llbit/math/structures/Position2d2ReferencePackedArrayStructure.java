package se.llbit.math.structures;

import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;

public class Position2d2ReferencePackedArrayStructure<T> implements Position2ReferenceStructure<T> {

  protected final Long2ReferenceMap<T[]> map = new Long2ReferenceOpenHashMap<>();

  private long packedSectionPos(int cx, int cz) {
    return (cz & 0xFFFFFFFFL) | (cx & 0xFFFFFFFFL) << 32;
  }

  private int packedIndex(int x, int y, int z) {
    x &= 0xf;
    z &= 0xf;
    return x + 16 * z;
  }

  @Override
  public void set(int x, int y, int z, T data) {
    int cx = x >> 4;
    int cz = z >> 4;
    long sp = packedSectionPos(cx, cz);
    this.map.computeIfAbsent(sp, sectionPos -> (T[]) new Object[16 * 16])[packedIndex(x, y, z)] = data;
  }

  @Override
  public T get(int x, int y, int z) {
    int cx = x >> 4;
    int cz = z >> 4;
    long sp = packedSectionPos(cx, cz);
    T[] tArray = this.map.get(sp);
    if(tArray == null) {
      return null;
    }
    T t = tArray[packedIndex(x, y, z)];
    return t;
  }
}
