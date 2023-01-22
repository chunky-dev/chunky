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
package se.llbit.chunky.ui.render.tabs;

import se.llbit.chunky.ui.builder.UiBuilder;
import se.llbit.chunky.ui.render.AbstractRenderControlsTab;

public class HelpTab extends AbstractRenderControlsTab {
  public HelpTab() {
    super("Help");
  }

  @Override
  public void build(UiBuilder builder) {
    builder.text().setText("Many controls contain a short description. Hover the cursor over the control to view that description.");

    builder.label();
    builder.label()
      .setText("Map controls:")
      .setFont("System Bold", 12);

    builder.text().setText("- Left-click and drag to move the map view.");
    builder.text().setText("- Left-click to select or deselect a single chunk or region.");
    builder.text().setText("- Hold Shift and left-click and drag to select multiple chunks.");
    builder.text().setText("- Hold Ctrl+Shift and left-click and drag to deselect multiple chunks.");
    builder.text().setText("- Use the mouse wheel to change the map scale (zoom).");
    builder.text().setText("- Right-click the map to show more actions.");

    builder.label();
    builder.label()
      .setText("Camera controls:")
      .setFont("System Bold", 12);

    builder.text().setText("- Left-click and drag to change the viewing angle of the camera.");
    builder.text().setText("- Use the scroll wheel to change the camera FoV (zoom).");
    builder.text().setText("- Right-click the render preview to show more actions.");

    builder.label();

    builder.label().setText("- W, A, S, D: Move the camera.");
    builder.label().setText("- R: Move the camera up.");
    builder.label().setText("- F: Move the camera down.");

    builder.label();

    builder.text().setText("- Holding Shift while using the movement controls multiplies the movement of the camera by 0.1.");
    builder.text().setText("- Holding Ctrl while using the movement controls multiplies the movement of the camera by 100.");
    builder.text().setText("- Holding Ctrl+Shift while using the movement controls multiplies the movement of the camera by 10.");
  }
}
