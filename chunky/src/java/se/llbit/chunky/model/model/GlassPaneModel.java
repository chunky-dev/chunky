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
package se.llbit.chunky.model.model;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class GlassPaneModel extends QuadModel {
  //region quads
  private static final Quad[] core = {
      // Top
      new Quad(
          new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 16 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
          new Vector4(7 / 16.0, 9 / 16.0, 7 / 16.0, 9 / 16.0)
      ),

      // Bottom
      new Quad(
          new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector4(7 / 16.0, 9 / 16.0, 7 / 16.0, 9 / 16.0)
      ),

      // North
      new Quad(
          new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 16 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector4(7 / 16.0, 9 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      null, // East
      null, // South
      null  // West
  };

  static {
    core[3] = new Quad(core[2], Transform.NONE.rotateY());
    core[4] = new Quad(core[3], Transform.NONE.rotateY());
    core[5] = new Quad(core[4], Transform.NONE.rotateY());
  }

  private static final Quad[][] connector = {
      // Front side.
      {
          // Left face.
          new Quad(new Vector3(7 / 16., 1, 7 / 16.), new Vector3(7 / 16., 1, 0),
              new Vector3(7 / 16., 0, 7 / 16.), new Vector4(7 / 16., 0, 1, 0)),

          // Right face.
          new Quad(new Vector3(9 / 16., 1, 0), new Vector3(9 / 16., 1, 7 / 16.),
              new Vector3(9 / 16., 0, 0), new Vector4(0, 7 / 16., 1, 0)),

          // Top face.
          new Quad(new Vector3(9 / 16., 1, 0), new Vector3(7 / 16., 1, 0),
              new Vector3(9 / 16., 1, 7 / 16.), new Vector4(9 / 16., 7 / 16., 0, 7 / 16.)),

          // Bottom face.
          new Quad(new Vector3(7 / 16., 0, 0), new Vector3(9 / 16., 0, 0),
              new Vector3(7 / 16., 0, 7 / 16.), new Vector4(7 / 16., 9 / 16., 0, 7 / 16.)),

      },
      // Back side.
      {
          // Left face.
          new Quad(new Vector3(7 / 16., 1, 1), new Vector3(7 / 16., 1, 9 / 16.),
              new Vector3(7 / 16., 0, 1), new Vector4(1, 9 / 16., 1, 0)),

          // Right face.
          new Quad(new Vector3(9 / 16., 1, 9 / 16.), new Vector3(9 / 16., 1, 1),
              new Vector3(9 / 16., 0, 9 / 16.), new Vector4(9 / 16., 1, 1, 0)),

          // Top face.
          new Quad(new Vector3(9 / 16., 1, 9 / 16.), new Vector3(7 / 16., 1, 9 / 16.),
              new Vector3(9 / 16., 1, 1), new Vector4(9 / 16., 7 / 16., 9 / 16., 1)),

          // Bottom face.
          new Quad(new Vector3(7 / 16., 0, 9 / 16.), new Vector3(9 / 16., 0, 9 / 16.),
              new Vector3(7 / 16., 0, 1), new Vector4(7 / 16., 9 / 16., 9 / 16., 1)),
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
  //endregion

  private final Quad[] quads;
  private final Texture[] textures;

  public GlassPaneModel(Texture top, Texture side, boolean north, boolean south, boolean east, boolean west) {
    ArrayList<Quad> quads = new ArrayList<>();
    ArrayList<Texture> textures = new ArrayList<>();

    Consumer<Quad[]> addConnector = qs -> {
      quads.addAll(Arrays.asList(qs));
      textures.addAll(Arrays.asList(side, side, top, top));
    };

    // Top and bottom
    quads.add(core[0]);
    quads.add(core[1]);
    textures.add(top);
    textures.add(top);

    // Cull sides
    if (!north) quads.add(core[2]);
    if (!east) quads.add(core[3]);
    if (!south) quads.add(core[4]);
    if (!west) quads.add(core[5]);

    while (textures.size() < quads.size()) {
      textures.add(side);
    }

    // Add connectors
    if (north) addConnector.accept(panes[0]);
    if (south) addConnector.accept(panes[1]);
    if (east) addConnector.accept(panes[2]);
    if (west) addConnector.accept(panes[3]);

    this.quads = quads.toArray(new Quad[0]);
    this.textures = textures.toArray(new Texture[0]);
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
