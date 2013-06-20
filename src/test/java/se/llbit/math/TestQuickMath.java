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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * TODO add more tests
 * TODO test NaN parameters
 * TODO test +-0.0 parameters
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class TestQuickMath {

	/**
	 * Test double precision minimum.
	 */
	@Test
	public void testMin_1() {
		assertTrue(11.0 == QuickMath.min(132.0, 11.0));
		assertTrue(11.0 == QuickMath.min(11.0, 132.0));
		assertTrue(-132.0 == QuickMath.min(-132.0, -11.0));
		assertTrue(-132.0 == QuickMath.min(-11.0, -132.0));
	}

	/**
	 * If either argument is NaN the first argument is returned.
	 */
	@Test
	public void testMin_2() {
		assertTrue(11.0 == QuickMath.min(Double.NaN, 11.0));
		assertTrue(Double.isNaN(QuickMath.min(11.0, Double.NaN)));
	}

	/**
	 * Test double precision maximum.
	 */
	@Test
	public void testMax_1() {
		assertTrue(132.0 == QuickMath.max(132.0, 11.0));
		assertTrue(132.0 == QuickMath.max(11.0, 132.0));
		assertTrue(-11.0 == QuickMath.max(-132.0, -11.0));
		assertTrue(-11.0 == QuickMath.max(-11.0, -132.0));
	}

	/**
	 * If either argument is NaN the first argument is returned.
	 */
	@Test
	public void testMax_2() {
		assertTrue(11.0 == QuickMath.max(Double.NaN, 11.0));
		assertTrue(Double.isNaN(QuickMath.max(11.0, Double.NaN)));
	}

	/**
	 * Test double precision minimum func.
	 */
	@Test
	public void testAbs_1() {
		assertTrue(1.0 == QuickMath.abs(1.0));
		assertTrue(1.0 == QuickMath.abs(-1.0));
		assertTrue(0.0 == QuickMath.abs(0.0));
		assertTrue(0.0 == QuickMath.abs(-0.0));
	}
}
