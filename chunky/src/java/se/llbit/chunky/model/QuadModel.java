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

package se.llbit.chunky.model;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Constants;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Quad;
import se.llbit.math.Ray2;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Random;

/**
 * A block model that is made out of textured quads.
 */
@PluginApi
public abstract class QuadModel implements BlockModel {

  // For some visualizations, see this PR: https://github.com/chunky-dev/chunky/pull/1603
  public static final Quad FULL_BLOCK_NORTH_SIDE = new Quad(
    new Vector3(1, 0, 0),
    new Vector3(0, 0, 0),
    new Vector3(1, 1, 0),
    new Vector4(0, 1, 0, 1));
  public static final Quad FULL_BLOCK_SOUTH_SIDE = new Quad(
    new Vector3(0, 0, 1),
    new Vector3(1, 0, 1),
    new Vector3(0, 1, 1),
    new Vector4(0, 1, 0, 1));
  public static final Quad FULL_BLOCK_WEST_SIDE = new Quad(
    new Vector3(0, 0, 0),
    new Vector3(0, 0, 1),
    new Vector3(0, 1, 0),
    new Vector4(0, 1, 0, 1));
  public static final Quad FULL_BLOCK_EAST_SIDE = new Quad(
    new Vector3(1, 0, 1),
    new Vector3(1, 0, 0),
    new Vector3(1, 1, 1),
    new Vector4(0, 1, 0, 1));
  public static final Quad FULL_BLOCK_TOP_SIDE = new Quad(
    new Vector3(1, 1, 0),
    new Vector3(0, 1, 0),
    new Vector3(1, 1, 1),
    new Vector4(1, 0, 1, 0));
  public static final Quad FULL_BLOCK_BOTTOM_SIDE = new Quad(
    new Vector3(0, 0, 0),
    new Vector3(1, 0, 0),
    new Vector3(0, 0, 1),
    new Vector4(0, 1, 0, 1));

  public static final Quad[] FULL_BLOCK_QUADS = {
    FULL_BLOCK_NORTH_SIDE, FULL_BLOCK_SOUTH_SIDE,
    FULL_BLOCK_WEST_SIDE, FULL_BLOCK_EAST_SIDE,
    FULL_BLOCK_BOTTOM_SIDE, FULL_BLOCK_TOP_SIDE
  };

  public boolean refractive = false;

  @PluginApi
  public abstract Quad[] getQuads();

  @PluginApi
  public abstract Texture[] getTextures();

  @PluginApi
  public Tint[] getTints() {
    return null;
  }

  @Override
  public int faceCount() {
    return getQuads().length;
  }

  @Override
  public void sample(int face, Vector3 loc, Random rand) {
    getQuads()[face % faceCount()].sample(loc, rand);
  }

  @Override
  public double faceSurfaceArea(int face) {
    return getQuads()[face % faceCount()].surfaceArea();
  }

  @Override
  public boolean intersect(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene) {
    boolean hit = false;

    Quad[] quads = getQuads();
    Texture[] textures = getTextures();
    Tint[] tintedQuads = getTints();

    float[] color = null;
    Tint tint = Tint.NONE;
    if (refractive) {
      for (int i = 0; i < quads.length; ++i) {
        Quad quad = quads[i];
        if (quad.closestIntersection(ray, intersectionRecord)) {
          if (ray.d.dot(quad.n) < 0) {
            float[] c = textures[i].getColor(intersectionRecord.uv.x, intersectionRecord.uv.y);
            if (c[3] > Constants.EPSILON) {
              tint = tintedQuads == null ? Tint.NONE : tintedQuads[i];
              color = c;
            } else {
              tint = Tint.NONE;
              color = new float[] {1, 1, 1, 0};
            }
          } else {
            tint = Tint.NONE;
            color = new float[] {1, 1, 1, 0};
          }
          hit = true;
          intersectionRecord.setNormal(quad.n);
        }
      }
    } else {
      for (int i = 0; i < quads.length; ++i) {
        Quad quad = quads[i];
        double distance = intersectionRecord.distance;
        if (quad.closestIntersection(ray, intersectionRecord)) {
          float[] c = textures[i].getColor(intersectionRecord.uv.x, intersectionRecord.uv.y);
          if (c[3] > Constants.EPSILON) {
            tint = tintedQuads == null ? Tint.NONE : tintedQuads[i];
            color = c;
            if (quad.doubleSided) {
              intersectionRecord.setNormal(Vector3.orientNormal(ray.d, quad.n));
            } else {
              intersectionRecord.setNormal(quad.n);
            }
            intersectionRecord.flags |= IntersectionRecord.NO_MEDIUM_CHANGE;
            hit = true;
          } else {
            intersectionRecord.distance = distance;
          }
        }
      }
    }

    if (hit) {
      intersectionRecord.color.set(color);
      tint.tint(intersectionRecord.color, ray, scene);
    }
    return hit;
  }

  @Override
  public boolean isInside(Ray2 ray) {
    if (!refractive) {
      return false;
    }

    IntersectionRecord intersectionTest = new IntersectionRecord();

    Quad[] quads = getQuads();
    boolean hit = false;
    for (Quad quad : quads) {
      if (quad.closestIntersection(ray, intersectionTest)) {
        hit = true;
      }
    }
    if (hit) {
      return ray.d.dot(intersectionTest.n) > 0;
    }
    return false;
  }
}
