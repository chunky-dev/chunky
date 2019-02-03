/*
 * Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world;

import org.junit.BeforeClass;
import org.junit.Test;
import se.llbit.chunky.block.Air;
import se.llbit.chunky.idblock.IdBlock;

import static org.junit.Assert.assertEquals;

/**
 * These are not exhaustive tests of all block properties.
 * This is just some minimal sanity checking to test
 * that a few properties are correct.
 */
public class TestBlockProperties {

  @BeforeClass public static void setDefaults() {
    IdBlock.loadDefaultMaterialProperties();
  }

  @Test public void testSolid() {
    assertEquals(false, Air.INSTANCE.solid);
    assertEquals(false, IdBlock.WATER.solid);
    assertEquals(true, IdBlock.STONE.solid);
    assertEquals(true, IdBlock.SAND.solid);
    assertEquals(true, IdBlock.GRASS.solid);
  }

  @Test public void testInvisible() {
    // The invisible flag indicates if a block should be represented as a voxel.
    assertEquals(true, IdBlock.AIR.invisible);
    assertEquals(false, IdBlock.STONE.invisible);
    assertEquals(true, IdBlock.PISTON_EXTENSION.invisible);
    assertEquals(true, IdBlock.SIGNPOST.invisible);
    assertEquals(true, IdBlock.WALLSIGN.invisible);
    assertEquals(true, IdBlock.LILY_PAD.invisible);
    assertEquals(true, IdBlock.HEAD.invisible);
    assertEquals(true, IdBlock.BARRIER.invisible);
  }

