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
import se.llbit.chunky.world.BlockData;
import se.llbit.math.ColorUtil;
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * A lily pad.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class LilyPadModel {
  private static final Quad quad =
      new DoubleSidedQuad(new Vector3(1, .875, 0), new Vector3(0, .875, 0),
          new Vector3(1, .875, 1), new Vector4(1, 0, 0, 1));

  private static final Quad[] rot = new Quad[4];

  private static final int COLOR = 0x009218;
  private static final float[] lilyPadColor = new float[4];

  static {
    rot[0] = quad;
    rot[3] = rot[0].transform(Transform.NONE.rotateY());
    rot[2] = rot[3].transform(Transform.NONE.rotateY());
    rot[1] = rot[2].transform(Transform.NONE.rotateY());
    ColorUtil.getRGBAComponents(COLOR, lilyPadColor);
  }

  public static boolean intersect(Ray ray) {
    ray.t = Double.POSITIVE_INFINITY;
    int dir = 3 & (ray.getCurrentData() >> BlockData.LILY_PAD_ROTATION);
    if (rot[dir].intersect(ray)) {
      float[] color = Texture.lilyPad.getColor(ray.u, ray.v);
      if (color[3] > Ray.EPSILON) {
        ray.color.set(color);
        ray.color.x *= lilyPadColor[0];
        ray.color.y *= lilyPadColor[1];
        ray.color.z *= lilyPadColor[2];
        ray.setN(0, -QuickMath.signum(ray.d.y), 0);
        ray.distance += ray.tNext;
        ray.o.scaleAdd(ray.tNext, ray.d);
        return true;
      }
    }
    return false;
  }

  public static void getColor(Ray ray) {
    Texture.lilyPad.getColor(ray);
    if (ray.color.w > Ray.EPSILON) {
      ray.color.x *= lilyPadColor[0];
      ray.color.y *= lilyPadColor[1];
      ray.color.z *= lilyPadColor[2];
    }
  }
}
