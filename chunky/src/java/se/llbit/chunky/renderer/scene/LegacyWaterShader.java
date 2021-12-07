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

import se.llbit.chunky.model.WaterModel;
import se.llbit.json.JsonObject;
import se.llbit.math.Ray;

public class LegacyWaterShader implements WaterShader {
  @Override
  public void doWaterShading(Ray ray, double animationTime) {
    WaterModel.doWaterDisplacement(ray);
  }

  @Override
  public WaterShader clone() {
    return new LegacyWaterShader();
  }

  @Override
  public void save(JsonObject json) {
    json.add("waterShader", "LEGACY");
  }

  @Override
  public void load(JsonObject json) {
  }
}
