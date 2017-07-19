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
    assertEquals(false, Block.AIR.isFenceConnector());
    assertEquals(false, Block.WATER.isFenceConnector());
    assertEquals(false, Block.FLOWER.isFenceConnector());
    assertEquals(false, Block.CROPS.isFenceConnector());
    assertEquals(false, Block.SUGARCANE.isFenceConnector());
    assertEquals(false, Block.CARROTS.isFenceConnector());
    assertEquals(false, Block.MELON.isFenceConnector());
    assertEquals(false, Block.PUMPKIN.isFenceConnector());
    assertEquals(false, Block.JACKOLANTERN.isFenceConnector());
    assertEquals(true, Block.FURNACEUNLIT.isFenceConnector());
    assertEquals(true, Block.OAKWOODSTAIRS.isFenceConnector());
    assertEquals(false, Block.TRAPDOOR.isFenceConnector());
    assertEquals(false, Block.IRON_TRAPDOOR.isFenceConnector());

    // Nether brick fences should not connect to regular fences.
    assertEquals(false, Block.NETHERBRICKFENCE.isFenceConnector());
    assertEquals(false, Block.FENCE.isNetherBrickFenceConnector());
    assertEquals(false, Block.DARKOAKFENCE.isNetherBrickFenceConnector());

    // Glass panes should connect to iron bars.
    assertEquals(true, Block.IRONBARS.isGlassPaneConnector());
    assertEquals(true, Block.GLASSPANE.isIronBarsConnector());

    // Glass panes should not connect to fences.
    assertEquals(false, Block.FENCE.isGlassPaneConnector());
    assertEquals(false, Block.BIRCHFENCE.isGlassPaneConnector());
    assertEquals(false, Block.GLASSPANE.isFenceConnector());
    assertEquals(false, Block.NETHERBRICKFENCE.isGlassPaneConnector());
    assertEquals(false, Block.GLASSPANE.isNetherBrickFenceConnector());

    // Fences should not connect to glass blocks.
    assertEquals(false, Block.GLASS.isFenceConnector());
    assertEquals(false, Block.GLASS.isNetherBrickFenceConnector());

    // Fences should not connect to iron bars.
    assertEquals(false, Block.FENCE.isIronBarsConnector());
    assertEquals(false, Block.SPRUCEFENCE.isIronBarsConnector());
    assertEquals(false, Block.DARKOAKFENCE.isIronBarsConnector());
    assertEquals(false, Block.JUNGLEFENCE.isIronBarsConnector());
    assertEquals(false, Block.ACACIAFENCE.isIronBarsConnector());
    assertEquals(false, Block.NETHERBRICKFENCE.isIronBarsConnector());
    assertEquals(false, Block.IRONBARS.isFenceConnector());
    assertEquals(false, Block.IRONBARS.isNetherBrickFenceConnector());

    // Iron bars should not connect to fence gates.
    assertEquals(false, Block.FENCEGATE.isIronBarsConnector());
    assertEquals(false, Block.SPRUCEFENCEGATE.isIronBarsConnector());
    assertEquals(false, Block.ACACIAFENCEGATE.isIronBarsConnector());
  }
}
