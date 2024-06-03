package se.llbit.chunky.resources.texturepack;

import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.LayeredResourcePacks;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * This texture will try to load several textures and fail if at least one of them could not be
 * loaded.
 */
public class AllTextures extends TextureLoader {
  private final TextureLoader[] textures;

  /**
   * Attempts to load all given textures.
   *
   * @param textures List of textures to load
   */
  public AllTextures(TextureLoader... textures) {
    this.textures = textures;
  }

  /**
   * Don't use this.
   */
  public AllTextures(TextureLoader ignored) {
    throw new Error("It is pointless to create an all texture loader with only one texture.");
  }

  @Override
  public boolean load(LayeredResourcePacks texturePack) {
    int loaded = 0;
    for (TextureLoader alternative : textures) {
      if (alternative.load(texturePack)) {
        loaded++;
      }
    }
    return loaded == textures.length;
  }

  @Override
  public boolean loadFromTerrain(BitmapImage[] terrain) {
    int loaded = 0;
    for (TextureLoader alternative : textures) {
      if (alternative.loadFromTerrain(terrain)) {
        loaded++;
      }
    }
    return loaded == textures.length;
  }

  @Override
  protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    throw new UnsupportedOperationException("Call load(ZipFile) instead!");
  }
}
