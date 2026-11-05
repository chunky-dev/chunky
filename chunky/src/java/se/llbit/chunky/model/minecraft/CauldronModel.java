/*
 * Copyright (c) 2012-2023 Chunky contributors
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

import se.llbit.chunky.block.MinecraftBlock;
import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.block.minecraft.Lava;
import se.llbit.chunky.block.minecraft.Water;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Constants;
import se.llbit.math.IntersectionRecord;
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
              new Vector4(2 / 16.0, 14 / 16.0, 1 - 14 / 16.0, 1 - 2 / 16.0),
            true),
          new Quad(
              new Vector3(2 / 16.0, 12 / 16.0, 14 / 16.0),
              new Vector3(14 / 16.0, 12 / 16.0, 14 / 16.0),
              new Vector3(2 / 16.0, 12 / 16.0, 2 / 16.0),
              new Vector4(2 / 16.0, 14 / 16.0, 1 - 14 / 16.0, 1 - 2 / 16.0),
            true),
          new Quad(
              new Vector3(2 / 16.0, 15 / 16.0, 14 / 16.0),
              new Vector3(14 / 16.0, 15 / 16.0, 14 / 16.0),
              new Vector3(2 / 16.0, 15 / 16.0, 2 / 16.0),
              new Vector4(2 / 16.0, 14 / 16.0, 1 - 14 / 16.0, 1 - 2 / 16.0),
            true)
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

  public static boolean intersect(Ray ray, IntersectionRecord intersectionRecord, Scene scene, int level, Texture contentTexture, String materialName) {
    boolean hit = false;
    for (int i = 0; i < quads.length; ++i) {
      Quad quad = quads[i];
      if (quad.closestIntersection(ray, intersectionRecord)) {
        float[] color = tex[i].getColor(intersectionRecord.uv.x, intersectionRecord.uv.y);
        if (color[3] > Constants.EPSILON) {
          intersectionRecord.color.set(color);
          intersectionRecord.setNormal(quad.n);
          hit = true;
        }
      }
    }

    Quad water = waterLevels[level];
    if (water != null && water.closestIntersection(ray, intersectionRecord)) {
      hit = true;
      intersectionRecord.setNormal(water.n);
      if (ray.d.dot(water.n) > 0) {
        intersectionRecord.material = scene.waterPlaneMaterial(ray.o.rScaleAdd(intersectionRecord.distance, ray.d));
        intersectionRecord.material.getColor(intersectionRecord);
        intersectionRecord.n.scale(-1);
        intersectionRecord.shadeN.scale(-1);
      } else {
        contentTexture.getColor(intersectionRecord);
        MinecraftBlock mat = new MinecraftBlock(materialName, Texture.air);
        scene.getPalette().applyMaterial(mat);
        intersectionRecord.material = mat;
      }
    }
    return hit;
  }

  public static boolean intersectWithWater(Ray ray, IntersectionRecord intersectionRecord, Scene scene, int level) {
    boolean hit = false;
    for (int i = 0; i < quads.length; ++i) {
      Quad quad = quads[i];
      if (quad.closestIntersection(ray, intersectionRecord)) {
        float[] color = tex[i].getColor(intersectionRecord.uv.x, intersectionRecord.uv.y);
        if (color[3] > Constants.EPSILON) {
          intersectionRecord.color.set(color);
          intersectionRecord.setNormal(quad.n);
          hit = true;
        }
      }
    }

    Quad water = waterLevels[level];
    if (water != null && water.closestIntersection(ray, intersectionRecord)) {
      hit = true;
      intersectionRecord.setNormal(water.n);
      Ray testRay = new Ray(ray);
      testRay.o.scaleAdd(intersectionRecord.distance, testRay.d);
      Vector3 shadeNormal = scene.getCurrentWaterShader().doWaterShading(testRay, intersectionRecord, scene.getAnimationTime());
      intersectionRecord.shadeN.set(shadeNormal);
      if (ray.d.dot(water.n) > 0) {
        intersectionRecord.material = scene.waterPlaneMaterial(testRay.o);
        intersectionRecord.material.getColor(intersectionRecord);
        intersectionRecord.n.scale(-1);
        intersectionRecord.shadeN.scale(-1);
      } else {
        intersectionRecord.material = scene.getPalette().water;
        Water.INSTANCE.getColor(intersectionRecord);
      }
    }
    return hit;
  }

  public static boolean intersectWithLava(Ray ray, IntersectionRecord intersectionRecord, Scene scene) {
    boolean hit = false;
    for (int i = 0; i < quads.length; ++i) {
      Quad quad = quads[i];
      if (quad.closestIntersection(ray, intersectionRecord)) {
        float[] color = tex[i].getColor(intersectionRecord.uv.x, intersectionRecord.uv.y);
        if (color[3] > Constants.EPSILON) {
          intersectionRecord.color.set(color);
          intersectionRecord.setNormal(quad.n);
          hit = true;
        }
      }
    }

    Quad lava = waterLevels[3];
    if (lava.closestIntersection(ray, intersectionRecord)) {
      hit = true;
      intersectionRecord.setNormal(lava.n);
      if (ray.d.dot(lava.n) > 0) {
        intersectionRecord.material = scene.waterPlaneMaterial(ray.o.rScaleAdd(intersectionRecord.distance, ray.d));
        intersectionRecord.material.getColor(intersectionRecord);
        intersectionRecord.n.scale(-1);
        intersectionRecord.shadeN.scale(-1);
      } else {
        Texture.lava.getColor(intersectionRecord);
        // set the current material to lava so that only the lava is emissive and not the cauldron
        MinecraftBlock lavaMat = new Lava(7);
        scene.getPalette().applyMaterial(lavaMat);
        intersectionRecord.material = lavaMat;
      }
    }
    return hit;
  }
}
