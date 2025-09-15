/* Copyright (c) 2022 Chunky Contributors
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
package se.llbit.chunky.renderer.scene;

import se.llbit.util.Registerable;

public enum SunSamplingStrategy implements Registerable {
//    OFF("Off", "Sun is not sampled with next event estimation.", false, true, false, true),
//    NON_LUMINOUS("Non-Luminous", "Sun is drawn on the skybox but it does not contribute to the lighting of the scene.", false, false, false, false),
//    FAST("Fast", "Fast sun sampling algorithm. Lower noise but does not correctly model some visual effects.", true, false, false, false),
//    HIGH_QUALITY("High Quality", "High quality sun sampling. More noise but correctly models visual effects such as caustics.", true, true, true, true);

  SAMPLE_THROUGH_OPACITY("Sample through opacity", "Sample the sun and sky through translucent textures", true, false),
  SAMPLE_ONLY("Sample only", "Sample the sun on diffuse reflections.", true, false),
  MIX("Mix", "Sample the sun on diffuse reflections, and diffusely intersect on specular interactions.", true, true),
  OFF("Diffuse", "Diffusely intersect on all interactions.", false, true);

  private final String displayName;
  private final String description;

  private final boolean sunSampling;
  private final boolean diffuseSun;

  SunSamplingStrategy(String displayName, String description, boolean sunSampling, boolean diffuseSun) {
    this.displayName = displayName;
    this.description = description;

    this.sunSampling = sunSampling;
    this.diffuseSun = diffuseSun;
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public String getName() {
    return this.displayName;
  }

  @Override public String getDescription() {
    return this.description;
  }

  @Override public String getId() {
    return this.name();
  }

  public boolean doSunSampling() {
    return sunSampling;
  }

  public boolean isDiffuseSun() {
    return diffuseSun;
  }

  public static SunSamplingStrategy get(String name) {
    try {
      return valueOf(name);
    } catch (IllegalArgumentException e) {
      return SAMPLE_THROUGH_OPACITY;
    }
  }
}
