/* Copyright (c) 2018 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.math;

import org.junit.Test;
import se.llbit.math.primitive.MutableAABB;

import static org.junit.Assert.assertEquals;

public class MutableAABBTest {
  @Test public void testSurfaceArea() {
    MutableAABB unitBox1 = new MutableAABB(0, 1, 0, 1, 0, 1);
    MutableAABB unitBox2 = new MutableAABB(-1, 0, -1, 0, -1, 0);
    MutableAABB unitBox3 = new MutableAABB(-Math.PI, 1-Math.PI, -0.5, 0.5, -0.25, 0.75);
    assertEquals(6, unitBox1.surfaceArea(), 1e-9);
    assertEquals(6, unitBox2.surfaceArea(), 1e-9);
    assertEquals(6, unitBox3.surfaceArea(), 1e-9);

    MutableAABB xBox = new MutableAABB(-1.5, 1.5, 0, 1, 0, 1);
    MutableAABB yBox = new MutableAABB(0, 1, -1.5, 1.5, 0, 1);
    MutableAABB zBox = new MutableAABB(0, 1, 0, 1, -1.5, 1.5);
    assertEquals(14, xBox.surfaceArea(), 1e-9);
    assertEquals(14, yBox.surfaceArea(), 1e-9);
    assertEquals(14, zBox.surfaceArea(), 1e-9);
  }
}
