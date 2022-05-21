/*
 * Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import se.llbit.chunky.ui.controller.ResourcePackChooserController;

import java.io.IOException;

/**
 * A dialog for loading and choosing the resource packs for a scene.
 */
public class ResourcePackChooser extends Stage {
  public ResourcePackChooser(se.llbit.chunky.renderer.scene.Scene scene) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("ResourcePackChooser.fxml"));
    Parent root = loader.load();
    ResourcePackChooserController controller = loader.getController();
    setTitle("Select Resource Packs");
    getIcons().add(new Image(getClass().getResourceAsStream("/chunky-icon.png")));
    setScene(new Scene(root));
    controller.populate(scene);
    addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode() == KeyCode.ESCAPE) {
        e.consume();
        close();
      }
    });
  }
}
