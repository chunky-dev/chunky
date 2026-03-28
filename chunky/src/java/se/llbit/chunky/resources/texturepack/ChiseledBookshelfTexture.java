package se.llbit.chunky.resources.texturepack;

import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.LayeredResourcePacks;
import se.llbit.chunky.resources.Texture;

import java.io.IOException;
import java.io.InputStream;

public class ChiseledBookshelfTexture extends TextureLoader {
  private final SimpleTexture emptyLoader;
  private final SimpleTexture occupiedLoader;
  private final Texture empty = new Texture();
  private final Texture occupied = new Texture();

  public ChiseledBookshelfTexture(String emptyPath, String occupiedPath) {
    emptyLoader = new SimpleTexture(emptyPath, empty);
    occupiedLoader = new SimpleTexture(occupiedPath, occupied);
  }

  @Override
  public boolean load(LayeredResourcePacks resourcePacks) {
    if (!emptyLoader.load(resourcePacks) || !occupiedLoader.load(resourcePacks)) {
      return false;
    }
    if (empty.getWidth() != occupied.getWidth() || empty.getHeight() != occupied.getHeight()) {
      // To simplify this implemention, we expect empty and occupied textures to have the same size
      // This should be the case unless you're mixing resource packs in a weird way
      return false;
    }
    BitmapImage occupiedBitmap = occupied.getBitmap();
    int w = empty.getWidth();
    int h = empty.getHeight();
    for (int i = 0; i < Texture.chiseledBookshelfCombinations.length; i++) {
      BitmapImage bitmap = new BitmapImage(empty.getBitmap());
      boolean topLeft = i % 2 == 1, topCenter = (i >> 1) % 2 == 1, topRight = (i >> 2) % 2 == 1,
        bottomLeft = (i >> 3) % 2 == 1, bottomCenter = (i >> 4) % 2 == 1, bottomRight = (i >> 5) % 2 == 1;
      if (topLeft) {
        bitmap.blit(occupiedBitmap, 0, 0, 0, 0, w / 3, h / 2);
      }
      if (topCenter) {
        bitmap.blit(occupiedBitmap, w / 3, 0, w / 3, 0, w * 2 / 3, h / 2);
      }
      if (topRight) {
        bitmap.blit(occupiedBitmap, w * 2 / 3, 0, w * 2 / 3, 0, w, h / 2);
      }
      if (bottomLeft) {
        bitmap.blit(occupiedBitmap, 0, h / 2, 0, h / 2, w / 3, h);
      }
      if (bottomCenter) {
        bitmap.blit(occupiedBitmap, w / 3, h / 2, w / 3, h / 2, w * 2 / 3, h);
      }
      if (bottomRight) {
        bitmap.blit(occupiedBitmap, w * 2 / 3, h / 2, w * 2 / 3, h / 2, w, h);
      }
      Texture.chiseledBookshelfCombinations[i].setTexture(bitmap);
    }
    return true;
  }

  @Override
  protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    return false;
  }
}
