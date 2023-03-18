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

import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import se.llbit.chunky.ui.builder.UiText;

public class FxText implements UiText {
  public final Text node;

  public FxText(Text text) {
    this.node = text;
  }

  @Override
  public UiText setText(String text) {
    node.setText(text);
    return this;
  }

  @Override
  public UiText setTooltip(String tooltip) {
    Tooltip.install(node, new Tooltip(tooltip));
    return this;
  }

  @Override
  public UiText setFont(String font, double size) {
    node.setFont(new Font(font, size));
    return this;
  }
}
