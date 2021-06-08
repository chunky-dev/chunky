/* Copyright (c) 2012-2015 Jesper Öqvist <jesper@llbit.se>
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class IronBarsModel extends QuadModel {

  private static final Quad[] core = {
      new Quad(new Vector3(.5, 1, 7 / 16.), new Vector3(.5, 1, 9 / 16.),
          new Vector3(.5, 0, 7 / 16.), new Vector4(7 / 16., 9 / 16., 1, 0), true),

      new Quad(new Vector3(7 / 16., 1, .5), new Vector3(9 / 16., 1, .5),
          new Vector3(7 / 16., 0, .5), new Vector4(7 / 16., 9 / 16., 1, 0), true),
  };

  private static final Quad[] coreTop = {
      // Top face.
      new Quad(new Vector3(9 / 16., 1, 7 / 16.), new Vector3(7 / 16., 1, 7 / 16.),
          new Vector3(9 / 16., 1, 9 / 16.), new Vector4(9 / 16., 7 / 16., 7 / 16., 9 / 16.), true),

      // Bottom face.
      new Quad(new Vector3(7 / 16., 0, 7 / 16.), new Vector3(9 / 16., 0, 7 / 16.),
          new Vector3(7 / 16., 0, 9 / 16.), new Vector4(7 / 16., 9 / 16., 7 / 16., 9 / 16.), true),
  };

  private static final Quad[][] connector = {
      // Front side.
      {
          // Center face.
          new Quad(new Vector3(.5, 1, .5), new Vector3(.5, 1, 0),
              new Vector3(.5, 0, .5), new Vector4(.5, 0, 1, 0), true),

          // Top face.
          new Quad(new Vector3(9 / 16., 1, 0), new Vector3(7 / 16., 1, 0),
              new Vector3(9 / 16., 1, 7 / 16.), new Vector4(9 / 16., 7 / 16., 0, 7 / 16.), true),

          // Bottom face.
          new Quad(new Vector3(7 / 16., 0, 0), new Vector3(9 / 16., 0, 0),
              new Vector3(7 / 16., 0, 7 / 16.), new Vector4(7 / 16., 9 / 16., 0, 7 / 16.), true),

      },
      // Back side.
      {
          // Center face.
          new Quad(new Vector3(.5, 1, 1), new Vector3(.5, 1, .5),
              new Vector3(.5, 0, 1), new Vector4(1, .5, 1, 0), true),

          // Top face.
          new Quad(new Vector3(9 / 16., 1, 9 / 16.), new Vector3(7 / 16., 1, 9 / 16.),
              new Vector3(9 / 16., 1, 1), new Vector4(9 / 16., 7 / 16., 9 / 16., 1), true),

          // Bottom face.
          new Quad(new Vector3(7 / 16., 0, 9 / 16.), new Vector3(9 / 16., 0, 9 / 16.),
              new Vector3(7 / 16., 0, 1), new Vector4(7 / 16., 9 / 16., 9 / 16., 1), true),
      },
  };

  private static final Quad[][] panes = new Quad[4][];

  static {
    panes[0] = connector[0];
    panes[1] = connector[1];
    for (int j = 2; j < 4; ++j) {
      panes[j] = Model.rotateY(connector[j - 2]);
    }
  }

  private final Quad[] quads;
  private final Texture[] textures;

  public IronBarsModel(int connections) {
    ArrayList<Quad> quads = new ArrayList<>();
    Collections.addAll(quads, coreTop);
    if (connections == 0) {
      Collections.addAll(quads, core);
    } else {
      for (int i = 0; i < 4; ++i) {
        if ((connections & (1 << i)) != 0)
          Collections.addAll(quads, panes[i]);
      }
    }
    this.quads = quads.toArray(new Quad[0]);
    this.textures = new Texture[this.quads.length];
    Arrays.fill(this.textures, Texture.ironBars);
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
