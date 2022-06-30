/* Copyright (c) 2016 - 2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016 - 2021 Chunky contributors
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

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.RenderController;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.SceneManager;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.dialogs.ResourcePackChooser;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.world.Icon;
import se.llbit.fxutil.Dialogs;
import se.llbit.log.Log;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TexturesTab extends ScrollPane implements RenderControlsTab, Initializable {
  private RenderController controller;
  private SceneManager sceneManager;

  @FXML
  private CheckBox biomeColors;
  @FXML
  private CheckBox biomeBlending;
  @FXML
  private CheckBox singleColorBtn;
  @FXML
  private Button editResourcePacks;

  public TexturesTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("TexturesTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    editResourcePacks.setTooltip(
      new Tooltip("Select resource packs Chunky uses to load textures."));
    editResourcePacks.setGraphic(new ImageView(Icon.pencil.fxImage()));
    editResourcePacks.setOnAction(evt -> {
      try {
        ResourcePackChooser resourcePackChooser = new ResourcePackChooser(
          sceneManager.getScene(),
          sceneManager.getTaskTracker()
        );
        resourcePackChooser.show();
      } catch (IOException ex) {
        Log.error("Failed to create resource pack chooser window.", ex);
      }
    });

    singleColorBtn.setTooltip(new Tooltip("Set block textures to a single color which is the average of all color values of its current texture."));
    singleColorBtn.setSelected(PersistentSettings.getSingleColorTextures());
    singleColorBtn.selectedProperty().addListener((observable, oldValue, newValue) -> {
      Scene scene = sceneManager.getScene();
      PersistentSettings.setSingleColorTextures(newValue);
      scene.refresh();
      scene.rebuildBvh();
    });

    biomeColors.setTooltip(new Tooltip("Color grass and tree leaves according to the biome."));
    biomeColors.selectedProperty().addListener((observable, oldValue, newValue) -> {
      sceneManager.getScene().setBiomeColorsEnabled(newValue);
    });
    biomeColors.selectedProperty().addListener((observable, oldValue, newValue) -> {
      Scene scene = sceneManager.getScene();
      boolean enabled = scene.biomeColorsEnabled();

      scene.setBiomeColorsEnabled(newValue);
      biomeBlending.setDisable(!newValue);

      if(!scene.haveLoadedChunks()) {
        return;
      }
      if(enabled != newValue && newValue) { // Jank to avoid not snapshotting the scene settings
        alertIfReloadNeeded("biome colors");
      }
    });

    biomeBlending.setTooltip(new Tooltip("Blend edges of biomes (looks better but loads slower)."));
    biomeBlending.selectedProperty().addListener((observable, oldValue, newValue) -> {
      Scene scene = sceneManager.getScene();
      boolean enabled = scene.biomeBlendingEnabled();

      scene.setBiomeBlendingEnabled(newValue);

      if(enabled != newValue && newValue) { // Jank to avoid not snapshotting the scene settings
        alertIfReloadNeeded("biome blending");
      }
    });
  }

  private void alertIfReloadNeeded(String changedFeature) {
    if(!sceneManager.getScene().haveLoadedChunks()) {
      return;
    }
    Alert warning = Dialogs.createAlert(Alert.AlertType.CONFIRMATION);
    warning.setContentText("The selected chunks need to be reloaded in order for "+changedFeature+" to update.");
    warning.getButtonTypes().setAll(
      ButtonType.CANCEL,
      new ButtonType("Reload chunks", ButtonBar.ButtonData.FINISH));
    warning.setTitle("Chunk reload required");
    ButtonType result = warning.showAndWait().orElse(ButtonType.CANCEL);
    if (result.getButtonData() == ButtonBar.ButtonData.FINISH) {
      controller.getSceneManager().reloadChunks();
    }
  }

  @Override
  public void setController(RenderControlsFxController fxController) {
    controller = fxController.getRenderController();
    sceneManager = controller.getSceneManager();
  }

  @Override
  public void update(Scene scene) {
    biomeColors.setSelected(scene.biomeColorsEnabled());
    biomeBlending.setDisable(!scene.biomeColorsEnabled());
    biomeBlending.setSelected(scene.biomeBlendingEnabled());
  }

  @Override
  public String getTabTitle() {
    return "Textures & Resource Packs";
  }

  @Override
  public Node getTabContent() {
    return this;
  }
}
