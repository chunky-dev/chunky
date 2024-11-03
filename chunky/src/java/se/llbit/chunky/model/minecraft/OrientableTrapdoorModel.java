/*
 * Copyright (c) 2024 Chunky contributors
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

public class OrientableTrapdoorModel extends QuadModel {
  private static final Quad[] quadsTop = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 13 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 13 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 13 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 13 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 13 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 13 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 13 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 13 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 13 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 13 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 13 / 16.0)
    )
  };

  private static final Quad[] quadsBottom = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 3 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 3 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 3 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 3 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 3 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 13 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 3 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 3 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 13 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 3 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 3 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 13 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 3 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 3 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 13 / 16.0)
    )
  };

  private static final Quad[] quadsOpen = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 13 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 13 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 13 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 13 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 13 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 13 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 13 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 13 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 0 / 16.0, 16 / 16.0)
    )
  };

  private Quad[] quads;
  private final Texture[] textures;

  public OrientableTrapdoorModel(Texture texture, String half, String facing, boolean open) {
    if (open) {
      quads = quadsOpen;
    } else if (half.equals("top")) {
      quads = quadsTop;
    } else {
      quads = quadsBottom;
    }

    if (facing.equals("east")) {
      if (open && half.equals("top")) {
        quads = Model.rotateX(Model.rotateY(quads, Math.toRadians(270)), Math.toRadians(180));
      } else {
        quads = Model.rotateY(quads, Math.toRadians(-90));
      }
    } else if (facing.equals("north")) {
      if (open && half.equals("top")) {
        quads = Model.rotateX(Model.rotateY(quads, Math.toRadians(180)), Math.toRadians(180));
      }
    } else if (facing.equals("south")) {
      if (half.equals("top") && open) {
        quads = Model.rotateX(quads, Math.toRadians(180));
      } else {
        quads = Model.rotateY(quads, Math.toRadians(180));
      }
    } else { // west
      if (half.equals("top") && open) {
        quads = Model.rotateY(Model.rotateX(quads, Math.toRadians(180)), Math.toRadians(-90));
      } else {
        quads = Model.rotateY(quads, Math.toRadians(-270));
      }
    }

    textures = new Texture[quads.length];
    Arrays.fill(textures, texture);
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
