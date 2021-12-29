/* Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import se.llbit.chunky.launcher.LauncherSettings;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * JavaFX window for the Chunky launcher.
 */
public class ChunkyLauncherFx extends Application {
  private static LauncherSettings settings;
  private static AtomicBoolean first = new AtomicBoolean(true);
  private static CountDownLatch latch = new CountDownLatch(1);
  private static Consumer<Stage> callback;
  private static Stage stage;
  private static HostServices hostServices = null;

  @Override public void start(Stage stage) throws Exception {
    hostServices = getHostServices();

    FXMLLoader loader = new FXMLLoader(getClass().getResource("ChunkyLauncher.fxml"));
    ChunkyLauncherController controller = new ChunkyLauncherController(settings);
    loader.setController(controller);
    Parent root = loader.load();
    stage.getIcons().add(new Image(getClass().getResourceAsStream("chunky-cfg.png")));
    stage.setTitle("Chunky Launcher");
    stage.setScene(new Scene(root));
    stage.setOnShowing(controller::onShowing);
    ChunkyLauncherFx.stage = stage;
    latch.countDown();
    callback.accept(stage);
  }

  public static void launchWebpage(String url) {
    if (hostServices == null) {
      Platform.runLater(() -> launchWebpage(url));
    } else {
      hostServices.showDocument(url);
    }
  }

  /**
   * Starts the JavaFX application thread and calls the callback with the stage for
   * the Chunky Launcher.
   *
   * <p>This design is needlessly convoluted to work around the fact that JavaFX only
   * allows a single active Application, and no GUI elements can be created without
   * first launching that application. We have to use this callback method since we
   * want to be able to show both the first-time-setup window and the debug console
   * (when --console was specified) without showing the Chunky Launcher window.
   * When the callback is called we can be sure that the callback is called on the
   * application thread and that the application has been started, which makes it
   * possible to open other windows. If the main Chunky Launcher window should be
   * displayed then the callback argument can be used by calling stage.show().
   */
  public static void withLauncher(LauncherSettings settings, Consumer<Stage> callback) {
    if (first.compareAndSet(true, false)) {
      new Thread(() -> {
        ChunkyLauncherFx.settings = settings;
        ChunkyLauncherFx.callback = callback;
        launch();
      }).start();
    } else {
      try {
        latch.await();
        Platform.runLater(() -> callback.accept(stage));
      } catch (InterruptedException ignored) {
        // Ignored.
      }
    }
  }
}
