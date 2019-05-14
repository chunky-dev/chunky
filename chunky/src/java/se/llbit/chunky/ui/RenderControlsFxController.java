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
package se.llbit.chunky.ui;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.OutputMode;
import se.llbit.chunky.renderer.RenderController;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.RenderStatusListener;
import se.llbit.chunky.renderer.Renderer;
import se.llbit.chunky.renderer.ResetReason;
import se.llbit.chunky.renderer.SnapshotControl;
import se.llbit.chunky.renderer.scene.AsynchronousSceneManager;
import se.llbit.chunky.renderer.scene.RenderResetHandler;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.render.AdvancedTab;
import se.llbit.chunky.ui.render.CameraTab;
import se.llbit.chunky.ui.render.EntitiesTab;
import se.llbit.chunky.ui.render.GeneralTab;
import se.llbit.chunky.ui.render.HelpTab;
import se.llbit.chunky.ui.render.LightingTab;
import se.llbit.chunky.ui.render.MaterialsTab;
import se.llbit.chunky.ui.render.PostprocessingTab;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.ui.render.SkyTab;
import se.llbit.chunky.ui.render.WaterTab;
import se.llbit.chunky.world.Icon;
import se.llbit.log.Log;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Controller for the Render Controls dialog.
 */
public class RenderControlsFxController implements Initializable, RenderResetHandler {
  private AsynchronousSceneManager asyncSceneManager;

  public ChunkyFxController getChunkyController() {
    return controller;
  }

  public RenderController getRenderController() {
    return controller.getChunky().getRenderController();
  }

  static class GUIRenderListener implements RenderStatusListener {
    private final RenderControlsFxController gui;
    private int spp;
    private int sps;

    public GUIRenderListener(RenderControlsFxController renderControls) {
      this.gui = renderControls;
    }

    @Override public void setRenderTime(long time) {
      Platform.runLater(() -> {
        int seconds = (int) ((time / 1000) % 60);
        int minutes = (int) ((time / 60000) % 60);
        int hours = (int) (time / 3600000);
        gui.renderTimeLbl.setText(String
            .format("Render time: %d hours, %d minutes, %d seconds", hours, minutes, seconds));
      });
    }

    @Override public void setSamplesPerSecond(int sps) {
      this.sps = sps;
      updateSppStats();
    }

    @Override public void setSpp(int spp) {
      this.spp = spp;
      updateSppStats();
    }

    private void updateSppStats() {
      Platform.runLater(() -> gui.sppLbl.setText(String
          .format("%s SPP, %s SPS", gui.decimalFormat.format(spp),
              gui.decimalFormat.format(sps))));
    }

    @Override public void renderStateChanged(RenderMode state) {
      Platform.runLater(() -> {
        switch (state) {
          case RENDERING:
            gui.start.setSelected(true);
            break;
          case PAUSED:
            gui.pause.setSelected(true);
            break;
          case PREVIEW:
            gui.reset.setSelected(true);
            break;
        }
      });
    }
  }

  public final DecimalFormat decimalFormat = new DecimalFormat();

  /**
   * The number of milliseconds spent on rendering a scene until
   * the reset confirmation must be shown when trying to edit
   * the scene state.
   */
  private static final long SCENE_EDIT_GRACE_PERIOD = 30000;

  private Scene scene;

  private File saveFrameDirectory = new File(System.getProperty("user.dir"));

  private Stage stage;
  private RenderCanvasFx canvas;
  private Renderer renderer;

  /** Used to ensure only one render reset confirm dialog is displayed at a time. */
  protected AtomicBoolean resetConfirmMutex = new AtomicBoolean(false);

  private final ProgressListener progressListener = new ProgressListener() {
    @Override public void setProgress(String task, int done, int start, int target) {
      Platform.runLater(() -> {
        progressBar.setProgress((double) done / (target - start));
        progressLbl.setText(String.format("%s: %s of %s", task, decimalFormat.format(done),
            decimalFormat.format(target)));
        etaLbl.setText("ETA: N/A");
      });
    }

    @Override public void setProgress(String task, int done, int start, int target, String eta) {
      Platform.runLater(() -> {
        progressBar.setProgress((double) done / (target - start));
        progressLbl.setText(String.format("%s: %s of %s", task, decimalFormat.format(done),
            decimalFormat.format(target)));
        etaLbl.setText("ETA: " + eta);
      });
    }
  };

