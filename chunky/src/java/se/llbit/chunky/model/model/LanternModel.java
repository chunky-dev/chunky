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

package se.llbit.chunky.model.model;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class LanternModel extends QuadModel {

  //region Model
  private static final Quad[] model =
      new Quad[]{
          new Quad(
              new Vector3(5 / 16.0, 7 / 16.0, 11 / 16.0),
              new Vector3(11 / 16.0, 7 / 16.0, 11 / 16.0),
              new Vector3(5 / 16.0, 7 / 16.0, 5 / 16.0),
              new Vector4(0 / 16.0, 6 / 16.0, 1 / 16.0, 7 / 16.0)),
          new Quad(
              new Vector3(5 / 16.0, 0 / 16.0, 5 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 5 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 11 / 16.0),
              new Vector4(0 / 16.0, 6 / 16.0, 1 / 16.0, 7 / 16.0)),
          new Quad(
              new Vector3(5 / 16.0, 7 / 16.0, 11 / 16.0),
              new Vector3(5 / 16.0, 7 / 16.0, 5 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 11 / 16.0),
              new Vector4(6 / 16.0, 0 / 16.0, 14 / 16.0, 7 / 16.0)),
          new Quad(
              new Vector3(11 / 16.0, 7 / 16.0, 5 / 16.0),
              new Vector3(11 / 16.0, 7 / 16.0, 11 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 5 / 16.0),
              new Vector4(6 / 16.0, 0 / 16.0, 14 / 16.0, 7 / 16.0)),
          new Quad(
              new Vector3(5 / 16.0, 7 / 16.0, 5 / 16.0),
              new Vector3(11 / 16.0, 7 / 16.0, 5 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 5 / 16.0),
              new Vector4(6 / 16.0, 0 / 16.0, 14 / 16.0, 7 / 16.0)),
          new Quad(
              new Vector3(11 / 16.0, 7 / 16.0, 11 / 16.0),
              new Vector3(5 / 16.0, 7 / 16.0, 11 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 11 / 16.0),
              new Vector4(6 / 16.0, 0 / 16.0, 14 / 16.0, 7 / 16.0)),
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 10 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 10 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 6 / 16.0),
              new Vector4(1 / 16.0, 5 / 16.0, 2 / 16.0, 6 / 16.0)),
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 10 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 6 / 16.0),
              new Vector3(6 / 16.0, 7 / 16.0, 10 / 16.0),
              new Vector4(5 / 16.0, 1 / 16.0, 16 / 16.0, 14 / 16.0)),
          new Quad(
              new Vector3(10 / 16.0, 9 / 16.0, 6 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 10 / 16.0),
              new Vector3(10 / 16.0, 7 / 16.0, 6 / 16.0),
              new Vector4(5 / 16.0, 1 / 16.0, 16 / 16.0, 14 / 16.0)),
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 6 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 6 / 16.0),
              new Vector3(6 / 16.0, 7 / 16.0, 6 / 16.0),
              new Vector4(5 / 16.0, 1 / 16.0, 16 / 16.0, 14 / 16.0)),
          new Quad(
              new Vector3(10 / 16.0, 9 / 16.0, 10 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 10 / 16.0),
              new Vector3(10 / 16.0, 7 / 16.0, 10 / 16.0),
              new Vector4(5 / 16.0, 1 / 16.0, 16 / 16.0, 14 / 16.0)),
          new Quad(
              new Vector3(6.5 / 16.0, 11 / 16.0, 8 / 16.0),
              new Vector3(9.5 / 16.0, 11 / 16.0, 8 / 16.0),
              new Vector3(6.5 / 16.0, 9 / 16.0, 8 / 16.0),
              new Vector4(11 / 16.0, 14 / 16.0, 15 / 16.0, 13 / 16.0)
          ),
          new Quad(
              new Vector3(9.5 / 16.0, 11 / 16.0, 8 / 16.0),
              new Vector3(6.5 / 16.0, 11 / 16.0, 8 / 16.0),
              new Vector3(9.5 / 16.0, 9 / 16.0, 8 / 16.0),
              new Vector4(14 / 16.0, 11 / 16.0, 15 / 16.0, 13 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 11 / 16.0, 9.5 / 16.0),
              new Vector3(8 / 16.0, 11 / 16.0, 6.5 / 16.0),
              new Vector3(8 / 16.0, 9 / 16.0, 9.5 / 16.0),
              new Vector4(11 / 16.0, 14 / 16.0, 6 / 16.0, 4 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 11 / 16.0, 6.5 / 16.0),
              new Vector3(8 / 16.0, 11 / 16.0, 9.5 / 16.0),
              new Vector3(8 / 16.0, 9 / 16.0, 6.5 / 16.0),
              new Vector4(14 / 16.0, 11 / 16.0, 6 / 16.0, 4 / 16.0)
          )
      };
  //endregion

  //region Hanging
  private static final Quad[] modelHanging =
      Model.join(
          new Quad[]{
              new Quad(
                  new Vector3(5 / 16.0, 8 / 16.0, 11 / 16.0),
                  new Vector3(11 / 16.0, 8 / 16.0, 11 / 16.0),
                  new Vector3(5 / 16.0, 8 / 16.0, 5 / 16.0),
                  new Vector4(0 / 16.0, 6 / 16.0, 1 / 16.0, 7 / 16.0)),
              new Quad(
                  new Vector3(5 / 16.0, 1 / 16.0, 5 / 16.0),
                  new Vector3(11 / 16.0, 1 / 16.0, 5 / 16.0),
                  new Vector3(5 / 16.0, 1 / 16.0, 11 / 16.0),
                  new Vector4(0 / 16.0, 6 / 16.0, 1 / 16.0, 7 / 16.0)),
              new Quad(
                  new Vector3(5 / 16.0, 8 / 16.0, 11 / 16.0),
                  new Vector3(5 / 16.0, 8 / 16.0, 5 / 16.0),
                  new Vector3(5 / 16.0, 1 / 16.0, 11 / 16.0),
                  new Vector4(6 / 16.0, 0 / 16.0, 14 / 16.0, 7 / 16.0)),
              new Quad(
                  new Vector3(11 / 16.0, 8 / 16.0, 5 / 16.0),
                  new Vector3(11 / 16.0, 8 / 16.0, 11 / 16.0),
                  new Vector3(11 / 16.0, 1 / 16.0, 5 / 16.0),
                  new Vector4(6 / 16.0, 0 / 16.0, 14 / 16.0, 7 / 16.0)),
              new Quad(
                  new Vector3(5 / 16.0, 8 / 16.0, 5 / 16.0),
                  new Vector3(11 / 16.0, 8 / 16.0, 5 / 16.0),
                  new Vector3(5 / 16.0, 1 / 16.0, 5 / 16.0),
                  new Vector4(6 / 16.0, 0 / 16.0, 14 / 16.0, 7 / 16.0)),
              new Quad(
                  new Vector3(11 / 16.0, 8 / 16.0, 11 / 16.0),
                  new Vector3(5 / 16.0, 8 / 16.0, 11 / 16.0),
                  new Vector3(11 / 16.0, 1 / 16.0, 11 / 16.0),
                  new Vector4(6 / 16.0, 0 / 16.0, 14 / 16.0, 7 / 16.0)),
              new Quad(
                  new Vector3(6 / 16.0, 10 / 16.0, 10 / 16.0),
                  new Vector3(10 / 16.0, 10 / 16.0, 10 / 16.0),
                  new Vector3(6 / 16.0, 10 / 16.0, 6 / 16.0),
                  new Vector4(1 / 16.0, 5 / 16.0, 2 / 16.0, 6 / 16.0)),
              new Quad(
                  new Vector3(6 / 16.0, 8 / 16.0, 6 / 16.0),
                  new Vector3(10 / 16.0, 8 / 16.0, 6 / 16.0),
                  new Vector3(6 / 16.0, 8 / 16.0, 10 / 16.0),
                  new Vector4(1 / 16.0, 5 / 16.0, 2 / 16.0, 6 / 16.0)),
              new Quad(
                  new Vector3(6 / 16.0, 10 / 16.0, 10 / 16.0),
                  new Vector3(6 / 16.0, 10 / 16.0, 6 / 16.0),
                  new Vector3(6 / 16.0, 8 / 16.0, 10 / 16.0),
                  new Vector4(5 / 16.0, 1 / 16.0, 16 / 16.0, 14 / 16.0)),
              new Quad(
                  new Vector3(10 / 16.0, 10 / 16.0, 6 / 16.0),
                  new Vector3(10 / 16.0, 10 / 16.0, 10 / 16.0),
                  new Vector3(10 / 16.0, 8 / 16.0, 6 / 16.0),
                  new Vector4(5 / 16.0, 1 / 16.0, 16 / 16.0, 14 / 16.0)),
              new Quad(
                  new Vector3(6 / 16.0, 10 / 16.0, 6 / 16.0),
                  new Vector3(10 / 16.0, 10 / 16.0, 6 / 16.0),
                  new Vector3(6 / 16.0, 8 / 16.0, 6 / 16.0),
                  new Vector4(5 / 16.0, 1 / 16.0, 16 / 16.0, 14 / 16.0)),
              new Quad(
                  new Vector3(10 / 16.0, 10 / 16.0, 10 / 16.0),
                  new Vector3(6 / 16.0, 10 / 16.0, 10 / 16.0),
                  new Vector3(10 / 16.0, 8 / 16.0, 10 / 16.0),
                  new Vector4(5 / 16.0, 1 / 16.0, 16 / 16.0, 14 / 16.0)),
          },
          Model.rotateY(
              new Quad[]{
                  new Quad(
                      new Vector3(6.5 / 16.0, 15 / 16.0, 8 / 16.0),
                      new Vector3(9.5 / 16.0, 15 / 16.0, 8 / 16.0),
                      new Vector3(6.5 / 16.0, 11 / 16.0, 8 / 16.0),
                      new Vector4(11 / 16.0, 14 / 16.0, 15 / 16.0, 11 / 16.0)
                  ),
                  new Quad(
                      new Vector3(9.5 / 16.0, 15 / 16.0, 8 / 16.0),
                      new Vector3(6.5 / 16.0, 15 / 16.0, 8 / 16.0),
                      new Vector3(9.5 / 16.0, 11 / 16.0, 8 / 16.0),
                      new Vector4(14 / 16.0, 11 / 16.0, 15 / 16.0, 11 / 16.0)
                  ),
                  new Quad(
                      new Vector3(8 / 16.0, 16 / 16.0, 9.5 / 16.0),
                      new Vector3(8 / 16.0, 16 / 16.0, 6.5 / 16.0),
                      new Vector3(8 / 16.0, 10 / 16.0, 9.5 / 16.0),
                      new Vector4(11 / 16.0, 14 / 16.0, 10 / 16.0, 4 / 16.0)
                  ),
                  new Quad(
                      new Vector3(8 / 16.0, 16 / 16.0, 6.5 / 16.0),
                      new Vector3(8 / 16.0, 16 / 16.0, 9.5 / 16.0),
                      new Vector3(8 / 16.0, 10 / 16.0, 6.5 / 16.0),
                      new Vector4(14 / 16.0, 11 / 16.0, 10 / 16.0, 4 / 16.0)
                  )
              },
              Math.toRadians(45)));
  //endregion

  private final Quad[] quads;
  private final Texture[] textures;

  public LanternModel(Texture texture, boolean hanging) {
    quads = hanging ? modelHanging : model;
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
