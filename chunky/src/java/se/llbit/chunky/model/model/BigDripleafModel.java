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

package se.llbit.chunky.model.model;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class BigDripleafModel extends QuadModel {

  //#region big_dripleaf
  private static final Quad[] bigDripleafNorth = Model.join(
      new Quad[]{
          // top
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          // tip
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 15 / 16.0, 0.002 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0.002 / 16.0),
              new Vector3(16 / 16.0, 11 / 16.0, 0.002 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          // side
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 11 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(0.002 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0.002 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0.002 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(15.998 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(15.998 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(15.998 / 16.0, 11 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
          )
      },
      // stem
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )}, Math.toRadians(45), new Vector3(0.5, 0, 12.0 / 16.0)), // TODO rescale
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )}, Math.toRadians(-45), new Vector3(0.5, 0, 12.0 / 16.0)) // TODO rescale
  );
  //#endregion

  //#region big_dripleaf_partial_tilt
  private static final Quad[] bigDripleafPartialTiltNorth = Model.join(
      Model.rotateX(new Quad[]{
          // top
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          // top
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          // side
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 11 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(0.002 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0.002 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0.002 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(15.998 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(15.998 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(15.998 / 16.0, 11 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
          )
      }, Math.toRadians(-22.5), new Vector3(0.5, 15.0 / 16.0, 1)),
      // stem
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )}, Math.toRadians(45), new Vector3(0.5, 0.5, 12.0 / 16.0)), // TODO rescale
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )}, Math.toRadians(-45), new Vector3(0.5, 0.5, 12.0 / 16.0)) // TODO rescale
  );
  //#endregion

  //#region big_dripleaf_full_tilt
  private static final Quad[] bigDripleafFullTiltNorth = Model.join(
      Model.rotateX(new Quad[]{
          // top
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          // tip
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          // side
          new Quad(
              new Vector3(0 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 11 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(0.002 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(0.002 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(0.002 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(15.998 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(15.998 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(15.998 / 16.0, 11 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 15 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 15 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 11 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
          )
      }, Math.toRadians(-45), new Vector3(8 / 16.0, 15 / 16.0, 16 / 16.0)),
      // stem
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )}, Math.toRadians(45), new Vector3(0.5, 0.5, 12.0 / 16.0)), // TODO rescale
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(11 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(5 / 16.0, 15 / 16.0, 12 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 12 / 16.0),
              new Vector4(14 / 16.0, 3 / 16.0, 16 / 16.0, 0 / 16.0)
          )}, Math.toRadians(-45), new Vector3(0.5, 0.5, 12.0 / 16.0)) // TODO rescale
  );
  //#endregion

  private static final Texture[] textures;

  static {
    Texture top = Texture.bigDripleafTop;
    Texture tip = Texture.bigDripleafTip;
    Texture side = Texture.bigDripleafSide;
    Texture stem = Texture.bigDripleafStem;
    textures = new Texture[] {top, top, tip, tip, side, side, side, side, stem, stem, stem, stem};
  }

  private Quad[] quads;

  public BigDripleafModel(String facing, String tilt) {
    switch (tilt) {
      case "partial":
        quads = bigDripleafPartialTiltNorth;
        break;
      case "full":
        quads = bigDripleafFullTiltNorth;
        break;
      case "none":
      case "unstable":
      default:
        quads = bigDripleafNorth;
    }

    switch (facing) {
      case "east":
        quads = Model.rotateY(quads, -Math.toRadians(90));
        break;
      case "south":
        quads = Model.rotateY(quads, -Math.toRadians(180));
        break;
      case "west":
        quads = Model.rotateY(quads, -Math.toRadians(270));
        break;
    }
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }
}
