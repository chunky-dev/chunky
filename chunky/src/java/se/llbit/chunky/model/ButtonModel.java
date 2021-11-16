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

import java.util.Arrays;

public class ButtonModel extends QuadModel {
  private static final Quad[] attachedSouth = {
      // front
      new Quad(new Vector3(.6875, .375, .875), new Vector3(.3125, .375, .875),
          new Vector3(.6875, .625, .875), new Vector4(.6875, .3125, .375, .625)),

      // back
      new Quad(new Vector3(.3125, .375, 1), new Vector3(.6875, .375, 1),
          new Vector3(.3125, .625, 1), new Vector4(.3125, .6875, .375, .625)),

      // right
      new Quad(new Vector3(.3125, .375, .875), new Vector3(.3125, .375, 1),
          new Vector3(.3125, .625, .875), new Vector4(.875, 1, .375, .625)),

      // left
      new Quad(new Vector3(.6875, .375, 1), new Vector3(.6875, .375, .875),
          new Vector3(.6875, .625, 1), new Vector4(1, .875, .375, .625)),

      // top
      new Quad(new Vector3(.6875, .625, .875), new Vector3(.3125, .625, .875),
          new Vector3(.6875, .625, 1), new Vector4(.6875, .3125, .875, 1)),

      // bottom
      new Quad(new Vector3(.3125, .375, .875), new Vector3(.6875, .375, .875),
          new Vector3(.3125, .375, 1), new Vector4(.3125, .6875, .875, 1)),
  };

  private final Quad[] quads;
  private final Texture[] textures;

  public ButtonModel(String face, String facing, Texture tex) {
    textures = new Texture[attachedSouth.length];
    Arrays.fill(textures, tex);

    switch (face) {
      case "ceiling":
        quads = Model.rotateNegX(attachedSouth);
        break;
      case "wall":
        switch (facing) {
          default:
          case "north":
            quads = attachedSouth;
            break;
          case "south":
            quads = Model.rotateY(Model.rotateY(attachedSouth));
            break;
          case "west":
            quads = Model.rotateNegY(attachedSouth);
            break;
          case "east":
            quads = Model.rotateY(attachedSouth);
            break;
        }
        break;
      case "floor":
      default:
        quads = Model.rotateX(attachedSouth);
    }
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
