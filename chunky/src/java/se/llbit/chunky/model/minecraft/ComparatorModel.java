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

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class ComparatorModel extends QuadModel {
  // The comparator base-plate facing north:
  private static final Quad[] north = {
    // Front face.
    new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, .125, 0),
      new Vector4(1, 0, 0, .125)),

    // Back face.
    new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 1), new Vector3(0, .125, 1),
      new Vector4(0, 1, 0, .125)),

    // Right face.
    new Quad(new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(0, .125, 0),
      new Vector4(0, 1, 0, .125)),

    // Left face.
    new Quad(new Vector3(1, 0, 1), new Vector3(1, 0, 0), new Vector3(1, .125, 1),
      new Vector4(1, 0, 0, .125)),

    // Top face.
    new Quad(new Vector3(1, .125, 0), new Vector3(0, .125, 0), new Vector3(1, .125, 1),
      new Vector4(1, 0, 1, 0)),
  };

  private static final Quad[] torchHigh = {
    new Quad(new Vector3(.75, 2 / 16., 7 / 16.), new Vector3(4 / 16., 2 / 16., 7 / 16.),
      new Vector3(.75, 13 / 16., 7 / 16.), new Vector4(12 / 16., 4 / 16., 5 / 16., 1)),

    new Quad(new Vector3(4 / 16., 2 / 16., 9 / 16.), new Vector3(.75, 2 / 16., 9 / 16.),
      new Vector3(4 / 16., 13 / 16., 9 / 16.), new Vector4(4 / 16., .75, 5 / 16., 1)),

    new Quad(new Vector3(7 / 16., 2 / 16., 4 / 16.), new Vector3(7 / 16., 2 / 16., .75),
      new Vector3(7 / 16., 13 / 16., 4 / 16.), new Vector4(4 / 16., .75, 5 / 16., 1)),

    new Quad(new Vector3(9 / 16., 2 / 16., .75), new Vector3(9 / 16., 2 / 16., 4 / 16.),
      new Vector3(9 / 16., 13 / 16., .75), new Vector4(.75, 4 / 16., 5 / 16., 1)),

    // Top face.
    new Quad(new Vector3(7 / 16., 7 / 16., 9 / 16.), new Vector3(9 / 16., 7 / 16., 9 / 16.),
      new Vector3(7 / 16., 7 / 16., 7 / 16.),
      new Vector4(7 / 16., 9 / 16., 8 / 16., .625))
  };

  // The lowered torch is 3 texels lower than the high version.
  private static final Quad[] torchLow = {
    new Quad(new Vector3(.75, 2 / 16., 7 / 16.), new Vector3(4 / 16., 2 / 16., 7 / 16.),
      new Vector3(.75, 10 / 16., 7 / 16.), new Vector4(12 / 16., 4 / 16., 8 / 16., 1)),

    new Quad(new Vector3(4 / 16., 2 / 16., 9 / 16.), new Vector3(.75, 2 / 16., 9 / 16.),
      new Vector3(4 / 16., 10 / 16., 9 / 16.), new Vector4(4 / 16., .75, 8 / 16., 1)),

    new Quad(new Vector3(7 / 16., 2 / 16., 4 / 16.), new Vector3(7 / 16., 2 / 16., .75),
      new Vector3(7 / 16., 10 / 16., 4 / 16.), new Vector4(4 / 16., .75, 8 / 16., 1)),

    new Quad(new Vector3(9 / 16., 2 / 16., .75), new Vector3(9 / 16., 2 / 16., 4 / 16.),
      new Vector3(9 / 16., 10 / 16., .75), new Vector4(.75, 4 / 16., 8 / 16., 1)),

    // Top face.
    new Quad(new Vector3(7 / 16., 4 / 16., 9 / 16.), new Vector3(9 / 16., 4 / 16., 9 / 16.),
      new Vector3(7 / 16., 4 / 16., 7 / 16.),
      new Vector4(7 / 16., 9 / 16., 8 / 16., .625))
  };

  private static final Quad[][][] torch1 = new Quad[2][4][];
  private static final Quad[][][] torch2 = new Quad[2][4][];
  private static final Quad[][][] torch3 = new Quad[2][4][];

  private static final Quad[][] rot = new Quad[4][];

  private static final Texture[] blockTex = {Texture.comparatorOff, Texture.comparatorOn,};

  private static final Texture[] torchTex = {Texture.redstoneTorchOff, Texture.redstoneTorchOn,};

  static {
    rot[0] = north;
    rot[1] = Model.rotateY(rot[0]);
    rot[2] = Model.rotateY(rot[1]);
    rot[3] = Model.rotateY(rot[2]);

    torch1[0][0] = Model.translate(torchLow, 0, 0, -5 / 16.);
    torch1[0][1] = Model.rotateY(torch1[0][0]);
    torch1[0][2] = Model.rotateY(torch1[0][1]);
    torch1[0][3] = Model.rotateY(torch1[0][2]);

    torch1[1][0] = Model.translate(torchHigh, 0, 0, -5 / 16.);
    torch1[1][1] = Model.rotateY(torch1[1][0]);
    torch1[1][2] = Model.rotateY(torch1[1][1]);
    torch1[1][3] = Model.rotateY(torch1[1][2]);

    torch2[0][0] = Model.translate(torchHigh, 3 / 16., 0, 4 / 16.);
    torch2[0][1] = Model.rotateY(torch2[0][0]);
    torch2[0][2] = Model.rotateY(torch2[0][1]);
    torch2[0][3] = Model.rotateY(torch2[0][2]);

    torch2[1][0] = Model.translate(torchHigh, 3 / 16., 0, 4 / 16.);
    torch2[1][1] = Model.rotateY(torch2[1][0]);
    torch2[1][2] = Model.rotateY(torch2[1][1]);
    torch2[1][3] = Model.rotateY(torch2[1][2]);

    torch3[0][0] = Model.translate(torchHigh, -3 / 16., 0, 4 / 16.);
    torch3[0][1] = Model.rotateY(torch3[0][0]);
    torch3[0][2] = Model.rotateY(torch3[0][1]);
    torch3[0][3] = Model.rotateY(torch3[0][2]);

    torch3[1][0] = Model.translate(torchHigh, -3 / 16., 0, 4 / 16.);
    torch3[1][1] = Model.rotateY(torch3[1][0]);
    torch3[1][2] = Model.rotateY(torch3[1][1]);
    torch3[1][3] = Model.rotateY(torch3[1][2]);
  }

  private final Quad[] quads;
  private final Texture[] textures;

  public ComparatorModel(String facing, String mode, boolean powered) {
    int direction = switch (facing) {
      case "north" -> 2;
      case "south" -> 0;
      case "west" -> 1;
      case "east" -> 3;
      default -> 0;
    };
    int active = mode.equals("subtract") ? 1 : 0;
    quads = Model.join(rot[direction], torch1[active][direction],
      torch2[active][direction], torch3[active][direction]);
    textures = new Texture[quads.length];
    Arrays.fill(textures, torchTex[powered ? 1 : 0]);
    Arrays.fill(textures, 0, rot[direction].length, blockTex[powered ? 1 : 0]);
    Arrays.fill(textures,
      rot[direction].length,
      rot[direction].length + torch1[active][direction].length,
      torchTex[active]);
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
