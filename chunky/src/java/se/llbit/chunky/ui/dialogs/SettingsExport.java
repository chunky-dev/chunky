/* Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.ui.dialogs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import se.llbit.chunky.ui.Icons;
import se.llbit.fxutil.ColumnsBoxBuilder;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This dialog lets the user export selected scene settings as a JSON string.
 *
 * <p>The dialog contains check boxes for selecting which parts of the
 * scene configuration should be exported. A text field is updated to
 * contain the JSON string for exporting the selected settings.
 */
public class SettingsExport extends Stage {
  // Maximum of 15 items in a column
  static final int MAX_COLUMN = 15;

  static final Set<String> excluded = new HashSet<>();
  static final Set<String> defaultIncluded = new HashSet<>();
  static final Map<String, Set<String>> groups = new LinkedHashMap<>();

  static {
    // Set up the hidden configuration variables.
    excluded.add("sdfVersion");
    excluded.add("name");

    // Render status variables should not be exported.
    excluded.add("renderTime");
    excluded.add("spp");
    excluded.add("pathTrace");

    groups.put("Camera", set("camera", "cameraPresets"));
    groups.put("Canvas size", set("width", "height"));
    groups.put("Emitters", set("emittersEnabled", "emitterIntensity"));
    groups.put("Entities", set("actors", "renderActors"));
    groups.put("Fog", set("fogColor", "fastFog", "fogDensity"));
    groups.put("Sky", set("sky", "transparentSky"));
    groups.put("Sun", set("sun", "sunEnabled"));
    groups.put("Water", set("waterColor", "waterOpacity", "waterVisibility", "useCustomWaterColor", "waterHeight", "stillWater"));
    groups.put("Misc", set("sppTarget", "dumpFrequency", "saveSnapshots", "world", "outputMode",
        "biomeColorsEnabled", "exposure"));
    groups.put("Advanced", set("postprocess", "rayDepth"));

    defaultIncluded.add("Fog");
    defaultIncluded.add("Water");
    defaultIncluded.add("Emitters");
    defaultIncluded.add("Sun");
    defaultIncluded.add("Sky");

    // Exclude the grouped options to avoid duplicates.
    for (Set<String> group : groups.values()) {
      excluded.addAll(group);
    }
  }

  private static Set<String> set(String... members) {
    HashSet<String> set = new HashSet<>();
    if (members != null) {
      Collections.addAll(set, members);
    }
    return set;
  }

  private final JsonObject json;

  private Map<String, CheckBox> checkMap = new HashMap<>();
  private TextField jsonField = new TextField("{}");

  /**
   * @param json the complete scene settings JSON
   */
  public SettingsExport(JsonObject json) {
    this.json = json;
    ScrollPane scrollPane = new ScrollPane();
    VBox vBox = new VBox();
    vBox.setPadding(new Insets(10));
    vBox.setSpacing(10);

    vBox.getChildren().add(new Label("Settings to export:"));

    ColumnsBoxBuilder settingsBuilder = new ColumnsBoxBuilder(MAX_COLUMN, box -> {
      box.setSpacing(10);
      box.setPadding(new Insets(10));
    });

    for (Map.Entry<String, Set<String>> group : groups.entrySet()) {
      CheckBox checkBox = new CheckBox(group.getKey());
      checkBox.setSelected(defaultIncluded.contains(group.getKey()));
      checkBox.setOnAction(event -> update());
      for (String setting : group.getValue()) {
        checkMap.put(setting, checkBox);
      }
      settingsBuilder.add(checkBox);
    }

    for (JsonMember setting : json.object()) {
      if (!excluded.contains(setting.getName())) {
        // TODO build a hierarchical checkbox system for complex settings.
        CheckBox checkBox = new CheckBox(setting.getName());
        checkBox.setSelected(defaultIncluded.contains(setting.getName()));
        checkBox.setOnAction(event -> update());
        checkMap.put(setting.getName(), checkBox);
        settingsBuilder.add(checkBox);
      }
    }

    HBox settingsBox = settingsBuilder.build();
    vBox.getChildren().add(settingsBox);

    HBox exportBox = new HBox();
    exportBox.setAlignment(Pos.BASELINE_LEFT);
    exportBox.setSpacing(10);
    exportBox.getChildren().add(new Label("Settings JSON:"));
    exportBox.getChildren().add(jsonField);
    HBox.setHgrow(jsonField, Priority.ALWAYS);

    vBox.getChildren().add(exportBox);

    HBox buttonBox = new HBox();
    buttonBox.setAlignment(Pos.BOTTOM_CENTER);

    Button selectAllButton = new Button("Select All");
    selectAllButton.setOnAction(event -> {
      checkMap.values().forEach(checkbox -> checkbox.setSelected(true));
      update();
    });
    buttonBox.getChildren().add(selectAllButton);

    Label spacer = new Label(" ");
    spacer.setMaxWidth(Double.POSITIVE_INFINITY);
    buttonBox.getChildren().add(spacer);
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button doneButton = new Button("Done");
    doneButton.setOnAction(event -> hide());
    buttonBox.getChildren().add(doneButton);

    vBox.getChildren().add(buttonBox);

    scrollPane.setContent(vBox);
    setScene(new Scene(scrollPane));
    setTitle("Settings Export");
    getIcons().add(Icons.CHUNKY_ICON);
    addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode() == KeyCode.ESCAPE) {
        e.consume();
        hide();
      }
    });

    update();

    jsonField.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        Platform.runLater(() -> jsonField.selectAll());
      }
    });

    setOnShowing(event -> {
      Platform.runLater(() -> jsonField.requestFocus());
    });
  }

  private void update() {
    JsonObject result = new JsonObject();
    for (JsonMember setting : json.object()) {
      CheckBox checkBox = checkMap.get(setting.name);
      if (checkBox != null && checkBox.isSelected()) {
        result.add(setting.copy());
      }
    }
    jsonField.setText(result.toCompactString());
  }
}
