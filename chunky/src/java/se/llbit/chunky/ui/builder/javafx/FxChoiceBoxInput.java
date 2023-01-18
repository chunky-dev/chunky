/*
 * Copyright (c) 2023 Chunky contributors
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

package se.llbit.chunky.ui.builder.javafx;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import se.llbit.chunky.ui.RegisterableCellAdapter;
import se.llbit.chunky.ui.builder.ChoiceBoxInput;
import se.llbit.fxutil.CustomizedListCellFactory;
import se.llbit.util.Registerable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class FxChoiceBoxInput<T extends Registerable> extends FxInput<T, HBox, ChoiceBoxInput<T>> implements ChoiceBoxInput<T> {
  protected final Label label;
  protected final ComboBox<T> comboBox;
  protected final ChangeListener<T> listener;

  public FxChoiceBoxInput() {
    super(new HBox());
    label = new Label();
    comboBox = new ComboBox<>();

    input.setSpacing(10);
    input.getChildren().addAll(label, comboBox);

    listener = (observable, oldValue, newValue) -> callCallbacks(newValue);
    comboBox.getSelectionModel().selectedItemProperty().addListener(listener);
    CustomizedListCellFactory.install(comboBox, new RegisterableCellAdapter());
  }

  @Override
  public ChoiceBoxInput<T> addItems(Collection<? extends T> items) {
    comboBox.getItems().addAll(items);
    return this;
  }

  @Override
  public ChoiceBoxInput<T> addItem(T item) {
    comboBox.getItems().add(item);
    return this;
  }

  @Override
  public ChoiceBoxInput<T> clear() {
    comboBox.getItems().clear();
    return this;
  }

  @Override
  public ChoiceBoxInput<T> set(T value) {
    comboBox.getSelectionModel().selectedItemProperty().removeListener(listener);
    comboBox.getSelectionModel().select(value);
    comboBox.getSelectionModel().selectedItemProperty().addListener(listener);
    return this;
  }

  @Override
  public ChoiceBoxInput<T> set(String id) {
    comboBox.getItems().stream().filter(i -> Objects.equals(id, i.getId())).findFirst().ifPresent(this::set);
    return this;
  }

  @Override
  public T get() {
    return comboBox.getValue();
  }

  @Override
  public ChoiceBoxInput<T> setName(String name) {
    label.setText(name);
    return this;
  }

  @Override
  public ChoiceBoxInput<T> setTooltip(String tooltip) {
    Tooltip.install(input, new Tooltip(tooltip));
    return this;
  }

  protected static class ChoiceSeparator extends Separator implements Registerable {
    @Override
    public String getName() {
      return "";
    }

    @Override
    public String getDescription() {
      return null;
    }
  }
}
