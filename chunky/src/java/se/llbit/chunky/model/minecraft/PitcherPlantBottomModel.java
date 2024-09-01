/*
 * Copyright (c) 2023 Chunky contributors
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

package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class PitcherPlantBottomModel extends QuadModel {
  private static final AbstractTexture bottom = Texture.pitcherCropBottomStage4;
  private static final AbstractTexture top = Texture.pitcherCropTopStage4;
  private static final AbstractTexture[] textures = new AbstractTexture[]{
    bottom, bottom, bottom, bottom, bottom, bottom, bottom, bottom, bottom, bottom, bottom, bottom,
    top, top, top, top, top, top, top, top, top, top, top, top
  };

  private static final Quad[] quads = Model.rotateY(Model.join(new Quad[]{
    // bottom part
    new Quad(
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, -5 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 11 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 11 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    )
  }, Model.translate(new Quad[]{
    // top part
    new Quad(
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, -5 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 11 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 11 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, 11 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, -5 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 11 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, -5 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    )
  }, 0, 1, 0)), Math.toRadians(45));

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public AbstractTexture[] getTextures() {
    return textures;
  }
}
