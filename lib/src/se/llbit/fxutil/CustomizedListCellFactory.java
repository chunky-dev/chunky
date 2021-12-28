/* Copyright (c) 2021 Chunky Contributors
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
package se.llbit.fxutil;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * A list cell factory that shows labels and tooltips.
 *
 * @param <A> Type of the item that the adapter gets the label and tooltip from
 * @param <T> Item type
 */
public class CustomizedListCellFactory<A, T extends A> implements Callback<ListView<T>, ListCell<T>> {
    private final Adapter<A> adapter;

    public CustomizedListCellFactory(Adapter<A> adapter) {
        this.adapter = adapter;
    }

    @Override
    public ListCell<T> call(ListView<T> listView) {
        return createCell();
    }

    public ListCell<T> createCell() {
        return new Cell<>(adapter);
    }

    public static <A, T extends A> void install(ComboBox<T> comboBox, Adapter<A> adapter) {
        CustomizedListCellFactory<A, T> factory = new CustomizedListCellFactory<>(adapter);
        comboBox.setCellFactory(factory);
        comboBox.setButtonCell(factory.createCell());
    }

    public static class Cell<A, T extends A> extends ListCell<T> {
        private final Adapter<A> adapter;

        public Cell(Adapter<A> adapter) {
            this.adapter = adapter;
        }

        public Cell() {
            this(null);
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty || adapter == null) {
                setGraphic(null);
            } else if (item instanceof Separator) {
                setDisabled(true);
                setMouseTransparent(true);
                setGraphic((Node) item);
                setPadding(Insets.EMPTY);
            } else {
                setGraphic(null);
                setText(adapter.getLabel(item));
                setTooltip(adapter.getTooltip(item));
            }
        }
    }

    public interface Adapter<T> {
        String getLabel(T item);

        default Tooltip getTooltip(T item) {
            return null;
        }
    }
}
