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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.IntegerAdjuster;
import se.llbit.chunky.ui.builder.*;

import java.util.ArrayList;

public class FxBuildableUi extends VBox {
  public void build(Configurable configurable) {
    Builder builder = new Builder();
    configurable.build(builder);
    this.getChildren().setAll(builder.nodes);
  }

  public static class Builder implements UiBuilder {
    protected final ArrayList<Node> nodes = new ArrayList<>();

    @Override
    public boolean addNode(Object node) {
      if (node instanceof Node) {
        nodes.add((Node) node);
        return true;
      }
      return false;
    }

    @Override
    public void separator() {
      Separator separator = new Separator();
      separator.setPrefWidth(200);
      nodes.add(separator);
    }

    @Override
    public AdjusterInput<Integer> integerAdjuster() {
      IntegerAdjuster adjuster = new IntegerAdjuster();
      nodes.add(adjuster);
      return new FxAdjusterInput<>(adjuster, Number::intValue);
    }

    @Override
    public AdjusterInput<Double> doubleAdjuster() {
      DoubleAdjuster adjuster = new DoubleAdjuster();
      nodes.add(adjuster);
      return new FxAdjusterInput<>(adjuster, Number::doubleValue);
    }

    @Override
    public CheckboxInput checkbox() {
      CheckBox checkbox = new CheckBox();
      nodes.add(checkbox);
      return new FxCheckboxInput(checkbox);
    }

    @Override
    public UiButton button() {
      Button button = new Button();
      nodes.add(button);
      return new FxButton(button);
    }

    @Override
    public <T> ChoiceBoxInput<T> choiceBoxInput() {
      FxChoiceBoxInput<T> choiceBoxInput = new FxChoiceBoxInput<>();
      nodes.add(choiceBoxInput.input);
      return choiceBoxInput;
    }
  }
}
