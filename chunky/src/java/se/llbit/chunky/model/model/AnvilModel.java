/*
 * Copyright (c) 2013-2023 Chunky contributors
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

import se.llbit.chunky.model.AABBModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;

/**
 * Anvil block.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class AnvilModel extends AABBModel {
  private static final AABB[][] boxes = {
      // north-south
      {
        new AABB(3 / 16., 13 / 16., 10 / 16., 1, 0, 1),
        new AABB(2 / 16., 14 / 16., 0, 4 / 16., 2 / 16., 14 / 16.),
        new AABB(4 / 16., 12 / 16., 4 / 16., 5 / 16., 3 / 16., 13 / 16.),
        new AABB(6 / 16., 10 / 16., 5 / 16., 10 / 16., 4 / 16., 12 / 16.),
      },
      // east-west
      {
        new AABB(0, 1, 10 / 16., 1, 3 / 16., 13 / 16.),
        new AABB(2 / 16., 14 / 16., 0, 4 / 16., 2 / 16., 14 / 16.),
        new AABB(3 / 16., 13 / 16., 4 / 16., 5 / 16., 4 / 16., 12 / 16.),
        new AABB(4 / 16., 12 / 16., 5 / 16., 10 / 16., 6 / 16., 10 / 16.),
      },
  };

  public static final Texture[] topTexture = {
      Texture.anvilTop,
      Texture.anvilTopDamaged1,
      Texture.anvilTopDamaged2,
      Texture.anvilTopDamaged2
  };

  private final int orientation;
  private final Texture[][] textures;
  private final UVMapping[][] mapping;

  public AnvilModel(int orientation, int damage) {
    this.orientation = orientation;

    Texture side = Texture.anvilSide;
    Texture top = topTexture[damage];
    this.textures = new Texture[][] {
        {side, side, side, side, top, side},
        {side, side, side, side, side, side},
        {side, side, side, side, side, side},
        {side, side, side, side, side, side},
    };

    // Default mapping (all nones)
    this.mapping = new UVMapping[4][6];
    if (this.orientation == 1) {
      this.mapping[0][4] = UVMapping.ROTATE_90;
    }
  }

  @Override
  public AABB[] getBoxes() {
    // north-south
    if (orientation == 0)
      return boxes[0];

    // east-west
    return boxes[1];
  }

  @Override
  public UVMapping[][] getUVMapping() {
    return this.mapping;
  }

  @Override
  public Texture[][] getTextures() {
    return this.textures;
  }
}