  private RenderStatusListener renderTracker = RenderStatusListener.NONE;

  private TaskTracker taskTracker = new TaskTracker(ProgressListener.NONE);

  private Collection<RenderControlsTab> tabs = new ArrayList<>();

  /** Maps JavaFX tabs to tab controllers. */
  private Map<Tab, RenderControlsTab> tabControllers = Collections.emptyMap();

  private final Tooltip tooltip;

  @FXML private TextField sceneNameField;

  @FXML private Button saveBtn;

  @FXML private ToggleButton start;
  @FXML private ToggleButton pause;
  @FXML private ToggleButton reset;

  @FXML private Button saveFrameBtn;

  @FXML private Button togglePreviewBtn;

  @FXML private ProgressBar progressBar;

  @FXML private Label progressLbl;

  @FXML private Label etaLbl;

  @FXML private Label renderTimeLbl;

  @FXML private Label sppLbl;

  @FXML private IntegerAdjuster targetSpp;

  @FXML private Button saveDefaultSpp;

  @FXML private TabPane tabPane;

  private ChunkyFxController controller;

  public RenderControlsFxController() {
    decimalFormat.setGroupingSize(3);
    decimalFormat.setGroupingUsed(true);
    tooltip = new Tooltip();
    tooltip.setConsumeAutoHidingEvents(false);
    tooltip.setAutoHide(true);
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    saveBtn.setTooltip(new Tooltip("Save the current scene."));
    saveBtn.setGraphic(new ImageView(Icon.disk.fxImage()));
    saveBtn.setOnAction(e -> asyncSceneManager.saveScene());
    saveFrameBtn.setOnAction(this::saveCurrentFrame);
    togglePreviewBtn.setOnAction(e -> {
      if (canvas == null || !canvas.isShowing()) {
        openPreview();
      } else {
        canvas.hide();
      }
    });
    start.setGraphic(new ImageView(Icon.play.fxImage()));
    start.setTooltip(new Tooltip("Start rendering."));
    start.setOnAction(e -> scene.startRender());
    pause.setGraphic(new ImageView(Icon.pause.fxImage()));
    pause.setTooltip(new Tooltip("Pause the render."));
    pause.setOnAction(e -> scene.pauseRender());
    reset.setGraphic(new ImageView(Icon.stop.fxImage()));
    reset.setTooltip(new Tooltip("Resets the current render. Discards render progress."));
    reset.setOnAction(e -> scene.haltRender());
    sppLbl.setTooltip(new Tooltip("SPP = Samples Per Pixel, SPS = Samples Per Second"));
    targetSpp.setName("Target SPP");
    targetSpp.setTooltip("Rendering is stopped after reaching the target Samples Per Pixel (SPP).");
    targetSpp.setRange(100, 100000);
    targetSpp.makeLogarithmic();
    saveDefaultSpp.setTooltip(new Tooltip("Make the current SPP target the default."));
    saveDefaultSpp.setOnAction(e ->
        PersistentSettings.setSppTargetDefault(scene.getTargetSpp()));
  }

