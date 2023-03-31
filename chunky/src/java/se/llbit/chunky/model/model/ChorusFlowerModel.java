/*
 * Copyright (c) 2015-2023 Chunky contributors
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

import java.util.Arrays;

public class ChorusFlowerModel extends AABBModel {
  private static final AABB[] boxes = {
      new AABB(0, 1, 2 / 16., 14 / 16., 2 / 16., 14 / 16.),
      new AABB(2 / 16., 14 / 16., 0, 1, 2 / 16., 14 / 16.),
      new AABB(2 / 16., 14 / 16., 2 / 16., 14 / 16., 0, 1),
  };

  private final Texture[][] textures;

  public ChorusFlowerModel(int age) {
    Texture tex = age < 5 ? Texture.chorusFlower : Texture.chorusFlowerDead;
    textures = new Texture[3][6];
    for (int i = 0; i < 3; i++) Arrays.fill(textures[i], tex);
  }

  @Override
  public AABB[] getBoxes() {
    return boxes;
  }

  @Override
  public Texture[][] getTextures() {
    return textures;
  }
}
