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
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.converter.NumberStringConverter;

import java.util.function.Consumer;

/**
 * A UI control combining a label, slider, and text field for adjusting one numeric property.
 */
public abstract class Adjuster<T extends Number> extends HBox {
  private StringProperty name = new SimpleStringProperty("Name");
  private final Label nameLbl = new Label();
  private final Slider valueSlider = new Slider();
  private final TextField valueField = new TextField();
  private final Property<Number> value;
  protected boolean clampMax;
  protected boolean clampMin;
  private double sliderMin = 0.01; // Lower limit for logarithmic calculations.
  private double min = 0; // TODO: handle minimum.
  private double max = 100;
  private boolean logarithmic = false;
  private boolean maxInfinity = false;
  private ChangeListener<Number> listener;

  protected Adjuster(Property<Number> value) {
    this.value = value;
    nameLbl.textProperty().bind(Bindings.concat(name, ":"));
    setAlignment(Pos.CENTER_LEFT);
    setSpacing(10);
    getChildren().addAll(nameLbl, valueSlider, valueField);
    valueField.setPrefWidth(103);
    valueField.textProperty().bindBidirectional(value, new NumberStringConverter());
    valueSlider.valueProperty().bindBidirectional(value);
    valueSlider.setMin(0);
    valueSlider.setMax(100);
  }

  public void setName(String name) {
    this.name.set(name);
  }

  public String getName() {
    return name.get();
  }

  // TODO: not used - should be removed?
  public StringProperty nameProperty() {
    return name;
  }

  public void setRange(double min, double max) {
    if (min < 0.01 && min >= 0) {
      sliderMin = 0.01;
    } else {
      sliderMin = min;
    }
    this.min = min;
    this.max = max;
    if (!logarithmic) {
      valueSlider.setMin(min);
      valueSlider.setMax(max);
    }
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
    valueSlider.setTooltip(new Tooltip(tooltip));
  }

  /**
   * Make the adjuster use a logarithmic mapping for the slider position.
   */
  public void makeLogarithmic() {
    logarithmic = true;
    valueSlider.setMin(0);
    valueSlider.setMax(100);
    DoubleProperty sliderValue = new SimpleDoubleProperty();
    ChangeListener<Number> sliderListener = (observable, oldValue, newValue) -> {
      double result;
      if (maxInfinity && newValue.doubleValue() > 99.9) {
        result = Double.POSITIVE_INFINITY;
      } else {
        double logMin = Math.log(sliderMin);
        double logMax = Math.log(max);
        double range = logMax - logMin;
        result = Math.pow(Math.E, (newValue.doubleValue() / 100.0) * range + logMin);
      }
      value.setValue(result);
    };
    ChangeListener<Number> valueListener = (observable, oldValue, newValue) -> {
      double result;
      double logMin = Math.log(sliderMin);
      double logMax = Math.log(max);
      double logValue = Math.log(newValue.doubleValue());
      logValue = Math.max(logMin, logValue);
      logValue = Math.min(logMax, logValue);
      double pos = (logValue - logMin) / (logMax - logMin);
      result = pos * 100;
      // Temporarily stop listening to avoid event recursion.
      sliderValue.removeListener(sliderListener);
      sliderValue.set(result);
      sliderValue.addListener(sliderListener);
    };
    sliderValue.addListener(sliderListener);
    value.addListener(valueListener);
    valueSlider.valueProperty().unbindBidirectional(value);
    valueSlider.valueProperty().bindBidirectional(sliderValue);
  }

  /**
   * When set to true the value is set to infinity when the slider is at the maximum position.
   */
  public void setMaxInfinity(boolean maxInfinity) {
    this.maxInfinity = maxInfinity;
  }

  public void onValueChange(Consumer<T> changeConsumer) {
    if (listener != null) {
      value.removeListener(listener);
    }
    listener = (observable, oldValue, newValue) -> changeConsumer.accept(clamp(newValue));
    value.addListener(listener);
  }

  protected abstract T clamp(Number value);

  public void clampBoth() {
    clampMax();
    clampMin();
  }

  public void clampMax() {
    clampMax = true;
  }

  public void clampMin() {
    clampMin = true;
  }
}
