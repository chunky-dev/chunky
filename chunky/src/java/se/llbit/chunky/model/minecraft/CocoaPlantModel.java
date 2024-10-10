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

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

/**
 * Cocoa Plant model used before Minecraft 1.19 (22w11a).
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class CocoaPlantModel extends QuadModel {
  //region Large
  private static final Quad[] large = {
      // front
      new Quad(new Vector3(12 / 16., 3 / 16., 7 / 16.), new Vector3(4 / 16., 3 / 16., 7 / 16.),
          new Vector3(12 / 16., 12 / 16., 7 / 16.),
          new Vector4(7 / 16., 15 / 16., 3 / 16., 12 / 16.)),

      // back
      new Quad(new Vector3(4 / 16., 3 / 16., 15 / 16.), new Vector3(12 / 16., 3 / 16., 15 / 16.),
          new Vector3(4 / 16., 12 / 16., 15 / 16.),
          new Vector4(15 / 16., 7 / 16., 3 / 16., 12 / 16.)),

      // left
      new Quad(new Vector3(4 / 16., 3 / 16., 7 / 16.), new Vector3(4 / 16., 3 / 16., 15 / 16.),
          new Vector3(4 / 16., 12 / 16., 7 / 16.),
          new Vector4(7 / 16., 15 / 16., 3 / 16., 12 / 16.)),

      // right
      new Quad(new Vector3(12 / 16., 3 / 16., 15 / 16.), new Vector3(12 / 16., 3 / 16., 7 / 16.),
          new Vector3(12 / 16., 12 / 16., 15 / 16.),
          new Vector4(15 / 16., 7 / 16., 3 / 16., 12 / 16.)),

      // top
      new Quad(new Vector3(12 / 16., 12 / 16., 7 / 16.), new Vector3(4 / 16., 12 / 16., 7 / 16.),
          new Vector3(12 / 16., 12 / 16., 15 / 16.), new Vector4(7 / 16., 0, 9 / 16., 1)),

      // bottom
      new Quad(new Vector3(4 / 16., 3 / 16., 7 / 16.), new Vector3(12 / 16., 3 / 16., 7 / 16.),
          new Vector3(4 / 16., 3 / 16., 15 / 16.), new Vector4(0, 7 / 16., 9 / 16., 1)),
  };
  //endregion

  //region Medium
  private static final Quad[] medium = {
      // front
      new Quad(new Vector3(11 / 16., 5 / 16., 9 / 16.), new Vector3(5 / 16., 5 / 16., 9 / 16.),
          new Vector3(11 / 16., 12 / 16., 9 / 16.),
          new Vector4(9 / 16., 15 / 16., 5 / 16., 12 / 16.)),

      // back
      new Quad(new Vector3(5 / 16., 5 / 16., 15 / 16.), new Vector3(11 / 16., 5 / 16., 15 / 16.),
          new Vector3(5 / 16., 12 / 16., 15 / 16.),
          new Vector4(15 / 16., 9 / 16., 5 / 16., 12 / 16.)),

      // left
      new Quad(new Vector3(5 / 16., 5 / 16., 9 / 16.), new Vector3(5 / 16., 5 / 16., 15 / 16.),
          new Vector3(5 / 16., 12 / 16., 9 / 16.),
          new Vector4(9 / 16., 15 / 16., 5 / 16., 12 / 16.)),

      // right
      new Quad(new Vector3(11 / 16., 5 / 16., 15 / 16.), new Vector3(11 / 16., 5 / 16., 9 / 16.),
          new Vector3(11 / 16., 12 / 16., 15 / 16.),
          new Vector4(15 / 16., 9 / 16., 5 / 16., 12 / 16.)),

      // top
      new Quad(new Vector3(11 / 16., 12 / 16., 9 / 16.), new Vector3(5 / 16., 12 / 16., 9 / 16.),
          new Vector3(11 / 16., 12 / 16., 15 / 16.), new Vector4(6 / 16., 0, 10 / 16., 1)),

      // bottom
      new Quad(new Vector3(5 / 16., 5 / 16., 9 / 16.), new Vector3(11 / 16., 5 / 16., 9 / 16.),
          new Vector3(5 / 16., 5 / 16., 15 / 16.), new Vector4(0, 6 / 16., 10 / 16., 1)),
  };
  //endregion

  //region Small
  private static final Quad[] small = {
      // front
      new Quad(new Vector3(10 / 16., 7 / 16., 11 / 16.), new Vector3(6 / 16., 7 / 16., 11 / 16.),
          new Vector3(10 / 16., 12 / 16., 11 / 16.),
          new Vector4(11 / 16., 15 / 16., 7 / 16., 12 / 16.)),

      // back
      new Quad(new Vector3(6 / 16., 7 / 16., 15 / 16.), new Vector3(10 / 16., 7 / 16., 15 / 16.),
          new Vector3(6 / 16., 12 / 16., 15 / 16.),
          new Vector4(15 / 16., 11 / 16., 7 / 16., 12 / 16.)),

      // left
      new Quad(new Vector3(6 / 16., 7 / 16., 11 / 16.), new Vector3(6 / 16., 7 / 16., 15 / 16.),
          new Vector3(6 / 16., 12 / 16., 11 / 16.),
          new Vector4(11 / 16., 15 / 16., 7 / 16., 12 / 16.)),

      // right
      new Quad(new Vector3(10 / 16., 7 / 16., 15 / 16.), new Vector3(10 / 16., 7 / 16., 11 / 16.),
          new Vector3(10 / 16., 12 / 16., 15 / 16.),
          new Vector4(15 / 16., 11 / 16., 7 / 16., 12 / 16.)),

      // top
      new Quad(new Vector3(10 / 16., 12 / 16., 11 / 16.),
          new Vector3(6 / 16., 12 / 16., 11 / 16.), new Vector3(10 / 16., 12 / 16., 15 / 16.),
          new Vector4(4 / 16., 0, 12 / 16., 1)),

      // bottom
      new Quad(new Vector3(6 / 16., 7 / 16., 11 / 16.), new Vector3(10 / 16., 7 / 16., 11 / 16.),
          new Vector3(6 / 16., 7 / 16., 15 / 16.), new Vector4(0, 4 / 16., 12 / 16., 1)),
  };
  //endregion

  private static final Quad stemNorth = new Quad(
      new Vector3(.5, 12 / 16., .5), new Vector3(.5, 12 / 16., 1),
      new Vector3(.5, 1, .5), new Vector4(.5, 1, 12 / 16., 1), true);

  private static final Quad[][][] fruit = new Quad[3][4][];
  private static final Quad[] stem = new Quad[4];

  static {
    fruit[0][0] = small;
    fruit[1][0] = medium;
    fruit[2][0] = large;

    stem[0] = stemNorth;
    for (int i = 1; i < 4; ++i) {
      stem[i] = stem[i - 1].transform(Transform.NONE.rotateY());
      fruit[0][i] = Model.rotateY(fruit[0][i - 1]);
      fruit[1][i] = Model.rotateY(fruit[1][i - 1]);
      fruit[2][i] = Model.rotateY(fruit[2][i - 1]);
    }
  }

  private static final AbstractTexture[] tex = {Texture.cocoaPlantSmall, Texture.cocoaPlantMedium, Texture.cocoaPlantLarge};

  private final Quad[] quads;
  private final AbstractTexture[] textures;

  public CocoaPlantModel(int facing, int age) {
    quads = new Quad[fruit[age][facing].length+1];
    System.arraycopy(fruit[age][facing], 0, quads, 0, quads.length-1);
    quads[quads.length-1] = stem[facing];
    textures = new AbstractTexture[quads.length];
    Arrays.fill(textures, tex[age]);
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
