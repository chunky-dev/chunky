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

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Constants;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Ray2;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class SpriteModel extends QuadModel {

  protected static final Quad[] quads =
      {new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 1), new Vector3(0, 1, 0),
          new Vector4(0, 1, 0, 1)),

          new Quad(new Vector3(1, 0, 1), new Vector3(0, 0, 0), new Vector3(1, 1, 1),
              new Vector4(0, 1, 0, 1)),

          new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 1), new Vector3(1, 1, 0),
              new Vector4(0, 1, 0, 1)),

          new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 0), new Vector3(0, 1, 1),
              new Vector4(0, 1, 0, 1)),};

  static final Quad[][] orientedQuads = new Quad[6][];

  static {
    orientedQuads[4] = quads;
    orientedQuads[0] = Model.rotateX(Model.rotateX(quads));
    orientedQuads[2] = Model.rotateNegX(quads);
    orientedQuads[1] = Model.rotateY(orientedQuads[2]);
    orientedQuads[3] = Model.rotateY(orientedQuads[1]);
    orientedQuads[5] = Model.rotateY(orientedQuads[3]);
  }

  private final Texture[] textures;
  private final int facing;

  public SpriteModel(Texture texture, String facing) {
    this.textures = new Texture[]{
        texture, texture, texture, texture
    };
    this.facing = getOrientationIndex(facing);
  }

  public SpriteModel(Texture texture) {
    this(texture, "up");
  }

  @Override
  public Quad[] getQuads() {
    return orientedQuads[facing];
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }

  private static int getOrientationIndex(String facing) {
    switch (facing) {
      case "down":
        return 0;
      case "east":
        return 1;
      case "north":
        return 2;
      case "south":
        return 3;
      default:
      case "up":
        return 4;
      case "west":
        return 5;
    }
  }
}
