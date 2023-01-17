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
import se.llbit.chunky.ui.SliderAdjuster;
import se.llbit.chunky.ui.builder.AdjusterInput;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class FxAdjusterInput<T extends Number> implements AdjusterInput<T> {
  public final SliderAdjuster<T> adjuster;
  protected final ArrayList<Consumer<T>> callbacks = new ArrayList<>();
  protected final ChangeListener<Number> listener;

  public FxAdjusterInput(SliderAdjuster<T> adjuster, Function<Number, T> converter) {
    this.listener = (observable, oldValue, newValue) -> callback(converter.apply(newValue));
    this.adjuster = adjuster;
    this.adjuster.valueProperty().addListener(this.listener);
  }

  protected void callback(T value) {
    for (Consumer<T> cb : callbacks) {
      cb.accept(value);
    }
  }

  @Override
  public AdjusterInput<T> set(Number value) {
    adjuster.valueProperty().removeListener(listener);
    adjuster.set(value);
    adjuster.valueProperty().addListener(listener);
    return this;
  }

  @Override
  public T get() {
    return adjuster.get();
  }

  @Override
  public AdjusterInput<T> setName(String name) {
    adjuster.setName(name);
    return this;
  }

  @Override
  public AdjusterInput<T> setTooltip(String tooltip) {
    adjuster.setTooltip(tooltip);
    return this;
  }

  @Override
  public AdjusterInput<T> setRange(double min, double max) {
    adjuster.setRange(min, max);
    return this;
  }

  @Override
  public AdjusterInput<T> setRange(double min, double max, double logScaleStart) {
    adjuster.setRange(min, max, logScaleStart);
    return this;
  }

  @Override
  public double getMin() {
    return adjuster.getMin();
  }

  @Override
  public double getMax() {
    return adjuster.getMax();
  }

  @Override
  public AdjusterInput<T> setClamp(boolean min, boolean max) {
    adjuster.setClamp(min, max);
    return this;
  }

  @Override
  public AdjusterInput<T> setLogarithmic(boolean logarithmic) {
    if (adjuster.isLogarithmic() && !logarithmic) {
      throw new UnsupportedOperationException("Cannot make slider not logarithmic.");
    } else if (!adjuster.isLogarithmic() && logarithmic) {
      adjuster.makeLogarithmic();
    }
    return this;
  }

  @Override
  public AdjusterInput<T> setMaxInfinity(boolean maxInfinity) {
    adjuster.setMaxInfinity(maxInfinity);
    return this;
  }

  @Override
  public AdjusterInput<T> callCallbacks() {
    this.callback(this.get());
    return this;
  }

  @Override
  public AdjusterInput<T> addCallback(Consumer<T> callback) {
    callbacks.add(callback);
    return this;
  }

  @Override
  public AdjusterInput<T> removeCallback(Consumer<T> callback) {
    callbacks.remove(callback);
    return this;
  }
}
