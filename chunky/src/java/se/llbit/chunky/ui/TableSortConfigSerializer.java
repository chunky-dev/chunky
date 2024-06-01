package se.llbit.chunky.ui;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TableSortConfigSerializer {
    public static <T> JsonArray getSortConfig(TableView<T> table) {
        JsonArray columns = new JsonArray();
        for (TableColumn<T, ?> column : table.getSortOrder()) {
            JsonObject columnSort = new JsonObject();
            columnSort.add(column.getId(), column.getSortType() == TableColumn.SortType.ASCENDING ? 1 : -1);
            columns.add(columnSort);
        }
        return columns;
    }

    public static <T> void applySortConfig(TableView<T> table, JsonArray config) {
        List<TableColumn<T, ?>> columns = new ArrayList<>();
        for (JsonValue value : config) {
            if (value.isObject()) {
                for (JsonMember member : value.asObject()) {
                    Optional<TableColumn<T, ?>> column = table.getColumns().filtered(c -> c.getId().equals(member.getName())).stream().findFirst();
                    column.ifPresent(c -> {
                        c.setSortType(member.getValue().asInt(0) < 0 ? TableColumn.SortType.DESCENDING : TableColumn.SortType.ASCENDING);
                        columns.add(column.get());
                    });
                }
            }
        }
        table.getSortOrder().setAll(columns);
    }
}
