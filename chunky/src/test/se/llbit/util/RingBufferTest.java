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

import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class RingBufferTest {
	@Test
	public void testEmpty() {
		assertTrue(new RingBuffer<Integer>(10).isEmpty());
	}

	@Test
	public void testEmpty2() {
		RingBuffer<Integer> buff = new RingBuffer<>(10);
		buff.append(1);
		assertFalse(buff.isEmpty());
	}

	@Test
	public void testRemove() {
		RingBuffer<Integer> buff = new RingBuffer<>(10);
		buff.append(123);
		assertEquals((Integer) 123, buff.remove());
	}

	@Test
	public void testRemove2() {
		RingBuffer<String> buff = new RingBuffer<>(3);
		buff.append("A");
		buff.append("B");
		buff.append("C");
		buff.append("D");
		assertEquals("A", buff.remove());
		assertEquals("B", buff.remove());
		assertEquals("C", buff.remove());
	}

	@Test
	public void testRemove3() {
		RingBuffer<String> buff = new RingBuffer<>(50);
		try {
			buff.remove();
			fail("Expected exception.");
		} catch (NoSuchElementException e) {
		}
	}

	@Test
	public void testRemove4() {
		RingBuffer<String> buff = new RingBuffer<>(3);
		buff.append("A");
		buff.append("B");
		buff.append("C");
		buff.append("D");
		buff.remove();
		buff.remove();
		buff.remove();
		try {
			buff.remove();
			fail("Expected exception.");
		} catch (NoSuchElementException e) {
		}
	}
}
