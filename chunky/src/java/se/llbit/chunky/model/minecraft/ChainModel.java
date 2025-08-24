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

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class ChainModel extends QuadModel {
  private static final Quad[] quadsY =
    Model.rotateY(
      new Quad[]{
        new Quad(
          new Vector3(6.5 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(9.5 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(6.5 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector4(0 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)),
        new Quad(
          new Vector3(9.5 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(6.5 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(9.5 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector4(3 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)),
        new Quad(
          new Vector3(8 / 16.0, 16 / 16.0, 9.5 / 16.0),
          new Vector3(8 / 16.0, 16 / 16.0, 6.5 / 16.0),
          new Vector3(8 / 16.0, 0 / 16.0, 9.5 / 16.0),
          new Vector4(3 / 16.0, 6 / 16.0, 16 / 16.0, 0 / 16.0)),
        new Quad(
          new Vector3(8 / 16.0, 16 / 16.0, 6.5 / 16.0),
          new Vector3(8 / 16.0, 16 / 16.0, 9.5 / 16.0),
          new Vector3(8 / 16.0, 0 / 16.0, 6.5 / 16.0),
          new Vector4(6 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0))
      },
      Math.toRadians(45));

  private final Texture[] textures;
  private final Quad[] quads;

  public ChainModel(String axisName, Texture texture) {
    switch (axisName) {
      default:
      case "y":
        quads = quadsY;
        break;
      case "x":
        quads = Model.rotateZ(quadsY);
        break;
      case "z":
        quads = Model.rotateX(quadsY);
        break;
    }
    textures = new Texture[quads.length];
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
