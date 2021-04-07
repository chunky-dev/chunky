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
package se.llbit.chunky.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonString;
import se.llbit.util.Pair;

import java.net.URL;
import java.util.*;
import java.util.function.Supplier;

public class CreditsController implements Initializable {
  @FXML private Hyperlink markdown;
  @FXML private VBox pluginBox;
  @FXML private Hyperlink fastMath;
  @FXML private Hyperlink fastutil;
  @FXML private Hyperlink apacheLicense;
  @FXML private ImageView logoImage;
  @FXML private Hyperlink ghContributors;

  public static final Map<String, Pair<JsonObject, Supplier<List<Node>>>> plugins = new HashMap<>();

  /**
   * Set the children of a plugin.
   *
   * @param name The name of the plugin. Must be the same as declared in the plugin manifest. Is case sensitive.
   * @param supplier A {@code List<Node>} supplier that returns the plugin's children.
   */
  @PluginApi
  public static void setPluginChildrenSupplier(String name, Supplier<List<Node>> supplier) {
    plugins.get(name).thing2 = supplier;
  }

  /**
   * Add a plugin to the list of plugins.
   * Note: multiple plugins with the same name will only be added once.
   *
   * @param manifest The manifest {@code JsonObject} of the plugin.
   */
  public static void addPlugin(JsonObject manifest) {
    plugins.computeIfAbsent(manifest.get("name").asString(""), name -> new Pair<>(manifest, null));
  }

  /**
   * Add a plugin using this when running a plugin directly.
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
    logoImage.setImage(new Image(getClass().getResourceAsStream("chunky.png")));

    ghContributors.setBorder(Border.EMPTY);
    ghContributors.setOnAction(e -> launchAndReset(ghContributors, "https://github.com/chunky-dev/chunky/graphs/contributors"));

    markdown.setBorder(Border.EMPTY);
    markdown.setOnAction(e -> launchAndReset(markdown, "https://github.com/chunky-dev/chunky/blob/master/licenses/Markdown.txt"));

    apacheLicense.setBorder(Border.EMPTY);
    apacheLicense.setOnAction(e -> launchAndReset(apacheLicense, "http://www.apache.org/licenses/LICENSE-2.0.txt"));

    fastMath.setBorder(Border.EMPTY);
    fastMath.setOnAction(e -> launchAndReset(fastMath, "https://commons.apache.org/proper/commons-math/"));

    fastutil.setBorder(Border.EMPTY);
    fastutil.setOnAction(e -> launchAndReset(fastutil, "https://fastutil.di.unimi.it/"));

    if (plugins.size() > 0) {
      plugins.forEach((key, item) -> pluginBox.getChildren().addAll(buildBox(item)));
    } else {
      Label label = new Label("You have no plugins installed!");
      label.setPadding(new Insets(0, 0, 0 ,-10.0));
      pluginBox.getChildren().add(label);

      Hyperlink pluginLink = new Hyperlink("Get some plugins here!");
      pluginLink.setOnAction(e -> launchAndReset(pluginLink, "https://jackjt8.github.io/ChunkyGuide/docs/setup/plugins.html"));
      pluginBox.getChildren().add(pluginLink);
    }
  }

  public void setStage(Stage stage) {
    stage.getScene().addEventFilter(KeyEvent.ANY, e -> {
      if (e.getCode() == KeyCode.ESCAPE) {
        stage.close();
      }
    });
  }

  private void launchAndReset(Hyperlink link, String url) {
    ChunkyFx.launchUrl(url);
    link.setVisited(false);
  }

  private Collection<Node> buildBox(Pair<JsonObject, Supplier<List<Node>>> data) {
    ArrayList<Node> items = new ArrayList<>();

    Label name = new Label(data.thing1.get("name").asString("") + " " + data.thing1.get("version").asString("") + " (" + data.thing1.get("author").asString("unknown") + ")");
    name.setFont(new Font("System Bold", 12.0));
    name.setPadding(new Insets(4, 0, 0, -10.0));
    items.add(name);

    items.add(new Label(data.thing1.get("description").asString("")));

    if (data.thing2 != null) {
      items.addAll(data.thing2.get());
    }

    return items;
  }
}
