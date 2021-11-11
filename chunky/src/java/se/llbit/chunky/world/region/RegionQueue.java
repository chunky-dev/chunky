/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world.region;

import se.llbit.chunky.world.ChunkPosition;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Queue of region positions.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RegionQueue {

  private final Queue<ChunkPosition> queue = new LinkedList<>();
  private final Set<ChunkPosition> set = new HashSet<>();

  public synchronized ChunkPosition poll() {
    try {
      while (queue.isEmpty()) {
        wait();
      }
    } catch (InterruptedException e) {
      return null;
    }
    ChunkPosition position = queue.poll();
    set.remove(position);
    return position;
  }

  public synchronized boolean add(ChunkPosition position) {
    if (!set.contains(position)) {
      queue.add(position);
      set.add(position);
      notifyAll();
      return true;
    }
    return false;
  }

  public synchronized void clear() {
    queue.clear();
    set.clear();
  }

  public synchronized boolean isEmpty() {
    return queue.isEmpty();
  }
}
