/* Copyright (c) 2021 Chunky contributors
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

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.util.Util;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class LauncherInfo {
    public final String notes;
    public final String path;

    public final Date date;
    public final ArtifactVersion version;

    public final Map<String, ReleaseChannel> channels = new LinkedHashMap<>();

    public LauncherInfo() {
        this.notes = "";
        this.path = null;
        this.date = null;
        this.version = ChunkyLauncher.LAUNCHER_VERSION;
        channels.put(LauncherSettings.STABLE_RELEASE_CHANNEL.id, LauncherSettings.STABLE_RELEASE_CHANNEL);
        channels.put(LauncherSettings.SNAPSHOT_RELEASE_CHANNEL.id, LauncherSettings.SNAPSHOT_RELEASE_CHANNEL);
    }

    public LauncherInfo(JsonObject obj) {
        version = new DefaultArtifactVersion(obj.get("name").stringValue(""));
        notes = obj.get("notes").stringValue("");
        path = obj.get("path").stringValue("ChunkyLauncher.jar");
        date = Util.dateFromISO8601(obj.get("timestamp").stringValue(""));
        JsonArray releaseChannels = obj.get("channels").array();
        for (JsonValue channelValue : releaseChannels) {
            try {
                ReleaseChannel channel = new ReleaseChannel(channelValue.asObject());
                channels.put(channel.id, channel);
            } catch (IllegalArgumentException e) {
                Log.info("Invalid release channel", e);
            }
        }
    }
}
