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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FenceModel extends AABBModel {
  private static final AABB post = new AABB(6 / 16., 10 / 16., 0, 1, 6 / 16., 10 / 16.);

  private static final AABB[][] plank = {
      // Connected north.
      {
        new AABB(7 / 16.0, 9 / 16.0, 6 / 16.0, 9 / 16.0, 0, .4),
        new AABB(7 / 16.0, 9 / 16.0, 12 / 16.0, 15 / 16.0, 0, .4),
      },
      // Connected south.
      {
        new AABB(7 / 16.0, 9 / 16.0, 6 / 16.0, 9 / 16.0, .6, 1),
        new AABB(7 / 16.0, 9 / 16.0, 12 / 16.0, 15 / 16.0, .6, 1),
      },
      // Connected east.
      {
        new AABB(.6, 1, 6 / 16.0, 9 / 16.0, 7 / 16.0, 9 / 16.0),
        new AABB(.6, 1, 12 / 16.0, 15 / 16.0, 7 / 16.0, 9 / 16.0),
      },
      // Connected west.
      {
        new AABB(0, .4, 6 / 16.0, 9 / 16.0, 7 / 16.0, 9 / 16.0),
        new AABB(0, .4, 12 / 16.0, 15 / 16.0, 7 / 16.0, 9 / 16.0),
      },
  };

  private final AABB[] boxes;
  private final AbstractTexture[][] textures;

  public FenceModel(AbstractTexture texture, int connections) {
    ArrayList<AABB> boxes = new ArrayList<>();
    boxes.add(post);
    for (int i = 0; i < 4; i++) {
      if ((connections & (1 << i)) != 0)
        Collections.addAll(boxes, plank[i]);
    }
    this.boxes = boxes.toArray(new AABB[0]);
    this.textures = new AbstractTexture[this.boxes.length][];
    AbstractTexture[] tex = new AbstractTexture[6];
    Arrays.fill(tex, texture);
    Arrays.fill(this.textures, tex);
  }

  @Override
  public AABB[] getBoxes() {
    return boxes;
  }

  @Override
  public AbstractTexture[][] getTextures() {
    return textures;
  }
}
