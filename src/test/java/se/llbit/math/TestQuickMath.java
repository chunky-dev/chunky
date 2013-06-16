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
	 * Test double precision minimum func.
	 */
	@Test
	public void testMin_1() {
		assertTrue(11.0 == QuickMath.min(132.0, 11.0));
		assertTrue(11.0 == QuickMath.min(11.0, 132.0));
		assertTrue(-132.0 == QuickMath.min(-132.0, -11.0));
		assertTrue(-132.0 == QuickMath.min(-11.0, -132.0));
	}

	/**
	 * Test double NaN in minimum func.
	 */
	@Test
	public void testMin_2() {
		assertTrue(11.0 == QuickMath.min(Double.NaN, 11.0));
		assertTrue(Double.isNaN(QuickMath.min(11.0, Double.NaN)));
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
