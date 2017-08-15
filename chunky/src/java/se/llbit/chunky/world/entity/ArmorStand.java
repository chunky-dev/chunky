/*
 * Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world.entity;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;

import java.util.Collection;
import java.util.LinkedList;

public class ArmorStand extends Entity {

  private static final Quad[] base = {
      // cube1
      new Quad(
          new Vector3(2 / 16.0, 1 / 16.0, 14 / 16.0),
          new Vector3(14 / 16.0, 1 / 16.0, 14 / 16.0),
          new Vector3(2 / 16.0, 1 / 16.0, 2 / 16.0),
          new Vector4(12 / 64.0, 24 / 64.0, 20 / 64.0, 32 / 64.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(14 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 14 / 16.0),
          new Vector4(36 / 64.0, 24 / 64.0, 32 / 64.0, 20 / 64.0)),
      new Quad(
          new Vector3(14 / 16.0, 0, 14 / 16.0),
          new Vector3(14 / 16.0, 0, 2 / 16.0),
          new Vector3(14 / 16.0, 1 / 16.0, 14 / 16.0),
          new Vector4(24 / 64.0, 36 / 64.0, 19 / 64.0, 20 / 64.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 14 / 16.0),
          new Vector3(2 / 16.0, 1 / 16.0, 2 / 16.0),
          new Vector4(0, 12 / 64.0, 19 / 64.0, 20 / 64.0)),
      new Quad(
          new Vector3(14 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(14 / 16.0, 1 / 16.0, 2 / 16.0),
          new Vector4(36 / 64.0, 48 / 64.0, 19 / 64.0, 20 / 64.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 14 / 16.0),
          new Vector3(14 / 16.0, 0, 14 / 16.0),
          new Vector3(2 / 16.0, 1 / 16.0, 14 / 16.0),
          new Vector4(12 / 64.0, 24 / 64.0, 19 / 64.0, 20 / 64.0)),
  };

  private static final Quad[] quads = {
      // cube2
      new Quad(
          new Vector3(2 / 16.0, 24 / 16.0, 9.5 / 16.0),
          new Vector3(14 / 16.0, 24 / 16.0, 9.5 / 16.0),
          new Vector3(2 / 16.0, 24 / 16.0, 6.5 / 16.0),
          new Vector4(3 / 64.0, 15 / 64.0, 17.5 / 32.0, 19 / 32.0)),
      new Quad(
          new Vector3(2 / 16.0, 21 / 16.0, 6.5 / 16.0),
          new Vector3(14 / 16.0, 21 / 16.0, 6.5 / 16.0),
          new Vector3(2 / 16.0, 21 / 16.0, 9.5 / 16.0),
          new Vector4(15 / 64.0, 27 / 64.0, 19 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(14 / 16.0, 21 / 16.0, 9.5 / 16.0),
          new Vector3(14 / 16.0, 21 / 16.0, 6.5 / 16.0),
          new Vector3(14 / 16.0, 24 / 16.0, 9.5 / 16.0),
          new Vector4(15 / 64.0, 18 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(2 / 16.0, 21 / 16.0, 6.5 / 16.0),
          new Vector3(2 / 16.0, 21 / 16.0, 9.5 / 16.0),
          new Vector3(2 / 16.0, 24 / 16.0, 6.5 / 16.0),
          new Vector4(0, 3 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(14 / 16.0, 21 / 16.0, 6.5 / 16.0),
          new Vector3(2 / 16.0, 21 / 16.0, 6.5 / 16.0),
          new Vector3(14 / 16.0, 24 / 16.0, 6.5 / 16.0),
          new Vector4(18 / 64.0, 30 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(2 / 16.0, 21 / 16.0, 9.5 / 16.0),
          new Vector3(14 / 16.0, 21 / 16.0, 9.5 / 16.0),
          new Vector3(2 / 16.0, 24 / 16.0, 9.5 / 16.0),
          new Vector4(3 / 64.0, 15 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      // cube3
      new Quad(
          new Vector3(5 / 16.0, 21 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 21 / 16.0, 9 / 16.0),
          new Vector3(5 / 16.0, 21 / 16.0, 7 / 16.0),
          new Vector4(18 / 64.0, 20 / 64.0, 31 / 32.0, 32 / 32.0)),
      new Quad(
          new Vector3(5 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(5 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector4(20 / 64.0, 22 / 64.0, 31 / 32.0, 32 / 32.0)),
      new Quad(
          new Vector3(7 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 21 / 16.0, 9 / 16.0),
          new Vector4(20 / 64.0, 22 / 64.0, 27.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(5 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(5 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(5 / 16.0, 21 / 16.0, 7 / 16.0),
          new Vector4(16 / 64.0, 18 / 64.0, 27.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(7 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(5 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 21 / 16.0, 7 / 16.0),
          new Vector4(22 / 64.0, 24 / 64.0, 27.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(5 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(5 / 16.0, 21 / 16.0, 9 / 16.0),
          new Vector4(18 / 64.0, 20 / 64.0, 27.5 / 32.0, 31 / 32.0)),
      // cube4
      new Quad(
          new Vector3(7 / 16.0, 30 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 30 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 30 / 16.0, 7 / 16.0),
          new Vector4(2 / 64.0, 4 / 64.0, 31 / 32.0, 32 / 32.0)),
      new Quad(
          new Vector3(7 / 16.0, 24 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 24 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 24 / 16.0, 9 / 16.0),
          new Vector4(4 / 64.0, 6 / 64.0, 31 / 32.0, 32 / 32.0)),
      new Quad(
          new Vector3(9 / 16.0, 24 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 24 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 30 / 16.0, 9 / 16.0),
          new Vector4(0, 2 / 64.0, 27.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(7 / 16.0, 24 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 24 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 30 / 16.0, 7 / 16.0),
          new Vector4(4 / 64.0, 6 / 64.0, 27.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(9 / 16.0, 24 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 24 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 30 / 16.0, 7 / 16.0),
          new Vector4(2 / 64.0, 4 / 64.0, 27.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(7 / 16.0, 24 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 24 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 30 / 16.0, 9 / 16.0),
          new Vector4(6 / 64.0, 8 / 64.0, 27.5 / 32.0, 31 / 32.0)),
      // cube5
      new Quad(
          new Vector3(4 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(12 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(4 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector4(18 / 64.0, 30 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(4 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(12 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(4 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector4(18 / 64.0, 30 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(12 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(12 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(12 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector4(0, 3 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(4 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(4 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(4 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector4(15 / 64.0, 18 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(12 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(4 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(12 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector4(18 / 64.0, 30 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      new Quad(
          new Vector3(4 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(12 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(4 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector4(18 / 64.0, 30 / 64.0, 16 / 32.0, 17.5 / 32.0)),
      // cube6
      new Quad(
          new Vector3(9 / 16.0, 21 / 16.0, 9 / 16.0),
          new Vector3(11 / 16.0, 21 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 21 / 16.0, 7 / 16.0),
          new Vector4(50 / 64.0, 52 / 64.0, 23 / 32.0, 24 / 32.0)),
      new Quad(
          new Vector3(9 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(11 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector4(52 / 64.0, 54 / 64.0, 23 / 32.0, 24 / 32.0)),
      new Quad(
          new Vector3(11 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(11 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(11 / 16.0, 21 / 16.0, 9 / 16.0),
          new Vector4(52 / 64.0, 54 / 64.0, 19.5 / 32.0, 23 / 32.0)),
      new Quad(
          new Vector3(9 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 21 / 16.0, 7 / 16.0),
          new Vector4(48 / 64.0, 50 / 64.0, 19.5 / 32.0, 23 / 32.0)),
      new Quad(
          new Vector3(11 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 14 / 16.0, 7 / 16.0),
          new Vector3(11 / 16.0, 21 / 16.0, 7 / 16.0),
          new Vector4(54 / 64.0, 56 / 64.0, 19.5 / 32.0, 23 / 32.0)),
      new Quad(
          new Vector3(9 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(11 / 16.0, 14 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 21 / 16.0, 9 / 16.0),
          new Vector4(50 / 64.0, 52 / 64.0, 19.5 / 32.0, 23 / 32.0)),
      // cube7
      new Quad(
          new Vector3(5 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(5 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector4(10 / 64.0, 12 / 64.0, 31 / 32.0, 32 / 32.0)),
      new Quad(
          new Vector3(5 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(5 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector4(12 / 64.0, 14 / 64.0, 31 / 32.0, 32 / 32.0)),
      new Quad(
          new Vector3(7 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector4(12 / 64.0, 14 / 64.0, 25.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(5 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(5 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector3(5 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector4(8 / 64.0, 10 / 64.0, 25.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(7 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(5 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector4(14 / 64.0, 16 / 64.0, 25.5 / 32.0, 31 / 32.0)),
      new Quad(
          new Vector3(5 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector3(5 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector4(10 / 64.0, 12 / 64.0, 25.5 / 32.0, 31 / 32.0)),
      // cube8
      new Quad(
          new Vector3(9 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(11 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector4(42 / 64.0, 44 / 64.0, 23 / 32.0, 24 / 32.0)),
      new Quad(
          new Vector3(9 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(11 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector4(44 / 64.0, 46 / 64.0, 23 / 32.0, 24 / 32.0)),
      new Quad(
          new Vector3(11 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector3(11 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(11 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector4(42 / 64.0, 40 / 64.0, 17.5 / 32.0, 23 / 32.0)),
      new Quad(
          new Vector3(9 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector4(46 / 64.0, 44 / 64.0, 17.5 / 32.0, 23 / 32.0)),
      new Quad(
          new Vector3(11 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 1 / 16.0, 7 / 16.0),
          new Vector3(11 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector4(48 / 64.0, 46 / 64.0, 17.5 / 32.0, 23 / 32.0)),
      new Quad(
          new Vector3(9 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector3(11 / 16.0, 1 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector4(44 / 64.0, 42 / 64.0, 17.5 / 32.0, 23 / 32.0)),
  };

  private static final Quad[] leftArm = {
      new Quad(
          new Vector3(-1 / 16.0, 6 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, 6 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, 6 / 16.0, -1 / 16.0),
          new Vector4(36 / 64.0, 34 / 64.0, 46 / 64.0, 48 / 64.0)),
      new Quad(
          new Vector3(-1 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, -6 / 16.0, 1 / 16.0),
          new Vector4(38 / 64.0, 36 / 64.0, 48 / 64.0, 46 / 64.0)),
      new Quad(
          new Vector3(1 / 16.0, -6 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, 6 / 16.0, 1 / 16.0),
          new Vector4(38 / 64.0, 36 / 64.0, 34 / 64.0, 46 / 64.0)),
      new Quad(
          new Vector3(-1 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, -6 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, 6 / 16.0, -1 / 16.0),
          new Vector4(34 / 64.0, 32 / 64.0, 34 / 64.0, 46 / 64.0)),
      new Quad(
          new Vector3(1 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, 6 / 16.0, -1 / 16.0),
          new Vector4(36 / 64.0, 34 / 64.0, 34 / 64.0, 46 / 64.0)),
      new Quad(
          new Vector3(-1 / 16.0, -6 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, -6 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, 6 / 16.0, 1 / 16.0),
          new Vector4(40 / 64.0, 38 / 64.0, 34 / 64.0, 46 / 64.0)),
  };

  private static final Quad[] rightArm = {
      new Quad(
          new Vector3(-1 / 16.0, 6 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, 6 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, 6 / 16.0, -1 / 16.0),
          new Vector4(28 / 64.0, 26 / 64.0, 64 / 64.0, 62 / 64.0)),
      new Quad(
          new Vector3(-1 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, -6 / 16.0, 1 / 16.0),
          new Vector4(28 / 64.0, 30 / 64.0, 62 / 64.0, 64 / 64.0)),
      new Quad(
          new Vector3(1 / 16.0, -6 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, 6 / 16.0, 1 / 16.0),
          new Vector4(24 / 64.0, 26 / 64.0, 50 / 64.0, 62 / 64.0)),
      new Quad(
          new Vector3(-1 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, -6 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, 6 / 16.0, -1 / 16.0),
          new Vector4(28 / 64.0, 30 / 64.0, 50 / 64.0, 62 / 64.0)),
      new Quad(
          new Vector3(1 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, 6 / 16.0, -1 / 16.0),
          new Vector4(26 / 64.0, 28 / 64.0, 50 / 64.0, 62 / 64.0)),
      new Quad(
          new Vector3(-1 / 16.0, -6 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, -6 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, 6 / 16.0, 1 / 16.0),
          new Vector4(30 / 64.0, 32 / 64.0, 50 / 64.0, 62 / 64.0)),
  };

  private final double rotation;
  private final JsonObject gear;
  private final boolean showArms;

  public ArmorStand(Vector3 position, double rotation, JsonObject json) {
    super(position);
    this.rotation = rotation;
    this.showArms = json.get("showArms").asBoolean(false);
    this.gear = json.get("gear").object();
  }

  public ArmorStand(Vector3 position, double rotation, Tag tag) {
    this(position, rotation, parseSettings(tag));
  }

  private static JsonObject parseSettings(Tag tag) {
    JsonObject gear = new JsonObject();
    Tag armorItems = tag.get("ArmorItems");
    CompoundTag boots = armorItems.get(0).asCompound();
    // TODO: handle colored leather.
    if (!boots.isEmpty()) {
      gear.add("feet", PlayerEntity.parseItem(boots));
    }
    CompoundTag legs = armorItems.get(1).asCompound();
    if (!legs.isEmpty()) {
      gear.add("legs", PlayerEntity.parseItem(legs));
    }
    CompoundTag chest = armorItems.get(2).asCompound();
    if (!chest.isEmpty()) {
      gear.add("chest", PlayerEntity.parseItem(chest));
    }
    CompoundTag head = armorItems.get(3).asCompound();
    if (!head.isEmpty()) {
      gear.add("head", PlayerEntity.parseItem(head));
    }
    JsonObject settings = new JsonObject();
    settings.add("showArms", tag.get("ShowArms").boolValue(false));
    settings.add("gear", gear);
    return settings;
  }

  @Override public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> primitives = new LinkedList<>();
    Material material = new TextureMaterial(Texture.armorStand);

    Vector3 worldOffset = new Vector3(
        position.x + offset.x,
        position.y + offset.y,
        position.z + offset.z);

    // Add the base - not rotated.
    Transform transform = Transform.NONE.translate(-0.5, 0, -0.5).translate(worldOffset);
    for (Quad quad : base) {
      quad.addTriangles(primitives, material, transform);
    }


    double armWidth = 2;
    double rightArmPose = 0.1;
    double leftArmPose = -0.07;
    double yaw = QuickMath.degToRad(180 - rotation);
    JsonObject pose = new JsonObject();
    pose.add("pitch", 0);
    pose.add("headYaw", 0);
    pose.add("yaw", yaw);
    pose.add("leftArm", leftArmPose);
    pose.add("rightArm", rightArmPose);
    pose.add("leftLeg", 0);
    pose.add("rightLeg", 0);

    PlayerEntity.addArmor(primitives, gear, pose, worldOffset, armWidth);

    if (showArms) {
      transform = Transform.NONE
          .translate(0, -5 / 16., 0)
          .rotateX(leftArmPose)
          .translate(-(4 + armWidth) / 16., 23 / 16., 0)
          .rotateY(yaw)
          .translate(worldOffset);
      for (Quad quad : leftArm) {
        quad.addTriangles(primitives, material, transform);
      }

      transform = Transform.NONE
          .translate(0, -5 / 16., 0)
          .rotateX(rightArmPose)
          .translate((4 + armWidth) / 16., 23 / 16., 0)
          .rotateY(yaw)
          .translate(worldOffset);
      for (Quad quad : rightArm) {
        quad.addTriangles(primitives, material, transform);
      }
    }

    // Add the armor stand.
    double rot = QuickMath.degToRad(360 - rotation);
    transform = Transform.NONE.translate(-0.5, 0, -.5).rotateY(rot).translate(worldOffset);
    for (Quad quad : quads) {
      quad.addTriangles(primitives, material, transform);
    }
    return primitives;
  }

  @Override public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "armor_stand");
    json.add("position", position.toJson());
    json.add("rotation", rotation);
    json.add("showArms", showArms);
    json.add("gear", gear);
    return json;
  }

  /**
   * Deserialize entity from JSON.
   *
   * @return deserialized entity, or {@code null} if it was not a valid entity
   */
  public static Entity fromJson(JsonObject json) {
    Vector3 position = new Vector3();
    position.fromJson(json.get("position").object());
    double rotation = json.get("rotation").doubleValue(0.0);
    return new ArmorStand(position, rotation, json);
  }
}
