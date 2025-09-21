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
  private static final Quad[] quadsNorth = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    )
  };

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

  public ObserverModel(String facing, boolean powered) {
    quads = switch (facing) {
      case "down" -> Model.rotateNegX(quadsNorth);
      case "east" -> Model.rotateY(quadsNorth);
      case "south" -> Model.rotateY(quadsNorth, Math.toRadians(180));
      case "up" -> Model.rotateX(quadsNorth);
      case "west" -> Model.rotateNegY(quadsNorth);
      default -> quadsNorth;
    };
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
