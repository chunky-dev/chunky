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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.resources.MinecraftFinder;
import se.llbit.chunky.world.World;
import se.llbit.log.Log;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class WorldChooserController implements Initializable {
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
          mapLoader.loadWorld(row.getItem());
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
          mapLoader.loadWorld(new World(directory, false));
          stage.close();
        } else {
          Log.warn("Non-directory selected.");
        }
      }
    });
    loadSelectedBtn.setOnAction(e -> {
      if (!worldTbl.getSelectionModel().isEmpty()) {
        mapLoader.loadWorld(worldTbl.getSelectionModel().getSelectedItem());
        stage.close();
      }
    });
  }

  private void fillWorldList(File worldSavesDir) {
    List<World> worlds = new ArrayList<>();
    if (worldSavesDir != null) {
      File[] worldDirs = worldSavesDir.listFiles();
      if (worldDirs != null) {
        for (File dir : worldDirs) {
          if (World.isWorldDir(dir)) {
            worlds.add(new World(dir, false));
          }
        }
      }
    }
    Collections.sort(worlds);
    worldTbl.setItems(FXCollections.observableArrayList(worlds));
    if (!worlds.isEmpty()) {
      worldTbl.getSelectionModel().select(0);
    }
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
