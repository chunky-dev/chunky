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
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.LilyPadEntity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Collection;
import java.util.Collections;

public class LilyPad extends MinecraftBlockTranslucent {
  public LilyPad() {
    super("lily_pad", Texture.lilyPad);
    opaque = false;
  }

  @Override public boolean hasEntities() {
    return true;
  }

  @Override public Collection<Entity> createEntities(Vector3 position) {
    return Collections.singleton(new LilyPadEntity(position));
  }
}
