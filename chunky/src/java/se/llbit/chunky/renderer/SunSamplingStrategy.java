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
    Off("Off", "Sun is not sampled with next event estimation."),
    Fast("Fast", "Fast sun sampling algorithm. Lower noise but does not correctly model some visual effects."),
    HighQuality("High Quality", "High quality sun sampling. More noise but correctly models visual effects.");

    private final String friendlyName;
    private final String description;

    SunSamplingStrategy(String name, String description) {
        this.friendlyName = name;
        this.description = description;
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
}
