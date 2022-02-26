/* Copyright (c) 2010-2016 Jesper Öqvist <jesper@llbit.se>
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

import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.resources.MinecraftFinder;
import se.llbit.chunky.resources.TexturePackLoader;
import se.llbit.chunky.world.World;
import se.llbit.fxutil.Dialogs;
import se.llbit.log.Log;

import java.io.File;
import java.net.URL;
import java.util.*;

public class WorldChooserController implements Initializable {
  @FXML private Label statusLabel;

  @FXML private TableView<World> worldTbl;

  @FXML private TableColumn<World, String> worldNameCol;

  @FXML private TableColumn<World, String> worldDirCol;

  @FXML private TableColumn<World, String> gameModeCol;

  @FXML private TableColumn<World, Number> seedCol;

  @FXML private Button changeWorldDirBtn;

  @FXML private Button browseBtn;

  @FXML private Button loadSelectedBtn;
  private Stage stage;

  @Override public void initialize(URL location, ResourceBundle resources) {
    worldNameCol
        .setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().levelName()));
    worldDirCol.setCellValueFactory(
        data -> new ReadOnlyStringWrapper(data.getValue().getWorldDirectory().getName()));
    gameModeCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().gameMode()));
    seedCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getSeed()));
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * Populate the world table.
   */
  public void populate(WorldMapLoader mapLoader) {
    worldTbl.setRowFactory(tbl -> {
      TableRow<World> row = new TableRow<>();
      row.setOnMouseClicked(e -> {
        if (e.getClickCount() == 2 && !row.isEmpty()) {
          this.loadWorld(row.getItem(), mapLoader);
          e.consume();
          stage.close();
        }
      });
      return row;
    });
    fillWorldList(getWorldSavesDirectory());
    changeWorldDirBtn
        .setTooltip(new Tooltip("Select the directory where Minecraft worlds are saved."));
    changeWorldDirBtn.setOnAction(e -> {
      DirectoryChooser chooser = new DirectoryChooser();
      chooser.setTitle("Choose Minecraft saves directory");
      File initialDirectory = getWorldSavesDirectory();
      if (initialDirectory != null && initialDirectory.isDirectory()) {
        chooser.setInitialDirectory(initialDirectory);
      }
      File directory = chooser.showDialog(stage);
      if (directory != null) {
        if (directory.isDirectory()) {
          fillWorldList(directory);
        } else {
          Log.warn("Non-directory selected.");
        }
      }
    });
    browseBtn.setOnAction(e -> {
      DirectoryChooser chooser = new DirectoryChooser();
      chooser.setTitle("Choose world directory");
      File initialDirectory = PersistentSettings.getLastWorld();
      if (initialDirectory != null && initialDirectory.isDirectory()) {
        chooser.setInitialDirectory(initialDirectory);
      }
      File directory = chooser.showDialog(stage);
      if (directory != null) {
        if (directory.isDirectory()) {
          this.loadWorld(World.loadWorld(directory, mapLoader.getDimension(), World.LoggedWarnings.NORMAL), mapLoader);
          stage.close();
        } else {
          Log.warn("Non-directory selected.");
        }
      }
    });
    loadSelectedBtn.setOnAction(e -> {
      if (!worldTbl.getSelectionModel().isEmpty()) {
        this.loadWorld(worldTbl.getSelectionModel().getSelectedItem(), mapLoader);
        stage.close();
      }
    });
  }

  private void loadWorld(World world, WorldMapLoader mapLoader) {
    world.getResourcePack().ifPresent((worldResourcePack) -> {
      List<String> texturePacks = new ArrayList<>(TexturePackLoader.getTexturePacks());
      if (!texturePacks.contains(worldResourcePack.getAbsolutePath())) {
        Alert loadTexturesConfirm = Dialogs.createAlert(Alert.AlertType.CONFIRMATION);
        loadTexturesConfirm.getButtonTypes().clear();
        loadTexturesConfirm.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        loadTexturesConfirm.setTitle("Bundled resource pack");
        loadTexturesConfirm.setContentText(
                "The world \"" + world.levelName() + "\" contains a resource pack. Do you want to load it now?");
        Dialogs.stayOnTop(loadTexturesConfirm);
        if (loadTexturesConfirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.YES) {
          if (!texturePacks.contains(worldResourcePack.getAbsolutePath())) {
            texturePacks.add(0, worldResourcePack.getAbsolutePath());
            TexturePackLoader.loadTexturePacks(texturePacks.toArray(new String[0]), true);
          }
        }
      }
    });
    mapLoader.loadWorld(world.getWorldDirectory());
  }

  /**
   * Disables or enables window controls (TableView & Buttons).
   */
  private void disableControls(boolean value) {
    worldTbl.setDisable(value);
    changeWorldDirBtn.setDisable(value);
    loadSelectedBtn.setDisable(value);
  }

  private void fillWorldList(final File worldSavesDir) {
    final String prevStatus = statusLabel.getText();
    statusLabel.setText("Loading worlds list...");
    disableControls(true);

    Task<List<World>> loadWorldsTask = new Task<List<World>>() {
      @Override
      protected List<World> call() {
        List<World> worlds = new ArrayList<>();
        if (worldSavesDir != null) {
          File[] worldDirs = worldSavesDir.listFiles();
          if (worldDirs != null) {
            for (File dir : worldDirs) {
              if (World.isWorldDir(dir)) {
                worlds.add(World.loadWorld(dir, World.OVERWORLD_DIMENSION,
                    World.LoggedWarnings.SILENT));
              }
            }
          }
        }
        return worlds;
      }
    };

    loadWorldsTask.setOnSucceeded((WorkerStateEvent event) -> {
      List<World> worlds = loadWorldsTask.getValue();

      Collections.sort(worlds);
      worldTbl.setItems(FXCollections.observableArrayList(worlds));
      if (!worlds.isEmpty()) {
        worldTbl.getSelectionModel().select(0);
      }

      statusLabel.setText(prevStatus);
      disableControls(false);
    });

    loadWorldsTask.setOnCancelled(event -> disableControls(false));
    loadWorldsTask.setOnFailed(event -> disableControls(false));

    Thread loadWorldsThread = new Thread(loadWorldsTask, "Worlds list loader");
    loadWorldsThread.start();
  }

  /**
   * Get the directory where Minecraft worlds are stored.
   * This is normally the parent of the active world directory.
   */
  public File getWorldSavesDirectory() {
    File worldDirectory = PersistentSettings.getLastWorld();
    if (worldDirectory == null || !worldDirectory.isDirectory()) {
      return MinecraftFinder.getSavesDirectory();
    } else {
      return worldDirectory.getParentFile();
    }
  }
}
