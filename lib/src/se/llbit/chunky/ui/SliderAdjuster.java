/* Copyright (c) 2016-2018 Jesper Öqvist <jesper@llbit.se>
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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;

/**
 * A control for editing numeric values with a slider and text field.
 */
public abstract class SliderAdjuster<T extends Number> extends Adjuster<T> {
  private static final double LOG_ARG_MIN = 0.01; // Lower limit for logarithm argument.

  private final Slider valueSlider = new Slider();
  private double logScaleStart = LOG_ARG_MIN; 
  private double min = 0; // TODO: handle minimum.
  private double max = 100;
  protected boolean clampMax;
  protected boolean clampMin;
  private boolean logarithmic = false;
  private boolean maxInfinity = false;

  protected SliderAdjuster(Property<Number> value) {
    super(value);
    setSpacing(10);
    getChildren().setAll(nameLbl, valueSlider, valueField);
    valueSlider.valueProperty().bindBidirectional(value);
    valueSlider.setMin(0);
    valueSlider.setMax(100);
  }

  @Override public void setTooltip(String tooltip) {
    super.setTooltip(tooltip);
    valueSlider.setTooltip(new Tooltip(tooltip));
  }

  public void setRange(double min, double max) {
    setRange(min, max, min < LOG_ARG_MIN && min >= 0 ? LOG_ARG_MIN : min);
  }

  public void setRange(double min, double max, double logScaleStart) {
    this.logScaleStart = logScaleStart;
    this.min = min;
    this.max = max;
    if (!logarithmic) {
      valueSlider.setMin(min);
      valueSlider.setMax(max);
    }
  }

  public double getMin() {
    return min;
  }

  public double getMax() {
    return max;
  }

  /**
   * Make the adjuster use a logarithmic mapping for the slider position.
   */
  public void makeLogarithmic() {
    logarithmic = true;

    // In logarithmic case, slider value is always from 0 to 100.
    valueSlider.setMin(0);
    valueSlider.setMax(100);

    DoubleProperty sliderValue = new SimpleDoubleProperty();
    ChangeListener<Number> sliderListener = (observable, oldValue, newValue) -> {
      double result;
      if (maxInfinity && newValue.doubleValue() > 99.9) {
        result = Double.POSITIVE_INFINITY;
      } else if (newValue.doubleValue() < 0.1) {
        result = min; // Set value to lowest number.
      } else {
        double logMin = Math.log(logScaleStart);
        double logMax = Math.log(max);
        double range = logMax - logMin;
        result = Math.pow(Math.E, (newValue.doubleValue() / 100.0) * range + logMin);
      }
      value.setValue(result);
    };
    ChangeListener<Number> valueListener = (observable, oldValue, newValue) -> {
      double result;
      double logMin = Math.log(logScaleStart);
      double logMax = Math.log(max);
      if (newValue.doubleValue() < logScaleStart) {
        result = 0; // Set slider to lowest position.
      } else {
        double logValue = Math.log(newValue.doubleValue());
        logValue = Math.max(logMin, logValue);
        logValue = Math.min(logMax, logValue);
        double pos = (logValue - logMin) / (logMax - logMin);
        result = pos * 100;
      }
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
