/* Copyright (c) 2016-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016-2021 Chunky contributors
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
package se.llbit.chunky.ui.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.ui.ChunkyFx;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonString;
import se.llbit.log.Log;
import se.llbit.util.Pair;

public class CreditsController implements Initializable {

  @FXML
  private Label version;
  @FXML
  private Hyperlink gplv3;
  @FXML
  private Hyperlink markdown;
  @FXML
  private Hyperlink markdownLicense;
  @FXML
  private Hyperlink fastMath;
  @FXML
  private Hyperlink fastMathLicense;
  @FXML
  private Hyperlink fastutil;
  @FXML
  private Hyperlink fastutilLicense;
  @FXML
  private Hyperlink gson;
  @FXML
  private Hyperlink gsonLicense;
  @FXML
  private Hyperlink simplexnoise;
  @FXML
  private Hyperlink simplexnoiseLicense;
  @FXML
  private Hyperlink semver4j;
  @FXML
  private Hyperlink semver4jLicense;
  @FXML
  private VBox pluginBox;
  @FXML
  private ImageView logoImage;
  @FXML
  private Hyperlink ghContributors;
  @FXML
  private Hyperlink pluginLink;
  @FXML
  private Button closeButton;

  public static final Map<String, Pair<JsonObject, Supplier<List<Node>>>> plugins = new HashMap<>();

  /**
   * Set the children of a plugin.
   *
   * @param name     The name of the plugin. Must be the same as declared in the plugin manifest. Is
   *                 case sensitive.
   * @param supplier A {@code List<Node>} supplier that returns the plugin's children.
   */
  @PluginApi
  public static void setPluginChildrenSupplier(String name, Supplier<List<Node>> supplier) {
    if (plugins.containsKey(name)) {
      plugins.get(name).thing2 = supplier;
    } else {
      Log.warn("Plugin \"" + name + "\" not loaded correctly.");
    }
  }

  /**
   * Add a plugin to the list of plugins. Note: multiple plugins with the same name will only be
   * added once.
   *
   * @param manifest The manifest {@code JsonObject} of the plugin.
   */
  public static void addPlugin(JsonObject manifest) {
    plugins.computeIfAbsent(manifest.get("name").asString(""), name -> new Pair<>(manifest, null));
  }

  /**
   * Add a plugin. Use this when running a plugin directly.
   */
  @PluginApi
  public static void addPlugin(String name, String version, String author, String description) {
    JsonObject manifest = new JsonObject();
    manifest.set("name", new JsonString(name));
    manifest.set("version", new JsonString(version));
    manifest.set("author", new JsonString(author));
    manifest.set("description", new JsonString(description));
    addPlugin(manifest);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    logoImage.setImage(new Image(getClass().getResourceAsStream("/se/llbit/chunky/ui/chunky.png")));

    version.setText(Chunky.getMainWindowTitle());

    gplv3.setOnAction(
        e -> launchAndReset(gplv3, "https://github.com/chunky-dev/chunky/blob/master/LICENSE")
    );

    ghContributors.setBorder(Border.EMPTY);
    ghContributors.setOnAction(e -> launchAndReset(ghContributors,
        "https://github.com/chunky-dev/chunky/graphs/contributors"));

    markdown.setBorder(Border.EMPTY);
    markdown.setOnAction(
        e -> launchAndReset(markdown, "https://daringfireball.net/projects/markdown/"));
    markdownLicense.setBorder(Border.EMPTY);
    markdownLicense.setOnAction(e -> launchAndReset(markdownLicense,
        "https://daringfireball.net/projects/markdown/license"));

    fastMath.setBorder(Border.EMPTY);
    fastMath.setOnAction(
        e -> launchAndReset(fastMath, "https://commons.apache.org/proper/commons-math/"));
    fastMathLicense.setBorder(Border.EMPTY);
    fastMathLicense.setOnAction(
        e -> launchAndReset(fastMathLicense, "http://www.apache.org/licenses/LICENSE-2.0"));

    fastutil.setBorder(Border.EMPTY);
    fastutil.setOnAction(e -> launchAndReset(fastutil, "https://fastutil.di.unimi.it/"));
    fastutilLicense.setBorder(Border.EMPTY);
    fastutilLicense.setOnAction(e -> launchAndReset(fastutilLicense, "https://www.apache.org/licenses/LICENSE-2.0"));

    gson.setBorder(Border.EMPTY);
    gson.setOnAction(e -> launchAndReset(gson, "https://github.com/google/gson"));
    gsonLicense.setBorder(Border.EMPTY);
    gsonLicense.setOnAction(e -> launchAndReset(gsonLicense, "https://www.apache.org/licenses/LICENSE-2.0.txt"));

    simplexnoise.setBorder(Border.EMPTY);
    simplexnoise.setOnAction(e -> launchAndReset(simplexnoise, "https://github.com/keijiro/sketches2016/blob/master/Simplex2/SimplexNoise.java"));
    simplexnoiseLicense.setBorder(Border.EMPTY);
    simplexnoiseLicense.setOnAction(e -> launchAndReset(simplexnoiseLicense, "https://unlicense.org/"));

    semver4j.setBorder(Border.EMPTY);
    semver4j.setOnAction(e -> launchAndReset(semver4j, "https://github.com/vdurmont/semver4j"));
    semver4jLicense.setBorder(Border.EMPTY);
    semver4jLicense.setOnAction(e -> launchAndReset(semver4jLicense, "https://github.com/vdurmont/semver4j/blob/master/LICENSE.md"));

    if (plugins.size() > 0) {
      plugins.forEach((key, item) -> pluginBox.getChildren().addAll(buildBox(item)));
    } else {
      Label label = new Label("You have no plugins activated!");
      pluginBox.getChildren().add(label);
    }

    pluginLink
        .setOnAction(e -> launchAndReset(pluginLink, "https://chunky-dev.github.io/docs/plugins/"));
  }

  public void setStage(Stage stage) {
    stage.getScene().addEventFilter(KeyEvent.ANY, e -> {
      if (e.getCode() == KeyCode.ESCAPE) {
        stage.close();
      }
    });

    closeButton.setOnAction(e -> stage.close());
  }

  private void launchAndReset(Hyperlink link, String url) {
    ChunkyFx.openUrl(url);
    link.setVisited(false);
  }

  private Collection<Node> buildBox(Pair<JsonObject, Supplier<List<Node>>> data) {
    ArrayList<Node> items = new ArrayList<>();

    Label name = new Label(
        data.thing1.get("name").asString("") + " " + data.thing1.get("version").asString("") + " ("
            + data.thing1.get("author").asString("unknown") + ")");
    name.setFont(new Font("System Bold", 12.0));
    items.add(name);

    Label description = new Label(data.thing1.get("description").asString(""));
    description.setPadding(new Insets(0, 0, 4, 10.0));
    items.add(description);

    if (data.thing2 != null) {
      items.addAll(data.thing2.get());
    }

    return items;
  }
}
