package se.llbit.math.structures;

import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;

import java.util.Objects;

public class Position3d2IntPackedArray implements Position2IntStructure {

  protected final Object2ReferenceMap<XYZTriple, int[]> structure = new Object2ReferenceOpenHashMap<>();

  protected int lastX, lastY, lastZ;
  protected int[] lastData = null;

  private int packedIndex(int x, int y, int z) {
    x &= 0xf;
    y &= 0xf;
    z &= 0xf;
    return x + 16 * (y + 16 * z);
  }

  @Override
  public void set(int x, int y, int z, int data) {
    int xSection = x >> 4;
    int ySection = y >> 4;
    int zSection = z >> 4;
    int[] arr;
    if(xSection == this.lastX && ySection == this.lastY && zSection == this.lastZ) {
      arr = this.lastData;
    } else {
      arr = this.structure.computeIfAbsent(new XYZTriple(xSection, ySection, zSection), sectionPos -> new int[16 * 16 * 16]);
      this.lastX = xSection;
      this.lastY = ySection;
      this.lastZ = zSection;
      this.lastData = arr;
    }
    if(arr != null) {
      arr[packedIndex(x, y, z)] = data;
    }
  }

  @Override
  public int get(int x, int y, int z) {
    int xSection = x >> 4;
    int ySection = y >> 4;
    int zSection = z >> 4;
    int[] arr;
    if(xSection == this.lastX && ySection == this.lastY && zSection == this.lastZ) {
      arr = this.lastData;
    } else {
      arr = this.structure.computeIfAbsent(new XYZTriple(xSection, ySection, zSection), sectionPos -> new int[16 * 16 * 16]);
      this.lastX = xSection;
      this.lastY = ySection;
      this.lastZ = zSection;
      this.lastData = arr;
    }
    if(arr != null) {
      return arr[packedIndex(x, y, z)];
    }
    return 0;
  }

  protected static class XYZTriple {
    protected final int x, y, z;

    protected XYZTriple(int x, int y, int z) {
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
