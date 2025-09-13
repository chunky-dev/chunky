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
import se.llbit.chunky.model.Tint;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Ray;

public class UntintedLeafModel extends LeafModel {
  private static final AABB[] boxes = { new AABB(0, 1, 0, 1, 0, 1) };

  public UntintedLeafModel(Texture texture) {
    super(texture, Tint.NONE);
  }

  @Override
  public AABB[] getBoxes() {
    return boxes;
  }

  @Override
  public Tint[][] getTints() {
    return null;
  }
}
