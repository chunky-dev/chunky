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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.TexturePackLoader;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.dialogs.ResourceLoadOrderEditor;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.world.Icon;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TexturesTab extends ScrollPane implements RenderControlsTab, Initializable {
  private Scene scene;

  @FXML
  private Button editResourcePacks;
  @FXML
  private CheckBox singleColorBtn;
  @FXML
  private CheckBox disableDefaultTexturesBtn;

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
    editResourcePacks.setOnAction(e -> {
      ResourceLoadOrderEditor editor = new ResourceLoadOrderEditor(() -> {
        scene.refresh();
        scene.rebuildBvh();
      });
      editor.show();
    });

    singleColorBtn.setSelected(PersistentSettings.getSingleColorTextures());
    singleColorBtn.selectedProperty().addListener((observable, oldValue, newValue) -> {
      PersistentSettings.setSingleColorTextures(newValue);
      TexturePackLoader.loadTexturePacks(PersistentSettings.getLastTexturePack(), true);
      scene.refresh();
      scene.rebuildBvh();
    });

    disableDefaultTexturesBtn.setSelected(PersistentSettings.getDisableDefaultTextures());
    disableDefaultTexturesBtn.selectedProperty().addListener((observable, oldValue, newValue) -> {
      PersistentSettings.setDisableDefaultTextures(newValue);
      TexturePackLoader.loadTexturePacks(PersistentSettings.getLastTexturePack(), true);
      scene.refresh();
      scene.rebuildBvh();
    });
  }

  @Override
  public void setController(RenderControlsFxController controller) {
    scene = controller.getRenderController().getSceneManager().getScene();
  }

  @Override
  public void update(Scene scene) {
  }

  @Override
  public String getTabTitle() {
    return "Resource Packs & Textures";
  }

  @Override
  public Node getTabContent() {
    return this;
  }
}
