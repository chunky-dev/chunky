/*
 * Copyright (c) 2022 Chunky contributors
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

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * A builder for a horizontal box of vertical columns with a maximum length.
 */
public class ColumnsBoxBuilder {
  private final int maxColumnItems;
  private final Consumer<VBox> columnStyle;
  private final HBox columnsBox = new HBox();
  private VBox currentColumn = null;

  /**
   * @param maxColumnItems Maximum number of items in each column
   * @param columnStyle    Style function called on each column
   */
  public ColumnsBoxBuilder(int maxColumnItems, Consumer<VBox> columnStyle) {
    this.maxColumnItems = maxColumnItems;
    this.columnStyle = columnStyle;
  }

  public ColumnsBoxBuilder(int maxColumnItems) {
    this(maxColumnItems, i -> {});
  }

  /**
   * Add a JavaFX Node to the builder.
   */
  public void add(Node item) {
    if (currentColumn == null) {
      currentColumn = new VBox();
      columnStyle.accept(currentColumn);
    }

    currentColumn.getChildren().add(item);

    if (currentColumn.getChildren().size() >= this.maxColumnItems) {
      columnsBox.getChildren().add(currentColumn);
      currentColumn = null;
    }
  }

  /**
   * Build and get the resulting HBox.
   */
  public HBox build() {
    if (currentColumn != null) {
      columnsBox.getChildren().add(currentColumn);
      currentColumn = null;
    }

    return columnsBox;
  }
}
