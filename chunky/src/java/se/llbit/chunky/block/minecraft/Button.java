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

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.minecraft.ButtonModel;
import se.llbit.chunky.resources.texture.AbstractTexture;

public class Button extends AbstractModelBlock {

  private final String description;
  // TODO(llbit): render powered buttons

  public Button(String name, AbstractTexture texture, String face, String facing, boolean powered) {
    super(name, texture);
    this.description = String.format("face=%s, facing=%s, powered=%s",
        face, facing, powered);
    this.model = new ButtonModel(face, facing, texture);
    // TODO handle rotation on top/bottom positions!
  }

  @Override
  public String description() {
    return description;
  }
}
