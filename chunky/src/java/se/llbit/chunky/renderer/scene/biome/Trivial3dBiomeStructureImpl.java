package se.llbit.chunky.renderer.scene.biome;

import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import se.llbit.math.structures.Position3d2ReferencePackedArrayStructure;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;

public class Trivial3dBiomeStructureImpl extends Position3d2ReferencePackedArrayStructure<float[]> implements BiomeStructure {

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
      out.writeLong(key.z);
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
