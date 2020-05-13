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
package se.llbit.chunky.ui.render;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import se.llbit.chunky.renderer.RenderController;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.Sky;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.math.QuickMath;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SkyboxSettings extends VBox implements Initializable {
  private Scene scene;

  @FXML private Button up;
  @FXML private Button down;
  @FXML private Button front;
  @FXML private Button back;
  @FXML private Button right;
  @FXML private Button left;
  @FXML private DoubleAdjuster skyboxRotation;

  private File skyboxDirectory;

  public SkyboxSettings() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("SkyboxSettings.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    up.setOnAction(e -> selectSkyboxTexture(Sky.SKYBOX_UP));
    down.setOnAction(e -> selectSkyboxTexture(Sky.SKYBOX_DOWN));
    front.setOnAction(e -> selectSkyboxTexture(Sky.SKYBOX_FRONT));
    back.setOnAction(e -> selectSkyboxTexture(Sky.SKYBOX_BACK));
    right.setOnAction(e -> selectSkyboxTexture(Sky.SKYBOX_RIGHT));
    left.setOnAction(e -> selectSkyboxTexture(Sky.SKYBOX_LEFT));
    skyboxRotation.setName("Skybox rotation");
    skyboxRotation.setTooltip("Controls the azimuth rotational offset of the skybox.");
    skyboxRotation.setRange(0, 360);
    skyboxRotation
        .onValueChange(value -> scene.sky().setRotation(QuickMath.degToRad(value)));
  }

  private void selectSkyboxTexture(int textureIndex) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose Sky Texture");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Sky textures", "*.png", "*.jpg", "*.hdr", "*.pfm"));
    if (skyboxDirectory != null && skyboxDirectory.isDirectory()) {
      fileChooser.setInitialDirectory(skyboxDirectory);
    }
    File imageFile = fileChooser.showOpenDialog(getScene().getWindow());
    if (imageFile != null) {
      skyboxDirectory = imageFile.getParentFile();
      scene.sky().loadSkyboxTexture(imageFile.getAbsolutePath(), textureIndex);
    }
  }

  public void setRenderController(RenderController controller) {
    scene = controller.getSceneManager().getScene();
  }

  public void update(Scene scene) {
    skyboxRotation.set(QuickMath.radToDeg(scene.sky().getRotation()));
  }
}
