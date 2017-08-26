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
package se.llbit.chunky.entity;

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
import se.llbit.util.JsonUtil;

import java.util.Collection;
import java.util.LinkedList;

public class ArmorStand extends Entity implements Poseable {

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

  private static final Quad[] torso = {
      // torsoTop
      new Quad(
          new Vector3(-5 / 16.0, 6 / 16.0, 1.5 / 16.0),
          new Vector3(5 / 16.0, 6 / 16.0, 1.5 / 16.0),
          new Vector3(-5 / 16.0, 6 / 16.0, -1.5 / 16.0),
          new Vector4(0.75 / 16.0, 3.75 / 16.0, 8.75 / 16.0, 9.5 / 16.0)),
      new Quad(
          new Vector3(-5 / 16.0, 3 / 16.0, -1.5 / 16.0),
          new Vector3(5 / 16.0, 3 / 16.0, -1.5 / 16.0),
          new Vector3(-5 / 16.0, 3 / 16.0, 1.5 / 16.0),
          new Vector4(3.75 / 16.0, 6.75 / 16.0, 9.5 / 16.0, 8.75 / 16.0)),
      new Quad(
          new Vector3(5 / 16.0, 3 / 16.0, 1.5 / 16.0),
          new Vector3(5 / 16.0, 3 / 16.0, -1.5 / 16.0),
          new Vector3(5 / 16.0, 6 / 16.0, 1.5 / 16.0),
          new Vector4(3.75 / 16.0, 4.5 / 16.0, 8 / 16.0, 8.75 / 16.0)),
      new Quad(
          new Vector3(-5 / 16.0, 3 / 16.0, -1.5 / 16.0),
          new Vector3(-5 / 16.0, 3 / 16.0, 1.5 / 16.0),
          new Vector3(-5 / 16.0, 6 / 16.0, -1.5 / 16.0),
          new Vector4(0, 0.75 / 16.0, 8 / 16.0, 8.75 / 16.0)),
      new Quad(
          new Vector3(5 / 16.0, 3 / 16.0, -1.5 / 16.0),
          new Vector3(-5 / 16.0, 3 / 16.0, -1.5 / 16.0),
          new Vector3(5 / 16.0, 6 / 16.0, -1.5 / 16.0),
          new Vector4(4.5 / 16.0, 7.5 / 16.0, 8 / 16.0, 8.75 / 16.0)),
      new Quad(
          new Vector3(-5 / 16.0, 3 / 16.0, 1.5 / 16.0),
          new Vector3(5 / 16.0, 3 / 16.0, 1.5 / 16.0),
          new Vector3(-5 / 16.0, 6 / 16.0, 1.5 / 16.0),
          new Vector4(0.75 / 16.0, 3.75 / 16.0, 8 / 16.0, 8.75 / 16.0)),
      // torsoLeft
      new Quad(
          new Vector3(-3 / 16.0, 3 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, 3 / 16.0, 1 / 16.0),
          new Vector3(-3 / 16.0, 3 / 16.0, -1 / 16.0),
          new Vector4(4.5 / 16.0, 5 / 16.0, 15.5 / 16.0, 16 / 16.0)),
      new Quad(
          new Vector3(-3 / 16.0, -4 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, -4 / 16.0, -1 / 16.0),
          new Vector3(-3 / 16.0, -4 / 16.0, 1 / 16.0),
          new Vector4(5 / 16.0, 5.5 / 16.0, 15.5 / 16.0, 16 / 16.0)),
      new Quad(
          new Vector3(-1 / 16.0, -4 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, -4 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, 3 / 16.0, 1 / 16.0),
          new Vector4(5 / 16.0, 5.5 / 16.0, 13.75 / 16.0, 15.5 / 16.0)),
      new Quad(
          new Vector3(-3 / 16.0, -4 / 16.0, -1 / 16.0),
          new Vector3(-3 / 16.0, -4 / 16.0, 1 / 16.0),
          new Vector3(-3 / 16.0, 3 / 16.0, -1 / 16.0),
          new Vector4(4 / 16.0, 4.5 / 16.0, 13.75 / 16.0, 15.5 / 16.0)),
      new Quad(
          new Vector3(-1 / 16.0, -4 / 16.0, -1 / 16.0),
          new Vector3(-3 / 16.0, -4 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, 3 / 16.0, -1 / 16.0),
          new Vector4(5.5 / 16.0, 6 / 16.0, 13.75 / 16.0, 15.5 / 16.0)),
      new Quad(
          new Vector3(-3 / 16.0, -4 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, -4 / 16.0, 1 / 16.0),
          new Vector3(-3 / 16.0, 3 / 16.0, 1 / 16.0),
          new Vector4(4.5 / 16.0, 5 / 16.0, 13.75 / 16.0, 15.5 / 16.0)),
      // torsoRight
      new Quad(
          new Vector3(1 / 16.0, 3 / 16.0, 1 / 16.0),
          new Vector3(3 / 16.0, 3 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, 3 / 16.0, -1 / 16.0),
          new Vector4(12.5 / 16.0, 13 / 16.0, 11.5 / 16.0, 12 / 16.0)),
      new Quad(
          new Vector3(1 / 16.0, -4 / 16.0, -1 / 16.0),
          new Vector3(3 / 16.0, -4 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, -4 / 16.0, 1 / 16.0),
          new Vector4(13 / 16.0, 13.5 / 16.0, 11.5 / 16.0, 12 / 16.0)),
      new Quad(
          new Vector3(3 / 16.0, -4 / 16.0, 1 / 16.0),
          new Vector3(3 / 16.0, -4 / 16.0, -1 / 16.0),
          new Vector3(3 / 16.0, 3 / 16.0, 1 / 16.0),
          new Vector4(13 / 16.0, 13.5 / 16.0, 9.75 / 16.0, 11.5 / 16.0)),
      new Quad(
          new Vector3(1 / 16.0, -4 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, -4 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, 3 / 16.0, -1 / 16.0),
          new Vector4(12 / 16.0, 12.5 / 16.0, 9.75 / 16.0, 11.5 / 16.0)),
      new Quad(
          new Vector3(3 / 16.0, -4 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, -4 / 16.0, -1 / 16.0),
          new Vector3(3 / 16.0, 3 / 16.0, -1 / 16.0),
          new Vector4(13.5 / 16.0, 14 / 16.0, 9.75 / 16.0, 11.5 / 16.0)),
      new Quad(
          new Vector3(1 / 16.0, -4 / 16.0, 1 / 16.0),
          new Vector3(3 / 16.0, -4 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, 3 / 16.0, 1 / 16.0),
          new Vector4(12.5 / 16.0, 13 / 16.0, 9.75 / 16.0, 11.5 / 16.0)),
      // pelvis
      new Quad(
          new Vector3(-4 / 16.0, -4 / 16.0, 1 / 16.0),
          new Vector3(4 / 16.0, -4 / 16.0, 1 / 16.0),
          new Vector3(-4 / 16.0, -4 / 16.0, -1 / 16.0),
          new Vector4(4.5 / 16.0, 7.5 / 16.0, 8 / 16.0, 8.75 / 16.0)),
      new Quad(
          new Vector3(-4 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(4 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(-4 / 16.0, -6 / 16.0, 1 / 16.0),
          new Vector4(4.5 / 16.0, 7.5 / 16.0, 8 / 16.0, 8.75 / 16.0)),
      new Quad(
          new Vector3(4 / 16.0, -6 / 16.0, 1 / 16.0),
          new Vector3(4 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(4 / 16.0, -4 / 16.0, 1 / 16.0),
          new Vector4(0, 0.75 / 16.0, 8 / 16.0, 8.75 / 16.0)),
      new Quad(
          new Vector3(-4 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(-4 / 16.0, -6 / 16.0, 1 / 16.0),
          new Vector3(-4 / 16.0, -4 / 16.0, -1 / 16.0),
          new Vector4(3.75 / 16.0, 4.5 / 16.0, 8 / 16.0, 8.75 / 16.0)),
      new Quad(
          new Vector3(4 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(-4 / 16.0, -6 / 16.0, -1 / 16.0),
          new Vector3(4 / 16.0, -4 / 16.0, -1 / 16.0),
          new Vector4(4.5 / 16.0, 7.5 / 16.0, 8 / 16.0, 8.75 / 16.0)),
      new Quad(
          new Vector3(-4 / 16.0, -6 / 16.0, 1 / 16.0),
          new Vector3(4 / 16.0, -6 / 16.0, 1 / 16.0),
          new Vector3(-4 / 16.0, -4 / 16.0, 1 / 16.0),
          new Vector4(4.5 / 16.0, 7.5 / 16.0, 8 / 16.0, 8.75 / 16.0)),
  };

  private static final Quad[] neck = {
      new Quad(
          new Vector3(-1 / 16.0, 3 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, 3 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, 3 / 16.0, -1 / 16.0),
          new Vector4(0.5 / 16.0, 1 / 16.0, 15.5 / 16.0, 16 / 16.0)),
      new Quad(
          new Vector3(-1 / 16.0, -3 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, -3 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, -3 / 16.0, 1 / 16.0),
          new Vector4(1 / 16.0, 1.5 / 16.0, 15.5 / 16.0, 16 / 16.0)),
      new Quad(
          new Vector3(1 / 16.0, -3 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, -3 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, 3 / 16.0, 1 / 16.0),
          new Vector4(0, 0.5 / 16.0, 13.75 / 16.0, 15.5 / 16.0)),
      new Quad(
          new Vector3(-1 / 16.0, -3 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, -3 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, 3 / 16.0, -1 / 16.0),
          new Vector4(1 / 16.0, 1.5 / 16.0, 13.75 / 16.0, 15.5 / 16.0)),
      new Quad(
          new Vector3(1 / 16.0, -3 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, -3 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, 3 / 16.0, -1 / 16.0),
          new Vector4(0.5 / 16.0, 1 / 16.0, 13.75 / 16.0, 15.5 / 16.0)),
      new Quad(
          new Vector3(-1 / 16.0, -3 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, -3 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, 3 / 16.0, 1 / 16.0),
          new Vector4(1.5 / 16.0, 2 / 16.0, 13.75 / 16.0, 15.5 / 16.0)),
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

  private static final Quad[] leftLeg = {
      new Quad(
          new Vector3(-1 / 16.0, 5.5 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, 5.5 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, 5.5 / 16.0, -1 / 16.0),
          new Vector4(10.5 / 16.0, 11 / 16.0, 11.5 / 16.0, 12 / 16.0)),
      new Quad(
          new Vector3(-1 / 16.0, -5.5 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, -5.5 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, -5.5 / 16.0, 1 / 16.0),
          new Vector4(11 / 16.0, 11.5 / 16.0, 11.5 / 16.0, 12 / 16.0)),
      new Quad(
          new Vector3(1 / 16.0, -5.5 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, -5.5 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, 5.5 / 16.0, 1 / 16.0),
          new Vector4(10.5 / 16.0, 10 / 16.0, 8.75 / 16.0, 11.5 / 16.0)),
      new Quad(
          new Vector3(-1 / 16.0, -5.5 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, -5.5 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, 5.5 / 16.0, -1 / 16.0),
          new Vector4(11.5 / 16.0, 11 / 16.0, 8.75 / 16.0, 11.5 / 16.0)),
      new Quad(
          new Vector3(1 / 16.0, -5.5 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, -5.5 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, 5.5 / 16.0, -1 / 16.0),
          new Vector4(12 / 16.0, 11.5 / 16.0, 8.75 / 16.0, 11.5 / 16.0)),
      new Quad(
          new Vector3(-1 / 16.0, -5.5 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, -5.5 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, 5.5 / 16.0, 1 / 16.0),
          new Vector4(11 / 16.0, 10.5 / 16.0, 8.75 / 16.0, 11.5 / 16.0)),
  };

  private static final Quad[] rightLeg = {
      new Quad(
          new Vector3(-1 / 16.0, 5.5 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, 5.5 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, 5.5 / 16.0, -1 / 16.0),
          new Vector4(2.5 / 16.0, 3 / 16.0, 15.5 / 16.0, 16 / 16.0)),
      new Quad(
          new Vector3(-1 / 16.0, -5.5 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, -5.5 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, -5.5 / 16.0, 1 / 16.0),
          new Vector4(3 / 16.0, 3.5 / 16.0, 15.5 / 16.0, 16 / 16.0)),
      new Quad(
          new Vector3(1 / 16.0, -5.5 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, -5.5 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, 5.5 / 16.0, 1 / 16.0),
          new Vector4(3 / 16.0, 3.5 / 16.0, 12.75 / 16.0, 15.5 / 16.0)),
      new Quad(
          new Vector3(-1 / 16.0, -5.5 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, -5.5 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, 5.5 / 16.0, -1 / 16.0),
          new Vector4(2 / 16.0, 2.5 / 16.0, 12.75 / 16.0, 15.5 / 16.0)),
      new Quad(
          new Vector3(1 / 16.0, -5.5 / 16.0, -1 / 16.0),
          new Vector3(-1 / 16.0, -5.5 / 16.0, -1 / 16.0),
          new Vector3(1 / 16.0, 5.5 / 16.0, -1 / 16.0),
          new Vector4(3.5 / 16.0, 4 / 16.0, 12.75 / 16.0, 15.5 / 16.0)),
      new Quad(
          new Vector3(-1 / 16.0, -5.5 / 16.0, 1 / 16.0),
          new Vector3(1 / 16.0, -5.5 / 16.0, 1 / 16.0),
          new Vector3(-1 / 16.0, 5.5 / 16.0, 1 / 16.0),
          new Vector4(2.5 / 16.0, 3 / 16.0, 12.75 / 16.0, 15.5 / 16.0)),
  };

  private double scale = 1.0;
  private double headScale = 1.0;
  private final JsonObject gear;
  private final JsonObject pose;
  private final boolean showArms;

  public ArmorStand(JsonObject json) {
    super(JsonUtil.vec3FromJson(json.get("position")));
    this.scale = json.get("scale").asDouble(1.0);
    this.headScale = json.get("headScale").asDouble(1.0);
    this.showArms = json.get("showArms").asBoolean(false);
    this.gear = json.get("gear").object();
    this.pose = json.get("pose").object();
  }

  public ArmorStand(Vector3 position, Tag tag) {
    super(position);
    gear = new JsonObject();
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
    showArms = tag.get("ShowArms").boolValue(false);
    if (tag.get("Small").boolValue(false)) {
      scale = 0.5;
      headScale = 1.75;
    }
    Tag poseTag = tag.get("Pose");
    pose = new JsonObject();
    float rotation = tag.get("Rotation").get(0).floatValue();
    double yaw = QuickMath.degToRad(180 - rotation);
    pose.add("all", JsonUtil.vec3ToJson(new Vector3(0, yaw, 0)));
    pose.add("head", JsonUtil.listTagToJson(poseTag.get("Head")));
    pose.add("chest", JsonUtil.listTagToJson(poseTag.get("Body")));
    pose.add("leftArm", JsonUtil.listTagToJson(poseTag.get("LeftArm")));
    pose.add("rightArm", JsonUtil.listTagToJson(poseTag.get("RightArm")));
    pose.add("leftLeg", JsonUtil.listTagToJson(poseTag.get("LeftLeg")));
    pose.add("rightLeg", JsonUtil.listTagToJson(poseTag.get("RightLeg")));
  }

  @Override public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> primitives = new LinkedList<>();
    Material material = new TextureMaterial(Texture.armorStand);

    Vector3 worldOffset = new Vector3(
        position.x + offset.x,
        position.y + offset.y,
        position.z + offset.z);

    // Add the base - not rotated or scaled.
    Transform transform = Transform.NONE.translate(-0.5, 0, -0.5).translate(worldOffset);
    for (Quad quad : base) {
      quad.addTriangles(primitives, material, transform);
    }


    double armWidth = 2;
    JsonObject pose = new JsonObject();
    // TODO: sort out rotations so we use the same sign everywhere (player entity).
    Vector3 allPose = JsonUtil.vec3FromJson(this.pose.get("all"));
    double yaw = allPose.y;
    Vector3 headPose = JsonUtil.vec3FromJson(this.pose.get("head"));
    headPose.x = - headPose.x;
    headPose.y = - headPose.y;
    Vector3 chestPose = JsonUtil.vec3FromJson(this.pose.get("chest"));
    Vector3 leftArmPose = JsonUtil.vec3FromJson(this.pose.get("leftArm"));
    leftArmPose.x = -leftArmPose.x;
    leftArmPose.y = -leftArmPose.y;
    Vector3 rightArmPose = JsonUtil.vec3FromJson(this.pose.get("rightArm"));
    rightArmPose.x = -rightArmPose.x;
    rightArmPose.y = -rightArmPose.y;
    Vector3 leftLegPose = JsonUtil.vec3FromJson(this.pose.get("leftLeg"));
    leftLegPose.x = -leftLegPose.x;
    leftLegPose.y = -leftLegPose.y;
    Vector3 rightLegPose = JsonUtil.vec3FromJson(this.pose.get("rightLeg"));
    rightLegPose.x = -rightLegPose.x;
    rightLegPose.y = -rightLegPose.y;
    pose.add("all", JsonUtil.vec3ToJson(allPose));
    pose.add("head", JsonUtil.vec3ToJson(headPose));
    pose.add("chest", JsonUtil.vec3ToJson(chestPose));
    pose.add("leftArm", JsonUtil.vec3ToJson(leftArmPose));
    pose.add("rightArm", JsonUtil.vec3ToJson(rightArmPose));
    pose.add("leftLeg", JsonUtil.vec3ToJson(leftLegPose));
    pose.add("rightLeg", JsonUtil.vec3ToJson(rightLegPose));

    Transform worldTransform = Transform.NONE
        .scale(scale)
        .rotateX(allPose.x)
        .rotateY(allPose.y)
        .rotateZ(allPose.z)
        .translate(worldOffset);
    PlayerEntity.addArmor(primitives, gear, pose, armWidth, worldTransform,
        headScale);

    if (showArms) {
      transform = Transform.NONE
          .translate(0, -5 / 16., 0)
          .rotateX(leftArmPose.x)
          .rotateY(leftArmPose.y)
          .rotateZ(leftArmPose.z)
          .translate(-(4 + armWidth) / 16., 23 / 16., 0)
          .chain(worldTransform);
      for (Quad quad : leftArm) {
        quad.addTriangles(primitives, material, transform);
      }

      transform = Transform.NONE
          .translate(0, -5 / 16., 0)
          .rotateX(rightArmPose.x)
          .rotateY(rightArmPose.y)
          .rotateZ(rightArmPose.z)
          .translate((4 + armWidth) / 16., 23 / 16., 0)
          .chain(worldTransform);
      for (Quad quad : rightArm) {
        quad.addTriangles(primitives, material, transform);
      }
    }

    transform = Transform.NONE
        .translate(0, -5 / 16., 0)
        .rotateX(leftLegPose.x)
        .rotateY(leftLegPose.y)
        .rotateZ(leftLegPose.z)
        .translate(-2 / 16., 11.5 / 16., 0)
        .chain(worldTransform);
    for (Quad quad : leftLeg) {
      quad.addTriangles(primitives, material, transform);
    }

    transform = Transform.NONE
        .translate(0, -5 / 16., 0)
        .rotateX(rightLegPose.x)
        .rotateY(rightLegPose.y)
        .rotateZ(rightLegPose.z)
        .translate(2 / 16., 11.5 / 16., 0)
        .chain(worldTransform);
    for (Quad quad : rightLeg) {
      quad.addTriangles(primitives, material, transform);
    }

    transform = Transform.NONE
        .translate(0, -5 / 16., 0)
        .rotateX(chestPose.x)
        .rotateY(chestPose.y)
        .rotateZ(chestPose.z)
        .translate(0, (5 + 18) / 16., 0)
        .chain(worldTransform);
    for (Quad quad : torso) {
      quad.addTriangles(primitives, material, transform);
    }

    transform = Transform.NONE
        .translate(0, 2 / 16.0, 0)
        .rotateX(headPose.x)
        .rotateY(headPose.y)
        .rotateZ(headPose.z)
        .scale(headScale)
        .translate(0, 24 / 16.0, 0)
        .chain(worldTransform);
    for (Quad quad : neck) {
      quad.addTriangles(primitives, material, transform);
    }
    return primitives;
  }

  @Override public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "armor_stand");
    json.add("position", position.toJson());
    json.add("scale", scale);
    json.add("headScale", headScale);
    json.add("showArms", showArms);
    json.add("gear", gear);
    json.add("pose", pose);
    return json;
  }

  /**
   * Deserialize entity from JSON.
   *
   * @return deserialized entity, or {@code null} if it was not a valid entity
   */
  public static Entity fromJson(JsonObject json) {
    return new ArmorStand(json);
  }

  @Override public String[] partNames() {
    return new String[] { "all", "head", "chest", "leftArm", "rightArm", "leftLeg", "rightLeg" };
  }
}
