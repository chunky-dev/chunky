/* Copyright (c) 2012-2021 Chunky contributors
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
package se.llbit.chunky.renderer.scene;

import se.llbit.chunky.model.minecraft.WaterModel;
import se.llbit.json.JsonObject;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Ray;
import se.llbit.math.Ray2;
import se.llbit.math.Vector3;

public class LegacyWaterShader implements WaterShader {
  @Override
  public Vector3 doWaterShading(Ray2 ray, IntersectionRecord intersectionRecord, double animationTime) {
    return WaterModel.doWaterDisplacement(ray, intersectionRecord);
  }

  @Override
  public WaterShader clone() {
    return new LegacyWaterShader();
  }

  @Override
  public void save(JsonObject json) {
  }

  @Override
  public void load(JsonObject json) {
  }
}
