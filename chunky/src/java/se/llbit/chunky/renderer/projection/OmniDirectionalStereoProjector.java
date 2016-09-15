/* Copyright (c) 2016 Chunky contributors
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
package se.llbit.chunky.renderer.projection;

import org.apache.commons.math3.util.FastMath;
import se.llbit.math.Vector3;

import java.util.Random;

/**
 * A projector for Omni-Directional Stereo (ODS) images.
 *
 * <p>This projector, unlike the {@link PanoramicProjector}, can create distinct
 * images for the left and the right eye by slightly displacing the view ray origins based on the
 * viewing angle to account for the inter-pupillary distance. This allows to create panoramic
 * stereo images that are perfect for viewing on VR devices.
 *
 * @see <a href="https://developers.google.com/vr/jump/rendering-ods-content.pdf">Rendering Omni‐directional Stereo Content</a>
 */
public class OmniDirectionalStereoProjector implements Projector {
  /**
   * The inter-pupillary distance of the viewer, in meters.
   */
  private static final double interPupillaryDistance = 0.069;

  private final double scale;

  public OmniDirectionalStereoProjector(Eye eye) {
    if (eye == Eye.LEFT) {
      scale = -interPupillaryDistance / 2;
    } else {
      scale = interPupillaryDistance / 2;
    }
  }

  @Override
  public void apply(double x, double y, Random random, Vector3 pos, Vector3 direction) {
    apply(x, y, pos, direction);
  }

  @Override
  public void apply(double x, double y, Vector3 pos, Vector3 direction) {
    double theta = (x + 0.5) * FastMath.PI - FastMath.PI;
    double phi = FastMath.PI / 2 - (y + 0.5) * FastMath.PI;

    pos.set(FastMath.cos(theta) * scale, 0, FastMath.sin(theta) * scale);
    direction.set(FastMath.sin(theta) * FastMath.cos(phi), -FastMath.sin(phi),
        FastMath.cos(theta) * FastMath.cos(phi));
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

  public enum Eye {
    LEFT,
    RIGHT
  }
}
