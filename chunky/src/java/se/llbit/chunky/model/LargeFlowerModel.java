/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class LargeFlowerModel {
  protected static Quad[] quads = {
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 1), new Vector3(0, 1, 0),
          new Vector4(0, 1, 0, 1)),

        new Quad(new Vector3(1, 0, 1), new Vector3(0, 0, 0), new Vector3(1, 1, 1),
            new Vector4(0, 1, 0, 1)),

        new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 1), new Vector3(1, 1, 0),
            new Vector4(0, 1, 0, 1)),

        new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 0), new Vector3(0, 1, 1),
            new Vector4(0, 1, 0, 1)),
  };
  protected static Quad[] sunflower = {
      new Quad(new Vector3(14 / 16., 8 / 16., 2 / 16.), new Vector3(2 / 16., 16 / 16., 2 / 16.),
          new Vector3(14 / 16., 8 / 16., 14 / 16.),
          new Vector4(2 / 16., 14 / 16., 2 / 16., 14 / 16.)),
      new Quad(new Vector3(2 / 16., 16 / 16., 2 / 16.),
          new Vector3(14 / 16., 8 / 16., 2 / 16.), new Vector3(2 / 16., 16 / 16., 14 / 16.),
          new Vector4(2 / 16., 14 / 16., 2 / 16., 14 / 16.)),
  };

  final static Texture[][] tex = {
      { Texture.sunflowerBottom, Texture.sunflowerTop },
      { Texture.lilacBottom, Texture.lilacTop },
      { Texture.doubleTallGrassBottom, Texture.doubleTallGrassTop },
      { Texture.largeFernBottom, Texture.largeFernTop },
      { Texture.roseBushBottom, Texture.roseBushTop },
      { Texture.peonyBottom, Texture.peonyTop },
  };
  final static Texture[] sunflowerTex = {Texture.sunflowerFront, Texture.sunflowerBack};

  public static boolean intersect(Ray ray, Scene scene) {
    int data = ray.getBlockData();
    int kind = (data & 7) % 6;
    int top = (data & 8) >> 3;
    return intersect(ray, scene, kind, top);
  }

  public static boolean intersect(Ray ray, Scene scene,
      int kind, int top) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (Quad quad : quads) {
      if (quad.intersect(ray)) {
        float[] color = tex[kind][top].getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          if (kind == 2 || kind == 3) {
            float[] biomeColor = ray.getBiomeGrassColor(scene);
            ray.color.x *= biomeColor[0];
            ray.color.y *= biomeColor[1];
            ray.color.z *= biomeColor[2];
          }
          ray.t = ray.tNext;
          ray.setNormal(quad.n);
          hit = true;
        }
      }
    }
    if (kind == 0 && top == 1) {
      for (int i = 0; i < sunflower.length; ++i) {
        Quad quad = sunflower[i];
        if (quad.intersect(ray)) {
          float[] color = sunflowerTex[i].getColor(ray.u, ray.v);
          if (color[3] > Ray.EPSILON) {
            ray.color.set(color);
            ray.t = ray.tNext;
            ray.setNormal(quad.n);
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
