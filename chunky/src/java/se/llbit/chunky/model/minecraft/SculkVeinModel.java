/*
 * Copyright (c) 2023 Chunky contributors
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
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.ArrayList;
import java.util.Arrays;

public class SculkVeinModel extends QuadModel {

  private static final Quad[] sculkVein = {
      // North
      new Quad(new Vector3(0, 0, 0.1 / 16), new Vector3(1, 0, 0.1 / 16),
          new Vector3(0, 1, 0.1 / 16), new Vector4(0, 1, 0, 1), true),

      // South
      new Quad(new Vector3(1, 0, 15.9 / 16), new Vector3(0, 0, 15.9 / 16),
          new Vector3(1, 1, 15.9 / 16), new Vector4(1, 0, 0, 1), true),

      // East
      new Quad(new Vector3(15.9 / 16, 0, 0), new Vector3(15.9 / 16, 0, 1),
          new Vector3(15.9 / 16, 1, 0), new Vector4(0, 1, 0, 1), true),

      // West
      new Quad(new Vector3(0.1 / 16, 0, 1), new Vector3(0.1 / 16, 0, 0),
          new Vector3(0.1 / 16, 1, 1), new Vector4(1, 0, 0, 1), true),

      // Top
      new Quad(new Vector3(0, 15.9 / 16, 0), new Vector3(1, 15.9 / 16, 0),
          new Vector3(0, 15.9 / 16, 1), new Vector4(0, 1, 0, 1), true),

      // Bottom
      new Quad(new Vector3(0, 0.1 / 16, 0), new Vector3(1, 0.1 / 16, 0),
          new Vector3(0, 0.1 / 16, 1), new Vector4(0, 1, 0, 1), true),
  };

  private final Quad[] quads;
  private final AbstractTexture[] textures;

  public SculkVeinModel(int connections) {
    ArrayList<Quad> quads = new ArrayList<>();
    for (int i = 0; i < sculkVein.length; i++) {
      if ((connections & (1 << i)) != 0)
        quads.add(sculkVein[i]);
    }
    this.quads = quads.toArray(new Quad[0]);
    this.textures = new AbstractTexture[this.quads.length];
    Arrays.fill(this.textures, Texture.sculkVein);
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public AbstractTexture[] getTextures() {
    return textures;
  }
}
