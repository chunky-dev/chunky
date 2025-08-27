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

import se.llbit.chunky.block.MinecraftBlockTranslucent;
import se.llbit.chunky.model.minecraft.WaterModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.SolidColorTexture;
import se.llbit.chunky.world.Material;
import se.llbit.math.*;

public class Water extends MinecraftBlockTranslucent {

  public static final Water INSTANCE = new Water(0);

  public static final int FULL_WATER_BLOCK = 16;

  public final int level;
  public final int data;

  public Water(int level, int data) {
    super("water", SolidColorTexture.EMPTY);
    this.level = level % 8;
    this.data = data;
    solid = false;
    localIntersect = !isFullBlock();
  }

  public Water(int level) {
    this(level, 0);
  }

  public boolean isFullBlock() {
    return (this.data & (1 << FULL_WATER_BLOCK)) != 0;
  }

  @Override public boolean isWater() {
    return true;
  }

  @Override public boolean isSameMaterial(Material other) {
    return other.isWater();
  }

  @Override public boolean intersect(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene) {
    boolean hit = WaterModel.intersect(ray, intersectionRecord, scene, data);
    if (hit) {
      intersectionRecord.material.getColor(intersectionRecord);
    }
    return hit;
  }

  @Override public String description() {
    return String.format("level=%d", level);
  }

  @Override
  public boolean isInside(Ray2 ray) {
    if (isFullBlock()) {
      return true;
    } else {
      double ix = ray.o.x - QuickMath.floor(ray.o.x);
      double iy = ray.o.y - QuickMath.floor(ray.o.y);
      double iz = ray.o.z - QuickMath.floor(ray.o.z);

      if (iy > 1 - WaterModel.TOP_BLOCK_GAP) {
        return false;
      }
      Ray2 testRay = new Ray2(new Vector3(ix, iy, iz), ray.d);
      IntersectionRecord intersectionRecord = new IntersectionRecord();
      return WaterModel.isInside(testRay, intersectionRecord, data);
    }
  }

  public boolean isInside(double x, double y, double z) {
    Ray2 ray = new Ray2();
    ray.o.set(x, y, z);
    return isInside(ray);
  }
}
