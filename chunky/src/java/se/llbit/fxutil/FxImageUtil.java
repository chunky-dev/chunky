/* Copyright (c) 2021 Chunky contributors
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
package se.llbit.fxutil;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import se.llbit.chunky.resources.BitmapImage;

public class FxImageUtil {

  /**
   * @return a JavaFX version of a BitmapImage.
   */
  public static Image toFxImage(BitmapImage image) {
    WritableImage fxImage = new WritableImage(image.width, image.height);
    fxImage.getPixelWriter().setPixels(0, 0, image.width, image.height,
        PixelFormat.getIntArgbInstance(), image.toIntArray(), 0, image.width);
    return fxImage;
  }
}
