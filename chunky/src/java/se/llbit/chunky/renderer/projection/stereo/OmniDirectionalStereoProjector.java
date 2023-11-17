/* Copyright (c) 2016-2022 Chunky contributors
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
package se.llbit.chunky.renderer.projection.stereo;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.projection.PanoramicProjector;
import se.llbit.chunky.renderer.projection.Projector;
import se.llbit.math.Constants;
import se.llbit.math.Point3;
import se.llbit.math.Vector3;

import java.util.Random;

/**
 * A projector for Omni-Directional Stereo (ODS) images.
 *
 * <p>This projector, unlike the {@link PanoramicProjector}, can create distinct
 * images for the left and the right hasEye by slightly displacing the view ray origins based on the
 * viewing angle to account for the inter-pupillary distance. This allows to create panoramic
 * stereo images that are perfect for viewing on VR devices.
 *
 * <p>The resulting image will have a complete 360° range, if and only if the canvas' aspect ratio matches the target format.
 * See the implementing classes for details.
 *
 * @see <a href="https://developers.google.com/vr/jump/rendering-ods-content.pdf">Rendering Omni‐directional Stereo Content</a>
 */
public abstract class OmniDirectionalStereoProjector implements Projector {
  /**
   * The inter-pupillary distance of the viewer, in meters.
   */
  private static final double INTERPUPILLARY_DISTANCE = 0.069;

  @Override
  public void apply(double x, double y, Random random, Point3 pos, Vector3 direction) {
    apply(x, y, pos, direction);
  }

  @Override
  public abstract void apply(double x, double y, Point3 pos, Vector3 direction);

  /**
   * @param x 0-1
   * @param y 0-1
   */
  protected void applyLeftEye(double x, double y, Point3 pos, Vector3 direction) {
    apply(x, y, -INTERPUPILLARY_DISTANCE / 2, pos, direction);
  }

  /**
   * @param x 0-1
   * @param y 0-1
   */
  protected void applyRightEye(double x, double y, Point3 pos, Vector3 direction) {
    apply(x, y, INTERPUPILLARY_DISTANCE / 2, pos, direction);
  }

  /**
   * @param x 0-1
   * @param y 0-1
   */
  private void apply(double x, double y, double scale, Point3 pos, Vector3 direction) {
    double theta = x * Math.PI - Constants.HALF_PI;
    double phi = Constants.HALF_PI - y * Math.PI;

    pos.set(
      FastMath.cos(theta) * scale,
      0,
      FastMath.sin(theta) * scale
    );
    direction.set(
      FastMath.sin(theta) * FastMath.cos(phi),
      -FastMath.sin(phi),
      FastMath.cos(theta) * FastMath.cos(phi)
    );
  }

  @Override
  public double getMinRecommendedFoV() {
    return 180;
  }

  @Override
  public double getMaxRecommendedFoV() {
    return 180;
  }

  @Override
  public double getDefaultFoV() {
    return 180;
  }
}
