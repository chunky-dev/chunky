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
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class RedstoneRepeaterModel extends QuadModel {
  //region Body
  private static final Quad[] north = {
      // Front.
      new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, .125, 0),
          new Vector4(1, 0, 0, .125)),

      // Back.
      new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 1), new Vector3(0, .125, 1),
          new Vector4(0, 1, 0, .125)),

      // Right.
      new Quad(new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(0, .125, 0),
          new Vector4(0, 1, 0, .125)),

      // Left.
      new Quad(new Vector3(1, 0, 1), new Vector3(1, 0, 0), new Vector3(1, .125, 1),
          new Vector4(1, 0, 0, .125)),

      // Top.
      new Quad(new Vector3(1, .125, 0), new Vector3(0, .125, 0), new Vector3(1, .125, 1),
          new Vector4(1, 0, 1, 0)),
  };
  //endregion

  //region Torch
  private static final Quad[] torch = {
      new Quad(new Vector3(.75, 2 / 16., .4375), new Vector3(.25, 2 / 16., .4375),
          new Vector3(.75, 14 / 16., .4375), new Vector4(.75, .25, 4 / 16., 1)),

      new Quad(new Vector3(.25, 2 / 16., .5625), new Vector3(.75, 2 / 16., .5625),
          new Vector3(.25, 14 / 16., .5625), new Vector4(.25, .75, 4 / 16., 1)),

      new Quad(new Vector3(.4375, 2 / 16., .25), new Vector3(.4375, 2 / 16., .75),
          new Vector3(.4375, 14 / 16., .25), new Vector4(.25, .75, 4 / 16., 1)),

      new Quad(new Vector3(.5625, 2 / 16., .75), new Vector3(.5625, 2 / 16., .25),
          new Vector3(.5625, 14 / 16., .75), new Vector4(.75, .25, 4 / 16., 1)),

      // Top.
      new Quad(new Vector3(.4375, 8 / 16., .5625), new Vector3(.5625, 8 / 16., .5625),
          new Vector3(.4375, 8 / 16., .4375), new Vector4(.4375, .5625, .5, .625)),
  };
  //endregion

  //region Lock
  // {"elements":[{"from":[2,2,8],"to":[14,4,10],"faces":{"up":{"uv":[7,2,9,14],"texture":"#lock","rotation":1},"down":{"uv":[7,2,9,14],"texture":"#lock","rotation":1},"east":{"uv":[6,7,8,9],"texture":"#lock"},"west":{"uv":[6,7,8,9],"texture":"#lock"},"north":{"uv":[2,7,14,9],"texture":"#lock"},"south":{"uv":[2,7,14,9],"texture":"#lock"}}}]}
  private static final Quad[] lock = {
      new Quad(
          new Vector3(2 / 16.0, 4 / 16.0, 8 / 16.0),
          new Vector3(2 / 16.0, 4 / 16.0, 10 / 16.0),
          new Vector3(14 / 16.0, 4 / 16.0, 8 / 16.0),
          new Vector4(7 / 16.0, 9 / 16.0, 2 / 16.0, 14 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 2 / 16.0, 10 / 16.0),
          new Vector3(2 / 16.0, 2 / 16.0, 8 / 16.0),
          new Vector3(14 / 16.0, 2 / 16.0, 10 / 16.0),
          new Vector4(7 / 16.0, 9 / 16.0, 2 / 16.0, 14 / 16.0)),
      new Quad(
          new Vector3(14 / 16.0, 2 / 16.0, 10 / 16.0),
          new Vector3(14 / 16.0, 2 / 16.0, 8 / 16.0),
          new Vector3(14 / 16.0, 4 / 16.0, 10 / 16.0),
          new Vector4(6 / 16.0, 8 / 16.0, 7 / 16.0, 9 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 2 / 16.0, 8 / 16.0),
          new Vector3(2 / 16.0, 2 / 16.0, 10 / 16.0),
          new Vector3(2 / 16.0, 4 / 16.0, 8 / 16.0),
          new Vector4(6 / 16.0, 8 / 16.0, 7 / 16.0, 9 / 16.0)),
      new Quad(
          new Vector3(14 / 16.0, 2 / 16.0, 8 / 16.0),
          new Vector3(2 / 16.0, 2 / 16.0, 8 / 16.0),
          new Vector3(14 / 16.0, 4 / 16.0, 8 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 7 / 16.0, 9 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 2 / 16.0, 10 / 16.0),
          new Vector3(14 / 16.0, 2 / 16.0, 10 / 16.0),
          new Vector3(2 / 16.0, 4 / 16.0, 10 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 7 / 16.0, 9 / 16.0)),
  };
  //endregion

  private static final Quad[][] torch1 = new Quad[4][];
  private static final Quad[][][][] torch2 = new Quad[2][4][4][];

  private static final Quad[][] rot = new Quad[4][];

  private static final AbstractTexture[] tex = {Texture.redstoneRepeaterOff, Texture.redstoneRepeaterOn,};

  private static final AbstractTexture[][] torchTex = {
      { Texture.redstoneTorchOff, Texture.redstoneTorchOn, },
      { Texture.bedrock, Texture.bedrock, },
  };

  static {
    rot[0] = north;
    rot[1] = Model.rotateY(rot[0]);
    rot[2] = Model.rotateY(rot[1]);
    rot[3] = Model.rotateY(rot[2]);

    torch1[0] = Model.translate(torch, 0, 0, -5 / 16.);
    torch1[1] = Model.rotateY(torch1[0]);
    torch1[2] = Model.rotateY(torch1[1]);
    torch1[3] = Model.rotateY(torch1[2]);

    torch2[0][0][0] = Model.translate(torch, 0, 0, -1 / 16.);
    torch2[0][0][1] = Model.rotateY(torch2[0][0][0]);
    torch2[0][0][2] = Model.rotateY(torch2[0][0][1]);
    torch2[0][0][3] = Model.rotateY(torch2[0][0][2]);

    torch2[0][1][0] = Model.translate(torch, 0, 0, 1 / 16.);
    torch2[0][1][1] = Model.rotateY(torch2[0][1][0]);
    torch2[0][1][2] = Model.rotateY(torch2[0][1][1]);
    torch2[0][1][3] = Model.rotateY(torch2[0][1][2]);

    torch2[0][2][0] = Model.translate(torch, 0, 0, 3 / 16.);
    torch2[0][2][1] = Model.rotateY(torch2[0][2][0]);
    torch2[0][2][2] = Model.rotateY(torch2[0][2][1]);
    torch2[0][2][3] = Model.rotateY(torch2[0][2][2]);

    torch2[0][3][0] = Model.translate(torch, 0, 0, 5 / 16.);
    torch2[0][3][1] = Model.rotateY(torch2[0][3][0]);
    torch2[0][3][2] = Model.rotateY(torch2[0][3][1]);
    torch2[0][3][3] = Model.rotateY(torch2[0][3][2]);

    torch2[1][0][0] = Model.translate(lock, 0, 0, -1 / 16.);
    torch2[1][0][1] = Model.rotateY(torch2[1][0][0]);
    torch2[1][0][2] = Model.rotateY(torch2[1][0][1]);
    torch2[1][0][3] = Model.rotateY(torch2[1][0][2]);

    torch2[1][1][0] = Model.translate(lock, 0, 0, 1 / 16.);
    torch2[1][1][1] = Model.rotateY(torch2[1][1][0]);
    torch2[1][1][2] = Model.rotateY(torch2[1][1][1]);
    torch2[1][1][3] = Model.rotateY(torch2[1][1][2]);

    torch2[1][2][0] = Model.translate(lock, 0, 0, 3 / 16.);
    torch2[1][2][1] = Model.rotateY(torch2[1][2][0]);
    torch2[1][2][2] = Model.rotateY(torch2[1][2][1]);
    torch2[1][2][3] = Model.rotateY(torch2[1][2][2]);

    torch2[1][3][0] = Model.translate(lock, 0, 0, 5 / 16.);
    torch2[1][3][1] = Model.rotateY(torch2[1][3][0]);
    torch2[1][3][2] = Model.rotateY(torch2[1][3][1]);
    torch2[1][3][3] = Model.rotateY(torch2[1][3][2]);
  }

  private final Quad[] quads;
  private final AbstractTexture[] textures;

  public RedstoneRepeaterModel(int delay, int direction, int on, int locked) {
    this.quads = Model.join(rot[direction], torch1[direction], torch2[locked][delay][direction]);
    this.textures = new AbstractTexture[this.quads.length];
    Arrays.fill(textures, 0, rot[direction].length, tex[on]);
    Arrays.fill(textures, rot[direction].length, rot[direction].length+torch1[direction].length, torchTex[0][on]);
    Arrays.fill(textures, rot[direction].length+torch1[direction].length, this.textures.length, torchTex[locked][on]);
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
