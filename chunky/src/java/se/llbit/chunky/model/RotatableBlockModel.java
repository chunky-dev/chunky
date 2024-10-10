/*
 * Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.Quad;

/**
 * This block model is used to render blocks which can face east, west, north, south, up and down.
 * For example, command blocks and observer blocks.
 */
public class RotatableBlockModel extends QuadModel {
  private Quad[] quads = new Quad[] {
    FULL_BLOCK_NORTH_SIDE,
    FULL_BLOCK_EAST_SIDE,
    FULL_BLOCK_SOUTH_SIDE,
    FULL_BLOCK_WEST_SIDE,
    FULL_BLOCK_TOP_SIDE,
    FULL_BLOCK_BOTTOM_SIDE
  };
  private final AbstractTexture[] textures;

  public RotatableBlockModel(AbstractTexture north, AbstractTexture east, AbstractTexture south, AbstractTexture west, AbstractTexture top, AbstractTexture bottom) {
    textures = new AbstractTexture[] {north, east, south, west, top, bottom};
  }

  public void setFaceQuad(int face, Quad quad) {
    quads[face] = quad;
  }

  public void rotateX(int r) {
    r %= 4;
    if(r < 0) {
      r += 4;
    }
    switch(r) {
      case 1:
        quads = Model.rotateX(quads);
        break;
      case 2:
        quads = Model.rotateX(Model.rotateX(quads));
        break;
      case 3:
        quads = Model.rotateNegX(quads);
        break;
    }
  }

  public void rotateY(int r) {
    r %= 4;
    if(r < 0) {
      r += 4;
    }
    switch(r) {
      case 1:
        quads = Model.rotateY(quads);
        break;
      case 2:
        quads = Model.rotateY(Model.rotateY(quads));
        break;
      case 3:
        quads = Model.rotateNegY(quads);
        break;
    }
  }

  public void rotateZ(int r) {
    r %= 4;
    if(r < 0) {
      r += 4;
    }
    switch(r) {
      case 1:
        quads = Model.rotateZ(quads);
        break;
      case 2:
        quads = Model.rotateZ(Model.rotateZ(quads));
        break;
      case 3:
        quads = Model.rotateNegZ(quads);
        break;
    }
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
