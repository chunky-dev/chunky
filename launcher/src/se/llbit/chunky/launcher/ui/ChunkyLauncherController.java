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

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.stage.PopupWindow;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.launcher.ChunkyDeployer;
import se.llbit.chunky.launcher.ChunkyLauncher;
import se.llbit.chunky.launcher.ConsoleLogger;
import se.llbit.chunky.launcher.Dialogs;
import se.llbit.chunky.launcher.JreUtil;
import se.llbit.chunky.launcher.LaunchMode;
import se.llbit.chunky.launcher.LauncherSettings;
import se.llbit.chunky.launcher.UpdateChecker;
import se.llbit.chunky.launcher.UpdateListener;
import se.llbit.chunky.launcher.VersionInfo;
import se.llbit.chunky.resources.MinecraftFinder;
import se.llbit.chunky.resources.SettingsDirectory;
import se.llbit.chunky.ui.IntegerAdjuster;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * JavaFX window for the Chunky launcher.
 */
public final class ChunkyLauncherController implements Initializable, UpdateListener {
  private final LauncherSettings settings;

  @FXML protected ComboBox<VersionInfo> version;
  @FXML protected Button checkForUpdate;
  @FXML protected ProgressIndicator busyIndicator;
  @FXML protected TextField minecraftDirectory;
  @FXML protected Button browseMinecraft;
  @FXML protected IntegerAdjuster memoryLimit;
  @FXML protected TextField javaRuntime;
  @FXML protected Button browseJava;
  @FXML protected TextField javaOptions;
  @FXML protected TextField chunkyOptions;
  @FXML protected CheckBox enableDebugConsole;
  @FXML protected CheckBox verboseLogging;
  @FXML protected CheckBox closeConsoleOnExit;
  @FXML protected CheckBox downloadSnapshots;
  @FXML protected TextField settingsDirectory;
  @FXML protected Button openSettingsDirectory;
  @FXML protected CheckBox alwaysShowLauncher;
  @FXML protected Button launchButton;
  @FXML protected Button cancelButton;
  @FXML protected Label launcherVersion;
  @FXML protected TitledPane advancedSettings;

