/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.resources.texturepack;

import se.llbit.chunky.renderer.scene.PlayerModel;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.EntityTexture;
import se.llbit.resources.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

/**
 * Helper to load entity textures, i.e. creeper, zombie, skeleton etc. textures.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class EntityTextureLoader extends TextureLoader {
  private final String file;
  private final EntityTexture texture;
  private final PlayerModel model;

  public EntityTextureLoader(String file, EntityTexture texture) {
    this(file, texture, PlayerModel.STEVE);
  }

  public EntityTextureLoader(String file, EntityTexture texture, PlayerModel model) {
    this.file = file;
    this.texture = texture;
    this.model = model;
  }

  @Override protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    BitmapImage image = ImageLoader.read(imageStream);

    if (image.width != image.height && image.width != 2 * image.height) {
      throw new TextureFormatError("Entity texture should be 64x64 or 64x32 pixels, "
          + "or a multiple of those dimensions.");
    }

    texture.setTexture(image);

    boolean extended = image.height == image.width;
    double height = extended ? 64 : 32;

    // Head texture
    texture.headFront.set(8 / 64., 16 / 64., (height - 16) / height, (height - 8) / height);
    texture.headBack.set(24 / 64., 32 / 64., (height - 16) / height, (height - 8) / height);
    texture.headTop.set(8 / 64., 16 / 64., (height - 8) / height, 1);
    texture.headBottom.set(16 / 64., 24 / 64., (height - 8) / height, 1);
    texture.headRight.set(0, 8 / 64., (height - 16) / height, (height - 8) / height);
    texture.headLeft.set(16 / 64., 24 / 64., (height - 16) / height, (height - 8) / height);

    // Hat texture
    texture.hatFront.set(40 / 64., 48 / 64., (height - 16) / height, (height - 8) / height);
    texture.hatBack.set(56 / 64., 1, (height - 16) / height, (height - 8) / height);
    texture.hatTop.set(40 / 64., 48 / 64., (height - 8) / height, 1);
    texture.hatBottom.set(48 / 64., 56 / 64., (height - 8) / height, 1);
    texture.hatRight.set(32 / 64., 40 / 64., (height - 16) / height, (height - 8) / height);
    texture.hatLeft.set(48 / 64., 56 / 64., (height - 16) / height, (height - 8) / height);

    // Chest texture
    texture.chestRight.set(16 / 64., 20 / 64., (height - 32) / height, (height - 20) / height);
    texture.chestFront.set(20 / 64., 28 / 64., (height - 32) / height, (height - 20) / height);
    texture.chestLeft.set(28 / 64., 32 / 64., (height - 32) / height, (height - 20) / height);
    texture.chestBack.set(32 / 64., 40 / 64., (height - 32) / height, (height - 20) / height);
    texture.chestTop.set(20 / 64., 28 / 64., (height - 20) / height, (height - 16) / height);
    texture.chestBottom.set(28 / 64., 36 / 64., (height - 20) / height, (height - 16) / height);

    // Right leg texture
    texture.rightLegRight.set(0 / 64., 4 / 64., (height - 32) / height, (height - 20) / height);
    texture.rightLegFront.set(4 / 64., 8 / 64., (height - 32) / height, (height - 20) / height);
    texture.rightLegLeft.set(8 / 64., 12 / 64., (height - 32) / height, (height - 20) / height);
    texture.rightLegBack.set(12 / 64., 16 / 64., (height - 32) / height, (height - 20) / height);
    texture.rightLegTop.set(4 / 64., 8 / 64., (height - 20) / height, (height - 16) / height);
    texture.rightLegBottom.set(8 / 64., 12 / 64., (height - 20) / height, (height - 16) / height);

    // Right arm texture
    if (model == PlayerModel.ALEX) {
      texture.rightArmRight.set(40 / 64., 44 / 64., (height - 32) / height, (height - 20) / height);
      texture.rightArmFront.set(44 / 64., 47 / 64., (height - 32) / height, (height - 20) / height);
      texture.rightArmLeft.set(47 / 64., 50 / 64., (height - 32) / height, (height - 20) / height);
      texture.rightArmBack.set(50 / 64., 54 / 64., (height - 32) / height, (height - 20) / height);
      texture.rightArmTop.set(44 / 64., 47 / 64., (height - 20) / height, (height - 16) / height);
      texture.rightArmBottom.set(47 / 64., 50 / 64., (height - 20) / height, (height - 16) / height);
    } else {
      texture.rightArmRight.set(40 / 64., 44 / 64., (height - 32) / height, (height - 20) / height);
      texture.rightArmFront.set(44 / 64., 48 / 64., (height - 32) / height, (height - 20) / height);
      texture.rightArmLeft.set(48 / 64., 52 / 64., (height - 32) / height, (height - 20) / height);
      texture.rightArmBack.set(52 / 64., 56 / 64., (height - 32) / height, (height - 20) / height);
      texture.rightArmTop.set(44 / 64., 48 / 64., (height - 20) / height, (height - 16) / height);
      texture.rightArmBottom.set(48 / 64., 52 / 64., (height - 20) / height, (height - 16) / height);
    }

    if (extended) {
      // Jacket texture
      texture.jacketRight.set(16 / 64., 20 / 64., (height - 48) / height, (height - 36) / height);
      texture.jacketFront.set(20 / 64., 28 / 64., (height - 48) / height, (height - 36) / height);
      texture.jacketLeft.set(28 / 64., 32 / 64., (height - 48) / height, (height - 36) / height);
      texture.jacketBack.set(32 / 64., 40 / 64., (height - 48) / height, (height - 36) / height);
      texture.jacketTop.set(20 / 64., 28 / 64., (height - 36) / height, (height - 32) / height);
      texture.jacketBottom.set(28 / 64., 36 / 64., (height - 36) / height, (height - 32) / height);

      // Right pant texture
      texture.rightPantRight.set(0 / 64., 4 / 64., (height - 48) / height, (height - 36) / height);
      texture.rightPantFront.set(4 / 64., 8 / 64., (height - 48) / height, (height - 36) / height);
      texture.rightPantLeft.set(8 / 64., 12 / 64., (height - 48) / height, (height - 36) / height);
      texture.rightPantBack.set(12 / 64., 16 / 64., (height - 48) / height, (height - 36) / height);
      texture.rightPantTop.set(4 / 64., 8 / 64., (height - 36) / height, (height - 32) / height);
      texture.rightPantBottom.set(8 / 64., 12 / 64., (height - 36) / height, (height - 32) / height);

      // Left pant texture
      texture.leftPantRight.set(0 / 64., 4 / 64., (height - 64) / height, (height - 52) / height);
      texture.leftPantFront.set(4 / 64., 8 / 64., (height - 64) / height, (height - 52) / height);
      texture.leftPantLeft.set(8 / 64., 12 / 64., (height - 64) / height, (height - 52) / height);
      texture.leftPantBack.set(12 / 64., 16 / 64., (height - 64) / height, (height - 52) / height);
      texture.leftPantTop.set(4 / 64., 8 / 64., (height - 52) / height, (height - 48) / height);
      texture.leftPantBottom.set(8 / 64., 12 / 64., (height - 52) / height, (height - 48) / height);

      // Left leg texture
      texture.leftLegRight.set(16 / 64., 20 / 64., (height - 64) / height, (height - 52) / height);
      texture.leftLegFront.set(20 / 64., 24 / 64., (height - 64) / height, (height - 52) / height);
      texture.leftLegLeft.set(24 / 64., 28 / 64., (height - 64) / height, (height - 52) / height);
      texture.leftLegBack.set(28 / 64., 32 / 64., (height - 64) / height, (height - 52) / height);
      texture.leftLegTop.set(20 / 64., 24 / 64., (height - 52) / height, (height - 48) / height);
      texture.leftLegBottom.set(24 / 64., 28 / 64., (height - 52) / height, (height - 48) / height);

      if (model == PlayerModel.ALEX) {
        // Right sleeve texture
        texture.rightSleeveRight.set(40 / 64., 44 / 64., (height - 48) / height, (height - 36) / height);
        texture.rightSleeveFront.set(44 / 64., 47 / 64., (height - 48) / height, (height - 36) / height);
        texture.rightSleeveLeft.set(47 / 64., 51 / 64., (height - 48) / height, (height - 36) / height);
        texture.rightSleeveBack.set(51 / 64., 54 / 64., (height - 48) / height, (height - 36) / height);
        texture.rightSleeveTop.set(44 / 64., 47 / 64., (height - 36) / height, (height - 32) / height);
        texture.rightSleeveBottom.set(47 / 64., 50 / 64., (height - 36) / height, (height - 32) / height);

        // Left sleeve texture
        texture.leftSleeveRight.set(48 / 64., 52 / 64., (height - 64) / height, (height - 52) / height);
        texture.leftSleeveFront.set(52 / 64., 55 / 64., (height - 64) / height, (height - 52) / height);
        texture.leftSleeveLeft.set(55 / 64., 59 / 64., (height - 64) / height, (height - 52) / height);
        texture.leftSleeveBack.set(58 / 64., 62 / 64., (height - 64) / height, (height - 52) / height);
        texture.leftSleeveTop.set(52 / 64., 55 / 64., (height - 52) / height, (height - 48) / height);
        texture.leftSleeveBottom.set(55 / 64., 58 / 64., (height - 52) / height, (height - 48) / height);

        // Left arm texture
        texture.leftArmRight.set(32 / 64., 36 / 64., (height - 64) / height, (height - 52) / height);
        texture.leftArmFront.set(36 / 64., 39 / 64., (height - 64) / height, (height - 52) / height);
        texture.leftArmLeft.set(39 / 64., 42 / 64., (height - 64) / height, (height - 52) / height);
        texture.leftArmBack.set(42 / 64., 46 / 64., (height - 64) / height, (height - 52) / height);
        texture.leftArmTop.set(36 / 64., 39 / 64., (height - 52) / height, (height - 48) / height);
        texture.leftArmBottom.set(39 / 64., 42 / 64., (height - 52) / height, (height - 48) / height);
      } else {
        // Right sleeve texture
        texture.rightSleeveRight.set(40 / 64., 44 / 64., (height - 48) / height, (height - 36) / height);
        texture.rightSleeveFront.set(44 / 64., 48 / 64., (height - 48) / height, (height - 36) / height);
        texture.rightSleeveLeft.set(48 / 64., 52 / 64., (height - 48) / height, (height - 36) / height);
        texture.rightSleeveBack.set(52 / 64., 56 / 64., (height - 48) / height, (height - 36) / height);
        texture.rightSleeveTop.set(44 / 64., 48 / 64., (height - 36) / height, (height - 32) / height);
        texture.rightSleeveBottom.set(48 / 64., 52 / 64., (height - 36) / height, (height - 32) / height);

        // Left sleeve texture
        texture.leftSleeveRight.set(48 / 64., 52 / 64., (height - 64) / height, (height - 52) / height);
        texture.leftSleeveFront.set(52 / 64., 56 / 64., (height - 64) / height, (height - 52) / height);
        texture.leftSleeveLeft.set(56 / 64., 60 / 64., (height - 64) / height, (height - 52) / height);
        texture.leftSleeveBack.set(60 / 64., 64 / 64., (height - 64) / height, (height - 52) / height);
        texture.leftSleeveTop.set(52 / 64., 56 / 64., (height - 52) / height, (height - 48) / height);
        texture.leftSleeveBottom.set(56 / 64., 60 / 64., (height - 52) / height, (height - 48) / height);

        // Left arm texture
        texture.leftArmRight.set(32 / 64., 36 / 64., (height - 64) / height, (height - 52) / height);
        texture.leftArmFront.set(36 / 64., 40 / 64., (height - 64) / height, (height - 52) / height);
        texture.leftArmLeft.set(40 / 64., 44 / 64., (height - 64) / height, (height - 52) / height);
        texture.leftArmBack.set(44 / 64., 48 / 64., (height - 64) / height, (height - 52) / height);
        texture.leftArmTop.set(36 / 64., 40 / 64., (height - 52) / height, (height - 48) / height);
        texture.leftArmBottom.set(40 / 64., 44 / 64., (height - 52) / height, (height - 48) / height);
      }
    } else {
      texture.leftLegRight.set(texture.rightLegLeft);
      texture.leftLegRight.x = texture.rightLegLeft.y;
      texture.leftLegRight.y = texture.rightLegLeft.x;
      texture.leftLegFront.set(texture.rightLegFront);
      texture.leftLegFront.x = texture.rightLegFront.y;
      texture.leftLegFront.y = texture.rightLegFront.x;
      texture.leftLegLeft.set(texture.rightLegRight);
      texture.leftLegLeft.x = texture.rightLegRight.y;
      texture.leftLegLeft.y = texture.rightLegRight.x;
      texture.leftLegBack.set(texture.rightLegBack);
      texture.leftLegBack.x = texture.rightLegBack.y;
      texture.leftLegBack.y = texture.rightLegBack.x;
      texture.leftLegTop.set(texture.rightLegTop);
      texture.leftLegBottom.set(texture.rightLegBottom);
      texture.leftArmRight.set(texture.rightArmRight);
      texture.leftArmFront.set(texture.rightArmFront);
      texture.leftArmLeft.set(texture.rightArmLeft);
      texture.leftArmBack.set(texture.rightArmBack);
      texture.leftArmTop.set(texture.rightArmTop);
      texture.leftArmBottom.set(texture.rightArmBottom);
    }
    return true;
  }

  @Override public boolean load(ZipFile texturePack, String topLevelDir) {
    return load(topLevelDir + file, texturePack);
  }

}
