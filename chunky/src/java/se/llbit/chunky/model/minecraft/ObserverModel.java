/*
 * Copyright (c) 2016-2023 Chunky contributors
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

public class ObserverModel extends QuadModel {

  // Facing up:
  private static final Quad[] observer = new Quad[] {
      // Bottom face.
      new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 1, 0),
          new Vector4(1, 0, 1, 0)),

      // Top face.
      new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 1), new Vector3(0, 1, 1),
          new Vector4(0, 1, 1, 0)),

      // West face.
      new Quad(new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(0, 1, 0),
          new Vector4(0, 1, 0, 1)),

      // East face.
      new Quad(new Vector3(1, 0, 1), new Vector3(1, 0, 0), new Vector3(1, 1, 1),
          new Vector4(1, 0, 0, 1)),

      // Front face.
      new Quad(new Vector3(1, 1, 0), new Vector3(0, 1, 0), new Vector3(1, 1, 1),
          new Vector4(1, 0, 0, 1)),

      // Back face.
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1)),
  };

  private static final Quad[][] faces = new Quad[8][];

  static {
    // Rotate faces for all directions.
    faces[1] = observer;
    Quad[] temp = Model.rotateX(observer);
    // Facing down:
    faces[0] = Model.rotateX(temp);
    // Facing north:
    faces[2] = Model.rotateX(faces[0]);
    // Facing east:
    faces[5] = Model.rotateY(faces[2]);
    // Facing south:
    faces[3] = Model.rotateY(faces[5]);
    // Facing west:
    faces[4] = Model.rotateY(faces[3]);
    // Facing down:
    faces[6] = faces[1];
    // Facing up:
    faces[7] = observer;
  }

  private static final Texture[] texturesOff = {
      Texture.observerTop, Texture.observerTop,
      Texture.observerSide, Texture.observerSide,
      Texture.observerFront, Texture.observerBack
  };

  private static final Texture[] texturesOn = {
      Texture.observerTop, Texture.observerTop,
      Texture.observerSide, Texture.observerSide,
      Texture.observerFront, Texture.observerBackOn
  };

  private final Quad[] quads;
  private final Texture[] textures;

  public ObserverModel(int facing, boolean powered) {
    refractive = true;
    quads = faces[facing];
    textures = powered ? texturesOn : texturesOff;
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
