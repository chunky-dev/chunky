/* Copyright (c) 2012-2022 Chunky contributors
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
package se.llbit.chunky.renderer.postprocessing;

/**
 * Wraps the sample buffer to deny write access outside the scene.
 * The sample buffer stores the original R-, G- and B-channel samples of the image.
 * They are represented as doubles with linear exposure.
 *
 * TODO: move everything canvas related out of the scene (width, height, alphaChannel)?
 */
public class ReadOnlySampleBufferWrapper {
  protected final double[] samples;

  public ReadOnlySampleBufferWrapper(double[] samples) {
    this.samples = samples;
  }

  public double getSample(int i) {
    return samples[i];
  }
}
