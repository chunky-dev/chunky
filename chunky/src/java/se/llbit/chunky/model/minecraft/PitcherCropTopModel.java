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

public class PitcherCropTopModel extends QuadModel {
  private static final AbstractTexture top = Texture.pitcherCropTop;
  private static final AbstractTexture side = Texture.pitcherCropSide;
  private static final AbstractTexture bottom = Texture.pitcherCropBottom;
  private static final AbstractTexture bottomStage2 = Texture.pitcherCropBottomStage2;
  private static final AbstractTexture topStage3 = Texture.pitcherCropTopStage3;
  private static final AbstractTexture topStage4 = Texture.pitcherCropTopStage4;

  private static final AbstractTexture[][] textures = new AbstractTexture[][]{
    null, null,
    new AbstractTexture[]{
      bottomStage2, bottomStage2, bottomStage2, bottomStage2, top, bottom, side, side, side, side
    },
    new AbstractTexture[]{
      topStage3, topStage3, topStage3, topStage3
    },
    new AbstractTexture[]{
      topStage4, topStage4, topStage4, topStage4
    }
  };

  private static final Quad[][] quads = new Quad[][]{
    null, null,
    Model.translate(Model.join(
      Model.rotateY(new Quad[]{
        new Quad(
          new Vector3(0 / 16.0, 21 / 16.0, 8 / 16.0),
          new Vector3(16 / 16.0, 21 / 16.0, 8 / 16.0),
          new Vector3(0 / 16.0, 5 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(16 / 16.0, 21 / 16.0, 8 / 16.0),
          new Vector3(0 / 16.0, 21 / 16.0, 8 / 16.0),
          new Vector3(16 / 16.0, 5 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(8 / 16.0, 21 / 16.0, 16 / 16.0),
          new Vector3(8 / 16.0, 21 / 16.0, 0 / 16.0),
          new Vector3(8 / 16.0, 5 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(8 / 16.0, 21 / 16.0, 0 / 16.0),
          new Vector3(8 / 16.0, 21 / 16.0, 16 / 16.0),
          new Vector3(8 / 16.0, 5 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        )
      }, Math.toRadians(45)),
      new Quad[]{
        new Quad(
          new Vector3(3 / 16.0, 5 / 16.0, 13 / 16.0),
          new Vector3(13 / 16.0, 5 / 16.0, 13 / 16.0),
          new Vector3(3 / 16.0, 5 / 16.0, 3 / 16.0),
          new Vector4(3 / 16.0, 13 / 16.0, 3 / 16.0, 13 / 16.0)
        ),
        new Quad(
          new Vector3(3 / 16.0, -1 / 16.0, 3 / 16.0),
          new Vector3(13 / 16.0, -1 / 16.0, 3 / 16.0),
          new Vector3(3 / 16.0, -1 / 16.0, 13 / 16.0),
          new Vector4(3 / 16.0, 13 / 16.0, 3 / 16.0, 13 / 16.0)
        ),
        new Quad(
          new Vector3(3 / 16.0, 5 / 16.0, 13 / 16.0),
          new Vector3(3 / 16.0, 5 / 16.0, 3 / 16.0),
          new Vector3(3 / 16.0, -1 / 16.0, 13 / 16.0),
          new Vector4(13 / 16.0, 3 / 16.0, 6 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(13 / 16.0, 5 / 16.0, 3 / 16.0),
          new Vector3(13 / 16.0, 5 / 16.0, 13 / 16.0),
          new Vector3(13 / 16.0, -1 / 16.0, 3 / 16.0),
          new Vector4(13 / 16.0, 3 / 16.0, 6 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(3 / 16.0, 5 / 16.0, 3 / 16.0),
          new Vector3(13 / 16.0, 5 / 16.0, 3 / 16.0),
          new Vector3(3 / 16.0, -1 / 16.0, 3 / 16.0),
          new Vector4(13 / 16.0, 3 / 16.0, 6 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(13 / 16.0, 5 / 16.0, 13 / 16.0),
          new Vector3(3 / 16.0, 5 / 16.0, 13 / 16.0),
          new Vector3(13 / 16.0, -1 / 16.0, 13 / 16.0),
          new Vector4(13 / 16.0, 3 / 16.0, 6 / 16.0, 0 / 16.0)
        )
      }
    ), 0, -1, 0),
    Model.join(
      Model.rotateY(new Quad[]{
        new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        )
      }, Math.toRadians(45)),
      Model.rotateY(new Quad[]{
        new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        )
      }, Math.toRadians(-45))
    ),
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(8 / 16.0, 16 / 16.0, 16 / 16.0),
        new Vector3(8 / 16.0, 16 / 16.0, 0 / 16.0),
        new Vector3(8 / 16.0, 0 / 16.0, 16 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
        new Vector3(8 / 16.0, 16 / 16.0, 0 / 16.0),
        new Vector3(8 / 16.0, 16 / 16.0, 16 / 16.0),
        new Vector3(8 / 16.0, 0 / 16.0, 0 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
        new Vector3(0 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(16 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(0 / 16.0, 0 / 16.0, 8 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
        new Vector3(16 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(0 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(16 / 16.0, 0 / 16.0, 8 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      )
    }, Math.toRadians(45))
  };

  private final int age;

  public PitcherCropTopModel(int age) {
    this.age = age;
  }

  @Override
  public Quad[] getQuads() {
    return quads[age];
  }

  @Override
  public AbstractTexture[] getTextures() {
    return textures[age];
  }
}
