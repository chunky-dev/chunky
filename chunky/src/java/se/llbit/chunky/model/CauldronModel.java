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
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * A cauldron model.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class CauldronModel {
  private static final Quad[] quads = {
      // front
      new Quad(new Vector3(1, 3 / 16., 0), new Vector3(0, 3 / 16., 0), new Vector3(1, 1, 0),
          new Vector4(1, 0, 3 / 16., 1)),

      // back
      new Quad(new Vector3(0, 3 / 16., 1), new Vector3(1, 3 / 16., 1), new Vector3(0, 1, 1),
          new Vector4(0, 1, 3 / 16., 1)),

      // right
      new Quad(new Vector3(0, 3 / 16., 0), new Vector3(0, 3 / 16., 1), new Vector3(0, 1, 0),
          new Vector4(0, 1, 3 / 16., 1)),

      // left
      new Quad(new Vector3(1, 3 / 16., 1), new Vector3(1, 3 / 16., 0), new Vector3(1, 1, 1),
          new Vector4(1, 0, 3 / 16., 1)),

      // top front
      new Quad(new Vector3(1 - 1 / 8., 1, 0), new Vector3(1 / 8., 1, 0),
          new Vector3(1 - 1 / 8., 1, 1 / 8.), new Vector4(1 - 1 / 8., 1 / 8., 0, 1 / 8.)),

      // top back
      new Quad(new Vector3(1 - 1 / 8., 1, 1 - 1 / 8.), new Vector3(1 / 8., 1, 1 - 1 / 8.),
          new Vector3(1 - 1 / 8., 1, 1), new Vector4(1 - 1 / 8., 1 / 8., 1 - 1 / 8., 1)),

      // top left
      new Quad(new Vector3(1, 1, 0), new Vector3(1 - 1 / 8., 1, 0), new Vector3(1, 1, 1),
          new Vector4(1, 1 - 1 / 8., 0, 1)),

      // top right
      new Quad(new Vector3(1 / 8., 1, 0), new Vector3(0, 1, 0), new Vector3(1 / 8., 1, 1),
          new Vector4(1 / 8., 0, 0, 1)),

      // inside back
      new Quad(new Vector3(1 - 1 / 8., 1 / 4., 1 - 1 / 8.),
          new Vector3(1 / 8., 1 / 4., 1 - 1 / 8.), new Vector3(1 - 1 / 8., 1, 1 - 1 / 8.),
          new Vector4(1 - 1 / 8., 1 / 8., 1 / 4., 1)),

      // inside frontt
      new Quad(new Vector3(1 / 8., 1 / 4., 1 / 8.), new Vector3(1 - 1 / 8., 1 / 4., 1 / 8.),
          new Vector3(1 / 8., 1, 1 / 8.), new Vector4(1 / 8., 1 - 1 / 8., 1 / 4., 1)),

      // inside left
      new Quad(new Vector3(1 - 1 / 8., 1 / 4., 1 / 8.),
          new Vector3(1 - 1 / 8., 1 / 4., 1 - 1 / 8.), new Vector3(1 - 1 / 8., 1, 1 / 8.),
          new Vector4(1 / 8., 1 - 1 / 8., 1 / 4., 1)),

      // inside right
      new Quad(new Vector3(1 / 8., 1 / 4., 1 - 1 / 8.), new Vector3(1 / 8., 1 / 4., 1 / 8.),
          new Vector3(1 / 8., 1, 1 - 1 / 8.), new Vector4(1 - 1 / 8., 1 / 8., 1 / 4., 1)),

      // inside center
      new Quad(new Vector3(1 - 1 / 8., 1 / 4., 1 / 8.), new Vector3(1 / 8., 1 / 4., 1 / 8.),
          new Vector3(1 - 1 / 8., 1 / 4., 1 - 1 / 8.),
          new Vector4(1 - 1 / 8., 1 / 8., 1 / 8., 1 - 1 / 8.)),

      // front left leg
      new Quad(new Vector3(1, 0, 0), new Vector3(1 - 1 / 4., 0, 0), new Vector3(1, 3 / 16., 0),
          new Vector4(1, 1 - 1 / 4., 0, 3 / 16.)),

      // front right leg
      new Quad(new Vector3(1 / 4., 0, 0), new Vector3(0, 0, 0), new Vector3(1 / 4., 3 / 16., 0),
          new Vector4(1 / 4., 0, 0, 3 / 16.)),

      // back left leg
      new Quad(new Vector3(1 - 1 / 4., 0, 1), new Vector3(1, 0, 1),
          new Vector3(1 - 1 / 4., 3 / 16., 1), new Vector4(1 - 1 / 4., 1, 0, 3 / 16.)),

      // back right leg
      new Quad(new Vector3(0, 0, 1), new Vector3(1 / 4., 0, 1), new Vector3(0, 3 / 16., 1),
          new Vector4(0, 1 / 4., 0, 3 / 16.)),

      // right front leg
      new Quad(new Vector3(0, 0, 1 - 1 / 4.), new Vector3(0, 0, 1),
          new Vector3(0, 3 / 16., 1 - 1 / 4.), new Vector4(1 - 1 / 4., 1, 3 / 16., 1)),

      // right back leg
      new Quad(new Vector3(0, 0, 0), new Vector3(0, 0, 1 / 4.), new Vector3(0, 3 / 16., 0),
          new Vector4(0, 1 / 4., 3 / 16., 1)),

      // left front leg
      new Quad(new Vector3(1, 0, 1), new Vector3(1, 0, 1 - 1 / 4.), new Vector3(1, 3 / 16., 1),
          new Vector4(1, 1 - 1 / 4., 0, 3 / 16.)),

      // left back leg
      new Quad(new Vector3(1, 0, 1 / 4.), new Vector3(1, 0, 0), new Vector3(1, 3 / 16., 1 / 4.),
          new Vector4(1 / 4., 0, 0, 3 / 16.)),

      // below
      new Quad(new Vector3(0, 3 / 16., 0), new Vector3(1, 3 / 16., 0),
          new Vector3(0, 3 / 16., 1), new Vector4(0, 1, 0, 1)),

/*		// bottom
    new Quad(new Vector3d(0, 0, 0), new Vector3d(1., 0, 0),
				new Vector3d(0, 0, 1), new Vector4d(0, 1, 0, 1)),*/

  };

  private static final Texture[] tex = {
      Texture.cauldronSide, Texture.cauldronSide, Texture.cauldronSide, Texture.cauldronSide,
      Texture.cauldronTop, Texture.cauldronTop, Texture.cauldronTop, Texture.cauldronTop,
      Texture.cauldronSide, Texture.cauldronSide, Texture.cauldronSide, Texture.cauldronSide,
      Texture.cauldronInside, Texture.cauldronSide, Texture.cauldronSide, Texture.cauldronSide,
      Texture.cauldronSide, Texture.cauldronSide, Texture.cauldronSide, Texture.cauldronSide,
      Texture.cauldronSide, Texture.cauldronInside,
  };

  public static boolean intersect(Ray ray) {
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
    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }
}
