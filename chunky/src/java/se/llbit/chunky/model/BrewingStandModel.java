/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class BrewingStandModel extends QuadModel {
  //region quads
  private static final Quad[] baseQuads = new Quad[] {
      new Quad(
          new Vector3(7 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector4(7 / 16.0, 9 / 16.0, 7 / 16.0, 9 / 16.0)
      ),
      new Quad(
          new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector4(7 / 16.0, 9 / 16.0, 7 / 16.0, 9 / 16.0)
      ),
      new Quad(
          new Vector3(7 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector4(9 / 16.0, 7 / 16.0, 14 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(9 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector4(9 / 16.0, 7 / 16.0, 14 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(7 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector4(9 / 16.0, 7 / 16.0, 14 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(9 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector4(9 / 16.0, 7 / 16.0, 14 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(9 / 16.0, 2 / 16.0, 11 / 16.0),
          new Vector3(15 / 16.0, 2 / 16.0, 11 / 16.0),
          new Vector3(9 / 16.0, 2 / 16.0, 5 / 16.0),
          new Vector4(9 / 16.0, 15 / 16.0, 5 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(9 / 16.0, 0 / 16.0, 5 / 16.0),
          new Vector3(15 / 16.0, 0 / 16.0, 5 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 11 / 16.0),
          new Vector4(9 / 16.0, 15 / 16.0, 5 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(9 / 16.0, 2 / 16.0, 11 / 16.0),
          new Vector3(9 / 16.0, 2 / 16.0, 5 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 11 / 16.0),
          new Vector4(11 / 16.0, 5 / 16.0, 2 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(15 / 16.0, 2 / 16.0, 5 / 16.0),
          new Vector3(15 / 16.0, 2 / 16.0, 11 / 16.0),
          new Vector3(15 / 16.0, 0 / 16.0, 5 / 16.0),
          new Vector4(11 / 16.0, 5 / 16.0, 2 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(9 / 16.0, 2 / 16.0, 5 / 16.0),
          new Vector3(15 / 16.0, 2 / 16.0, 5 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 5 / 16.0),
          new Vector4(15 / 16.0, 9 / 16.0, 2 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(15 / 16.0, 2 / 16.0, 11 / 16.0),
          new Vector3(9 / 16.0, 2 / 16.0, 11 / 16.0),
          new Vector3(15 / 16.0, 0 / 16.0, 11 / 16.0),
          new Vector4(15 / 16.0, 9 / 16.0, 2 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 2 / 16.0, 7 / 16.0),
          new Vector3(8 / 16.0, 2 / 16.0, 7 / 16.0),
          new Vector3(2 / 16.0, 2 / 16.0, 1 / 16.0),
          new Vector4(2 / 16.0, 8 / 16.0, 9 / 16.0, 15 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 0 / 16.0, 1 / 16.0),
          new Vector3(8 / 16.0, 0 / 16.0, 1 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector4(2 / 16.0, 8 / 16.0, 9 / 16.0, 15 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 2 / 16.0, 7 / 16.0),
          new Vector3(2 / 16.0, 2 / 16.0, 1 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector4(7 / 16.0, 1 / 16.0, 2 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 2 / 16.0, 1 / 16.0),
          new Vector3(8 / 16.0, 2 / 16.0, 7 / 16.0),
          new Vector3(8 / 16.0, 0 / 16.0, 1 / 16.0),
          new Vector4(7 / 16.0, 1 / 16.0, 2 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 2 / 16.0, 1 / 16.0),
          new Vector3(8 / 16.0, 2 / 16.0, 1 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 1 / 16.0),
          new Vector4(8 / 16.0, 2 / 16.0, 2 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 2 / 16.0, 7 / 16.0),
          new Vector3(2 / 16.0, 2 / 16.0, 7 / 16.0),
          new Vector3(8 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector4(8 / 16.0, 2 / 16.0, 2 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 2 / 16.0, 15 / 16.0),
          new Vector3(8 / 16.0, 2 / 16.0, 15 / 16.0),
          new Vector3(2 / 16.0, 2 / 16.0, 9 / 16.0),
          new Vector4(2 / 16.0, 8 / 16.0, 1 / 16.0, 7 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector3(8 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 15 / 16.0),
          new Vector4(2 / 16.0, 8 / 16.0, 1 / 16.0, 7 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 2 / 16.0, 15 / 16.0),
          new Vector3(2 / 16.0, 2 / 16.0, 9 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 15 / 16.0),
          new Vector4(15 / 16.0, 9 / 16.0, 2 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 2 / 16.0, 9 / 16.0),
          new Vector3(8 / 16.0, 2 / 16.0, 15 / 16.0),
          new Vector3(8 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector4(15 / 16.0, 9 / 16.0, 2 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 2 / 16.0, 9 / 16.0),
          new Vector3(8 / 16.0, 2 / 16.0, 9 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector4(8 / 16.0, 2 / 16.0, 2 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 2 / 16.0, 15 / 16.0),
          new Vector3(2 / 16.0, 2 / 16.0, 15 / 16.0),
          new Vector3(8 / 16.0, 0 / 16.0, 15 / 16.0),
          new Vector4(8 / 16.0, 2 / 16.0, 2 / 16.0, 0 / 16.0)
      ),
  };

  private static final Quad[] noBottleQuads = {
      // east
      new Quad(new Vector3(9 / 16., 0, .5), new Vector3(1, 0, .5),
          new Vector3(.5, 1, .5), new Vector4(9 / 16., 1, 0, 1), true),

      // southwest 210
      new Quad(new Vector3(.46, 0, 9 / 16.), new Vector3(.25, 0, .933),
          new Vector3(.46, 1, 9 / 16.), new Vector4(9 / 16., 1, 0, 1), true),

      // northwest 330
      new Quad(new Vector3(.46, 0, 7 / 16.), new Vector3(.25, 0, .067),
          new Vector3(.46, 1, 7 / 16.), new Vector4(9 / 16., 1, 0, 1), true),
  };

  private static final Quad[] bottleQuads = {
      // east
      new Quad(new Vector3(9 / 16., 0, .5), new Vector3(1, 0, .5),
          new Vector3(.50, 1, 8 / 16.), new Vector4(7 / 16., 0, 0, 1), true),

      // southwest 210
      new Quad(new Vector3(.46, 0, 9 / 16.), new Vector3(.25, 0, .933),
          new Vector3(.46, 1, 9 / 16.), new Vector4(7 / 16., 0., 0, 1), true),

      // northwest 330
      new Quad(new Vector3(.46, 0, 7 / 16.), new Vector3(.25, 0, .067),
          new Vector3(.46, 1, 7 / 16.), new Vector4(7 / 16., 0, 0, 1), true),
  };
  //endregion

  private static final Texture base = Texture.brewingStandBase;
  private static final Texture stand = Texture.brewingStandSide;
  private static final Texture[] tex = new Texture[] {
      stand, stand, stand, stand, stand, stand, base, base, base, base, base, base, base, base, base, base, base,
      base, base, base, base, base, base, base, stand, stand, stand
  };

  private final Quad[] quads;

  public BrewingStandModel(boolean bottle0, boolean bottle1, boolean bottle2) {
    int len = baseQuads.length;
    quads = new Quad[len + 3];
    System.arraycopy(baseQuads, 0, quads, 0, len);
    quads[len + 0] = bottle0 ? bottleQuads[0] : noBottleQuads[0];
    quads[len + 1] = bottle1 ? bottleQuads[1] : noBottleQuads[1];
    quads[len + 2] = bottle2 ? bottleQuads[2] : noBottleQuads[2];
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return tex;
  }
}
