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

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import se.llbit.chunky.block.*;
import se.llbit.chunky.block.minecraft.UnknownBlock;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.world.ExtraMaterials;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.MaterialStore;
import se.llbit.fx.LuxColorPicker;
import se.llbit.math.ColorUtil;
import se.llbit.nbt.CompoundTag;

import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.ResourceBundle;

// TODO: customization of textures, base color, etc.
public class MaterialsTab extends VBox implements RenderControlsTab, Initializable {
  private Scene scene;

  private final DoubleAdjuster emittance = new DoubleAdjuster();
  private final DoubleAdjuster alpha = new DoubleAdjuster();
  private final DoubleAdjuster subsurfaceScattering = new DoubleAdjuster();
  private final LuxColorPicker diffuseColor = new LuxColorPicker();
  private final DoubleAdjuster specular = new DoubleAdjuster();
  private final DoubleAdjuster ior = new DoubleAdjuster();
  private final DoubleAdjuster perceptualSmoothness = new DoubleAdjuster();
  private final DoubleAdjuster perceptualTransmissionSmoothness = new DoubleAdjuster();
  private final DoubleAdjuster metalness = new DoubleAdjuster();
  private final DoubleAdjuster transmissionMetalness = new DoubleAdjuster();
  private final LuxColorPicker specularColor = new LuxColorPicker();
  private final LuxColorPicker transmissionSpecularColor = new LuxColorPicker();
  private final DoubleAdjuster volumeDensity = new DoubleAdjuster();
  private final DoubleAdjuster volumeAnisotropy = new DoubleAdjuster();
  private final DoubleAdjuster volumeEmittance = new DoubleAdjuster();
  private final LuxColorPicker volumeColor = new LuxColorPicker();

  private final DoubleAdjuster absorption = new DoubleAdjuster();
  private final LuxColorPicker absorptionColor = new LuxColorPicker();
  private final CheckBox opaque = new CheckBox();
  private final CheckBox hidden = new CheckBox();

  private ChangeListener<Color> specularColorListener = (observable, oldValue, newValue) -> {};
  private ChangeListener<Color> transmissionSpecularColorListener = (observable, oldValue, newValue) -> {};
  private ChangeListener<Color> diffuseColorListener = (observable, oldValue, newValue) -> {};
  private ChangeListener<Color> volumeColorListener = (observable, oldValue, newValue) -> {};
  private ChangeListener<Color> absorptionColorListener = (observable, oldValue, newValue) -> {};
  private ChangeListener<Boolean> opaqueListener = (observable, oldValue, newValue) -> {};
  private ChangeListener<Boolean> hiddenListener = (observable, oldValue, newValue) -> {};

  private final ListView<String> listView;

