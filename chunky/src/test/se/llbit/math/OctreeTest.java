package se.llbit.math;

import org.junit.jupiter.api.Test;

import static se.llbit.testutil.TestUtils.assertThrowsWithExpectedMessage;

public class OctreeTest {
  @Test
  public void test() {
    int depth = 5;
    int cubeDepth = 3;

    Octree octree = new Octree(new PackedOctree(depth));

    int cubeSize = 1 << cubeDepth;
    int[] types = new int[cubeSize * cubeSize * cubeSize];
    int cubesWithinOctree = (1 << depth) - cubeSize; // the number of times the cube can fit along each dimension of the octree
    for (int x = 0; x < cubesWithinOctree; x++) {
      for (int y = 0; y < cubesWithinOctree; y++) {
        for (int z = 0; z < cubesWithinOctree; z++) {
          octree.setCube(cubeDepth, types, x, y, z);
        }
      }
    }

    assertThrowsWithExpectedMessage(AssertionError.class, () -> octree.setCube(cubeDepth, types, 25, 0, 0),
      "setCube x (25,33) out of bounds for octree (0,31)");
    assertThrowsWithExpectedMessage(AssertionError.class, () -> octree.setCube(cubeDepth, types, 0, 25, 0),
      "setCube y (25,33) out of bounds for octree (0,31)");
    assertThrowsWithExpectedMessage(AssertionError.class, () -> octree.setCube(cubeDepth, types, 0, 0, 25),
      "setCube z (25,33) out of bounds for octree (0,31)");
    assertThrowsWithExpectedMessage(AssertionError.class, () -> octree.setCube(cubeDepth, types, 64, 13, 23),
      "setCube x (64,72) out of bounds for octree (0,31)");

    assertThrowsWithExpectedMessage(AssertionError.class, () -> octree.setCube(cubeDepth, types, -1, 22, 1),
      "setCube position must not be negative (-1,22,1)");
    assertThrowsWithExpectedMessage(AssertionError.class, () -> octree.setCube(cubeDepth, types, 5, -123, 23),
      "setCube position must not be negative (5,-123,23)");
    assertThrowsWithExpectedMessage(AssertionError.class, () -> octree.setCube(cubeDepth, types, 17, 9, -32),
      "setCube position must not be negative (17,9,-32)");

  }
}
