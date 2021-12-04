/* Copyright (c) 2013-2014 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.JsonSettings;
import se.llbit.chunky.resources.SettingsDirectory;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonNumber;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.util.OSDetector;

import java.io.File;
import java.io.InvalidObjectException;
import java.util.ArrayList;

public class LauncherSettings {
  private static final int DEFAULT_MEMORY_LIMIT = 1024;
  public static final String LAUNCHER_SETTINGS_FILE = "chunky-launcher.json";
  public static final String DEFAULT_UPDATE_SITE = "https://chunkyupdate.lemaik.de";
  public static final ReleaseChannel DEFAULT_RELEASE_CHANNEL = new ReleaseChannel(
      "Stable", "latest.json", "Latest stable release of Chunky.");

  public int launcherVersion = 1;

  public String javaDir = "";
  public int memoryLimit = DEFAULT_MEMORY_LIMIT;

  /** URL used for checking for new updates and downloading archives. */
  public String updateSite = DEFAULT_UPDATE_SITE;

  /** Show the debugging console when launching Chunky. */
  public boolean debugConsole = false;

  /** Force showing the debugging console even for headless rendering. */
  public boolean forceGuiConsole = false;

  public boolean verboseLogging = false;
  public boolean verboseLauncher = false;
  public String javaOptions = "";
  public String chunkyOptions = "";
  public String version = "latest";
  public boolean closeConsoleOnExit = true;

  private final JsonSettings settings = new JsonSettings();

  public boolean headless = false;
  public boolean showLauncher = true;
  public boolean showAdvancedSettings = false;

  public ReleaseChannel selectedChannel;
  public ArrayList<ReleaseChannel> releaseChannels;

  public LauncherSettings() {
    javaOptions = defaultJavaOptions();
  }

  public void load() {
    File directory = SettingsDirectory.getSettingsDirectory();
    if (directory != null) {
      settings.load(new File(directory, LAUNCHER_SETTINGS_FILE));
    }

    launcherVersion = settings.getInt("launcherVersion", 0);

    updateSite = settings.getString("updateSite", DEFAULT_UPDATE_SITE);
    javaDir = settings.getString("javaDir", "");
    if (javaDir.isEmpty()) {
      javaDir = settings.getString("javaExecutable", "");
    }
    if (javaDir.isEmpty()) {
      javaDir = System.getProperty("java.home");
    }
    memoryLimit = settings.getInt("memoryLimit", DEFAULT_MEMORY_LIMIT);
    debugConsole = settings.getBool("showConsole", false);
    verboseLogging = settings.getBool("verboseLogging", false);
    verboseLauncher = settings.getBool("verboseLauncher", false);
    closeConsoleOnExit = settings.getBool("closeConsoleOnExit", true);
    javaOptions = settings.getString("javaOptions", defaultJavaOptions());
    chunkyOptions = settings.getString("chunkyOptions", "");
    version = settings.getString("version", "latest");
    showLauncher = settings.getBool("showLauncher", true);
    showAdvancedSettings = settings.getBool("showAdvancedSettings", false);

    boolean downloadSnapshots = settings.getBool("downloadSnapshots", false);
    JsonObject releaseChannelObj = settings.get("releaseChannels").object();
    releaseChannels = new ArrayList<>();
    releaseChannels.add(DEFAULT_RELEASE_CHANNEL);
    for (JsonValue obj : releaseChannelObj.get("channels").array()) {
      try {
        ReleaseChannel channel = new ReleaseChannel(obj.asObject());
        int index = releaseChannels.indexOf(channel);
        if (index == -1) {
          releaseChannels.add(channel);
        } else {
          releaseChannels.set(index, channel);
        }
      } catch (InvalidObjectException e) {
        Log.info("Invalid release channel", e);
      }
    }
    int selectedChannelValue = releaseChannelObj.get("selectedChannel").intValue(0);
    if (downloadSnapshots) {
      selectedChannelValue = releaseChannels.size() - 1;
    }
    selectedChannel = releaseChannels.get(selectedChannelValue);
  }

  private String defaultJavaOptions() {
    // Workaround for JavaFX hardware rendering issue on Windows.
    // See https://www.reddit.com/r/javahelp/comments/84w6i6/problem_displaying_anything_with_javafx_only/
    return (OSDetector.getOS() == OSDetector.OS.WIN)
        ? "-Dprism.order=sw"
        : "";
  }

  public void save() {
    settings.setString("updateSite", updateSite);
    settings.setString("javaDir", javaDir);
    settings.setInt("memoryLimit", memoryLimit);
    settings.setBool("showConsole", debugConsole);
    settings.setBool("verboseLogging", verboseLogging);
    settings.setBool("verboseLauncher", verboseLauncher);
    settings.setBool("closeConsoleOnExit", closeConsoleOnExit);
    settings.setString("javaOptions", javaOptions);
    settings.setString("chunkyOptions", chunkyOptions);
    settings.setString("version", version);
    settings.setBool("showLauncher", showLauncher);
    settings.setBool("showAdvancedSettings", showAdvancedSettings);

    JsonObject releaseChannelsObject = new JsonObject();
    int selectedChannelValue = releaseChannels.indexOf(selectedChannel);
    releaseChannelsObject.set("selectedChannel", new JsonNumber(selectedChannelValue));
    JsonArray releaseChannelsValue = new JsonArray(releaseChannels.size());
    for (ReleaseChannel channel : releaseChannels) {
      releaseChannelsValue.add(channel.toJson());
    }
    releaseChannelsObject.set("channels", releaseChannelsValue);
    settings.set("releaseChannels", releaseChannelsObject);

    File directory = SettingsDirectory.getSettingsDirectory();
    settings.save(directory, new File(directory, LAUNCHER_SETTINGS_FILE));
  }

  public String getResourceUrl(String path) {
    String updateSite = this.updateSite;
    if (!updateSite.endsWith("/")) {
      updateSite += "/";
    }
    return updateSite + path;
  }
}
