package se.llbit.chunky.resources.texturepack;

import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.LayeredResourcePacks;
import se.llbit.chunky.resources.Texture;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

/**
 * This texture loader is an adapter for the 26.2-snapshot-7 (or later) sign textures.
 * It converts pre-26.2-snapshot-7 sign textures to the new format.
 */
public class SignTextureAdapter26_2 extends TextureLoader {
  private final Texture targetTexture;
  private final Function<Texture, TextureLoader> originalTextureLoader;

  public SignTextureAdapter26_2(Texture targetTexture, Function<Texture, TextureLoader> originalTextureLoader) {
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

    newTexture.blit(originalTexture.getBitmap(), 0, 2 * scale, 2 * scale, 2 * scale, 28 * scale, 14 * scale);
    newTexture.blit(originalTexture.getBitmap(), 0, 16 * scale, 28 * scale, 2 * scale, 52 * scale, 14 * scale);
    newTexture.blit(originalTexture.getBitmap(), 0, 0, 2 * scale, 0, 26 * scale, 2 * scale);
    newTexture.blit(originalTexture.getBitmap(), 24 * scale, 16 * scale, 0, 2 * scale, 2 * scale, 14 * scale);
    newTexture.blit(originalTexture.getBitmap(), 0, 28 * scale, 26 * scale, 2 * scale, 50 * scale, 0);
    newTexture.blit(originalTexture.getBitmap(), 28 * scale, 0, 2 * scale, 16 * scale, 6 * scale, 30 * scale);
    newTexture.blit(originalTexture.getBitmap(), 28 * scale, 16 * scale, 6 * scale, 16 * scale, 8 * scale, 30 * scale);
    newTexture.blit(originalTexture.getBitmap(), 30 * scale, 16 * scale, 0, 16 * scale, 2 * scale, 30 * scale);
    newTexture.blit(originalTexture.getBitmap(), 28 * scale, 30 * scale, 4 * scale, 16 * scale, 6 * scale, 14 * scale);

    targetTexture.setTexture(newTexture);
    return true;
  }

  @Override
  protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    return false;
  }
}
