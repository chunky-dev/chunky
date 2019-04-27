package se.llbit.chunky.resources;

import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.math.Octree;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OctreeLoader {
  private static final int OCTREE_VERSION = 2;
  public Octree octree;
  public BlockPalette palette;

  /**
   * Deserialize the octree from a data input stream.
   *
   * @return The deserialized octree
   * @throws IOException
   */
  public void load(DataInputStream in) throws IOException {
    int version = in.readInt();
    if (version != OCTREE_VERSION) {
      throw new IOException("Incompatible octree format.");
    }
    palette = BlockPalette.read(in);
    int treeDepth = in.readInt();
    octree = Octree.load(in);
  }

  /**
   * Serialize this octree to a data output stream.
   *
   * @throws IOException
   */
  public static void store(DataOutputStream out, Octree octree, BlockPalette palette)
      throws IOException {
    out.writeInt(OCTREE_VERSION);
    palette.write(out);
    octree.store(out);
  }

}
