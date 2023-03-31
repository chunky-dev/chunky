/*
 * Copyright (c) 2015-2023 Chunky contributors
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
import se.llbit.math.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

// TODO: Improve rendering of chorus plants - pseudorandom plant part selection.
public class ChorusPlantModel extends QuadModel {
  static final Quad[] noside_n = {
      new Quad(
          new Vector3(12 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector3(4 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector3(12 / 16.0, 12 / 16.0, 4 / 16.0),
          new Vector4(12 / 16.0, 4 / 16.0, 4 / 16.0, 12 / 16.0)),
  };

  static final Quad[] side_n = {
      // cube1
      new Quad(
          new Vector3(4 / 16.0, 12 / 16.0, 4 / 16.0),
          new Vector3(12 / 16.0, 12 / 16.0, 4 / 16.0),
          new Vector3(4 / 16.0, 12 / 16.0, 0),
          new Vector4(4 / 16.0, 12 / 16.0, 16 / 16.0, 12 / 16.0)),
      new Quad(
          new Vector3(4 / 16.0, 4 / 16.0, 0),
          new Vector3(12 / 16.0, 4 / 16.0, 0),
          new Vector3(4 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector4(4 / 16.0, 12 / 16.0, 12 / 16.0, 16 / 16.0)),
      new Quad(
          new Vector3(12 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 0),
          new Vector3(12 / 16.0, 12 / 16.0, 4 / 16.0),
          new Vector4(0, 4 / 16.0, 4 / 16.0, 12 / 16.0)),
      new Quad(
          new Vector3(4 / 16.0, 4 / 16.0, 0),
          new Vector3(4 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector3(4 / 16.0, 12 / 16.0, 0),
          new Vector4(0, 4 / 16.0, 4 / 16.0, 12 / 16.0)),
      new Quad(
          new Vector3(12 / 16.0, 4 / 16.0, 0),
          new Vector3(4 / 16.0, 4 / 16.0, 0),
          new Vector3(12 / 16.0, 12 / 16.0, 0),
          new Vector4(12 / 16.0, 4 / 16.0, 4 / 16.0, 12 / 16.0)),
  };

  static final Quad[][] noside = new Quad[6][];
  static final Quad[][] side = new Quad[6][];

  static {
    noside[0] = noside_n;
    noside[1] = Model.rotateY(noside[0]);
    noside[2] = Model.rotateY(noside[1]);
    noside[3] = Model.rotateY(noside[2]);
    noside[4] = Model.rotateX(noside[0]);
    noside[5] = Model.rotateNegX(noside[0]);
    side[0] = side_n;
    side[1] = Model.rotateY(side[0]);
    side[2] = Model.rotateY(side[1]);
    side[3] = Model.rotateY(side[2]);
    side[4] = Model.rotateX(side[0]);
    side[5] = Model.rotateNegX(side[0]);
  }

  private final Quad[] quads;
  private final Texture[] textures;

  public ChorusPlantModel(
      boolean north, boolean south, boolean east, boolean west,
      boolean up, boolean down) {
    ArrayList<Quad> quads = new ArrayList<>();
    if (north) {
      Collections.addAll(quads, side[0]);
    } else {
      Collections.addAll(quads, noside[0]);
    }
    if (east) {
      Collections.addAll(quads, side[1]);
    } else {
      Collections.addAll(quads, noside[1]);
    }
    if (south) {
      Collections.addAll(quads, side[2]);
    } else {
      Collections.addAll(quads, noside[2]);
    }
    if (west) {
      Collections.addAll(quads, side[3]);
    } else {
      Collections.addAll(quads, noside[3]);
    }
    if (up) {
      Collections.addAll(quads, side[4]);
    } else {
      Collections.addAll(quads, noside[4]);
    }
    if (down) {
      Collections.addAll(quads, side[5]);
    } else {
      Collections.addAll(quads, noside[5]);
    }
    this.quads = quads.toArray(new Quad[0]);
    this.textures = new Texture[this.quads.length];
    Arrays.fill(this.textures, Texture.chorusPlant);
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
