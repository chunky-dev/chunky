/* Copyright (c) 2023 Chunky Contributors
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

public enum WaterShadingStrategy implements Registerable {
    SIMPLEX("Simplex", "Uses configurable noise to shade the water, which prevents tiling at great distances."),
    LEGACY("Legacy", "Uses a tiled normal map to shade the water"),
    STILL("Still", "Renders the water surface as flat.");

    private final String displayName;
    private final String description;

    WaterShadingStrategy(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
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
}
