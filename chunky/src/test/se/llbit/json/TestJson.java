/* Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.json;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the Chunky JSON library.
 */
public class TestJson {
  private static JsonValue parse(String json) throws IOException, JsonParser.SyntaxError {
    InputStream input = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
    try (JsonParser parser = new JsonParser(input)) {
      return parser.parse();
    }
  }

  /** Possible to parse empty objects. */
  @Test public void testEmptyObject() throws IOException, JsonParser.SyntaxError {
    assertEquals(0, parse("{}").object().getNumMember());
  }

  /** Possible to parse empty arrays. */
  @Test public void testEmptyArray() throws IOException, JsonParser.SyntaxError {
    assertEquals(0, parse("[]").array().getNumElement());
  }

  /** JsonArray.getNumElement() gives the number of array elements. */
  @Test public void testArray1() throws IOException, JsonParser.SyntaxError {
    assertEquals(2, parse("[1, 2]").array().getNumElement());
    assertEquals(3, parse("[1, 2, 3]").array().getNumElement());
    assertEquals(0, parse("[[]]").array().get(0).array().getNumElement());
  }

  /** JsonArray.getElement() is equivalent to JsonArray.get(). */
  @Test public void testArray2() throws IOException, JsonParser.SyntaxError {
    JsonArray array = parse("[{}, [1,2,3]]").array();
    assertSame(array.getElement(0), array.get(0));
  }

  /** Test the isObject() and isArray methods methods. */
  @Test public void testIsObjectArray() throws IOException, JsonParser.SyntaxError {
    JsonArray array = parse("[{}, [1,2,3], 3, \"foo\", true, false]").array();
    assertTrue(array.get(0).isObject());
    assertTrue(array.get(1).isArray());
    assertFalse(array.get(2).isArray());
    assertFalse(array.get(3).isArray());
  }

  /** Test numbers. */
  @Test public void testNumber1() throws IOException, JsonParser.SyntaxError {
    JsonArray array = parse("[0, 1, 2, 3]").array();
    assertEquals(0, array.get(0).intValue(-1));
    assertEquals(1, array.get(1).longValue(-1));
    assertEquals(2, array.get(2).floatValue(-1), 0.001);
    assertEquals(3, array.get(3).doubleValue(-1), 0.001);
  }

  /** Test string parsing. */
  @Test public void testString1() throws IOException, JsonParser.SyntaxError {
    JsonArray array = parse("[\"foo\", \"bart\"]").array();
    assertEquals("foo", array.get(0).stringValue(""));
    assertEquals("bart", array.get(1).stringValue(""));
    assertEquals(-1, array.get(4).doubleValue(-1), 0.001);
    assertEquals(false, array.get(400).boolValue(false));
    assertEquals(true, array.get(4001).boolValue(true));
  }

  /** Undefined value is replaced by the given defaults. */
  @Test public void testUndefined1() throws IOException, JsonParser.SyntaxError {
    JsonArray array = parse("[]").array();
    assertEquals("bort", array.get(0).stringValue("bort"));
    assertEquals(true, array.get(1).boolValue(true));
  }

  /** Inconvertible types result in the given defaults. */
  @Test public void testWrongType() throws IOException, JsonParser.SyntaxError {
    JsonArray array = parse("[1, false]").array();
    assertEquals("bort", array.get(0).stringValue("bort"));
    assertEquals(100, array.get(1).intValue(100));
  }
}
