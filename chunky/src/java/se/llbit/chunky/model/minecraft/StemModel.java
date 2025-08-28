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

package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.model.Tint;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class StemModel extends QuadModel {
  private static final Texture[] textures = {Texture.stemStraight, Texture.stemStraight};
  private static final Tint[][] stemColors = {
      {new Tint(0xFF00E210), new Tint(0xFF00E210)},
      {new Tint(0xFF00E210), new Tint(0xFF00E210)},
      {new Tint(0xFF00E210), new Tint(0xFF00E210)},
      {new Tint(0xFF00CC06), new Tint(0xFF00CC06)},
      {new Tint(0xFF5FC803), new Tint(0xFF5FC803)},
      {new Tint(0xFF65C206), new Tint(0xFF65C206)},
      {new Tint(0xFFA0B800), new Tint(0xFFA0B800)},
      {new Tint(0xFFBFB600), new Tint(0xFFBFB600)},
  };

  private final Quad[] quads;
  private final Tint[] tints;

  public StemModel(int height) {
    this.quads = new Quad[] {
        new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 1),
            new Vector3(0, (height + 1) / 8., 0), new Vector4(0, 1, (7 - height) / 8., 1), true),
        new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 1),
            new Vector3(1, (height + 1) / 8., 0), new Vector4(0, 1, (7 - height) / 8., 1), true),
    };
    this.tints = stemColors[height];
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }

  @Override
  public Tint[] getTints() {
    return tints;
  }
}