  @Test public void testFenceConn1() {
    assertEquals(false, IdBlock.AIR.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.WATER.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.FLOWER.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.CROPS.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.SUGARCANE.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.CARROTS.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.MELON.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.PUMPKIN.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.JACKOLANTERN.isFenceConnector(0, 0));
    assertEquals(true, IdBlock.FURNACEUNLIT.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.TRAPDOOR.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.IRON_TRAPDOOR.isFenceConnector(0, 0));

    // Stair connections are direction dependent: only connect when facing away from the fence.
    assertEquals(true, IdBlock.OAKWOODSTAIRS.isFenceConnector(BlockData.NORTH, BlockData.SOUTH));
    assertEquals(false, IdBlock.OAKWOODSTAIRS.isFenceConnector(BlockData.NORTH, BlockData.EAST));
    assertEquals(false, IdBlock.OAKWOODSTAIRS.isFenceConnector(BlockData.NORTH, BlockData.NORTH));

    // Nether brick fences should not connect to regular fences.
    assertEquals(false, IdBlock.NETHERBRICKFENCE.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.FENCE.isNetherBrickFenceConnector(0, 0));
    assertEquals(false, IdBlock.DARKOAKFENCE.isNetherBrickFenceConnector(0, 0));

    // Glass panes should connect to iron bars.
    assertEquals(true, IdBlock.IRONBARS.isGlassPaneConnector(0, 0));
    assertEquals(true, IdBlock.GLASSPANE.isIronBarsConnector(0, 0));
    assertEquals(true, IdBlock.STAINED_GLASSPANE.isIronBarsConnector(0, 0));

    // Glass panes should not connect to fences.
    assertEquals(false, IdBlock.FENCE.isGlassPaneConnector(0, 0));
    assertEquals(false, IdBlock.BIRCHFENCE.isGlassPaneConnector(0, 0));
    assertEquals(false, IdBlock.GLASSPANE.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.STAINED_GLASSPANE.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.NETHERBRICKFENCE.isGlassPaneConnector(0, 0));
    assertEquals(false, IdBlock.STAINED_GLASSPANE.isNetherBrickFenceConnector(0, 0));

    // Fences should not connect to glass blocks.
    assertEquals(false, IdBlock.GLASS.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.GLASS.isNetherBrickFenceConnector(0, 0));
    assertEquals(false, IdBlock.STAINED_GLASS.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.STAINED_GLASS.isNetherBrickFenceConnector(0, 0));

    // Fences should not connect to glass panes.
    assertEquals(false, IdBlock.GLASSPANE.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.GLASSPANE.isNetherBrickFenceConnector(0, 0));
    assertEquals(false, IdBlock.STAINED_GLASSPANE.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.STAINED_GLASSPANE.isNetherBrickFenceConnector(0, 0));

    // Fences should not connect to iron bars.
    assertEquals(false, IdBlock.FENCE.isIronBarsConnector(0, 0));
    assertEquals(false, IdBlock.SPRUCEFENCE.isIronBarsConnector(0, 0));
    assertEquals(false, IdBlock.DARKOAKFENCE.isIronBarsConnector(0, 0));
    assertEquals(false, IdBlock.JUNGLEFENCE.isIronBarsConnector(0, 0));
    assertEquals(false, IdBlock.ACACIAFENCE.isIronBarsConnector(0, 0));
    assertEquals(false, IdBlock.NETHERBRICKFENCE.isIronBarsConnector(0, 0));
    assertEquals(false, IdBlock.IRONBARS.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.IRONBARS.isNetherBrickFenceConnector(0, 0));

    // Iron bars should not connect to fence gates.
    assertEquals(false, IdBlock.FENCEGATE.isIronBarsConnector(0, 0));
    assertEquals(false, IdBlock.SPRUCEFENCEGATE.isIronBarsConnector(0, 0));
    assertEquals(false, IdBlock.ACACIAFENCEGATE.isIronBarsConnector(0, 0));

    // Fences should not connect to leaf blocks.
    assertEquals(false, IdBlock.LEAVES.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.LEAVES.isNetherBrickFenceConnector(0, 0));
    assertEquals(false, IdBlock.LEAVES2.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.LEAVES2.isNetherBrickFenceConnector(0, 0));

    // Fences should not connect to stone walls.
    assertEquals(false, IdBlock.FENCE.isStoneWallConnector(0, 0));
    assertEquals(false, IdBlock.SPRUCEFENCE.isStoneWallConnector(0, 0));
    assertEquals(false, IdBlock.DARKOAKFENCE.isStoneWallConnector(0, 0));
    assertEquals(false, IdBlock.JUNGLEFENCE.isStoneWallConnector(0, 0));
    assertEquals(false, IdBlock.ACACIAFENCE.isStoneWallConnector(0, 0));
    assertEquals(false, IdBlock.NETHERBRICKFENCE.isStoneWallConnector(0, 0));
    assertEquals(false, IdBlock.STONEWALL.isFenceConnector(0, 0));
    assertEquals(false, IdBlock.STONEWALL.isNetherBrickFenceConnector(0, 0));

    // Iron bars should not connect to stone walls.
    assertEquals(false, IdBlock.IRONBARS.isStoneWallConnector(0, 0));
    assertEquals(false, IdBlock.STONEWALL.isIronBarsConnector(0, 0));

    // Glass panes should not connect to stone walls.
    assertEquals(false, IdBlock.GLASSPANE.isStoneWallConnector(0, 0));
    assertEquals(false, IdBlock.STAINED_GLASSPANE.isStoneWallConnector(0, 0));
    assertEquals(false, IdBlock.STONEWALL.isGlassPaneConnector(0, 0));

    // Stone walls should not connect to leaf blocks.
    assertEquals(false, IdBlock.LEAVES.isStoneWallConnector(0, 0));
    assertEquals(false, IdBlock.LEAVES2.isStoneWallConnector(0, 0));

    // Stone walls should not connect to glass blocks.
    assertEquals(false, IdBlock.GLASS.isStoneWallConnector(0, 0));
    assertEquals(false, IdBlock.STAINED_GLASS.isStoneWallConnector(0, 0));

    // Glass panes should not connect to leaf blocks.
    assertEquals(false, IdBlock.LEAVES.isGlassPaneConnector(0, 0));
    assertEquals(false, IdBlock.LEAVES2.isGlassPaneConnector(0, 0));

    // Iron bars should not connect to leaf blocks.
    assertEquals(false, IdBlock.LEAVES.isIronBarsConnector(0, 0));
    assertEquals(false, IdBlock.LEAVES2.isIronBarsConnector(0, 0));
  }

  @Test public void testWallTopConnector() {
    assertEquals(true, IdBlock.ENDROD.isWallTopConnector());
    assertEquals(true, IdBlock.HOPPER.isWallTopConnector());
    assertEquals(true, IdBlock.LEAVES.isWallTopConnector());
    assertEquals(true, IdBlock.LEAVES2.isWallTopConnector());
    assertEquals(true, IdBlock.REDSTONETORCHON.isWallTopConnector());
    assertEquals(true, IdBlock.TORCH.isWallTopConnector());
    assertEquals(true, IdBlock.STONE.isWallTopConnector());
    assertEquals(true, IdBlock.ENDERCHEST.isWallTopConnector());
    assertEquals(true, IdBlock.DRAGONEGG.isWallTopConnector());
  }
}
