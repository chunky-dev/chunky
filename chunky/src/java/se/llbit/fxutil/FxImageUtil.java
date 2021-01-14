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
        PixelFormat.getIntArgbInstance(), image.data, 0, image.width);
    return fxImage;
  }
}
