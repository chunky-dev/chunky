package se.llbit.chunky.renderer.scene.biome;

import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import se.llbit.math.structures.Position3d2ReferencePackedArrayStructure;
import se.llbit.util.annotation.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;

public class Trivial3dBiomeStructure implements BiomeStructure.Factory {

  @Override
  public BiomeStructure create() {
    return new Impl();
  }

  @Override
  public BiomeStructure load(@NotNull DataInputStream in) throws IOException {
    /*
     * Stored as:
     * (int) size
     * (int) x, y, z
     * (long) Length of present bitset in longs
     * (BitSet as longs) Present values bitset
     * (int) number of values stored
     * (float[][]) The internal data of each packed x,y,z position
     */

    Impl impl = new Impl();
    int size = in.readInt();
    for (int i = 0; i < size; i++) {
      int x = in.readInt();
      int y = in.readInt();
      int z = in.readInt();

      long[] longs = new long[in.readInt()];
      for (int bitsetIdx = 0; bitsetIdx < longs.length; bitsetIdx++) {
        longs[bitsetIdx] = in.readLong();
      }

      BitSet presentValues = BitSet.valueOf(longs);

      int count = in.readInt();
      float[][] floats = new float[count][];
      for (int idx = 0; idx < count; idx++) {
        if (presentValues.get(idx)) {
          float[] farray = new float[3];
          farray[0] = in.readFloat();
          farray[1] = in.readFloat();
          farray[2] = in.readFloat();
          floats[idx] = farray;
        }
      }
      impl.setCube(x, y, z, floats);
    }
    return impl;
  }

  @Override
  public boolean is3d() {
    return true;
  }

  static class Impl extends Position3d2ReferencePackedArrayStructure<float[]> implements BiomeStructure {

    public void setCube(int x, int y, int z, float[][] data) {
      this.map.put(new XYZTriple(x, y, z), data);
    }

    @Override
    public void store(DataOutputStream out) throws IOException {
      out.writeInt(this.map.size());
      for (Object2ReferenceMap.Entry<XYZTriple, float[][]> entry : this.map.object2ReferenceEntrySet()) {
        XYZTriple key = entry.getKey();
        out.writeInt(key.x);
        out.writeInt(key.y);
        out.writeInt(key.z);
        Object[] value = entry.getValue();

        BitSet presentValues = new BitSet(value.length);
        for (int i = 0, valueLength = value.length; i < valueLength; i++) {
          presentValues.set(i, value[i] != null);
        }
        long[] longs = presentValues.toLongArray();
        out.writeInt(longs.length);
        for (long l : longs) {
          out.writeLong(l);
        }

        out.writeInt(value.length);
        for (Object o : value) {
          if (o != null) {
            for (float f : (float[]) o) {
              out.writeFloat(f);
            }
          }
        }
      }
    }

    @Override
    public String biomeFormat() {
      return "TRIVIAL_3D";
    }

    @Override
    public void compact() {

    }
  }
}
