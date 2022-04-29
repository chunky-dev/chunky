package se.llbit.chunky.renderer.scene.biome;

import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import se.llbit.math.structures.Position2d2ReferencePackedArrayStructure;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;

public class Trivial2dBiomeStructureImpl extends Position2d2ReferencePackedArrayStructure<float[]> implements BiomeStructure {

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
