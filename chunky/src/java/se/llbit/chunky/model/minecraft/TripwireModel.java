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

import static se.llbit.chunky.world.BlockData.CONNECTED_EAST;
import static se.llbit.chunky.world.BlockData.CONNECTED_NORTH;
import static se.llbit.chunky.world.BlockData.CONNECTED_SOUTH;
import static se.llbit.chunky.world.BlockData.CONNECTED_WEST;

import java.util.Arrays;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class TripwireModel extends QuadModel {

  //region tripwireN
  private static final Quad[] tripwireN = {
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      )
  };
  //endregion

  //region tripwireNE
  private static final Quad[] tripwireNE = {
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
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
  //endregion

  //region tripwireNS
  private static final Quad[] tripwireNS = {
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 16 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 16 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      )
  };
  //endregion

  //region tripwireNSE
  private static final Quad[] tripwireNSE = {
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 16 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 16 / 16.0),
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
  //endregion

  //region tripwireNSEW
  private static final Quad[] tripwire_NSEW = {
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 4 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 16 / 16.0),
          new Vector3(8.25 / 16.0, 1.5 / 16.0, 12 / 16.0),
          new Vector3(7.75 / 16.0, 1.5 / 16.0, 16 / 16.0),
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
  //endregion

  private static final Quad[][] model = new Quad[16][];

  static {
    model[0] = tripwireNS; // If no side is elected, render north-south.
    model[CONNECTED_NORTH | CONNECTED_SOUTH | CONNECTED_EAST | CONNECTED_WEST] = tripwire_NSEW;
    model[CONNECTED_NORTH | CONNECTED_EAST | CONNECTED_SOUTH] = tripwireNSE;
    model[CONNECTED_EAST | CONNECTED_SOUTH | CONNECTED_WEST] = Model
        .rotateY(model[CONNECTED_NORTH | CONNECTED_EAST | CONNECTED_SOUTH]);
    model[CONNECTED_SOUTH | CONNECTED_WEST | CONNECTED_NORTH] = Model
        .rotateY(model[CONNECTED_EAST | CONNECTED_SOUTH | CONNECTED_WEST]);
    model[CONNECTED_WEST | CONNECTED_NORTH | CONNECTED_EAST] = Model
        .rotateY(model[CONNECTED_SOUTH | CONNECTED_WEST | CONNECTED_NORTH]);
    model[CONNECTED_NORTH | CONNECTED_SOUTH] = tripwireNS;
    model[CONNECTED_EAST | CONNECTED_WEST] = Model
        .rotateY(model[CONNECTED_NORTH | CONNECTED_SOUTH]);
    model[CONNECTED_NORTH | CONNECTED_EAST] = tripwireNE;
    model[CONNECTED_EAST | CONNECTED_SOUTH] = Model
        .rotateY(model[CONNECTED_NORTH | CONNECTED_EAST]);
    model[CONNECTED_SOUTH | CONNECTED_WEST] = Model
        .rotateY(model[CONNECTED_EAST | CONNECTED_SOUTH]);
    model[CONNECTED_WEST | CONNECTED_NORTH] = Model
        .rotateY(model[CONNECTED_SOUTH | CONNECTED_WEST]);
    model[CONNECTED_NORTH] = tripwireN;
    model[CONNECTED_EAST] = Model.rotateY(model[CONNECTED_NORTH]);
    model[CONNECTED_SOUTH] = Model.rotateY(model[CONNECTED_EAST]);
    model[CONNECTED_WEST] = Model.rotateY(model[CONNECTED_SOUTH]);
  }

  private final Quad[] quads;
  private final Texture[] textures;

  public TripwireModel(int connections) {
    quads = model[connections];
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
