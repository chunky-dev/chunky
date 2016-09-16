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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import se.llbit.chunky.main.Chunky;

/**
 * The main window of the Chunky UI.
 */
public class ChunkyFx extends Application {

  private static Chunky chunkyInstance;

  @Override public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("Chunky.fxml"));
    Parent root = loader.load();
    ChunkyFxController controller = loader.getController();
    controller.setChunky(chunkyInstance);
    stage.setTitle(Chunky.getAppName());
    Scene scene = new Scene(root);
    stage.setScene(scene);
    controller.setStageAndScene(this, stage, scene);
    stage.getIcons().add(new Image(getClass().getResourceAsStream("/chunky-icon.png")));
    stage.show();
  }

  public static void startChunkyUI(Chunky chunkyInstance) {
    ChunkyFx.chunkyInstance = chunkyInstance;
    launch();
  }
}
