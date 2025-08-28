/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2021 Chunky contributors
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
package se.llbit.chunky.resources;

import se.llbit.chunky.renderer.scene.PlayerModel;
import se.llbit.math.Vector4;

public class PlayerTexture extends EntityTexture {

  private static final ExtendedUVMap EXTENDED_UV_STEVE = new ExtendedUVMap(PlayerModel.STEVE);
  private static final UVMap UV_STEVE = new UVMap(PlayerModel.STEVE);
  private static final ExtendedUVMap EXTENDED_UV_ALEX = new ExtendedUVMap(PlayerModel.ALEX);
  private static final UVMap UV_ALEX = new UVMap(PlayerModel.ALEX);
  private PlayerModel model = PlayerModel.STEVE;

  @Override
  public UVMap getUV() {
    if (width == height) {
      return model == PlayerModel.ALEX ? EXTENDED_UV_ALEX : EXTENDED_UV_STEVE;
    }
    return model == PlayerModel.ALEX ? UV_ALEX : UV_STEVE;
  }

  public PlayerModel getModel() {
    return model;
  }

  public void setModel(PlayerModel model) {
    this.model = model;
  }

  public static class UVMap extends EntityTexture.UVMap {

    // Hat layer coordinates.
    public final Vector4 hatFront = new Vector4();
    public final Vector4 hatBack = new Vector4();
    public final Vector4 hatTop = new Vector4();
    public final Vector4 hatBottom = new Vector4();
    public final Vector4 hatRight = new Vector4();
    public final Vector4 hatLeft = new Vector4();

    // Chest layer coordinates
    public final Vector4 chestFront = new Vector4();
    public final Vector4 chestBack = new Vector4();
    public final Vector4 chestTop = new Vector4();
    public final Vector4 chestBottom = new Vector4();
    public final Vector4 chestRight = new Vector4();
    public final Vector4 chestLeft = new Vector4();

    // Right leg layer coordinates
    public final Vector4 rightLegFront = new Vector4();
    public final Vector4 rightLegBack = new Vector4();
    public final Vector4 rightLegTop = new Vector4();
    public final Vector4 rightLegBottom = new Vector4();
    public final Vector4 rightLegRight = new Vector4();
    public final Vector4 rightLegLeft = new Vector4();

    // Left leg layer coordinates
    public final Vector4 leftLegFront = new Vector4();
    public final Vector4 leftLegBack = new Vector4();
    public final Vector4 leftLegTop = new Vector4();
    public final Vector4 leftLegBottom = new Vector4();
    public final Vector4 leftLegRight = new Vector4();
    public final Vector4 leftLegLeft = new Vector4();

    // Right arm layer coordinates
    public final Vector4 rightArmFront = new Vector4();
    public final Vector4 rightArmBack = new Vector4();
    public final Vector4 rightArmTop = new Vector4();
    public final Vector4 rightArmBottom = new Vector4();
    public final Vector4 rightArmRight = new Vector4();
    public final Vector4 rightArmLeft = new Vector4();

    // Left arm layer coordinates
    public final Vector4 leftArmFront = new Vector4();
    public final Vector4 leftArmBack = new Vector4();
    public final Vector4 leftArmTop = new Vector4();
    public final Vector4 leftArmBottom = new Vector4();
    public final Vector4 leftArmRight = new Vector4();
    public final Vector4 leftArmLeft = new Vector4();

    private UVMap(PlayerModel model) {
      this(model, false);
    }

