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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.UVTriangle;
import se.llbit.math.Vector3;

/**
 * Utility methods for quads and triangles.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class Model {

  /**
   * @param src source quads
   * @return Quads rotated minus 90 degrees around the X axis
   */
  public static Quad[] rotateNegX(Quad[] src) {
    Quad[] rot = new Quad[src.length];
    for (int i = 0; i < src.length; ++i) {
      rot[i] = src[i].transform(Transform.NONE.rotateNegX());
    }
    return rot;
  }

  /**
   * @param src source quads
   * @return Quads rotated 90 degrees around the X axis
   */
  public static Quad[] rotateX(Quad[] src) {
    Quad[] rot = new Quad[src.length];
    for (int i = 0; i < src.length; ++i) {
      rot[i] = src[i].transform(Transform.NONE.rotateX());
    }
    return rot;
  }

  /**
   * @param src source quads
   * @return Quads rotated about the X axis by some angle
   */
  public static Quad[] rotateX(Quad[] src, double angle) {
    Quad[] rot = new Quad[src.length];
    for (int i = 0; i < src.length; ++i) {
      rot[i] = src[i].transform(Transform.NONE.rotateX(angle));
    }
    return rot;
  }

  /**
   * @param src source quads
   * @return Quads rotated about the X axis by some angle
   */
  public static Quad[] rotateX(Quad[] src, double angle, Vector3 origin) {
    Quad[] rot = new Quad[src.length];
    for (int i = 0; i < src.length; ++i) {
      rot[i] = src[i].transform(Transform.NONE
          .translate(-origin.x + 0.5, -origin.y + 0.5, -origin.z + 0.5)
          .rotateX(angle)
          .translate(origin.x - 0.5, origin.y - 0.5, origin.z - 0.5));
    }
    return rot;
  }

  /**
   * @param src source quads
   * @return Quads rotated 90 degrees around the Y axis
   */
  public static Quad[] rotateY(Quad[] src) {
    Quad[] rot = new Quad[src.length];
    for (int i = 0; i < src.length; ++i) {
      rot[i] = src[i].transform(Transform.NONE.rotateY());
    }
    return rot;
  }

  /**
   * @param src source quads
   * @return Quads rotated 0, 90, 180 and 270 degrees around the Y axis
   */
  public static Quad[][] rotateYNESW(Quad[] src) {
    Quad[][] rot = new Quad[4][];
    rot[0] = src;
    rot[1] = rotateY(rot[0]);
    rot[2] = rotateY(rot[1]);
    rot[3] = rotateY(rot[2]);
    return rot;
  }

  /**
   * @param src source quads
   * @return Quads rotated 90 degrees around the negative Y axis
   */
  public static Quad[] rotateNegY(Quad[] src) {
    Quad[] rot = new Quad[src.length];
    for (int i = 0; i < src.length; ++i) {
      rot[i] = src[i].transform(Transform.NONE.rotateNegY());
    }
    return rot;
  }

  /**
   * @param src source quads
   * @return UVTriangles rotated about the Y axis
   */
  public static UVTriangle[] rotateY(UVTriangle[] src) {
    UVTriangle[] rot = new UVTriangle[src.length];
    for (int i = 0; i < src.length; ++i) {
      rot[i] = src[i].getYRotated();
    }
    return rot;
  }

  /**
   * @param src source quads
   * @return Quads rotated about the Y axis by some angle
   */
  public static Quad[] rotateY(Quad[] src, double angle) {
    Quad[] rot = new Quad[src.length];
    for (int i = 0; i < src.length; ++i) {
      rot[i] = src[i].transform(Transform.NONE.rotateY(angle));
    }
    return rot;
  }

  /**
   * @param src    source quads
   * @param origin origin of the rotation axis, relative to the center of a block
   * @return Quads rotated about the Y axis by some angle around the given origin
   */
  public static Quad[] rotateY(Quad[] src, double angle, Vector3 origin) {
    Quad[] rot = new Quad[src.length];
    for (int i = 0; i < src.length; ++i) {
      rot[i] = src[i]
          .transform(Transform.NONE.translate(-origin.x, -origin.y, -origin.z).rotateY(angle)
              .translate(origin));
    }
    return rot;
  }

  /**
   * @param src source quads
   * @return Quads rotated about the Z axis
   */
  public static Quad[] rotateZ(Quad[] src) {
    Quad[] rot = new Quad[src.length];
    for (int i = 0; i < src.length; ++i) {
      rot[i] = src[i].transform(Transform.NONE.rotateZ());
    }
    return rot;
  }

  /**
   * @param src source quads
   * @return Quads rotated about the Z axis by some angle
   */
  public static Quad[] rotateZ(Quad[] src, double angle) {
    Quad[] rot = new Quad[src.length];
    for (int i = 0; i < src.length; ++i) {
      rot[i] = src[i].transform(Transform.NONE.rotateZ(angle));
    }
    return rot;
  }

  /**
   * @param src source quads
   * @param x   Distance to translate along the X axis
   * @param y   Distance to translate along the Y axis
   * @param z   Distance to translate along the Z axis
   * @return Translated copies of the source quads
   */
  public static Quad[] translate(Quad[] src, double x, double y, double z) {
    Quad[] out = new Quad[src.length];
    for (int i = 0; i < src.length; ++i) {
      out[i] = src[i].transform(Transform.NONE.translate(x, y, z));
    }
    return out;
  }

  /**
   * @param src source quads
   * @return Scaled copies of the source quads
   */
  public static Quad[] scale(Quad[] src, double scale) {
    Quad[] out = new Quad[src.length];
    for (int i = 0; i < src.length; ++i) {
      out[i] = src[i].getScaled(scale);
    }
    return out;
  }

  /**
   * @param models source quads
   * @return All quads merged into a single array
   */
  public static Quad[] join(Quad[]... models) {
    List<Quad> all = new ArrayList<>();
    for (Quad[] quads : models) {
      Collections.addAll(all, quads);
    }
    return all.toArray(new Quad[all.size()]);
  }
}
