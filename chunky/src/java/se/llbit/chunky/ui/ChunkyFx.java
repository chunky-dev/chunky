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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.resources.SettingsDirectory;

import java.io.File;

/**
 * The main window of the Chunky UI.
 *
 * <p>This should only be started via se.llbit.chunky.main.Chunky.
 * Do not try to start Chunky with the default JavaFX main method!
 */
public class ChunkyFx extends Application {

  private static Chunky chunkyInstance;

  @Override public void start(Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("Chunky.fxml"));
      ChunkyFxController controller = new ChunkyFxController(chunkyInstance);
      loader.setController(controller);
      Parent root = loader.load();
      stage.setTitle(Chunky.getMainWindowTitle());
      Scene scene = new Scene(root);
      stage.setScene(scene);
      controller.setApplication(this, scene);
      stage.getIcons().add(new Image(getClass().getResourceAsStream("/chunky-icon.png")));
      stage.setOnCloseRequest(event -> {
        Platform.exit();
        System.exit(0);
      });
      File stylesheet = new File(SettingsDirectory.getSettingsDirectory(), "style.css");
      if (stylesheet.isFile()) {
        scene.getStylesheets().add(stylesheet.toURI().toURL().toExternalForm());
      } else {
        scene.getStylesheets().add("style.css");
      }
      stage.setWidth(1800);
      stage.setMaximized(true);
      stage.show();
    } catch (Exception e) {
      e.printStackTrace(System.err);
    }
  }

  public static void startChunkyUI(Chunky chunkyInstance) {
    ChunkyFx.chunkyInstance = chunkyInstance;
    launch();
  }
}
