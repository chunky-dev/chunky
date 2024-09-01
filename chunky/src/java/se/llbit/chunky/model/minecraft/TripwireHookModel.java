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

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class TripwireHookModel extends QuadModel {

  private static final AbstractTexture hookT = Texture.tripwireHook;
  private static final AbstractTexture wood = Texture.oakPlanks;
  private static final AbstractTexture tripwire = Texture.tripwire;

  private static final AbstractTexture[] tex = new AbstractTexture[]{
      hookT, hookT, hookT, hookT, hookT, hookT, hookT, hookT, hookT, hookT,
      wood, wood, wood, wood, wood, wood, wood, wood, wood, wood, wood, wood
  };

  //region Model
  private static final Quad[][] model = Model.rotateYNESW(Model.join(
      Model.rotateX(new Quad[]{
          new Quad(
              new Vector3(6.2 / 16.0, 4.6 / 16.0, 11.5 / 16.0),
              new Vector3(9.8 / 16.0, 4.6 / 16.0, 11.5 / 16.0),
              new Vector3(6.2 / 16.0, 4.6 / 16.0, 7.9 / 16.0),
              new Vector4(5 / 16.0, 11 / 16.0, 7 / 16.0, 13 / 16.0)
          ),
          new Quad(
              new Vector3(6.2 / 16.0, 3.8 / 16.0, 7.9 / 16.0),
              new Vector3(9.8 / 16.0, 3.8 / 16.0, 7.9 / 16.0),
              new Vector3(6.2 / 16.0, 3.8 / 16.0, 11.5 / 16.0),
              new Vector4(5 / 16.0, 11 / 16.0, 7 / 16.0, 13 / 16.0)
          ),
          new Quad(
              new Vector3(6.2 / 16.0, 4.6 / 16.0, 11.5 / 16.0),
              new Vector3(6.2 / 16.0, 4.6 / 16.0, 7.9 / 16.0),
              new Vector3(6.2 / 16.0, 3.8 / 16.0, 11.5 / 16.0),
              new Vector4(11 / 16.0, 5 / 16.0, 8 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(9.8 / 16.0, 4.6 / 16.0, 7.9 / 16.0),
              new Vector3(9.8 / 16.0, 4.6 / 16.0, 11.5 / 16.0),
              new Vector3(9.8 / 16.0, 3.8 / 16.0, 7.9 / 16.0),
              new Vector4(11 / 16.0, 5 / 16.0, 13 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(6.2 / 16.0, 4.6 / 16.0, 7.9 / 16.0),
              new Vector3(9.8 / 16.0, 4.6 / 16.0, 7.9 / 16.0),
              new Vector3(6.2 / 16.0, 3.8 / 16.0, 7.9 / 16.0),
              new Vector4(11 / 16.0, 5 / 16.0, 13 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(9.8 / 16.0, 4.6 / 16.0, 11.5 / 16.0),
              new Vector3(6.2 / 16.0, 4.6 / 16.0, 11.5 / 16.0),
              new Vector3(9.8 / 16.0, 3.8 / 16.0, 11.5 / 16.0),
              new Vector4(11 / 16.0, 5 / 16.0, 8 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 4.6 / 16.0, 10.3 / 16.0),
              new Vector3(8.6 / 16.0, 4.6 / 16.0, 10.3 / 16.0),
              new Vector3(7.4 / 16.0, 3.8 / 16.0, 10.3 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 8 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(8.6 / 16.0, 4.6 / 16.0, 9.1 / 16.0),
              new Vector3(7.4 / 16.0, 4.6 / 16.0, 9.1 / 16.0),
              new Vector3(8.6 / 16.0, 3.8 / 16.0, 9.1 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 13 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 4.6 / 16.0, 9.1 / 16.0),
              new Vector3(7.4 / 16.0, 4.6 / 16.0, 10.3 / 16.0),
              new Vector3(7.4 / 16.0, 3.8 / 16.0, 9.1 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 8 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(8.6 / 16.0, 4.6 / 16.0, 10.3 / 16.0),
              new Vector3(8.6 / 16.0, 4.6 / 16.0, 9.1 / 16.0),
              new Vector3(8.6 / 16.0, 3.8 / 16.0, 10.3 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 13 / 16.0, 12 / 16.0)
          )}, Math.toRadians(-45), new Vector3(0.5, 6 / 16.0, 5.2 / 16.0)),
      Model.rotateX(new Quad[]{
          new Quad(
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector4(7 / 16.0, 9 / 16.0, 9 / 16.0, 14 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 5.2 / 16.0, 10 / 16.0),
              new Vector3(8.8 / 16.0, 5.2 / 16.0, 10 / 16.0),
              new Vector3(7.4 / 16.0, 5.2 / 16.0, 14 / 16.0),
              new Vector4(7 / 16.0, 9 / 16.0, 2 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector3(7.4 / 16.0, 5.2 / 16.0, 14 / 16.0),
              new Vector4(7 / 16.0, 2 / 16.0, 7 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(8.8 / 16.0, 5.2 / 16.0, 10 / 16.0),
              new Vector4(14 / 16.0, 9 / 16.0, 7 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector3(7.4 / 16.0, 5.2 / 16.0, 10 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 7 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(8.8 / 16.0, 5.2 / 16.0, 14 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 7 / 16.0, 5 / 16.0)
          )}, Math.toRadians(45), new Vector3(0.5, 6 / 16.0, 14 / 16.0)),
      new Quad[]{
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector4(6 / 16.0, 10 / 16.0, 14 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(6 / 16.0, 1 / 16.0, 14 / 16.0),
              new Vector3(10 / 16.0, 1 / 16.0, 14 / 16.0),
              new Vector3(6 / 16.0, 1 / 16.0, 16 / 16.0),
              new Vector4(6 / 16.0, 10 / 16.0, 0 / 16.0, 2 / 16.0)
          ),
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(6 / 16.0, 1 / 16.0, 16 / 16.0),
              new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 1 / 16.0)
          ),
          new Quad(
              new Vector3(10 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(10 / 16.0, 1 / 16.0, 14 / 16.0),
              new Vector4(16 / 16.0, 14 / 16.0, 9 / 16.0, 1 / 16.0)
          ),
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(6 / 16.0, 1 / 16.0, 14 / 16.0),
              new Vector4(10 / 16.0, 6 / 16.0, 9 / 16.0, 1 / 16.0)
          ),
          new Quad(
              new Vector3(10 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(10 / 16.0, 1 / 16.0, 16 / 16.0),
              new Vector4(10 / 16.0, 6 / 16.0, 9 / 16.0, 1 / 16.0)
          )
      }));
  //endregion

  //region Powered
  private static final Quad[][] quadsPowered = Model.rotateYNESW(Model.join(new Quad[]{
          new Quad(
              new Vector3(6.2 / 16.0, 5 / 16.0, 10.3 / 16.0),
              new Vector3(9.8 / 16.0, 5 / 16.0, 10.3 / 16.0),
              new Vector3(6.2 / 16.0, 5 / 16.0, 6.7 / 16.0),
              new Vector4(5 / 16.0, 11 / 16.0, 7 / 16.0, 13 / 16.0)
          ),
          new Quad(
              new Vector3(6.2 / 16.0, 4.2 / 16.0, 6.7 / 16.0),
              new Vector3(9.8 / 16.0, 4.2 / 16.0, 6.7 / 16.0),
              new Vector3(6.2 / 16.0, 4.2 / 16.0, 10.3 / 16.0),
              new Vector4(5 / 16.0, 11 / 16.0, 7 / 16.0, 13 / 16.0)
          ),
          new Quad(
              new Vector3(6.2 / 16.0, 5 / 16.0, 10.3 / 16.0),
              new Vector3(6.2 / 16.0, 5 / 16.0, 6.7 / 16.0),
              new Vector3(6.2 / 16.0, 4.2 / 16.0, 10.3 / 16.0),
              new Vector4(11 / 16.0, 5 / 16.0, 8 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(9.8 / 16.0, 5 / 16.0, 6.7 / 16.0),
              new Vector3(9.8 / 16.0, 5 / 16.0, 10.3 / 16.0),
              new Vector3(9.8 / 16.0, 4.2 / 16.0, 6.7 / 16.0),
              new Vector4(11 / 16.0, 5 / 16.0, 13 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(6.2 / 16.0, 5 / 16.0, 6.7 / 16.0),
              new Vector3(9.8 / 16.0, 5 / 16.0, 6.7 / 16.0),
              new Vector3(6.2 / 16.0, 4.2 / 16.0, 6.7 / 16.0),
              new Vector4(11 / 16.0, 5 / 16.0, 13 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(9.8 / 16.0, 5 / 16.0, 10.3 / 16.0),
              new Vector3(6.2 / 16.0, 5 / 16.0, 10.3 / 16.0),
              new Vector3(9.8 / 16.0, 4.2 / 16.0, 10.3 / 16.0),
              new Vector4(11 / 16.0, 5 / 16.0, 8 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 5 / 16.0, 9.1 / 16.0),
              new Vector3(8.6 / 16.0, 5 / 16.0, 9.1 / 16.0),
              new Vector3(7.4 / 16.0, 4.2 / 16.0, 9.1 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 8 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(8.6 / 16.0, 5 / 16.0, 7.9 / 16.0),
              new Vector3(7.4 / 16.0, 5 / 16.0, 7.9 / 16.0),
              new Vector3(8.6 / 16.0, 4.2 / 16.0, 7.9 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 13 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 5 / 16.0, 7.9 / 16.0),
              new Vector3(7.4 / 16.0, 5 / 16.0, 9.1 / 16.0),
              new Vector3(7.4 / 16.0, 4.2 / 16.0, 7.9 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 8 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(8.6 / 16.0, 5 / 16.0, 9.1 / 16.0),
              new Vector3(8.6 / 16.0, 5 / 16.0, 7.9 / 16.0),
              new Vector3(8.6 / 16.0, 4.2 / 16.0, 9.1 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 13 / 16.0, 12 / 16.0)
          )},
      Model.rotateX(new Quad[]{
          new Quad(
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector4(7 / 16.0, 9 / 16.0, 9 / 16.0, 14 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 5.2 / 16.0, 10 / 16.0),
              new Vector3(8.8 / 16.0, 5.2 / 16.0, 10 / 16.0),
              new Vector3(7.4 / 16.0, 5.2 / 16.0, 14 / 16.0),
              new Vector4(7 / 16.0, 9 / 16.0, 2 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector3(7.4 / 16.0, 5.2 / 16.0, 14 / 16.0),
              new Vector4(7 / 16.0, 2 / 16.0, 7 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(8.8 / 16.0, 5.2 / 16.0, 10 / 16.0),
              new Vector4(14 / 16.0, 9 / 16.0, 7 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector3(7.4 / 16.0, 5.2 / 16.0, 10 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 7 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(8.8 / 16.0, 5.2 / 16.0, 14 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 7 / 16.0, 5 / 16.0)
          )}, Math.toRadians(-22.5), new Vector3(0.5, 6 / 16.0, 14 / 16.0)),
      new Quad[]{
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector4(6 / 16.0, 10 / 16.0, 14 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(6 / 16.0, 1 / 16.0, 14 / 16.0),
              new Vector3(10 / 16.0, 1 / 16.0, 14 / 16.0),
              new Vector3(6 / 16.0, 1 / 16.0, 16 / 16.0),
              new Vector4(6 / 16.0, 10 / 16.0, 0 / 16.0, 2 / 16.0)
          ),
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(6 / 16.0, 1 / 16.0, 16 / 16.0),
              new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 1 / 16.0)
          ),
          new Quad(
              new Vector3(10 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(10 / 16.0, 1 / 16.0, 14 / 16.0),
              new Vector4(16 / 16.0, 14 / 16.0, 9 / 16.0, 1 / 16.0)
          ),
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(6 / 16.0, 1 / 16.0, 14 / 16.0),
              new Vector4(10 / 16.0, 6 / 16.0, 9 / 16.0, 1 / 16.0)
          ),
          new Quad(
              new Vector3(10 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(10 / 16.0, 1 / 16.0, 16 / 16.0),
              new Vector4(10 / 16.0, 6 / 16.0, 9 / 16.0, 1 / 16.0)
          )
      }));
  //endregion

  private static final AbstractTexture[] texAttached = new AbstractTexture[]{
      tripwire, tripwire, hookT, hookT, hookT, hookT, hookT, hookT, hookT, hookT, hookT, hookT,
      wood, wood, wood, wood, wood, wood, wood, wood, wood, wood, wood, wood
  };

  //region Attached
  private static final Quad[][] quadsAttached = Model.rotateYNESW(Model.join(
      Model.rotateX(new Quad[]{
          new Quad(
              new Vector3(8.25 / 16.0, 1.5 / 16.0, 6.7 / 16.0),
              new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
              new Vector3(7.75 / 16.0, 1.5 / 16.0, 6.7 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 8 / 16.0, 10 / 16.0)
          ),
          new Quad(
              new Vector3(8.25 / 16.0, 1.5 / 16.0, 0 / 16.0),
              new Vector3(8.25 / 16.0, 1.5 / 16.0, 6.7 / 16.0),
              new Vector3(7.75 / 16.0, 1.5 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 8 / 16.0, 10 / 16.0)
          ),
      }, Math.toRadians(-22.5), new Vector3(0.5, 0, 0)), // TODO rescale
      Model.rotateX(new Quad[]{
          new Quad(
              new Vector3(6.2 / 16.0, 5 / 16.0, 10.3 / 16.0),
              new Vector3(9.8 / 16.0, 5 / 16.0, 10.3 / 16.0),
              new Vector3(6.2 / 16.0, 5 / 16.0, 6.7 / 16.0),
              new Vector4(5 / 16.0, 11 / 16.0, 7 / 16.0, 13 / 16.0)
          ),
          new Quad(
              new Vector3(6.2 / 16.0, 4.2 / 16.0, 6.7 / 16.0),
              new Vector3(9.8 / 16.0, 4.2 / 16.0, 6.7 / 16.0),
              new Vector3(6.2 / 16.0, 4.2 / 16.0, 10.3 / 16.0),
              new Vector4(5 / 16.0, 11 / 16.0, 7 / 16.0, 13 / 16.0)
          ),
          new Quad(
              new Vector3(6.2 / 16.0, 5 / 16.0, 10.3 / 16.0),
              new Vector3(6.2 / 16.0, 5 / 16.0, 6.7 / 16.0),
              new Vector3(6.2 / 16.0, 4.2 / 16.0, 10.3 / 16.0),
              new Vector4(11 / 16.0, 5 / 16.0, 8 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(9.8 / 16.0, 5 / 16.0, 6.7 / 16.0),
              new Vector3(9.8 / 16.0, 5 / 16.0, 10.3 / 16.0),
              new Vector3(9.8 / 16.0, 4.2 / 16.0, 6.7 / 16.0),
              new Vector4(11 / 16.0, 5 / 16.0, 13 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(6.2 / 16.0, 5 / 16.0, 6.7 / 16.0),
              new Vector3(9.8 / 16.0, 5 / 16.0, 6.7 / 16.0),
              new Vector3(6.2 / 16.0, 4.2 / 16.0, 6.7 / 16.0),
              new Vector4(11 / 16.0, 5 / 16.0, 13 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(9.8 / 16.0, 5 / 16.0, 10.3 / 16.0),
              new Vector3(6.2 / 16.0, 5 / 16.0, 10.3 / 16.0),
              new Vector3(9.8 / 16.0, 4.2 / 16.0, 10.3 / 16.0),
              new Vector4(11 / 16.0, 5 / 16.0, 8 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 5 / 16.0, 9.1 / 16.0),
              new Vector3(8.6 / 16.0, 5 / 16.0, 9.1 / 16.0),
              new Vector3(7.4 / 16.0, 4.2 / 16.0, 9.1 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 8 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(8.6 / 16.0, 5 / 16.0, 7.9 / 16.0),
              new Vector3(7.4 / 16.0, 5 / 16.0, 7.9 / 16.0),
              new Vector3(8.6 / 16.0, 4.2 / 16.0, 7.9 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 13 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 5 / 16.0, 7.9 / 16.0),
              new Vector3(7.4 / 16.0, 5 / 16.0, 9.1 / 16.0),
              new Vector3(7.4 / 16.0, 4.2 / 16.0, 7.9 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 8 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(8.6 / 16.0, 5 / 16.0, 9.1 / 16.0),
              new Vector3(8.6 / 16.0, 5 / 16.0, 7.9 / 16.0),
              new Vector3(8.6 / 16.0, 4.2 / 16.0, 9.1 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 13 / 16.0, 12 / 16.0)
          )
      }, Math.toRadians(-22.5), new Vector3(0.5, 4.2 / 16, 6.7 / 16)),
      new Quad[]{
          new Quad(
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector4(7 / 16.0, 9 / 16.0, 9 / 16.0, 14 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 5.2 / 16.0, 10 / 16.0),
              new Vector3(8.8 / 16.0, 5.2 / 16.0, 10 / 16.0),
              new Vector3(7.4 / 16.0, 5.2 / 16.0, 14 / 16.0),
              new Vector4(7 / 16.0, 9 / 16.0, 2 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector3(7.4 / 16.0, 5.2 / 16.0, 14 / 16.0),
              new Vector4(7 / 16.0, 2 / 16.0, 7 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(8.8 / 16.0, 5.2 / 16.0, 10 / 16.0),
              new Vector4(14 / 16.0, 9 / 16.0, 7 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector3(7.4 / 16.0, 5.2 / 16.0, 10 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 7 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector4(6 / 16.0, 10 / 16.0, 14 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(6 / 16.0, 1 / 16.0, 14 / 16.0),
              new Vector3(10 / 16.0, 1 / 16.0, 14 / 16.0),
              new Vector3(6 / 16.0, 1 / 16.0, 16 / 16.0),
              new Vector4(6 / 16.0, 10 / 16.0, 0 / 16.0, 2 / 16.0)
          ),
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(6 / 16.0, 1 / 16.0, 16 / 16.0),
              new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 1 / 16.0)
          ),
          new Quad(
              new Vector3(10 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(10 / 16.0, 1 / 16.0, 14 / 16.0),
              new Vector4(16 / 16.0, 14 / 16.0, 9 / 16.0, 1 / 16.0)
          ),
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(6 / 16.0, 1 / 16.0, 14 / 16.0),
              new Vector4(10 / 16.0, 6 / 16.0, 9 / 16.0, 1 / 16.0)
          ),
          new Quad(
              new Vector3(10 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(10 / 16.0, 1 / 16.0, 16 / 16.0),
              new Vector4(10 / 16.0, 6 / 16.0, 9 / 16.0, 1 / 16.0)
          )
      }));
  //endregion

  //region Attached Powered
  private static final Quad[][] quadsAttachedPowered = Model.rotateYNESW(Model.join(
      Model.rotateX(new Quad[]{
          new Quad(
              new Vector3(8.25 / 16.0, 0.5 / 16.0, 6.7 / 16.0),
              new Vector3(8.25 / 16.0, 0.5 / 16.0, 0 / 16.0),
              new Vector3(7.75 / 16.0, 0.5 / 16.0, 6.7 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 8 / 16.0, 10 / 16.0)
          ),
          new Quad(
              new Vector3(8.25 / 16.0, 0.5 / 16.0, 0 / 16.0),
              new Vector3(8.25 / 16.0, 0.5 / 16.0, 6.7 / 16.0),
              new Vector3(7.75 / 16.0, 0.5 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 8 / 16.0, 10 / 16.0)
          ),
      }, Math.toRadians(-22.5), new Vector3(0.5, 0, 0)), // TODO rescale
      new Quad[]{
          new Quad(
              new Vector3(6.2 / 16.0, 4.2 / 16.0, 10.3 / 16.0),
              new Vector3(9.8 / 16.0, 4.2 / 16.0, 10.3 / 16.0),
              new Vector3(6.2 / 16.0, 4.2 / 16.0, 6.7 / 16.0),
              new Vector4(5 / 16.0, 11 / 16.0, 7 / 16.0, 13 / 16.0)
          ),
          new Quad(
              new Vector3(6.2 / 16.0, 3.4 / 16.0, 6.7 / 16.0),
              new Vector3(9.8 / 16.0, 3.4 / 16.0, 6.7 / 16.0),
              new Vector3(6.2 / 16.0, 3.4 / 16.0, 10.3 / 16.0),
              new Vector4(5 / 16.0, 11 / 16.0, 7 / 16.0, 13 / 16.0)
          ),
          new Quad(
              new Vector3(6.2 / 16.0, 4.2 / 16.0, 10.3 / 16.0),
              new Vector3(6.2 / 16.0, 4.2 / 16.0, 6.7 / 16.0),
              new Vector3(6.2 / 16.0, 3.4 / 16.0, 10.3 / 16.0),
              new Vector4(11 / 16.0, 5 / 16.0, 8 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(9.8 / 16.0, 4.2 / 16.0, 6.7 / 16.0),
              new Vector3(9.8 / 16.0, 4.2 / 16.0, 10.3 / 16.0),
              new Vector3(9.8 / 16.0, 3.4 / 16.0, 6.7 / 16.0),
              new Vector4(11 / 16.0, 5 / 16.0, 13 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(6.2 / 16.0, 4.2 / 16.0, 6.7 / 16.0),
              new Vector3(9.8 / 16.0, 4.2 / 16.0, 6.7 / 16.0),
              new Vector3(6.2 / 16.0, 3.4 / 16.0, 6.7 / 16.0),
              new Vector4(11 / 16.0, 5 / 16.0, 13 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(9.8 / 16.0, 4.2 / 16.0, 10.3 / 16.0),
              new Vector3(6.2 / 16.0, 4.2 / 16.0, 10.3 / 16.0),
              new Vector3(9.8 / 16.0, 3.4 / 16.0, 10.3 / 16.0),
              new Vector4(11 / 16.0, 5 / 16.0, 8 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 4.2 / 16.0, 9.1 / 16.0),
              new Vector3(8.6 / 16.0, 4.2 / 16.0, 9.1 / 16.0),
              new Vector3(7.4 / 16.0, 3.4 / 16.0, 9.1 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 8 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(8.6 / 16.0, 4.2 / 16.0, 7.9 / 16.0),
              new Vector3(7.4 / 16.0, 4.2 / 16.0, 7.9 / 16.0),
              new Vector3(8.6 / 16.0, 3.4 / 16.0, 7.9 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 13 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 4.2 / 16.0, 7.9 / 16.0),
              new Vector3(7.4 / 16.0, 4.2 / 16.0, 9.1 / 16.0),
              new Vector3(7.4 / 16.0, 3.4 / 16.0, 7.9 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 8 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(8.6 / 16.0, 4.2 / 16.0, 9.1 / 16.0),
              new Vector3(8.6 / 16.0, 4.2 / 16.0, 7.9 / 16.0),
              new Vector3(8.6 / 16.0, 3.4 / 16.0, 9.1 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 13 / 16.0, 12 / 16.0)
          ),
      },
      Model.rotateX(new Quad[]{
          new Quad(
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector4(7 / 16.0, 9 / 16.0, 9 / 16.0, 14 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 5.2 / 16.0, 10 / 16.0),
              new Vector3(8.8 / 16.0, 5.2 / 16.0, 10 / 16.0),
              new Vector3(7.4 / 16.0, 5.2 / 16.0, 14 / 16.0),
              new Vector4(7 / 16.0, 9 / 16.0, 2 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector3(7.4 / 16.0, 5.2 / 16.0, 14 / 16.0),
              new Vector4(7 / 16.0, 2 / 16.0, 7 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(8.8 / 16.0, 5.2 / 16.0, 10 / 16.0),
              new Vector4(14 / 16.0, 9 / 16.0, 7 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 10 / 16.0),
              new Vector3(7.4 / 16.0, 5.2 / 16.0, 10 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 7 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(8.8 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(7.4 / 16.0, 6.8 / 16.0, 14 / 16.0),
              new Vector3(8.8 / 16.0, 5.2 / 16.0, 14 / 16.0),
              new Vector4(9 / 16.0, 7 / 16.0, 7 / 16.0, 5 / 16.0)
          ),
      }, Math.toRadians(-22.5), new Vector3(0.5, 6 / 16.0, 14 / 16.0)),
      new Quad[]{
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector4(6 / 16.0, 10 / 16.0, 14 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(6 / 16.0, 1 / 16.0, 14 / 16.0),
              new Vector3(10 / 16.0, 1 / 16.0, 14 / 16.0),
              new Vector3(6 / 16.0, 1 / 16.0, 16 / 16.0),
              new Vector4(6 / 16.0, 10 / 16.0, 0 / 16.0, 2 / 16.0)
          ),
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(6 / 16.0, 1 / 16.0, 16 / 16.0),
              new Vector4(2 / 16.0, 0 / 16.0, 9 / 16.0, 1 / 16.0)
          ),
          new Quad(
              new Vector3(10 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(10 / 16.0, 1 / 16.0, 14 / 16.0),
              new Vector4(16 / 16.0, 14 / 16.0, 9 / 16.0, 1 / 16.0)
          ),
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 14 / 16.0),
              new Vector3(6 / 16.0, 1 / 16.0, 14 / 16.0),
              new Vector4(10 / 16.0, 6 / 16.0, 9 / 16.0, 1 / 16.0)
          ),
          new Quad(
              new Vector3(10 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 16 / 16.0),
              new Vector3(10 / 16.0, 1 / 16.0, 16 / 16.0),
              new Vector4(10 / 16.0, 6 / 16.0, 9 / 16.0, 1 / 16.0)
          )
      }));
  //endregion

  private final Quad[] quads;
  private final AbstractTexture[] textures;

  public TripwireHookModel(int direction, boolean attached, boolean powered) {
    if (attached) {
      quads = powered ? quadsAttachedPowered[direction] : quadsAttached[direction];
      textures = texAttached;
    } else {
      quads = powered ? quadsPowered[direction] : model[direction];
      textures = tex;
    }
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public AbstractTexture[] getTextures() {
    return textures;
  }
}

