/*
 * Copyright (c) 2023 Chunky contributors
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

package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Vector3;

public class Lectern extends EmptyModelBlock {
    private final String facing;
    private final boolean hasBook;

    public Lectern(String facing, boolean hasBook) {
        super("lectern", Texture.lecternFront);
        this.facing = facing;
        this.hasBook = hasBook;
        invisible = true;
    }

    @Override
    public boolean isEntity() {
        return true;
    }

    @Override
    public Entity toEntity(Vector3 position) {
        return new se.llbit.chunky.entity.Lectern(position, this.facing, this.hasBook);
    }
}
