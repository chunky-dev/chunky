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
import se.llbit.chunky.model.minecraft.CauldronModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Cauldron extends MinecraftBlockTranslucent {

  private final int level;

  public Cauldron(String name, int level) {
    super(name, Texture.cauldronSide);
    this.level = level;
    localIntersect = true;
  }

  public int getLevel() {
    return level;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return CauldronModel.intersectWithWater(ray, scene, level);
  }

  @Override
  public String description() {
    return "level=" + level;
  }
}
