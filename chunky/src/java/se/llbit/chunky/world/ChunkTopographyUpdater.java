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
package se.llbit.chunky.world;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Renders topography layer for chunks on demand.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class ChunkTopographyUpdater extends Thread {

  private final Set<Chunk> queue = new HashSet<>();

  /**
   * Create new chunk parser
   */
  public ChunkTopographyUpdater() {
    super("Chunk Topography Updater");
  }

  @Override public void run() {
    try {
      while (!isInterrupted()) {
        Chunk chunk = getNext();
        chunk.renderTopography();
      }
    } catch (InterruptedException e) {
    }
  }

  /**
   * Get next chunk from the parse queue
   *
   * @throws InterruptedException
   */
  private synchronized Chunk getNext() throws InterruptedException {
    while (queue.isEmpty()) {
      wait();
    }
    Iterator<Chunk> iter = queue.iterator();
    Chunk chunk = iter.next();
    iter.remove();
    return chunk;
  }

  /**
   * Add a chunk to the parse queue.
   */
  public synchronized void addChunk(Chunk chunk) {
    queue.add(chunk);
    notify();
  }

  /**
   * Remove all chunks from the parse queue.
   */
  public synchronized void clearQueue() {
    queue.clear();
  }

  /**
   * @return <code>true</code> if the work queue is not empty
   */
  public synchronized boolean isWorking() {
    // TODO: add loading indicator.
    return !queue.isEmpty();
  }

}
