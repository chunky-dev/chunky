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
import se.llbit.chunky.block.Block;

import static org.junit.Assert.assertEquals;

/**
 * These are not exhaustive tests of all block properties.
 * This is just some minimal sanity checking to test
 * that a few properties are correct.
 */
public class TestBlockProperties {

  @BeforeClass public static void setDefaults() {
    Block.loadDefaultMaterialProperties();
  }

  @Test public void testSolid() {
    assertEquals(false, Block.AIR.solid);
    assertEquals(false, Block.WATER.solid);
    assertEquals(true, Block.STONE.solid);
    assertEquals(true, Block.SAND.solid);
    assertEquals(true, Block.GRASS.solid);
  }

  @Test public void testInvisible() {
    // The invisible flag indicates if a block should be represented as a voxel.
    assertEquals(true, Block.AIR.invisible);
    assertEquals(false, Block.STONE.invisible);
    assertEquals(true, Block.PISTON_EXTENSION.invisible);
    assertEquals(true, Block.SIGNPOST.invisible);
    assertEquals(true, Block.WALLSIGN.invisible);
    assertEquals(true, Block.LILY_PAD.invisible);
    assertEquals(true, Block.HEAD.invisible);
    assertEquals(true, Block.BARRIER.invisible);
  }

  @Test public void testFenceConn1() {
    assertEquals(false, Block.AIR.isFenceConnector(0, 0));
    assertEquals(false, Block.WATER.isFenceConnector(0, 0));
    assertEquals(false, Block.FLOWER.isFenceConnector(0, 0));
    assertEquals(false, Block.CROPS.isFenceConnector(0, 0));
    assertEquals(false, Block.SUGARCANE.isFenceConnector(0, 0));
    assertEquals(false, Block.CARROTS.isFenceConnector(0, 0));
    assertEquals(false, Block.MELON.isFenceConnector(0, 0));
    assertEquals(false, Block.PUMPKIN.isFenceConnector(0, 0));
    assertEquals(false, Block.JACKOLANTERN.isFenceConnector(0, 0));
    assertEquals(true, Block.FURNACEUNLIT.isFenceConnector(0, 0));
    assertEquals(false, Block.TRAPDOOR.isFenceConnector(0, 0));
    assertEquals(false, Block.IRON_TRAPDOOR.isFenceConnector(0, 0));

    // Stair connections are direction dependent: only connect when facing away from the fence.
    assertEquals(true, Block.OAKWOODSTAIRS.isFenceConnector(BlockData.NORTH, BlockData.SOUTH));
    assertEquals(false, Block.OAKWOODSTAIRS.isFenceConnector(BlockData.NORTH, BlockData.EAST));
    assertEquals(false, Block.OAKWOODSTAIRS.isFenceConnector(BlockData.NORTH, BlockData.NORTH));

    // Nether brick fences should not connect to regular fences.
    assertEquals(false, Block.NETHERBRICKFENCE.isFenceConnector(0, 0));
    assertEquals(false, Block.FENCE.isNetherBrickFenceConnector(0, 0));
    assertEquals(false, Block.DARKOAKFENCE.isNetherBrickFenceConnector(0, 0));

    // Glass panes should connect to iron bars.
    assertEquals(true, Block.IRONBARS.isGlassPaneConnector(0, 0));
    assertEquals(true, Block.GLASSPANE.isIronBarsConnector(0, 0));
    assertEquals(true, Block.STAINED_GLASSPANE.isIronBarsConnector(0, 0));

    // Glass panes should not connect to fences.
    assertEquals(false, Block.FENCE.isGlassPaneConnector(0, 0));
    assertEquals(false, Block.BIRCHFENCE.isGlassPaneConnector(0, 0));
    assertEquals(false, Block.GLASSPANE.isFenceConnector(0, 0));
    assertEquals(false, Block.STAINED_GLASSPANE.isFenceConnector(0, 0));
    assertEquals(false, Block.NETHERBRICKFENCE.isGlassPaneConnector(0, 0));
    assertEquals(false, Block.STAINED_GLASSPANE.isNetherBrickFenceConnector(0, 0));

    // Fences should not connect to glass blocks.
    assertEquals(false, Block.GLASS.isFenceConnector(0, 0));
    assertEquals(false, Block.GLASS.isNetherBrickFenceConnector(0, 0));
    assertEquals(false, Block.STAINED_GLASS.isFenceConnector(0, 0));
    assertEquals(false, Block.STAINED_GLASS.isNetherBrickFenceConnector(0, 0));

    // Fences should not connect to glass panes.
    assertEquals(false, Block.GLASSPANE.isFenceConnector(0, 0));
    assertEquals(false, Block.GLASSPANE.isNetherBrickFenceConnector(0, 0));
    assertEquals(false, Block.STAINED_GLASSPANE.isFenceConnector(0, 0));
    assertEquals(false, Block.STAINED_GLASSPANE.isNetherBrickFenceConnector(0, 0));

    // Fences should not connect to iron bars.
    assertEquals(false, Block.FENCE.isIronBarsConnector(0, 0));
    assertEquals(false, Block.SPRUCEFENCE.isIronBarsConnector(0, 0));
    assertEquals(false, Block.DARKOAKFENCE.isIronBarsConnector(0, 0));
    assertEquals(false, Block.JUNGLEFENCE.isIronBarsConnector(0, 0));
    assertEquals(false, Block.ACACIAFENCE.isIronBarsConnector(0, 0));
    assertEquals(false, Block.NETHERBRICKFENCE.isIronBarsConnector(0, 0));
    assertEquals(false, Block.IRONBARS.isFenceConnector(0, 0));
    assertEquals(false, Block.IRONBARS.isNetherBrickFenceConnector(0, 0));

    // Iron bars should not connect to fence gates.
    assertEquals(false, Block.FENCEGATE.isIronBarsConnector(0, 0));
    assertEquals(false, Block.SPRUCEFENCEGATE.isIronBarsConnector(0, 0));
    assertEquals(false, Block.ACACIAFENCEGATE.isIronBarsConnector(0, 0));

    // Fences should not connect to leaf blocks.
    assertEquals(false, Block.LEAVES.isFenceConnector(0, 0));
    assertEquals(false, Block.LEAVES.isNetherBrickFenceConnector(0, 0));
    assertEquals(false, Block.LEAVES2.isFenceConnector(0, 0));
    assertEquals(false, Block.LEAVES2.isNetherBrickFenceConnector(0, 0));

    // Fences should not connect to stone walls.
    assertEquals(false, Block.FENCE.isStoneWallConnector(0, 0));
    assertEquals(false, Block.SPRUCEFENCE.isStoneWallConnector(0, 0));
    assertEquals(false, Block.DARKOAKFENCE.isStoneWallConnector(0, 0));
    assertEquals(false, Block.JUNGLEFENCE.isStoneWallConnector(0, 0));
    assertEquals(false, Block.ACACIAFENCE.isStoneWallConnector(0, 0));
    assertEquals(false, Block.NETHERBRICKFENCE.isStoneWallConnector(0, 0));
    assertEquals(false, Block.STONEWALL.isFenceConnector(0, 0));
    assertEquals(false, Block.STONEWALL.isNetherBrickFenceConnector(0, 0));

    // Iron bars should not connect to stone walls.
    assertEquals(false, Block.IRONBARS.isStoneWallConnector(0, 0));
    assertEquals(false, Block.STONEWALL.isIronBarsConnector(0, 0));

    // Glass panes should not connect to stone walls.
    assertEquals(false, Block.GLASSPANE.isStoneWallConnector(0, 0));
    assertEquals(false, Block.STAINED_GLASSPANE.isStoneWallConnector(0, 0));
    assertEquals(false, Block.STONEWALL.isGlassPaneConnector(0, 0));

    // Stone walls should not connect to leaf blocks.
    assertEquals(false, Block.LEAVES.isStoneWallConnector(0, 0));
    assertEquals(false, Block.LEAVES2.isStoneWallConnector(0, 0));

    // Stone walls should not connect to glass blocks.
    assertEquals(false, Block.GLASS.isStoneWallConnector(0, 0));
    assertEquals(false, Block.STAINED_GLASS.isStoneWallConnector(0, 0));

    // Glass panes should not connect to leaf blocks.
    assertEquals(false, Block.LEAVES.isGlassPaneConnector(0, 0));
    assertEquals(false, Block.LEAVES2.isGlassPaneConnector(0, 0));

    // Iron bars should not connect to leaf blocks.
    assertEquals(false, Block.LEAVES.isIronBarsConnector(0, 0));
    assertEquals(false, Block.LEAVES2.isIronBarsConnector(0, 0));
  }

  @Test public void testWallTopConnector() {
    assertEquals(true, Block.ENDROD.isWallTopConnector());
    assertEquals(true, Block.HOPPER.isWallTopConnector());
    assertEquals(true, Block.LEAVES.isWallTopConnector());
    assertEquals(true, Block.LEAVES2.isWallTopConnector());
    assertEquals(true, Block.REDSTONETORCHON.isWallTopConnector());
    assertEquals(true, Block.TORCH.isWallTopConnector());
    assertEquals(true, Block.STONE.isWallTopConnector());
    assertEquals(true, Block.ENDERCHEST.isWallTopConnector());
    assertEquals(true, Block.DRAGONEGG.isWallTopConnector());
  }
}
