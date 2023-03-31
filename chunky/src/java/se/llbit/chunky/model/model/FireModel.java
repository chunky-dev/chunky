/*
 * Copyright (c) 2015-2023 Chunky contributors
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
package se.llbit.chunky.model.model;

import se.llbit.chunky.model.AnimatedQuadModel;
import se.llbit.chunky.resources.AnimatedTexture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class FireModel extends AnimatedQuadModel {
  private final static Quad[] quads = {
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 1), new Vector3(0, 1, 0),
          new Vector4(0, 1, 0, 1)),

      new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 1), new Vector3(1, 1, 0),
          new Vector4(0, 1, 0, 1)),

      new Quad(new Vector3(1, 0, 1), new Vector3(0, 0, 0), new Vector3(1, 1, 1),
          new Vector4(1, 0, 0, 1)),

      new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 0), new Vector3(0, 1, 1),
          new Vector4(1, 0, 0, 1)),
  };

  private final AnimatedTexture[] textures;

  public FireModel(AnimatedTexture tex0, AnimatedTexture tex1) {
    super(20, true);
    this.textures = new AnimatedTexture[] {tex0, tex1, tex0, tex1};
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public AnimatedTexture[] getTextures() {
    return textures;
  }
}
