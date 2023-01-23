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

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.IntegerAdjuster;
import se.llbit.chunky.ui.builder.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class FxBuildableUi extends VBox {
  protected ArrayList<FxElement> elements = new ArrayList<>();

  public void build(Configurable configurable) {
    Builder builder = new Builder();
    configurable.build(builder);
    elements = builder.elements;
    this.getChildren().setAll(elements.stream().map(FxElement::getNode).collect(Collectors.toList()));
  }

  public void refresh() {
    elements.forEach(FxElement::refresh);
  }

  public static class Builder implements UiBuilder {
    protected final ArrayList<FxElement> elements = new ArrayList<>();

    @Override
    public boolean addNode(Object node) {
      if (node instanceof Node) {
        Node n = (Node) node;
        elements.add(() -> n);
        return true;
      }
      return false;
    }

    @Override
    public void separator() {
      Separator separator = new Separator();
      separator.setPrefWidth(200);
      elements.add(() -> separator);
    }

    @Override
    public AdjusterInput<Integer> integerAdjuster() {
      FxAdjusterInput<Integer> adjuster = new FxAdjusterInput<>(new IntegerAdjuster(), Number::intValue);
      elements.add(adjuster);
      return adjuster;
    }

    @Override
    public AdjusterInput<Double> doubleAdjuster() {
      FxAdjusterInput<Double> adjuster = new FxAdjusterInput<>(new DoubleAdjuster(), Number::doubleValue);
      elements.add(adjuster);
      return adjuster;
    }

    @Override
    public CheckboxInput checkbox() {
      FxCheckboxInput checkbox = new FxCheckboxInput();
      elements.add(checkbox);
      return checkbox;
    }

    @Override
    public UiButton button() {
      FxButton button = new FxButton();
      elements.add(button);
      return button;
    }

    @Override
    public <T> ChoiceBoxInput<T> choiceBoxInput() {
      FxChoiceBoxInput<T> choiceBoxInput = new FxChoiceBoxInput<>();
      elements.add(choiceBoxInput);
      return choiceBoxInput;
    }

    @Override
    public UiLabel label() {
      Label label = new Label();
      label.setWrapText(true);
      label.setMaxWidth(350);
      elements.add(() -> label);
      return new FxLabel(label);
    }

    @Override
    public UiText text() {
      Text text = new Text();
      text.setWrappingWidth(350);
      elements.add(() -> text);
      return new FxText(text);
    }
  }
}
