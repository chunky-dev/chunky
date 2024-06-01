/* Copyright (c) 2018 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.ui.elements;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import se.llbit.chunky.ui.Adjuster;
import se.llbit.math.QuickMath;

/**
 * A control for editing angles with a knob and text field.
 */
public class AngleAdjuster extends Adjuster<Double> {
  private final Label knob;
  private final ImageView dimple;
  private final SimpleDoubleProperty angle = new SimpleDoubleProperty();

  public AngleAdjuster() {
    super(new SimpleDoubleProperty());
    ImageView wheel = new ImageView(new Image(getClass().getResourceAsStream("jog_wheel.png")));
    knob = new Label();
    knob.setGraphic(wheel);
    knob.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    knob.setOnMouseDragged(this::handleDrag);
    knob.setOnMousePressed(this::handleDrag);
    dimple = new ImageView(new Image(getClass().getResourceAsStream("jog_wheel_dimple.png")));
    dimple.setTranslateX(16);
    dimple.setDisable(true);
    StackPane stackPane = new StackPane();
    stackPane.getChildren().addAll(knob, dimple);
    getChildren().setAll(nameLbl, valueField, stackPane);
    angle.bindBidirectional(value);
    angle.addListener((observable, oldValue, newValue) -> {
      double angle = newValue.doubleValue();
      angle = QuickMath.degToRad(angle);
      dimple.setTranslateX(Math.cos(angle) * 16);
      dimple.setTranslateY(- Math.sin(angle) * 16);
    });
    knob.setOnMouseReleased(e -> {
      this.fireEvent(new Event(Adjuster.AFTER_VALUE_CHANGE));
    });
  }

  @Override public void setTooltip(String tooltip) {
    super.setTooltip(tooltip);
    knob.setTooltip(new Tooltip(tooltip));
  }

  private void handleDrag(MouseEvent event) {
    Point2D pos = knob.localToParent(event.getX(), event.getY());
    Point2D center = knob.localToParent(0, 0);
    center = center.add(30, 30);
    Point2D diff = new Point2D(pos.getX() - center.getX(), - (pos.getY() - center.getY()));
    double angle = Math.atan2(diff.getY(), diff.getX());
    setAndUpdate(QuickMath.radToDeg(angle));
  }

  @Override protected Double clamp(Number value) {
    return value.doubleValue();
  }
}