    protected UVMap(PlayerModel model, boolean extended) {
      super(extended);
      double height = extended ? 64 : 32;

      // Head texture
      headFront.set(8 / 64., 16 / 64., (height - 16) / height, (height - 8) / height);
      headBack.set(24 / 64., 32 / 64., (height - 16) / height, (height - 8) / height);
      headTop.set(8 / 64., 16 / 64., (height - 8) / height, 1);
      headBottom.set(16 / 64., 24 / 64., (height - 8) / height, 1);
      headRight.set(0, 8 / 64., (height - 16) / height, (height - 8) / height);
      headLeft.set(16 / 64., 24 / 64., (height - 16) / height, (height - 8) / height);

      // Hat texture
      hatFront.set(40 / 64., 48 / 64., (height - 16) / height, (height - 8) / height);
      hatBack.set(56 / 64., 1, (height - 16) / height, (height - 8) / height);
      hatTop.set(40 / 64., 48 / 64., (height - 8) / height, 1);
      hatBottom.set(48 / 64., 56 / 64., (height - 8) / height, 1);
      hatRight.set(32 / 64., 40 / 64., (height - 16) / height, (height - 8) / height);
      hatLeft.set(48 / 64., 56 / 64., (height - 16) / height, (height - 8) / height);

      // Chest texture
      chestRight.set(16 / 64., 20 / 64., (height - 32) / height, (height - 20) / height);
      chestFront.set(20 / 64., 28 / 64., (height - 32) / height, (height - 20) / height);
      chestLeft.set(28 / 64., 32 / 64., (height - 32) / height, (height - 20) / height);
      chestBack.set(32 / 64., 40 / 64., (height - 32) / height, (height - 20) / height);
      chestTop.set(20 / 64., 28 / 64., (height - 20) / height, (height - 16) / height);
      chestBottom.set(28 / 64., 36 / 64., (height - 20) / height, (height - 16) / height);

      // Right leg texture
      rightLegRight.set(0 / 64., 4 / 64., (height - 32) / height, (height - 20) / height);
      rightLegFront.set(4 / 64., 8 / 64., (height - 32) / height, (height - 20) / height);
      rightLegLeft.set(8 / 64., 12 / 64., (height - 32) / height, (height - 20) / height);
      rightLegBack.set(12 / 64., 16 / 64., (height - 32) / height, (height - 20) / height);
      rightLegTop.set(4 / 64., 8 / 64., (height - 20) / height, (height - 16) / height);
      rightLegBottom.set(8 / 64., 12 / 64., (height - 20) / height, (height - 16) / height);

      // Right arm texture
      if (model == PlayerModel.ALEX) {
        rightArmRight.set(40 / 64., 44 / 64., (height - 32) / height, (height - 20) / height);
        rightArmFront.set(44 / 64., 47 / 64., (height - 32) / height, (height - 20) / height);
        rightArmLeft.set(47 / 64., 50 / 64., (height - 32) / height, (height - 20) / height);
        rightArmBack.set(50 / 64., 54 / 64., (height - 32) / height, (height - 20) / height);
        rightArmTop.set(44 / 64., 47 / 64., (height - 20) / height, (height - 16) / height);
        rightArmBottom.set(47 / 64., 50 / 64., (height - 20) / height, (height - 16) / height);
      } else {
        rightArmRight.set(40 / 64., 44 / 64., (height - 32) / height, (height - 20) / height);
        rightArmFront.set(44 / 64., 48 / 64., (height - 32) / height, (height - 20) / height);
        rightArmLeft.set(48 / 64., 52 / 64., (height - 32) / height, (height - 20) / height);
        rightArmBack.set(52 / 64., 56 / 64., (height - 32) / height, (height - 20) / height);
        rightArmTop.set(44 / 64., 48 / 64., (height - 20) / height, (height - 16) / height);
        rightArmBottom.set(48 / 64., 52 / 64., (height - 20) / height, (height - 16) / height);
      }

      if (!extended) {
        leftLegRight.set(rightLegLeft);
        leftLegRight.x = rightLegLeft.y;
        leftLegRight.y = rightLegLeft.x;
        leftLegFront.set(rightLegFront);
        leftLegFront.x = rightLegFront.y;
        leftLegFront.y = rightLegFront.x;
        leftLegLeft.set(rightLegRight);
        leftLegLeft.x = rightLegRight.y;
        leftLegLeft.y = rightLegRight.x;
        leftLegBack.set(rightLegBack);
        leftLegBack.x = rightLegBack.y;
        leftLegBack.y = rightLegBack.x;
        leftLegTop.set(rightLegTop);
        leftLegBottom.set(rightLegBottom);
        leftArmRight.set(rightArmRight);
        leftArmFront.set(rightArmFront);
        leftArmLeft.set(rightArmLeft);
        leftArmBack.set(rightArmBack);
        leftArmTop.set(rightArmTop);
        leftArmBottom.set(rightArmBottom);
      }
    }
  }

  public static class ExtendedUVMap extends UVMap {

    // Jacket layer coordinates
    public final Vector4 jacketFront = new Vector4();
    public final Vector4 jacketBack = new Vector4();
    public final Vector4 jacketTop = new Vector4();
    public final Vector4 jacketBottom = new Vector4();
    public final Vector4 jacketRight = new Vector4();
    public final Vector4 jacketLeft = new Vector4();

