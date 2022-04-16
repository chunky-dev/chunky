package se.llbit.math.structures;

import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;

import java.util.Objects;

public class Position3d2ReferencePackedArrayStructure<T> implements Position2ReferenceStructure<T> {

  private final Object2ReferenceMap<XYZTriple, T[]> map = new Object2ReferenceOpenHashMap<>();

  private int packedIndex(int x, int y, int z) {
    x &= 0xf;
    y &= 0xf;
    z &= 0xf;
    return x + 16 * (y + 16 * z);
  }
  @Override
  public void set(int x, int y, int z, T data) {
    this.map.computeIfAbsent(new XYZTriple(x >> 4, y >> 4, z >> 4), sectionPos -> (T[]) new Object[16 * 16 * 16])[packedIndex(x, y, z)] = data;
  }

  @Override
  public T get(int x, int y, int z) {
    return this.map.get(new XYZTriple(x >> 4, y >> 4, z >> 4))[packedIndex(x, y, z)];
  }

  @Override
  public void compact() {

  }

  private static class XYZTriple {
    private final int x, y, z;

    private XYZTriple(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      XYZTriple xyzTriple = (XYZTriple) o;
      return x == xyzTriple.x && y == xyzTriple.y && z == xyzTriple.z;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y, z);
    }
  }
}