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
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * The new Comparator model introduced in Minecraft 1.21.2 (24w33a).
 */
public class ComparatorModel1212 extends QuadModel {
  private static final Texture slab = Texture.smoothStone;
  private static final Texture topOff = Texture.comparatorOff;
  private static final Texture topOn = Texture.comparatorOn;
  private static final Texture unlit = Texture.redstoneTorchOff;
  private static final Texture lit = Texture.redstoneTorchOn;

  private static final Texture[] comparatorTex = new Texture[]{
    topOff, slab, slab, slab, slab, slab, unlit, unlit, unlit, unlit, unlit, unlit, unlit, unlit, unlit, unlit, unlit, unlit, unlit, unlit, unlit
  };

  private static final Quad[] comparator = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(6 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(4 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(4 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(4 / 16.0, 2 / 16.0, 13 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(6 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(6 / 16.0, 2 / 16.0, 11 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(6 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(4 / 16.0, 2 / 16.0, 11 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(4 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(6 / 16.0, 2 / 16.0, 13 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(12 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(10 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(10 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(10 / 16.0, 2 / 16.0, 13 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(12 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(12 / 16.0, 2 / 16.0, 11 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(12 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(10 / 16.0, 2 / 16.0, 11 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(10 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(12 / 16.0, 2 / 16.0, 13 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector3(7 / 16.0, 2 / 16.0, 4 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector3(9 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 2 / 16.0, 2 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector3(9 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector3(7 / 16.0, 2 / 16.0, 2 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 2 / 16.0, 4 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 7 / 16.0)
    )
  };

  private static final Texture[] comparatorOnTex = new Texture[]{
    topOn, slab, slab, slab, slab, slab, lit, lit, lit, lit, lit, unlit, unlit, unlit, unlit, unlit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit
  };

  private static final Quad[] comparatorOn = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(6 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(4 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(4 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(4 / 16.0, 2 / 16.0, 13 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(6 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(6 / 16.0, 2 / 16.0, 11 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(6 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(4 / 16.0, 2 / 16.0, 11 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(4 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(6 / 16.0, 2 / 16.0, 13 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector3(7 / 16.0, 2 / 16.0, 4 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector3(9 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 2 / 16.0, 2 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector3(9 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector3(7 / 16.0, 2 / 16.0, 2 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 2 / 16.0, 4 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(12 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(10 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(10 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(10 / 16.0, 2 / 16.0, 13 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(12 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(12 / 16.0, 2 / 16.0, 11 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(12 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(10 / 16.0, 2 / 16.0, 11 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(10 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(12 / 16.0, 2 / 16.0, 13 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(3.5 / 16.0, 4.5 / 16.0, 13.5 / 16.0),
      new Vector3(6.5 / 16.0, 4.5 / 16.0, 13.5 / 16.0),
      new Vector3(3.5 / 16.0, 4.5 / 16.0, 10.5 / 16.0),
      new Vector4(6 / 16.0, 7 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(3.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(3.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector4(6 / 16.0, 7 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(3.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(6.5 / 16.0, 4.5 / 16.0, 10.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(3.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector3(3.5 / 16.0, 4.5 / 16.0, 13.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(3.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(3.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector3(3.5 / 16.0, 4.5 / 16.0, 10.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(6.5 / 16.0, 4.5 / 16.0, 13.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(9.5 / 16.0, 4.5 / 16.0, 13.5 / 16.0),
      new Vector3(12.5 / 16.0, 4.5 / 16.0, 13.5 / 16.0),
      new Vector3(9.5 / 16.0, 4.5 / 16.0, 10.5 / 16.0),
      new Vector4(6 / 16.0, 7 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(12.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector4(6 / 16.0, 7 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(12.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(12.5 / 16.0, 4.5 / 16.0, 10.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector3(12.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector3(9.5 / 16.0, 4.5 / 16.0, 13.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector3(9.5 / 16.0, 4.5 / 16.0, 10.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(12.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector3(12.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(12.5 / 16.0, 4.5 / 16.0, 13.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    )
  };
  private static final Texture[] comparatorSubtractTex = new Texture[]{
    topOff, slab, slab, slab, slab, slab, unlit, unlit, unlit, unlit, unlit, unlit, unlit, unlit, unlit, unlit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit
  };

  private static final Quad[] comparatorSubtract = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(6 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(4 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(4 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(4 / 16.0, 2 / 16.0, 13 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(6 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(6 / 16.0, 2 / 16.0, 11 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(6 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(4 / 16.0, 2 / 16.0, 11 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(4 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(6 / 16.0, 2 / 16.0, 13 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(12 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(10 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(10 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(10 / 16.0, 2 / 16.0, 13 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(12 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(12 / 16.0, 2 / 16.0, 11 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(12 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(10 / 16.0, 2 / 16.0, 11 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(10 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(12 / 16.0, 2 / 16.0, 13 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector3(7 / 16.0, 2 / 16.0, 4 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector3(9 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 2 / 16.0, 2 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector3(9 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector3(7 / 16.0, 2 / 16.0, 2 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 2 / 16.0, 4 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 7 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(6.5 / 16.0, 2.5 / 16.0, 4.5 / 16.0),
      new Vector3(9.5 / 16.0, 2.5 / 16.0, 4.5 / 16.0),
      new Vector3(6.5 / 16.0, 2.5 / 16.0, 1.5 / 16.0),
      new Vector4(6 / 16.0, 7 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(6.5 / 16.0, 5.5 / 16.0, 1.5 / 16.0),
      new Vector3(9.5 / 16.0, 5.5 / 16.0, 1.5 / 16.0),
      new Vector3(6.5 / 16.0, 5.5 / 16.0, 4.5 / 16.0),
      new Vector4(6 / 16.0, 7 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(9.5 / 16.0, 5.5 / 16.0, 1.5 / 16.0),
      new Vector3(6.5 / 16.0, 5.5 / 16.0, 1.5 / 16.0),
      new Vector3(9.5 / 16.0, 2.5 / 16.0, 1.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(6.5 / 16.0, 5.5 / 16.0, 4.5 / 16.0),
      new Vector3(9.5 / 16.0, 5.5 / 16.0, 4.5 / 16.0),
      new Vector3(6.5 / 16.0, 2.5 / 16.0, 4.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(6.5 / 16.0, 5.5 / 16.0, 1.5 / 16.0),
      new Vector3(6.5 / 16.0, 5.5 / 16.0, 4.5 / 16.0),
      new Vector3(6.5 / 16.0, 2.5 / 16.0, 1.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(9.5 / 16.0, 5.5 / 16.0, 4.5 / 16.0),
      new Vector3(9.5 / 16.0, 5.5 / 16.0, 1.5 / 16.0),
      new Vector3(9.5 / 16.0, 2.5 / 16.0, 4.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    )
  };

  private static final Texture[] comparatorSubtractOnTex = new Texture[]{
    topOn, slab, slab, slab, slab, slab, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit, lit
  };

  private static final Quad[] comparatorSubtractOn = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector3(7 / 16.0, 2 / 16.0, 4 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector3(9 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 2 / 16.0, 2 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector3(9 / 16.0, 5 / 16.0, 2 / 16.0),
      new Vector3(7 / 16.0, 2 / 16.0, 2 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 5 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 2 / 16.0, 4 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(6 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(4 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(4 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(4 / 16.0, 2 / 16.0, 13 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(6 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(6 / 16.0, 2 / 16.0, 11 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(4 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(6 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(4 / 16.0, 2 / 16.0, 11 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(4 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(6 / 16.0, 2 / 16.0, 13 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(12 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(10 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(10 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(10 / 16.0, 2 / 16.0, 13 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(12 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(12 / 16.0, 2 / 16.0, 11 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(12 / 16.0, 7 / 16.0, 11 / 16.0),
      new Vector3(10 / 16.0, 2 / 16.0, 11 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(12 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(10 / 16.0, 7 / 16.0, 13 / 16.0),
      new Vector3(12 / 16.0, 2 / 16.0, 13 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(3.5 / 16.0, 4.5 / 16.0, 13.5 / 16.0),
      new Vector3(6.5 / 16.0, 4.5 / 16.0, 13.5 / 16.0),
      new Vector3(3.5 / 16.0, 4.5 / 16.0, 10.5 / 16.0),
      new Vector4(6 / 16.0, 7 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(3.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(3.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector4(6 / 16.0, 7 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(3.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(6.5 / 16.0, 4.5 / 16.0, 10.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(3.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector3(3.5 / 16.0, 4.5 / 16.0, 13.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(3.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(3.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector3(3.5 / 16.0, 4.5 / 16.0, 10.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(6.5 / 16.0, 4.5 / 16.0, 13.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(9.5 / 16.0, 4.5 / 16.0, 13.5 / 16.0),
      new Vector3(12.5 / 16.0, 4.5 / 16.0, 13.5 / 16.0),
      new Vector3(9.5 / 16.0, 4.5 / 16.0, 10.5 / 16.0),
      new Vector4(6 / 16.0, 7 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(12.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector4(6 / 16.0, 7 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(12.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(12.5 / 16.0, 4.5 / 16.0, 10.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector3(12.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector3(9.5 / 16.0, 4.5 / 16.0, 13.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector3(9.5 / 16.0, 4.5 / 16.0, 10.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(12.5 / 16.0, 7.5 / 16.0, 13.5 / 16.0),
      new Vector3(12.5 / 16.0, 7.5 / 16.0, 10.5 / 16.0),
      new Vector3(12.5 / 16.0, 4.5 / 16.0, 13.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(6.5 / 16.0, 2.5 / 16.0, 4.5 / 16.0),
      new Vector3(9.5 / 16.0, 2.5 / 16.0, 4.5 / 16.0),
      new Vector3(6.5 / 16.0, 2.5 / 16.0, 1.5 / 16.0),
      new Vector4(6 / 16.0, 7 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(6.5 / 16.0, 5.5 / 16.0, 1.5 / 16.0),
      new Vector3(9.5 / 16.0, 5.5 / 16.0, 1.5 / 16.0),
      new Vector3(6.5 / 16.0, 5.5 / 16.0, 4.5 / 16.0),
      new Vector4(6 / 16.0, 7 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(9.5 / 16.0, 5.5 / 16.0, 1.5 / 16.0),
      new Vector3(6.5 / 16.0, 5.5 / 16.0, 1.5 / 16.0),
      new Vector3(9.5 / 16.0, 2.5 / 16.0, 1.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(6.5 / 16.0, 5.5 / 16.0, 4.5 / 16.0),
      new Vector3(9.5 / 16.0, 5.5 / 16.0, 4.5 / 16.0),
      new Vector3(6.5 / 16.0, 2.5 / 16.0, 4.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(6.5 / 16.0, 5.5 / 16.0, 1.5 / 16.0),
      new Vector3(6.5 / 16.0, 5.5 / 16.0, 4.5 / 16.0),
      new Vector3(6.5 / 16.0, 2.5 / 16.0, 1.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(9.5 / 16.0, 5.5 / 16.0, 4.5 / 16.0),
      new Vector3(9.5 / 16.0, 5.5 / 16.0, 1.5 / 16.0),
      new Vector3(9.5 / 16.0, 2.5 / 16.0, 4.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
    )
  };

  private final Quad[] quads;
  private final Texture[] textures;

  public ComparatorModel1212(String facing, String mode, boolean powered) {
    Quad[] model;
    if (mode.equals("subtract")) {
      model = powered ? comparatorSubtractOn : comparatorSubtract;
      textures = powered ? comparatorSubtractOnTex : comparatorSubtractTex;
    } else {
      model = powered ? comparatorOn : comparator;
      textures = powered ? comparatorOnTex : comparatorTex;
    }
    quads = switch (facing) {
      case "west" -> Model.rotateY(model);
      case "north" -> Model.rotateY(Model.rotateY(model));
      case "east" -> Model.rotateNegY(model);
      default -> model;
    };
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return RedstoneTorchModel.intersectWithGlow(ray, scene, this);
  }
}
