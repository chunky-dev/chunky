package se.llbit.chunky.resources.texturepack;

import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.LayeredResourcePacks;

import java.io.IOException;
import java.io.InputStream;

/**
 * This texture loader will load different textures depending on a texture being available.
 */
public class ConditionalTextures extends TextureLoader {
  private final String testFor;
  private final TextureLoader then;
  private final TextureLoader otherwise;

  public ConditionalTextures(String testFor, TextureLoader then, TextureLoader otherwise) {
    this.testFor = testFor;
    this.then = then;
    this.otherwise = otherwise;
  }

  @Override
  public boolean load(LayeredResourcePacks texturePack) {
    if (texturePack.getFirstEntry(testFor).isPresent()) {
      return then.load(texturePack);
    }
    return otherwise.load(texturePack);
  }

  @Override
  protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    throw new UnsupportedOperationException("Call load(ZipFile) instead!");
  }
}
