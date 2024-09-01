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

package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class BellModel extends QuadModel {
  private static final AbstractTexture bell = Texture.bellBody;
  private static final AbstractTexture bar = Texture.darkOakPlanks;
  private static final AbstractTexture post = Texture.stone;

  private static final AbstractTexture[] texBellFloor = new AbstractTexture[] {
      bar, bar, bar, bar, post, post, post, post, post, post, post, post, post, post, post, post,
      bell, bell, bell, bell, bell, bell, bell, bell, bell, bell, bell
  };

  private static final AbstractTexture[] texBellWall = new AbstractTexture[] {
      bar, bar, bar, bar, bar, bar,
      bell, bell, bell, bell, bell, bell, bell, bell, bell, bell, bell
  };

  private static final AbstractTexture[] texBellDoubleWall = new AbstractTexture[] {
      bar, bar, bar, bar, bar, bar,
      bell, bell, bell, bell, bell, bell, bell, bell, bell, bell, bell
  };

  private static final AbstractTexture[] texBellCeiling = new AbstractTexture[] {
      bar, bar, bar, bar, bar,
      bell, bell, bell, bell, bell, bell, bell, bell, bell, bell, bell
  };

  //region Bell Body
  private static final Quad[] quadsBell = new Quad[] {
      new Quad( // up
          new Vector3(4 / 16.0, 6 / 16.0, 12 / 16.0),
          new Vector3(12 / 16.0, 6 / 16.0, 12 / 16.0),
          new Vector3(4 / 16.0, 6 / 16.0, 4 / 16.0),
          new Vector4(8 / 32.0, 16 / 32.0, 1 - 13 / 32.0, 1 - 21 / 32.0)
      ),
      new Quad( // down
          new Vector3(12 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector3(4 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector4(16 / 32.0, 24 / 32.0, 1 - 13 / 32.0, 1 - 21 / 32.0)
      ),
      new Quad( // west
          new Vector3(4 / 16.0, 6 / 16.0, 12 / 16.0),
          new Vector3(4 / 16.0, 6 / 16.0, 4 / 16.0),
          new Vector3(4 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector4(0 / 32.0, 8 / 32.0, 1 - 23 / 32.0, 1 - 21 / 32.0)
      ),
      new Quad( // east
          new Vector3(12 / 16.0, 6 / 16.0, 4 / 16.0),
          new Vector3(12 / 16.0, 6 / 16.0, 12 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector4(16 / 32.0, 24 / 32.0, 1 - 23 / 32.0, 1 - 21 / 32.0)
      ),
      new Quad( // north
          new Vector3(4 / 16.0, 6 / 16.0, 4 / 16.0),
          new Vector3(12 / 16.0, 6 / 16.0, 4 / 16.0),
          new Vector3(4 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector4(8 / 32.0, 16 / 32.0, 1 - 23 / 32.0, 1 - 21 / 32.0)
      ),
      new Quad( // south
          new Vector3(12 / 16.0, 6 / 16.0, 12 / 16.0),
          new Vector3(4 / 16.0, 6 / 16.0, 12 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 12 / 16.0),
          new Vector4(24 / 32.0, 32 / 32.0, 1 - 23 / 32.0, 1 - 21 / 32.0)
      ),
      new Quad( // up
          new Vector3(5 / 16.0, 13 / 16.0, 11 / 16.0),
          new Vector3(11 / 16.0, 13 / 16.0, 11 / 16.0),
          new Vector3(5 / 16.0, 13 / 16.0, 5 / 16.0),
          new Vector4(6 / 32.0, 12 / 32.0, 1 - 6 / 32.0, 1 - 0 / 32.0)
      ),
      new Quad( // west
          new Vector3(5 / 16.0, 13 / 16.0, 11 / 16.0),
          new Vector3(5 / 16.0, 13 / 16.0, 5 / 16.0),
          new Vector3(5 / 16.0, 6 / 16.0, 11 / 16.0),
          new Vector4(0 / 32.0, 6 / 32.0, 1 - 13 / 32.0, 1 - 6 / 32.0)
      ),
      new Quad( // east
          new Vector3(11 / 16.0, 13 / 16.0, 5 / 16.0),
          new Vector3(11 / 16.0, 13 / 16.0, 11 / 16.0),
          new Vector3(11 / 16.0, 6 / 16.0, 5 / 16.0),
          new Vector4(12 / 32.0, 18 / 32.0, 1 - 13 / 32.0, 1 - 6 / 32.0)
      ),
      new Quad( // north
          new Vector3(5 / 16.0, 13 / 16.0, 5 / 16.0),
          new Vector3(11 / 16.0, 13 / 16.0, 5 / 16.0),
          new Vector3(5 / 16.0, 6 / 16.0, 5 / 16.0),
          new Vector4(6 / 32.0, 12 / 32.0, 1 - 13 / 32.0, 1 - 6 / 32.0)
      ),
      new Quad( // south
          new Vector3(11 / 16.0, 13 / 16.0, 11 / 16.0),
          new Vector3(5 / 16.0, 13 / 16.0, 11 / 16.0),
          new Vector3(11 / 16.0, 6 / 16.0, 11 / 16.0),
          new Vector4(18 / 32.0, 24 / 32.0, 1 - 13 / 32.0, 1 - 6 / 32.0)
      )
  };
  //endregion

  //region Bell Floor
  private static final Quad[] quadsBellFloor = new Quad[] {
      new Quad(
          new Vector3(2 / 16.0, 15 / 16.0, 9 / 16.0),
          new Vector3(14 / 16.0, 15 / 16.0, 9 / 16.0),
          new Vector3(2 / 16.0, 15 / 16.0, 7 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 11 / 16.0, 13 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 13 / 16.0, 7 / 16.0),
          new Vector3(14 / 16.0, 13 / 16.0, 7 / 16.0),
          new Vector3(2 / 16.0, 13 / 16.0, 9 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 11 / 16.0, 13 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 15 / 16.0, 7 / 16.0),
          new Vector3(14 / 16.0, 15 / 16.0, 7 / 16.0),
          new Vector3(2 / 16.0, 13 / 16.0, 7 / 16.0),
          new Vector4(14 / 16.0, 2 / 16.0, 14 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 15 / 16.0, 9 / 16.0),
          new Vector3(2 / 16.0, 15 / 16.0, 9 / 16.0),
          new Vector3(14 / 16.0, 13 / 16.0, 9 / 16.0),
          new Vector4(14 / 16.0, 2 / 16.0, 13 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 12 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 10 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 12 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 10 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 15 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 15 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(14 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector3(14 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector4(2 / 16.0, 0 / 16.0, 15 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(14 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 10 / 16.0),
          new Vector4(2 / 16.0, 0 / 16.0, 15 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 12 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 10 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 12 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 10 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 15 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 15 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector3(2 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 6 / 16.0),
          new Vector4(2 / 16.0, 0 / 16.0, 15 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(2 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(2 / 16.0, 0 / 16.0, 10 / 16.0),
          new Vector4(2 / 16.0, 0 / 16.0, 15 / 16.0, 0 / 16.0)
      )
  };
  //endregion

  //region Bell Wall
  private static final Quad[] quadsBellWall = new Quad[] {
      new Quad(
          new Vector3(3 / 16.0, 15 / 16.0, 9 / 16.0),
          new Vector3(16 / 16.0, 15 / 16.0, 9 / 16.0),
          new Vector3(3 / 16.0, 15 / 16.0, 7 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 11 / 16.0, 13 / 16.0)
      ),
      new Quad(
          new Vector3(3 / 16.0, 13 / 16.0, 7 / 16.0),
          new Vector3(16 / 16.0, 13 / 16.0, 7 / 16.0),
          new Vector3(3 / 16.0, 13 / 16.0, 9 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 11 / 16.0, 13 / 16.0)
      ),
      new Quad(
          new Vector3(3 / 16.0, 15 / 16.0, 9 / 16.0),
          new Vector3(3 / 16.0, 15 / 16.0, 7 / 16.0),
          new Vector3(3 / 16.0, 13 / 16.0, 9 / 16.0),
          new Vector4(7 / 16.0, 5 / 16.0, 12 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 15 / 16.0, 7 / 16.0),
          new Vector3(16 / 16.0, 15 / 16.0, 9 / 16.0),
          new Vector3(16 / 16.0, 13 / 16.0, 7 / 16.0),
          new Vector4(7 / 16.0, 5 / 16.0, 12 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(3 / 16.0, 15 / 16.0, 7 / 16.0),
          new Vector3(16 / 16.0, 15 / 16.0, 7 / 16.0),
          new Vector3(3 / 16.0, 13 / 16.0, 7 / 16.0),
          new Vector4(14 / 16.0, 2 / 16.0, 14 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 15 / 16.0, 9 / 16.0),
          new Vector3(3 / 16.0, 15 / 16.0, 9 / 16.0),
          new Vector3(16 / 16.0, 13 / 16.0, 9 / 16.0),
          new Vector4(14 / 16.0, 2 / 16.0, 13 / 16.0, 11 / 16.0)
      )
  };
  //endregion

  //region Bell Double Wall
  private static final Quad[] quadsBellDoubleWall = new Quad[] {
      new Quad(
          new Vector3(0 / 16.0, 15 / 16.0, 9 / 16.0),
          new Vector3(16 / 16.0, 15 / 16.0, 9 / 16.0),
          new Vector3(0 / 16.0, 15 / 16.0, 7 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 11 / 16.0, 13 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 13 / 16.0, 7 / 16.0),
          new Vector3(16 / 16.0, 13 / 16.0, 7 / 16.0),
          new Vector3(0 / 16.0, 13 / 16.0, 9 / 16.0),
          new Vector4(2 / 16.0, 14 / 16.0, 11 / 16.0, 13 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 15 / 16.0, 9 / 16.0),
          new Vector3(0 / 16.0, 15 / 16.0, 7 / 16.0),
          new Vector3(0 / 16.0, 13 / 16.0, 9 / 16.0),
          new Vector4(7 / 16.0, 5 / 16.0, 12 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 15 / 16.0, 7 / 16.0),
          new Vector3(16 / 16.0, 15 / 16.0, 9 / 16.0),
          new Vector3(16 / 16.0, 13 / 16.0, 7 / 16.0),
          new Vector4(7 / 16.0, 5 / 16.0, 12 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 15 / 16.0, 7 / 16.0),
          new Vector3(16 / 16.0, 15 / 16.0, 7 / 16.0),
          new Vector3(0 / 16.0, 13 / 16.0, 7 / 16.0),
          new Vector4(14 / 16.0, 2 / 16.0, 14 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 15 / 16.0, 9 / 16.0),
          new Vector3(0 / 16.0, 15 / 16.0, 9 / 16.0),
          new Vector3(16 / 16.0, 13 / 16.0, 9 / 16.0),
          new Vector4(14 / 16.0, 2 / 16.0, 13 / 16.0, 11 / 16.0)
      )
  };
  //endregion

  //region Bell Ceiling
  private static final Quad[] quadsBellCeiling = new Quad[] {
      new Quad(
          new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 16 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
          new Vector4(1 / 16.0, 3 / 16.0, 11 / 16.0, 13 / 16.0)
      ),
      new Quad(
          new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 13 / 16.0, 9 / 16.0),
          new Vector4(6 / 16.0, 4 / 16.0, 14 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(9 / 16.0, 16 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 16 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 13 / 16.0, 7 / 16.0),
          new Vector4(3 / 16.0, 1 / 16.0, 14 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 16 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 13 / 16.0, 7 / 16.0),
          new Vector4(9 / 16.0, 7 / 16.0, 14 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(9 / 16.0, 16 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 13 / 16.0, 9 / 16.0),
          new Vector4(8 / 16.0, 6 / 16.0, 14 / 16.0, 11 / 16.0)
      )
  };
  //endregion

  private final AbstractTexture[] textures;
  private Quad[] quads;

  public BellModel(String facing, String attachment) {
    int orientation;
    switch (facing) {
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

    switch (attachment) {
      default:
      case "floor":
        quads = quadsBellFloor;
        textures = texBellFloor;
        quads = Model.rotateY(quads, -Math.toRadians(90*orientation));
        break;
      case "ceiling":
        quads = quadsBellCeiling;
        textures = texBellCeiling;
        quads = Model.rotateY(quads, -Math.toRadians(90*orientation));
        break;
      case "single_wall":
        quads = quadsBellWall;
        textures = texBellWall;
        quads = Model.rotateY(quads, -Math.toRadians(90*orientation - 90));
        break;
      case "double_wall":
        quads = quadsBellDoubleWall;
        textures = texBellDoubleWall;
        switch (orientation) {
          case 0:
            quads = Model.rotateY(quads, -Math.toRadians(90));
            break;
          case 2:
            quads = Model.rotateY(quads, -Math.toRadians(270));
            break;
          case 3:
            quads = Model.rotateY(quads, -Math.toRadians(180));
            break;
        }
        break;
    }

    this.quads = Model.join(quads, quadsBell);
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public AbstractTexture[] getTextures() {
    return textures;
  }
}
