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

import se.llbit.chunky.block.Lava;
import se.llbit.chunky.block.Water;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * A cauldron model.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class CauldronModel {

  private static final Quad[] quads =
      new Quad[]{
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 2 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)),
          new Quad(
              new Vector3(0 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector3(2 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 3 / 16.0, 16 / 16.0),
              new Vector4(0 / 16.0, 2 / 16.0, 16 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 3 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 3 / 16.0)),
          new Quad(
              new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(2 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 3 / 16.0)),
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector4(1 - 2 / 16.0, 1 - 0 / 16.0, 16 / 16.0, 3 / 16.0)),
          new Quad(
              new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(2 / 16.0, 3 / 16.0, 16 / 16.0),
              new Vector4(2 / 16.0, 0 / 16.0, 16 / 16.0, 3 / 16.0)),
          new Quad(
              new Vector3(2 / 16.0, 4 / 16.0, 14 / 16.0),
              new Vector3(14 / 16.0, 4 / 16.0, 14 / 16.0),
              new Vector3(2 / 16.0, 4 / 16.0, 2 / 16.0),
              new Vector4(2 / 16.0, 14 / 16.0, 1 - 14 / 16.0, 1 - 2 / 16.0)),
          new Quad(
              new Vector3(2 / 16.0, 3 / 16.0, 2 / 16.0),
              new Vector3(14 / 16.0, 3 / 16.0, 2 / 16.0),
              new Vector3(2 / 16.0, 3 / 16.0, 14 / 16.0),
              new Vector4(2 / 16.0, 14 / 16.0, 14 / 16.0, 2 / 16.0)),
          new Quad(
              new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector4(14 / 16.0, 16 / 16.0, 1 - 16 / 16.0, 1 - 0 / 16.0)),
          new Quad(
              new Vector3(14 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector3(14 / 16.0, 3 / 16.0, 16 / 16.0),
              new Vector4(14 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(14 / 16.0, 3 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 3 / 16.0)),
          new Quad(
              new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 3 / 16.0)),
          new Quad(
              new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(14 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector4(1 - 16 / 16.0, 1 - 14 / 16.0, 16 / 16.0, 3 / 16.0)),
          new Quad(
              new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 3 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 14 / 16.0, 16 / 16.0, 3 / 16.0)),
          new Quad(
              new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
              new Vector3(14 / 16.0, 16 / 16.0, 2 / 16.0),
              new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector4(2 / 16.0, 14 / 16.0, 1 - 2 / 16.0, 1 - 0 / 16.0)),
          new Quad(
              new Vector3(2 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector3(14 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector3(2 / 16.0, 3 / 16.0, 2 / 16.0),
              new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(2 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(14 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(2 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector4(1 - 14 / 16.0, 1 - 2 / 16.0, 16 / 16.0, 3 / 16.0)),
          new Quad(
              new Vector3(14 / 16.0, 16 / 16.0, 2 / 16.0),
              new Vector3(2 / 16.0, 16 / 16.0, 2 / 16.0),
              new Vector3(14 / 16.0, 3 / 16.0, 2 / 16.0),
              new Vector4(14 / 16.0, 2 / 16.0, 16 / 16.0, 3 / 16.0)),
          new Quad(
              new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(2 / 16.0, 16 / 16.0, 14 / 16.0),
              new Vector4(2 / 16.0, 14 / 16.0, 1 - 16 / 16.0, 1 - 14 / 16.0)),
          new Quad(
              new Vector3(2 / 16.0, 3 / 16.0, 14 / 16.0),
              new Vector3(14 / 16.0, 3 / 16.0, 14 / 16.0),
              new Vector3(2 / 16.0, 3 / 16.0, 16 / 16.0),
              new Vector4(2 / 16.0, 14 / 16.0, 16 / 16.0, 14 / 16.0)),
          new Quad(
              new Vector3(2 / 16.0, 16 / 16.0, 14 / 16.0),
              new Vector3(14 / 16.0, 16 / 16.0, 14 / 16.0),
              new Vector3(2 / 16.0, 3 / 16.0, 14 / 16.0),
              new Vector4(1 - 14 / 16.0, 1 - 2 / 16.0, 16 / 16.0, 3 / 16.0)),
          new Quad(
              new Vector3(14 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(2 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(14 / 16.0, 3 / 16.0, 16 / 16.0),
              new Vector4(14 / 16.0, 2 / 16.0, 16 / 16.0, 3 / 16.0)),
          new Quad(
              new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
              new Vector3(4 / 16.0, 0 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 0 / 16.0, 2 / 16.0),
              new Vector4(0 / 16.0, 4 / 16.0, 2 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(0 / 16.0, 3 / 16.0, 2 / 16.0),
              new Vector3(0 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 0 / 16.0, 2 / 16.0),
              new Vector4(2 / 16.0, 0 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(4 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector3(4 / 16.0, 3 / 16.0, 2 / 16.0),
              new Vector3(4 / 16.0, 0 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 2 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(0 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector3(4 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
              new Vector4(1 - 4 / 16.0, 1 - 0 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(4 / 16.0, 3 / 16.0, 2 / 16.0),
              new Vector3(0 / 16.0, 3 / 16.0, 2 / 16.0),
              new Vector3(4 / 16.0, 0 / 16.0, 2 / 16.0),
              new Vector4(4 / 16.0, 0 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(0 / 16.0, 0 / 16.0, 2 / 16.0),
              new Vector3(2 / 16.0, 0 / 16.0, 2 / 16.0),
              new Vector3(0 / 16.0, 0 / 16.0, 4 / 16.0),
              new Vector4(0 / 16.0, 2 / 16.0, 4 / 16.0, 2 / 16.0)),
          new Quad(
              new Vector3(0 / 16.0, 3 / 16.0, 4 / 16.0),
              new Vector3(0 / 16.0, 3 / 16.0, 2 / 16.0),
              new Vector3(0 / 16.0, 0 / 16.0, 4 / 16.0),
              new Vector4(4 / 16.0, 2 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(2 / 16.0, 3 / 16.0, 2 / 16.0),
              new Vector3(2 / 16.0, 3 / 16.0, 4 / 16.0),
              new Vector3(2 / 16.0, 0 / 16.0, 2 / 16.0),
              new Vector4(2 / 16.0, 4 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(2 / 16.0, 3 / 16.0, 4 / 16.0),
              new Vector3(0 / 16.0, 3 / 16.0, 4 / 16.0),
              new Vector3(2 / 16.0, 0 / 16.0, 4 / 16.0),
              new Vector4(2 / 16.0, 0 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(12 / 16.0, 0 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
              new Vector3(12 / 16.0, 0 / 16.0, 2 / 16.0),
              new Vector4(12 / 16.0, 16 / 16.0, 2 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(12 / 16.0, 3 / 16.0, 2 / 16.0),
              new Vector3(12 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector3(12 / 16.0, 0 / 16.0, 2 / 16.0),
              new Vector4(2 / 16.0, 0 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(16 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 3 / 16.0, 2 / 16.0),
              new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 2 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(12 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 3 / 16.0, 0 / 16.0),
              new Vector3(12 / 16.0, 0 / 16.0, 0 / 16.0),
              new Vector4(1 - 16 / 16.0, 1 - 12 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(16 / 16.0, 3 / 16.0, 2 / 16.0),
              new Vector3(12 / 16.0, 3 / 16.0, 2 / 16.0),
              new Vector3(16 / 16.0, 0 / 16.0, 2 / 16.0),
              new Vector4(16 / 16.0, 12 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(14 / 16.0, 0 / 16.0, 2 / 16.0),
              new Vector3(16 / 16.0, 0 / 16.0, 2 / 16.0),
              new Vector3(14 / 16.0, 0 / 16.0, 4 / 16.0),
              new Vector4(14 / 16.0, 16 / 16.0, 4 / 16.0, 2 / 16.0)),
          new Quad(
              new Vector3(14 / 16.0, 3 / 16.0, 4 / 16.0),
              new Vector3(14 / 16.0, 3 / 16.0, 2 / 16.0),
              new Vector3(14 / 16.0, 0 / 16.0, 4 / 16.0),
              new Vector4(4 / 16.0, 2 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(16 / 16.0, 3 / 16.0, 2 / 16.0),
              new Vector3(16 / 16.0, 3 / 16.0, 4 / 16.0),
              new Vector3(16 / 16.0, 0 / 16.0, 2 / 16.0),
              new Vector4(2 / 16.0, 4 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(16 / 16.0, 3 / 16.0, 4 / 16.0),
              new Vector3(14 / 16.0, 3 / 16.0, 4 / 16.0),
              new Vector3(16 / 16.0, 0 / 16.0, 4 / 16.0),
              new Vector4(16 / 16.0, 14 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(0 / 16.0, 0 / 16.0, 14 / 16.0),
              new Vector3(4 / 16.0, 0 / 16.0, 14 / 16.0),
              new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
              new Vector4(0 / 16.0, 4 / 16.0, 16 / 16.0, 14 / 16.0)),
          new Quad(
              new Vector3(0 / 16.0, 3 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 3 / 16.0, 14 / 16.0),
              new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 14 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(4 / 16.0, 3 / 16.0, 14 / 16.0),
              new Vector3(4 / 16.0, 3 / 16.0, 16 / 16.0),
              new Vector3(4 / 16.0, 0 / 16.0, 14 / 16.0),
              new Vector4(14 / 16.0, 16 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(0 / 16.0, 3 / 16.0, 14 / 16.0),
              new Vector3(4 / 16.0, 3 / 16.0, 14 / 16.0),
              new Vector3(0 / 16.0, 0 / 16.0, 14 / 16.0),
              new Vector4(1 - 4 / 16.0, 1 - 0 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(4 / 16.0, 3 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 3 / 16.0, 16 / 16.0),
              new Vector3(4 / 16.0, 0 / 16.0, 16 / 16.0),
              new Vector4(4 / 16.0, 0 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(0 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector3(2 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector3(0 / 16.0, 0 / 16.0, 14 / 16.0),
              new Vector4(0 / 16.0, 2 / 16.0, 14 / 16.0, 12 / 16.0)),
          new Quad(
              new Vector3(0 / 16.0, 3 / 16.0, 14 / 16.0),
              new Vector3(0 / 16.0, 3 / 16.0, 12 / 16.0),
              new Vector3(0 / 16.0, 0 / 16.0, 14 / 16.0),
              new Vector4(14 / 16.0, 12 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(2 / 16.0, 3 / 16.0, 12 / 16.0),
              new Vector3(2 / 16.0, 3 / 16.0, 14 / 16.0),
              new Vector3(2 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(12 / 16.0, 14 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(0 / 16.0, 3 / 16.0, 12 / 16.0),
              new Vector3(2 / 16.0, 3 / 16.0, 12 / 16.0),
              new Vector3(0 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(1 - 2 / 16.0, 1 - 0 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(12 / 16.0, 0 / 16.0, 14 / 16.0),
              new Vector3(16 / 16.0, 0 / 16.0, 14 / 16.0),
              new Vector3(12 / 16.0, 0 / 16.0, 16 / 16.0),
              new Vector4(12 / 16.0, 16 / 16.0, 16 / 16.0, 14 / 16.0)),
          new Quad(
              new Vector3(12 / 16.0, 3 / 16.0, 16 / 16.0),
              new Vector3(12 / 16.0, 3 / 16.0, 14 / 16.0),
              new Vector3(12 / 16.0, 0 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 14 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(16 / 16.0, 3 / 16.0, 14 / 16.0),
              new Vector3(16 / 16.0, 3 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 0 / 16.0, 14 / 16.0),
              new Vector4(14 / 16.0, 16 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(12 / 16.0, 3 / 16.0, 14 / 16.0),
              new Vector3(16 / 16.0, 3 / 16.0, 14 / 16.0),
              new Vector3(12 / 16.0, 0 / 16.0, 14 / 16.0),
              new Vector4(1 - 16 / 16.0, 1 - 12 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(16 / 16.0, 3 / 16.0, 16 / 16.0),
              new Vector3(12 / 16.0, 3 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 12 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(14 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector3(16 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector3(14 / 16.0, 0 / 16.0, 14 / 16.0),
              new Vector4(14 / 16.0, 16 / 16.0, 14 / 16.0, 12 / 16.0)),
          new Quad(
              new Vector3(14 / 16.0, 3 / 16.0, 14 / 16.0),
              new Vector3(14 / 16.0, 3 / 16.0, 12 / 16.0),
              new Vector3(14 / 16.0, 0 / 16.0, 14 / 16.0),
              new Vector4(14 / 16.0, 12 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(16 / 16.0, 3 / 16.0, 12 / 16.0),
              new Vector3(16 / 16.0, 3 / 16.0, 14 / 16.0),
              new Vector3(16 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(12 / 16.0, 14 / 16.0, 3 / 16.0, 0 / 16.0)),
          new Quad(
              new Vector3(14 / 16.0, 3 / 16.0, 12 / 16.0),
              new Vector3(16 / 16.0, 3 / 16.0, 12 / 16.0),
              new Vector3(14 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(1 - 16 / 16.0, 1 - 14 / 16.0, 3 / 16.0, 0 / 16.0))
      };

  private static final Quad[] waterLevels =
      new Quad[]{
          null, // level 0 is empty
          new Quad(
              new Vector3(2 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(14 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(2 / 16.0, 9 / 16.0, 2 / 16.0),
              new Vector4(2 / 16.0, 14 / 16.0, 1 - 14 / 16.0, 1 - 2 / 16.0)),
          new Quad(
              new Vector3(2 / 16.0, 12 / 16.0, 14 / 16.0),
              new Vector3(14 / 16.0, 12 / 16.0, 14 / 16.0),
              new Vector3(2 / 16.0, 12 / 16.0, 2 / 16.0),
              new Vector4(2 / 16.0, 14 / 16.0, 1 - 14 / 16.0, 1 - 2 / 16.0)),
          new Quad(
              new Vector3(2 / 16.0, 15 / 16.0, 14 / 16.0),
              new Vector3(14 / 16.0, 15 / 16.0, 14 / 16.0),
              new Vector3(2 / 16.0, 15 / 16.0, 2 / 16.0),
              new Vector4(2 / 16.0, 14 / 16.0, 1 - 14 / 16.0, 1 - 2 / 16.0))
      };

  private static final Texture top = Texture.cauldronTop;
  private static final Texture bottom = Texture.cauldronBottom;
  private static final Texture side = Texture.cauldronSide;
  private static final Texture inside = Texture.cauldronInside;
  private static final Texture[] tex =
      new Texture[]{
          top, inside, side, side, side, side, inside, inside, top, inside, side, side, side, side,
          top, inside, side, side, top, inside, side, side, bottom, side, side, side, side, bottom,
          side, side, side, bottom, side, side, side, side, bottom, side, side, side, bottom, side,
          side, side, side, bottom, side, side, side, bottom, side, side, side, side, bottom, side,
          side, side
      };

  public static boolean intersect(Ray ray, boolean stillWater, int level) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (int i = 0; i < quads.length; ++i) {
      Quad quad = quads[i];
      if (quad.intersect(ray)) {
        float[] color = tex[i].getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.n.set(quad.n);
          hit = true;
        }
      }
    }

    // TODO since this water is the same block, refraction is not taken into account – still better than no water
    Quad water = waterLevels[level];
    if (water != null && water.intersect(ray)) {
      if (!stillWater) {
        WaterModel.doWaterDisplacement(ray);
      } else {
        ray.n.set(water.n);
      }
      ray.setPrevMaterial(ray.getCurrentMaterial(), ray.getCurrentData());
      ray.setCurrentMaterial(Water.INSTANCE);
      ray.t = ray.tNext;
    }
    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }

  public static boolean intersectWithLava(Ray ray) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (int i = 0; i < quads.length; ++i) {
      Quad quad = quads[i];
      if (quad.intersect(ray)) {
        float[] color = tex[i].getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.n.set(quad.n);
          hit = true;
        }
      }
    }
    Quad lava = waterLevels[3];
    if (lava.intersect(ray)) {
      float[] color = Texture.lava.getColor(ray.u, ray.v);
      if (color[3] > Ray.EPSILON) {
        ray.color.set(color);
        ray.t = ray.tNext;
        ray.n.set(lava.n);
        hit = true;

        // set the current material to lava so that only the lava is emissive and not the cauldron
        ray.setPrevMaterial(ray.getCurrentMaterial(), ray.getCurrentData());
        ray.setCurrentMaterial(new Lava(7));
      }
    }
    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }
}
