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
package se.llbit.chunky.renderer;

import se.llbit.util.Registerable;

public enum SunSamplingStrategy implements Registerable {
    OFF("Off", "Sun is not sampled with next event estimation.", false, true, false, true, false),
    NON_LUMINOUS("Non-Luminous", "Sun is drawn on the skybox but it does not contribute to the lighting of the scene.", false, false, false, false, false),
    FAST("Fast", "Fast sun sampling algorithm. Lower noise but does not correctly model some visual effects.", true, false, false, false, false),
    HIGH_QUALITY("High Quality", "High quality sun sampling. More noise but correctly models visual effects such as caustics.", true, true, true, true, false),
    DIFFUSE("Diffuse", "Sun is sampled on a certain percentage of diffuse reflections. Correctly models visual effects while reducing noise for direct and diffuse illumination.", false, true, false, true, true);

    private final String displayName;
    private final String description;

    private final boolean sunSampling;
    private final boolean diffuseSun;
    private final boolean strictDirectLight;
    private final boolean sunLuminosity;
    private final boolean diffuseSampling;

    SunSamplingStrategy(String displayName, String description, boolean sunSampling, boolean diffuseSun, boolean strictDirectLight, boolean sunLuminosity, boolean diffuseSampling) {
        this.displayName = displayName;
        this.description = description;

        this.sunSampling = sunSampling;
        this.diffuseSun = diffuseSun;
        this.strictDirectLight = strictDirectLight;
        this.sunLuminosity = sunLuminosity;
        this.diffuseSampling = diffuseSampling;
    }

    @Override
    public String getName() {
        return this.displayName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getId() {
        return this.name();
    }

    public boolean doSunSampling() {
        return sunSampling;
    }

    public boolean isDiffuseSun() {
        return diffuseSun;
    }

    public boolean isStrictDirectLight() {
        return strictDirectLight;
    }

    public boolean isSunLuminosity() {
        return sunLuminosity;
    }

    public boolean isDiffuseSampling() { return diffuseSampling; }
}
