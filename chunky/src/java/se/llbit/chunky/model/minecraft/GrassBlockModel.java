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

import static se.llbit.chunky.model.Tint.BIOME_GRASS;
import static se.llbit.chunky.model.Tint.NONE;

import se.llbit.chunky.model.AABBModel;
import se.llbit.chunky.model.Tint;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.Constants;
import se.llbit.math.IntersectionRecord;

public class GrassBlockModel extends AABBModel {

  private final static Tint[][] tints = new Tint[][] {
      {NONE, NONE, NONE, NONE, BIOME_GRASS, NONE},
      {BIOME_GRASS, BIOME_GRASS, BIOME_GRASS, BIOME_GRASS, NONE, NONE}
  };

  private final static AABB[] boxes = new AABB[]{
      new AABB(0, 1, 0, 1, 0, 1),
      new AABB(0, 1, 0, 1, 0, 1)
  };

  private static final Texture[][] textures = new Texture[][]{
      {
          Texture.grassSideSaturated, Texture.grassSideSaturated,
          Texture.grassSideSaturated, Texture.grassSideSaturated,
          Texture.grassTop, Texture.dirt
      },
      {
        Texture.grassSide, Texture.grassSide,
        Texture.grassSide, Texture.grassSide,
        null, null
      }
  };

  @Override
  public AABB[] getBoxes() {
    return boxes;
  }

  @Override
  public Texture[][] getTextures() {
    return textures;
  }

  @Override
  public Tint[][] getTints() {
    return tints;
  }
  
  @Override
  public boolean intersectFace(IntersectionRecord intersectionRecord, Scene scene, Texture texture, UVMapping mapping) {
    // This is the method that handles intersecting faces of all AABB-based models.
    // Do normal mapping, parallax occlusion mapping, specular maps and all the good stuff here!

    if (texture == null) {
      return false;
    }

    double tmp;
    if (mapping != null) {
      switch (mapping) {
        case ROTATE_90:
          tmp = intersectionRecord.uv.x;
          intersectionRecord.uv.x = 1 - intersectionRecord.uv.y;
          intersectionRecord.uv.y = tmp;
          break;
        case ROTATE_180:
          intersectionRecord.uv.x = 1 - intersectionRecord.uv.x;
          intersectionRecord.uv.y = 1 - intersectionRecord.uv.y;
          break;
        case ROTATE_270:
          tmp = intersectionRecord.uv.y;
          intersectionRecord.uv.y = 1 - intersectionRecord.uv.x;
          intersectionRecord.uv.x = tmp;
          break;
        case FLIP_U:
          intersectionRecord.uv.x = 1 - intersectionRecord.uv.x;
          break;
        case FLIP_V:
          intersectionRecord.uv.y = 1 - intersectionRecord.uv.y;
          break;
      }
    }

    float[] color = texture.getColor(intersectionRecord.uv.x, intersectionRecord.uv.y);
    if (color[3] > Constants.EPSILON) {
      intersectionRecord.color.set(color);
      return true;
    }
    return false;
  }
}
