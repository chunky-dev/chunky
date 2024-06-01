/*
 * Copyright (c) 2012-2023 Chunky contributors
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
package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.model.Tint;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.ArrayList;
import java.util.Arrays;

public class VineModel extends QuadModel {
  private static final Quad[] model = {
      // North
      new Quad(new Vector3(0, 0, 0.8 / 16), new Vector3(1, 0, 0.8 / 16),
          new Vector3(0, 1, 0.8 / 16), new Vector4(0, 1, 0, 1), true),

      // South
      new Quad(new Vector3(1, 0, 15.2 / 16), new Vector3(0, 0, 15.2 / 16),
          new Vector3(1, 1, 15.2 / 16), new Vector4(1, 0, 0, 1), true),

      // East
      new Quad(new Vector3(15.2 / 16, 0, 0), new Vector3(15.2 / 16, 0, 1),
          new Vector3(15.2 / 16, 1, 0), new Vector4(0, 1, 0, 1), true),

      // West
      new Quad(new Vector3(0.8 / 16, 0, 1), new Vector3(0.8 / 16, 0, 0),
          new Vector3(0.8 / 16, 1, 1), new Vector4(1, 0, 0, 1), true)
  };

  /**
   * It's not in Minecraft's block model file, but the top part of vines rotates depending on
   * the presence of other sides. By manually checking the rotation for all 16 states,
   * the lookup table below was created.
   */
  protected static final Quad[] topQuads;

  static {
    Quad top90 =
        new Quad(new Vector3(0, 15.2 / 16, 0), new Vector3(1, 15.2 / 16, 0),
            new Vector3(0, 15.2 / 16, 1), new Vector4(0, 1, 0, 1), true);

    Quad top = top90.transform(Transform.NONE.rotateNegY());
    Quad top180 = top90.transform(Transform.NONE.rotateY());
    Quad top270 = top180.transform(Transform.NONE.rotateY());

    // bits of the index are other sides in order west,east,south,north
    topQuads = new Quad[]{
        top90,  // 0000
        top270, // 0001
        top90,  // 0010
        top180, // 0011
        top,    // 0100
        top90,  // 0101
        top180, // 0110
        top90,  // 0111
        top180, // 1000
        top,    // 1001
        top270, // 1010
        top270, // 1011
        top90,  // 1100
        top,    // 1101
        top180, // 1110
        top90,  // 1111
    };
  }


  private final Quad[] quads;
  private final Texture[] textures;
  private final Tint[] tints;

  public VineModel(int connections) {
    ArrayList<Quad> quads = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      if ((connections & (1 << i)) != 0)
        quads.add(model[i]);
    }
    if ((connections & (1 << 4)) != 0) {
      quads.add(topQuads[connections & 0b1111]);
    }

    this.quads = quads.toArray(new Quad[0]);
    this.textures = new Texture[this.quads.length];
    Arrays.fill(textures, Texture.vines);
    this.tints = new Tint[this.quads.length];
    Arrays.fill(tints, Tint.BIOME_FOLIAGE);
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }

  @Override
  public Tint[] getTints() {
    return tints;
  }
}
