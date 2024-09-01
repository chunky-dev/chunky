/*
 * Copyright (c) 2013-2023 Chunky contributors
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

public class SunFlowerModel extends QuadModel {
  private final static Quad[] bottom = {
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 1), new Vector3(0, 1, 0),
          new Vector4(0, 1, 0, 1)),

      new Quad(new Vector3(1, 0, 1), new Vector3(0, 0, 0), new Vector3(1, 1, 1),
          new Vector4(0, 1, 0, 1)),

      new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 1), new Vector3(1, 1, 0),
          new Vector4(0, 1, 0, 1)),

      new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 0), new Vector3(0, 1, 1),
          new Vector4(0, 1, 0, 1)),
  };

  private final static AbstractTexture[] bottomTex = {
      Texture.sunflowerBottom, Texture.sunflowerBottom, Texture.sunflowerBottom, Texture.sunflowerBottom
  };

  private final static Quad[] top = {
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 1), new Vector3(0, 1, 0),
          new Vector4(0, 1, 0, 1)),

      new Quad(new Vector3(1, 0, 1), new Vector3(0, 0, 0), new Vector3(1, 1, 1),
          new Vector4(0, 1, 0, 1)),

      new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 1), new Vector3(1, 1, 0),
          new Vector4(0, 1, 0, 1)),

      new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 0), new Vector3(0, 1, 1),
          new Vector4(0, 1, 0, 1)),

      new Quad(new Vector3(14 / 16., 8 / 16., 2 / 16.), new Vector3(2 / 16., 16 / 16., 2 / 16.),
          new Vector3(14 / 16., 8 / 16., 14 / 16.),
          new Vector4(2 / 16., 14 / 16., 2 / 16., 14 / 16.)),

      new Quad(new Vector3(2 / 16., 16 / 16., 2 / 16.),
          new Vector3(14 / 16., 8 / 16., 2 / 16.), new Vector3(2 / 16., 16 / 16., 14 / 16.),
          new Vector4(2 / 16., 14 / 16., 2 / 16., 14 / 16.)),
  };

  private final static AbstractTexture[] topTex = {
      Texture.sunflowerTop, Texture.sunflowerTop, Texture.sunflowerTop, Texture.sunflowerTop,
      Texture.sunflowerFront, Texture.sunflowerBack
  };

  private final Quad[] quads;
  private final AbstractTexture[] textures;

  public SunFlowerModel(boolean isTop) {
    quads = isTop ? top : bottom;
    textures = isTop ? topTex : bottomTex;
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public AbstractTexture[] getTextures() {
    return textures;
  }
}