  public ChunkyLauncherController(LauncherSettings settings) {
    this.settings = settings;
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    busyIndicator.setTooltip(new Tooltip("Checking for Chunky update..."));
    cancelButton.setTooltip(new Tooltip("Close the Chunky launcher."));
    cancelButton.setOnAction(event -> cancelButton.getScene().getWindow().hide());
    launchButton.setTooltip(new Tooltip("Launch Chunky using the current settings."));
    launchButton.setOnAction(event -> launchChunky());
    launcherVersion.setText(ChunkyLauncher.LAUNCHER_VERSION);
    advancedSettings.setExpanded(settings.showAdvancedSettings);
    advancedSettings.expandedProperty().addListener(observable -> Platform.runLater(
            () -> advancedSettings.getScene().getWindow().sizeToScene()));

    updateVersionList();

    memoryLimit.setTooltip("Maximum Java heap space in megabytes (MiB).\n"
        + "Limited by the available memory in your computer.");
    memoryLimit.setRange(512, 1 << 14);
    memoryLimit.makeLogarithmic();
    memoryLimit.set(settings.memoryLimit);
    memoryLimit.onValueChange(value -> settings.memoryLimit = value);

    alwaysShowLauncher.setSelected(settings.showLauncher);
    alwaysShowLauncher.selectedProperty().addListener(
        (observable, oldValue, newValue) -> settings.showLauncher = newValue);

    minecraftDirectory.setText(MinecraftFinder.getMinecraftDirectory().getAbsolutePath());
    minecraftDirectory.setTooltip(new Tooltip(
        "The Minecraft directory is used to find Minecraft saves and the default texture pack."));

    browseMinecraft.setTooltip(new Tooltip("Choose Minecraft directory."));
    browseMinecraft.setOnAction(event -> {
      DirectoryChooser chooser = new DirectoryChooser();
      chooser.setTitle("Select Minecraft Directory");
      File initialDirectory = MinecraftFinder.getMinecraftDirectory();
      if (initialDirectory != null && initialDirectory.isDirectory()) {
        chooser.setInitialDirectory(initialDirectory);
      }
      File directory = chooser.showDialog(browseMinecraft.getScene().getWindow());
      if (directory != null) {
        if (MinecraftFinder.getMinecraftJar(directory, false) != null) {
          String path = directory.getAbsolutePath();
          minecraftDirectory.setText(path);
          PersistentSettings.setMinecraftDirectory(path);
        } else {
          launcherWarning("Not a Minecraft Directory",
              "Could not find a valid Minecraft installation in the selected directory.");
        }
      }
    });

    javaRuntime.setText(getConfiguredJre());

    browseJava.setTooltip(new Tooltip("Choose Java Runtime directory."));
    browseJava.setOnAction(event -> {
      DirectoryChooser chooser = new DirectoryChooser();
      chooser.setTitle("Select Java Installation");
      File jreDir = new File(javaRuntime.getText());
      if (jreDir.isDirectory()) {
        chooser.setInitialDirectory(jreDir);
      }
      File directory = chooser.showDialog(browseJava.getScene().getWindow());
      if (directory != null) {
        File jreSubDir = new File(directory, "jre");
        if (JreUtil.isJreDir(directory)) {
          javaRuntime.setText(directory.getAbsolutePath());
        } else if (JreUtil.isJreDir(jreSubDir)) {
          javaRuntime.setText(jreSubDir.getAbsolutePath());
        } else {
          launcherWarning("Not a Java Runtime Installation",
              "Could not find a valid Java installation in the selected directory.");
        }
      }
    });

    javaOptions.setTooltip(new Tooltip("Additional command-line options to pass to Java."));
    javaOptions.setText(settings.javaOptions);
    chunkyOptions.setTooltip(new Tooltip("Additional options to pass to Chunky."));
    chunkyOptions.setText(settings.chunkyOptions);
    enableDebugConsole.setTooltip(new Tooltip("Opens a debug console when Chunky is started."));
    enableDebugConsole.setSelected(settings.debugConsole);
    verboseLogging.setTooltip(new Tooltip("Enables verbose log output."));
    verboseLogging.setSelected(settings.verboseLogging);
    closeConsoleOnExit.setTooltip(new Tooltip("Close the debug console when Chunky exits."));
    closeConsoleOnExit.setSelected(settings.closeConsoleOnExit);
    downloadSnapshots.setSelected(settings.downloadSnapshots);
    downloadSnapshots.selectedProperty().addListener((observable, oldValue, newValue) -> {
      // In contrast to the other options, we have to update the
      // "download snapshots" setting directly so that when the
      // user clicks "Check for Update" we will respect the check box value.
      settings.downloadSnapshots = newValue;
      settings.save();
    });
    settingsDirectory.setText(SettingsDirectory.defaultSettingsDirectory().getAbsolutePath());
    openSettingsDirectory.setOnAction(event -> {
      // Running Desktop.open() on the JavaFX application thread seems to
      // lock up the application on Linux, so we create a new thread to run that.
      // This StackOverflow question seems to ask about the same bug:
      // http://stackoverflow.com/questions/23176624/javafx-freeze-on-desktop-openfile-desktop-browseuri
      new Thread(() -> {
        try {
          if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(SettingsDirectory.defaultSettingsDirectory());
          } else {
            Platform.runLater(
                () -> launcherWarning("Failed to open Settings Directory",
                    "Can not open system file browser."));
          }
        } catch (IOException e1) {
          Platform.runLater(() -> launcherWarning("Failed to open Settings Directory",
              "Failed to open system file browser. Reason: " + e1.getMessage()));
        }
      }).start();
    });

