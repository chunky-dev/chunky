/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;

import java.util.Arrays;

public class FenceGateModel extends AABBModel {
  private static final AABB[] closed = {
      new AABB(0, .125, .3125, 1, .4375, .5625),
      new AABB(.375, .625, .375, .9375, .4375, .5625),
      new AABB(.875, 1, .3125, 1, .4375, .5625),

      new AABB(.125, .875, .375, .5625, .4375, .5625),
      new AABB(.125, .875, .75, .9375, .4375, .5625),
  };

  private static final AABB[] open = {
      new AABB(0, .125, .3125, 1, .4375, .5625),
      new AABB(.875, 1, .3125, 1, .4375, .5625),

      new AABB(0, .125, .375, .5625, .5625, .8125),
      new AABB(0, .125, .75, .9375, .5625, .8125),
      new AABB(0, .125, .375, .9375, .8125, .9375),

      new AABB(.875, 1, .375, .5625, .5625, .8125),
      new AABB(.875, 1, .75, .9375, .5625, .8125),
      new AABB(.875, 1, .375, .9375, .8125, .9375),
  };

  private static final AABB[][][][] rot = new AABB[2][2][4][];

  static {
    rot[0][0][0] = closed;
    rot[0][0][1] = new AABB[closed.length];
    rot[0][0][2] = rot[0][0][0];
    rot[0][0][3] = rot[0][0][1];
    for (int i = 0; i < closed.length; ++i)
      rot[0][0][1][i] = closed[i].getYRotated();
    rot[0][1][0] = open;
    for (int j = 1; j < 4; ++j) {
      rot[0][1][j] = new AABB[rot[0][1][j - 1].length];
      for (int i = 0; i < rot[0][1][j - 1].length; ++i)
        rot[0][1][j][i] = rot[0][1][j - 1][i].getYRotated();
    }
    for (int i = 0; i < rot[1].length; ++i) {
      for (int j = 0; j < rot[1][i].length; ++j) {
        rot[1][i][j] = new AABB[rot[0][i][j].length];
        for (int k = 0; k < rot[1][i][j].length; ++k) {
          rot[1][i][j][k] = rot[0][i][j][k].getTranslated(0, -3 / 16., 0);
        }
      }
    }
  }

  private final AABB[] boxes;
  private final Texture[][] textures;

  public FenceGateModel(Texture texture, int facing, int inWall, int isOpen) {
    boxes = rot[inWall][isOpen][facing];
    textures = new Texture[boxes.length][];
    Texture[] tex = new Texture[6];
    Arrays.fill(tex, texture);
    Arrays.fill(textures, tex);
  }

  @Override
  public AABB[] getBoxes() {
    return boxes;
  }

  @Override
  public Texture[][] getTextures() {
    return textures;
  }
}