    // Right pant layer coordinates
    public final Vector4 rightPantFront = new Vector4();
    public final Vector4 rightPantBack = new Vector4();
    public final Vector4 rightPantTop = new Vector4();
    public final Vector4 rightPantBottom = new Vector4();
    public final Vector4 rightPantRight = new Vector4();
    public final Vector4 rightPantLeft = new Vector4();

    // Left pant layer coordinates
    public final Vector4 leftPantFront = new Vector4();
    public final Vector4 leftPantBack = new Vector4();
    public final Vector4 leftPantTop = new Vector4();
    public final Vector4 leftPantBottom = new Vector4();
    public final Vector4 leftPantRight = new Vector4();
    public final Vector4 leftPantLeft = new Vector4();

    // Right sleeve layer coordinates
    public final Vector4 rightSleeveFront = new Vector4();
    public final Vector4 rightSleeveBack = new Vector4();
    public final Vector4 rightSleeveTop = new Vector4();
    public final Vector4 rightSleeveBottom = new Vector4();
    public final Vector4 rightSleeveRight = new Vector4();
    public final Vector4 rightSleeveLeft = new Vector4();

    // Left sleeve layer coordinates
    public final Vector4 leftSleeveFront = new Vector4();
    public final Vector4 leftSleeveBack = new Vector4();
    public final Vector4 leftSleeveTop = new Vector4();
    public final Vector4 leftSleeveBottom = new Vector4();
    public final Vector4 leftSleeveRight = new Vector4();
    public final Vector4 leftSleeveLeft = new Vector4();

