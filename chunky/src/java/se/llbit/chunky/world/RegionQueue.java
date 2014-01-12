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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Queue of region positions.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RegionQueue implements Queue<ChunkPosition> {

	private final Queue<ChunkPosition> queue = new LinkedList<ChunkPosition>();
	private final Set<ChunkPosition> set = new HashSet<ChunkPosition>();

	@Override
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

	@Override
	public synchronized boolean add(ChunkPosition position) {
		if (!set.contains(position)) {
			queue.add(position);
			set.add(position);
			notifyAll();
			return true;
		}
		return false;
	}

	@Override
	public synchronized boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public synchronized int size() {
		return queue.size();
	}

	@Override
	public boolean contains(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<ChunkPosition> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized Object[] toArray() {
		return queue.toArray();
	}

	@Override
	public synchronized <T> T[] toArray(T[] a) {
		return queue.toArray(a);
	}

	@Override
	public synchronized boolean remove(Object o) {
		set.remove(o);
		return queue.remove(o);
	}

	@Override
	public synchronized boolean containsAll(Collection<?> c) {
		return set.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends ChunkPosition> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void clear() {
		queue.clear();
		set.clear();
	}

	@Override
	public boolean offer(ChunkPosition e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ChunkPosition remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ChunkPosition element() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ChunkPosition peek() {
		throw new UnsupportedOperationException();
	}

}
