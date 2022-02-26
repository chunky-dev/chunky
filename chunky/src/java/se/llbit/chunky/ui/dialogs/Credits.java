/* Copyright (c) 2012-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2012-2021 Chunky contributors
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
import javafx.stage.Stage;
import se.llbit.chunky.ui.controller.CreditsController;

import java.io.IOException;

/**
 * Displays Chunky credits.
 */
public class Credits extends Stage {
  public Credits() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("Credits.fxml"));
    Parent root = loader.load();
    CreditsController controller = loader.getController();
    setTitle("Chunky Credits");
    setScene(new Scene(root));
    this.resizableProperty().setValue(false);
    controller.setStage(this);
  }
}
