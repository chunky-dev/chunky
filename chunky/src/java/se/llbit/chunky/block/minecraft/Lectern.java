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

import se.llbit.chunky.block.MinecraftBlockTranslucent;
import se.llbit.chunky.entity.Book;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Ray;
import se.llbit.math.Ray2;
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
    public Entity[] toEntity(Vector3 position) {
        Vector3 bookPosition = position.rAdd(0, 8.5 / 16.0, 0);

        double bookYaw;
        switch (facing) {
            case "north":
                bookPosition.add(0, 0, -2 / 16.0);
                bookYaw = 0;
                break;
            case "east":
                bookPosition.add(2 / 16.0, 0, 0);
                bookYaw = -Math.PI / 2;
                break;
            case "south":
                bookPosition.add(0, 0, 2 / 16.0);
                bookYaw = Math.PI;
                break;
            case "west":
                bookPosition.add(-2 / 16.0, 0, 0);
                bookYaw = Math.PI / 2;
                break;
            default:
                bookYaw = 0;
        }

        Book book = new Book(
            bookPosition,
            Math.PI - Math.PI / 16,
            Math.PI / 8,
            Math.PI - Math.PI / 8);
        book.setPitch(Math.toRadians(90 - 22.5));
        book.setYaw(bookYaw);
        return new Entity[] {new se.llbit.chunky.entity.Lectern(position, this.facing, this.hasBook), book};
    }
}
