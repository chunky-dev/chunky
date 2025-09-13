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

package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.HoneyBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

public class Honey extends AbstractModelBlock {
    public Honey() {
        super("honey_block", Texture.honeyBlockSide);
        model = new HoneyBlockModel();
    }

  @Override
  public boolean intersect(Ray ray, IntersectionRecord intersectionRecord, Scene scene) {
    if (model.intersect(ray, intersectionRecord, scene)) {
      if (ray.getCurrentMedium() == this) {
        if (ray.d.dot(intersectionRecord.n) > 0) {
          Vector3 o = new Vector3(ray.o);
          if (onEdge(o, ray.d, intersectionRecord.distance)) {
            return false;
          }
          intersectionRecord.n.scale(-1);
          intersectionRecord.shadeN.scale(-1);
        }
      }
      return true;
    }
    return false;
  }
}
