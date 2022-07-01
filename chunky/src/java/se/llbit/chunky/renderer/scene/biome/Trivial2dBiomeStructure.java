package se.llbit.chunky.renderer.scene.biome;

import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import se.llbit.math.structures.Position2d2ReferencePackedArrayStructure;
import se.llbit.util.annotation.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;

public class Trivial2dBiomeStructure implements BiomeStructure.Factory {
  @Override
  public BiomeStructure create() {
    return new Impl();
  }

  /**
   * Stored as:
   * (int) size
   * (int) long packed key
   * (long) Length of present bitset in longs
   * (BitSet as longs) Present values bitset
   * (int) number of values stored
   * (float[][]) The internal data of each packed x,z position
   */
  @Override
  public BiomeStructure load(@NotNull DataInputStream in) throws IOException {
    Impl impl = new Impl();
    int size = in.readInt();
    for (int i = 0; i < size; i++) {
      long key = in.readLong();

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
      impl.setCube(key, floats);
    }
    return impl;
  }

  @Override
  public boolean is3d() {
    return false;
  }

  static class Impl extends Position2d2ReferencePackedArrayStructure<float[]> implements BiomeStructure {

    public void setCube(long packedPosition, float[][] data) {
      this.map.put(packedPosition, data);
    }

    @Override
    public void store(DataOutputStream out) throws IOException {
      out.writeInt(this.map.size());
      for (Long2ReferenceMap.Entry<float[][]> entry : this.map.long2ReferenceEntrySet()) {
        out.writeLong(entry.getLongKey());
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
      return "TRIVIAL_2D";
    }

    @Override
    public void compact() {
    }
  }
}
