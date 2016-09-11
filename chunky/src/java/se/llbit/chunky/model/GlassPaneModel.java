/* Copyright (c) 2012-2015 Jesper Öqvist <jesper@llbit.se>
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
import se.llbit.chunky.world.BlockData;
import se.llbit.math.AABB;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class GlassPaneModel {
  private static AABB core = new AABB(7 / 16., 9 / 16., 0, 1, 7 / 16., 9 / 16.);

  private static Quad[][] connector = {
      // Front side.
      {
          // Left face.
          new Quad(new Vector3(7 / 16., 1, 7 / 16.), new Vector3(7 / 16., 1, 0),
              new Vector3(7 / 16., 0, 7 / 16.), new Vector4(7 / 16., 0, 1, 0)),

          // Right face.
          new Quad(new Vector3(9 / 16., 1, 0), new Vector3(9 / 16., 1, 7 / 16.),
              new Vector3(9 / 16., 0, 0), new Vector4(0, 7 / 16., 1, 0)),

          // Top face.
          new Quad(new Vector3(9 / 16., 1, 0), new Vector3(7 / 16., 1, 0),
              new Vector3(9 / 16., 1, 7 / 16.), new Vector4(9 / 16., 7 / 16., 0, 7 / 16.)),

          // Bottom face.
          new Quad(new Vector3(7 / 16., 0, 0), new Vector3(9 / 16., 0, 0),
              new Vector3(7 / 16., 0, 7 / 16.), new Vector4(7 / 16., 9 / 16., 0, 7 / 16.)),

      },
      // Back side.
      {
          // Left face.
          new Quad(new Vector3(7 / 16., 1, 1), new Vector3(7 / 16., 1, 9 / 16.),
              new Vector3(7 / 16., 0, 1), new Vector4(1, 9 / 16., 1, 0)),

          // Right face.
          new Quad(new Vector3(9 / 16., 1, 9 / 16.), new Vector3(9 / 16., 1, 1),
              new Vector3(9 / 16., 0, 9 / 16.), new Vector4(9 / 16., 1, 1, 0)),

          // Top face.
          new Quad(new Vector3(9 / 16., 1, 9 / 16.), new Vector3(7 / 16., 1, 9 / 16.),
              new Vector3(9 / 16., 1, 1), new Vector4(9 / 16., 7 / 16., 9 / 16., 1)),

          // Bottom face.
          new Quad(new Vector3(7 / 16., 0, 9 / 16.), new Vector3(9 / 16., 0, 9 / 16.),
              new Vector3(7 / 16., 0, 1), new Vector4(7 / 16., 9 / 16., 9 / 16., 1)),},};

  private static Quad[][] panes = new Quad[4][];

  static {
    panes[0] = connector[0];
    panes[1] = connector[1];
    for (int j = 2; j < 4; ++j) {
      panes[j] = Model.rotateY(connector[j - 2]);
    }
  }

  public static boolean intersect(Ray ray, Texture sideTexture, Texture topTexture) {
    int metadata = 0xF & (ray.getCurrentData() >> BlockData.GLASS_PANE_OFFSET);
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    if (core.intersect(ray)) {
      if (ray.n.y > 0 || ray.n.y < 0) {
        topTexture.getColor(ray);
      } else {
        sideTexture.getColor(ray);
      }
      ray.n.scale(QuickMath.signum(-ray.d.dot(ray.n)));
      ray.t = ray.tNext;
      hit = true;
    }
    for (int i = 0; i < 4; ++i) {
      if ((metadata & (1 << i)) != 0) {
        for (int j = 0; j < panes[i].length; ++j) {
          Quad quad = panes[i][j];
          if (quad.intersect(ray)) {
            if (j < 2) {
              sideTexture.getColor(ray);
            } else {
              topTexture.getColor(ray);
            }
            ray.n.set(quad.n);
            ray.t = ray.tNext;
            hit = true;
          }
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
