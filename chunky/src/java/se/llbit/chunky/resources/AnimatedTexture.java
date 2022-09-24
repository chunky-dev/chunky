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
package se.llbit.chunky.resources;

import se.llbit.math.Ray;

/**
 * Basic animated texture extension.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class AnimatedTexture extends Texture {

  protected int numFrames = 1;
  protected int frameHeight = 0;

  public AnimatedTexture(String resourceName) {
    super(resourceName);
    updateNumFrames();
  }

  @Override
  public float[] getColor(double u, double v) {
    return getColor(u, v, 0);
  }

  /**
   * Get color for animation frame.
   */
  public float[] getColor(double u, double v, int frame) {
    int frameFraction = Math.floorMod(frame, numFrames);
    return getColor((int) (u * width - Ray.EPSILON),
      (int) ((1 - v) * frameHeight - Ray.EPSILON + frameFraction * frameHeight));
  }

  /**
   * Get color for animation frame.
   */
  public boolean applyColor(Ray ray, int frame) {
    int frameFraction = Math.floorMod(frame, numFrames);
    int x = (int) (ray.u * width - Ray.EPSILON);
    int y =  (int) ((1 - ray.v) * frameHeight - Ray.EPSILON + frameFraction * frameHeight);
    int i=(y*width+x)*4;
    if (gamaImage[i+3] > Ray.EPSILON) {
      ray.color.set(gamaImage[i], gamaImage[i + 1], gamaImage[i + 2], gamaImage[i + 3]);
      return true;
    }
    return false;
  }

  @Override
  public boolean applyColor(Ray ray) {
    return  applyColor(ray,0);
  }

  @Override public void setTexture(BitmapImage newImage) {
    super.setTexture(newImage);
    updateNumFrames();
  }

  private void updateNumFrames() {
    frameHeight = Math.min(height, width);
    numFrames = height / frameHeight;
    numFrames = Math.max(1, numFrames);
  }
}