  public MaterialsTab() {
    emittance.setName("Emittance");
    emittance.setRange(0, 100);
    emittance.clampMin();
    emittance.setTooltip("Intensity of the light emitted from the selected material.");

    alpha.setName("Alpha");
    alpha.setRange(0, 1);
    alpha.clampBoth();
    alpha.setTooltip("Alpha (opacity) of the selected material.");

    subsurfaceScattering.setName("Subsurface scattering");
    subsurfaceScattering.setRange(0, 1);
    subsurfaceScattering.clampBoth();
    subsurfaceScattering.setTooltip("Probability of a ray to be scattered behind the surface.");

    Label diffuseColorLabel = new Label("Diffuse color:");
    diffuseColor.colorProperty().addListener(diffuseColorListener);
    HBox diffuseColorBox = new HBox(10, diffuseColorLabel, diffuseColor);

    specular.setName("Specular");
    specular.setRange(0, 1);
    specular.clampBoth();
    specular.setTooltip("Reflectivity of the selected material.");

    ior.setName("IoR");
    ior.setRange(0, 5);
    ior.clampMin();
    ior.setTooltip("Index of Refraction of the selected material.");
    ior.setMaximumFractionDigits(6);

    perceptualSmoothness.setName("Smoothness");
    perceptualSmoothness.setRange(0, 1);
    perceptualSmoothness.clampBoth();
    perceptualSmoothness.setTooltip("Smoothness of the selected material.");

    perceptualTransmissionSmoothness.setName("Transmission smoothness");
    perceptualTransmissionSmoothness.setRange(0, 1);
    perceptualTransmissionSmoothness.clampBoth();
    perceptualTransmissionSmoothness.setTooltip("Smoothness of the selected material applied to light transmission.");

    metalness.setName("Metalness");
    metalness.setRange(0, 1);
    metalness.clampBoth();
    metalness.setTooltip("Texture tinting of reflected light.");

    transmissionMetalness.setName("Transmission metalness");
    transmissionMetalness.setRange(0, 1);
    transmissionMetalness.clampBoth();
    transmissionMetalness.setTooltip("Texture tinting of refracted/transmitted light.");

    Label specularColorLabel = new Label("Specular color:");
    specularColor.colorProperty().addListener(specularColorListener);
    HBox specularColorBox = new HBox(10, specularColorLabel, specularColor);

    Label transmissionSpecularColorLabel = new Label("Transmission specular color:");
    transmissionSpecularColor.colorProperty().addListener(transmissionSpecularColorListener);
    HBox transmissionSpecularColorBox = new HBox(10, transmissionSpecularColorLabel, transmissionSpecularColor);

    volumeDensity.setName("Volume density");
    volumeDensity.setRange(0, 1);
    volumeDensity.clampMin();
    volumeDensity.setTooltip("Density of volume medium.");

    volumeAnisotropy.setName("Volume anisotropy");
    volumeAnisotropy.setRange(-1, 1);
    volumeAnisotropy.clampBoth();
    volumeAnisotropy.setTooltip("Changes the direction light is more likely to be scattered.\n" +
      "Positive values increase the chance light scatters into its original direction of travel.\n" +
      "Negative values increase the chance light scatters away from its original direction of travel.");

    volumeEmittance.setName("Volume emittance");
    volumeEmittance.setRange(0, 100);
    volumeEmittance.clampMin();
    volumeEmittance.setTooltip("Emittance of volume medium.");

    Label volumeColorLabel = new Label("Volume color:");
    volumeColor.colorProperty().addListener(volumeColorListener);
    HBox volumeColorBox = new HBox(10, volumeColorLabel, volumeColor);

    absorption.setName("Absorption");
    absorption.setRange(0, 10);
    absorption.clampMin();
    absorption.makeLogarithmic();
    absorption.setTooltip("Absorption of selected material.");

    Label absorptionColorLabel = new Label("Absorption color:");
    absorptionColor.colorProperty().addListener(absorptionColorListener);
    HBox absorptionColorBox = new HBox(10, absorptionColorLabel, absorptionColor);

    opaque.setText("Opaque");
    opaque.setTooltip(new Tooltip("Blocks surrounded by opaque blocks are replaced with stone to improve performance.\n" +
                                     "This change takes effect after chunks are loaded."));
    opaque.selectedProperty().addListener(opaqueListener);

    hidden.setText("Hidden");
    hidden.setTooltip(new Tooltip("Sets whether the block is visible."));
    hidden.selectedProperty().addListener(hiddenListener);

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

    GridPane settings = new GridPane();

    ColumnConstraints columnConstraints = new ColumnConstraints();
    columnConstraints.setPercentWidth(50);

    settings.getColumnConstraints().addAll(columnConstraints, columnConstraints);
    settings.setHgap(10);
    settings.setVgap(10);

    VBox diffuseSettings = new VBox(10, emittance, alpha, subsurfaceScattering, diffuseColorBox);
    VBox volumeSettings = new VBox(10, volumeDensity, volumeAnisotropy, volumeEmittance, volumeColorBox);
    VBox specularSettings = new VBox(10, specular, ior, perceptualSmoothness, perceptualTransmissionSmoothness);
    VBox specularColorSettings = new VBox(10, metalness, transmissionMetalness, specularColorBox, transmissionSpecularColorBox);
    VBox absorptionSettings = new VBox(10, absorption, absorptionColorBox);
    VBox otherSettings = new VBox(10, opaque, hidden);

    settings.add(diffuseSettings, 0, 0);
    settings.add(volumeSettings, 1, 0);
    settings.add(specularSettings, 0, 1);
    settings.add(specularColorSettings, 1, 1);
    settings.add(absorptionSettings, 0, 2);
    settings.add(otherSettings, 1, 2);

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
    listPane.setPrefHeight(200);

    getChildren().addAll(listPane, settings);
  }

