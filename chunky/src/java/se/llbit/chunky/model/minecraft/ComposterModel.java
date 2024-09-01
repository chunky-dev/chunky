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
import se.llbit.math.*;

public class ComposterModel extends QuadModel {
  private static final AbstractTexture top = Texture.composterTop;
  private static final AbstractTexture bottom = Texture.composterBottom;
  private static final AbstractTexture side = Texture.composterSide;
  private static final AbstractTexture inside = Texture.composterBottom;
  private static final AbstractTexture[] composterTex = new AbstractTexture[] {
      inside, bottom, top, side, side, side, side, top, side, side, side, side, top, side, side, top, side, side
  };

  //region Composter
  private static final Quad[] composter = new Quad[] {
      new Quad(
          new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector4(1 - 2 / 16.0, 1 - 0 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector4(14 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector4(1 - 16 / 16.0, 1 - 14 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector4(14 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 1 - 2 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector4(1 - 14 / 16.0, 1 - 2 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 0 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 1 - 16 / 16.0, 1 - 14 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 0 / 16.0, 14 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 14 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector4(1 - 14 / 16.0, 1 - 2 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 0 / 16.0, 16 / 16.0)
      )
  };
  //endregion


  private static final AbstractTexture[] contentTex = new AbstractTexture[] {
      null, Texture.composterCompost, Texture.composterCompost, Texture.composterCompost, Texture.composterCompost,
      Texture.composterCompost, Texture.composterCompost, Texture.composterCompost, Texture.composterReady
  };

  //region Content
  private static final Quad[] content = {
      null,
      new Quad(
          new Vector3(14 / 16.0, 3 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 3 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 3 / 16.0, 14 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 5 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 5 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 5 / 16.0, 14 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 7 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 7 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 7 / 16.0, 14 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 9 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 9 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 9 / 16.0, 14 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 11 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 11 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 11 / 16.0, 14 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 13 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 13 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 13 / 16.0, 14 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 15 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 15 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 15 / 16.0, 14 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 15 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 15 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 15 / 16.0, 14 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)
      )
  };
  //endregion

  private final Quad[] quads;
  private final AbstractTexture[] textures;

  public ComposterModel(int level) {
    if (level == 0) {
      quads = composter;
      textures = composterTex;
    } else {
      quads = Model.join(composter, new Quad[] {content[level]});
      textures = new AbstractTexture[quads.length];
      System.arraycopy(composterTex, 0, textures, 0, composterTex.length);
      textures[composterTex.length] =  contentTex[level];
    }
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public AbstractTexture[] getTextures() {
    return textures;
  }
}
