/*
 * Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.Texture;
import se.llbit.log.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

/**
 * This texture loader is an adapter for the pre-1.12 bed textures.
 * It composes the post-1.12 bed texture from parts of the pre-1.12 textures.
 *
 * <p>This texture loader is only used if a post-1.12 red bed texture is not found.
 */
public class BedTextureAdapter extends TextureLoader {
  private final Texture bedHeadTop = new Texture();
  private final Texture bedFootTop = new Texture();
  private final Texture bedFootEnd = new Texture();
  private final Texture bedFootSide = new Texture();
  private final Texture bedHeadSide = new Texture();
  private final Texture bedHeadEnd = new Texture();
  private final Texture bottom = new Texture();
  private final AlternateTextures bottomLoader;
  private final AlternateTextures footTopLoader;
  private final AlternateTextures headTopLoader;
  private final AlternateTextures footEndLoader;
  private final AlternateTextures footSideLoader;
  private final AlternateTextures headSideLoader;
  private final AlternateTextures headEndLoader;

  public BedTextureAdapter() {
    bottomLoader = new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/blocks/planks_oak", bottom),
        new SimpleTexture("textures/blocks/wood", bottom),
        new IndexedTexture(0x04, bottom));
    footTopLoader = new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/blocks/bed_feet_top", bedFootTop),
        new SimpleTexture("textures/blocks/bed_feet_top", bedFootTop),
        new IndexedTexture(0x86, bedFootTop));
    headTopLoader = new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/blocks/bed_head_top", bedHeadTop),
        new SimpleTexture("textures/blocks/bed_head_top", bedHeadTop),
        new IndexedTexture(0x87, bedHeadTop));
    footEndLoader = new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/blocks/bed_feet_end", bedFootEnd),
        new SimpleTexture("textures/blocks/bed_feet_end", bedFootEnd),
        new IndexedTexture(0x95, bedFootEnd));
    footSideLoader = new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/blocks/bed_feet_side", bedFootSide),
        new SimpleTexture("textures/blocks/bed_feet_side", bedFootSide),
        new IndexedTexture(0x96, bedFootSide));
    headSideLoader = new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/blocks/bed_head_side", bedHeadSide),
        new SimpleTexture("textures/blocks/bed_head_side", bedHeadSide),
        new IndexedTexture(0x97, bedHeadSide));
    headEndLoader = new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/blocks/bed_head_end", bedHeadEnd),
        new SimpleTexture("textures/blocks/bed_head_end", bedHeadEnd),
        new IndexedTexture(0x98, bedHeadEnd));
  }

  @Override public boolean load(ZipFile texturePack, String topLevelDir) {
    boolean allLoaded = true;
    int scale = 1;
    BitmapImage bitmap = new BitmapImage(64, 64);
    // All texture parts must have the same scale.
    if (bottomLoader.load(texturePack, topLevelDir)) {
      // Copy bottom texture to right position.
      scale = bottom.getWidth() / 16;
      if (16 * scale != bottom.getWidth()) {
        Log.warn("Can't load pre-1.12 bed texture: oak plank texture size must be "
            + "an even multiple of 16.");
        return false;
      }
      bitmap = new BitmapImage(64 * scale, 64 * scale);
      bitmap.blit(bottom.getBitmap(), 28 * scale, 6 * scale);
      bitmap.blit(bottom.getBitmap(), 28 * scale, 28 * scale);
    } else {
      allLoaded = false;
    }
    if (footEndLoader.load(texturePack, topLevelDir) && checkSize(bedFootEnd, scale)) {
      bitmap.blit(bedFootEnd.getBitmap().vFlipped(),
          22 * scale, 22 * scale,
          0, 3 * scale,
          16 * scale, 9 * scale);
      // Leg 1 (left):
      bitmap.blit(bedFootEnd.getBitmap(),
          53 * scale, 3 * scale,
          0, 13 * scale,
          3 * scale, 16 * scale);
      // Leg 3 (right):
      bitmap.blit(bedFootEnd.getBitmap(),
          50 * scale, 15 * scale,
          13 * scale, 13 * scale,
          16 * scale, 16 * scale);
    } else {
      allLoaded = false;
    }
    if (footSideLoader.load(texturePack, topLevelDir) && checkSize(bedFootSide, scale)) {
      bitmap.blit(bedFootSide.getBitmap().rotated().vFlipped(),
          0, 28 * scale,
          3 * scale, 0,
          9 * scale, 16 * scale);
      bitmap.blit(bedFootSide.getBitmap().rotated270(),
          22 * scale, 28 * scale,
          7 * scale, 0,
          13 * scale, 16 * scale);
      // Leg 1 (bottom left):
      bitmap.blit(bedFootSide.getBitmap().hFlipped(),
          50 * scale, 3 * scale,
          13 * scale, 13 * scale,
          16 * scale, 16 * scale);
      // Leg 3 (bottom right):
      bitmap.blit(bedFootSide.getBitmap(),
          53 * scale, 15 * scale,
          0, 13 * scale,
          3 * scale, 16 * scale);
    } else {
      allLoaded = false;
    }
    if (footTopLoader.load(texturePack, topLevelDir) && checkSize(bedFootTop, scale)) {
      bitmap.blit(bedFootTop.getBitmap().rotated180(), 6 * scale, 28 * scale);
    } else {
      allLoaded = false;
    }
    if (headEndLoader.load(texturePack, topLevelDir) && checkSize(bedHeadEnd, scale)) {
      bitmap.blit(bedHeadEnd.getBitmap().vFlipped(),
          6 * scale, 0,
          0, 3 * scale,
          16 * scale, 9 * scale);
      // Leg 4 (left):
      bitmap.blit(bedHeadEnd.getBitmap(),
          53 * scale, 21 * scale,
          0, 13 * scale,
          3 * scale, 16 * scale);
      // Leg 2 (right):
      bitmap.blit(bedHeadEnd.getBitmap(),
          50 * scale, 9 * scale,
          13 * scale, 13 * scale,
          16 * scale, 16 * scale);
    } else {
      allLoaded = false;
    }
    if (headSideLoader.load(texturePack, topLevelDir) && checkSize(bedHeadSide, scale)) {
      bitmap.blit(bedHeadSide.getBitmap().rotated().vFlipped(),
          0, 6 * scale,
          3 * scale, 0,
          9 * scale, 16 * scale);
      bitmap.blit(bedHeadSide.getBitmap().rotated270(),
          22 * scale, 6 * scale,
          7 * scale, 0,
          13 * scale, 16 * scale);
      // Leg 4 (top left):
      bitmap.blit(bedHeadSide.getBitmap(),
          50 * scale, 21 * scale,
          13 * scale, 13 * scale,
          16 * scale, 16 * scale);
      // Leg 2 (top right):
      bitmap.blit(bedHeadSide.getBitmap().hFlipped(),
          53 * scale, 9 * scale,
          0, 13 * scale,
          3 * scale, 16 * scale);
    } else {
      allLoaded = false;
    }
    if (headTopLoader.load(texturePack, topLevelDir) && checkSize(bedHeadTop, scale)) {
      bitmap.blit(bedHeadTop.getBitmap().rotated270(), 6 * scale, 6 * scale);
    } else {
      allLoaded = false;
    }
    Texture.bedRed.setTexture(bitmap);
    return allLoaded;
  }

  /**
   * Ensures that the bed texture part has the right dimensions.
   * @return {@code true} if the texture has the correct size
   */
  private boolean checkSize(Texture texture, int scale) {
    if (texture.getWidth() != 16 * scale) {
      Log.warnf("Bed texture has wrong scale: expected %d, but was %d.",
          16 * scale, texture.getWidth());
      return false;
    }
    if (texture.getWidth() != texture.getHeight()) {
      Log.warnf("Bed texture is not square: %dx%d",
          texture.getWidth(), texture.getHeight());
      return false;
    }
    return true;
  }

  @Override protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    return false;
  }
}
