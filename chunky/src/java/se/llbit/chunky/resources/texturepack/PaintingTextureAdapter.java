/*
 * Copyright (c) 2021 Chunky contributors
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
import se.llbit.chunky.resources.LayeredResourcePacks;
import se.llbit.chunky.resources.Texture;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * This texture loader is an adapter for the pre-1.14 painting textures. It splits the pre-1.14
 * paintings texture atlas into multiple textures.
 */
public class PaintingTextureAdapter extends TextureLoader {

  @Override
  public boolean load(LayeredResourcePacks texturePack) {
    PaintingsAtlas paintings = new PaintingsAtlas();
    if (new SimpleTexture("assets/minecraft/textures/painting/paintings_kristoffer_zetterstrand",
            paintings).load(texturePack)) {
      Texture.paintingKebab.setTexture(paintings.getPainting(0, 16, 0, 16));
      Texture.paintingAztec.setTexture(paintings.getPainting(16, 32, 0, 16));
      Texture.paintingAlban.setTexture(paintings.getPainting(32, 48, 0, 16));
      Texture.paintingAztec2.setTexture(paintings.getPainting(48, 64, 0, 16));
      Texture.paintingBomb.setTexture(paintings.getPainting(64, 80, 0, 16));
      Texture.paintingPlant.setTexture(paintings.getPainting(80, 96, 0, 16));
      Texture.paintingWasteland.setTexture(paintings.getPainting(96, 112, 0, 16));
      Texture.paintingWanderer.setTexture(paintings.getPainting(0, 16, 64, 96));
      Texture.paintingGraham.setTexture(paintings.getPainting(16, 32, 64, 96));
      Texture.paintingPool.setTexture(paintings.getPainting(0, 32, 32, 48));
      Texture.paintingCourbet.setTexture(paintings.getPainting(32, 64, 32, 48));
      Texture.paintingSunset.setTexture(paintings.getPainting(96, 128, 32, 48));
      Texture.paintingSea.setTexture(paintings.getPainting(64, 96, 32, 48));
      Texture.paintingCreebet.setTexture(paintings.getPainting(128, 160, 32, 48));
      Texture.paintingMatch.setTexture(paintings.getPainting(0, 32, 128, 160));
      Texture.paintingBust.setTexture(paintings.getPainting(32, 64, 128, 160));
      Texture.paintingStage.setTexture(paintings.getPainting(64, 96, 128, 160));
      Texture.paintingVoid.setTexture(paintings.getPainting(96, 128, 128, 160));
      Texture.paintingSkullAndRoses.setTexture(paintings.getPainting(128, 160, 128, 160));
      Texture.paintingWither.setTexture(paintings.getPainting(160, 192, 128, 160));
      Texture.paintingFighters.setTexture(paintings.getPainting(0, 64, 96, 128));
      Texture.paintingSkeleton.setTexture(paintings.getPainting(192, 256, 64, 112));
      Texture.paintingDonkeyKong.setTexture(paintings.getPainting(192, 256, 112, 160));
      Texture.paintingPointer.setTexture(paintings.getPainting(0, 64, 192, 256));
      Texture.paintingPigscene.setTexture(paintings.getPainting(64, 128, 192, 256));
      Texture.paintingBurningSkull.setTexture(paintings.getPainting(128, 192, 192, 256));
      Texture.paintingBack.setTexture(paintings.getPainting(192, 256, 0, 64));
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    return false;
  }

  @Override
  public void reset() {
    // the painting textures are reset by the corresponding PaintingTexture loaders
  }

  private static class PaintingsAtlas extends Texture {

    public BitmapImage getPainting(int px0, int px1, int py0, int py1) {
      BitmapImage painting = new BitmapImage(px1 - px0, py1 - py0);
      painting.blit(getBitmap(), 0, 0, px0, py0, px1, py1);
      return painting;
    }
  }
}