  private void updateSelectedMaterial(String materialName) {
    boolean materialExists = false;
    diffuseColor.colorProperty().removeListener(diffuseColorListener);
    specularColor.colorProperty().removeListener(specularColorListener);
    transmissionSpecularColor.colorProperty().removeListener(transmissionSpecularColorListener);
    volumeColor.colorProperty().removeListener(volumeColorListener);
    absorptionColor.colorProperty().removeListener(absorptionColorListener);
    opaque.selectedProperty().removeListener(opaqueListener);
    hidden.selectedProperty().removeListener(hiddenListener);
    if (MaterialStore.collections.containsKey(materialName)) {
      double emAcc = 0;
      double alphaAcc = 0;
      double subsurfaceScatteringAcc = 0;
      double specAcc = 0;
      double iorAcc = 0;
      double perceptualSmoothnessAcc = 0;
      double perceptualTransmissionSmoothnessAcc = 0;
      double metalnessAcc = 0;
      double transmissionMetalnessAcc = 0;
      double volumeDensityAcc = 0;
      double volumeAnisotropyAcc = 0;
      double volumeEmittanceAcc = 0;
      double absorptionAcc = 0;

      Collection<Block> blocks = MaterialStore.collections.get(materialName);
      for (Block block : blocks) {
        emAcc += block.emittance;
        alphaAcc += block.alpha;
        subsurfaceScatteringAcc += block.subSurfaceScattering;
        specAcc += block.specular;
        iorAcc += block.ior;
        perceptualSmoothnessAcc += block.getPerceptualSmoothness();
        perceptualTransmissionSmoothnessAcc += block.getPerceptualTransmissionSmoothness();
        metalnessAcc += block.metalness;
        transmissionMetalnessAcc += block.transmissionMetalness;
        volumeDensityAcc += block.volumeDensity;
        volumeAnisotropyAcc += block.volumeAnisotropy;
        volumeEmittanceAcc += block.volumeEmittance;
        absorptionAcc += block.absorption;
      }

      emittance.set(emAcc / blocks.size());
      alpha.set(alphaAcc / blocks.size());
      subsurfaceScattering.set(subsurfaceScatteringAcc / blocks.size());
      specular.set(specAcc / blocks.size());
      ior.set(iorAcc / blocks.size());
      perceptualSmoothness.set(perceptualSmoothnessAcc / blocks.size());
      perceptualTransmissionSmoothness.set(perceptualTransmissionSmoothnessAcc / blocks.size());
      metalness.set(metalnessAcc / blocks.size());
      transmissionMetalness.set(transmissionMetalnessAcc / blocks.size());
      volumeDensity.set(volumeDensityAcc / blocks.size());
      volumeAnisotropy.set(volumeAnisotropyAcc / blocks.size());
      volumeEmittance.set(volumeEmittanceAcc / blocks.size());
      absorption.set(absorptionAcc / blocks.size());
      materialExists = true;

    } else if (ExtraMaterials.idMap.containsKey(materialName)) {
      Material material = ExtraMaterials.idMap.get(materialName);
      if (material != null) {
        emittance.set(material.emittance);
        alpha.set(material.alpha);
        subsurfaceScattering.set(material.subSurfaceScattering);
        diffuseColor.setColor(ColorUtil.toFx(material.diffuseColor));
        specular.set(material.specular);
        ior.set(material.ior);
        perceptualSmoothness.set(material.getPerceptualSmoothness());
        perceptualTransmissionSmoothness.set(material.getPerceptualTransmissionSmoothness());
        metalness.set(material.metalness);
        transmissionMetalness.set(material.transmissionMetalness);
        specularColor.setColor(ColorUtil.toFx(material.specularColor));
        transmissionSpecularColor.setColor(ColorUtil.toFx(material.transmissionSpecularColor));
        volumeDensity.set(material.volumeDensity);
        volumeAnisotropy.set(material.volumeAnisotropy);
        volumeEmittance.set(material.volumeEmittance);
        volumeColor.setColor(ColorUtil.toFx(material.volumeColor));
        absorption.set(material.absorption);
        absorptionColor.setColor(ColorUtil.toFx(material.absorptionColor));
        opaque.setSelected(material.opaque);
        hidden.setSelected(material.hidden);
        materialExists = true;
      }
    } else if (MaterialStore.blockIds.contains(materialName)) {
      Block block = new UnknownBlock(materialName.substring(10));
      for (BlockProvider provider : BlockSpec.blockProviders) {
        Block aBlock = provider.getBlockByTag(materialName, new CompoundTag());
        if (aBlock != null) {
          block = aBlock;
          break;
        }
      }
      scene.getPalette().applyMaterial(block);
      emittance.set(block.emittance);
      alpha.set(block.alpha);
      subsurfaceScattering.set(block.subSurfaceScattering);
      diffuseColor.setColor(ColorUtil.toFx(block.diffuseColor));
      specular.set(block.specular);
      ior.set(block.ior);
      perceptualSmoothness.set(block.getPerceptualSmoothness());
      perceptualTransmissionSmoothness.set(block.getPerceptualTransmissionSmoothness());
      metalness.set(block.metalness);
      transmissionMetalness.set(block.transmissionMetalness);
      specularColor.setColor(ColorUtil.toFx(block.specularColor));
      transmissionSpecularColor.setColor(ColorUtil.toFx(block.transmissionSpecularColor));
      volumeDensity.set(block.volumeDensity);
      volumeAnisotropy.set(block.volumeAnisotropy);
      volumeEmittance.set(block.volumeEmittance);
      volumeColor.setColor(ColorUtil.toFx(block.volumeColor));
      absorption.set(block.absorption);
      absorptionColor.setColor(ColorUtil.toFx(block.absorptionColor));
      opaque.setSelected(block.opaque);
      hidden.setSelected(block.hidden);
      materialExists = true;
    }
    if (materialExists) {
      emittance.onValueChange(value -> scene.setEmittance(materialName, value.floatValue()));
      alpha.onValueChange(value -> scene.setAlpha(materialName, value.floatValue()));
      subsurfaceScattering.onValueChange(value -> scene.setSubsurfaceScattering(materialName, value.floatValue()));
      diffuseColorListener = (observable, oldValue, newValue) -> scene.setDiffuseColor(materialName, ColorUtil.fromFx(newValue));
      diffuseColor.colorProperty().addListener(diffuseColorListener);
      specular.onValueChange(value -> scene.setSpecular(materialName, value.floatValue()));
      ior.onValueChange(value -> scene.setIor(materialName, value.floatValue()));
      perceptualSmoothness.onValueChange(
        value -> scene.setPerceptualSmoothness(materialName, value.floatValue())
      );
      perceptualTransmissionSmoothness.onValueChange(
        value -> scene.setPerceptualTransmissionSmoothness(materialName, value.floatValue())
      );
      metalness.onValueChange(value -> scene.setMetalness(materialName, value.floatValue()));
      transmissionMetalness.onValueChange(value -> scene.setTransmissionMetalness(materialName, value.floatValue()));
      specularColorListener = (observable, oldValue, newValue) -> scene.setSpecularColor(materialName, ColorUtil.fromFx(newValue));
      specularColor.colorProperty().addListener(specularColorListener);
      transmissionSpecularColorListener = (observable, oldValue, newValue) -> scene.setTransmissionSpecularColor(materialName, ColorUtil.fromFx(newValue));
      transmissionSpecularColor.colorProperty().addListener(transmissionSpecularColorListener);
      volumeDensity.onValueChange(value -> scene.setVolumeDensity(materialName, value.floatValue()));
      volumeAnisotropy.onValueChange(value -> scene.setVolumeAnisotropy(materialName, value.floatValue()));
      volumeEmittance.onValueChange(value -> scene.setVolumeEmittance(materialName, value.floatValue()));
      volumeColorListener = (observable, oldValue, newValue) -> scene.setVolumeColor(materialName, ColorUtil.fromFx(newValue));
      volumeColor.colorProperty().addListener(volumeColorListener);
      absorption.onValueChange(value -> scene.setAbsorption(materialName, value.floatValue()));
      absorptionColorListener = (observable, oldValue, newValue) -> scene.setAbsorptionColor(materialName, ColorUtil.fromFx(newValue));
      absorptionColor.colorProperty().addListener(absorptionColorListener);
      opaqueListener = (observable, oldValue, newValue) -> scene.setOpaque(materialName, newValue);
      opaque.selectedProperty().addListener(opaqueListener);
      hiddenListener = (observable, oldValue, newValue) -> scene.setHidden(materialName, newValue);
      hidden.selectedProperty().addListener(hiddenListener);
    } else {
      emittance.onValueChange(value -> {});
      alpha.onValueChange(value -> {});
      subsurfaceScattering.onValueChange(value -> {});
      specular.onValueChange(value -> {});
      ior.onValueChange(value -> {});
      perceptualSmoothness.onValueChange(value -> {});
      perceptualTransmissionSmoothness.onValueChange(value -> {});
      metalness.onValueChange(value -> {});
      transmissionMetalness.onValueChange(value -> {});
      volumeDensity.onValueChange(value -> {});
      volumeAnisotropy.onValueChange(value -> {});
      volumeEmittance.onValueChange(value -> {});
      absorption.onValueChange(value -> {});
    }
  }

  @Override public void update(Scene scene) {
    String material = listView.getSelectionModel().getSelectedItem();
    updateSelectedMaterial(material);
  }

  @Override public String getTabTitle() {
    return "Materials";
  }

  @Override public VBox getTabContent() {
    return this;
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
  }

  @Override public void setController(RenderControlsFxController controller) {
    scene = controller.getRenderController().getSceneManager().getScene();
  }
}
