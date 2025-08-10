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

import se.llbit.chunky.block.minecraft.Chest;
import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texturepack.ChestTexture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * Chests, large chests and ender chests
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChestModel extends QuadModel {
  //region Single Chest
  protected static final Quad[] single = {
    // north
    new Quad(new Vector3(.9375, 0, .0625), new Vector3(.0625, 0, .0625),
      new Vector3(.9375, .875, .0625), new Vector4(1, 0, 0, 1)),

    // south
    new Quad(new Vector3(.0625, 0, .9375), new Vector3(.9375, 0, .9375),
      new Vector3(.0625, .875, .9375), new Vector4(0, 1, 0, 1)),

    // west
    new Quad(new Vector3(.0625, 0, .0625), new Vector3(.0625, 0, .9375),
      new Vector3(.0625, .875, .0625), new Vector4(0, 1, 0, 1)),

    // east
    new Quad(new Vector3(.9375, 0, .9375), new Vector3(.9375, 0, .0625),
      new Vector3(.9375, .875, .9375), new Vector4(1, 0, 0, 1)),

    // top
    new Quad(new Vector3(.9375, .875, .0625), new Vector3(.0625, .875, .0625),
      new Vector3(.9375, .875, .9375), new Vector4(1, 0, 0, 1)),

    // bottom
    new Quad(new Vector3(.0625, 0, .0625), new Vector3(.9375, 0, .0625),
      new Vector3(.0625, 0, .9375), new Vector4(0, 1, 0, 1)),

    // -- lock

    // north
    new Quad(new Vector3(.5626, .4375, 0), new Vector3(.4375, .4375, 0),
      new Vector3(.5626, .6875, 0), new Vector4(.125, .375, .375, .875)),

    // west
    new Quad(new Vector3(.4375, .4375, 0), new Vector3(.4375, .4375, .0625),
      new Vector3(.4375, .6875, 0), new Vector4(.625, .75, .375, .875)),

    // east
    new Quad(new Vector3(.5626, .4375, .0625), new Vector3(.5626, .4375, 0),
      new Vector3(.5626, .6875, .0625), new Vector4(0, .125, .375, .875)),

    // top
    new Quad(new Vector3(.5626, .6875, 0), new Vector3(.4375, .6875, 0),
      new Vector3(.5626, .6875, .0625), new Vector4(.125, .375, .875, 1)),

    // bottom
    new Quad(new Vector3(.4375, .4375, 0), new Vector3(.5626, .4375, 0),
      new Vector3(.4375, .4375, .0625), new Vector4(.625, .375, .875, 1)),

  };
  //endregion

  //region Large Chest (left)
  protected static final Quad[] left = {
    // north
    new Quad(new Vector3(1, 0, .0625), new Vector3(.0625, 0, .0625),
      new Vector3(1, .875, .0625), new Vector4(1, 0, 0, 1)),

    // south
    new Quad(new Vector3(.0625, 0, .9375), new Vector3(1, 0, .9375),
      new Vector3(.0625, .875, .9375), new Vector4(0, 1, 0, 1)),

    // west
    new Quad(new Vector3(.0625, 0, .0625), new Vector3(.0625, 0, .9375),
      new Vector3(.0625, .875, .0625), new Vector4(0, 1, 0, 1)),

    // top
    new Quad(new Vector3(1, .875, .0625), new Vector3(.0625, .875, .0625),
      new Vector3(1, .875, .9375), new Vector4(1, 0, 0, 1)),

    // bottom
    new Quad(new Vector3(.0625, 0, .0625), new Vector3(1, 0, .0625),
      new Vector3(.0625, 0, .9375), new Vector4(0, 1, 0, 1)),

    // -- lock

    // north
    new Quad(new Vector3(1, .4375, 0), new Vector3(.9375, .4375, 0),
      new Vector3(1, .6875, 0), new Vector4(.25, .375, .375, .875)),

    // west
    new Quad(new Vector3(.9375, .4375, 0), new Vector3(.9375, .4375, .0625),
      new Vector3(.9375, .6875, 0), new Vector4(.625, .75, .375, .875)),

    // top
    new Quad(new Vector3(1, .6875, 0), new Vector3(.9375, .6875, 0),
      new Vector3(1, .6875, .0625), new Vector4(.25, .375, .875, 1)),

    // bottom
    new Quad(new Vector3(.9375, .4375, 0), new Vector3(1, .4375, 0),
      new Vector3(.9375, .4375, .0625), new Vector4(.625, .5, .875, 1))
  };
  //endregion

  //region Large Chest (right)
  protected static final Quad[] right = {
    // north
    new Quad(new Vector3(.9375, 0, .0625), new Vector3(0, 0, .0625),
      new Vector3(.9375, .875, .0625), new Vector4(1, 0, 0, 1)),

    // south
    new Quad(new Vector3(0, 0, .9375), new Vector3(.9375, 0, .9375),
      new Vector3(0, .875, .9375), new Vector4(0, 1, 0, 1)),

    // east
    new Quad(new Vector3(.9375, 0, .9375), new Vector3(.9375, 0, .0625),
      new Vector3(.9375, .875, .9375), new Vector4(1, 0, 0, 1)),

    // top
    new Quad(new Vector3(.9375, .875, .0625), new Vector3(0, .875, .0625),
      new Vector3(.9375, .875, .9375), new Vector4(1, 0, 0, 1)),

    // bottom
    new Quad(new Vector3(0, 0, .0625), new Vector3(.9375, 0, .0625), new Vector3(0, 0, .9375),
      new Vector4(0, 1, 0, 1)),

    // -- lock

    // north
    new Quad(new Vector3(.0625, .4375, 0), new Vector3(0, .4375, 0),
      new Vector3(.0625, .6875, 0), new Vector4(.125, .25, .375, .875)),

    // east
    new Quad(new Vector3(.0625, .4375, .0625), new Vector3(.0625, .4375, 0),
      new Vector3(.0625, .6875, .0625), new Vector4(0, .125, .375, .875)),

    // top
    new Quad(new Vector3(.0625, .6875, 0), new Vector3(0, .6875, 0),
      new Vector3(.0625, .6875, .0625), new Vector4(.125, .25, .875, 1)),

    // bottom
    new Quad(new Vector3(0, .4375, 0), new Vector3(.0625, .4375, 0),
      new Vector3(0, .4375, .0625), new Vector4(.5, .375, .875, 1))
  };
  //endregion

  private static final ChestTexture.Textures[] normalChest = {
    Texture.chest,
    Texture.largeChestLeft,
    Texture.largeChestRight
  };
  private static final ChestTexture.Textures[] trappedChest = {
    Texture.trappedChest,
    Texture.largeTrappedChestLeft,
    Texture.largeTrappedChestRight
  };
  private static final ChestTexture.Textures[] copperChest = {
    Texture.copperChest,
    Texture.largeCopperChestLeft,
    Texture.largeCopperChestRight
  };
  private static final ChestTexture.Textures[] exposedCopperChest = {
    Texture.exposedCopperChest,
    Texture.largeExposedCopperChestLeft,
    Texture.largeExposedCopperChestRight
  };
  private static final ChestTexture.Textures[] weatheredCopperChest = {
    Texture.weatheredCopperChest,
    Texture.largeWeatheredCopperChestLeft,
    Texture.largeWeatheredCopperChestRight
  };
  private static final ChestTexture.Textures[] oxidizedCopperChest = {
    Texture.oxidizedCopperChest,
    Texture.largeOxidizedCopperChestLeft,
    Texture.largeOxidizedCopperChestRight
  };

  protected static final Quad[][][] variants = new Quad[3][6][];

  static {
    variants[0][0] = variants[0][1] = new Quad[0];
    variants[0][2] = single;
    variants[1][0] = variants[1][1] = new Quad[0];
    variants[1][2] = left;
    variants[2][0] = variants[2][1] = new Quad[0];
    variants[2][2] = right;
    rotateFaceY(2, 5);
    rotateFaceY(5, 3);
    rotateFaceY(3, 4);
  }

  private static void rotateFaceY(int i, int j) {
    for (int v = 0; v < 3; ++v) {
      variants[v][j] = Model.rotateY(variants[v][i]);
    }
  }

  private final Quad[] quads;
  private final Texture[] textures;

  public ChestModel(Chest.Type type, int facing, Chest.Kind kind) {
    ChestTexture.Textures textures = switch (kind) {
      case ENDER -> Texture.enderChest;
      case TRAPPED -> trappedChest[type.ordinal()];
      case COPPER -> copperChest[type.ordinal()];
      case EXPOSED_COPPER ->  exposedCopperChest[type.ordinal()];
      case OXIDIZED_COPPER -> oxidizedCopperChest[type.ordinal()];
      case WEATHERED_COPPER -> weatheredCopperChest[type.ordinal()];
      case NORMAL -> normalChest[type.ordinal()];
    };
    quads = variants[type.ordinal()][facing];
    this.textures = switch (type) {
      case SINGLE -> new Texture[]{
        textures.front, textures.back, textures.left, textures.right,
        textures.top, textures.bottom, textures.lock, textures.lock,
        textures.lock, textures.lock, textures.lock,
      };
      case LEFT -> new Texture[]{
        textures.front, textures.back, textures.left,
        textures.top, textures.bottom, textures.lock,
        textures.lock, textures.lock, textures.lock,
      };
      case RIGHT -> new Texture[]{
        textures.front, textures.back, textures.right,
        textures.top, textures.bottom, textures.lock,
        textures.lock, textures.lock, textures.lock,
      };
    };
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
