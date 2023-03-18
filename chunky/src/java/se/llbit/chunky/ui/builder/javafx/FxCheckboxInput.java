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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import se.llbit.chunky.ui.builder.CheckboxInput;

import java.util.function.BooleanSupplier;

public class FxCheckboxInput extends FxInput<Boolean, CheckBox, CheckboxInput> implements CheckboxInput {
  protected final ChangeListener<Boolean> listener;
  protected BooleanSupplier disableSupplier = null;

  public FxCheckboxInput() {
    super(new CheckBox());
    listener = (observable, oldValue, newValue) -> callCallbacks(newValue);
    input.selectedProperty().addListener(listener);
  }

  @Override
  protected void doSet(Boolean value) {
    input.selectedProperty().removeListener(listener);
    input.setSelected(value);
    input.selectedProperty().addListener(listener);
  }

  @Override
  public Boolean get() {
    return input.isSelected();
  }

  @Override
  public CheckboxInput setName(String name) {
    input.setText(name);
    return this;
  }

  @Override
  public CheckboxInput setTooltip(String tooltip) {
    input.setTooltip(new Tooltip(tooltip));
    return this;
  }

  @Override
  public CheckboxInput setDisable(boolean value) {
    disableSupplier = null;
    input.setDisable(value);
    return this;
  }

  @Override
  public CheckboxInput setDisable(BooleanSupplier valueSupplier) {
    disableSupplier = valueSupplier;
    input.setDisable(valueSupplier.getAsBoolean());
    return this;
  }

  @Override
  public void refresh() {
    super.refresh();
    if (disableSupplier != null) {
      input.setDisable(disableSupplier.getAsBoolean());
    }
  }
}
