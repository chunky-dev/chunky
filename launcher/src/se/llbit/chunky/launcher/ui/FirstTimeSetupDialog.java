/* Copyright (c) 2013-2016 Jesper Öqvist <jesper@llbit.se>
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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.launcher.Dialogs;
import se.llbit.chunky.launcher.LauncherSettings;
import se.llbit.chunky.resources.SettingsDirectory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * A dialog which lets the user select a settings directory where
 * Chunky settings should be stored. After an option has been selected
 * the selected directory is created and the onAccept callback is called.
 */
public class FirstTimeSetupDialog extends Stage {

  /**
   * @param onAccept callback to run when the user has selected a settings
   * directory and the settings directory has been created.
   */
  public FirstTimeSetupDialog(Runnable onAccept) {
    setTitle("Chunky First-Time Setup");

    Label description = new Label("It looks like this is your first time starting Chunky!\n"
        + "(or the previous settings could not be found)");

    Label description2 = new Label(
        "Please select which directory to store Chunky configuration in:");

    ToggleGroup group = new ToggleGroup();
    RadioButton homeDirectoryBtn = new RadioButton(
        "Home Directory (Recommended):\n" + SettingsDirectory.getHomeDirectory());
    homeDirectoryBtn.setToggleGroup(group);
    RadioButton programDirectoryBtn = new RadioButton(
        "Program Directory (for portable/thumb drive installations):\n" + SettingsDirectory
            .getProgramDirectory());
    programDirectoryBtn.setToggleGroup(group);
    RadioButton workingDirectoryBtn =
        new RadioButton("Working Directory:\n" + SettingsDirectory.getWorkingDirectory());
    workingDirectoryBtn.setToggleGroup(group);

    homeDirectoryBtn.setSelected(true);

    getIcons().add(new Image(getClass().getResourceAsStream("chunky-cfg.png")));

    Button okBtn = new Button("Use Selected Directory");
    okBtn.setDefaultButton(true);
    okBtn.setOnAction(event -> {
      File settingsDir;
      if (homeDirectoryBtn.isSelected()) {
        settingsDir = SettingsDirectory.getHomeDirectory();
      } else if (programDirectoryBtn.isSelected()) {
        settingsDir = SettingsDirectory.getProgramDirectory();
      } else {
        settingsDir = SettingsDirectory.getWorkingDirectory();
      }
      boolean initialized = false;
      if (settingsDir != null) {
        try {
          if (!settingsDir.isDirectory()) {
            settingsDir.mkdirs();
          }
          File settingsFile = new File(settingsDir, PersistentSettings.SETTINGS_FILE);
          try (PrintStream out = new PrintStream(new FileOutputStream(settingsFile))) {
            // Create an empty settings file (default settings will be used).
            out.println("{}");
          }
          File launcherSettings = new File(settingsDir, LauncherSettings.LAUNCHER_SETTINGS_FILE);
          try (PrintStream out = new PrintStream(new FileOutputStream(launcherSettings))) {
            // Create an empty settings file (default settings will be used).
            out.println("{}");
          }
          initialized = settingsFile.isFile() && launcherSettings.isFile();
        } catch (IOException e1) {
          System.err.println(e1.getMessage());
        }
        PersistentSettings.changeSettingsDirectory(settingsDir);
      }
      if (!initialized) {
        Dialogs.error("Failed to Initialize",
            "Failed to initialize Chunky configuration directory! "
                + "You may need administrative permissions on the computer to do this.");
      } else {
        onAccept.run();
        hide();
      }
    });

    Button cancelBtn = new Button("Cancel");
    cancelBtn.setCancelButton(true);
    cancelBtn.setOnAction(e -> hide());

    HBox buttons = new HBox();
    buttons.setSpacing(10);
    buttons.setAlignment(Pos.CENTER_RIGHT);
    buttons.getChildren().setAll(okBtn, cancelBtn);

    VBox vBox = new VBox();
    vBox.setPadding(new Insets(10));
    vBox.setSpacing(10);
    vBox.getChildren().setAll(description, description2,
        homeDirectoryBtn,
        programDirectoryBtn,
        workingDirectoryBtn,
        buttons);

    setScene(new Scene(vBox));
  }
}
