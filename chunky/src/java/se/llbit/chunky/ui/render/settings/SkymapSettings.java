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
package se.llbit.chunky.ui.render.settings;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import se.llbit.chunky.renderer.RenderController;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.math.QuickMath;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SkymapSettings extends VBox implements Initializable {
  private Scene scene;
  private File lastDirectory;

  @FXML private Button loadSkymap;
  @FXML private DoubleAdjuster skymapYaw;
  @FXML private DoubleAdjuster skymapPitch;
  @FXML private DoubleAdjuster skymapRoll;
  @FXML private ToggleButton v90;
  @FXML private HBox panoSpecific;

  public SkymapSettings() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("SkymapSettings.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  public void setPanoramic(boolean pano) {
    panoSpecific.setVisible(pano);
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    loadSkymap.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Choose Sky Texture");
      fileChooser.getExtensionFilters().add(
          new FileChooser.ExtensionFilter("Sky textures", "*.png", "*.jpg", "*.hdr", "*.pfm"));
      if (lastDirectory != null && lastDirectory.isDirectory()) {
        fileChooser.setInitialDirectory(lastDirectory);
      }
      File imageFile = fileChooser.showOpenDialog(getScene().getWindow());
      if (imageFile != null) {
        lastDirectory = imageFile.getParentFile();
        scene.sky().loadSkymap(imageFile.getAbsolutePath());
      }
    });
    skymapYaw.setName("Skymap yaw");
    skymapYaw.setTooltip("Controls the rotation of the skymap around the Y axis.");
    skymapYaw.setRange(0, 360);
    skymapYaw
      .onValueChange(value -> scene.sky().setYaw(QuickMath.degToRad(value)));
    skymapPitch.setName("Skymap pitch");
    skymapPitch.setTooltip("Controls the rotation of the skymap around the X axis.");
    skymapPitch.setRange(0, 360);
    skymapPitch
      .onValueChange(value -> scene.sky().setPitch(QuickMath.degToRad(value)));
    skymapRoll.setName("Skymap roll");
    skymapRoll.setTooltip("Controls the rotation of the skymap around the Z axis.");
    skymapRoll.setRange(0, 360);
    skymapRoll
      .onValueChange(value -> scene.sky().setRoll(QuickMath.degToRad(value)));
    v90.selectedProperty().addListener(
        (observable, oldValue, newValue) -> scene.sky().setMirrored(newValue));
  }

  public void update(Scene scene) {
    skymapYaw.set(QuickMath.radToDeg(scene.sky().getYaw()));
    skymapPitch.set(QuickMath.radToDeg(scene.sky().getPitch()));
    skymapRoll.set(QuickMath.radToDeg(scene.sky().getRoll()));
    v90.setSelected(scene.sky().isMirrored());
  }

  public void setRenderController(RenderController controller) {
    scene = controller.getSceneManager().getScene();
  }
}
