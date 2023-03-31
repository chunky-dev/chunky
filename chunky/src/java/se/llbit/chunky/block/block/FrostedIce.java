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

import se.llbit.chunky.block.MinecraftBlockTranslucent;
import se.llbit.chunky.resources.Texture;

public class FrostedIce extends MinecraftBlockTranslucent {
  private static final Texture[] texture = {
      Texture.frostedIce0, Texture.frostedIce1, Texture.frostedIce2, Texture.frostedIce3
  };

  private final int age;

  public FrostedIce(int age) {
    super("frosted_ice", texture[age & 3]);
    solid = true;
    this.age = age & 3;
  }

  @Override public String description() {
    return "age=" + age;
  }
}
