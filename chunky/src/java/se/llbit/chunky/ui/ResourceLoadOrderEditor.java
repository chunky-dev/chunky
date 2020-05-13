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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.resources.MinecraftFinder;
import se.llbit.chunky.resources.TextureCache;
import se.llbit.chunky.resources.TexturePackLoader;

import java.io.File;

public class ResourceLoadOrderEditor extends Stage {
  private final ListView<String> pathList = new ListView<>();

  private static File prevDir = null;

  public ResourceLoadOrderEditor(Runnable onResourcesChanged) {
    VBox content = new VBox();
    content.setSpacing(10);
    content.setPadding(new Insets(10));
    content.setPrefWidth(450);
    HBox buttons = new HBox();
    buttons.setAlignment(Pos.BOTTOM_RIGHT);
    buttons.setSpacing(10);
    Button apply = new Button("Apply");
    apply.setDefaultButton(true);
    apply.setOnAction(event -> {
      String[] paths = new String[pathList.getItems().size()];
      pathList.getItems().toArray(paths);
      TexturePackLoader.loadTexturePacks(paths, true);
      TextureCache.reset();
      onResourcesChanged.run();
      hide();
    });
    Button up = new Button("Up");
    up.setTooltip(new Tooltip("Move the selected resource pack up in the load order."));
    up.setOnAction(event -> {
      int index = pathList.getSelectionModel().getSelectedIndex();
      if (index > 0 && index < pathList.getItems().size()) {
        String item = pathList.getItems().remove(index);
        pathList.getItems().add(index - 1, item);
        pathList.getSelectionModel().select(index - 1);
      }
    });
    Button down = new Button("Down");
    down.setTooltip(new Tooltip("Move the selected resource pack down in the load order."));
    down.setOnAction(event -> {
      int index = pathList.getSelectionModel().getSelectedIndex();
      if (index >= 0 && index + 1 < pathList.getItems().size()) {
        String item = pathList.getItems().remove(index);
        pathList.getItems().add(index + 1, item);
        pathList.getSelectionModel().select(index + 1);
      }
    });
    Button removeResourcePack = new Button("Remove");
    removeResourcePack.setOnAction(event -> {
      int index = pathList.getSelectionModel().getSelectedIndex();
      if (index >= 0 && index < pathList.getItems().size()) {
        pathList.getItems().remove(index);
      }
    });
    Button addResourcePack = new Button("Add");
    addResourcePack.setOnAction(event -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Choose Resource Pack");
      fileChooser.getExtensionFilters().add(
          new FileChooser.ExtensionFilter("Resource Packs", "*.zip"));
      setInitialDirectory(fileChooser);
      File resourcePack = fileChooser.showOpenDialog(this);
      if (resourcePack != null) {
        prevDir = resourcePack.getParentFile();
        pathList.getItems().add(resourcePack.getAbsolutePath());
      }
    });
    buttons.getChildren().addAll(up, down, addResourcePack, removeResourcePack, apply);
    Label label = new Label("Resource packs (loaded from top to bottom):");
    String resourcePacks = PersistentSettings.getLastTexturePack();
    for (String path : resourcePacks.split(File.pathSeparator)) {
      if (!path.isEmpty()) {
        pathList.getItems().add(path);
      }
    }
    content.getChildren().addAll(label, pathList, buttons);
    setScene(new Scene(content));
    setTitle("Resource Packs");
    getIcons().add(new Image(getClass().getResourceAsStream("/chunky-icon.png")));
    addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode() == KeyCode.ESCAPE) {
        e.consume();
        hide();
      }
    });
  }

  private void setInitialDirectory(FileChooser fileChooser) {
    // First try to use the previously used (in the current session) resource packs directory:
    if (prevDir != null) {
      fileChooser.setInitialDirectory(prevDir);
      return;
    }
    // Try to set the initial directory to the top resource pack directory:
    if (!pathList.getItems().isEmpty()) {
      String path = pathList.getItems().get(0);
      File resourcePack = new File(path);
      if (resourcePack.isDirectory()) {
        fileChooser.setInitialDirectory(resourcePack);
        return;
      } else if (resourcePack.isFile() && resourcePack.getParentFile() != null) {
        fileChooser.setInitialDirectory(resourcePack.getParentFile());
        return;
      }
    }
    // Try to set the default Minecraft resource packs directory:
    File mcDir = MinecraftFinder.getMinecraftDirectory();
    if (mcDir != null && mcDir.isDirectory()) {
      File packsDir = new File(mcDir, "resourcepacks");
      if (packsDir.isDirectory()) {
        fileChooser.setInitialDirectory(packsDir);
      }
    }
  }
}
