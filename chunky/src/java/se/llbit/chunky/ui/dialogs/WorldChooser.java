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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.ui.Icons;
import se.llbit.chunky.ui.controller.WorldChooserController;

import java.io.IOException;

/**
 * A dialog for choosing a Minecraft world.
 */
public class WorldChooser extends Stage {
  public WorldChooser(WorldMapLoader mapLoader) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("WorldChooser.fxml"));
    Parent root = loader.load();
    WorldChooserController controller = loader.getController();
    setTitle("Select World");
    getIcons().add(Icons.CHUNKY_ICON);
    setScene(new Scene(root));
    controller.setStage(this);
    controller.populate(mapLoader);
    addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode() == KeyCode.ESCAPE) {
        e.consume();
        close();
      }
    });
  }
}