  private void saveCurrentFrame(Event event) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Current Frame");
    if (saveFrameDirectory != null && saveFrameDirectory.isDirectory()) {
      fileChooser.setInitialDirectory(saveFrameDirectory);
    }
    OutputMode outputMode = scene.getOutputMode();
    String extension = ".png";
    switch (outputMode) {
      case PNG:
        fileChooser.setSelectedExtensionFilter(
            new FileChooser.ExtensionFilter("PNG files", "*.png"));
        break;
      case TIFF_32:
        extension = ".tiff";
        fileChooser.setSelectedExtensionFilter(
            new FileChooser.ExtensionFilter("PNG files", "*.png"));
        break;
    }
    fileChooser.setInitialFileName(String.format("%s-%d%s",
        scene.name(), renderer.getRenderStatus().getSpp(), extension));
    File target = fileChooser.showSaveDialog(stage);
    if (target != null) {
      saveFrameDirectory = target.getParentFile();
      try {
        if (!target.getName().endsWith(extension)) {
          target = new File(target.getPath() + extension);
        }
        scene.saveFrame(target, taskTracker);
      } catch (IOException e1) {
        Log.error("Failed to save current frame", e1);
      }
    }
  }

  public void setStage(Stage stage) {
    this.stage = stage;
    stage.setOnHiding(e -> {
      controller.cameraViewUpdated(); // Clear the camera view visualization.
      scene.setRenderMode(RenderMode.PAUSED);
      scene.forceReset();
      if (canvas != null && canvas.isShowing()) {
        canvas.close();
      }
    });
    stage.setOnShown(e -> {
      openPreview();
      controller.cameraViewUpdated(); // Trigger redraw of camera view visualization.
    });
  }

  private void buildTabs() {
    try {
      // Create the default tabs:
      tabs.add(new GeneralTab());
      tabs.add(new LightingTab());
      tabs.add(new SkyTab());
      tabs.add(new WaterTab());
      tabs.add(new CameraTab());
      tabs.add(new EntitiesTab());
      tabs.add(new MaterialsTab());
      tabs.add(new PostprocessingTab());
      tabs.add(new AdvancedTab());
      tabs.add(new HelpTab());

      // Transform tabs (allows plugin hooks to modify the tabs):
      tabs = controller.getChunky().getRenderControlsTabTransformer().apply(tabs);

      if (tabs.contains(null)) {
        Log.error("Null tabs inserted in tab collection (possible plugin error).");
        tabs = tabs.stream().filter(tab -> tab != null).collect(Collectors.toList());
      }

      Collection<Tab> javaFxTabs = new ArrayList<>();
      tabControllers = new HashMap<>();
      for (RenderControlsTab tab : tabs) {
        Tab javaFxTab = tab.getTab();
        tabControllers.put(javaFxTab, tab);
        javaFxTabs.add(javaFxTab);
      }
      tabPane.getTabs().addAll(javaFxTabs);
    } catch (IOException e) {
      Log.error("Failed to build render controls tabs.", e);
    }

    tabPane.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> updateTab(newValue));
  }

  private void refreshSettings() {
    targetSpp.set(scene.getTargetSpp());
    updateTab(tabPane.getSelectionModel().getSelectedItem());
  }

  private void updateTab(Tab tab) {
    RenderControlsTab controller = tabControllers.get(tab);
    if (controller != null) {
      tabControllers.get(tab).update(scene);
    } else {
      Log.error("Missing tab controller!");
    }
  }

  public void openPreview() {
    if (canvas == null) {
      canvas = new RenderCanvasFx(scene, renderer);
      EventHandler<WindowEvent> onHiding = canvas.getOnHiding();
      EventHandler<WindowEvent> onShowing = canvas.getOnShowing();
      canvas.setOnHiding(e -> {
        togglePreviewBtn.setText("Show preview window");
        onHiding.handle(e);
      });
      canvas.setOnShowing(e -> {
        togglePreviewBtn.setText("Hide preview window");
        onShowing.handle(e);
      });
      canvas.initOwner(stage);
      canvas.show();
      canvas.setRenderListener(renderTracker);
    } else {
      canvas.show();
      canvas.toFront();
    }
    int x = (int) (stage.getX() + stage.getWidth());
    int y = (int) stage.getY();
    canvas.setX(x);
    canvas.setY(y);
    canvas.repaint();
  }

  public RenderCanvasFx getCanvas() {
    return canvas;
  }

  public void setController(ChunkyFxController controller) {
    this.controller = controller;
    RenderController renderController = controller.getChunky().getRenderController();
    this.renderTracker = new GUIRenderListener(this);
    this.taskTracker = new TaskTracker(progressListener);
    buildTabs();
    renderer = renderController.getRenderer();
    scene = renderController.getSceneManager().getScene();
    sceneNameField.setText(scene.name);
    sceneNameField.setTextFormatter(new TextFormatter<TextFormatter.Change>(change -> {
      if (change.isReplaced()) {
        if (change.getText().isEmpty()) {
          // Disallow clearing the scene name.
          change.setText(change.getControlText().substring(change.getRangeStart(),
              change.getRangeEnd()));
        }
      }
      if (change.isAdded()) {
        if (!AsynchronousSceneManager.sceneNameIsValid(change.getText())) {
          // Stop a change adding illegal characters to the scene name.
          change.setText("");
        }
      }
      return change;
    }));
    sceneNameField.textProperty().addListener((observable, oldValue, newValue) -> {
      scene.setName(newValue);
      renderController.getSceneProvider().withSceneProtected(scene1 -> scene1.setName(newValue));
      updateTitle();
    });
    sceneNameField.setOnAction(event -> asyncSceneManager.saveScene());
    targetSpp.set(scene.getTargetSpp());
    targetSpp.onValueChange(value -> scene.setTargetSpp(value));

    // TODO: remove the cast.
    asyncSceneManager =
        (AsynchronousSceneManager) renderController.getSceneManager();
    asyncSceneManager.setResetHandler(this);
    asyncSceneManager.setTaskTracker(taskTracker);
    asyncSceneManager.setOnSceneLoaded(() -> {
      CountDownLatch guiUpdateLatch = new CountDownLatch(1);
      Platform.runLater(() -> {
        synchronized (scene) {
          sceneNameField.setText(scene.name());
          canvas.setCanvasSize(scene.width, scene.height);
        }
        updateTitle();
        refreshSettings();
        guiUpdateLatch.countDown();
      });
      new Thread(() -> {
        try {
          guiUpdateLatch.await();
          canvas.forceRepaint();
        } catch (InterruptedException ignored) {
          // Ignored.
        }
      }).start();
    });
    asyncSceneManager.setOnChunksLoaded(() -> Platform.runLater(() -> {
      openPreview();
      tabs.forEach(RenderControlsTab::onChunksLoaded);
    }));
    asyncSceneManager.setTaskTracker(taskTracker);
    renderer.setSnapshotControl(SnapshotControl.DEFAULT);
    renderer.setOnFrameCompleted((scene1, spp) -> {
      if (SnapshotControl.DEFAULT.saveSnapshot(scene1, spp)) {
        // Save the current frame.
        scene1.saveSnapshot(renderController.getContext().getSceneDirectory(), taskTracker);
      }

      if (SnapshotControl.DEFAULT.saveRenderDump(scene1, spp)) {
        // Save the scene description and current render dump.
        asyncSceneManager.saveScene();
      }
    });
    renderer.addRenderListener(renderTracker);
    renderer.setRenderTask(taskTracker.backgroundTask());
    tabs.forEach(tab -> tab.setController(this));
    updateTab(tabPane.getSelectionModel().getSelectedItem());
  }

  private void updateTitle() {
    stage.setTitle("Render Controls - " + scene.name());
  }

  @Override  public boolean allowSceneRefresh() {
    if (scene.getResetReason() == ResetReason.SCENE_LOADED
        || renderer.getRenderStatus().getRenderTime() < SCENE_EDIT_GRACE_PERIOD) {
      return true;
    } else {
      requestRenderReset();
    }
    return false;
  }

  private void requestRenderReset() {
    if (resetConfirmMutex.compareAndSet(false, true)) {
      Platform.runLater(() -> {
        try {
          ConfirmResetPopup popup = new ConfirmResetPopup(
              () -> {
                // On accept.
                asyncSceneManager.applySceneChanges();
                resetConfirmMutex.set(false);
              },
              () -> {
                // On reject.
                asyncSceneManager.discardSceneChanges();
                refreshSettings();
                resetConfirmMutex.set(false);
              });
          popup.show(stage);
        } catch (IOException e) {
          Log.warn("Could not open reset confirmation dialog.", e);
        }
      });
    }
  }

  /**
   * Shows a simple popup with a tooltip type message.
   * The popup is displayed directly below the given scene node.
   *
   * @param node the scene node to display the popup below
   */
  public void showPopup(String message, Region node) {
    if (node.getScene() != null && node.getScene().getWindow() != null) {
      Point2D offset = node.localToScene(0, 0);
      tooltip.setText(message);
      tooltip.show(node,
          offset.getX() + node.getScene().getX() + node.getScene().getWindow().getX(),
          offset.getY() + node.getScene().getY() + node.getScene().getWindow().getY()
              + node.getHeight());
    }
  }
}
