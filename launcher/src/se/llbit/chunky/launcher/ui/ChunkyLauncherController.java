/* Copyright (c) 2013-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2013-2021 Chunky Contributors
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
import javafx.stage.WindowEvent;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.launcher.*;
import se.llbit.chunky.resources.MinecraftFinder;
import se.llbit.chunky.resources.SettingsDirectory;
import se.llbit.chunky.ui.IntegerAdjuster;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * JavaFX window for the Chunky launcher.
 */
public final class ChunkyLauncherController implements Initializable, UpdateListener {
  final LauncherSettings settings;

  @FXML public ComboBox<VersionInfo> version;
  @FXML public Button checkForUpdate;
  @FXML public ProgressIndicator busyIndicator;
  @FXML public TextField minecraftDirectory;
  @FXML public Button browseMinecraft;
  @FXML public IntegerAdjuster memoryLimit;
  @FXML public TextField updateSite;
  @FXML public Button resetUpdateSite;
  @FXML public TextField javaRuntime;
  @FXML public Button browseJava;
  @FXML public TextField javaOptions;
  @FXML public TextField chunkyOptions;
  @FXML public CheckBox enableDebugConsole;
  @FXML public CheckBox verboseLogging;
  @FXML public CheckBox closeConsoleOnExit;
  @FXML public TextField settingsDirectory;
  @FXML public Button openSettingsDirectory;
  @FXML public CheckBox alwaysShowLauncher;
  @FXML public Button launchButton;
  @FXML public Button cancelButton;
  @FXML public Label launcherVersion;
  @FXML public TitledPane advancedSettings;
  @FXML public Button pluginsButton;
  @FXML public ComboBox<ReleaseChannel> releaseChannelBox;
  @FXML public Button releaseChannelReload;

  public ChunkyLauncherController(LauncherSettings settings) {
    this.settings = settings;
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    busyIndicator.setTooltip(new Tooltip("Checking for Chunky update..."));
    cancelButton.setTooltip(new Tooltip("Close the Chunky launcher."));
    cancelButton.setOnAction(event -> cancelButton.getScene().getWindow().hide());
    pluginsButton.setTooltip(new Tooltip("Customize plugins."));
    pluginsButton.setOnAction(event -> {
      try {
        PluginManager pluginManager = new PluginManager();
        pluginManager.show();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    launchButton.setTooltip(new Tooltip("Launch Chunky using the current settings."));
    launchButton.setOnAction(event -> launchChunky());
    launcherVersion.setText("v" + ChunkyLauncher.LAUNCHER_VERSION);
    advancedSettings.setExpanded(settings.showAdvancedSettings);
    advancedSettings.expandedProperty().addListener(observable -> Platform.runLater(
            () -> advancedSettings.getScene().getWindow().sizeToScene()));

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

    updateSite.setText(settings.updateSite);
    updateSite.setTooltip(new Tooltip("Update site URL for Chunky releases."));
    updateSite.textProperty().addListener((observable, oldValue, newValue) -> {
      settings.updateSite = newValue;
      settings.save();
    });
    resetUpdateSite.setOnAction(event -> {
      updateSite.setText(LauncherSettings.DEFAULT_UPDATE_SITE);
    });
    resetUpdateSite.setTooltip(new Tooltip("Reset to default Chunky update site."));

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

    releaseChannelBox.setCellFactory(new ReleaseChannel.CellFactory());
    releaseChannelBox.setButtonCell(new ReleaseChannel.ReleaseChannelCell());
    releaseChannelBox.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (newValue == null) {
            releaseChannelBox.setTooltip(null);
          } else {
            releaseChannelBox.setTooltip(new Tooltip(newValue.notes));
            settings.selectedChannel = newValue;
            settings.save();
          }
        });
    updateChannelsList();

    releaseChannelReload.setOnAction(event -> {
      if (isBusy()) {
        setBusy(true);
        updateLauncher(
            error -> Platform.runLater(() -> {
              setBusy(false);
              launcherError("Failed to Update", error);
            }),
            info -> Platform.runLater(() -> {
              setBusy(false);
              updateChannelsList();
            })
        );
      }
    });

    openSettingsDirectory.setOnAction(event -> {
      // Running Desktop.open() on the JavaFX application thread seems to
      // lock up the application on Linux, so we create a new thread to run that.
      // This StackOverflow question seems to ask about the same bug:
      // http://stackoverflow.com/questions/23176624/javafx-freeze-on-desktop-openfile-desktop-browseuri
      new Thread(() -> {
        try {
          if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(SettingsDirectory.getSettingsDirectory());
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

    updateLauncher(error -> System.err.printf("Launcher Info Error: %s\n", error), info -> {});
  }

  public void updateLauncher(Consumer<String> errorCallback, Consumer<LauncherInfo> infoCallback) {
    LauncherInfoChecker updateChecker = new LauncherInfoChecker(settings,
        errorCallback,
        info -> {
          if (info != null) {
            if (ChunkyLauncher.LAUNCHER_VERSION.compareTo(info.version) < 0) {
              Platform.runLater(() -> {
                try {
                  LauncherUpdateDialog updateDialog = new LauncherUpdateDialog(settings, info);
                  updateDialog.show();
                } catch (IOException e) {
                  e.printStackTrace(System.err);
                }
              });
            }

            settings.releaseChannels = info.channels;
            int index = settings.releaseChannels.indexOf(settings.selectedChannel);
            if (index == -1) index = 0;
            settings.selectedChannel = settings.releaseChannels.get(index);
            settings.save();
          }
          infoCallback.accept(info);
        });
    updateChecker.start();
  }

  /**
   * Updates the launcher settings when opening the main launcher window.
   *
   * <p>The launcher UI may be created before the first-time setup
   * window is shown, at which point the settings directory has
   * not yet been selected. The launcher settings and installed versions
   * must thus be updated only when the launcher UI is shown.
   */
  protected void onShowing(WindowEvent event) {
    File settingsDir = SettingsDirectory.getSettingsDirectory();
    if (settingsDir != null) {
      settingsDirectory.setText(settingsDir.getAbsolutePath());
    }

    updateVersionList();
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

  private void updateChannelsList() {
    releaseChannelBox.getItems().clear();
    releaseChannelBox.getItems().addAll(settings.releaseChannels);
    releaseChannelBox.getSelectionModel().select(settings.selectedChannel);
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
    releaseChannelReload.setDisable(busy);
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
