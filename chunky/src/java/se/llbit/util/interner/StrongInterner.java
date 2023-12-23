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

import java.util.HashMap;

/**
 * A simple interner that keeps strong references to interned objects.
 */
public class StrongInterner<T> implements Interner<T> {
  protected final HashMap<T, T> pool = new HashMap<>();

  @Override
  public T maybeIntern(T sample) {
    T interned = pool.get(sample);
    if (interned != null) {
      return interned;
    }
    pool.put(sample, sample);
    return null;
  }

  @Override
  public T intern(T sample) {
    return pool.computeIfAbsent(sample, k -> k);
  }
}
