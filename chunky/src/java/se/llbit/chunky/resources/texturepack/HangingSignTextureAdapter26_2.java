package se.llbit.chunky.resources.texturepack;

import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.LayeredResourcePacks;
import se.llbit.chunky.resources.Texture;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

/**
 * This texture loader is an adapter for the 26.2-snapshot-7 (or later) hanging sign textures.
 * It converts pre-26.2-snapshot-7 hanging sign textures to the new format.
 */
public class HangingSignTextureAdapter26_2 extends TextureLoader {
  private final Texture targetTexture;
  private final Function<Texture, TextureLoader> originalTextureLoader;

  public HangingSignTextureAdapter26_2(Texture targetTexture, Function<Texture, TextureLoader> originalTextureLoader) {
    this.targetTexture = targetTexture;
    this.originalTextureLoader = originalTextureLoader;
  }

  @Override
  public boolean load(LayeredResourcePacks resourcePacks) {
    Texture originalTexture = new Texture();
    if (!originalTextureLoader.apply(originalTexture).load(resourcePacks)) {
      return false;
    }

    int scale = originalTexture.getWidth() / 64;
    BitmapImage newTexture = new BitmapImage(32 * scale, 32 * scale);

    newTexture.blit(originalTexture.getBitmap(), 0, 0, 4 * scale, 0, 20 * scale, 4 * scale);
    newTexture.blit(originalTexture.getBitmap(), 0, 4 * scale, 4 * scale, 4 * scale, 24 * scale, 6 * scale);
    newTexture.blit(originalTexture.getBitmap(), 20 * scale, 0, 14 * scale, 6 * scale, 26 * scale, 12 * scale);
    newTexture.blit(originalTexture.getBitmap(), 0, 7 * scale, 24 * scale, 4 * scale, 40 * scale, 6 * scale);
    newTexture.blit(originalTexture.getBitmap(), 16 * scale, 7 * scale, 0, 4 * scale, 4 * scale, 6 * scale);
    newTexture.blit(originalTexture.getBitmap(), 0, 9 * scale, 36 * scale, 4 * scale, 20 * scale, 0);
    newTexture.blit(originalTexture.getBitmap(), 22 * scale, 7 * scale, 0, 6 * scale, 9 * scale, 12 * scale);
    newTexture.blit(originalTexture.getBitmap(), 0, 16 * scale, 0, 14 * scale, 32 * scale, 24 * scale);
    newTexture.blit(originalTexture.getBitmap(), 2 * scale, 14 * scale, 2 * scale, 12 * scale, 16 * scale, 14 * scale);
    newTexture.blit(originalTexture.getBitmap(), 2 * scale, 26 * scale, 16 * scale, 14 * scale, 30 * scale, 12 * scale);

    targetTexture.setTexture(newTexture);
    return true;
  }

  @Override
  protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    return false;
  }
}
