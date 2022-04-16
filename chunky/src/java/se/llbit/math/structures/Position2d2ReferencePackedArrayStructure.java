package se.llbit.math.structures;

import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;

public class Position2d2ReferencePackedArrayStructure<T> implements Position2ReferenceStructure<T> {

  private final Long2ReferenceMap<T[]> map = new Long2ReferenceOpenHashMap<>();

  private long packedSectionPos(int x, int z) {
    return (x >> 4) | ((long) (z >> 4) << 32);
  }

  private int packedIndex(int x, int y, int z) {
    x &= 0xf;
    z &= 0xf;
    return x + 16 * z;
  }

  @Override
  public void set(int x, int y, int z, T data) {
    this.map.computeIfAbsent(packedSectionPos(x, z), sectionPos -> (T[]) new Object[16 * 16])[packedIndex(x, y, z)] = data;
  }

  @Override
  public T get(int x, int y, int z) {
    T[] tArray = this.map.get(packedSectionPos(x, z));
    if(tArray == null) {
      return null;
    }
    T t = tArray[packedIndex(x, y, z)];
    if(t == null) {
      int asd = 0;
    }
    return t;
  }

  @Override
  public void compact() {

  }
}
