/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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

import java.util.LinkedList;
import java.util.Queue;

/**
 * Parses regions
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class RegionParser extends Thread {

	private Queue<Region> queue = new LinkedList<Region>();

	/**
	 * Create new region parser
	 */
	public RegionParser() {
	    super("Region Parser");
	}

	public void run() {
		try {
			while (!isInterrupted()) {
				Region region = getNext();
				region.parse();
			}
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Get next region from the parse queue
	 * @return
	 * @throws InterruptedException
	 */
	private synchronized Region getNext() throws InterruptedException {
		while (queue.isEmpty())
			wait();
		return queue.poll();
	}

	/**
	 * Clear the parse queue
	 */
	public synchronized void clearQueue() {
		queue.clear();
	}

	/**
	 * Add a region to the parse queue
	 * @param region
	 */
	public synchronized void addRegion(Region region) {
		queue.add(region);
		notify();
	}

	/**
	 * @return <code>true</code> if the work queue is not empty
	 */
	public synchronized boolean isWorking() {
		return !queue.isEmpty();
	}
}
