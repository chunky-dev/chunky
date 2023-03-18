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

package se.llbit.chunky.ui.builder;

import java.util.function.Consumer;

public interface AdjusterInput<T extends Number> extends UiInput<T, AdjusterInput<T>> {
  AdjusterInput<T> setRange(double min, double max);
  AdjusterInput<T> setRange(double min, double max, double logScaleStart);
  double getMin();
  double getMax();
  AdjusterInput<T> setClamp(boolean min, boolean max);

  AdjusterInput<T> setLogarithmic(boolean logarithmic);
  AdjusterInput<T> setMaxInfinity(boolean maxInfinity);

  AdjusterInput<T> addAfterChangeHandler(Consumer<T> handler);
  AdjusterInput<T> removeAfterChangeHandler(Consumer<T> handler);
}
