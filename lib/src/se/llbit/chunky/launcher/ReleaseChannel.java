package se.llbit.chunky.launcher;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonString;

import java.io.InvalidObjectException;
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

    public final String name;
    public final String path;
    public final String notes;

    public ReleaseChannel(String name, String path, String notes) {
        this.name = name;
        this.path = path;
        this.notes = notes;
    }

    public ReleaseChannel(JsonObject obj) throws InvalidObjectException {
        this.name = obj.get("name").stringValue(null);
        this.path = obj.get("path").stringValue(null);
        this.notes = obj.get("notes").stringValue(null);

        if (this.name == null || this.path == null || this.notes == null) {
            throw new InvalidObjectException(String.format("One or more keys were not found:" +
                "\n'name': %s\n'path':%s\n'notes':%s", name, path, notes));
        }
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.add("name", name);
        obj.add("path", path);
        obj.add("notes", notes);
        return obj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleaseChannel that = (ReleaseChannel) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
