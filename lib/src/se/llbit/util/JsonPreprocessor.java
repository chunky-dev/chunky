/* Copyright (c) 2022 Chunky contributors
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

import se.llbit.json.JsonParser;
import se.llbit.json.JsonValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Preprocessing input stream for converting some lenient JSON
 * into strict JSON.
 */
public class JsonPreprocessor extends InputStream {
  private static final Pattern COMMENT_STRIP_MATCHER = Pattern.compile("(?<json>^((\"([^\"]?(\\\\\")?)*\")?[^/\"]*((?!//)/)?)*)");

  /**
   * Leniently parse JSON.
   * @param is Input stream
   * @return The parsed JSON value
   */
  public static JsonValue parse(InputStream is) throws IOException, JsonParser.SyntaxError {
    is = new JsonPreprocessor(is);
    JsonValue value = (new JsonParser(is).parse());
    is.close();
    return value;
  }

  private final BufferedReader reader;
  private String line;
  private int index;

  public JsonPreprocessor(InputStream is) throws IOException {
    reader = new BufferedReader(new InputStreamReader(is), 1);
    readLine();
  }

  private void readLine() throws IOException {
    String l = reader.readLine();
    if (l != null) {
      // This remove comments from lines
      Matcher matcher = COMMENT_STRIP_MATCHER.matcher(l);
      boolean a = matcher.lookingAt();
      assert a;

      line = matcher.group("json") + "\n";
    } else {
      // No more lines
      line = null;
    }
    index = 0;
  }

  @Override
  public int read() throws IOException {
    if (line == null) {
      return -1;
    }
    char c = line.charAt(index);

    index++;
    if (index >= line.length()) {
      readLine();
    }
    return c;
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }
}
