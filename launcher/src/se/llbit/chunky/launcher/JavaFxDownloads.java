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

package se.llbit.chunky.launcher;

import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonValue;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class JavaFxDownloads {
  public static class SyntaxException extends Exception {
    public SyntaxException(String message) {
      super(message);
    }

    public SyntaxException(JsonParser.SyntaxError e) {
      super("Json syntax error:\n" + e.getMessage());
    }
  }

  public static class Os {
    public final String name;
    public final Arch[] archs;
    private final Pattern regex;

    Os(JsonObject obj) throws SyntaxException {
      name = getChecked(obj, "name");
      this.regex = Pattern.compile(getChecked(obj, "regex"));

      ArrayList<Arch> archs = new ArrayList<>();
      JsonValue archsVal = obj.get("archs");
      if (!archsVal.isArray()) throw new SyntaxException("OS object field archs must be array.");
      for (JsonValue arch : archsVal.asArray().elements) {
        if (!arch.isObject()) throw new SyntaxException("OS object field archs must be array of architecture objects");
        archs.add(new Arch(arch.asObject()));
      }
      this.archs = archs.toArray(new Arch[0]);
    }

    public boolean doesMatch(String os) {
      return regex.matcher(os).matches();
    }
  }

  public static class Arch {
    public final String name;
    public final URL url;
    public final String sha256;
    private final Pattern regex;

    Arch(JsonObject obj) throws SyntaxException {
      name = getChecked(obj, "name");
      regex = Pattern.compile(getChecked(obj, "regex"));
      sha256 = getChecked(obj, "sha256");
      try {
        url = new URL(getChecked(obj, "url"));
      } catch (MalformedURLException e) {
        throw new SyntaxException("Architecture object has invalid URL");
      }
    }

    public boolean doesMatch(String arch) {
      return regex.matcher(arch).matches();
    }
  }

  private static String getChecked(JsonObject obj, String field) throws SyntaxException {
    String o = obj.get(field).asString(null);
    if (o == null) throw new SyntaxException("Missing field: " + field);
    return o;
  }

  /**
   * Parse a json object for the download list.
   */
  public static Os[] parse(JsonArray objs) throws SyntaxException {
    ArrayList<Os> out = new ArrayList<>();
    for (JsonValue value : objs.elements) {
      if (!value.isObject()) throw new SyntaxException("Expecting array of OS objects.");
      out.add(new Os(value.asObject()));
    }
    return out.toArray(new Os[0]);
  }

  /**
   * Parse an input stream for the download list.
   */
  public static Os[] parse(InputStream is) throws SyntaxException, IOException {
    try (JsonParser parser = new JsonParser(is)) {
      JsonValue value = parser.parse();
      if (!value.isArray()) throw new SyntaxException("Expecting array of OS objects.");
      return parse(value.asArray());
    } catch (JsonParser.SyntaxError e) {
      throw new SyntaxException(e);
    }
  }

  /**
   * Fet the download list from a URL.
   */
  public static Os[] fetch(URL url) throws SyntaxException, IOException {
    // Follow redirects
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    int responseCode = conn.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
      responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
      responseCode == HttpURLConnection.HTTP_SEE_OTHER ||
      responseCode == 307) { // HTTP 307: Temporary Redirect. Does not have const in HttpURLConnection
      return fetch(new URL(conn.getHeaderField("Location")));
    }

    try (InputStream is = url.openStream()) {
      return parse(is);
    }
  }
}
