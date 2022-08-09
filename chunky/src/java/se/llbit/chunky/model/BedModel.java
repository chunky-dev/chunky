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

import java.util.Arrays;

public class BedModel extends QuadModel {
  //region Bed Foot
  private static final Quad[] foot = {
      // Mattress:
      new Quad(
          new Vector3(0, 9 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 9 / 16.0, 16 / 16.0),
          new Vector3(0, 9 / 16.0, 0),
          new Vector4(22 / 64.0, 6 / 64.0, 36 / 64.0, 20 / 64.0)),
      new Quad(
          new Vector3(0, 3 / 16.0, 0),
          new Vector3(16 / 16.0, 3 / 16.0, 0),
          new Vector3(0, 3 / 16.0, 16 / 16.0),
          new Vector4(28 / 64.0, 44 / 64.0, 20 / 64.0, 36 / 64.0)),
      new Quad(
          new Vector3(16 / 16.0, 9 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 3 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 9 / 16.0, 0),
          new Vector4(6 / 64.0, 0, 36 / 64.0, 20 / 64.0)),
      new Quad(
          new Vector3(0, 3 / 16.0, 16 / 16.0),
          new Vector3(0, 9 / 16.0, 16 / 16.0),
          new Vector3(0, 3 / 16.0, 0),
          new Vector4(28 / 64.0, 22 / 64.0, 36 / 64.0, 20 / 64.0)),
      new Quad(
          new Vector3(16 / 16.0, 3 / 16.0, 0),
          new Vector3(0, 3 / 16.0, 0),
          new Vector3(16 / 16.0, 9 / 16.0, 0),
          new Vector4(22 / 64.0, 38 / 64.0, 42 / 64.0, 36 / 64.0)),
      // Leg 1 (left when facing back end):
      new Quad(
          new Vector3(16 / 16.0, 0, 3 / 16.0),
          new Vector3(13 / 16.0, 0, 3 / 16.0),
          new Vector3(16 / 16.0, 0, 0),
          new Vector4(56 / 64.0, 59 / 64.0, 52 / 64.0, 49 / 64.0)),
      new Quad(
          new Vector3(16 / 16.0, 0, 3 / 16.0),
          new Vector3(16 / 16.0, 0, 0),
          new Vector3(16 / 16.0, 3 / 16.0, 3 / 16.0),
          new Vector4(50 / 64.0, 53 / 64.0, 61 / 64.0, 58 / 64.0)),
      new Quad(
          new Vector3(13 / 16.0, 0, 0),
          new Vector3(13 / 16.0, 0, 3 / 16.0),
          new Vector3(13 / 16.0, 3 / 16.0, 0),
          new Vector4(56 / 64.0, 59 / 64.0, 61 / 64.0, 58 / 64.0)),
      new Quad(
          new Vector3(16 / 16.0, 0, 0),
          new Vector3(13 / 16.0, 0, 0),
          new Vector3(16 / 16.0, 3 / 16.0, 0),
          new Vector4(53 / 64.0, 56 / 64.0, 61 / 64.0, 58 / 64.0)),
      new Quad(
          new Vector3(13 / 16.0, 0, 3 / 16.0),
          new Vector3(16 / 16.0, 0, 3 / 16.0),
          new Vector3(13 / 16.0, 3 / 16.0, 3 / 16.0),
          new Vector4(59 / 64.0, 62 / 64.0, 61 / 64.0, 58 / 64.0)),
      // Leg 3 (right when facing back end):
      new Quad(
          new Vector3(3 / 16.0, 0, 0),
          new Vector3(3 / 16.0, 0, 3 / 16.0),
          new Vector3(0, 0, 0),
          new Vector4(56 / 64.0, 59 / 64.0, 52 / 64.0, 49 / 64.0)),
      new Quad(
          new Vector3(3 / 16.0, 0, 3 / 16.0),
          new Vector3(3 / 16.0, 0, 0),
          new Vector3(3 / 16.0, 3 / 16.0, 3 / 16.0),
          new Vector4(59 / 64.0, 62 / 64.0, 49 / 64.0, 46 / 64.0)),
      new Quad(
          new Vector3(0, 0, 0),
          new Vector3(0, 0, 3 / 16.0),
          new Vector3(0, 3 / 16.0, 0),
          new Vector4(53 / 64.0, 56 / 64.0, 49 / 64.0, 46 / 64.0)),
      new Quad(
          new Vector3(3 / 16.0, 0, 0),
          new Vector3(0, 0, 0),
          new Vector3(3 / 16.0, 3 / 16.0, 0),
          new Vector4(50 / 64.0, 53 / 64.0, 49 / 64.0, 46 / 64.0)),
      new Quad(
          new Vector3(0, 0, 3 / 16.0),
          new Vector3(3 / 16.0, 0, 3 / 16.0),
          new Vector3(0, 3 / 16.0, 3 / 16.0),
          new Vector4(56 / 64.0, 59 / 64.0, 49 / 64.0, 46 / 64.0)),
  };
  //endregion

