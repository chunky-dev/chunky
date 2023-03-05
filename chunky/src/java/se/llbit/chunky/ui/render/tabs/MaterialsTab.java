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
package se.llbit.chunky.ui.render.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import se.llbit.chunky.block.*;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.world.ExtraMaterials;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.MaterialStore;

import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.ResourceBundle;

// TODO: customization of textures, base color, etc.
public class MaterialsTab extends HBox implements RenderControlsTab, Initializable {
  private Scene scene;

  private final DoubleAdjuster emittance = new DoubleAdjuster();
  private final DoubleAdjuster specular = new DoubleAdjuster();
  private final DoubleAdjuster ior = new DoubleAdjuster();
  private final DoubleAdjuster perceptualSmoothness = new DoubleAdjuster();
  private final DoubleAdjuster metalness = new DoubleAdjuster();
  private final ListView<String> listView;

  public MaterialsTab() {
    emittance.setName("Emittance");
    emittance.setRange(0, 100);
    emittance.setTooltip("Intensity of the light emitted from the selected material.");
    specular.setName("Specular");
    specular.setRange(0, 1);
    specular.setTooltip("Reflectivity of the selected material.");
    ior.setName("IoR");
    ior.setRange(0, 5);
    ior.setTooltip("Index of Refraction of the selected material.");
    perceptualSmoothness.setName("Smoothness");
    perceptualSmoothness.setRange(0, 1);
    perceptualSmoothness.setTooltip("Smoothness of the selected material.");
    metalness.setName("Metalness");
    metalness.setRange(0, 1);
    metalness.setTooltip("Metalness (texture-tinted reflectivity) of the selected material.");
    ObservableList<String> blockIds = FXCollections.observableArrayList();
    blockIds.addAll(MaterialStore.collections.keySet());
    blockIds.addAll(ExtraMaterials.idMap.keySet());
    blockIds.addAll(MaterialStore.blockIds);
    FilteredList<String> filteredList = new FilteredList<>(
      new SortedList<>(blockIds, Comparator.naturalOrder())
    );
    listView = new ListView<>(filteredList);
    listView.getSelectionModel().selectedItemProperty().addListener(
      (observable, oldValue, materialName) -> updateSelectedMaterial(materialName)
    );
    VBox settings = new VBox();
    settings.setSpacing(10);
    settings.getChildren().addAll(
      new Label("Material Properties"),
      emittance, specular, perceptualSmoothness, ior, metalness,
      new Label("(set to zero to disable)"));
    setPadding(new Insets(10));
    setSpacing(15);
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
    getChildren().addAll(listPane, settings);
  }

  private void updateSelectedMaterial(String materialName) {
    boolean materialExists = false;
    if (MaterialStore.collections.containsKey(materialName)) {
      double emAcc = 0;
      double specAcc = 0;
      double iorAcc = 0;
      double perceptualSmoothnessAcc = 0;
      double metalnessAcc = 0;
      Collection<Block> blocks = MaterialStore.collections.get(materialName);
      for (Block block : blocks) {
        emAcc += block.emittance;
        specAcc += block.specular;
        iorAcc += block.ior;
        perceptualSmoothnessAcc += block.getPerceptualSmoothness();
        metalnessAcc += block.metalness;
      }
      emittance.set(emAcc / blocks.size());
      specular.set(specAcc / blocks.size());
      ior.set(iorAcc / blocks.size());
      perceptualSmoothness.set(perceptualSmoothnessAcc / blocks.size());
      metalness.set(metalnessAcc / blocks.size());
      materialExists = true;
    } else if (ExtraMaterials.idMap.containsKey(materialName)) {
      Material material = ExtraMaterials.idMap.get(materialName);
      if (material != null) {
        emittance.set(material.emittance);
        specular.set(material.specular);
        ior.set(material.ior);
        perceptualSmoothness.set(material.getPerceptualSmoothness());
        metalness.set(material.metalness);
        materialExists = true;
      }
    } else if (MaterialStore.blockIds.contains(materialName)) {
      Block block = new MinecraftBlock(materialName.substring(10), Texture.air);
      scene.getPalette().applyMaterial(block);
      emittance.set(block.emittance);
      specular.set(block.specular);
      ior.set(block.ior);
      perceptualSmoothness.set(block.getPerceptualSmoothness());
      metalness.set(block.metalness);
      materialExists = true;
    }
    if (materialExists) {
      emittance.onValueChange(value -> scene.setEmittance(materialName, value.floatValue()));
      specular.onValueChange(value -> scene.setSpecular(materialName, value.floatValue()));
      ior.onValueChange(value -> scene.setIor(materialName, value.floatValue()));
      perceptualSmoothness.onValueChange(value -> scene.setPerceptualSmoothness(materialName, value.floatValue()));
      metalness.onValueChange(value -> scene.setMetalness(materialName, value.floatValue()));
    } else {
      emittance.onValueChange(value -> {});
      specular.onValueChange(value -> {});
      ior.onValueChange(value -> {});
      perceptualSmoothness.onValueChange(value -> {});
      metalness.onValueChange(value -> {});
    }
  }

  @Override public void update(Scene scene) {
    String material = listView.getSelectionModel().getSelectedItem();
    updateSelectedMaterial(material);
  }

  @Override public String getTabTitle() {
    return "Materials";
  }

  @Override public Node getTabContent() {
    return this;
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
  }

  @Override public void setController(RenderControlsFxController controller) {
    scene = controller.getRenderController().getSceneManager().getScene();
  }
}