    protected ExtendedUVMap(PlayerModel model) {
      super(model, true);
      double height = 64;

      // Jacket texture
      jacketRight.set(16 / 64., 20 / 64., (height - 48) / height, (height - 36) / height);
      jacketFront.set(20 / 64., 28 / 64., (height - 48) / height, (height - 36) / height);
      jacketLeft.set(28 / 64., 32 / 64., (height - 48) / height, (height - 36) / height);
      jacketBack.set(32 / 64., 40 / 64., (height - 48) / height, (height - 36) / height);
      jacketTop.set(20 / 64., 28 / 64., (height - 36) / height, (height - 32) / height);
      jacketBottom.set(28 / 64., 36 / 64., (height - 36) / height, (height - 32) / height);

      // Right pant texture
      rightPantRight.set(0 / 64., 4 / 64., (height - 48) / height, (height - 36) / height);
      rightPantFront.set(4 / 64., 8 / 64., (height - 48) / height, (height - 36) / height);
      rightPantLeft.set(8 / 64., 12 / 64., (height - 48) / height, (height - 36) / height);
      rightPantBack.set(12 / 64., 16 / 64., (height - 48) / height, (height - 36) / height);
      rightPantTop.set(4 / 64., 8 / 64., (height - 36) / height, (height - 32) / height);
      rightPantBottom
          .set(8 / 64., 12 / 64., (height - 36) / height, (height - 32) / height);

      // Left pant texture
      leftPantRight.set(0 / 64., 4 / 64., (height - 64) / height, (height - 52) / height);
      leftPantFront.set(4 / 64., 8 / 64., (height - 64) / height, (height - 52) / height);
      leftPantLeft.set(8 / 64., 12 / 64., (height - 64) / height, (height - 52) / height);
      leftPantBack.set(12 / 64., 16 / 64., (height - 64) / height, (height - 52) / height);
      leftPantTop.set(4 / 64., 8 / 64., (height - 52) / height, (height - 48) / height);
      leftPantBottom.set(8 / 64., 12 / 64., (height - 52) / height, (height - 48) / height);

      // Left leg texture
      leftLegRight.set(16 / 64., 20 / 64., (height - 64) / height, (height - 52) / height);
      leftLegFront.set(20 / 64., 24 / 64., (height - 64) / height, (height - 52) / height);
      leftLegLeft.set(24 / 64., 28 / 64., (height - 64) / height, (height - 52) / height);
      leftLegBack.set(28 / 64., 32 / 64., (height - 64) / height, (height - 52) / height);
      leftLegTop.set(20 / 64., 24 / 64., (height - 52) / height, (height - 48) / height);
      leftLegBottom.set(24 / 64., 28 / 64., (height - 52) / height, (height - 48) / height);

      if (model == PlayerModel.ALEX) {
        // Right sleeve texture
        rightSleeveRight.set(40 / 64., 44 / 64., (height - 48) / height, (height - 36) / height);
        rightSleeveFront.set(44 / 64., 47 / 64., (height - 48) / height, (height - 36) / height);
        rightSleeveLeft.set(47 / 64., 51 / 64., (height - 48) / height, (height - 36) / height);
        rightSleeveBack.set(51 / 64., 54 / 64., (height - 48) / height, (height - 36) / height);
        rightSleeveTop.set(44 / 64., 47 / 64., (height - 36) / height, (height - 32) / height);
        rightSleeveBottom.set(47 / 64., 50 / 64., (height - 36) / height, (height - 32) / height);

        // Left sleeve texture
        leftSleeveRight.set(48 / 64., 52 / 64., (height - 64) / height, (height - 52) / height);
        leftSleeveFront.set(52 / 64., 55 / 64., (height - 64) / height, (height - 52) / height);
        leftSleeveLeft.set(55 / 64., 59 / 64., (height - 64) / height, (height - 52) / height);
        leftSleeveBack.set(58 / 64., 62 / 64., (height - 64) / height, (height - 52) / height);
        leftSleeveTop.set(52 / 64., 55 / 64., (height - 52) / height, (height - 48) / height);
        leftSleeveBottom.set(55 / 64., 58 / 64., (height - 52) / height, (height - 48) / height);

        // Left arm texture
        leftArmRight.set(32 / 64., 36 / 64., (height - 64) / height, (height - 52) / height);
        leftArmFront.set(36 / 64., 39 / 64., (height - 64) / height, (height - 52) / height);
        leftArmLeft.set(39 / 64., 42 / 64., (height - 64) / height, (height - 52) / height);
        leftArmBack.set(42 / 64., 46 / 64., (height - 64) / height, (height - 52) / height);
        leftArmTop.set(36 / 64., 39 / 64., (height - 52) / height, (height - 48) / height);
        leftArmBottom.set(39 / 64., 42 / 64., (height - 52) / height, (height - 48) / height);
      } else {
        // Right sleeve texture
        rightSleeveRight.set(40 / 64., 44 / 64., (height - 48) / height, (height - 36) / height);
        rightSleeveFront.set(44 / 64., 48 / 64., (height - 48) / height, (height - 36) / height);
        rightSleeveLeft.set(48 / 64., 52 / 64., (height - 48) / height, (height - 36) / height);
        rightSleeveBack.set(52 / 64., 56 / 64., (height - 48) / height, (height - 36) / height);
        rightSleeveTop.set(44 / 64., 48 / 64., (height - 36) / height, (height - 32) / height);
        rightSleeveBottom.set(48 / 64., 52 / 64., (height - 36) / height, (height - 32) / height);

        // Left sleeve texture
        leftSleeveRight.set(48 / 64., 52 / 64., (height - 64) / height, (height - 52) / height);
        leftSleeveFront.set(52 / 64., 56 / 64., (height - 64) / height, (height - 52) / height);
        leftSleeveLeft.set(56 / 64., 60 / 64., (height - 64) / height, (height - 52) / height);
        leftSleeveBack.set(60 / 64., 64 / 64., (height - 64) / height, (height - 52) / height);
        leftSleeveTop.set(52 / 64., 56 / 64., (height - 52) / height, (height - 48) / height);
        leftSleeveBottom.set(56 / 64., 60 / 64., (height - 52) / height, (height - 48) / height);

        // Left arm texture
        leftArmRight.set(32 / 64., 36 / 64., (height - 64) / height, (height - 52) / height);
        leftArmFront.set(36 / 64., 40 / 64., (height - 64) / height, (height - 52) / height);
        leftArmLeft.set(40 / 64., 44 / 64., (height - 64) / height, (height - 52) / height);
        leftArmBack.set(44 / 64., 48 / 64., (height - 64) / height, (height - 52) / height);
        leftArmTop.set(36 / 64., 40 / 64., (height - 52) / height, (height - 48) / height);
        leftArmBottom.set(40 / 64., 44 / 64., (height - 52) / height, (height - 48) / height);
      }
    }
  }
}
