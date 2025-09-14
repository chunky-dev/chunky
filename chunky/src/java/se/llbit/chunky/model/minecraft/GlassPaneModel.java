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
import se.llbit.chunky.model.builder.QuadModelBuilder;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.function.BiFunction;

public class GlassPaneModel extends QuadModel {
  private static final Quad[] panePostQuads = {
    new Quad(
      new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 7 / 16.0, 9 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 7 / 16.0, 9 / 16.0)
    )
  };

  private static final BiFunction<Texture, Texture, Texture[]> paneSideTex = (edge, pane) -> new Texture[]{
    edge, edge, pane, pane, edge
  };

  private static final Quad[] paneSideQuads = new Quad[]{
    new Quad(
      new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 9 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 9 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector4(9 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(9 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 9 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(9 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 16 / 16.0, 0 / 16.0)
    )
  };

  private static final Quad[] paneSideAltQuads = new Quad[]{
    new Quad(
      new Vector3(7 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(9 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 9 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 9 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 7 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(7 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(7 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 16 / 16.0, 0 / 16.0)
    )
  };


  private static final Quad[] paneNoSideQuads = new Quad[]{
    new Quad(
      new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 16 / 16.0, 0 / 16.0)
    )
  };

  private static final Quad[] paneNoSideAltQuads = new Quad[]{
    new Quad(
      new Vector3(9 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 16 / 16.0, 0 / 16.0)
    )
  };

  private final Quad[] quads;
  private final Texture[] textures;

  public GlassPaneModel(Texture edge, Texture pane, boolean north, boolean south, boolean east, boolean west) {
    QuadModelBuilder builder = new QuadModelBuilder();
    builder.addModel(panePostQuads, edge);
    if (north) {
      builder.addModel(paneSideQuads, paneSideTex.apply(edge, pane));
    } else {
      builder.addModel(paneNoSideQuads, pane);
    }
    if (east) {
      builder.addModel(Model.rotateY(paneSideQuads), paneSideTex.apply(edge, pane));
    } else {
      builder.addModel(paneNoSideAltQuads, pane);
    }
    if (south) {
      builder.addModel(paneSideAltQuads, paneSideTex.apply(edge, pane));
    } else {
      builder.addModel(Model.rotateY(paneNoSideAltQuads), pane);
    }
    if (west) {
      builder.addModel(Model.rotateY(paneSideAltQuads), paneSideTex.apply(edge, pane));
    } else {
      builder.addModel(Model.rotateNegY(paneNoSideQuads), pane);
    }
    quads = builder.getQuads();
    textures = builder.getTextures();
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
