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


import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ChoiceBoxInput<T> extends UiInput<T, ChoiceBoxInput<T>> {
  ChoiceBoxInput<T> addItems(Collection<? extends T> items);
  default ChoiceBoxInput<T> addItems(T... items) {
    return addItems(Arrays.asList(items));
  }
  ChoiceBoxInput<T> select(Predicate<T> predicate);
  ChoiceBoxInput<T> setStringConverter(Function<T, String> toString);
  /**
   * Set the tooltip converter. Return {@code null} for no tooltip.
   */
  ChoiceBoxInput<T> setTooltipConverter(Function<T, String> toTooltip);
}
