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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    assertFalse(Block.AIR.isSolid);
    assertFalse(Block.WATER.isSolid);
    assertTrue(Block.STONE.isSolid);
    assertTrue(Block.SAND.isSolid);
    assertTrue(Block.GRASS.isSolid);
  }

  @Test public void testFenceConn1() {
    assertFalse(Block.AIR.isFenceConnector());
    assertFalse(Block.WATER.isFenceConnector());
    assertFalse(Block.FLOWER.isFenceConnector());
    assertFalse(Block.CROPS.isFenceConnector());
    assertFalse(Block.SUGARCANE.isFenceConnector());
    assertFalse(Block.CARROTS.isFenceConnector());
  }
}
