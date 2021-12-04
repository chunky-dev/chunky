/* Copyright (c) 2021 Chunky Contributors
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
package se.llbit.chunky.launcher.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import se.llbit.chunky.launcher.LauncherInfo;
import se.llbit.chunky.launcher.LauncherSettings;

import java.io.IOException;

public class LauncherUpdateDialog extends Stage {
  public LauncherUpdateDialog(LauncherSettings settings, LauncherInfo info) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("LauncherUpdateDialog.fxml"));
    loader.setController(new LauncherUpdateDialogController(settings, info));
    Parent root = loader.load();
    setResizable(false);
    setTitle(String.format("New Launcher Available: %s", info.name));
    getIcons().add(new Image(getClass().getResourceAsStream("chunky-cfg.png")));
    initModality(Modality.APPLICATION_MODAL);
    setScene(new Scene(root));
  }
}
