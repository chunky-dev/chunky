/* Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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

import java.util.NoSuchElementException;

/**
 * This is a bounded size FIFO queue that has a nonblocking append operation.
 * Any items that do not fit in the queue are discarded.
 */
public class RingBuffer<T> {
  private final Object[] data;
  private final int capacity;
  private int size; // Current size.
  private int remove = 0; // Position to remove next.

  public RingBuffer(int capacity) {
    this.capacity = capacity;
    data = new Object[capacity];
  }

  /**
   * Add an item to the buffer. This operation does not block: if the buffer
   * is full the item is discarded.
   * @return {@code true} if the item was added to the queue.
   */
  public boolean append(T item) {
    if (size < capacity) {
      data[(remove + size) % capacity] = item;
      size += 1;
      return true;
    }
    return false;
  }

  /**
   * Remove an element from the buffer.
   */
  public T remove() {
    if (size == 0) {
      throw new NoSuchElementException("Buffer is empty.");
    }
    T result = (T) data[remove];
    remove = (remove + 1) % capacity;
    size -= 1;
    return result;
  }

  public boolean isEmpty() {
    return size == 0;
  }
}
