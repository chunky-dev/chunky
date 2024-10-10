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

import se.llbit.chunky.model.AABBModel;
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.AABB;

public class StairModel extends AABBModel {

  private static final AABB[][][] corners = {
      // Not flipped:
      {
          {
              // s-e
              new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0.5, 1, 0.5, 1, 0.5, 1),
          }, {
          // s-w
          new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0, 0.5, 0.5, 1, 0.5, 1),
      }, {
          // n-e
          new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0.5, 1, 0.5, 1, 0, 0.5),
      }, {
          // n-w
          new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0, 0.5, 0.5, 1, 0, 0.5),
      }, {
          // inner s-e
          new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0, 1, 0.5, 1, 0.5, 1),
          new AABB(0.5, 1, 0.5, 1, 0, 0.5),
      }, {
          // inner s-w
          new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0.5, 1, 0.5, 1, 0.5, 1),
          new AABB(0, 0.5, 0.5, 1, 0, 1),
      }, {
          // inner n-e
          new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0.5, 1, 0.5, 1, 0, 1),
          new AABB(0, 0.5, 0.5, 1, 0, 0.5),
      }, {
          // inner n-w
          new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0, 1, 0.5, 1, 0, 0.5),
          new AABB(0, 0.5, 0.5, 1, 0.5, 1),
      },
      },
      // Flipped:
      {
          {
              // s-e
              new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0.5, 1, 0, 0.5, 0.5, 1),
          }, {
          // s-w
          new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0, 0.5, 0, 0.5, 0.5, 1),
      }, {
          // n-e
          new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0.5, 1, 0, 0.5, 0, 0.5),
      }, {
          // n-w
          new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0, 0.5, 0, 0.5, 0, 0.5),
      }, {
          // inner s-e
          new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0, 1, 0, 0.5, 0.5, 1),
          new AABB(0.5, 1, 0, 0.5, 0, 0.5),
      }, {
          // inner s-w
          new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0.5, 1, 0, 0.5, 0.5, 1),
          new AABB(0, 0.5, 0, 0.5, 0, 1),
      }, {
          // inner n-e
          new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0.5, 1, 0, 0.5, 0, 1),
          new AABB(0, 0.5, 0, 0.5, 0, 0.5),
      }, {
          // inner n-w
          new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0, 1, 0, 0.5, 0, 0.5),
          new AABB(0, 0.5, 0, 0.5, 0.5, 1),
      },
      },
  };
  private static final AABB[][][] stairs = {
      // Not flipped.
      {
          {
              // ascending east
              new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0.5, 1, 0.5, 1, 0, 1),
          }, {
          // ascending west
          new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0, 0.5, 0.5, 1, 0, 1),
      }, {
          // ascending south
          new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0, 1, 0.5, 1, 0.5, 1),
      }, {
          // ascending north
          new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0, 1, 0.5, 1, 0, 0.5),
      },
      },
      // flipped
      {
          {
              // ascending east
              new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0.5, 1, 0, 0.5, 0, 1),
          }, {
          // ascending west
          new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0, 0.5, 0, 0.5, 0, 1),
      }, {
          // ascending south
          new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0, 1, 0, 0.5, 0.5, 1),
      }, {
          // ascending north
          new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0, 1, 0, 0.5, 0, 0.5),
      },
      },
  };

  private final int flipped;
  private final boolean isCorner;
  private final int corner;
  private final int facing;
  private final AbstractTexture[][] cornerTextures;
  private final AbstractTexture[][] textures;

  public StairModel(AbstractTexture side, AbstractTexture top, AbstractTexture bottom, int flipped, boolean isCorner,
                    int corner, int facing) {
    this.flipped = flipped;
    this.isCorner = isCorner;
    this.corner = corner;
    this.facing = facing;
    this.cornerTextures = new AbstractTexture[][]{
        {side, side, side, side, top, bottom},
        {side, side, side, side, top, bottom},
        {side, side, side, side, top, bottom}
    };
    this.textures = new AbstractTexture[][]{
        {side, side, side, side, top, bottom},
        {side, side, side, side, top, bottom}
    };
  }

  @Override
  public AABB[] getBoxes() {
    if (isCorner) {
      return corners[flipped][7 & corner];
    } else {
      return stairs[flipped][facing];
    }
  }

  @Override
  public AbstractTexture[][] getTextures() {
    if (isCorner) {
      return cornerTextures;
    } else {
      return textures;
    }
  }
}
