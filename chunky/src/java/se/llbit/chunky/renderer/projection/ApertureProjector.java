/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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

import java.io.File;
import java.io.IOException;
import java.util.Random;

import se.llbit.chunky.renderer.ApertureShape;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.log.Log;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.resources.ImageLoader;
import se.llbit.util.annotation.Nullable;

/**
 * Simulates a non-point aperture to produce a depth-of-field effect.
 * Delegates calculation of base offset/direction to another projector. If
 * apertureSize is 0 this will still work, but it will not have any effect.
 * In that case you should use the wrapped Projector directly.
 */
public class ApertureProjector implements Projector {
  protected final Projector wrapped;
  protected final double aperture;
  protected final double subjectDistance;
  @Nullable
  protected BitmapImage apertureMask;

  public ApertureProjector(Projector wrapped, double apertureSize, double subjectDistance) {
    this.wrapped = wrapped;
    this.aperture = Math.max(apertureSize, Ray.EPSILON);
    this.subjectDistance = Math.max(subjectDistance, Ray.EPSILON);
    this.apertureMask = null;
  }

  public ApertureProjector(Projector wrapped, double apertureSize, double subjectDistance, String apertureMaskFilename) {
    this(wrapped, apertureSize, subjectDistance);
    this.apertureMask = loadApertureMask(apertureMaskFilename);
  }

  public ApertureProjector(Projector wrapped, double apertureSize, double subjectDistance, ApertureShape apertureShape) {
    this(wrapped, apertureSize, subjectDistance);
    this.apertureMask = loadApertureMask(apertureShape);
  }

  static private BitmapImage loadApertureMask(String filename) {
    File textureFile = new File(filename);
    if (textureFile.exists()) {
      try {
        Log.info("Loading aperture mask: " + filename);
        BitmapImage apertureMask = ImageLoader.read(textureFile);

        if(apertureMask.width != apertureMask.height) {
          Log.errorf("Failed to load aperture mask: %s. Mask is not a square image.", filename);
          return null;
        }

        return apertureMask;
      } catch (IOException e) {
        Log.error("Failed to load aperture mask: " + filename, e);
        return null;
      }
    } else {
      Log.errorf("Failed to load aperture mask: %s (file does not exist)", filename);
      return null;
    }
  }

  static private BitmapImage loadApertureMask(ApertureShape apertureShape) {
    try {
      String resourceName = apertureShape.getResourceName();
      if (resourceName != null) {
        return ImageLoader.read(ApertureProjector.class.getResourceAsStream(resourceName));
      }
    } catch (IOException e) {
      Log.error("Failed to load built-in aperture mask: " + apertureShape, e);
    }
    return null;
  }

  @Override public void apply(double x, double y, Random random, Vector3 o, Vector3 d) {
    wrapped.apply(x, y, random, o, d);

    d.scale(subjectDistance / d.z);

    double rx = 0, ry = 0;

    if(apertureMask != null) {
      // If an aperture mask is given, pick a random point in a square,
      // check the mask at the chosen point. The value of the mask at that point
      // is the probability that a ray with that direction can traverse the aperture
      // (usually the aperture mask is mostly pure black and pure white but this is not forced)
      // If the ray is rejected, retry with a new point until a ray is accepted
      // (rejection sampling)
      // (We bound the number of tries to not get an infinite loop in the
      // case the provided mask is too dark, or even pure black. When no ray
      // is accepted, the ray is shot straight, which is undesirable but
      // shouldn't happen will a well behaved mask)
      for(int iter = 0; iter < 100; ++iter) {
        double u = random.nextDouble();
        double v = random.nextDouble();

        int col = (int)(u * apertureMask.width);
        int row = (int)(v * apertureMask.height);
        int color = apertureMask.data[row*apertureMask.width + col];

        // The mask should be grayscale so use any of the component (except alpha)
        double probability = (color & 0xff) / 255.0;

        if(random.nextDouble() <= probability) {
          // ray accepted
          rx = (u - 0.5) * 2 * aperture;
          ry = (v - 0.5) * 2 * aperture;

          break;
        }
      }
    } else {
      // If no aperture mask is given, just pick a random point in a circle,
      // this has the same effect as having a mask that is a pure white circle
      // on a black background
      double r = Math.sqrt(random.nextDouble()) * aperture;
      double theta = random.nextDouble() * Math.PI * 2.;
      rx = Math.cos(theta) * r;
      ry = Math.sin(theta) * r;
    }

    d.sub(rx, ry, 0);
    o.add(rx, ry, 0);
  }

  @Override public void apply(double x, double y, Vector3 pos, Vector3 direction) {
    wrapped.apply(x, y, pos, direction);
  }

  @Override public double getMinRecommendedFoV() {
    return wrapped.getMinRecommendedFoV();
  }

  @Override public double getMaxRecommendedFoV() {
    return wrapped.getMaxRecommendedFoV();
  }

  @Override public double getDefaultFoV() {
    return wrapped.getDefaultFoV();
  }
}
