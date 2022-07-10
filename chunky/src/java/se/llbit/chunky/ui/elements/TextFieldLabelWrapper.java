/* Copyright (c) 2022 Chunky contributors
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

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

@DefaultProperty("textField")
public class TextFieldLabelWrapper extends StackPane {

  private final StringProperty labelTextProperty = new SimpleStringProperty();
  private final Label labelNode = new Label();

  private final ObjectProperty<TextField> textFieldProperty = new SimpleObjectProperty<>();

  private boolean requirePaddingUpdate = false;
  private Insets defaultPadding = new Insets(4, 6, 4, 6);

  protected TextField defaultTextField() {
    return new TextField();
  }

  public TextFieldLabelWrapper() {
    this.getStyleClass().add("text-field-label-wrapper");

    labelNode.textProperty().bind(labelTextProperty);
    labelTextProperty.addListener(unused -> requirePaddingUpdate = true);
    labelNode.setTranslateX(5);
    labelNode.setPadding(new Insets(0, 1, 0, 1));
    labelNode.setMouseTransparent(true);
    getChildren().add(labelNode);

    setAlignment(Pos.BASELINE_LEFT);
    setAccessibleRole(AccessibleRole.TEXT_FIELD);
    setTextField(null);
  }

  public void setTextField(TextField textField) {
    if (textField == null) {
      textField = defaultTextField();
    }
    getChildren().remove(textFieldProperty.get());

    textFieldProperty.set(textField);
    labelNode.setLabelFor(textField);

    getChildren().add(0, textField);
  }

  @Override
  protected void layoutChildren() {
    super.layoutChildren();
    if (requirePaddingUpdate) {
      updateTextFieldPadding();
    }
  }

  private void updateTextFieldPadding() {
    TextField textField = textFieldProperty.get();
    if (defaultPadding == null) {
      defaultPadding = textField.getPadding();
    }
    textField.setPadding(new Insets(
      defaultPadding.getTop(),
      defaultPadding.getRight(),
      defaultPadding.getBottom(),
      defaultPadding.getLeft() + labelNode.getLayoutBounds().getWidth()
    ));
    requirePaddingUpdate = false;
  }

  public TextField getTextField() {
    return textFieldProperty.get();
  }

  public void setLabelText(String labelText) {
    labelTextProperty.set(labelText);
  }

  public String getLabelText() {
    return labelTextProperty.get();
  }

  public Label getLabelNode() {
    return labelNode;
  }
}
