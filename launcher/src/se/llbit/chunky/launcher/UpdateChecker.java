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

import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Check for update and run update dialog (or just update in headless mode).
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class UpdateChecker extends Thread {
  private final LauncherSettings settings;
  private final UpdateListener listener;
  private final ReleaseChannel channel;

  public UpdateChecker(LauncherSettings settings, ReleaseChannel channel, UpdateListener listener) {
    this.settings = settings;
    this.listener = listener;
    this.channel = channel;
  }

  public UpdateChecker(LauncherSettings settings, ReleaseChannel channel) {
    this(settings, channel, new UpdateListener() {
      @Override
      public void updateError(String message) {
        // ignore
      }

      @Override
      public void updateAvailable(VersionInfo latest) {
        // ignore
      }

      @Override
      public void noUpdateAvailable() {
        // ignore
      }
    });
  }

  @Override
  public void run() {
    try {
      if (!tryUpdate()) {
        listener.noUpdateAvailable();
      }
    } catch (Exception e1) {
      e1.printStackTrace();
      listener.updateError("Can not update at this time.");
    }
  }

  private boolean tryUpdate() {
    List<VersionInfo> candidates = getVersions();

    // Filter out corrupt versions.
    Iterator<VersionInfo> iter = candidates.iterator();
    while (iter.hasNext()) {
      VersionInfo version = iter.next();
      if (!version.isValid()) {
        System.err.println("Corrupted version info");
        listener.updateError("Downloaded corrupted version info: " + version);
        iter.remove();
      }
    }

    if (candidates.isEmpty()) {
      return false;
    }

    // Find latest candidate.
    VersionInfo latest = candidates.get(0);
    for (VersionInfo candidate : candidates) {
      if (candidate.compareTo(latest) < 0) {
        latest = candidate;
      }
    }

    // Check if version is already installed.
    for (VersionInfo version : ChunkyDeployer.availableVersions()) {
      if (version.name.equals(latest.name) && version.compareTo(latest) == 0 &&
        ChunkyDeployer.checkVersionIntegrity(version.name)) {
        return false;
      }
    }

    // Install the candidate!
    listener.updateAvailable(latest);
    return true;
  }

  public List<VersionInfo> getVersions() {
    List<VersionInfo> candidates = new LinkedList<>();
    getVersion(candidates, settings.getResourceUrl(channel.path));
    return Collections.unmodifiableList(candidates);
  }

  private void getVersion(List<VersionInfo> candidates, String url) {
    try {
      URL latestJson = new URL(url);
      HttpURLConnection conn = (HttpURLConnection) latestJson.openConnection();
      int responseCode = conn.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
        responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
        responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
        getVersion(candidates, conn.getHeaderField("Location"));
        return;
      }
      try (
        InputStream in = latestJson.openStream();
        JsonParser parser = new JsonParser(in)
      ) {
        VersionInfo version = new VersionInfo(parser.parse().object());
        candidates.add(version);
      }
    } catch (MalformedURLException e1) {
      System.err.println("Malformed version info URL.");
      listener.updateError("Malformed version info/update site URL: " + url);
    } catch (IOException e1) {
      System.err.println("Failed to fetch version info " + e1.getMessage());
      listener.updateError("Failed to fetch version info from URL: " + url);
    } catch (SyntaxError e1) {
      System.err.println("Version info JSON error: " + e1.getMessage());
      listener.updateError("Downloaded corrupt version info.");
    }
  }
}
