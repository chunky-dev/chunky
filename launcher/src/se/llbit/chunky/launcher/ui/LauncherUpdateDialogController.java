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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import se.llbit.chunky.launcher.LauncherInfo;
import se.llbit.chunky.launcher.LauncherSettings;

import java.net.URL;
import java.util.ResourceBundle;

public final class LauncherUpdateDialogController implements Initializable {
  private final LauncherInfo launcherInfo;
  private final LauncherSettings settings;

  @FXML public Text releaseInfo;
  @FXML public TextArea releaseNotes;
  @FXML public Button downloadButton;
  @FXML public Button cancelButton;

  public LauncherUpdateDialogController(LauncherSettings settings, LauncherInfo launcherInfo) {
    this.launcherInfo = launcherInfo;
    this.settings = settings;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    releaseInfo.setText(String.format("Version %s released on %s",
        launcherInfo.version.toString(), launcherInfo.date));
    if (launcherInfo.notes.isEmpty()) {
      releaseNotes.setText("No release notes available.");
    } else {
      releaseNotes.setText(launcherInfo.notes);
    }

    cancelButton.setOnAction(event -> cancelButton.getScene().getWindow().hide());
    downloadButton.setOnAction(event -> {
      ChunkyLauncherFx.launchWebpage(settings.getResourceUrl(launcherInfo.path));
    });
  }
}
