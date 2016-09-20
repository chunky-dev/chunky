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
package se.llbit.chunky.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneChooser extends Stage {

  public SceneChooser(ChunkyFxController chunkyFxController)
      throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("SceneChooser.fxml"));
    loader.setClassLoader(getClass()
        .getClassLoader()); // Needed for Java 1.8u40 where FXMLLoader has a null class loader for some reason.
    Parent root = loader.load();
    SceneChooserController controller = loader.getController();
    setTitle("Select 3D Scene");
    getIcons().add(new Image(getClass().getResourceAsStream("/chunky-icon.png")));
    setScene(new Scene(root));
    controller.setController(chunkyFxController);
    controller.setStage(this);
    addEventFilter(KeyEvent.ANY, e -> {
      if (e.getCode() == KeyCode.ESCAPE) {
        e.consume();
        close();
      }
    });
  }
}
