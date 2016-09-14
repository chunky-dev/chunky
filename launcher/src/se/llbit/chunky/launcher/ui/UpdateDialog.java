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
package se.llbit.chunky.launcher.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import se.llbit.chunky.launcher.VersionInfo;

import java.io.IOException;

public class UpdateDialog extends Stage {
  public UpdateDialog(ChunkyLauncherController launcher, VersionInfo versionInfo) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateDialog.fxml"));
    loader.setController(new UpdateDialogController(launcher, versionInfo));
    Parent root = loader.load();
    setResizable(false);
    setTitle(String.format("Update Available: %s", versionInfo.name));
    getIcons().add(new Image(getClass().getResourceAsStream("chunky-cfg.png")));
    initModality(Modality.APPLICATION_MODAL);
    setScene(new Scene(root));
    setOnHidden(event -> launcher.setBusy(false));
  }
}
