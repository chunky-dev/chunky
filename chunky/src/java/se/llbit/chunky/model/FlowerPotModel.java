/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

/**
 * Flower pot block.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class FlowerPotModel extends QuadModel {
  public enum Kind {
    NONE,
    POPPY,
    DANDELION,
    OAK_SAPLING,
    SPRUCE_SAPLING,
    BIRCH_SAPLING,
    JUNGLE_SAPLING,
    ACACIA_SAPLING,
    DARK_OAK_SAPLING,
    RED_MUSHROOM,
    BROWN_MUSHROOM,
    CACTUS,
    DEAD_BUSH,
    FERN,
    BLUE_ORCHID,
    ALLIUM,
    AZURE_BLUET,
    RED_TULIP,
    ORANGE_TULIP,
    WHITE_TULIP,
    PINK_TULIP,
    OXEYE_DAISY,
    BAMBOO,
    CORNFLOWER,
    LILY_OF_THE_VALLEY,
    WITHER_ROSE,
    WARPED_FUNGUS,
    CRIMSON_FUNGUS,
    WARPED_ROOTS,
    CRIMSON_ROOTS,
    AZALEA_BUSH,
    FLOWERING_AZALEA_BUSH,
    MANGROVE_PROPAGULE
  }

  private static final Texture flowerpot = Texture.flowerPot;
  private static final Texture dirt = Texture.dirt;
  private static final Texture[] flowerPotTex = new Texture[]{
    flowerpot, flowerpot, flowerpot, flowerpot, flowerpot, flowerpot, flowerpot, flowerpot, flowerpot, flowerpot,
    flowerpot, flowerpot, flowerpot, flowerpot, flowerpot, flowerpot, flowerpot, flowerpot, flowerpot, flowerpot,
    dirt, flowerpot
  };

  private static final Texture cactus_top = Texture.cactusTop;
  private static final Texture cactus = Texture.cactusSide;
  private static final Texture[] cactusTex = new Texture[]{
    cactus_top, cactus, cactus, cactus, cactus
  };

  private static final Texture[] azaleaBushTex = {
    Texture.pottedAzaleaBushTop, Texture.pottedAzaleaBushTop,
    Texture.pottedAzaleaBushSide, Texture.pottedAzaleaBushSide,
    Texture.pottedAzaleaBushSide, Texture.pottedAzaleaBushSide,
    Texture.pottedAzaleaBushSide, Texture.pottedAzaleaBushSide,
    Texture.pottedAzaleaBushSide, Texture.pottedAzaleaBushSide,
    Texture.pottedAzaleaBushPlant, Texture.pottedAzaleaBushPlant,
    Texture.pottedAzaleaBushPlant, Texture.pottedAzaleaBushPlant
  };

  private static final Texture[] floweringAzaleaBushTex = {
    Texture.pottedFloweringAzaleaBushTop, Texture.pottedFloweringAzaleaBushTop,
    Texture.pottedFloweringAzaleaBushSide, Texture.pottedFloweringAzaleaBushSide,
    Texture.pottedFloweringAzaleaBushSide, Texture.pottedFloweringAzaleaBushSide,
    Texture.pottedFloweringAzaleaBushSide, Texture.pottedFloweringAzaleaBushSide,
    Texture.pottedFloweringAzaleaBushSide, Texture.pottedFloweringAzaleaBushSide,
    Texture.pottedAzaleaBushPlant, Texture.pottedAzaleaBushPlant,
    Texture.pottedAzaleaBushPlant, Texture.pottedAzaleaBushPlant
  };

  //region Quads
  private static final Quad[] flowerPot = new Quad[]{
    new Quad(
      new Vector3(5 / 16.0, 6 / 16.0, 11 / 16.0),
      new Vector3(6 / 16.0, 6 / 16.0, 11 / 16.0),
      new Vector3(5 / 16.0, 6 / 16.0, 5 / 16.0),
      new Vector4(5 / 16.0, 6 / 16.0, 5 / 16.0, 11 / 16.0)
    ),
    new Quad(
      new Vector3(5 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector3(6 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector3(5 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector4(5 / 16.0, 6 / 16.0, 5 / 16.0, 11 / 16.0)
    ),
    new Quad(
      new Vector3(5 / 16.0, 6 / 16.0, 11 / 16.0),
      new Vector3(5 / 16.0, 6 / 16.0, 5 / 16.0),
      new Vector3(5 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector4(11 / 16.0, 5 / 16.0, 6 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 6 / 16.0, 5 / 16.0),
      new Vector3(6 / 16.0, 6 / 16.0, 11 / 16.0),
      new Vector3(6 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector4(11 / 16.0, 5 / 16.0, 6 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(5 / 16.0, 6 / 16.0, 5 / 16.0),
      new Vector3(6 / 16.0, 6 / 16.0, 5 / 16.0),
      new Vector3(5 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector4(11 / 16.0, 10 / 16.0, 6 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 6 / 16.0, 11 / 16.0),
      new Vector3(5 / 16.0, 6 / 16.0, 11 / 16.0),
      new Vector3(6 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector4(6 / 16.0, 5 / 16.0, 6 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 6 / 16.0, 11 / 16.0),
      new Vector3(11 / 16.0, 6 / 16.0, 11 / 16.0),
      new Vector3(10 / 16.0, 6 / 16.0, 5 / 16.0),
      new Vector4(10 / 16.0, 11 / 16.0, 5 / 16.0, 11 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector3(11 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector3(10 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector4(10 / 16.0, 11 / 16.0, 5 / 16.0, 11 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 6 / 16.0, 11 / 16.0),
      new Vector3(10 / 16.0, 6 / 16.0, 5 / 16.0),
      new Vector3(10 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector4(11 / 16.0, 5 / 16.0, 6 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(11 / 16.0, 6 / 16.0, 5 / 16.0),
      new Vector3(11 / 16.0, 6 / 16.0, 11 / 16.0),
      new Vector3(11 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector4(11 / 16.0, 5 / 16.0, 6 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 6 / 16.0, 5 / 16.0),
      new Vector3(11 / 16.0, 6 / 16.0, 5 / 16.0),
      new Vector3(10 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector4(6 / 16.0, 5 / 16.0, 6 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(11 / 16.0, 6 / 16.0, 11 / 16.0),
      new Vector3(10 / 16.0, 6 / 16.0, 11 / 16.0),
      new Vector3(11 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector4(11 / 16.0, 10 / 16.0, 6 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 6 / 16.0, 6 / 16.0),
      new Vector3(10 / 16.0, 6 / 16.0, 6 / 16.0),
      new Vector3(6 / 16.0, 6 / 16.0, 5 / 16.0),
      new Vector4(6 / 16.0, 10 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector3(10 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector3(6 / 16.0, 0 / 16.0, 6 / 16.0),
      new Vector4(6 / 16.0, 10 / 16.0, 5 / 16.0, 6 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 6 / 16.0, 5 / 16.0),
      new Vector3(10 / 16.0, 6 / 16.0, 5 / 16.0),
      new Vector3(6 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector4(10 / 16.0, 6 / 16.0, 6 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 6 / 16.0, 6 / 16.0),
      new Vector3(6 / 16.0, 6 / 16.0, 6 / 16.0),
      new Vector3(10 / 16.0, 0 / 16.0, 6 / 16.0),
      new Vector4(10 / 16.0, 6 / 16.0, 6 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 6 / 16.0, 11 / 16.0),
      new Vector3(10 / 16.0, 6 / 16.0, 11 / 16.0),
      new Vector3(6 / 16.0, 6 / 16.0, 10 / 16.0),
      new Vector4(6 / 16.0, 10 / 16.0, 5 / 16.0, 6 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 0 / 16.0, 10 / 16.0),
      new Vector3(10 / 16.0, 0 / 16.0, 10 / 16.0),
      new Vector3(6 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector4(6 / 16.0, 10 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 6 / 16.0, 10 / 16.0),
      new Vector3(10 / 16.0, 6 / 16.0, 10 / 16.0),
      new Vector3(6 / 16.0, 0 / 16.0, 10 / 16.0),
      new Vector4(10 / 16.0, 6 / 16.0, 6 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 6 / 16.0, 11 / 16.0),
      new Vector3(6 / 16.0, 6 / 16.0, 11 / 16.0),
      new Vector3(10 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector4(10 / 16.0, 6 / 16.0, 6 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 4 / 16.0, 10 / 16.0),
      new Vector3(10 / 16.0, 4 / 16.0, 10 / 16.0),
      new Vector3(6 / 16.0, 4 / 16.0, 6 / 16.0),
      new Vector4(6 / 16.0, 10 / 16.0, 6 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 0 / 16.0, 6 / 16.0),
      new Vector3(10 / 16.0, 0 / 16.0, 6 / 16.0),
      new Vector3(6 / 16.0, 0 / 16.0, 10 / 16.0),
      new Vector4(6 / 16.0, 10 / 16.0, 0 / 16.0, 4 / 16.0)
    )
  };

  private static final Quad[] cactusQuads = new Quad[]{
    new Quad(
      new Vector3(6 / 16.0, 16 / 16.0, 10 / 16.0),
      new Vector3(10 / 16.0, 16 / 16.0, 10 / 16.0),
      new Vector3(6 / 16.0, 16 / 16.0, 6 / 16.0),
      new Vector4(6 / 16.0, 10 / 16.0, 1 - 10 / 16.0, 1 - 6 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 16 / 16.0, 10 / 16.0),
      new Vector3(6 / 16.0, 16 / 16.0, 6 / 16.0),
      new Vector3(6 / 16.0, 5 / 16.0, 10 / 16.0),
      new Vector4(10 / 16.0, 6 / 16.0, 16 / 16.0, 4 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 16 / 16.0, 6 / 16.0),
      new Vector3(10 / 16.0, 16 / 16.0, 10 / 16.0),
      new Vector3(10 / 16.0, 5 / 16.0, 6 / 16.0),
      new Vector4(10 / 16.0, 6 / 16.0, 16 / 16.0, 4 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 16 / 16.0, 6 / 16.0),
      new Vector3(10 / 16.0, 16 / 16.0, 6 / 16.0),
      new Vector3(6 / 16.0, 5 / 16.0, 6 / 16.0),
      new Vector4(10 / 16.0, 6 / 16.0, 16 / 16.0, 4 / 16.0)
    ),
    new Quad(
      new Vector3(10 / 16.0, 16 / 16.0, 10 / 16.0),
      new Vector3(6 / 16.0, 16 / 16.0, 10 / 16.0),
      new Vector3(10 / 16.0, 5 / 16.0, 10 / 16.0),
      new Vector4(10 / 16.0, 6 / 16.0, 16 / 16.0, 4 / 16.0)
    )
  };

  private static final Quad[] flower = {
    new Quad(new Vector3(0, 4 / 16., 0), new Vector3(1, 4 / 16., 1), new Vector3(0, 1, 0),
      new Vector4(0, 1, 0, 12 / 16.)),

    new Quad(new Vector3(1, 4 / 16., 1), new Vector3(0, 4 / 16., 0), new Vector3(1, 1, 1),
      new Vector4(0, 1, 0, 12 / 16.)),

    new Quad(new Vector3(1, 4 / 16., 0), new Vector3(0, 4 / 16., 1), new Vector3(1, 1, 0),
      new Vector4(0, 1, 0, 12 / 16.)),

    new Quad(new Vector3(0, 4 / 16., 1), new Vector3(1, 4 / 16., 0), new Vector3(0, 1, 1),
      new Vector4(0, 1, 0, 12 / 16.)),
  };

  private static final Quad[] flowerSmall = {
    new Quad(new Vector3(2 / 16., 4 / 16., 2 / 16.), new Vector3(14 / 16., 4 / 16., 14 / 16.),
      new Vector3(2 / 16., 1, 2 / 16.), new Vector4(0, 1, 0, 1)),

    new Quad(new Vector3(14 / 16., 4 / 16., 14 / 16.),
      new Vector3(2 / 16., 4 / 16., 2 / 16.), new Vector3(14 / 16., 1, 14 / 16.),
      new Vector4(0, 1, 0, 1)),

    new Quad(new Vector3(14 / 16., 4 / 16., 2 / 16.),
      new Vector3(2 / 16., 4 / 16., 14 / 16.), new Vector3(14 / 16., 1, 2 / 16.),
      new Vector4(0, 1, 0, 1)),

    new Quad(new Vector3(2 / 16., 4 / 16., 14 / 16.),
      new Vector3(14 / 16., 4 / 16., 2 / 16.), new Vector3(2 / 16., 1, 14 / 16.),
      new Vector4(0, 1, 0, 1)),
  };

  private static final Quad[] bamboo = {
    new Quad(
      new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector4(13 / 16.0, 15 / 16.0, 1 - 0 / 16.0, 1 - 2 / 16.0)),
    new Quad(
      new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(13 / 16.0, 15 / 16.0, 4 / 16.0, 6 / 16.0)),
    new Quad(
      new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector4(6 / 16.0, 8 / 16.0, 0 / 16.0, 16 / 16.0)),
    new Quad(
      new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector4(6 / 16.0, 8 / 16.0, 0 / 16.0, 16 / 16.0)),
    new Quad(
      new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector4(6 / 16.0, 8 / 16.0, 0 / 16.0, 16 / 16.0)),
    new Quad(
      new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector4(6 / 16.0, 8 / 16.0, 0 / 16.0, 16 / 16.0))
  };

  private static final Quad[] bambooLeaf = {
    new Quad(
      new Vector3(16 / 16.0, 2 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 2 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 18 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)),
    new Quad(
      new Vector3(0 / 16.0, 2 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 2 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 18 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 0 / 16.0, 16 / 16.0))
  };

  private static final Quad[] azaleaBush = Model.join(new Quad[]{
      new Quad(
        new Vector3(4 / 16.0, 16 / 16.0, 12 / 16.0),
        new Vector3(12 / 16.0, 16 / 16.0, 12 / 16.0),
        new Vector3(4 / 16.0, 16 / 16.0, 4 / 16.0),
        new Vector4(4 / 16.0, 12 / 16.0, 4 / 16.0, 12 / 16.0)
      ),
      new Quad(
        new Vector3(4 / 16.0, 15.9 / 16.0, 4 / 16.0),
        new Vector3(12 / 16.0, 15.9 / 16.0, 4 / 16.0),
        new Vector3(4 / 16.0, 15.9 / 16.0, 12 / 16.0),
        new Vector4(4 / 16.0, 12 / 16.0, 12 / 16.0, 4 / 16.0)
      ),
      new Quad(
        new Vector3(4 / 16.0, 16 / 16.0, 4 / 16.0),
        new Vector3(12 / 16.0, 16 / 16.0, 4 / 16.0),
        new Vector3(4 / 16.0, 8 / 16.0, 4 / 16.0),
        new Vector4(12 / 16.0, 4 / 16.0, 11 / 16.0, 3 / 16.0)
      ),
      new Quad(
        new Vector3(12 / 16.0, 16 / 16.0, 4 / 16.0),
        new Vector3(4 / 16.0, 16 / 16.0, 4 / 16.0),
        new Vector3(12 / 16.0, 8 / 16.0, 4 / 16.0),
        new Vector4(4 / 16.0, 12 / 16.0, 11 / 16.0, 3 / 16.0)
      ),
      new Quad(
        new Vector3(4 / 16.0, 16 / 16.0, 12 / 16.0),
        new Vector3(12 / 16.0, 16 / 16.0, 12 / 16.0),
        new Vector3(4 / 16.0, 8 / 16.0, 12 / 16.0),
        new Vector4(4 / 16.0, 12 / 16.0, 11 / 16.0, 3 / 16.0)
      ),
      new Quad(
        new Vector3(12 / 16.0, 16 / 16.0, 12 / 16.0),
        new Vector3(4 / 16.0, 16 / 16.0, 12 / 16.0),
        new Vector3(12 / 16.0, 8 / 16.0, 12 / 16.0),
        new Vector4(12 / 16.0, 4 / 16.0, 11 / 16.0, 3 / 16.0)
      ),
      new Quad(
        new Vector3(4 / 16.0, 16 / 16.0, 12 / 16.0),
        new Vector3(4 / 16.0, 16 / 16.0, 4 / 16.0),
        new Vector3(4 / 16.0, 8 / 16.0, 12 / 16.0),
        new Vector4(12 / 16.0, 4 / 16.0, 11 / 16.0, 3 / 16.0)
      ),
      new Quad(
        new Vector3(4 / 16.0, 16 / 16.0, 4 / 16.0),
        new Vector3(4 / 16.0, 16 / 16.0, 12 / 16.0),
        new Vector3(4 / 16.0, 8 / 16.0, 4 / 16.0),
        new Vector4(4 / 16.0, 12 / 16.0, 11 / 16.0, 3 / 16.0)
      ),
      new Quad(
        new Vector3(12 / 16.0, 16 / 16.0, 12 / 16.0),
        new Vector3(12 / 16.0, 16 / 16.0, 4 / 16.0),
        new Vector3(12 / 16.0, 8 / 16.0, 12 / 16.0),
        new Vector4(4 / 16.0, 12 / 16.0, 11 / 16.0, 3 / 16.0)
      ),
      new Quad(
        new Vector3(12 / 16.0, 16 / 16.0, 4 / 16.0),
        new Vector3(12 / 16.0, 16 / 16.0, 12 / 16.0),
        new Vector3(12 / 16.0, 8 / 16.0, 4 / 16.0),
        new Vector4(12 / 16.0, 4 / 16.0, 11 / 16.0, 3 / 16.0)
      )
    },
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(2.6 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(13.4 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(2.6 / 16.0, 4 / 16.0, 8 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
      ),
      new Quad(
        new Vector3(13.4 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(2.6 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(13.4 / 16.0, 4 / 16.0, 8 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
      )
    }, Math.toRadians(45)),
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(8 / 16.0, 16 / 16.0, 13.4 / 16.0),
        new Vector3(8 / 16.0, 16 / 16.0, 2.6 / 16.0),
        new Vector3(8 / 16.0, 4 / 16.0, 13.4 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
      ),
      new Quad(
        new Vector3(8 / 16.0, 16 / 16.0, 2.6 / 16.0),
        new Vector3(8 / 16.0, 16 / 16.0, 13.4 / 16.0),
        new Vector3(8 / 16.0, 4 / 16.0, 2.6 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
      )
    }, Math.toRadians(45))
  );

  private static final Quad[] mangrovePropagule = Model.join(
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(4.5 / 16.0, 15 / 16.0, 8 / 16.0),
        new Vector3(11.5 / 16.0, 15 / 16.0, 8 / 16.0),
        new Vector3(4.5 / 16.0, 9 / 16.0, 8 / 16.0),
        new Vector4(11 / 16.0, 4 / 16.0, 15 / 16.0, 9 / 16.0), true
      )}, Math.toRadians(-45)),
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(8 / 16.0, 15 / 16.0, 4.5 / 16.0),
        new Vector3(8 / 16.0, 15 / 16.0, 11.5 / 16.0),
        new Vector3(8 / 16.0, 9 / 16.0, 4.5 / 16.0),
        new Vector4(11 / 16.0, 4 / 16.0, 15 / 16.0, 9 / 16.0), true
      )}, Math.toRadians(-45)),
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(7 / 16.0, 9 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 9 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 0 / 16.0, 8 / 16.0),
        new Vector4(9 / 16.0, 7 / 16.0, 9 / 16.0, 0 / 16.0), true
      ),}, Math.toRadians(-45)),
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(9 / 16.0, 9 / 16.0, 8 / 16.0),
        new Vector3(7 / 16.0, 9 / 16.0, 8 / 16.0),
        new Vector3(9 / 16.0, 0 / 16.0, 8 / 16.0),
        new Vector4(9 / 16.0, 7 / 16.0, 9 / 16.0, 0 / 16.0), true
      )}, Math.toRadians(45))
  );
  //endregion

  private final Quad[] quads;
  private final Texture[] textures;
  private final Tint[] tints;

  public FlowerPotModel(Kind kind) {
    switch (kind) {
      case NONE:
        quads = flowerPot;
        textures = flowerPotTex;
        tints = null;
        break;
      case CACTUS:
        quads = Model.join(flowerPot, cactusQuads);
        textures = new Texture[flowerPotTex.length + cactusTex.length];
        tints = null;
        System.arraycopy(flowerPotTex, 0, textures, 0, flowerPotTex.length);
        System.arraycopy(cactusTex, 0, textures, flowerPotTex.length, cactusTex.length);
        break;
      case FERN:
        quads = Model.join(flowerPot, flowerSmall);
        textures = new Texture[flowerPotTex.length + flowerSmall.length];
        tints = new Tint[flowerPotTex.length + flowerSmall.length];
        System.arraycopy(flowerPotTex, 0, textures, 0, flowerPotTex.length);
        Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.fern);
        Arrays.fill(tints, 0, flowerPotTex.length, Tint.NONE);
        Arrays.fill(tints, flowerPotTex.length, tints.length, Tint.BIOME_GRASS);
        break;
      case BAMBOO:
        quads = Model.join(flowerPot, bamboo, bambooLeaf);
        textures = new Texture[flowerPotTex.length + bamboo.length + bambooLeaf.length];
        tints = null;
        System.arraycopy(flowerPotTex, 0, textures, 0, flowerPotTex.length);
        Arrays.fill(textures, flowerPotTex.length, flowerPotTex.length + bamboo.length, Texture.bambooStalk);
        Arrays.fill(textures, flowerPotTex.length + bamboo.length, textures.length, Texture.bambooSingleLeaf);
        break;
      case AZALEA_BUSH:
      case FLOWERING_AZALEA_BUSH:
        quads = Model.join(flowerPot, azaleaBush);
        textures = new Texture[flowerPotTex.length + azaleaBush.length];
        tints = null;
        System.arraycopy(flowerPotTex, 0, textures, 0, flowerPotTex.length);
        System.arraycopy(kind == Kind.AZALEA_BUSH ? azaleaBushTex : floweringAzaleaBushTex, 0, textures,
          flowerPotTex.length, textures.length - flowerPotTex.length);
        break;
      case MANGROVE_PROPAGULE:
        quads = Model.join(flowerPot, mangrovePropagule);
        textures = new Texture[flowerPotTex.length + mangrovePropagule.length];
        tints = null;
        System.arraycopy(flowerPotTex, 0, textures, 0, flowerPotTex.length);
        Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.mangrovePropagule);
        break;
      default:
        quads = Model.join(flowerPot, flowerSmall);
        textures = new Texture[flowerPotTex.length + flowerSmall.length];
        tints = null;
        System.arraycopy(flowerPotTex, 0, textures, 0, flowerPotTex.length);
        switch (kind) {
          case POPPY:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.poppy);
            break;
          case DANDELION:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.dandelion);
            break;
          case OAK_SAPLING:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.oakSapling);
            break;
          case SPRUCE_SAPLING:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.spruceSapling);
            break;
          case BIRCH_SAPLING:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.birchSapling);
            break;
          case JUNGLE_SAPLING:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.jungleSapling);
            break;
          case ACACIA_SAPLING:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.acaciaSapling);
            break;
          case DARK_OAK_SAPLING:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.darkOakSapling);
            break;
          case RED_MUSHROOM:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.redMushroom);
            break;
          case BROWN_MUSHROOM:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.brownMushroom);
            break;
          case DEAD_BUSH:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.deadBush);
            break;
          case BLUE_ORCHID:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.blueOrchid);
            break;
          case ALLIUM:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.allium);
            break;
          case AZURE_BLUET:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.azureBluet);
            break;
          case RED_TULIP:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.redTulip);
            break;
          case ORANGE_TULIP:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.orangeTulip);
            break;
          case WHITE_TULIP:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.whiteTulip);
            break;
          case PINK_TULIP:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.pinkTulip);
            break;
          case OXEYE_DAISY:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.oxeyeDaisy);
            break;
          case CORNFLOWER:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.cornflower);
            break;
          case LILY_OF_THE_VALLEY:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.lilyOfTheValley);
            break;
          case WITHER_ROSE:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.witherRose);
            break;
          case WARPED_FUNGUS:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.warpedFungus);
            break;
          case CRIMSON_FUNGUS:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.crimsonFungus);
            break;
          case WARPED_ROOTS:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.warpedRootsPot);
            break;
          case CRIMSON_ROOTS:
            Arrays.fill(textures, flowerPotTex.length, textures.length, Texture.crimsonRootsPot);
            break;
        }
        break;
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

  @Override
  public Tint[] getTints() {
    return tints;
  }
}
