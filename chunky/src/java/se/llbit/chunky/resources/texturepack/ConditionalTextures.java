package se.llbit.chunky.resources.texturepack;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;
import se.llbit.chunky.resources.BitmapImage;

/** This texture loader will load different textures depending on a texture being available. */
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
  public boolean load(ZipFile texturePack, String topLevelDir) {
    if (texturePack.getEntry(topLevelDir + testFor) != null) {
      return then.load(texturePack, topLevelDir);
    }
    return otherwise.load(texturePack, topLevelDir);
  }

  @Override
  public boolean loadFromTerrain(BitmapImage[] terrain) {
    throw new UnsupportedOperationException("ConditionalTextures doesn't support loadFromTerrain");
  }

  @Override
  protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    throw new UnsupportedOperationException("Call load(ZipFile) instead!");
  }
}
