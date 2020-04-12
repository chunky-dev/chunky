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
package se.llbit.chunky.launcher.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.resources.SettingsDirectory;
import se.llbit.json.Json;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class PluginManagerController implements Initializable {
  @FXML protected Button saveButton;
  @FXML protected Button addButton;
  @FXML protected Button deleteButton;
  @FXML protected Button upButton;
  @FXML protected Button downButton;
  @FXML protected TableView<JsonObject> tableView;
  @FXML protected TableColumn<JsonObject, JsonObject> pluginColumn;
  @FXML protected TableColumn<JsonObject, Boolean> enabledColumn;
  @FXML protected Button openPluginDir;
  @FXML protected Label pluginName;
  @FXML protected Label pluginVersion;
  @FXML protected Label targetVersion;
  @FXML protected Text description;
  @FXML protected Label author;
  @FXML protected VBox pluginDetails;
  @FXML protected VBox errorDetails;
  @FXML protected TextArea errorMessage;

  private File pluginsDirectory = SettingsDirectory.getPluginsDirectory();

  @Override public void initialize(URL location, ResourceBundle resources) {
    saveButton.setOnAction(e -> {
      JsonArray array = new JsonArray();
      tableView.getItems().forEach(plugin -> {
        if (plugin.get("enabled").asBoolean(false)) {
          array.add(plugin.get("jar").stringValue(""));
        }
      });
      PersistentSettings.setPlugins(array);
      saveButton.getScene().getWindow().hide();
    });
    addButton.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Select Plugin Jar");
      fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Jar", "*.jar"));
      File jar = fileChooser.showOpenDialog(addButton.getScene().getWindow());
      if (jar != null) {
        File pluginsDir = SettingsDirectory.getPluginsDirectory();
        if (!pluginsDir.isDirectory()) {
          pluginsDir.mkdirs();
        }
        boolean alreadyExists = pluginExists(jar.getName());
        if (alreadyExists) {
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
          alert.setTitle("Replace Plugin");
          alert.setContentText(String.format("Replace the plugin %s, with the new file %s?",
              jar.getName(), jar.getAbsolutePath()));
          if (alert.showAndWait().get() != ButtonType.OK) {
            return;
          }
        }
        try {
          Files.copy(jar.toPath(), pluginsDir.toPath().resolve(jar.getName()),
              StandardCopyOption.REPLACE_EXISTING);
          if (alreadyExists) {
            enablePlugin(jar.getName());
            tableView.refresh();  // Refresh the enabled cell for the plugin.
          } else {
            addPlugin(jar.getName(), true);
          }
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    });
    openPluginDir.setOnAction(event -> {
      // Running Desktop.open() on the JavaFX application thread seems to
      // lock up the application on Linux, so we create a new thread to run that.
      // This StackOverflow question seems to ask about the same bug:
      // http://stackoverflow.com/questions/23176624/javafx-freeze-on-desktop-openfile-desktop-browseuri
      new Thread(() -> {
        try {
          if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(SettingsDirectory.getPluginsDirectory());
          } else {
            Log.warn("Can not open system file browser.");
          }
        } catch (IOException e1) {
          Log.warn("Failed to open scene directory.", e1);
        }
      }).start();
    });
    deleteButton.setTooltip(new Tooltip("Delete the plugin Jar file from the plugin directory."));
    deleteButton.setOnAction(event -> {
      // Remove the plugin from the list and delete it from the plugin directory.
      JsonObject plugin = tableView.getSelectionModel().getSelectedItem();
      String jar = plugin.get("jar").asString("");
      if (!jar.isEmpty()) {
        Path jarPath = SettingsDirectory.getPluginsDirectory().toPath().resolve(jar);
        try {
          Files.delete(jarPath);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      tableView.getItems().remove(plugin);
    });
    upButton.setTooltip(new Tooltip("Move the selected plugin earlier in the load order."));
    upButton.setOnAction(event -> {
      int selected = tableView.getSelectionModel().getSelectedIndex();
      if (selected > 0) {
        JsonObject item = tableView.getItems().remove(selected);
        tableView.getItems().add(selected - 1, item);
        tableView.getSelectionModel().select(selected - 1);
      }
    });
    downButton.setTooltip(new Tooltip("Move the selected plugin later in the load order."));
    downButton.setOnAction(event -> {
      int selected = tableView.getSelectionModel().getSelectedIndex();
      if (selected >= 0 && (selected + 1) < tableView.getItems().size()) {
        JsonObject item = tableView.getItems().remove(selected);
        tableView.getItems().add(selected + 1, item);
        tableView.getSelectionModel().select(selected + 1);
      }
    });

    enabledColumn.setCellValueFactory(data -> {
      BooleanProperty property = new SimpleBooleanProperty(data.getValue().get("enabled").boolValue(true));
      property.addListener((observable, oldValue, enabled) -> {
        if (enabled) {
          enablePlugin(data.getValue());
        } else {
          data.getValue().set("enabled", Json.of(false));
        }
      });
      return property;
    });
    enabledColumn.setCellFactory(CheckBoxTableCell.forTableColumn(enabledColumn));
    pluginColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
    pluginColumn.setCellFactory(column -> new TableCell<JsonObject, JsonObject>() {
      @Override protected void updateItem(JsonObject item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
          setText("");
          getTableRow().setStyle("");
        } else {
          setText(item.get("jar").asString(""));
          if (item.get("error").asString("").isEmpty()) {
            getTableRow().setStyle("");
          } else {
            getTableRow().setStyle("-fx-background-color:#ff7878");
          }
        }
      }
    });
    tableView.setEditable(true);
    pluginColumn.setEditable(false);
    tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, plugin) -> {
      if (plugin != null) {
        String error = plugin.get("error").asString("");
        if (error.isEmpty()) {
          errorDetails.setVisible(false);
          pluginDetails.setVisible(true);
          pluginName.setText(plugin.get("name").asString(""));
          pluginVersion.setText(plugin.get("version").asString(""));
          targetVersion.setText(plugin.get("targetVersion").asString(""));
          author.setText(plugin.get("author").asString(""));
          description.setText(plugin.get("description").asString(""));
        } else {
          errorDetails.setVisible(true);
          pluginDetails.setVisible(false);
          errorMessage.setText(error);
        }
      }
    });

    Set<String> enabled = new LinkedHashSet<>();
    JsonValue plugins = PersistentSettings.getPlugins();
    for (JsonValue plugin : plugins.array()) {
      String pluginJar = plugin.asString("");
      if (!pluginJar.isEmpty()) {
        enabled.add(pluginJar);
      }
    }

    if (!pluginsDirectory.isDirectory()) {
      pluginsDirectory.mkdirs();
    }
    Log.info("Getting plugins from " + pluginsDirectory.getAbsolutePath());
    File[] pluginFiles = pluginsDirectory.listFiles((f) -> f.getName().endsWith(".jar"));
    if (pluginFiles != null) {
      // First list the enabled plugins
      for (String jar : enabled) {
        addPlugin(jar, true);
      }
      for (File file : pluginFiles) {
        if (!enabled.contains(file.getName())) {
          addPlugin(file.getName(), false);
        }
      }
    }
  }

  private void enablePlugin(String jar) {
    for (JsonObject plugin : tableView.getItems()) {
      if (plugin.get("jar").asString("").equals(jar)) {
        enablePlugin(plugin);
        return;
      }
    }
  }

  private void enablePlugin(JsonObject plugin) {
    plugin.set("enabled", Json.of(true));
    // Move the enabled plugin row to after the last already enabled plugin.
    int start = tableView.getItems().indexOf(plugin);
    if (start > 0) {
      int index = start - 1;
      for (; index >= 0; index -= 1) {
        JsonObject item = tableView.getItems().get(index);
        if (item.get("enabled").boolValue(false)) {
          break;
        }
      }
      int selected = tableView.getSelectionModel().getSelectedIndex();
      tableView.getItems().remove(start);
      tableView.getItems().add(index + 1, plugin);
      if (selected == start) {
        tableView.getSelectionModel().select(index + 1);
      }
    }
  }

  /**
   * Check if a plugin with the given name already exists.
   */
  private boolean pluginExists(String jar) {
    for (JsonObject plugin : tableView.getItems()) {
      if (plugin.get("jar").asString("").equals(jar)) {
        return true;
      }
    }
    return false;
  }

  private void addPlugin(String jar, boolean enabled) {
    Path pluginPath = pluginsDirectory.toPath().resolve(jar);
    JsonObject plugin = new JsonObject();
    try (FileSystem zipFs = FileSystems
        .newFileSystem(URI.create("jar:" + pluginPath.toUri()), Collections.emptyMap())) {
      Path manifestPath = zipFs.getPath("/plugin.json");
      if (Files.exists(manifestPath)) {
        try (InputStream in = Files.newInputStream(manifestPath);
            JsonParser parser = new JsonParser(in)) {
          JsonObject manifest = parser.parse().object();
          plugin.add("name", manifest.get("name"));
          plugin.add("version", manifest.get("version"));
          plugin.add("author", manifest.get("author"));
          plugin.add("targetVersion", manifest.get("targetVersion"));
          plugin.add("description", manifest.get("description"));
        } catch (JsonParser.SyntaxError syntaxError) {
          syntaxError.printStackTrace();
        }
      } else {
        plugin.add("error",
            String.format("Missing plugin manifest file (plugin.json) in %s.", jar));
      }
    } catch (IOException | FileSystemNotFoundException e) {
      plugin.add("error",
          String.format("Failed to read plugin %s (%s).", jar, e.getMessage()));
    }
    plugin.add("jar", jar);
    plugin.add("enabled", enabled);
    tableView.getItems().add(plugin);
  }
}
