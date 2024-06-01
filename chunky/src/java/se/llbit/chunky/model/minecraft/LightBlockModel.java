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

import se.llbit.chunky.model.AABBModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Ray;
import se.llbit.math.Ray2;
import se.llbit.math.Vector4;

public class LightBlockModel extends AABBModel {
  public static final AABB[] aabb = { new AABB(0.125, 0.875, 0.125, 0.875, 0.125, 0.875) };

  private final AABB[] box = aabb;

  private final Texture[][] textures = new Texture[][] {{
    Texture.light, Texture.light, Texture.light,
    Texture.light, Texture.light, Texture.light
  }};

  private final Vector4 color;

  public LightBlockModel(Vector4 color) {
    this.color = color;
  }

  @Override
  public AABB[] getBoxes() {
    return box;
  }

  @Override
  public boolean intersect(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene) {
    boolean hit = false;
    AABB[] boxes = getBoxes();
    if (boxes[0].intersect(ray, intersectionRecord)) {
      intersectionRecord.color.set(color);
      hit = true;
    }
    return hit;
  }

  @Override
  public Texture[][] getTextures() {
    return textures;
  }
}
