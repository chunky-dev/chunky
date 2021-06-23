/* Copyright (c) 2019 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.chunky.resources;

import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.Lava;
import se.llbit.chunky.block.Water;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.world.WorldTexture;
import se.llbit.log.Log;
import se.llbit.math.Octree;

public class OctreeFileFormat {

  private static final int MIN_OCTREE_VERSION = 3;
  private static final int OCTREE_VERSION = 6;

  /**
   * In octree v3-v4, the top bit of the type field in a serialized octree node is reserved for
   * indicating if the node is a data node.
   */
  private static final int DATA_FLAG = 0x80000000;

  /**
   * Load octrees and grass/foliage textures from a file.
   *
   * @param in   input stream for the file to load the scene from.
   * @param impl The octree implementation to use
   */
  public static OctreeData load(DataInputStream in, String impl) throws IOException {
    int version = in.readInt();
    if (version < MIN_OCTREE_VERSION || version > OCTREE_VERSION) {
      throw new IOException(String.format(
          "Incompatible octree format: wrong version number (expected %d up to %d, was %d).",
          MIN_OCTREE_VERSION, OCTREE_VERSION, version));
    }
    OctreeData data = new OctreeData();
    data.palette = BlockPalette.read(in);
    data.worldTree = Octree.load(impl, version < 5 ? convertDataNodes(data.palette, in) : in);
    data.waterTree = Octree.load(impl, version < 5 ? convertDataNodes(data.palette, in) : in);
    data.grassColors = WorldTexture.load(in);
    data.foliageColors = WorldTexture.load(in);
    if (version >= 4) {
      data.waterColors = WorldTexture.load(in);
    }
    data.version = version;
    return data;
  }

  /**
   * This converts a v3-v4 octree to v5 while loading it. In v5, data nodes (only used for water and
   * lava) were replaced by new per-variant types.
   *
   * @param palette Block palette for the octree, new block variants for water and lava will be
   *                added to it
   * @param in      Input stream of a v3 or v4 octree
   * @return Input stream of a v5 octree
   * @throws IOException If reading or writing a streams fails
   * @see <a href="https://github.com/chunky-dev/chunky/pull/704">PR #704</a>
   */
  private static DataInputStream convertDataNodes(BlockPalette palette, final DataInputStream in)
      throws IOException {
    final PipedOutputStream pipedOut = new PipedOutputStream();
    PipedInputStream resultingStream = new PipedInputStream(pipedOut);
    Thread convertThread = new Thread(() -> {
      try (DataOutputStream out = new DataOutputStream(new FastBufferedOutputStream(pipedOut))) {
        out.writeInt(in.readInt()); // depth

        long remainingNodes = 1;
        while (remainingNodes > 0) {
          int type = in.readInt();
          remainingNodes--;
          if (type == Octree.BRANCH_NODE) {
            out.writeInt(type);
            remainingNodes += 8;
          } else {
            if ((type & DATA_FLAG) == 0) {
              out.writeInt(type);
            } else {
              int typeOnly = type ^ DATA_FLAG;
              int data = in.readInt();
              Block block = palette.get(typeOnly);
              if (block instanceof Water) {
                out.writeInt(palette.getWaterId(((Water) block).level, data));
              } else if (block instanceof Lava) {
                out.writeInt(palette.getWaterId(((Lava) block).level, data));
              } else {
                out.writeInt(typeOnly);
              }
            }
          }
        }
      } catch (IOException e) {
        Log.error("Octree conversion failed", e);
      }
    });
    convertThread.setDaemon(true);
    convertThread.start();
    return new DataInputStream(resultingStream);
  }

  /**
   * Save octrees and grass/foliage/water textures to a file.
   */
  public static void store(DataOutputStream out, Octree octree,
      Octree waterTree, BlockPalette palette,
      WorldTexture grassColors, WorldTexture foliageColors, WorldTexture waterColors)
      throws IOException {
    out.writeInt(OCTREE_VERSION);
    palette.write(out);
    octree.store(out);
    waterTree.store(out);
    grassColors.store(out);
    foliageColors.store(out);
    waterColors.store(out);
  }

  public static class OctreeData {

    public Octree worldTree, waterTree;
    public WorldTexture grassColors, foliageColors, waterColors;
    public BlockPalette palette;
    public int version;
  }
}