    checkForUpdate.setOnAction(event -> {
      if (isBusy()) {
        setBusy(true);
        UpdateChecker updateThread = new UpdateChecker(settings, this);
        updateThread.start();
      }
    });
  }

  private String getConfiguredJre() {
    String configuredJre = settings.javaDir;
    if (!configuredJre.isEmpty() && JreUtil.isJreDir(new File(configuredJre))) {
      return configuredJre;
    } else {
      return System.getProperty("java.home");
    }
  }

  private void launcherWarning(String title, String content) {
    Dialogs.warning(title, content);
  }

  public void launcherError(String title, String message) {
    Dialogs.error(title, message);
  }

  /** Show an error dialog when Chunky failed to launch. */
  private void launchFailure(String message) {
    LaunchErrorDialog dialog = new LaunchErrorDialog(message);
    dialog.show();
    dialog.toFront();
  }

  /** Initialize or update the version list. */
  void updateVersionList() {
    version.getItems().setAll(VersionInfo.LATEST);
    version.getItems().addAll(ChunkyDeployer.availableVersions());
    for (VersionInfo versionInfo : version.getItems()) {
      if (versionInfo.name.equals(settings.version)) {
        version.getSelectionModel().select(versionInfo);
        break;
      }
    }
  }

  @Override public void updateError(String message) {
    Platform.runLater(() -> {
      launcherError("Failed to download update", message);
      setBusy(false);
    });
  }

  @Override public void updateAvailable(VersionInfo latest) {
    Platform.runLater(() -> {
      try {
        UpdateDialog updateDialog = new UpdateDialog(this, latest);
        updateDialog.show();
      } catch (IOException e) {
        e.printStackTrace(System.err);
      }
    });
  }

  @Override public void noUpdateAvailable() {
    Platform.runLater(() -> {
      setBusy(false);
      Tooltip tooltip = new Tooltip("No update found. Try again later.");
      Point2D screen = checkForUpdate.localToScreen(0, 0);
      tooltip.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_RIGHT);
      tooltip.setAutoHide(true);
      tooltip.show(checkForUpdate, screen.getX() + checkForUpdate.getWidth(),
          screen.getY() + checkForUpdate.getHeight());
    });
  }

  /** Only call this from the JavaFX application thread! */
  public void setBusy(boolean busy) {
    busyIndicator.setVisible(busy);
    checkForUpdate.setDisable(busy);
  }

  /**
   * Check if an update or update check is in progress.
   * Should only be called from the JavaFX application thread.
   */
  public boolean isBusy() {
    // The busy indicator visibility is used for mutual exclusion.
    // This works because the visibility is only modified on
    // the JavaFX application thread.
    return !busyIndicator.isVisible();
  }

  /** This should only be called from the JavaFX application thread. */
  public void selectLatestVersion() {
    version.getSelectionModel().select(VersionInfo.LATEST);
  }

  /** This should only be called from the JavaFX application thread. */
  public void launchChunky() {
    settings.javaDir = javaRuntime.getText();
    settings.debugConsole = enableDebugConsole.isSelected();
    settings.verboseLogging = verboseLogging.isSelected();
    settings.closeConsoleOnExit = closeConsoleOnExit.isSelected();
    settings.javaOptions = javaOptions.getText();
    settings.chunkyOptions = chunkyOptions.getText();
    settings.version = version.getSelectionModel().getSelectedItem().name;
    settings.showLauncher = alwaysShowLauncher.isSelected();
    settings.showAdvancedSettings = advancedSettings.isExpanded();

    // Resolve specific version.
    VersionInfo version = ChunkyDeployer.resolveVersion(settings.version);
    if (!ChunkyDeployer.canLaunch(version, this, true)) {
      return;
    }

    ChunkyDeployer.LoggerBuilder loggerBuilder = () -> {
      if (settings.forceGuiConsole || (!settings.headless && settings.debugConsole)) {
        DebugConsole console = new DebugConsole(settings.closeConsoleOnExit);
        console.show();
        return console;
      } else {
        return new ConsoleLogger();
      }
    };
    PersistentSettings.setMinecraftDirectory(minecraftDirectory.getText());
    if (ChunkyDeployer.launchChunky(settings, version, LaunchMode.GUI, this::launchFailure,
        loggerBuilder) == 0) {
      settings.save();
      cancelButton.getScene().getWindow().hide();
    }
  }
}
