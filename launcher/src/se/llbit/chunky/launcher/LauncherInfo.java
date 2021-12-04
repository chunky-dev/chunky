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

import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.util.Util;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Date;

public class LauncherInfo {
    public final String name;
    public final String timestamp;
    public final Date date;
    public final String notes;
    public final String path;

    public final ArrayList<ReleaseChannel> channels = new ArrayList<>();

    public LauncherInfo(JsonObject obj) {
        name = obj.get("name").stringValue("");
        timestamp = obj.get("timestamp").stringValue("");
        notes = obj.get("notes").stringValue("");
        path = obj.get("path").stringValue("ChunkyLauncher.jar");
        date = Util.dateFromISO8601(timestamp);
        JsonArray releaseChannels = obj.get("channels").array();
        for (JsonValue channelValue : releaseChannels) {
            try {
                ReleaseChannel channel = new ReleaseChannel(channelValue.asObject());
                int index = channels.indexOf(channel);
                if (index == -1) {
                    channels.add(channel);
                } else {
                    channels.set(index, channel);
                }
            } catch (InvalidObjectException e) {
                Log.info("Invalid release channel", e);
            }
        }
    }
}
