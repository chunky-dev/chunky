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

package se.llbit.util.interner;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * A simple interner that keeps weak references to interned objects.
 */
public class WeakInterner<T> implements Interner<T> {
  protected static class HashedWeakReference<T> extends WeakReference<T> {
    private final int hash;

    public HashedWeakReference(T referent, ReferenceQueue<? super T> q) {
      super(referent, q);
      this.hash = referent.hashCode();
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof HashedWeakReference)) {
        return false;
      }
      HashedWeakReference<?> other = (HashedWeakReference<?>) obj;

      Object self = this.get();
      if (self == null) {
        return false;
      }

      return self.equals(other.get());
    }
  }

  protected final HashMap<HashedWeakReference<T>, HashedWeakReference<T>> pool = new HashMap<>();
  protected final ReferenceQueue<T> queue = new ReferenceQueue<>();

  @Override
  public T intern(T sample) {
    compact();

    HashedWeakReference<T> ref = new HashedWeakReference<>(sample, queue);
    HashedWeakReference<T> existing = pool.get(ref);

    T obj;
    if (existing != null && (obj = existing.get()) != null) {
      // Existing object exists and is alive
      // Don't enqueue our new ref
      ref.clear();
      return obj;
    } else {
      // Existing object is dead or doesn't exist
      // Enqueue our new ref
      pool.put(ref, ref);
      return sample;
    }
  }

  /**
   * Compact this interner by cleaning up dead references.
   */
  public void compact() {
    HashedWeakReference<T> ref;
    do {
      //noinspection unchecked - only HashedWeakReference<T> objects are added to the queue
      ref = (HashedWeakReference<T>) queue.poll();
      pool.remove(ref);
    } while (ref != null);
  }
}
