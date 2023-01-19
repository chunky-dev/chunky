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

import se.llbit.util.Registerable;

import java.util.function.Consumer;

public interface UiBuilder {
  /**
   * Add a node to this builder. Return {@code false} if the node could not be added. This is implementation
   * specific and should be type checked.
   */
  default boolean addNode(Object node) {
    return false;
  }

  default void addNodeOrElse(Object node, Consumer<UiBuilder> orElse) {
    if (!addNode(node)) {
      orElse.accept(this);
    }
  }

  void separator();
  AdjusterInput<Integer> integerAdjuster();
  AdjusterInput<Double> doubleAdjuster();
  CheckboxInput checkbox();
  UiButton button();
  <T> ChoiceBoxInput<T> choiceBoxInput();
  default <T extends Registerable> ChoiceBoxInput<T> registerableChoiceBoxInput() {
    ChoiceBoxInput<T> input = choiceBoxInput();
    input.setStringConverter(Registerable::getName);
    input.setTooltipConverter(Registerable::getDescription);
    return input;
  }
}
