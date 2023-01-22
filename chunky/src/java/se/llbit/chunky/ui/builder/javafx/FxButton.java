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

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import se.llbit.chunky.ui.builder.UiButton;
import se.llbit.chunky.world.Icon;

import java.util.ArrayList;
import java.util.function.Consumer;

public class FxButton implements UiButton {
  public final Button button;
  protected final ArrayList<Consumer<UiButton>> callbacks = new ArrayList<>();

  public FxButton(Button button) {
    this.button = button;
    this.button.setOnAction(e -> callCallbacks());
  }

  @Override
  public UiButton setText(String text) {
    button.setText(text);
    return this;
  }

  @Override
  public UiButton setTooltip(String tooltip) {
    button.setTooltip(new Tooltip(tooltip));
    return this;
  }

  @Override
  public UiButton setGraphic(Icon icon) {
    button.setGraphic(new ImageView(icon.fxImage()));
    return this;
  }

  @Override
  public UiButton callCallbacks() {
    for (Consumer<UiButton> cb : callbacks) {
      cb.accept(this);
    }
    return this;
  }

  @Override
  public UiButton addCallback(Consumer<UiButton> callback) {
    callbacks.add(callback);
    return this;
  }

  @Override
  public UiButton removeCallback(Consumer<UiButton> callback) {
    callbacks.remove(callback);
    return this;
  }
}
