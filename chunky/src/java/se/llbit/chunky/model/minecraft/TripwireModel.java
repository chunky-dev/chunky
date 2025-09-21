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
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class TripwireModel extends QuadModel {
  private static final Quad[] tripwireN = new Quad[]{
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
    )
  };

  private static final Quad[] tripwireNE = new Quad[]{
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(16 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(16 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
    )
  };

  private static final Quad[] tripwireNS = new Quad[]{
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 16 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 16 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
    )
  };

  private static final Quad[] tripwireNSE = new Quad[]{
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 16 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 16 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(16 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(16 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
    )
  };

  private static final Quad[] tripwireNSEW = new Quad[]{
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 16 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 16 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(4 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(0 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(4 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(0 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(4 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(4 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(16 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(16 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)
    )
  };

  private static final Quad[] tripwireAttachedNS = new Quad[]{
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 16 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 16 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 14 / 16.0)
    )
  };

  private static final Quad[] tripwireAttachedN = new Quad[]{
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 14 / 16.0)
    )
  };

  private static final Quad[] tripwireAttachedNE = new Quad[]{
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 12 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(16 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(16 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 12 / 16.0)
    )
  };

  private static final Quad[] tripwireAttachedNSE = new Quad[]{
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 16 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 16 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 12 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(16 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(16 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 12 / 16.0)
    )
  };

  private static final Quad[] tripwireAttachedNSEW = new Quad[]{
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 16 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector3(8.25 / 16.0, 1.5 / 16.0, 16 / 16.0),
      new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(4 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(0 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(4 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(0 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 12 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(4 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(4 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 12 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(8 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 12 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(16 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(16 / 16.0, 1.5 / 16.0, 7.75 / 16.0),
      new Vector3(12 / 16.0, 1.5 / 16.0, 8.25 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 12 / 16.0)
    )
  };

  private final Quad[] quads;
  private final Texture[] textures;

  public TripwireModel(boolean attached, boolean north, boolean south, boolean east, boolean west) {
    int nsew = (north ? 1 : 0) << 3 | (south ? 1 : 0) << 2 | (east ? 1 : 0) << 1 | (west ? 1 : 0);
    if (attached) {
      quads = switch (nsew) {
        case 0b0000 -> tripwireAttachedNS;
        case 0b0001 -> Model.rotateNegY(tripwireAttachedN);
        case 0b0010 -> Model.rotateY(tripwireAttachedN);
        case 0b0011 -> Model.rotateY(tripwireAttachedNS);
        case 0b0100 -> Model.rotateY(tripwireAttachedN, Math.toRadians(180));
        case 0b0101 -> Model.rotateY(tripwireAttachedNE, Math.toRadians(180));
        case 0b0110 -> Model.rotateY(tripwireAttachedNE);
        case 0b0111 -> Model.rotateY(tripwireAttachedNSE);
        case 0b1000 -> tripwireAttachedN;
        case 0b1001 -> Model.rotateNegY(tripwireAttachedNE);
        case 0b1010 -> tripwireAttachedNE;
        case 0b1011 -> Model.rotateNegY(tripwireAttachedNSE);
        case 0b1100 -> tripwireAttachedNS;
        case 0b1101 -> Model.rotateY(tripwireAttachedNSE, Math.toRadians(180));
        case 0b1110 -> tripwireAttachedNSE;
        case 0b1111 -> tripwireAttachedNSEW;
        default -> tripwireAttachedN;
      };
    } else {
      quads = switch (nsew) {
        case 0b0000 -> tripwireNS;
        case 0b0001 -> Model.rotateNegY(tripwireN);
        case 0b0010 -> Model.rotateY(tripwireN);
        case 0b0011 -> Model.rotateY(tripwireNS);
        case 0b0100 -> Model.rotateY(tripwireN, Math.toRadians(180));
        case 0b0101 -> Model.rotateY(tripwireNE, Math.toRadians(180));
        case 0b0110 -> Model.rotateY(tripwireNE);
        case 0b0111 -> Model.rotateY(tripwireNSE);
        case 0b1000 -> tripwireN;
        case 0b1001 -> Model.rotateNegY(tripwireNE);
        case 0b1010 -> tripwireNE;
        case 0b1011 -> Model.rotateNegY(tripwireNSE);
        case 0b1100 -> tripwireNS;
        case 0b1101 -> Model.rotateY(tripwireNSE, Math.toRadians(180));
        case 0b1110 -> tripwireNSE;
        case 0b1111 -> tripwireNSEW;
        default -> tripwireN;
      };
    }
    textures = new Texture[quads.length];
    Arrays.fill(textures, Texture.tripwire);
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }
}
