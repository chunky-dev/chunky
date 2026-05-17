package se.llbit.chunky.resources.texturepack;

import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.LayeredResourcePacks;
import se.llbit.chunky.resources.Texture;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

/**
 * This texture loader is an adapter for the 26.2-snapshot-3 (or later) bed textures.
 * It composes post 26.2-snapshot-3 textures from post-1.12 bed textures.
 */
public class BedTextureAdapter26_2 extends TextureLoader {
  private final BedTexture.Textures targetTextures;
  private final Function<Texture, TextureLoader> originalTextureLoader;

  public BedTextureAdapter26_2(BedTexture.Textures targetTextures, Function<Texture, TextureLoader> originalTextureLoader) {
    this.targetTextures = targetTextures;
    this.originalTextureLoader = originalTextureLoader;
  }

  @Override
  public boolean load(LayeredResourcePacks resourcePacks) {
    Texture originalTexture = new Texture();
    if (!originalTextureLoader.apply(originalTexture).load(resourcePacks)) {
      return false;
    }

    int scale = originalTexture.getWidth() / 64;
    BitmapImage headUp = new BitmapImage(16 * scale, 16 * scale);
    headUp.blit(originalTexture.getBitmap(), 0, 0, 6 * scale, 6 * scale, 22 * scale, 22 * scale);
    targetTextures.headUp.setTexture(headUp);

    BitmapImage headDown = new BitmapImage(16 * scale, 16 * scale);
    headDown.blit(originalTexture.getBitmap(), 0, 0, 44 * scale, 22 * scale, 28 * scale, 6 * scale);
    targetTextures.headDown.setTexture(headDown);

    BitmapImage headNorth = new BitmapImage(16 * scale, 16 * scale);
    headNorth.blit(originalTexture.getBitmap(), 0, 7 * scale, 22 * scale, 6 * scale, 6 * scale, 0);
    headNorth.blit(originalTexture.getBitmap(), 0, 13 * scale, 53 * scale, 3 * scale, 59 * scale, 6 * scale);
    headNorth.blit(originalTexture.getBitmap(), 10 * scale, 13 * scale, 59 * scale, 3 * scale, 62 * scale, 6 * scale);
    headNorth.blit(originalTexture.getBitmap(), 13 * scale, 13 * scale, 50 * scale, 3 * scale, 53 * scale, 6 * scale);
    targetTextures.headNorth.setTexture(headNorth);

    BitmapImage headEast = new BitmapImage(16 * scale, 16 * scale);
    BitmapImage tmp = new BitmapImage(6 * scale, 16 * scale);
    tmp.blit(originalTexture.getBitmap(), 0, 0, 22 * scale, 6 * scale, 28 * scale, 22 * scale);
    headEast.blit(tmp.rotated(), 0, 7 * scale);
    headEast.blit(originalTexture.getBitmap(), 13 * scale, 13 * scale, 50 * scale, 3 * scale, 53 * scale, 6 * scale);
    headEast.blit(originalTexture.getBitmap(), 10 * scale, 13 * scale, 59 * scale, 3 * scale, 62 * scale, 6 * scale);
    headEast.blit(originalTexture.getBitmap(), 7 * scale, 13 * scale, 59 * scale, 0, 56 * scale, 3 * scale);
    targetTextures.headEast.setTexture(headEast);

    BitmapImage headWest = new BitmapImage(16 * scale, 16 * scale);
    tmp = new BitmapImage(6 * scale, 16 * scale);
    tmp.blit(originalTexture.getBitmap(), 0, 0, 0, 6 * scale, 6 * scale, 22 * scale);
    headWest.blit(tmp.rotated270(), 0, 7 * scale);
    headWest.blit(originalTexture.getBitmap(), 0, 13 * scale, 53 * scale, 3 * scale, 59 * scale, 6 * scale);
    tmp = new BitmapImage(3 * scale, 3 * scale);
    tmp.blit(originalTexture.getBitmap(), 0, 0, 56 * scale, 0, 59 * scale, 3 * scale);
    headWest.blit(tmp.rotated270(), 6 * scale, 13 * scale);
    targetTextures.headWest.setTexture(headWest);

    BitmapImage footUp = new BitmapImage(16 * scale, 16 * scale);
    footUp.blit(originalTexture.getBitmap(), 0, 0, 6 * scale, 28 * scale, 22 * scale, 44 * scale);
    targetTextures.footUp.setTexture(footUp);

    BitmapImage footDown = new BitmapImage(16 * scale, 16 * scale);
    footDown.blit(originalTexture.getBitmap(), 0, 0, 44 * scale, 44 * scale, 28 * scale, 28 * scale);
    targetTextures.footDown.setTexture(footDown);

    BitmapImage footSouth = new BitmapImage(16 * scale, 16 * scale);
    footSouth.blit(originalTexture.getBitmap(), 0, 7 * scale, 22 * scale, 28 * scale, 38 * scale, 22 * scale);
    footSouth.blit(originalTexture.getBitmap(), 0, 13 * scale, 53 * scale, 3 * scale, 59 * scale, 6 * scale);
    footSouth.blit(originalTexture.getBitmap(), 10 * scale, 13 * scale, 59 * scale, 3 * scale, 62 * scale, 6 * scale);
    footSouth.blit(originalTexture.getBitmap(), 13 * scale, 13 * scale, 50 * scale, 3 * scale, 53 * scale, 6 * scale);
    targetTextures.footSouth.setTexture(footSouth);

    BitmapImage footEast = new BitmapImage(16 * scale, 16 * scale);
    tmp = new BitmapImage(6 * scale, 16 * scale);
    tmp.blit(originalTexture.getBitmap(), 0, 0, 22 * scale, 28 * scale, 28 * scale, 44 * scale);
    footEast.blit(tmp.rotated(), 0, 7 * scale);
    footEast.blit(originalTexture.getBitmap(), 0, 13 * scale, 53 * scale, 3 * scale, 59 * scale, 6 * scale);
    tmp = new BitmapImage(3 * scale, 3 * scale);
    tmp.blit(originalTexture.getBitmap(), 0, 0, 56 * scale, 0, 59 * scale, 3 * scale);
    footEast.blit(tmp.rotated(), 6 * scale, 13 * scale);
    targetTextures.footEast.setTexture(footEast);

    BitmapImage footWest = new BitmapImage(16 * scale, 16 * scale);
    tmp = new BitmapImage(6 * scale, 16 * scale);
    tmp.blit(originalTexture.getBitmap(), 0, 0, 0, 28 * scale, 6 * scale, 44 * scale);
    footWest.blit(tmp.rotated270(), 0, 7 * scale);
    footWest.blit(originalTexture.getBitmap(), 10 * scale, 13 * scale, 59 * scale, 3 * scale, 62 * scale, 6 * scale);
    footWest.blit(originalTexture.getBitmap(), 13 * scale, 13 * scale, 50 * scale, 3 * scale, 53 * scale, 6 * scale);
    footWest.blit(originalTexture.getBitmap(), 7 * scale, 13 * scale, 56 * scale, 0, 59 * scale, 3 * scale);
    targetTextures.footWest.setTexture(footWest);

    return true;
  }

  @Override
  protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    return false;
  }
}
