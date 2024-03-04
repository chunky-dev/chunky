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

import java.util.Arrays;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class SeaPickleModel extends QuadModel {

  //region seaPickle1
  private static final Quad[] seaPickle1 = {
      // cube1
      new Quad(
          new Vector3(6 / 16.0, 6 / 16.0, 10 / 16.0),
          new Vector3(10 / 16.0, 6 / 16.0, 10 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 6 / 16.0),
          new Vector3(10 / 16.0, 0, 6 / 16.0),
          new Vector3(6 / 16.0, 0, 10 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(10 / 16.0, 0, 10 / 16.0),
          new Vector3(10 / 16.0, 0, 6 / 16.0),
          new Vector3(10 / 16.0, 6 / 16.0, 10 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 6 / 16.0),
          new Vector3(6 / 16.0, 0, 10 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(10 / 16.0, 0, 6 / 16.0),
          new Vector3(6 / 16.0, 0, 6 / 16.0),
          new Vector3(10 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 10 / 16.0),
          new Vector3(10 / 16.0, 0, 10 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 10 / 16.0),
          new Vector4(0, 4 / 16.0, 5 / 16.0, 11 / 16.0)),
      // cube2
      new Quad(
          new Vector3(6 / 16.0, 5.95 / 16.0, 10 / 16.0),
          new Vector3(10 / 16.0, 5.95 / 16.0, 10 / 16.0),
          new Vector3(6 / 16.0, 5.95 / 16.0, 6 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      // cube3
      new Quad(new Quad(
          new Vector3(8.5 / 16.0, 5.2 / 16.0, 8 / 16.0),
          new Vector3(7.5 / 16.0, 5.2 / 16.0, 8 / 16.0),
          new Vector3(8.5 / 16.0, 8.7 / 16.0, 8 / 16.0),
          new Vector4(1 / 16.0, 3 / 16.0, 11 / 16.0, 16 / 16.0)),
          Transform.NONE.translate(0, 0, 0)
              .rotateY(Math.PI / 4.0)
              .translate(0, 0, 0)),
      new Quad(new Quad(
          new Vector3(7.5 / 16.0, 5.2 / 16.0, 8 / 16.0),
          new Vector3(8.5 / 16.0, 5.2 / 16.0, 8 / 16.0),
          new Vector3(7.5 / 16.0, 8.7 / 16.0, 8 / 16.0),
          new Vector4(13 / 16.0, 15 / 16.0, 11 / 16.0, 16 / 16.0)),
          Transform.NONE.translate(0, 0, 0)
              .rotateY(Math.PI / 4.0)
              .translate(0, 0, 0)),
  };
  //endregion

  //region seaPickle1Pickle
  private static final Quad[] seaPickle1Pickle = Model.join(
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(7.5 / 16.0, 8.7 / 16.0, 8 / 16.0),
              new Vector3(8.5 / 16.0, 8.7 / 16.0, 8 / 16.0),
              new Vector3(7.5 / 16.0, 5.2 / 16.0, 8 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(8.5 / 16.0, 8.7 / 16.0, 8 / 16.0),
              new Vector3(7.5 / 16.0, 8.7 / 16.0, 8 / 16.0),
              new Vector3(8.5 / 16.0, 5.2 / 16.0, 8 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(8 / 16.0, 8.7 / 16.0, 8.5 / 16.0),
              new Vector3(8 / 16.0, 8.7 / 16.0, 7.5 / 16.0),
              new Vector3(8 / 16.0, 5.2 / 16.0, 8.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 8.7 / 16.0, 7.5 / 16.0),
              new Vector3(8 / 16.0, 8.7 / 16.0, 8.5 / 16.0),
              new Vector3(8 / 16.0, 5.2 / 16.0, 7.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45)));
  //endregion

  //region seaPickle2
  private static final Quad[] seaPickle2 = {
      // cube1
      new Quad(
          new Vector3(3 / 16.0, 6 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 6 / 16.0, 7 / 16.0),
          new Vector3(3 / 16.0, 6 / 16.0, 3 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(3 / 16.0, 0, 3 / 16.0),
          new Vector3(7 / 16.0, 0, 3 / 16.0),
          new Vector3(3 / 16.0, 0, 7 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(7 / 16.0, 0, 7 / 16.0),
          new Vector3(7 / 16.0, 0, 3 / 16.0),
          new Vector3(7 / 16.0, 6 / 16.0, 7 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(3 / 16.0, 0, 3 / 16.0),
          new Vector3(3 / 16.0, 0, 7 / 16.0),
          new Vector3(3 / 16.0, 6 / 16.0, 3 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(7 / 16.0, 0, 3 / 16.0),
          new Vector3(3 / 16.0, 0, 3 / 16.0),
          new Vector3(7 / 16.0, 6 / 16.0, 3 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(3 / 16.0, 0, 7 / 16.0),
          new Vector3(7 / 16.0, 0, 7 / 16.0),
          new Vector3(3 / 16.0, 6 / 16.0, 7 / 16.0),
          new Vector4(0, 4 / 16.0, 5 / 16.0, 11 / 16.0)),
      // cube2
      new Quad(
          new Vector3(3 / 16.0, 5.95 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 5.95 / 16.0, 7 / 16.0),
          new Vector3(3 / 16.0, 5.95 / 16.0, 3 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      // cube3
      new Quad(
          new Vector3(8 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 8 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(8 / 16.0, 0, 8 / 16.0),
          new Vector3(12 / 16.0, 0, 8 / 16.0),
          new Vector3(8 / 16.0, 0, 12 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(12 / 16.0, 0, 12 / 16.0),
          new Vector3(12 / 16.0, 0, 8 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(8 / 16.0, 0, 8 / 16.0),
          new Vector3(8 / 16.0, 0, 12 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 8 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(12 / 16.0, 0, 8 / 16.0),
          new Vector3(8 / 16.0, 0, 8 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 8 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(8 / 16.0, 0, 12 / 16.0),
          new Vector3(12 / 16.0, 0, 12 / 16.0),
          new Vector3(8 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector4(0, 4 / 16.0, 7 / 16.0, 11 / 16.0)),
      // cube4
      new Quad(
          new Vector3(8 / 16.0, 3.95 / 16.0, 12 / 16.0),
          new Vector3(12 / 16.0, 3.95 / 16.0, 12 / 16.0),
          new Vector3(8 / 16.0, 3.95 / 16.0, 8 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
  };
  //endregion

  //region seaPickle2Pickle
  private static final Quad[] seaPickle2Pickle = Model.join(
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(4.5 / 16.0, 8.7 / 16.0, 5 / 16.0),
              new Vector3(5.5 / 16.0, 8.7 / 16.0, 5 / 16.0),
              new Vector3(4.5 / 16.0, 5.2 / 16.0, 5 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(5.5 / 16.0, 8.7 / 16.0, 5 / 16.0),
              new Vector3(4.5 / 16.0, 8.7 / 16.0, 5 / 16.0),
              new Vector3(5.5 / 16.0, 5.2 / 16.0, 5 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(5 / 16., 5.6 / 16., 5 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(5 / 16.0, 8.7 / 16.0, 5.5 / 16.0),
              new Vector3(5 / 16.0, 8.7 / 16.0, 4.5 / 16.0),
              new Vector3(5 / 16.0, 5.2 / 16.0, 5.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(5 / 16.0, 8.7 / 16.0, 4.5 / 16.0),
              new Vector3(5 / 16.0, 8.7 / 16.0, 5.5 / 16.0),
              new Vector3(5 / 16.0, 5.2 / 16.0, 4.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(5 / 16., 5.6 / 16., 5 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(9.5 / 16.0, 6.7 / 16.0, 10 / 16.0),
              new Vector3(10.5 / 16.0, 6.7 / 16.0, 10 / 16.0),
              new Vector3(9.5 / 16.0, 3.2 / 16.0, 10 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(10.5 / 16.0, 6.7 / 16.0, 10 / 16.0),
              new Vector3(9.5 / 16.0, 6.7 / 16.0, 10 / 16.0),
              new Vector3(10.5 / 16.0, 3.2 / 16.0, 10 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(10 / 16., 8 / 16., 10 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(10 / 16.0, 6.7 / 16.0, 10.5 / 16.0),
              new Vector3(10 / 16.0, 6.7 / 16.0, 9.5 / 16.0),
              new Vector3(10 / 16.0, 3.2 / 16.0, 10.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(10 / 16.0, 6.7 / 16.0, 9.5 / 16.0),
              new Vector3(10 / 16.0, 6.7 / 16.0, 10.5 / 16.0),
              new Vector3(10 / 16.0, 3.2 / 16.0, 9.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(10 / 16., 8 / 16., 10 / 16.)));
  //endregion

  //region seaPickle3
  private static final Quad[] seaPickle3 = {
      // cube1
      new Quad(
          new Vector3(6 / 16.0, 6 / 16.0, 13 / 16.0),
          new Vector3(10 / 16.0, 6 / 16.0, 13 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 9 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 9 / 16.0),
          new Vector3(10 / 16.0, 0, 9 / 16.0),
          new Vector3(6 / 16.0, 0, 13 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(10 / 16.0, 0, 13 / 16.0),
          new Vector3(10 / 16.0, 0, 9 / 16.0),
          new Vector3(10 / 16.0, 6 / 16.0, 13 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 9 / 16.0),
          new Vector3(6 / 16.0, 0, 13 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 9 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(10 / 16.0, 0, 9 / 16.0),
          new Vector3(6 / 16.0, 0, 9 / 16.0),
          new Vector3(10 / 16.0, 6 / 16.0, 9 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 13 / 16.0),
          new Vector3(10 / 16.0, 0, 13 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 13 / 16.0),
          new Vector4(0, 4 / 16.0, 5 / 16.0, 11 / 16.0)),
      // cube2
      new Quad(
          new Vector3(6 / 16.0, 5.95 / 16.0, 13 / 16.0),
          new Vector3(10 / 16.0, 5.95 / 16.0, 13 / 16.0),
          new Vector3(6 / 16.0, 5.95 / 16.0, 9 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      // cube3
      new Quad(
          new Vector3(2 / 16.0, 4 / 16.0, 6 / 16.0),
          new Vector3(6 / 16.0, 4 / 16.0, 6 / 16.0),
          new Vector3(2 / 16.0, 4 / 16.0, 2 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(6 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 6 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 6 / 16.0),
          new Vector3(6 / 16.0, 0, 2 / 16.0),
          new Vector3(6 / 16.0, 4 / 16.0, 6 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 6 / 16.0),
          new Vector3(2 / 16.0, 4 / 16.0, 2 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(6 / 16.0, 4 / 16.0, 2 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 6 / 16.0),
          new Vector3(6 / 16.0, 0, 6 / 16.0),
          new Vector3(2 / 16.0, 4 / 16.0, 6 / 16.0),
          new Vector4(0, 4 / 16.0, 7 / 16.0, 11 / 16.0)),
      // cube4
      new Quad(
          new Vector3(2 / 16.0, 3.95 / 16.0, 6 / 16.0),
          new Vector3(6 / 16.0, 3.95 / 16.0, 6 / 16.0),
          new Vector3(2 / 16.0, 3.95 / 16.0, 2 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      // cube5
      new Quad(
          new Vector3(8 / 16.0, 6 / 16.0, 8 / 16.0),
          new Vector3(12 / 16.0, 6 / 16.0, 8 / 16.0),
          new Vector3(8 / 16.0, 6 / 16.0, 4 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(8 / 16.0, 0, 4 / 16.0),
          new Vector3(12 / 16.0, 0, 4 / 16.0),
          new Vector3(8 / 16.0, 0, 8 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(12 / 16.0, 0, 8 / 16.0),
          new Vector3(12 / 16.0, 0, 4 / 16.0),
          new Vector3(12 / 16.0, 6 / 16.0, 8 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(8 / 16.0, 0, 4 / 16.0),
          new Vector3(8 / 16.0, 0, 8 / 16.0),
          new Vector3(8 / 16.0, 6 / 16.0, 4 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(12 / 16.0, 0, 4 / 16.0),
          new Vector3(8 / 16.0, 0, 4 / 16.0),
          new Vector3(12 / 16.0, 6 / 16.0, 4 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(8 / 16.0, 0, 8 / 16.0),
          new Vector3(12 / 16.0, 0, 8 / 16.0),
          new Vector3(8 / 16.0, 6 / 16.0, 8 / 16.0),
          new Vector4(0, 4 / 16.0, 5 / 16.0, 11 / 16.0)),
      // cube6
      new Quad(
          new Vector3(8 / 16.0, 5.95 / 16.0, 8 / 16.0),
          new Vector3(12 / 16.0, 5.95 / 16.0, 8 / 16.0),
          new Vector3(8 / 16.0, 5.95 / 16.0, 4 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
  };
  //endregion

  //region seaPickle3Pickle
  private static final Quad[] seaPickle3Pickle = Model.join(
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(7.5 / 16.0, 8.7 / 16.0, 11 / 16.0),
              new Vector3(8.5 / 16.0, 8.7 / 16.0, 11 / 16.0),
              new Vector3(7.5 / 16.0, 5.2 / 16.0, 11 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(8.5 / 16.0, 8.7 / 16.0, 11 / 16.0),
              new Vector3(7.5 / 16.0, 8.7 / 16.0, 11 / 16.0),
              new Vector3(8.5 / 16.0, 5.2 / 16.0, 11 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(8 / 16., 8 / 16., 11 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(8 / 16.0, 8.7 / 16.0, 11.5 / 16.0),
              new Vector3(8 / 16.0, 8.7 / 16.0, 10.5 / 16.0),
              new Vector3(8 / 16.0, 5.2 / 16.0, 11.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 8.7 / 16.0, 10.5 / 16.0),
              new Vector3(8 / 16.0, 8.7 / 16.0, 11.5 / 16.0),
              new Vector3(8 / 16.0, 5.2 / 16.0, 10.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(8 / 16., 8 / 16., 11 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(3.5 / 16.0, 6.7 / 16.0, 4 / 16.0),
              new Vector3(4.5 / 16.0, 6.7 / 16.0, 4 / 16.0),
              new Vector3(3.5 / 16.0, 3.2 / 16.0, 4 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(4.5 / 16.0, 6.7 / 16.0, 4 / 16.0),
              new Vector3(3.5 / 16.0, 6.7 / 16.0, 4 / 16.0),
              new Vector3(4.5 / 16.0, 3.2 / 16.0, 4 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(4 / 16., 8 / 16., 4 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(4 / 16.0, 6.7 / 16.0, 4.5 / 16.0),
              new Vector3(4 / 16.0, 6.7 / 16.0, 3.5 / 16.0),
              new Vector3(4 / 16.0, 3.2 / 16.0, 4.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(4 / 16.0, 6.7 / 16.0, 3.5 / 16.0),
              new Vector3(4 / 16.0, 6.7 / 16.0, 4.5 / 16.0),
              new Vector3(4 / 16.0, 3.2 / 16.0, 3.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(4 / 16., 8 / 16., 4 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(9.5 / 16.0, 8.7 / 16.0, 6 / 16.0),
              new Vector3(10.5 / 16.0, 8.7 / 16.0, 6 / 16.0),
              new Vector3(9.5 / 16.0, 5.2 / 16.0, 6 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(10.5 / 16.0, 8.7 / 16.0, 6 / 16.0),
              new Vector3(9.5 / 16.0, 8.7 / 16.0, 6 / 16.0),
              new Vector3(10.5 / 16.0, 5.2 / 16.0, 6 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(10 / 16., 8 / 16., 6 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(10 / 16.0, 8.7 / 16.0, 6.5 / 16.0),
              new Vector3(10 / 16.0, 8.7 / 16.0, 5.5 / 16.0),
              new Vector3(10 / 16.0, 5.2 / 16.0, 6.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(10 / 16.0, 8.7 / 16.0, 5.5 / 16.0),
              new Vector3(10 / 16.0, 8.7 / 16.0, 6.5 / 16.0),
              new Vector3(10 / 16.0, 5.2 / 16.0, 5.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(10 / 16., 8 / 16., 6 / 16.))
  );
  //endregion

  //region seaPickle4
  private static final Quad[] seaPickle4 = {
      // cube1
      new Quad(
          new Vector3(2 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector3(2 / 16.0, 6 / 16.0, 2 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(6 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 6 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 6 / 16.0),
          new Vector3(6 / 16.0, 0, 2 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 6 / 16.0),
          new Vector3(2 / 16.0, 6 / 16.0, 2 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 2 / 16.0),
          new Vector3(2 / 16.0, 0, 2 / 16.0),
          new Vector3(6 / 16.0, 6 / 16.0, 2 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 6 / 16.0),
          new Vector3(6 / 16.0, 0, 6 / 16.0),
          new Vector3(2 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector4(0, 4 / 16.0, 5 / 16.0, 11 / 16.0)),
      // cube2
      new Quad(
          new Vector3(2 / 16.0, 5.95 / 16.0, 6 / 16.0),
          new Vector3(6 / 16.0, 5.95 / 16.0, 6 / 16.0),
          new Vector3(2 / 16.0, 5.95 / 16.0, 2 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      // cube3
      new Quad(
          new Vector3(9 / 16.0, 4 / 16.0, 14 / 16.0),
          new Vector3(13 / 16.0, 4 / 16.0, 14 / 16.0),
          new Vector3(9 / 16.0, 4 / 16.0, 10 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(9 / 16.0, 0, 10 / 16.0),
          new Vector3(13 / 16.0, 0, 10 / 16.0),
          new Vector3(9 / 16.0, 0, 14 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(13 / 16.0, 0, 14 / 16.0),
          new Vector3(13 / 16.0, 0, 10 / 16.0),
          new Vector3(13 / 16.0, 4 / 16.0, 14 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(9 / 16.0, 0, 10 / 16.0),
          new Vector3(9 / 16.0, 0, 14 / 16.0),
          new Vector3(9 / 16.0, 4 / 16.0, 10 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(13 / 16.0, 0, 10 / 16.0),
          new Vector3(9 / 16.0, 0, 10 / 16.0),
          new Vector3(13 / 16.0, 4 / 16.0, 10 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 7 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(9 / 16.0, 0, 14 / 16.0),
          new Vector3(13 / 16.0, 0, 14 / 16.0),
          new Vector3(9 / 16.0, 4 / 16.0, 14 / 16.0),
          new Vector4(0, 4 / 16.0, 7 / 16.0, 11 / 16.0)),
      // cube4
      new Quad(
          new Vector3(9 / 16.0, 3.95 / 16.0, 14 / 16.0),
          new Vector3(13 / 16.0, 3.95 / 16.0, 14 / 16.0),
          new Vector3(9 / 16.0, 3.95 / 16.0, 10 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      // cube5
      new Quad(
          new Vector3(9 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector3(13 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector3(9 / 16.0, 6 / 16.0, 2 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(9 / 16.0, 0, 2 / 16.0),
          new Vector3(13 / 16.0, 0, 2 / 16.0),
          new Vector3(9 / 16.0, 0, 6 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(13 / 16.0, 0, 6 / 16.0),
          new Vector3(13 / 16.0, 0, 2 / 16.0),
          new Vector3(13 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(9 / 16.0, 0, 2 / 16.0),
          new Vector3(9 / 16.0, 0, 6 / 16.0),
          new Vector3(9 / 16.0, 6 / 16.0, 2 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(13 / 16.0, 0, 2 / 16.0),
          new Vector3(9 / 16.0, 0, 2 / 16.0),
          new Vector3(13 / 16.0, 6 / 16.0, 2 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 5 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(9 / 16.0, 0, 6 / 16.0),
          new Vector3(13 / 16.0, 0, 6 / 16.0),
          new Vector3(9 / 16.0, 6 / 16.0, 6 / 16.0),
          new Vector4(0, 4 / 16.0, 5 / 16.0, 11 / 16.0)),
      // cube6
      new Quad(
          new Vector3(9 / 16.0, 5.95 / 16.0, 6 / 16.0),
          new Vector3(13 / 16.0, 5.95 / 16.0, 6 / 16.0),
          new Vector3(9 / 16.0, 5.95 / 16.0, 2 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      // cube7
      new Quad(
          new Vector3(2 / 16.0, 7 / 16.0, 12 / 16.0),
          new Vector3(6 / 16.0, 7 / 16.0, 12 / 16.0),
          new Vector3(2 / 16.0, 7 / 16.0, 8 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 8 / 16.0),
          new Vector3(6 / 16.0, 0, 8 / 16.0),
          new Vector3(2 / 16.0, 0, 12 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 12 / 16.0),
          new Vector3(6 / 16.0, 0, 8 / 16.0),
          new Vector3(6 / 16.0, 7 / 16.0, 12 / 16.0),
          new Vector4(12 / 16.0, 16 / 16.0, 4 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 8 / 16.0),
          new Vector3(2 / 16.0, 0, 12 / 16.0),
          new Vector3(2 / 16.0, 7 / 16.0, 8 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 4 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(6 / 16.0, 0, 8 / 16.0),
          new Vector3(2 / 16.0, 0, 8 / 16.0),
          new Vector3(6 / 16.0, 7 / 16.0, 8 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 4 / 16.0, 11 / 16.0)),
      new Quad(
          new Vector3(2 / 16.0, 0, 12 / 16.0),
          new Vector3(6 / 16.0, 0, 12 / 16.0),
          new Vector3(2 / 16.0, 7 / 16.0, 12 / 16.0),
          new Vector4(0, 4 / 16.0, 4 / 16.0, 11 / 16.0)),
      // cube8
      new Quad(
          new Vector3(2 / 16.0, 6.95 / 16.0, 12 / 16.0),
          new Vector3(6 / 16.0, 6.95 / 16.0, 12 / 16.0),
          new Vector3(2 / 16.0, 6.95 / 16.0, 8 / 16.0),
          new Vector4(8 / 16.0, 12 / 16.0, 11 / 16.0, 15 / 16.0)),
  };
  //endregion

  //region seaPickle4Pickle
  private static final Quad[] seaPickle4Pickle = Model.join(
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(3.5 / 16.0, 8.7 / 16.0, 4 / 16.0),
              new Vector3(4.5 / 16.0, 8.7 / 16.0, 4 / 16.0),
              new Vector3(3.5 / 16.0, 5.2 / 16.0, 4 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(4.5 / 16.0, 8.7 / 16.0, 4 / 16.0),
              new Vector3(3.5 / 16.0, 8.7 / 16.0, 4 / 16.0),
              new Vector3(4.5 / 16.0, 5.2 / 16.0, 4 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(4 / 16., 8 / 16., 4 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(4 / 16.0, 8.7 / 16.0, 4.5 / 16.0),
              new Vector3(4 / 16.0, 8.7 / 16.0, 3.5 / 16.0),
              new Vector3(4 / 16.0, 5.2 / 16.0, 4.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(4 / 16.0, 8.7 / 16.0, 3.5 / 16.0),
              new Vector3(4 / 16.0, 8.7 / 16.0, 4.5 / 16.0),
              new Vector3(4 / 16.0, 5.2 / 16.0, 3.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(4 / 16., 8 / 16., 4 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(10.5 / 16.0, 6.7 / 16.0, 12 / 16.0),
              new Vector3(11.5 / 16.0, 6.7 / 16.0, 12 / 16.0),
              new Vector3(10.5 / 16.0, 3.2 / 16.0, 12 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(11.5 / 16.0, 6.7 / 16.0, 12 / 16.0),
              new Vector3(10.5 / 16.0, 6.7 / 16.0, 12 / 16.0),
              new Vector3(11.5 / 16.0, 3.2 / 16.0, 12 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(11 / 16., 8 / 16., 12 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(11 / 16.0, 6.7 / 16.0, 12.5 / 16.0),
              new Vector3(11 / 16.0, 6.7 / 16.0, 11.5 / 16.0),
              new Vector3(11 / 16.0, 3.2 / 16.0, 12.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(11 / 16.0, 6.7 / 16.0, 11.5 / 16.0),
              new Vector3(11 / 16.0, 6.7 / 16.0, 12.5 / 16.0),
              new Vector3(11 / 16.0, 3.2 / 16.0, 11.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(11 / 16., 8 / 16., 12 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(10.5 / 16.0, 8.7 / 16.0, 4 / 16.0),
              new Vector3(11.5 / 16.0, 8.7 / 16.0, 4 / 16.0),
              new Vector3(10.5 / 16.0, 5.2 / 16.0, 4 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(11.5 / 16.0, 8.7 / 16.0, 4 / 16.0),
              new Vector3(10.5 / 16.0, 8.7 / 16.0, 4 / 16.0),
              new Vector3(11.5 / 16.0, 5.2 / 16.0, 4 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(11 / 16., 8 / 16., 4 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(11 / 16.0, 8.7 / 16.0, 4.5 / 16.0),
              new Vector3(11 / 16.0, 8.7 / 16.0, 3.5 / 16.0),
              new Vector3(11 / 16.0, 5.2 / 16.0, 4.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(11 / 16.0, 8.7 / 16.0, 3.5 / 16.0),
              new Vector3(11 / 16.0, 8.7 / 16.0, 4.5 / 16.0),
              new Vector3(11 / 16.0, 5.2 / 16.0, 3.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(11 / 16., 8 / 16., 4 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(3.5 / 16.0, 9.7 / 16.0, 10 / 16.0),
              new Vector3(4.5 / 16.0, 9.7 / 16.0, 10 / 16.0),
              new Vector3(3.5 / 16.0, 6.2 / 16.0, 10 / 16.0),
              new Vector4(3 / 16.0, 1 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(4.5 / 16.0, 9.7 / 16.0, 10 / 16.0),
              new Vector3(3.5 / 16.0, 9.7 / 16.0, 10 / 16.0),
              new Vector3(4.5 / 16.0, 6.2 / 16.0, 10 / 16.0),
              new Vector4(1 / 16.0, 3 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(4 / 16., 8 / 16., 10 / 16.)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(4 / 16.0, 9.7 / 16.0, 10.5 / 16.0),
              new Vector3(4 / 16.0, 9.7 / 16.0, 9.5 / 16.0),
              new Vector3(4 / 16.0, 6.2 / 16.0, 10.5 / 16.0),
              new Vector4(15 / 16.0, 13 / 16.0, 16 / 16.0, 11 / 16.0)
          ),
          new Quad(
              new Vector3(4 / 16.0, 9.7 / 16.0, 9.5 / 16.0),
              new Vector3(4 / 16.0, 9.7 / 16.0, 10.5 / 16.0),
              new Vector3(4 / 16.0, 6.2 / 16.0, 9.5 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 16 / 16.0, 11 / 16.0)
          )
      }, Math.toRadians(45), new Vector3(4 / 16., 8 / 16., 10 / 16.)));
  //endregion

  private static final Quad[][] pickleModels = {
      seaPickle1,
      seaPickle2,
      seaPickle3,
      seaPickle4,
  };

  private static final Quad[][] picklePickle = {
      seaPickle1Pickle,
      seaPickle2Pickle,
      seaPickle3Pickle,
      seaPickle4Pickle,
  };

  private final Quad[] quads;
  private final Texture[] textures;

  public SeaPickleModel(int pickles, boolean live) {
    quads = Model.join(pickleModels[pickles - 1], live ? picklePickle[pickles - 1] : new Quad[0]);
    textures = new Texture[quads.length];
    Arrays.fill(textures, Texture.seaPickle);
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
