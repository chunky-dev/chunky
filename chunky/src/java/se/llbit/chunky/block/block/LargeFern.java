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
import se.llbit.chunky.model.GrassTintedSpriteModel;
import se.llbit.chunky.resources.Texture;

public class LargeFern extends AbstractModelBlock {

  public LargeFern(String half) {
    super("large_fern",
        half.equals("upper")
            ? Texture.largeFernTop
            : Texture.largeFernBottom);
    solid = false;
    model = new GrassTintedSpriteModel(texture);
  }
}
