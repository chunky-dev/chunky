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

package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.log.Log;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.util.annotation.Nullable;

import java.util.Arrays;

public class OrientedQuadModel extends QuadModel {

  public static final Quad FULL_BLOCK_NORTH_SIDE = new Quad(
    new Vector3(1, 0, 0),
    new Vector3(0, 0, 0),
    new Vector3(1, 1, 0),
    new Vector4(0, 1, 0, 1));
  public static final Quad FULL_BLOCK_SOUTH_SIDE = new Quad(
    new Vector3(0, 0, 1),
    new Vector3(1, 0, 1),
    new Vector3(0, 1, 1),
    new Vector4(0, 1, 0, 1));
  public static final Quad FULL_BLOCK_WEST_SIDE = new Quad(
    new Vector3(0, 0, 0),
    new Vector3(0, 0, 1),
    new Vector3(0, 1, 0),
    new Vector4(0, 1, 0, 1));
  public static final Quad FULL_BLOCK_EAST_SIDE = new Quad(
    new Vector3(1, 0, 1),
    new Vector3(1, 0, 0),
    new Vector3(1, 1, 1),
    new Vector4(0, 1, 0, 1));
  public static final Quad FULL_BLOCK_TOP_SIDE = new Quad(
    new Vector3(1, 1, 0),
    new Vector3(0, 1, 0),
    new Vector3(1, 1, 1),
    new Vector4(1, 0, 1, 0));
  public static final Quad FULL_BLOCK_BOTTOM_SIDE = new Quad(
    new Vector3(0, 0, 0),
    new Vector3(1, 0, 0),
    new Vector3(0, 0, 1),
    new Vector4(0, 1, 0, 1));

  public static final Quad[] FULL_BLOCK_QUADS = {
    FULL_BLOCK_NORTH_SIDE, FULL_BLOCK_SOUTH_SIDE,
    FULL_BLOCK_WEST_SIDE, FULL_BLOCK_EAST_SIDE,
    FULL_BLOCK_TOP_SIDE, FULL_BLOCK_BOTTOM_SIDE
  };

  public static Quad[] rotateToFacing(TexturedBlockModel.Orientation orientation, Quad[] quads) {
    switch (orientation.reduce()) {
      default:
        Log.warn("Unknown orientation: " + orientation);
      case NORTH:
        return quads;
      case SOUTH:
        return Model.rotateY(Model.rotateY(quads));
      case EAST:
        return Model.rotateY(quads, -Math.toRadians(90));
      case WEST:
        return Model.rotateNegY(quads);
    }
  }

  protected final Quad[] quads;
  protected final Texture[] textures;
  protected final Tint[] tints;

  public OrientedQuadModel(TexturedBlockModel.Orientation orientation, Quad[] quads, Texture[] textures,
                           @Nullable Tint[] tints) {
    this.quads = rotateToFacing(orientation, quads);
    this.textures = textures;
    this.tints = tints;
  }

  public OrientedQuadModel(TexturedBlockModel.Orientation orientation, Quad[] quads, Quad[] topBottomQuads,
                           Texture[] textures, @Nullable Tint[] tints) {
    quads = rotateToFacing(orientation, quads);
    topBottomQuads = orientation.side ? topBottomQuads : rotateToFacing(orientation, topBottomQuads);

    this.quads = new Quad[quads.length + topBottomQuads.length];
    System.arraycopy(quads, 0, this.quads, 0, quads.length);
    System.arraycopy(topBottomQuads, 0, this.quads, quads.length, topBottomQuads.length);
    this.textures = textures;
    this.tints = tints;
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return new Texture[0];
  }

  @Override
  public Tint[] getTints() {
    return tints;
  }
}
