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

package se.llbit.chunky.model.model;

import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.model.Tint;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class AttachedStemModel extends QuadModel {
  private static final Quad[] growth = new Quad[2];
  private static final Quad[] ripe = {
      new Quad(new Vector3(0, 0, .5), new Vector3(1, 0, .5), new Vector3(0, 1, .5),
          new Vector4(0, 1, 0, 1), true),
      new Quad(new Vector3(0, 0, .5), new Vector3(1, 0, .5),
          new Vector3(0, 1, .5), new Vector4(1, 0, 0, 1), true),
      new Quad(new Vector3(.5, 0, 0), new Vector3(.5, 0, 1),
          new Vector3(.5, 1, 0), new Vector4(0, 1, 0, 1), true),
      new Quad(new Vector3(.5, 0, 0), new Vector3(.5, 0, 1),
          new Vector3(.5, 1, 0), new Vector4(1, 0, 0, 1), true),
  };

  static {
    int height = 3;
    growth[0] = new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 1),
        new Vector3(0, (height + 1) / 8., 0), new Vector4(0, 1, (7 - height) / 8., 1), true);
    growth[1] = new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 1),
        new Vector3(1, (height + 1) / 8., 0), new Vector4(0, 1, (7 - height) / 8., 1), true);
  }

  private static final Texture[] textures = {
      Texture.stemStraight, Texture.stemStraight, Texture.stemBent
  };

  private static final Tint[] tints = new Tint[3];
  static {
      tints[0] = new Tint(0xFFBFB600);
      tints[1] = tints[0];
      tints[2] = tints[0];
  }

  private final Quad[] quads;

  public AttachedStemModel(int facing) {
    quads = new Quad[3];
    quads[0] = growth[0];
    quads[1] = growth[1];
    quads[2] = ripe[facing];
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
