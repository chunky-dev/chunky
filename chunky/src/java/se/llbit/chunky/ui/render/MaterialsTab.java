/*
 * Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.ui.render;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.RenderControlsFxController;
import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.Material;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class MaterialsTab extends Tab implements RenderControlsTab, Initializable {
  private Scene scene;

  private final DoubleAdjuster emittance = new DoubleAdjuster();
  private final DoubleAdjuster specular = new DoubleAdjuster();
  private final DoubleAdjuster ior = new DoubleAdjuster();

  public MaterialsTab() {
    setText("Materials");
    emittance.setName("Emittance");
    specular.setName("Specular");
    specular.setRange(0, 1);
    ior.setName("IoR");
    ObservableList<String> blockIds = FXCollections.observableArrayList();
    blockIds.addAll(Block.collections.keySet());
    blockIds.addAll(Block.idMap.keySet());
    FilteredList<String> filteredList = new FilteredList<>(blockIds);
    ListView<String> listView = new ListView<>(filteredList);
    listView.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, materialName) -> {
          boolean materialExists = false;
          if (Block.collections.containsKey(materialName)) {
            double emAcc = 0;
            double specAcc = 0;
            double iorAcc = 0;
            Collection<Block> blocks = Block.collections.get(materialName);
            for (Block block : blocks) {
              emAcc += block.emittance;
              specAcc += block.specular;
              iorAcc += block.ior;
            }
            emittance.set(emAcc / blocks.size());
            specular.set(specAcc / blocks.size());
            ior.set(iorAcc / blocks.size());
            materialExists = true;
          } else if (Block.idMap.containsKey(materialName)) {
            Material material = Block.idMap.get(materialName);
            if (material != null) {
              emittance.set(material.emittance);
              specular.set(material.specular);
              ior.set(material.ior);
              materialExists = true;
            }
          }
          if (materialExists) {
            emittance.onValueChange(value -> scene.setEmittance(materialName, value.floatValue()));
            specular.onValueChange(value -> scene.setSpecular(materialName, value.floatValue()));
            ior.onValueChange(value -> scene.setIor(materialName, value.floatValue()));
          } else {
            emittance.onValueChange(value -> {});
            specular.onValueChange(value -> {});
            ior.onValueChange(value -> {});
          }
        }
    );
    VBox settings = new VBox();
    settings.setSpacing(10);
    settings.getChildren().addAll(
        new Label("Material Properties"),
        emittance, specular, ior,
        new Label("(set to zero to disable)"));
    HBox content = new HBox();
    content.setPadding(new Insets(10));
    content.setSpacing(15);
    TextField filterField = new TextField();
    filterField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.trim().isEmpty()) {
        filteredList.setPredicate(name -> true);
      } else {
        filteredList.setPredicate(name -> name.contains(newValue));
      }
    });
    HBox filterBox = new HBox();
    filterBox.setAlignment(Pos.BASELINE_LEFT);
    filterBox.setSpacing(10);
    filterBox.getChildren().addAll(new Label("Filter:"), filterField);
    VBox listPane = new VBox();
    listPane.setSpacing(10);
    listPane.getChildren().addAll(filterBox, listView);
    content.getChildren().addAll(listPane, settings);
    setContent(content);
  }

  @Override public void update(Scene scene) {
  }

  @Override public Tab getTab() {
    return this;
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
  }

  @Override public void setController(RenderControlsFxController controller) {
    scene = controller.getRenderController().getSceneManager().getScene();
  }
}
