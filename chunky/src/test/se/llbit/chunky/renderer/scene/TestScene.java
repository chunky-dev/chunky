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
package se.llbit.chunky.renderer.scene;

import org.junit.Test;
import se.llbit.chunky.world.Block;

public class TestScene {
  /**
   * Test that modifying material properties does not throw an exception.
   *
   * <p>Regression test: https://www.reddit.com/r/chunky/comments/6l7vqd/chunky_snapshot_143alpha1/djuj3o6/
   */
  @Test public void testModifyMaterial1() {
    new Scene().setEmittance(Block.GRAVEL.getBlockName(), 0.3f);
    new Scene().setSpecular(Block.GRAVEL.getBlockName(), 0.3f);
    new Scene().setIor(Block.GRAVEL.getBlockName(), 0.3f);
  }
}
