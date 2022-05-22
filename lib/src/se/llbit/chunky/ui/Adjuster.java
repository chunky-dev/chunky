/* Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

/**
 * A control for editing numeric values with a text field.
 */
public abstract class Adjuster<T extends Number> extends HBox {
  public static EventType<Event> AFTER_VALUE_CHANGE = new EventType<>("AFTER_VALUE_CHANGE");

  private final StringProperty name = new SimpleStringProperty("Name");
  protected final Label nameLbl = new Label();
  protected final NumericTextField<Property<Number>> valueField;
  protected final Property<Number> value;
  private ChangeListener<Number> listener;

  protected Adjuster(Property<Number> value) {
    this.value = value;
    valueField = new NumericTextField<>(value);
    valueField.triggerRefresh();
    nameLbl.textProperty().bind(Bindings.concat(name, ":"));
    setAlignment(Pos.CENTER_LEFT);
    setSpacing(10);
    valueField.setPrefWidth(103);
    getChildren().addAll(nameLbl, valueField);

    Number[] oldValue = new Number[1];
    valueField.focusedProperty().addListener(observable -> {
      if (valueField.isFocused()) {
        oldValue[0] = valueField.valueProperty().getValue();
      } else {
        if (!valueField.valueProperty().getValue().equals(oldValue[0])) {
          this.fireEvent(new Event(Adjuster.AFTER_VALUE_CHANGE));
        }
      }
    });
  }

  public void setName(String name) {
    this.name.set(name);
  }

  public String getName() {
    return name.get();
  }

  public StringProperty nameProperty() {
    return name;
  }

  /**
   * Set value without triggering listeners to update.
   */
  public void set(Number newValue) {
    if (listener != null) {
      value.removeListener(listener);
      value.setValue(newValue);
      value.addListener(listener);
    } else {
      value.setValue(newValue);
    }
  }

  /**
   * Sets the value and updates the listeners.
   */
  public void setAndUpdate(Number newValue) {
    value.setValue(newValue);
  }

  public T get() {
    return (T) value.getValue();
  }

  public void setTooltip(String tooltip) {
    nameLbl.setTooltip(new Tooltip(tooltip));
    valueField.setTooltip(new Tooltip(tooltip));
  }

  protected abstract T clamp(Number value);

  public void onValueChange(Consumer<T> changeConsumer) {
    if (listener != null) {
      value.removeListener(listener);
    }
    listener = (observable, oldValue, newValue) -> changeConsumer.accept(clamp(newValue));
    value.addListener(listener);
  }

  public Property<Number> valueProperty() {
    return value;
  }
}
