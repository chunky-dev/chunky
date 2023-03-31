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

package se.llbit.chunky.block.block;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.model.LightBlockModel;
import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Vector4;

public class LightBlock extends AbstractModelBlock {

  private static final TexturedBlockModel previewBlockModel = new TexturedBlockModel(
      Texture.light, Texture.light, Texture.light,
      Texture.light, Texture.light, Texture.light
  );

  private final int level;

  private final Vector4 color = new Vector4(1, 1, 1, 1);

  public LightBlock(String name, int level) {
    super(name, Texture.light);
    this.level = level;
    this.model = new LightBlockModel(color);
    localIntersect = true;
    solid = false;
  }

  public int getLevel() {
    return level;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    if (scene.getMode() == RenderMode.PREVIEW) {
      return previewBlockModel.intersect(ray, scene);
    }
    if (scene.getMode() != RenderMode.PREVIEW &&
        (!scene.getEmittersEnabled() || emittance < Ray.EPSILON
            || ray.depth >= scene.getRayDepth() - 1 || ray.specular)) {
      return false;
    }
    return this.model.intersect(ray, scene);
  }

  @Override
  public String description() {
    return "level=" + level;
  }
}
