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
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import se.llbit.chunky.launcher.ChunkyLauncher;
import se.llbit.chunky.launcher.DownloadStatus;
import se.llbit.chunky.launcher.VersionInfo;
import se.llbit.chunky.resources.SettingsDirectory;
import se.llbit.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public final class UpdateDialogController implements Initializable {
  private final VersionInfo versionInfo;
  private final File libDir;
  private final File versionsDir;
  private final ExecutorService threadPool;
  private final ChunkyLauncherController launcher;

  @FXML protected Text releaseInfo;
  @FXML protected TextArea releaseNotes;
  @FXML protected Button updateButton;
  @FXML protected Button cancelButton;
  @FXML protected TitledPane detailsPane;
  @FXML protected TableView<Pair<VersionInfo.Library, VersionInfo.LibraryStatus>> dependencies;
  @FXML protected TableColumn<Pair<VersionInfo.Library, VersionInfo.LibraryStatus>, String> libraryCol;
  @FXML protected TableColumn<Pair<VersionInfo.Library, VersionInfo.LibraryStatus>, VersionInfo.LibraryStatus> statusCol;
  @FXML protected TableColumn<Pair<VersionInfo.Library, VersionInfo.LibraryStatus>, String> sizeCol;
  @FXML protected ProgressIndicator busyIndicator;
  @FXML protected ProgressBar progress;
  @FXML protected Label updateComplete;

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public UpdateDialogController(ChunkyLauncherController launcher, VersionInfo versionInfo) {
    this.launcher = launcher;
    this.versionInfo = versionInfo;
    File chunkyDir = SettingsDirectory.getSettingsDirectory();
    libDir = new File(chunkyDir, "lib");
    if (!libDir.isDirectory()) {
      libDir.mkdirs();
    }
    versionsDir = new File(chunkyDir, "versions");
    if (!versionsDir.isDirectory()) {
      versionsDir.mkdirs();
    }
    threadPool = Executors.newFixedThreadPool(4, runnable -> {
      Thread thread = Executors.defaultThreadFactory().newThread(runnable);
      thread.setDaemon(true);
      return thread;
    });
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    releaseInfo.setText(String.format("Version %s released on %s",
        versionInfo.name, versionInfo.date()));
    if (versionInfo.notes.isEmpty()) {
      releaseNotes.setText("No release notes available.");
    } else {
      releaseNotes.setText(versionInfo.notes);
    }
    updateButton.setOnAction(event -> downloadUpdate());
    cancelButton.setOnAction(event -> cancelButton.getScene().getWindow().hide());
    detailsPane.expandedProperty().addListener(observable -> Platform.runLater(
            () -> detailsPane.getScene().getWindow().sizeToScene()));
    dependencies.getItems().addAll(versionInfo.libraries.stream()
        .map(lib -> new Pair<>(lib, lib.testIntegrity(libDir))).collect(Collectors.toList()));
    libraryCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().thing1.name));
    statusCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().thing2));
    sizeCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(
        ChunkyLauncher.prettyPrintSize(data.getValue().thing1.size)));
    statusCol.setCellFactory(param -> new TableCell<Pair<VersionInfo.Library, VersionInfo.LibraryStatus>, VersionInfo.LibraryStatus>() {
      @Override protected void updateItem(VersionInfo.LibraryStatus status, boolean empty) {
        if (status != null) {
          InputStream imageStream = null;
          switch (status) {
            case PASSED:
            case DOWNLOADED_OK:
              imageStream = getClass().getResourceAsStream("cached.png");
              break;
            case CHECKSUM_MISMATCH:
            case MISSING:
              imageStream = getClass().getResourceAsStream("refresh.png");
              break;
            case INCOMPLETE_INFO:
            case MALFORMED_URL:
            case FILE_NOT_FOUND:
            case DOWNLOAD_FAILED:
              imageStream = getClass().getResourceAsStream("failed.png");
              break;
          }
          if (imageStream != null) {
            ImageView image = new ImageView(new Image(imageStream));
            setGraphic(image);
          }
          setText(status.downloadStatus());
        }
      }
    });
  }

  private void downloadUpdate() {
    updateButton.setDisable(true);
    busyIndicator.setVisible(true);
    progress.setProgress(0);
    progress.setStyle("");  // Clear the progressbar style in case a previous download failed.
    if (!libDir.isDirectory()) {
      updateFailed(String.format("Library directory (%s) does not exist.",
          libDir.getAbsolutePath()));
      return;
    }
    if (!versionsDir.isDirectory()) {
      updateFailed(String.format("Versions directory (%s) does not exist.",
          libDir.getAbsolutePath()));
      return;
    }
    final List<Future<DownloadStatus>> results = new LinkedList<>();
    List<Pair<VersionInfo.Library, VersionInfo.LibraryStatus>> toDownload =
        dependencies.getItems().stream().filter(
            item -> item.thing2 != VersionInfo.LibraryStatus.PASSED
                && item.thing2 != VersionInfo.LibraryStatus.INCOMPLETE_INFO)
            .collect(Collectors.toList());
    double totalBytes = 0;
    for (Pair<VersionInfo.Library, VersionInfo.LibraryStatus> download : toDownload) {
      totalBytes += download.thing1.size;
    }
    final double finalTotalBytes = totalBytes;
    toDownload.stream().forEach(item ->
        results.add(threadPool.submit(new DownloadJob(item.thing1,
            () -> progress.setProgress(
                progress.getProgress() + item.thing1.size / finalTotalBytes)))));
    new Downloader(this, versionInfo, results).start();
  }

  /** Waits for downloads to complete. */
  static class Downloader extends Thread {
    private final Collection<Future<DownloadStatus>> results;
    private final UpdateDialogController controller;
    private final VersionInfo version;

    public Downloader(UpdateDialogController controller, VersionInfo version,
        Collection<Future<DownloadStatus>> resultFutures) {
      this.controller = controller;
      this.version = version;
      results = resultFutures;
    }

    @Override public void run() {
      boolean failed = false;
      for (Future<DownloadStatus> result : results) {
        try {
          if (result.get() != DownloadStatus.SUCCESS) {
            failed = true;
          }
        } catch (InterruptedException | ExecutionException e) {
          failed = true;
        }
      }
      if (failed) {
        controller.updateFailed("Failed to download some required libraries. Please try again later.");
        return;
      }
      try {
        File versionFile = new File(controller.versionsDir, version.name + ".json");
        version.writeTo(versionFile);
        controller.downloadSucceeded(version);
      } catch (IOException e) {
        controller.updateFailed("Failed to update version info. Please try again later.");
      }
    }

  }

  /**
   * Shows a tooltip with the given error message and sets the progress bar color
   * to red and sets max progress.
   */
  private void updateFailed(final String message) {
    Platform.runLater(() -> {
      progress.setProgress(1.0);
      progress.setStyle("-fx-accent: red;");
      Tooltip tooltip = new Tooltip(message);
      Point2D screen = progress.localToScreen(0, 0);
      tooltip.setAutoHide(true);
      tooltip.show(progress, screen.getX(), screen.getY() + progress.getHeight());
      updateButton.setDisable(false);
      busyIndicator.setVisible(false);
      System.err.println(message);
    });
  }

  /**
   * Changes the title of the "Cancel" button to "Close".
   * Changes the update button into a launch button.
   * Also shows the "Download completed!" label, and sets the
   * progress bar to max progress and sets the progress color to green.
   */
  private void downloadSucceeded(VersionInfo version) {
    Platform.runLater(() -> {
      progress.setProgress(1.0);
      progress.setStyle("-fx-accent: green;");
      updateComplete.setVisible(true);
      cancelButton.setText("Close");
      updateButton.setDisable(false);
      updateButton.setText("Launch Chunky");
      updateButton.setOnAction(event -> {
        updateButton.getScene().getWindow().hide();
        launcher.launchChunky();
      });
      launcher.updateVersionList();
      launcher.version.getSelectionModel().select(version);
      busyIndicator.setVisible(false);
    });
  }

  class DownloadJob implements Callable<DownloadStatus> {

    private final VersionInfo.Library lib;
    private final Runnable callback;

    public DownloadJob(VersionInfo.Library lib, Runnable callback) {
      this.lib = lib;
      this.callback = callback;
    }

    @Override public DownloadStatus call() {
      DownloadStatus result = null;
      // First try to download using the URL specified for the library.
      if (!lib.url.isEmpty()) {
        result = ChunkyLauncher.tryDownload(libDir, lib, lib.url);
        switch (result) {
          case MALFORMED_URL:
            System.err.println("Malformed URL: " + lib.url);
            break;
          case FILE_NOT_FOUND:
            System.err.println("File not found: " + lib.url);
            break;
          case DOWNLOAD_FAILED:
            System.err.println("Download failed: " + lib.url);
            break;
          default:
            break;
        }
      }
      // Using the library URL failed.
      // Try downloading the library from the default update site.
      String defaultUrl = launcher.settings.getResourceUrl("lib/" + lib.name);
      if (result != DownloadStatus.SUCCESS) {
        result = ChunkyLauncher.tryDownload(libDir, lib, defaultUrl);
      }
      switch (result) {
        case SUCCESS:
          updateDependencyStatus(lib, VersionInfo.LibraryStatus.DOWNLOADED_OK);
          break;
        case MALFORMED_URL:
          updateDependencyStatus(lib, VersionInfo.LibraryStatus.MALFORMED_URL);
          System.err.println("Malformed URL: " + defaultUrl);
          break;
        case FILE_NOT_FOUND:
          updateDependencyStatus(lib, VersionInfo.LibraryStatus.FILE_NOT_FOUND);
          System.err.println("File not found: " + defaultUrl);
          break;
        case DOWNLOAD_FAILED:
          updateDependencyStatus(lib, VersionInfo.LibraryStatus.DOWNLOAD_FAILED);
          System.err.println("Download failed: " + defaultUrl);
          break;
      }
      callback.run();
      return result;
    }
  }

  /** Update status of a single library dependency in the table view. */
  private void updateDependencyStatus(VersionInfo.Library lib, VersionInfo.LibraryStatus newStatus) {
    dependencies.getItems().setAll(dependencies.getItems().stream().map(
        item -> {
          if (item.thing1 != lib) {
            return item;
          } else {
            return new Pair<>(item.thing1, newStatus);
          }
        }).collect(Collectors.toList()));
  }
}
