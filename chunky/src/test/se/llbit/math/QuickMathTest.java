/* Copyright (c) 2012-2013 Jesper Öqvist <jesper@llbit.se>
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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TODO add more tests
 * TODO test NaN parameters
 * TODO test +-0.0 parameters
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class QuickMathTest {

	/**
	 * Test double precision minimum.
	 */
	@Test
	public void testMin_1() {
    assertEquals(11.0, QuickMath.min(132.0, 11.0));
    assertEquals(11.0, QuickMath.min(11.0, 132.0));
    assertEquals(-132.0, QuickMath.min(-132.0, -11.0));
    assertEquals(-132.0, QuickMath.min(-11.0, -132.0));
	}

	/**
	 * If either argument is NaN the first argument is returned.
	 */
	@Test
	public void testMin_2() {
    assertEquals(11.0, QuickMath.min(Double.NaN, 11.0));
		assertTrue(Double.isNaN(QuickMath.min(11.0, Double.NaN)));
	}

	/**
	 * Test double precision maximum.
	 */
	@Test
	public void testMax_1() {
    assertEquals(132.0, QuickMath.max(132.0, 11.0));
    assertEquals(132.0, QuickMath.max(11.0, 132.0));
    assertEquals(-11.0, QuickMath.max(-132.0, -11.0));
    assertEquals(-11.0, QuickMath.max(-11.0, -132.0));
	}

	/**
	 * If either argument is NaN the first argument is returned.
	 */
	@Test
	public void testMax_2() {
    assertEquals(11.0, QuickMath.max(Double.NaN, 11.0));
		assertTrue(Double.isNaN(QuickMath.max(11.0, Double.NaN)));
	}

	/**
	 * Test double precision minimum func.
	 */
	@Test
	public void testAbs_1() {
    assertEquals(1.0, QuickMath.abs(1.0));
    assertEquals(1.0, QuickMath.abs(-1.0));
    assertEquals(0.0, QuickMath.abs(0.0));
    //assertEquals(0.0, QuickMath.abs(-0.0)); This actually fails... might want to fix // TODO: abs(-0) returns -0
	}

  @Test
  public void testPreviousMultipleOf16() {
    assertEquals(-1293856, QuickMath.prevMul16(-1293856));
    assertEquals(12395856, QuickMath.prevMul16(12395867));
    assertEquals(16, QuickMath.prevMul16(16));
    assertEquals(32, QuickMath.prevMul16(32));
    assertEquals(64, QuickMath.prevMul16(64));
    assertEquals(0, QuickMath.prevMul16(6));
    assertEquals(-16, QuickMath.prevMul16(-1));
    assertEquals(-128, QuickMath.prevMul16(-123));
  }
}