  //region Bed Head
  private static final Quad[] head = {
      // Mattress:
      new Quad(
          new Vector3(0, 9 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 9 / 16.0, 16 / 16.0),
          new Vector3(0, 9 / 16.0, 0),
          new Vector4(22 / 64.0, 6 / 64.0, 58 / 64.0, 42 / 64.0)),
      new Quad(
          new Vector3(0, 3 / 16.0, 0),
          new Vector3(16 / 16.0, 3 / 16.0, 0),
          new Vector3(0, 3 / 16.0, 16 / 16.0),
          new Vector4(28 / 64.0, 44 / 64.0, 58 / 64.0, 42 / 64.0)),
      new Quad(
          new Vector3(16 / 16.0, 9 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 3 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 9 / 16.0, 0),
          new Vector4(6 / 64.0, 0 / 64.0, 58 / 64.0, 42 / 64.0)),
      new Quad(
          new Vector3(0, 3 / 16.0, 16 / 16.0),
          new Vector3(0, 9 / 16.0, 16 / 16.0),
          new Vector3(0, 3 / 16.0, 0),
          new Vector4(0 / 64.0, 6 / 64.0, 58 / 64.0, 42 / 64.0)),
      new Quad(
          new Vector3(0, 3 / 16.0, 16 / 16.0),
          new Vector3(16 / 16.0, 3 / 16.0, 16 / 16.0),
          new Vector3(0, 9 / 16.0, 16 / 16.0),
          new Vector4(6 / 64.0, 22 / 64.0, 64 / 64.0, 58 / 64.0)),
      // Leg 4 (left when facing front end).
      new Quad(
          new Vector3(0, 0, 13 / 16.0),
          new Vector3(3 / 16.0, 0, 13 / 16.0),
          new Vector3(0, 0, 16 / 16.0),
          new Vector4(56 / 64.0, 59 / 64.0, 61 / 64.0, 64 / 64.0)),
      new Quad(
          new Vector3(3 / 16.0, 0, 16 / 16.0),
          new Vector3(3 / 16.0, 0, 13 / 16.0),
          new Vector3(3 / 16.0, 3 / 16.0, 16 / 16.0),
          new Vector4(56 / 64.0, 59 / 64.0, 40 / 64.0, 43 / 64.0)),
      new Quad(
          new Vector3(0, 0, 13 / 16.0),
          new Vector3(0, 0, 16 / 16.0),
          new Vector3(0, 3 / 16.0, 13 / 16.0),
          new Vector4(50 / 64.0, 53 / 64.0, 40 / 64.0, 43 / 64.0)),
      new Quad(
          new Vector3(3 / 16.0, 0, 13 / 16.0),
          new Vector3(0, 0, 13 / 16.0),
          new Vector3(3 / 16.0, 3 / 16.0, 13 / 16.0),
          new Vector4(59 / 64.0, 62 / 64.0, 40 / 64.0, 43 / 64.0)),
      new Quad(
          new Vector3(0, 0, 16 / 16.0),
          new Vector3(3 / 16.0, 0, 16 / 16.0),
          new Vector3(0, 3 / 16.0, 16 / 16.0),
          new Vector4(53 / 64.0, 56 / 64.0, 40 / 64.0, 43 / 64.0)),
      // Leg 2 (right when facing front end).
      new Quad(
          new Vector3(13 / 16.0, 0, 13 / 16.0),
          new Vector3(16 / 16.0, 0, 13 / 16.0),
          new Vector3(13 / 16.0, 0, 16 / 16.0),
          new Vector4(56 / 64.0, 59 / 64.0, 61 / 64.0, 64 / 64.0)),
      new Quad(
          new Vector3(16 / 16.0, 0, 16 / 16.0),
          new Vector3(16 / 16.0, 0, 13 / 16.0),
          new Vector3(16 / 16.0, 3 / 16.0, 16 / 16.0),
          new Vector4(53 / 64.0, 56 / 64.0, 52 / 64.0, 55 / 64.0)),
      new Quad(
          new Vector3(13 / 16.0, 0, 13 / 16.0),
          new Vector3(13 / 16.0, 0, 16 / 16.0),
          new Vector3(13 / 16.0, 3 / 16.0, 13 / 16.0),
          new Vector4(59 / 64.0, 62 / 64.0, 52 / 64.0, 55 / 64.0)),
      new Quad(
          new Vector3(16 / 16.0, 0, 13 / 16.0),
          new Vector3(13 / 16.0, 0, 13 / 16.0),
          new Vector3(16 / 16.0, 3 / 16.0, 13 / 16.0),
          new Vector4(56 / 64.0, 59 / 64.0, 52 / 64.0, 55 / 64.0)),
      new Quad(
          new Vector3(13 / 16.0, 0, 16 / 16.0),
          new Vector3(16 / 16.0, 0, 16 / 16.0),
          new Vector3(13 / 16.0, 3 / 16.0, 16 / 16.0),
          new Vector4(50 / 64.0, 53 / 64.0, 52 / 64.0, 55 / 64.0)),
   };
  //endregion

  private final Texture[] textures;
  private final Quad[] quads;

  public BedModel(boolean isHead, int facing, Texture color) {
    quads = Model.rotateY(isHead ? head : foot, -Math.toRadians(90 * facing));

    textures = new Texture[quads.length];
    Arrays.fill(textures, color);
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
