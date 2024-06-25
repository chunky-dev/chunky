/*
 * Copyright (c) 2023 Chunky contributors
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

package se.llbit.util;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import se.llbit.util.interner.Interner;
import se.llbit.util.interner.StrongInterner;
import se.llbit.util.interner.WeakInterner;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class WeakInternerTest {
  private static class TestInternable {
    public final long value;

    public TestInternable(long value) {
      this.value = value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof TestInternable)) return false;
      TestInternable that = (TestInternable) o;
      return value == that.value;
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }
  }

  @Test
  public void testWeakInternerSmokeIntern() {
    smokeIntern(new WeakInterner<>());
  }

  @Test
  public void testStrongInternerSmokeIntern() {
    smokeIntern(new StrongInterner<>());
  }

  private void smokeIntern(Interner<TestInternable> interner) {
    Random rand = new Random(0);

    TestInternable test11 = new TestInternable(rand.nextLong());
    TestInternable test12 = new TestInternable(rand.nextLong());
    TestInternable test13 = new TestInternable(rand.nextLong());
    TestInternable test21 = new TestInternable(test11.value);
    TestInternable test22 = new TestInternable(test12.value);
    TestInternable test23 = new TestInternable(test13.value);

    // Test that we get the same object back when interning the object.
    assertSame(interner.intern(test11), test11);
    assertSame(interner.intern(test12), test12);
    assertSame(interner.intern(test13), test13);

    // Test that we get back the interned object when interning the copy
    assertSame(interner.intern(test21), test11);
    assertSame(interner.intern(test22), test12);
    assertSame(interner.intern(test23), test13);
  }

  @Test
  public void testWeakInternerSmokeMaybeIntern() {
    smokeMaybeIntern(new WeakInterner<>());
  }

  @Test
  public void testStrongInternerSmokeMaybeIntern() {
    smokeMaybeIntern(new StrongInterner<>());
  }

  private void smokeMaybeIntern(Interner<TestInternable> interner) {
    Random rand = new Random(0);

    TestInternable test11 = new TestInternable(rand.nextLong());
    TestInternable test12 = new TestInternable(rand.nextLong());
    TestInternable test13 = new TestInternable(rand.nextLong());
    TestInternable test21 = new TestInternable(test11.value);
    TestInternable test22 = new TestInternable(test12.value);
    TestInternable test23 = new TestInternable(test13.value);

    // Test that we get `null` when we maybeIntern the object.
    assertSame(interner.maybeIntern(test11), null);
    assertSame(interner.maybeIntern(test12), null);
    assertSame(interner.maybeIntern(test13), null);

    // Test that we get back the interned object when interning the copy
    assertSame(interner.maybeIntern(test21), test11);
    assertSame(interner.maybeIntern(test22), test12);
    assertSame(interner.maybeIntern(test23), test13);
  }

  @Test
  public void testWeakness() {
    Random rand = new Random(0);
    Interner<Object> interner = new WeakInterner<>();

    TestInternable test11 = new TestInternable(rand.nextLong());
    TestInternable test12 = new TestInternable(rand.nextLong());
    TestInternable test13 = new TestInternable(rand.nextLong());
    TestInternable test21 = new TestInternable(test11.value);
    TestInternable test22 = new TestInternable(test12.value);
    TestInternable test23 = new TestInternable(test13.value);

    // Test that we get the same object back when interning the object.
    assertSame(interner.intern(test11), test11);
    assertSame(interner.intern(test12), test12);
    assertSame(interner.intern(test13), test13);

    // Test that we get back the interned object when interning the copy
    assertSame(interner.intern(test21), test11);
    assertSame(interner.intern(test22), test12);
    assertSame(interner.intern(test23), test13);

    // Get references to the interned objects
    WeakReference<TestInternable> weakTest1 = new WeakReference<>(test11);
    WeakReference<TestInternable> weakTest2 = new WeakReference<>(test12);
    WeakReference<TestInternable> weakTest3 = new WeakReference<>(test13);

    // Clear their references
    test11 = null;
    test12 = null;
    test13 = null;

    // Wait until they are garbage collected
    for (int i = 0; i < 1000; i++) {
      System.gc();
      if (weakTest1.get() == null && weakTest2.get() == null && weakTest3.get() == null) {
        break;
      }
    }
    assumeTrue(weakTest1.get() == null);
    assumeTrue(weakTest2.get() == null);
    assumeTrue(weakTest3.get() == null);

    // Test that we get the copy back when interning the copy
    assertSame(interner.intern(test21), test21);
    assertSame(interner.intern(test22), test22);
    assertSame(interner.intern(test23), test23);
  }
}
