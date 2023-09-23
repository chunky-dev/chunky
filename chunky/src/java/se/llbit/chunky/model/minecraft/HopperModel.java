/*
 * Copyright (c) 2013-2023 Chunky contributors
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
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * Hopper block
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class HopperModel extends QuadModel {
  private static final Texture top = Texture.hopperTop;
  private static final Texture side = Texture.hopperOutside;
  private static final Texture inside = Texture.hopperInside;
  private static final Texture[] tex = new Texture[]{
      inside, side, side, side, side, side, top, side, side, side, side, top, side, side, side, side, top, side, side,
      top, side, side, side, side, side, side, side, side, side, side, side, side
  };

  //region quads
  private static final Quad[] quadsDown = new Quad[]{
      new Quad(
          new Vector3(0 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 10 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 10 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 11 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector4(1 - 16 / 16.0, 1 - 0 / 16.0, 11 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 10 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector4(1 - 2 / 16.0, 1 - 0 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector4(2 / 16.0, 0 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector4(14 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(14 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(14 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector4(1 - 16 / 16.0, 1 - 14 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 14 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 1 - 2 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(2 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector4(1 - 14 / 16.0, 1 - 2 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 11 / 16.0, 2 / 16.0),
          new Vector4(14 / 16.0, 2 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 1 - 16 / 16.0, 1 - 14 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(2 / 16.0, 11 / 16.0, 14 / 16.0),
          new Vector4(1 - 14 / 16.0, 1 - 2 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector4(14 / 16.0, 2 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(4 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector3(4 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector4(4 / 16.0, 12 / 16.0, 12 / 16.0, 4 / 16.0)
      ),
      new Quad(
          new Vector3(4 / 16.0, 10 / 16.0, 12 / 16.0),
          new Vector3(4 / 16.0, 10 / 16.0, 4 / 16.0),
          new Vector3(4 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector4(12 / 16.0, 4 / 16.0, 10 / 16.0, 4 / 16.0)
      ),
      new Quad(
          new Vector3(12 / 16.0, 10 / 16.0, 4 / 16.0),
          new Vector3(12 / 16.0, 10 / 16.0, 12 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector4(4 / 16.0, 12 / 16.0, 10 / 16.0, 4 / 16.0)
      ),
      new Quad(
          new Vector3(4 / 16.0, 10 / 16.0, 4 / 16.0),
          new Vector3(12 / 16.0, 10 / 16.0, 4 / 16.0),
          new Vector3(4 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector4(1 - 12 / 16.0, 1 - 4 / 16.0, 10 / 16.0, 4 / 16.0)
      ),
      new Quad(
          new Vector3(12 / 16.0, 10 / 16.0, 12 / 16.0),
          new Vector3(4 / 16.0, 10 / 16.0, 12 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector4(12 / 16.0, 4 / 16.0, 10 / 16.0, 4 / 16.0)
      ),
      new Quad(
          new Vector3(6 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector3(10 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector3(6 / 16.0, 0 / 16.0, 10 / 16.0),
          new Vector4(6 / 16.0, 10 / 16.0, 10 / 16.0, 6 / 16.0)
      ),
      new Quad(
          new Vector3(6 / 16.0, 4 / 16.0, 10 / 16.0),
          new Vector3(6 / 16.0, 4 / 16.0, 6 / 16.0),
          new Vector3(6 / 16.0, 0 / 16.0, 10 / 16.0),
          new Vector4(10 / 16.0, 6 / 16.0, 4 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(10 / 16.0, 4 / 16.0, 6 / 16.0),
          new Vector3(10 / 16.0, 4 / 16.0, 10 / 16.0),
          new Vector3(10 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector4(6 / 16.0, 10 / 16.0, 4 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(6 / 16.0, 4 / 16.0, 6 / 16.0),
          new Vector3(10 / 16.0, 4 / 16.0, 6 / 16.0),
          new Vector3(6 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector4(1 - 10 / 16.0, 1 - 6 / 16.0, 4 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(10 / 16.0, 4 / 16.0, 10 / 16.0),
          new Vector3(6 / 16.0, 4 / 16.0, 10 / 16.0),
          new Vector3(10 / 16.0, 0 / 16.0, 10 / 16.0),
          new Vector4(10 / 16.0, 6 / 16.0, 4 / 16.0, 0 / 16.0)
      )
  };

  private static final Quad[] quadsSide = new Quad[]{
      new Quad(
          new Vector3(0 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 10 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 10 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 11 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector4(1 - 16 / 16.0, 1 - 0 / 16.0, 11 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 10 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 11 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(0 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector4(1 - 2 / 16.0, 1 - 0 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector4(2 / 16.0, 0 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector4(14 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(14 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(14 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector4(1 - 16 / 16.0, 1 - 14 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 14 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 1 - 2 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(2 / 16.0, 11 / 16.0, 0 / 16.0),
          new Vector4(1 - 14 / 16.0, 1 - 2 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
          new Vector3(14 / 16.0, 11 / 16.0, 2 / 16.0),
          new Vector4(14 / 16.0, 2 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 1 - 16 / 16.0, 1 - 14 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 14 / 16.0),
          new Vector3(2 / 16.0, 11 / 16.0, 14 / 16.0),
          new Vector4(1 - 14 / 16.0, 1 - 2 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(14 / 16.0, 11 / 16.0, 16 / 16.0),
          new Vector4(14 / 16.0, 2 / 16.0, 16 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(4 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector3(4 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector4(4 / 16.0, 12 / 16.0, 12 / 16.0, 4 / 16.0)
      ),
      new Quad(
          new Vector3(4 / 16.0, 10 / 16.0, 12 / 16.0),
          new Vector3(4 / 16.0, 10 / 16.0, 4 / 16.0),
          new Vector3(4 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector4(12 / 16.0, 4 / 16.0, 10 / 16.0, 4 / 16.0)
      ),
      new Quad(
          new Vector3(12 / 16.0, 10 / 16.0, 4 / 16.0),
          new Vector3(12 / 16.0, 10 / 16.0, 12 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector4(4 / 16.0, 12 / 16.0, 10 / 16.0, 4 / 16.0)
      ),
      new Quad(
          new Vector3(4 / 16.0, 10 / 16.0, 4 / 16.0),
          new Vector3(12 / 16.0, 10 / 16.0, 4 / 16.0),
          new Vector3(4 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector4(1 - 12 / 16.0, 1 - 4 / 16.0, 10 / 16.0, 4 / 16.0)
      ),
      new Quad(
          new Vector3(12 / 16.0, 10 / 16.0, 12 / 16.0),
          new Vector3(4 / 16.0, 10 / 16.0, 12 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector4(12 / 16.0, 4 / 16.0, 10 / 16.0, 4 / 16.0)
      ),
      new Quad(
          new Vector3(6 / 16.0, 8 / 16.0, 4 / 16.0),
          new Vector3(10 / 16.0, 8 / 16.0, 4 / 16.0),
          new Vector3(6 / 16.0, 8 / 16.0, 0 / 16.0),
          new Vector4(6 / 16.0, 10 / 16.0, 1 - 4 / 16.0, 1 - 0 / 16.0)
      ),
      new Quad(
          new Vector3(6 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector3(10 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector3(6 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector4(6 / 16.0, 10 / 16.0, 4 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(6 / 16.0, 8 / 16.0, 4 / 16.0),
          new Vector3(6 / 16.0, 8 / 16.0, 0 / 16.0),
          new Vector3(6 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 8 / 16.0, 4 / 16.0)
      ),
      new Quad(
          new Vector3(10 / 16.0, 8 / 16.0, 0 / 16.0),
          new Vector3(10 / 16.0, 8 / 16.0, 4 / 16.0),
          new Vector3(10 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 4 / 16.0, 8 / 16.0, 4 / 16.0)
      ),
      new Quad(
          new Vector3(6 / 16.0, 8 / 16.0, 0 / 16.0),
          new Vector3(10 / 16.0, 8 / 16.0, 0 / 16.0),
          new Vector3(6 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector4(1 - 10 / 16.0, 1 - 6 / 16.0, 8 / 16.0, 4 / 16.0)
      )
  };
  //endregion

  private final Quad[] quads;

  public HopperModel(String facing) {
    switch (facing) {
      case "down":
        quads = quadsDown;
        break;
      default:
      case "north":
        quads = quadsSide;
        break;
      case "east":
        quads = Model.rotateY(quadsSide);
        break;
      case "south":
        quads = Model.rotateY(quadsSide, Math.toRadians(180));
        break;
      case "west":
        quads = Model.rotateNegY(quadsSide);
        break;
    }
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
