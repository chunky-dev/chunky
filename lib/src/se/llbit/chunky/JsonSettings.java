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
package se.llbit.chunky;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import se.llbit.json.Json;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonNumber;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;
import se.llbit.json.JsonString;
import se.llbit.json.JsonValue;
import se.llbit.json.PrettyPrinter;
import se.llbit.log.Log;

/**
 * Utility class for managing program properties.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public final class JsonSettings {

  private JsonObject json = new JsonObject();

  /**
   * Load saved properties.
   *
   * @param file source file
   */
  public void load(File file) {
    String path = file.getAbsolutePath();
    try (InputStream in = new FileInputStream(file)) {
      JsonParser parser = new JsonParser(in);
      json = parser.parse().object();
      Log.infof("Settings loaded from %s", path);
    } catch (IOException e) {
      Log.warnf("Warning: Could not load settings from %s - defaults will be used", path);
    } catch (SyntaxError e) {
      Log.warnf(
          "Warning: Could not load settings from %s - defaults will be used (%s)",
          path, e.getMessage());
    }
  }

  /**
   * Save settings to file.
   *
   * @param file destination file
   */
  public void save(File settingsDir, File file) {
    if (file == null) {
      Log.error("Can't save settings: null file");
      return;
    }
    if (settingsDir == null) {
      settingsDir = file.getParentFile();
      if (settingsDir == null) {
        Log.error("Can't save settings to file: " + file.getAbsolutePath());
        return;
      }
    }
    if (!settingsDir.isDirectory()) {
      Log.warn("Warning: Chunky settings directory does not exist. " +
          "Creating settings directory at " +
          settingsDir.getAbsolutePath());
      boolean success = settingsDir.mkdirs();
      if (!success) {
        Log.error("Failed to create settings directory " + settingsDir.getAbsolutePath());
        return;
      }
    }
    try (OutputStream out = new FileOutputStream(file);
         PrettyPrinter pp = new PrettyPrinter("  ", new PrintStream(out))) {
      json.prettyPrint(pp);
      Log.info("Saved settings to " + file.getAbsolutePath());
    } catch (IOException e) {
      Log.warnf("Warning: Failed to save settings to %s: %s",
          file.getAbsolutePath(), e.getMessage());
    }
  }

  /**
   * Get string value of a setting.
   *
   * @param name     Property name
   * @param defValue Default value
   * @return The value of the property, or <code>defValue</code> if
   * the property was not set.
   */
  public String getString(String name, String defValue) {
    return json.get(name).stringValue(defValue);
  }

  /**
   * Get the raw JSON value for a setting.
   */
  public JsonValue get(String name) {
    return json.get(name);
  }

  /**
   * Replace the raw JSON value for a setting.
   */
  public void set(String name, JsonValue value) {
    json.set(name, value);
  }

  /**
   * Get boolean value of a setting.
   *
   * @param name     Property name
   * @param defValue Default value
   * @return The value of the property, or <code>defValue</code> if
   * the property was not set.
   */
  public boolean getBool(String name, boolean defValue) {
    return json.get(name).boolValue(defValue);
  }

  /**
   * Get the integer value of a setting.
   *
   * @param name     Property name
   * @param defValue Default value
   * @return The value of the property, or <code>defValue</code> if
   * the property was not set, or was not set to a valid integer value.
   */
  public int getInt(String name, int defValue) {
    return json.get(name).intValue(defValue);
  }

  /**
   * Get the double value of a setting.
   *
   * @param name     Property name
   * @param defValue Default value
   * @return The value of the property, or <code>defValue</code> if
   * the property was not set, or was not set to a valid integer value.
   */
  public double getDouble(String name, double defValue) {
    return json.get(name).doubleValue(defValue);
  }

  /**
   * Set string value.
   *
   * @param name  setting name
   * @param value new value
   */
  public void setString(String name, String value) {
    json.set(name, new JsonString(value));
  }

  /**
   * Set boolean value.
   *
   * @param name  setting name
   * @param value new value
   */
  public void setBool(String name, boolean value) {
    json.set(name, Json.of(value));
  }

  /**
   * Set integer value.
   *
   * @param name  setting name
   * @param value new value
   */
  public void setInt(String name, int value) {
    json.set(name, new JsonNumber("" + value));
  }

  /**
   * Set double value.
   *
   * @param name  setting name
   * @param value new value
   */
  public void setDouble(String name, double value) {
    json.set(name, new JsonNumber("" + value));
  }

  /**
   * Remove a setting by name.
   *
   * @param name setting name
   */
  public void removeSetting(String name) {
    json.remove(name);
  }

  /**
   * @param name setting name
   * @return <code>true</code> if the configuration contains a setting with
   * the given name
   */
  public boolean containsKey(String name) {
    for (JsonMember entry : json) {
      if (entry.name.equals(name)) {
        return true;
      }
    }
    return false;
  }
}

