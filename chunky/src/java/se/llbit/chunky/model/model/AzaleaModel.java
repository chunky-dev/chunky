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

public class AzaleaModel extends QuadModel {

  //region Azalea Model
  private static final Quad[] quads = Model.join(
      new Quad[]{
          // top
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          // side
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 5 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 16 / 16.0, 0.01 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 0.01 / 16.0),
              new Vector3(16 / 16.0, 5 / 16.0, 0.01 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 15.99 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 15.99 / 16.0),
              new Vector3(0 / 16.0, 5 / 16.0, 15.99 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 5 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 5 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(0.01 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(0.01 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(0.01 / 16.0, 5 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(15.99 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(15.99 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(15.99 / 16.0, 5 / 16.0, 16 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 5 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 5 / 16.0)
          ),
      },
      // plant
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(0.1 / 16.0, 15.9 / 16.0, 8 / 16.0),
              new Vector3(15.9 / 16.0, 15.9 / 16.0, 8 / 16.0),
              new Vector3(0.1 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(15.9 / 16.0, 15.9 / 16.0, 8 / 16.0),
              new Vector3(0.1 / 16.0, 15.9 / 16.0, 8 / 16.0),
              new Vector3(15.9 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
      }, Math.toRadians(45)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(8 / 16.0, 15.9 / 16.0, 15.9 / 16.0),
              new Vector3(8 / 16.0, 15.9 / 16.0, 0.1 / 16.0),
              new Vector3(8 / 16.0, 0 / 16.0, 15.9 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 15.9 / 16.0, 0.1 / 16.0),
              new Vector3(8 / 16.0, 15.9 / 16.0, 15.9 / 16.0),
              new Vector3(8 / 16.0, 0 / 16.0, 0.1 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          )
      }, Math.toRadians(45))
  );
  //endregion

  private final Texture[] textures;

  public AzaleaModel(Texture top, Texture side) {
    Texture plant = Texture.azaleaPlant;
    textures = new Texture[14];
    for (int i = 0; i < 2; i++) textures[i] = top;
    for (int i = 2; i < 10; i++) textures[i] = side;
    for (int i = 10; i < 14; i++) textures[i] = plant;
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
