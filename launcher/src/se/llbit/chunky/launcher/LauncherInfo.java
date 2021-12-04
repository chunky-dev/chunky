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
    public String notes = "";

    public ArrayList<ReleaseChannel> channels = new ArrayList<>();

    public LauncherInfo(JsonObject obj) {
        name = obj.get("name").stringValue("");
        timestamp = obj.get("timestamp").stringValue("");
        date = Util.dateFromISO8601(timestamp);
        notes = obj.get("notes").stringValue("");
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
