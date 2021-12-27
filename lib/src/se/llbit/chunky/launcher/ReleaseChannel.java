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

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
import se.llbit.json.JsonObject;

import java.util.Objects;

public class ReleaseChannel {
    public static final class ReleaseChannelCell extends ListCell<ReleaseChannel> {
        @Override
        protected void updateItem(ReleaseChannel item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setGraphic(null);
            } else {
                setText(item.name);
                setTooltip(new Tooltip(item.notes));
            }
        }
    }

    public static final class CellFactory implements Callback<ListView<ReleaseChannel>, ListCell<ReleaseChannel>> {
        @Override
        public ListCell<ReleaseChannel> call(ListView<ReleaseChannel> param) {
            return new ReleaseChannelCell();
        }
    }

    public final String id;
    public final String name;
    public final String path;
    public final String notes;

    public ReleaseChannel(String id, String name, String path, String notes) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.notes = notes;
    }

    public ReleaseChannel(JsonObject obj) throws IllegalArgumentException {
        this.id = obj.get("id").stringValue(obj.get("name").stringValue(null));
        this.name = obj.get("name").stringValue(null);
        this.path = obj.get("path").stringValue(null);
        this.notes = obj.get("notes").stringValue(null);

        if (this.id == null || this.name == null || this.path == null || this.notes == null) {
            throw new IllegalArgumentException("Invalid release channel object");
        }
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.add("id", id);
        obj.add("name", name);
        obj.add("path", path);
        obj.add("notes", notes);
        return obj;
    }
}
