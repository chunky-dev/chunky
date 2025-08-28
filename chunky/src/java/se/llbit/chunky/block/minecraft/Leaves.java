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
import se.llbit.chunky.block.Block;
import se.llbit.chunky.model.minecraft.LeafModel;
import se.llbit.chunky.model.minecraft.WaterModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.*;

public class Leaves extends AbstractModelBlock {

  public Leaves(String name, Texture texture, boolean tinted) {
    super(name, texture);
    solid = false;
    this.model = new LeafModel(texture, tinted);
  }

  public Leaves(String name, Texture texture, int tint) {
    super(name, texture);
    solid = false;
    this.model = new LeafModel(texture, tint);
  }

  @Override
  public boolean intersect(Ray ray, IntersectionRecord intersectionRecord, Scene scene) {
    Intersectable waterModel = null;
    boolean isWaterloggedFull = false;
    if (waterlogged) {
      int x = (int) QuickMath.floor(ray.o.x);
      int y = (int) QuickMath.floor(ray.o.y);
      int z = (int) QuickMath.floor(ray.o.z);
      isWaterloggedFull = scene.getWorldOctree().getMaterial(x, y + 1, z, scene.getPalette()).isWaterFilled();
      if (ray.getCurrentMedium().isWater()) {
        if (!isWaterloggedFull) {
          waterModel = WaterModel.WATER_TOP;
        }
      } else {
        waterModel = (isWaterloggedFull) ? Block.FULL_BLOCK : WaterModel.NOT_FULL_BLOCK;
      }
    }

    IntersectionRecord modelIntersect = new IntersectionRecord();
    IntersectionRecord waterIntersect = new IntersectionRecord();

    boolean modelHit = model.intersect(ray, modelIntersect, scene);
    boolean waterHit = false;
    if (waterModel != null) {
      waterHit = waterModel.closestIntersection(ray, waterIntersect, scene);
    }

    boolean hitTop = waterHit && !isWaterloggedFull && waterIntersect.n.y > 0 && ray.d.dot(waterIntersect.n) > 0;

    if (ray.getCurrentMedium().isWater()) {
      if (hitTop) {
        intersectionRecord.distance = waterIntersect.distance;
        intersectionRecord.setNormal(waterIntersect);
        intersectionRecord.material = (scene.waterPlaneMaterial(ray.o.rScaleAdd(intersectionRecord.distance, ray.d)));
        intersectionRecord.material.getColor(intersectionRecord);

        Ray testRay = new Ray(ray);
        testRay.o.scaleAdd(intersectionRecord.distance, testRay.d);
        Vector3 shadeNormal = scene.getCurrentWaterShader().doWaterShading(testRay, intersectionRecord, scene.getAnimationTime());
        intersectionRecord.shadeN.set(shadeNormal);

        intersectionRecord.n.scale(-1);
        intersectionRecord.shadeN.scale(-1);
        return true;
      } else {
        return false;
      }
    } else {
      if (modelHit
            && modelIntersect.distance < waterIntersect.distance + Constants.EPSILON
            && modelIntersect.color.w > Constants.EPSILON) {
        intersectionRecord.distance = modelIntersect.distance;
        intersectionRecord.setNormal(modelIntersect);
        intersectionRecord.color.set(modelIntersect.color);
        intersectionRecord.flags = modelIntersect.flags;
        return true;
      } else if (waterHit) {
        intersectionRecord.distance = waterIntersect.distance;
        intersectionRecord.setNormal(waterIntersect);
        Water.INSTANCE.getColor(intersectionRecord);
        intersectionRecord.material = scene.getPalette().water;
        intersectionRecord.flags = 0;

        if (intersectionRecord.n.y > 0) {
          Ray testRay = new Ray(ray);
          testRay.o.scaleAdd(intersectionRecord.distance, testRay.d);
          Vector3 shadeNormal = scene.getCurrentWaterShader().doWaterShading(testRay, intersectionRecord, scene.getAnimationTime());
          intersectionRecord.shadeN.set(shadeNormal);
        }
        return true;
      } else {
        Block waterPlaneMaterial;
        if (ray.getCurrentMedium() != (waterPlaneMaterial = scene.waterPlaneMaterial(ray.o))) {
          intersectionRecord.distance = 0;
          intersectionRecord.material = waterPlaneMaterial;
          waterPlaneMaterial.getColor(intersectionRecord);
          return true;
        }
        return false;
      }
    }
  }

  @Override public boolean isInside(Ray ray) {
    return false;
  }
}
