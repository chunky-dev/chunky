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

import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.world.WorldTexture;
import se.llbit.math.Octree;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OctreeFileFormat {
  private static final int MIN_OCTREE_VERSION = 3;
  private static final int OCTREE_VERSION = 4;

  /**
   * Load octrees and grass/foliage textures from a file.
   *
   * @param in input stream for the file to load the scene from.
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
    data.worldTree = Octree.load(impl, in);
    data.waterTree = Octree.load(impl, in);
    data.grassColors = WorldTexture.load(in);
    data.foliageColors = WorldTexture.load(in);
    if (version >= 4) {
      data.waterColors = WorldTexture.load(in);
    }
    return data;
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
  }
}
