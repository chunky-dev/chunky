/* Copyright (c) 2013-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2013-2021 Chunky Contributors
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
import se.llbit.json.JsonObject;
import se.llbit.json.JsonString;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.util.OSDetector;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class LauncherSettings {
  private static final int DEFAULT_MEMORY_LIMIT = 1024;
  public static final String LAUNCHER_SETTINGS_FILE = "chunky-launcher.json";
  public static final String DEFAULT_UPDATE_SITE = "https://chunkyupdate.lemaik.de";

  static final ReleaseChannel STABLE_RELEASE_CHANNEL = new ReleaseChannel(
      "stable", "Stable", "latest.json", "Latest stable release of Chunky.");
  static final ReleaseChannel SNAPSHOT_RELEASE_CHANNEL = new ReleaseChannel(
      "snapshot", "Snapshot", "snapshot.json", "Latest nightly snapshot of Chunky.");

  public static boolean disableLibraryValidation = false;

  public int settingsRevision = 0;

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
  public Map<String, ReleaseChannel> releaseChannels;
  public boolean skipJvmCheck = false;

  public LauncherSettings() {
    javaOptions = defaultJavaOptions();
  }

  public void load() {
    File directory = SettingsDirectory.getSettingsDirectory();
    if (directory != null) {
      settings.load(new File(directory, LAUNCHER_SETTINGS_FILE));
    }

    settingsRevision = settings.getInt("settingsRevision", 0);

    updateSite = settings.getString("updateSite", DEFAULT_UPDATE_SITE);
    javaDir = settings.getString("javaDir", "");
    if (javaDir.isEmpty()) {
      javaDir = settings.getString("javaExecutable", "");
      skipJvmCheck = false;
    }
    if (javaDir.isEmpty()) {
      javaDir = System.getProperty("java.home");
      skipJvmCheck = false;
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
    skipJvmCheck = settings.getBool("skipJvmCheck", false);

    JsonObject releaseChannelObj = settings.get("releaseChannels").object();
    releaseChannels = new LinkedHashMap<>();
    for (JsonValue obj : releaseChannelObj.get("channels").array()) {
      try {
        ReleaseChannel channel = new ReleaseChannel(obj.asObject());
        releaseChannels.put(channel.id, channel);
      } catch (IllegalArgumentException e) {
        Log.info("Invalid release channel", e);
      }
    }
    releaseChannels.putIfAbsent(STABLE_RELEASE_CHANNEL.id, STABLE_RELEASE_CHANNEL);
    releaseChannels.putIfAbsent(SNAPSHOT_RELEASE_CHANNEL.id, SNAPSHOT_RELEASE_CHANNEL);

    String selectedChannelId = releaseChannelObj.get("selectedChannel").stringValue(STABLE_RELEASE_CHANNEL.id);
    selectedChannel = releaseChannels.getOrDefault(selectedChannelId, null);
    if (selectedChannel == null) {
      selectedChannel = releaseChannels.get(STABLE_RELEASE_CHANNEL.id);
    }
    if (settings.getBool("downloadSnapshots", false)) {
      selectedChannel = releaseChannels.get(SNAPSHOT_RELEASE_CHANNEL.id);
    }
  }

  private String defaultJavaOptions() {
    // Workaround for JavaFX hardware rendering issue on Windows.
    // See https://www.reddit.com/r/javahelp/comments/84w6i6/problem_displaying_anything_with_javafx_only/
    return (OSDetector.getOS() == OSDetector.OS.WIN)
        ? "-Dprism.order=sw"
        : "";
  }

  public void save() {
    settings.setInt("settingsRevision", settingsRevision);

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
    settings.setBool("skipJvmCheck", skipJvmCheck);

    settings.removeSetting("downloadSnapshots");
    JsonObject releaseChannelsObject = new JsonObject();
    releaseChannelsObject.set("selectedChannel", new JsonString(selectedChannel.id));
    JsonArray releaseChannelsValue = new JsonArray(releaseChannels.size());
    for (ReleaseChannel channel : releaseChannels.values()) {
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

  public void setReleaseChannels(Map<String, ReleaseChannel> channels) {
    releaseChannels.clear();
    releaseChannels.putAll(channels);
    selectedChannel = releaseChannels.getOrDefault(selectedChannel.id, channels.values().stream().findFirst().orElse(STABLE_RELEASE_CHANNEL));
    save();
  }
}
