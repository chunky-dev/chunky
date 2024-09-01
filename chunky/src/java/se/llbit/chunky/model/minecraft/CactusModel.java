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

import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class CactusModel extends QuadModel {
  private static final Quad[] quads = {
      // front
      new Quad(new Vector3(1, 0, .0625), new Vector3(0, 0, .0625), new Vector3(1, 1, .0625),
          new Vector4(1, 0, 0, 1)),

      // back
      new Quad(new Vector3(0, 0, .9375), new Vector3(1, 0, .9375), new Vector3(0, 1, .9375),
          new Vector4(0, 1, 0, 1)),

      // left
      new Quad(new Vector3(.0625, 0, 0), new Vector3(.0625, 0, 1), new Vector3(.0625, 1, 0),
          new Vector4(0, 1, 0, 1)),

      // right
      new Quad(new Vector3(.9375, 0, 1), new Vector3(.9375, 0, 0), new Vector3(.9375, 1, 1),
          new Vector4(1, 0, 0, 1)),

      // top
      new Quad(new Vector3(1, 1, 0), new Vector3(0, 1, 0), new Vector3(1, 1, 1),
          new Vector4(1, 0, 0, 1)),

      // bottom
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1)),};

  private static final AbstractTexture[] textures = {
      Texture.cactusSide, Texture.cactusSide, Texture.cactusSide, Texture.cactusSide,
      Texture.cactusTop, Texture.cactusBottom
  };


  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public AbstractTexture[] getTextures() {
    return textures;
  }
}
