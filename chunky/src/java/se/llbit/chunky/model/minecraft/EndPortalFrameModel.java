/*
 * Copyright (c) 2012-2023 Chunky contributors
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
package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.*;

import java.util.Arrays;

public class EndPortalFrameModel extends QuadModel {
    private static final Quad[] endPortalQuadsNorth = new Quad[]{
            new Quad(
                    new Vector3(0 / 16.0, 13 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 13 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 13 / 16.0, 0 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 13 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 13 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 13 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 13 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 13 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 13 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(0 / 16.0, 13 / 16.0, 0 / 16.0),
                    new Vector3(16 / 16.0, 13 / 16.0, 0 / 16.0),
                    new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 13 / 16.0, 0 / 16.0)
            ),
            new Quad(
                    new Vector3(16 / 16.0, 13 / 16.0, 16 / 16.0),
                    new Vector3(0 / 16.0, 13 / 16.0, 16 / 16.0),
                    new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
                    new Vector4(16 / 16.0, 0 / 16.0, 13 / 16.0, 0 / 16.0)
            )};

    private static final Quad[][] orientedEndPortalQuads = new Quad[4][];

    private static final Texture[] tex = new Texture[]{
            Texture.endPortalFrameTop, Texture.endStone,
            Texture.endPortalFrameSide, Texture.endPortalFrameSide, Texture.endPortalFrameSide, Texture.endPortalFrameSide
    };

    private static final Quad[] eyeOfEnderQuadsNorth = new Quad[]{
            new Quad(
                    new Vector3(4 / 16.0, 16 / 16.0, 12 / 16.0),
                    new Vector3(12 / 16.0, 16 / 16.0, 12 / 16.0),
                    new Vector3(4 / 16.0, 16 / 16.0, 4 / 16.0),
                    new Vector4(4 / 16.0, 12 / 16.0, 4 / 16.0, 12 / 16.0)
            ),
            new Quad(
                    new Vector3(4 / 16.0, 16 / 16.0, 12 / 16.0),
                    new Vector3(4 / 16.0, 16 / 16.0, 4 / 16.0),
                    new Vector3(4 / 16.0, 13 / 16.0, 12 / 16.0),
                    new Vector4(12 / 16.0, 4 / 16.0, 16 / 16.0, 13 / 16.0)
            ),
            new Quad(
                    new Vector3(12 / 16.0, 16 / 16.0, 4 / 16.0),
                    new Vector3(12 / 16.0, 16 / 16.0, 12 / 16.0),
                    new Vector3(12 / 16.0, 13 / 16.0, 4 / 16.0),
                    new Vector4(12 / 16.0, 4 / 16.0, 16 / 16.0, 13 / 16.0)
            ),
            new Quad(
                    new Vector3(4 / 16.0, 16 / 16.0, 4 / 16.0),
                    new Vector3(12 / 16.0, 16 / 16.0, 4 / 16.0),
                    new Vector3(4 / 16.0, 13 / 16.0, 4 / 16.0),
                    new Vector4(12 / 16.0, 4 / 16.0, 16 / 16.0, 13 / 16.0)
            ),
            new Quad(
                    new Vector3(12 / 16.0, 16 / 16.0, 12 / 16.0),
                    new Vector3(4 / 16.0, 16 / 16.0, 12 / 16.0),
                    new Vector3(12 / 16.0, 13 / 16.0, 12 / 16.0),
                    new Vector4(12 / 16.0, 4 / 16.0, 16 / 16.0, 13 / 16.0)
            )
    };

    private static final Quad[][] orientedEyeOfEnderQuads = new Quad[4][];

    static {
        orientedEndPortalQuads[0] = endPortalQuadsNorth;
        orientedEndPortalQuads[1] = Model.rotateY(orientedEndPortalQuads[0]);
        orientedEndPortalQuads[2] = Model.rotateY(orientedEndPortalQuads[1]);
        orientedEndPortalQuads[3] = Model.rotateY(orientedEndPortalQuads[2]);

        orientedEyeOfEnderQuads[0] = eyeOfEnderQuadsNorth;
        orientedEyeOfEnderQuads[1] = Model.rotateY(orientedEyeOfEnderQuads[0]);
        orientedEyeOfEnderQuads[2] = Model.rotateY(orientedEyeOfEnderQuads[1]);
        orientedEyeOfEnderQuads[3] = Model.rotateY(orientedEyeOfEnderQuads[2]);
    }

    private final Quad[] quads;
    private final Texture[] textures;

    public EndPortalFrameModel(boolean hasEye, String facingString) {
        int orientation;
        switch (facingString) {
            default:
            case "north":
                orientation = 0;
                break;
            case "east":
                orientation = 1;
                break;
            case "south":
                orientation = 2;
                break;
            case "west":
                orientation = 3;
                break;
        }

        if (hasEye) {
            quads = Model.join(orientedEndPortalQuads[orientation], orientedEyeOfEnderQuads[orientation]);
            textures = new Texture[quads.length];
            Arrays.fill(textures, Texture.eyeOfTheEnder);
            System.arraycopy(tex, 0, textures, 0, tex.length);
        } else {
            quads = orientedEndPortalQuads[orientation];
            textures = tex;
        }
    }

    @Override
    public Quad[] getQuads() {
        return quads;
    }

    @Override
    public Texture[] getTextures() {
        return textures;
    }
}
