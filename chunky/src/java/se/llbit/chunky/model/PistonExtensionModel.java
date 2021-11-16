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

public class PistonExtensionModel extends QuadModel {
  private static final Quad[][] extension = {
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
          new Quad(new Vector3(0, 0, .25), new Vector3(1, 0, .25), new Vector3(0, 1, .25),
              new Vector4(0, 1, 0, 1)),

          // west
          new Quad(new Vector3(0, 1, 0), new Vector3(0, 0, 0), new Vector3(0, 1, .25),
              new Vector4(0, 1, 1, .75)),

          // east
          new Quad(new Vector3(1, 1, .25), new Vector3(1, 0, .25), new Vector3(1, 1, 0),
              new Vector4(1, 0, .75, 1)),

          // top
          new Quad(new Vector3(1, 1, 0), new Vector3(0, 1, 0), new Vector3(1, 1, .25),
              new Vector4(1, 0, 1, .75)),

          // bottom
          new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, .25),
              new Vector4(0, 1, 1, .75)),

          // extension west
          new Quad(new Vector3(.375, .375, .25), new Vector3(.375, .375, 1),
              new Vector3(.375, .625, .25), new Vector4(1, .25, .75, 1)),

          // extension east
          new Quad(new Vector3(.625, .375, 1), new Vector3(.625, .375, .25),
              new Vector3(.625, .625, 1), new Vector4(.25, 1, .75, 1)),

          // extension top
          new Quad(new Vector3(.375, .625, .25), new Vector3(.375, .625, 1),
              new Vector3(.625, .625, .25), new Vector4(1, .25, .75, 1)),

          // extension bottom
          new Quad(new Vector3(.375, .375, 1), new Vector3(.375, .375, .25),
              new Vector3(.625, .375, 1), new Vector4(.25, 1, .75, 1)),},

      // facing south
      {},

      //facing west
      {},

      // facing east
      {},};

  static {
    extension[0] = Model.rotateNegX(extension[2]);
    extension[1] = Model.rotateX(extension[2]);
    extension[5] = Model.rotateY(extension[2]);
    extension[3] = Model.rotateY(extension[5]);
    extension[4] = Model.rotateY(extension[3]);
  }

  private static final Texture[] textureNormal = {
      Texture.pistonTop, Texture.pistonTop, Texture.pistonSide, Texture.pistonSide,
      Texture.pistonSide, Texture.pistonSide, Texture.pistonSide, Texture.pistonSide,
      Texture.pistonSide, Texture.pistonSide
  };

  private static final Texture[] textureSticky = {
      Texture.pistonTopSticky, Texture.pistonTop, Texture.pistonSide, Texture.pistonSide,
      Texture.pistonSide, Texture.pistonSide, Texture.pistonSide, Texture.pistonSide,
      Texture.pistonSide, Texture.pistonSide,
  };

  private final Quad[] quads;
  private final Texture[] textures;

  public PistonExtensionModel(boolean sticky, int facing) {
    quads = extension[facing];
    textures = sticky ? textureSticky : textureNormal;
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
