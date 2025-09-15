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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TurtleEggModel extends QuadModel {
  //region EGG
  private static final Quad[][] egg_models = {
      {
          // cube1
          new Quad(
              new Vector3(5 / 16.0, 7 / 16.0, 9 / 16.0),
              new Vector3(10 / 16.0, 7 / 16.0, 9 / 16.0),
              new Vector3(5 / 16.0, 7 / 16.0, 4 / 16.0),
              new Vector4(0, 4 / 16.0, 12 / 16.0, 16 / 16.0)),
          new Quad(
              new Vector3(5 / 16.0, 0, 4 / 16.0),
              new Vector3(10 / 16.0, 0, 4 / 16.0),
              new Vector3(5 / 16.0, 0, 9 / 16.0),
              new Vector4(0, 4 / 16.0, 12 / 16.0, 16 / 16.0)),
          new Quad(
              new Vector3(10 / 16.0, 0, 9 / 16.0),
              new Vector3(10 / 16.0, 0, 4 / 16.0),
              new Vector3(10 / 16.0, 7 / 16.0, 9 / 16.0),
              new Vector4(1 / 16.0, 5 / 16.0, 5 / 16.0, 12 / 16.0)),
          new Quad(
              new Vector3(5 / 16.0, 0, 4 / 16.0),
              new Vector3(5 / 16.0, 0, 9 / 16.0),
              new Vector3(5 / 16.0, 7 / 16.0, 4 / 16.0),
              new Vector4(1 / 16.0, 5 / 16.0, 5 / 16.0, 12 / 16.0)),
          new Quad(
              new Vector3(10 / 16.0, 0, 4 / 16.0),
              new Vector3(5 / 16.0, 0, 4 / 16.0),
              new Vector3(10 / 16.0, 7 / 16.0, 4 / 16.0),
              new Vector4(1 / 16.0, 5 / 16.0, 5 / 16.0, 12 / 16.0)),
          new Quad(
              new Vector3(5 / 16.0, 0, 9 / 16.0),
              new Vector3(10 / 16.0, 0, 9 / 16.0),
              new Vector3(5 / 16.0, 7 / 16.0, 9 / 16.0),
              new Vector4(1 / 16.0, 5 / 16.0, 5 / 16.0, 12 / 16.0)),
      },
      {
          // cube2
          new Quad(
              new Vector3(1 / 16.0, 5 / 16.0, 11 / 16.0),
              new Vector3(5 / 16.0, 5 / 16.0, 11 / 16.0),
              new Vector3(1 / 16.0, 5 / 16.0, 7 / 16.0),
              new Vector4(6 / 16.0, 10 / 16.0, 5 / 16.0, 9 / 16.0)),
          new Quad(
              new Vector3(1 / 16.0, 0, 7 / 16.0),
              new Vector3(5 / 16.0, 0, 7 / 16.0),
              new Vector3(1 / 16.0, 0, 11 / 16.0),
              new Vector4(6 / 16.0, 10 / 16.0, 5 / 16.0, 9 / 16.0)),
          new Quad(
              new Vector3(5 / 16.0, 0, 11 / 16.0),
              new Vector3(5 / 16.0, 0, 7 / 16.0),
              new Vector3(5 / 16.0, 5 / 16.0, 11 / 16.0),
              new Vector4(10 / 16.0, 14 / 16.0, 1 / 16.0, 6 / 16.0)),
          new Quad(
              new Vector3(1 / 16.0, 0, 7 / 16.0),
              new Vector3(1 / 16.0, 0, 11 / 16.0),
              new Vector3(1 / 16.0, 5 / 16.0, 7 / 16.0),
              new Vector4(10 / 16.0, 14 / 16.0, 1 / 16.0, 6 / 16.0)),
          new Quad(
              new Vector3(5 / 16.0, 0, 7 / 16.0),
              new Vector3(1 / 16.0, 0, 7 / 16.0),
              new Vector3(5 / 16.0, 5 / 16.0, 7 / 16.0),
              new Vector4(10 / 16.0, 14 / 16.0, 1 / 16.0, 6 / 16.0)),
          new Quad(
              new Vector3(1 / 16.0, 0, 11 / 16.0),
              new Vector3(5 / 16.0, 0, 11 / 16.0),
              new Vector3(1 / 16.0, 5 / 16.0, 11 / 16.0),
              new Vector4(10 / 16.0, 14 / 16.0, 1 / 16.0, 6 / 16.0)),
      },
      {
          // cube3
          new Quad(
              new Vector3(11 / 16.0, 4 / 16.0, 10 / 16.0),
              new Vector3(14 / 16.0, 4 / 16.0, 10 / 16.0),
              new Vector3(11 / 16.0, 4 / 16.0, 7 / 16.0),
              new Vector4(5 / 16.0, 8 / 16.0, 13 / 16.0, 16 / 16.0)),
          new Quad(
              new Vector3(11 / 16.0, 0, 7 / 16.0),
              new Vector3(14 / 16.0, 0, 7 / 16.0),
              new Vector3(11 / 16.0, 0, 10 / 16.0),
              new Vector4(5 / 16.0, 8 / 16.0, 13 / 16.0, 16 / 16.0)),
          new Quad(
              new Vector3(14 / 16.0, 0, 10 / 16.0),
              new Vector3(14 / 16.0, 0, 7 / 16.0),
              new Vector3(14 / 16.0, 4 / 16.0, 10 / 16.0),
              new Vector4(8 / 16.0, 11 / 16.0, 9 / 16.0, 13 / 16.0)),
          new Quad(
              new Vector3(11 / 16.0, 0, 7 / 16.0),
              new Vector3(11 / 16.0, 0, 10 / 16.0),
              new Vector3(11 / 16.0, 4 / 16.0, 7 / 16.0),
              new Vector4(8 / 16.0, 11 / 16.0, 9 / 16.0, 13 / 16.0)),
          new Quad(
              new Vector3(14 / 16.0, 0, 7 / 16.0),
              new Vector3(11 / 16.0, 0, 7 / 16.0),
              new Vector3(14 / 16.0, 4 / 16.0, 7 / 16.0),
              new Vector4(8 / 16.0, 11 / 16.0, 9 / 16.0, 13 / 16.0)),
          new Quad(
              new Vector3(11 / 16.0, 0, 10 / 16.0),
              new Vector3(14 / 16.0, 0, 10 / 16.0),
              new Vector3(11 / 16.0, 4 / 16.0, 10 / 16.0),
              new Vector4(8 / 16.0, 11 / 16.0, 9 / 16.0, 13 / 16.0)),
      },
      {
          // cube4
          new Quad(
              new Vector3(7 / 16.0, 3 / 16.0, 13 / 16.0),
              new Vector3(10 / 16.0, 3 / 16.0, 13 / 16.0),
              new Vector3(7 / 16.0, 3 / 16.0, 10 / 16.0),
              new Vector4(0, 4 / 16.0, 1 / 16.0, 5 / 16.0)),
          new Quad(
              new Vector3(7 / 16.0, 0, 10 / 16.0),
              new Vector3(10 / 16.0, 0, 10 / 16.0),
              new Vector3(7 / 16.0, 0, 13 / 16.0),
              new Vector4(0, 4 / 16.0, 1 / 16.0, 5 / 16.0)),
          new Quad(
              new Vector3(10 / 16.0, 0, 13 / 16.0),
              new Vector3(10 / 16.0, 0, 10 / 16.0),
              new Vector3(10 / 16.0, 3 / 16.0, 13 / 16.0),
              new Vector4(4 / 16.0, 8 / 16.0, 1 / 16.0, 5 / 16.0)),
          new Quad(
              new Vector3(7 / 16.0, 0, 10 / 16.0),
              new Vector3(7 / 16.0, 0, 13 / 16.0),
              new Vector3(7 / 16.0, 3 / 16.0, 10 / 16.0),
              new Vector4(4 / 16.0, 8 / 16.0, 1 / 16.0, 5 / 16.0)),
          new Quad(
              new Vector3(10 / 16.0, 0, 10 / 16.0),
              new Vector3(7 / 16.0, 0, 10 / 16.0),
              new Vector3(10 / 16.0, 3 / 16.0, 10 / 16.0),
              new Vector4(4 / 16.0, 8 / 16.0, 1 / 16.0, 5 / 16.0)),
          new Quad(
              new Vector3(7 / 16.0, 0, 13 / 16.0),
              new Vector3(10 / 16.0, 0, 13 / 16.0),
              new Vector3(7 / 16.0, 3 / 16.0, 13 / 16.0),
              new Vector4(4 / 16.0, 8 / 16.0, 1 / 16.0, 5 / 16.0)),
      },
  };
  //endregion

  private static final Texture[] eggTextures = {
      Texture.turtleEgg,
      Texture.turtleEggSlightlyCracked,
      Texture.turtleEggVeryCracked
  };

  static final Quad[][][] rot;

  static {
    rot = new Quad[3][][];
    rot[0] = egg_models;
    rot[1] = new Quad[4][];
    for (int i = 0; i < 4; ++i) {
      rot[1][i] = Model.rotateNegY(egg_models[i]);
    }
    rot[2] = new Quad[4][];
    for (int i = 0; i < 4; ++i) {
      rot[2][i] = Model.rotateY(egg_models[i]);
    }
  }

  private final Quad[] quads;
  private final Texture[] textures;

  public TurtleEggModel(int eggs, int hatch) {
    refractive = true;
    eggs = Math.max(1, Math.min(egg_models.length, eggs));
    hatch = Math.max(0, Math.min(rot.length, hatch));
    ArrayList<Quad> quads = new ArrayList<>();
    for (int i = 0; i < eggs; i++)
      Collections.addAll(quads, rot[hatch][i]);
    this.quads = quads.toArray(new Quad[0]);
    this.textures = new Texture[this.quads.length];
    Arrays.fill(this.textures, eggTextures[hatch]);
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
