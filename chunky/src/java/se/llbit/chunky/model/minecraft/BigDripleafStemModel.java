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
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class BigDripleafStemModel extends QuadModel {

  //region Big Dripleaf Stem
  private static final Quad[] quadsNorth =
      Model.join(
          Model.rotateY(new Quad[]{
                  new Quad(
                      new Vector3(5 / 16.0, 16 / 16.0, 12 / 16.0),
                      new Vector3(11 / 16.0, 16 / 16.0, 12 / 16.0),
                      new Vector3(5 / 16.0, 0 / 16.0, 12 / 16.0),
                      new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
                  ),
                  new Quad(
                      new Vector3(11 / 16.0, 16 / 16.0, 12 / 16.0),
                      new Vector3(5 / 16.0, 16 / 16.0, 12 / 16.0),
                      new Vector3(11 / 16.0, 0 / 16.0, 12 / 16.0),
                      new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
                  )
              },
              Math.toRadians(45), new Vector3(0.5, 0, 12 / 16.0)), // TODO rescale
          Model.rotateY(new Quad[]{
                  new Quad(
                      new Vector3(5 / 16.0, 16 / 16.0, 12 / 16.0),
                      new Vector3(11 / 16.0, 16 / 16.0, 12 / 16.0),
                      new Vector3(5 / 16.0, 0 / 16.0, 12 / 16.0),
                      new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
                  ),
                  new Quad(
                      new Vector3(11 / 16.0, 16 / 16.0, 12 / 16.0),
                      new Vector3(5 / 16.0, 16 / 16.0, 12 / 16.0),
                      new Vector3(11 / 16.0, 0 / 16.0, 12 / 16.0),
                      new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
                  )
              },
              Math.toRadians(-45), new Vector3(0.5, 0, 12 / 16.0)) // TODO rescale
      );
  //endregion

  private static final AbstractTexture[] textures = new AbstractTexture[quadsNorth.length];
  static { Arrays.fill(textures, Texture.bigDripleafStem); }

  private final Quad[] quads;

  public BigDripleafStemModel(String facing) {
    switch (facing) {
      case "north":
      default:
        quads = quadsNorth;
        break;
      case "east":
        quads = Model.rotateY(quadsNorth, -Math.toRadians(90));
        break;
      case "south":
        quads = Model.rotateY(quadsNorth, -Math.toRadians(180));
        break;
      case "west":
        quads = Model.rotateY(quadsNorth, -Math.toRadians(270));
        break;
    }
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
