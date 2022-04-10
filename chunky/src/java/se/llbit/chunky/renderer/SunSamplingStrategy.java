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
    Off("Off", "Sun is not sampled with next event estimation.", false, true, false, true),
    NonLuminous("Non-Luminous", "Sun is drawn on the skybox but it does not contribute to the lighting of the scene.", false, false, false, false),
    Fast("Fast", "Fast sun sampling algorithm. Lower noise but does not correctly model some visual effects.", true, false, false, false),
    HighQuality("High Quality", "High quality sun sampling. More noise but correctly models visual effects.", true, true, true, true);

    private final String friendlyName;
    private final String description;

    private final boolean sunSampling;
    private final boolean diffuseSun;
    private final boolean strictDirectLight;
    private final boolean sunLuminosity;

    SunSamplingStrategy(String name, String description, boolean sunSampling, boolean diffuseSun, boolean strictDirectLight, boolean sunLuminosity) {
        this.friendlyName = name;
        this.description = description;

        this.sunSampling = sunSampling;
        this.diffuseSun = diffuseSun;
        this.strictDirectLight = strictDirectLight;
        this.sunLuminosity = sunLuminosity;
    }

    @Override
    public String getName() {
        return this.friendlyName;
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
}
