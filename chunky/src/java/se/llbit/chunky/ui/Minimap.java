/* Copyright (c) 2010-2012 Jesper Öqvist <jesper@llbit.se>
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

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.world.ChunkView;

/**
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class Minimap extends Map2D {
  private static final Font font = Font.font("Sans serif", FontWeight.BOLD, 11);

  public Minimap(WorldMapLoader loader, ChunkyFxController controller) {
    super(loader, controller);
  }

  public void onMousePressed(MouseEvent event) {
    mapLoader.moveView(event.getX() - getWidth() / 2, event.getY() - getHeight() / 2);
  }

  public int getWidth() {
    return (int) controller.getMinimapCanvas().getWidth();
  }

  public int getHeight() {
    return (int) controller.getMinimapCanvas().getHeight();
  }

  @Override public void viewUpdated() {
    viewUpdated(mapLoader.getMinimapView());
    mapBuffer.redrawView(mapLoader);
    repaintDirect();
  }

  @Override protected void repaint(GraphicsContext gc) {
    super.repaint(gc);
    ChunkView mapView = controller.getMap().getView();
    gc.setStroke(Color.ORANGE);
    gc.strokeRect(mapView.x0 - view.x0, mapView.z0 - view.z0, mapView.width / mapView.scale,
        mapView.height / mapView.scale);

    // Draw North direction indicator.
    gc.setFont(font);
    gc.setFill(Color.RED);
    gc.fillText("N", view.width / 2 - 4, 12);

    gc.setFill(Color.BLACK);
    gc.fillText(mapLoader.getWorldName(), 10, view.height - 10);
  }
}
