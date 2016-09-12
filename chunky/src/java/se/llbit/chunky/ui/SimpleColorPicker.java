/*
 * Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Window;

/**
 * A simple color picker control for JavaFX.
 *
 * <p>The control consists of a button which can be clicked to bring up
 * a color palette. The button has an icon displaying the currently selected
 * color. The color palette uses a Hue gradient selector and a HSV 2D gradient.
 * The color palette also has color swatches with neighbour colors and
 * previously selected colors are.
 */
public class SimpleColorPicker extends Button {

  private Color originalColor = Color.CRIMSON;
  private ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.CRIMSON);
  private final Popup popup;
  private final SimpleColorPalette palette;

  public SimpleColorPicker() {
    setText("Pick Color");
    palette = new SimpleColorPalette(this);
    popup = new Popup();
    popup.getContent().add(palette);
    popup.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        popup.hide();
      }
    });
    Rectangle colorSample = new Rectangle(12, 12);
    colorSample.setStroke(Color.DARKGRAY);
    colorSample.setStrokeWidth(1);
    colorSample.fillProperty().bind(color);
    setGraphic(colorSample);
    setOnAction(event -> {
      originalColor = getColor();
      palette.setColor(originalColor);
      Scene scene = getScene();
      Window window = scene.getWindow();
      popup.show(window);
      popup.setAutoHide(true);
      popup.setOnAutoHide(event2 -> updateHistory());
      Bounds buttonBounds = getBoundsInLocal();
      Point2D point = localToScreen(buttonBounds.getMinX(), buttonBounds.getMaxY());
      popup.setX(point.getX() - 9);
      popup.setY(point.getY() - 9);
    });
  }

  /**
   * Store the current color in the history palette.
   */
  protected void updateHistory() {
    palette.addToHistory(color.get());
  }

  public ObjectProperty<Color> colorProperty() {
    return color;
  }

  public void setColor(Color value) {
    color.set(value);
  }

  public Color getColor() {
    return color.get();
  }

  /**
   * Hide the color picker popup.
   */
  public void hide() {
    popup.hide();
  }

  public void revertToOriginalColor() {
    setColor(originalColor);
  }
}
