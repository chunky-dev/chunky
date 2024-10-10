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

import se.llbit.chunky.block.TexturedBlock;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texture.AbstractTexture;

public class RespawnAnchor extends TexturedBlock {
    private static final AbstractTexture[] sideTextures = new AbstractTexture[]{
            Texture.respawnAnchorSide0,
            Texture.respawnAnchorSide1,
            Texture.respawnAnchorSide2,
            Texture.respawnAnchorSide3,
            Texture.respawnAnchorSide4
    };

    public final int charges;

    public RespawnAnchor(int charges) {
        super("respawn_anchor", sideTextures[charges], Texture.respawnAnchorTop, Texture.respawnAnchorBottom);
        this.charges = charges;
    }
}
