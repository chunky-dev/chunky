/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
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

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.AnimatedTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.BlockData;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.util.MinecraftPRNG;

public class FireModel extends QuadModel {
  private final static Quad[] quads = {
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 1), new Vector3(0, 1, 0),
          new Vector4(0, 1, 0, 1)),

      new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 1), new Vector3(1, 1, 0),
          new Vector4(0, 1, 0, 1)),

      new Quad(new Vector3(1, 0, 1), new Vector3(0, 0, 0), new Vector3(1, 1, 1),
          new Vector4(1, 0, 0, 1)),

      new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 0), new Vector3(0, 1, 1),
          new Vector4(1, 0, 0, 1)),
  };

  private final Texture[] textures;

  public FireModel(AnimatedTexture tex0, AnimatedTexture tex1) {
    //TODO: Animated textures
    this.textures = new Texture[] {tex0, tex1, tex0, tex1};
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return intersect(ray, (AnimatedTexture[]) textures, scene.getAnimationTime());
  }

  public static boolean intersect(Ray ray, AnimatedTexture[] texture, double time) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    Vector3 position = new Vector3(ray.o);
    position.scaleAdd(Ray.OFFSET, ray.d);
    int i = (0xF & (ray.getCurrentData() >> BlockData.LILY_PAD_ROTATION))
        + (int) Math.floorMod(MinecraftPRNG.rand((long) position.x, (long) position.y, (long) position.z), Integer.MAX_VALUE)
        + (int) (time * 20);  // Fire animates at 20 fps
    int j = 0;
    for (Quad quad : quads) {
      if (quad.intersect(ray)) {
        float[] color = texture[j].getColor(ray.u, ray.v, i);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.setN(quad.n);
          hit = true;
        }
      }
      j = 1 - j;
    }
    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }
}
