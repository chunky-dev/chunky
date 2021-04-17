/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class PistonModel extends QuadModel {
  private static final Quad[][] retracted = {
      // down
      {},

      // up
      {},

      // facing north
      {
          // north
          new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 1, 0),
              new Vector4(1, 0, 0, 1)),

          // south
          new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 1), new Vector3(0, 1, 1),
              new Vector4(0, 1, 0, 1)),

          // west
          new Quad(new Vector3(0, 1, 0), new Vector3(0, 0, 0), new Vector3(0, 1, 1),
              new Vector4(0, 1, 1, 0)),

          // east
          new Quad(new Vector3(1, 1, 1), new Vector3(1, 0, 1), new Vector3(1, 1, 0),
              new Vector4(1, 0, 0, 1)),

          // top
          new Quad(new Vector3(1, 1, 0), new Vector3(0, 1, 0), new Vector3(1, 1, 1),
              new Vector4(1, 0, 1, 0)),

          // bottom
          new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
              new Vector4(0, 1, 1, 0)),},

      // facing south
      {},

      //facing west
      {},

      // facing east
      {},};

  private static final Quad[][] extended = {
      // down
      {},

      // up
      {},

      // facing north
      {
          // north
          new Quad(new Vector3(1, 0, .25), new Vector3(0, 0, .25), new Vector3(1, 1, .25),
              new Vector4(1, 0, 0, 1)),

          // south
          new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 1), new Vector3(0, 1, 1),
              new Vector4(0, 1, 0, 1)),

          // west
          new Quad(new Vector3(0, 1, .25), new Vector3(0, 0, .25), new Vector3(0, 1, 1),
              new Vector4(0, 1, .75, 0)),

          // east
          new Quad(new Vector3(1, 1, 1), new Vector3(1, 0, 1), new Vector3(1, 1, .25),
              new Vector4(1, 0, 0, .75)),

          // top
          new Quad(new Vector3(1, 1, .25), new Vector3(0, 1, .25), new Vector3(1, 1, 1),
              new Vector4(1, 0, .75, 0)),

          // bottom
          new Quad(new Vector3(0, 0, .25), new Vector3(1, 0, .25), new Vector3(0, 0, 1),
              new Vector4(0, 1, .75, 0)),

          // extension west
          new Quad(new Vector3(.375, .375, 0), new Vector3(.375, .375, .25),
              new Vector3(.375, .625, 0), new Vector4(.25, 0, .75, 1)),

          // extension east
          new Quad(new Vector3(.625, .375, .25), new Vector3(.625, .375, 0),
              new Vector3(.625, .625, .25), new Vector4(0, .25, .75, 1)),

          // extension top
          new Quad(new Vector3(.375, .625, 0), new Vector3(.375, .625, .25),
              new Vector3(.625, .625, 0), new Vector4(.25, 0, .75, 1)),

          // extension bottom
          new Quad(new Vector3(.375, .375, .25), new Vector3(.375, .375, 0),
              new Vector3(.625, .375, .25), new Vector4(0, .25, .75, 1)),},

      // facing south
      {},

      //facing west
      {},

      // facing east
      {},};

  static {
    extended[0] = Model.rotateNegX(extended[2]);
    extended[1] = Model.rotateX(extended[2]);
    extended[5] = Model.rotateY(extended[2]);
    extended[3] = Model.rotateY(extended[5]);
    extended[4] = Model.rotateY(extended[3]);
    retracted[0] = Model.rotateNegX(retracted[2]);
    retracted[1] = Model.rotateX(retracted[2]);
    retracted[5] = Model.rotateY(retracted[2]);
    retracted[3] = Model.rotateY(retracted[5]);
    retracted[4] = Model.rotateY(retracted[3]);
  }

  static final Texture[][][] texture = {
    {
      {
        Texture.pistonTop, Texture.pistonBottom, Texture.pistonSide, Texture.pistonSide,
        Texture.pistonSide, Texture.pistonSide, Texture.pistonSide, Texture.pistonSide,
        Texture.pistonSide, Texture.pistonSide,
      }, {
        Texture.pistonTopSticky, Texture.pistonBottom, Texture.pistonSide, Texture.pistonSide,
        Texture.pistonSide, Texture.pistonSide, Texture.pistonSide, Texture.pistonSide,
        Texture.pistonSide, Texture.pistonSide,
      },
    },
    {{
        Texture.pistonInnerTop, Texture.pistonBottom, Texture.pistonSide, Texture.pistonSide,
        Texture.pistonSide, Texture.pistonSide, Texture.pistonSide, Texture.pistonSide,
        Texture.pistonSide, Texture.pistonSide,
    }},
  };

  public final Quad[] quads;
  public final Texture[] textures;

  public PistonModel(boolean sticky, boolean isExtended, int facing) {
    quads = isExtended ? extended[facing] : retracted[facing];
    textures = isExtended ? texture[1][0] : (sticky ? texture[0][1] : texture[0][0]);
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
