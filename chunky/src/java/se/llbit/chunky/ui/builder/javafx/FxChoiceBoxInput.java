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
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import se.llbit.chunky.ui.builder.ChoiceBoxInput;
import se.llbit.fxutil.CustomizedListCellFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class FxChoiceBoxInput<T> extends FxInput<T, HBox, ChoiceBoxInput<T>> implements ChoiceBoxInput<T> {
  protected final Label label;
  protected final ComboBox<T> comboBox;
  protected final ChangeListener<T> listener;

  protected Function<T, String> toString = Objects::toString;
  protected Function<T, String> toTooltip = x -> null;

  public FxChoiceBoxInput() {
    super(new HBox());
    label = new Label();
    comboBox = new ComboBox<>();

    input.setSpacing(10);
    input.setAlignment(Pos.CENTER_LEFT);
    input.getChildren().addAll(label, comboBox);
    comboBox.setPrefWidth(150);

    listener = (observable, oldValue, newValue) -> callCallbacks(newValue);
    comboBox.getSelectionModel().selectedItemProperty().addListener(listener);
    CustomizedListCellFactory.install(comboBox, buildAdapter());
  }

  private CustomizedListCellFactory.Adapter<T> buildAdapter() {
    return new CustomizedListCellFactory.Adapter<T>() {
      @Override
      public String getLabel(T item) {
        if (item == null) return null;
        return toString.apply(item);
      }
      @Override
      public Tooltip getTooltip(T item) {
        if (item == null) return null;
        String tooltip = toTooltip.apply(item);
        if (tooltip == null || tooltip.isEmpty()) return null;
        return new Tooltip(tooltip);
      }
    };
  }

  @Override
  public ChoiceBoxInput<T> addItems(Collection<? extends T> items) {
    comboBox.getItems().addAll(items);
    return this;
  }

  @Override
  @SafeVarargs
  public final ChoiceBoxInput<T> addItems(T... items) {
    comboBox.getItems().addAll(items);
    return this;
  }

  @Override
  public ChoiceBoxInput<T> select(Predicate<T> predicate) {
    comboBox.getItems().stream().filter(predicate).findFirst().ifPresent(this::set);
    return this;
  }

  @Override
  public ChoiceBoxInput<T> setStringConverter(Function<T, String> toString) {
    this.toString = toString;
    CustomizedListCellFactory.install(comboBox, buildAdapter());
    return this;
  }

  @Override
  public ChoiceBoxInput<T> setTooltipConverter(Function<T, String> toTooltip) {
    this.toTooltip = toTooltip;
    CustomizedListCellFactory.install(comboBox, buildAdapter());
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
}
