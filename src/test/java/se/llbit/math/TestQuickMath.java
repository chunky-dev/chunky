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
