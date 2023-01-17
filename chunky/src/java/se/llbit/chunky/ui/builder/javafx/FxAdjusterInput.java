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
import se.llbit.chunky.ui.Adjuster;
import se.llbit.chunky.ui.SliderAdjuster;
import se.llbit.chunky.ui.builder.AdjusterInput;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class FxAdjusterInput<T extends Number> extends FxInput<T, SliderAdjuster<T>, AdjusterInput<T>> implements AdjusterInput<T> {
  protected final ArrayList<Consumer<T>> afterChangeHandlers = new ArrayList<>();
  protected final ChangeListener<Number> listener;

  public FxAdjusterInput(SliderAdjuster<T> adjuster, Function<Number, T> converter) {
    super(adjuster);
    listener = (observable, oldValue, newValue) -> callCallbacks(converter.apply(newValue));
    input.valueProperty().addListener(listener);
    input.addEventHandler(Adjuster.AFTER_VALUE_CHANGE, e -> {
      T value = get();
      for (Consumer<T> handler : afterChangeHandlers) {
        handler.accept(value);
      }
    });
  }

  @Override
  public AdjusterInput<T> set(Number value) {
    input.valueProperty().removeListener(listener);
    input.set(value);
    input.valueProperty().addListener(listener);
    return this;
  }

  @Override
  public T get() {
    return input.get();
  }

  @Override
  public AdjusterInput<T> setName(String name) {
    input.setName(name);
    return this;
  }

  @Override
  public AdjusterInput<T> setTooltip(String tooltip) {
    input.setTooltip(tooltip);
    return this;
  }

  @Override
  public AdjusterInput<T> setRange(double min, double max) {
    input.setRange(min, max);
    return this;
  }

  @Override
  public AdjusterInput<T> setRange(double min, double max, double logScaleStart) {
    input.setRange(min, max, logScaleStart);
    return this;
  }

  @Override
  public double getMin() {
    return input.getMin();
  }

  @Override
  public double getMax() {
    return input.getMax();
  }

  @Override
  public AdjusterInput<T> setClamp(boolean min, boolean max) {
    input.setClamp(min, max);
    return this;
  }

  @Override
  public AdjusterInput<T> setLogarithmic(boolean logarithmic) {
    if (input.isLogarithmic() && !logarithmic) {
      throw new UnsupportedOperationException("Cannot make slider not logarithmic.");
    } else if (!input.isLogarithmic() && logarithmic) {
      input.makeLogarithmic();
    }
    return this;
  }

  @Override
  public AdjusterInput<T> setMaxInfinity(boolean maxInfinity) {
    input.setMaxInfinity(maxInfinity);
    return this;
  }

  @Override
  public AdjusterInput<T> addAfterChangeHandler(Consumer<T> handler) {
    afterChangeHandlers.add(handler);
    return this;
  }

  @Override
  public AdjusterInput<T> removeAfterChangeHandler(Consumer<T> handler) {
    afterChangeHandlers.remove(handler);
    return this;
  }
}
