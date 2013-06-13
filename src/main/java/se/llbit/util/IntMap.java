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
package se.llbit.util;
import org.apache.commons.math3.util.FastMath;

import java.util.Iterator;

/**
 * A hash map that uses integer keys
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 *
 * @param <V>
 */
public class IntMap<V> implements Iterable<V> {

	private static class Entry<V> {
		int key;
		V value;
		Entry<V> next;

		Entry(int key, V value) {
			this.key = key;
			this.value = value;
		}
	}

	private static class EntryIterator<V> implements Iterator<V>{
		private int bucket = 0;
		private Entry<V> entry;
		private Entry<V>[] buckets;

		EntryIterator(Entry<V>[] buckets) {
			this.buckets = buckets;
		}

		@Override
		public boolean hasNext() {
			while (entry == null && bucket < NUM_BUCKETS) {
				entry = buckets[bucket++];
			}
			return entry != null;
		}

		@Override
		public V next() {
			if (entry == null) {
				return null;
			} else {
				V value = entry.value;
				entry = entry.next;
				return value;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private static final int NUM_BUCKETS = 10000;
	private int size = 0;

	@SuppressWarnings("unchecked")
	private Entry<V>[] buckets = new Entry[NUM_BUCKETS];

	/**
	 * @return Number of key-value pairs in the map
	 */
	public int size() {
		return size;
	}

	/**
	 * @return <code>true</code> if the map is empty
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * @param key
	 * @return <code>true</code> if the map contains the given key
	 */
	public boolean containsKey(int key) {
		Entry<V> bucket = buckets[hash(key)];
		while (bucket != null && bucket.key < key)
			bucket = bucket.next;
		return bucket != null && bucket.key == key;
	}

	private static final int hash(int key) {
		return FastMath.abs(key % NUM_BUCKETS);
	}

	/**
	 * Insert a new key-value pair into the map.
	 * @param key
	 * @param value
	 */
	public void put(int key, V value) {
		int hash = hash(key);
		Entry<V> bucket = buckets[hash];
		if (bucket == null) {
			bucket = new Entry<V>(key, value);
			buckets[hash] = bucket;
			size += 1;
		} else {
			while (bucket.next != null && key > bucket.key)
				bucket = bucket.next;
			if (key == bucket.key) {
				bucket.value = value;
			} else {
				bucket.next = new Entry<V>(key, value);
				size += 1;
			}
		}
	}

	/**
	 * @param key
	 * @return The value corresponding to the given key,
	 * or <code>null</code> if no such key exists
	 */
	public V get(int key) {
		Entry<V> bucket = buckets[hash(key)];
		while (bucket != null && bucket.key < key)
			bucket = bucket.next;
		if (bucket != null && bucket.key == key)
			return bucket.value;
		else
			return null;
	}

	/**
	 * Remove the given key from the map
	 * @param key
	 */
	public void remove(int key) {
		int hash = hash(key);
		Entry<V> bucket = buckets[hash];
		if (bucket != null) {
			if (bucket.key == key) {
				buckets[hash] = bucket.next;
				size -= 1;
			} else {
				while (bucket.next != null && bucket.next.key < key)
					bucket = bucket.next;
				if (bucket.next.key == key) {
					bucket.next = bucket.next.next;
					size -= 1;
				}
			}
		}
	}

	/**
	 * Remove all elements from the map.
	 */
	public void clear() {
		for (int i = 0; i < buckets.length; ++i) {
			buckets[i] = null;
		}
		size = 0;
	}

	/**
	 * @return An iterator to iterate over the values in the map
	 */
	@Override
	public Iterator<V> iterator() {
		return new EntryIterator<V>(buckets);
	}
}
