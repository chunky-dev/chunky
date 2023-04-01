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
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class PressurePlateModel extends QuadModel {
  private static final Quad[] quads = {
      // front
      new Quad(new Vector3(15 / 16., 0, 1 / 16.), new Vector3(1 / 16., 0, 1 / 16.),
          new Vector3(15 / 16., 1 / 16., 1 / 16.), new Vector4(15 / 16., 1 / 16., 0, 1 / 16.)),

      // back
      new Quad(new Vector3(1 / 16., 0, 15 / 16.), new Vector3(15 / 16., 0, 15 / 16.),
          new Vector3(1 / 16., 1 / 16., 15 / 16.), new Vector4(1 / 16., 15 / 16., 0, 1 / 16.)),

      // right
      new Quad(new Vector3(1 / 16., 0, 1 / 16.), new Vector3(1 / 16., 0, 15 / 16.),
          new Vector3(1 / 16., 1 / 16., 1 / 16.), new Vector4(1 / 16., 15 / 16., 0, 1 / 16.)),

      // left
      new Quad(new Vector3(15 / 16., 0, 15 / 16.), new Vector3(15 / 16., 0, 1 / 16.),
          new Vector3(15 / 16., 1 / 16., 15 / 16.), new Vector4(15 / 16., 1 / 16., 0, 1 / 16.)),

      // top
      new Quad(new Vector3(15 / 16., 1 / 16., 1 / 16.), new Vector3(1 / 16., 1 / 16., 1 / 16.),
          new Vector3(15 / 16., 1 / 16., 15 / 16.),
          new Vector4(15 / 16., 1 / 16., 15 / 16., 1 / 16.)),

      // bottom
      new Quad(new Vector3(1 / 16., 0, 1 / 16.), new Vector3(15 / 16., 0, 1 / 16.),
          new Vector3(1 / 16., 0, 15 / 16.), new Vector4(1 / 16., 15 / 16., 1 / 16., 15 / 16.)),

  };

  private final Texture[] textures = new Texture[quads.length];

  public PressurePlateModel(Texture texture) {
    Arrays.fill(textures, texture);
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }
}
