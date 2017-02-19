/* Copyright (c) 2017 Chunky contributors
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
package se.llbit.chunky.plugin;

import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;

import java.io.IOException;
import java.io.InputStream;

/**
 * Meta information about a plugin.
 */
public class PluginMeta {
  private final String name;
  private final String version;
  private final String author;
  private final String pluginClassName;

  public PluginMeta(String name, String version, String author, String main) {
    this.name = name;
    this.version = version;
    this.author = author;
    this.pluginClassName = main;
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  public String getAuthor() {
    return author;
  }

  public String getPluginClassName() {
    return pluginClassName;
  }

  static PluginMeta loadFromJson(InputStream in) throws LoadPluginException {
    try (JsonParser parser = new JsonParser(in)) {
      JsonObject json = parser.parse().object();

      String name = json.get("name").stringValue("");
      String main = json.get("main").stringValue("");
      if (name.isEmpty()) {
        throw new LoadPluginException("Plugin has no name specified");
      }
      if (main.isEmpty()) {
        throw new LoadPluginException("Plugin has no main class specified");
      }

      return new PluginMeta(
          name,
          json.get("version").stringValue(null),
          json.get("author").stringValue(null),
          main
      );
    } catch (JsonParser.SyntaxError e) {
      throw new LoadPluginException("Syntax error in plugin.json", e);
    } catch (IOException e) {
      throw new LoadPluginException("Could not read plugin.json", e);
    }
  }
}
