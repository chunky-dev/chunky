/*
 * Copyeast (c) 2023 Chunky contributors
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

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.model.RotatableBlockModel;
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class Crafter extends AbstractModelBlock {

  private final String description;

  public Crafter(String name, String orientation, boolean crafting, boolean triggered, AbstractTexture north, AbstractTexture northCrafting, AbstractTexture east, AbstractTexture eastCrafting, AbstractTexture eastTriggered,
                 AbstractTexture south, AbstractTexture southTriggered, AbstractTexture west, AbstractTexture westCrafting, AbstractTexture westTriggered, AbstractTexture top, AbstractTexture topCrafting, AbstractTexture topTriggered, AbstractTexture bottom) {
    super(name, north);
    this.description = "orientation=" + orientation + ", crafting=" + crafting + ", triggered=" + triggered;
    RotatableBlockModel m = new RotatableBlockModel(crafting ? northCrafting : north,
      crafting ? eastCrafting : (triggered ? eastTriggered : east),
      triggered ? southTriggered : south,
      crafting ? westCrafting : (triggered ? westTriggered : west),
      crafting ? topCrafting : (triggered ? topTriggered : top),
      bottom);
    // Fix top quad
    m.setFaceQuad(4, new Quad(
      new Vector3(0, 1, 1),
      new Vector3(1, 1, 1),
      new Vector3(0, 1, 0),
      new Vector4(1, 0, 1, 0)));
    switch(orientation) {
      case "north_up":
        break;
      case "east_up":
        m.rotateY(1);
        break;
      case "south_up":
        m.rotateY(2);
        break;
      case "west_up":
        m.rotateY(-1);
        break;
      case "up_south":
        m.rotateX(1);
        break;
      case "up_west":
        m.rotateX(1);
        m.rotateY(1);
        break;
      case "up_north":
        m.rotateX(1);
        m.rotateY(2);
        break;
      case "up_east":
        m.rotateX(1);
        m.rotateY(-1);
        break;
      case "down_north":
        m.rotateX(-1);
        break;
      case "down_east":
        m.rotateX(-1);
        m.rotateY(1);
        break;
      case "down_south":
        m.rotateX(-1);
        m.rotateY(2);
        break;
      case "down_west":
        m.rotateX(-1);
        m.rotateY(-1);
        break;
    }
    this.model = m;
    opaque = true;
    solid = true;
  }

  @Override
  public String description() {
    return description;
  }
}
