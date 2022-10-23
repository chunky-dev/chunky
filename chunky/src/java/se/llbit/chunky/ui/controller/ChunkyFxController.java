/* Copyright (c) 2016-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016-2021 Chunky contributors
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
package se.llbit.chunky.ui.controller;

import java.awt.Desktop;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.main.ZipExportJob;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.renderer.*;
import se.llbit.chunky.renderer.export.PictureExportFormats;
import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.renderer.export.PictureExportFormat;
import se.llbit.chunky.ui.ChunkMap;
import se.llbit.chunky.ui.dialogs.*;
import se.llbit.chunky.ui.DoubleTextField;
import se.llbit.chunky.ui.IntegerAdjuster;
import se.llbit.chunky.ui.PositiveIntegerAdjuster;
import se.llbit.chunky.ui.ProgressTracker;
import se.llbit.chunky.ui.RenderCanvasFx;
import se.llbit.chunky.ui.UILogReceiver;
import se.llbit.chunky.ui.dialogs.WorldChooser;
import se.llbit.chunky.renderer.scene.*;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkSelectionTracker;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.DeleteChunksJob;
import se.llbit.chunky.world.EmptyWorld;
import se.llbit.chunky.world.Icon;
import se.llbit.chunky.world.World;
import se.llbit.fx.ToolPane;
import se.llbit.fxutil.Dialogs;
import se.llbit.fxutil.GroupedChangeListener;
import se.llbit.log.Level;
import se.llbit.log.Log;
import se.llbit.math.Vector3;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;
import se.llbit.util.annotation.Nullable;

/**
 * Controller for the main Chunky window.
 */
public class ChunkyFxController
    implements Initializable, CameraViewListener, RenderResetHandler {

  private final Stage stage;
  private final Chunky chunky;
  private WorldMapLoader mapLoader;
  private ChunkMap map;
  private MapView mapView;
  protected ChunkSelectionTracker chunkSelection = new ChunkSelectionTracker();

  @Nullable private SceneChooser sceneChooser;

  @FXML private Canvas mapCanvas;
  @FXML private Canvas mapOverlay;
  @FXML private Label mapName;
  @FXML private MenuItem menuExit;
  @FXML private ToolPane renderControls;
  @FXML private Button changeWorldBtn;
  @FXML private Button reloadWorldBtn;
  @FXML private ToggleButton overworldBtn;
  @FXML private ToggleButton netherBtn;
  @FXML private ToggleButton endBtn;
  @FXML private PositiveIntegerAdjuster scale;
  @FXML private CheckBox showPlayers;
  @FXML private IntegerAdjuster yMin;
  @FXML private IntegerAdjuster yMax;
  @FXML private ToggleButton trackPlayerBtn;
  @FXML private ToggleButton trackCameraBtn;
  @FXML private DoubleTextField xPosition;
  @FXML private DoubleTextField zPosition;
  @FXML private StackPane mapPane;
  @FXML private SplitPane splitPane;
  @FXML private TabPane mainTabs;
  @FXML private Tab worldMapTab;
  @FXML private Tab previewTab;

  @FXML private MenuItem saveScene;
  @FXML private MenuItem saveSceneAs;
  @FXML private MenuItem saveSceneCopy;
  @FXML private MenuItem loadScene;
  @FXML private MenuItem loadSceneFile;
  @FXML private MenuItem creditsMenuItem;

  @FXML private ProgressBar progressBar;
  @FXML private Label progressLbl;
  @FXML private Label etaLbl;
  @FXML private Label renderTimeLbl;
  @FXML private Label sppLbl;
  @FXML private PositiveIntegerAdjuster targetSpp;
  @FXML private Button saveDefaultSpp;

  @FXML private ToggleButton start;
  @FXML private ToggleButton pause;
  @FXML private ToggleButton reset;

  RenderControlsFxController sceneControls;

  private File saveFrameDirectory = new File(System.getProperty("user.dir"));

  public final DecimalFormat decimalFormat = new DecimalFormat();
  {
    decimalFormat.setGroupingSize(3);
    decimalFormat.setGroupingUsed(true);
  }

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
  private final TaskTracker taskTracker = new TaskTracker(progressListener);

  public RenderController getRenderController() {
    return renderController;
  }

  public void showWorldMap() {
    mainTabs.getSelectionModel().select(worldMapTab);
  }

  public void showRenderPreview() {
    mainTabs.getSelectionModel().select(previewTab);
  }

  static class GUIRenderListener implements RenderStatusListener {
    private final ChunkyFxController gui;
    private int spp;
    private LinkedList<Integer> sps_queue = new LinkedList<Integer>();
    private static final int AVERAGE_PERIOD = 150;
    private int sps_average;

    public GUIRenderListener(ChunkyFxController renderControls) {
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
      // Rolling average of SPS
      int avg;
      if (sps_queue.size() >= AVERAGE_PERIOD) {
        avg = sps_average;
        // Probably not needed, but just in case sps_queue is too long
        for (int i = sps_queue.size(); i > AVERAGE_PERIOD; i--) {
          sps_queue.removeLast();
        }
        avg -= sps_queue.removeLast() / AVERAGE_PERIOD;
        sps_queue.addFirst(sps);
        avg += sps / AVERAGE_PERIOD;
      } else {
        avg = sps_average * sps_queue.size();
        avg += sps;
        sps_queue.addFirst(sps);
        avg /= sps_queue.size();
      }
      sps_average = avg;
      updateSppStats();
    }

    @Override public void setSpp(int spp) {
      this.spp = spp;
      updateSppStats();
    }

    private void updateSppStats() {
      Platform.runLater(() -> gui.sppLbl.setText(String
          .format("%s SPP, %s SPS", gui.decimalFormat.format(spp),
              gui.decimalFormat.format(sps_average))));
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

  /**
   * The number of milliseconds spent on rendering a scene until
   * the reset confirmation must be shown when trying to edit
   * the scene state.
   */
  private static final long SCENE_EDIT_GRACE_PERIOD = 30000;

  /** Used to ensure only one render reset confirm dialog is displayed at a time. */
  protected AtomicBoolean resetConfirmMutex = new AtomicBoolean(false);

  private BooleanProperty trackPlayer =
      new SimpleBooleanProperty(PersistentSettings.getFollowPlayer());
  private BooleanProperty trackCamera =
      new SimpleBooleanProperty(PersistentSettings.getFollowCamera());

  /** The main Chunky JavaFx window. */
  private Path prevPngDir = null;
  private RenderCanvasFx canvas;
  private AsynchronousSceneManager asyncSceneManager;
  private final RenderStatusListener renderTracker;
  private se.llbit.chunky.renderer.scene.Scene scene = null;
  private RenderManager renderManager;
  private RenderController renderController;

  public ChunkyFxController(Stage stage, Chunky chunky) {
    this.stage = stage;
    this.chunky = chunky;
    mapView = new MapView();
    renderTracker = new GUIRenderListener(this);
  }

  public void promptDeleteSelectedChunks() {
    Dialog<ButtonType> confirmationDialog = Dialogs.createSpecialApprovalConfirmation(
        "Delete selected chunks",
        "Confirm deleting the selected chunks",
        "Do you really want to delete the selected chunks from the world?\nThis will remove the selected chunks from your disk and cannot be undone. Be sure to have a backup!",
        "I do want to permanently delete the selected chunks"
    );
    if (confirmationDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
      deleteSelectedChunks(ProgressTracker.NONE);
    }
  }

  /**
   * Delete the currently selected chunks from the current world.
   */
  public void deleteSelectedChunks(ProgressTracker progress) {
    Collection<ChunkPosition> selected = chunkSelection.getSelection();
    if (!selected.isEmpty() && !progress.isBusy()) {
      DeleteChunksJob job = new DeleteChunksJob(mapLoader.getWorld(), selected, progress);
      job.start();
    }
  }

  /**
   * Export the selected chunks to a zip file.
   */
  public synchronized void exportZip(File targetFile, ProgressTracker progress) {
    new ZipExportJob(mapLoader.getWorld(), chunkSelection.getSelection(), targetFile, progress).start();
  }

  public void exportZip() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Export selected chunks to Zip file");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Zip archive", "*.zip"));
    mapLoader.withWorld(world -> fileChooser.setInitialFileName(world.levelName() + ".zip"));
    File target = fileChooser.showSaveDialog(stage.getScene().getWindow());
    if (target != null) {
      exportZip(target, ProgressTracker.NONE);
    }
  }

  public void exportMapView() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Export PNG");
    fileChooser
        .getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG image", "*.png"));
    mapLoader.withWorld(world -> fileChooser.setInitialFileName(world.levelName() + ".png"));
    if (prevPngDir != null) {
      fileChooser.setInitialDirectory(prevPngDir.toFile());
    }
    File target = fileChooser.showSaveDialog(stage.getScene().getWindow());
    if (target != null) {
      Path path = target.toPath();
      if (!target.getName().endsWith(".png")) {
        path = path.resolveSibling(target.getName() + ".png");
      }
      prevPngDir = path.getParent();
      map.renderView(path.toFile(), ProgressTracker.NONE);
    }
  }

  @Override public void initialize(URL fxmlUrl, ResourceBundle resources) {
    scene = chunky.getSceneManager().getScene();
    renderController = chunky.getRenderController();
    renderManager = renderController.getRenderManager();
    asyncSceneManager =
        (AsynchronousSceneManager) renderController.getSceneManager();
    asyncSceneManager.setResetHandler(this);
    asyncSceneManager.setTaskTracker(taskTracker);
    this.updateTitle();
    asyncSceneManager.setOnSceneLoaded(() -> {
      CountDownLatch guiUpdateLatch = new CountDownLatch(1);
      Platform.runLater(() -> {
        synchronized (scene) {
          canvas.setCanvasSize(scene.width, scene.height);
        }
        updateTitle();
        refreshSettings();
        guiUpdateLatch.countDown();
        World newWorld = scene.getWorld();

        boolean isSameWorld = mapLoader.getWorld().getWorldDirectory().equals(newWorld.getWorldDirectory());
        if (isSameWorld) {
          getChunkSelection().setSelection(chunky.getSceneManager().getScene().getChunks());
        } else {
          if (newWorld != EmptyWorld.INSTANCE && mapLoader.getWorld() != EmptyWorld.INSTANCE) {
            Alert loadWorldConfirm = Dialogs.createAlert(AlertType.CONFIRMATION);
            loadWorldConfirm.getButtonTypes().clear();
            loadWorldConfirm.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
            loadWorldConfirm.setTitle("Load scene world");
            loadWorldConfirm.setContentText(
              "This scene shows a different world than the one that is currently loaded. Do you want to load the world of this scene?");
            Dialogs.stayOnTop(loadWorldConfirm);
            if (loadWorldConfirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.YES) {
              mapLoader.loadWorld(newWorld.getWorldDirectory());
              getChunkSelection().setSelection(chunky.getSceneManager().getScene().getChunks());
            }
          }
        }
      });
      new Thread(() -> {
        try {
          guiUpdateLatch.await();
          Platform.runLater(() -> {
            showRenderPreview();
            canvas.forceRepaint();
          });
        } catch (InterruptedException ignored) {
          // Ignored.
        }
      }).start();
    });
    renderManager.setSnapshotControl(SnapshotControl.DEFAULT);
    renderManager.setOnFrameCompleted((scene1, spp) -> {
      if (renderManager.getSnapshotControl().saveSnapshot(scene1, spp)) {
        scene1.saveSnapshot(new File(renderController.getContext().getSceneDirectory(), "snapshots"), taskTracker);
      }

      if (renderManager.getSnapshotControl().saveRenderDump(scene1, spp)) {
        // Save the scene description and current render dump.
        asyncSceneManager.saveScene(renderController.getContext().getSceneDirectory());
      }
    });

    renderManager.addRenderListener(renderTracker);
    renderManager.setRenderTask(taskTracker.backgroundTask());

    saveScene.setGraphic(new ImageView(Icon.disk.fxImage()));
    saveScene.setOnAction(e -> asyncSceneManager.saveScene());

    saveSceneAs.setOnAction((e) -> {
      ValidatingTextInputDialog sceneNameDialog = new ValidatingTextInputDialog(scene.name(), AsynchronousSceneManager::sceneNameIsValid);
      sceneNameDialog.setTitle("Save scene as…");
      sceneNameDialog.setHeaderText("Enter a scene name");
      String newName = AsynchronousSceneManager.sanitizedSceneName(sceneNameDialog.showAndWait().orElse(""), "");
      if (!newName.isEmpty() && this.promptSaveScene(newName)) {
        asyncSceneManager.saveSceneAs(newName);
        scene.setName(newName);
        updateTitle();
      }
    });

    saveSceneCopy.setOnAction((e) -> {
      ValidatingTextInputDialog sceneNameDialog = new ValidatingTextInputDialog("Copy of " + scene.name(), AsynchronousSceneManager::sceneNameIsValid);
      sceneNameDialog.setTitle("Save a copy…");
      sceneNameDialog.setHeaderText("Enter a scene name");
      String newName = AsynchronousSceneManager.sanitizedSceneName(sceneNameDialog.showAndWait().orElse(""), "");
      if (!newName.isEmpty() && this.promptSaveScene(newName)) {
        File sceneDirectory = SynchronousSceneManager.resolveSceneDirectory(newName);
        asyncSceneManager.enqueueTask(() -> {
          asyncSceneManager.saveSceneCopy(new SceneIOProvider() {
            @Override
            public File getSceneDirectory() {
              return sceneDirectory;
            }

            @Override
            public File getSceneFile(String fileName) {
              if (!sceneDirectory.exists()) {
                sceneDirectory.mkdirs();
              }
              return new File(getSceneDirectory(), fileName);
            }
          }, newName);
        });
      }
    });

    loadScene.setGraphic(new ImageView(Icon.load.fxImage()));
    loadScene.setOnAction(e -> openSceneChooser());

    loadSceneFile.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Load scene");
      fileChooser
        .getExtensionFilters().add(new FileChooser.ExtensionFilter("Chunky scene", "*.json"));
      File sceneFile = fileChooser.showOpenDialog(stage);
      if (sceneFile != null && sceneFile.canRead()) {
        try {
          chunky.getSceneManager().loadScene(sceneFile.getParentFile(), sceneFile.getName().substring(0, sceneFile.getName().length() - 5));
        } catch (IOException | InterruptedException ex) {
          Log.error("Failed to load scene", ex);
        }
      }
    });

    Log.setReceiver(new UILogReceiver(), Level.ERROR, Level.WARNING);

    mapLoader = new WorldMapLoader(this, mapView);
    map = new ChunkMap(mapLoader, this, mapView, chunkSelection,
        mapCanvas, mapOverlay);

    yMin.setTooltip("Minimum Y level displayed in the map view.");
    yMax.setTooltip("Maximum Y level displayed in the map view.");

    AtomicBoolean ignoreYUpdate = new AtomicBoolean(false); // used to not trigger a world reload after changing the world, see #926
    mapLoader.addWorldLoadListener(
        (world, reloaded) -> {
          if (!reloaded) {
            chunkSelection.clearSelection();
          }
          world.addChunkDeletionListener(chunkSelection);
          Optional<Vector3> playerPos = world.playerPos();
          world.addChunkUpdateListener(map);

          Platform.runLater(
              () -> {
                if (!reloaded || trackPlayer.getValue()) {
                  mapView.panTo(playerPos.orElse(new Vector3(0, 0, 0)));
                }
                if (!reloaded) {
                  ignoreYUpdate.set(true);
                  if (mapLoader.getWorld().getVersionId() >= World.VERSION_21W06A) {
                    yMin.setRange(-64, 320);
                    yMin.set(-64);
                    yMax.setRange(-64, 320);
                    yMax.set(320);
                  } else {
                    yMin.setRange(0, 256);
                    yMin.set(0);
                    yMax.setRange(0, 256);
                    yMax.set(256);
                  }
                  ignoreYUpdate.set(false);
                }
                map.redrawMap();
                mapName.setText(world.levelName());
                showWorldMap();
              });
        });

    map.setOnViewDragged(() -> {
      trackPlayer.set(false);
      trackCamera.set(false);
    });

    trackPlayer.addListener(e -> {
      boolean track = trackPlayer.get();
      PersistentSettings.setFollowPlayer(track);
      if (track) {
        mapLoader.withWorld(world -> world.playerPos().ifPresent(mapView::panTo));
      }
    });

    trackCamera.addListener(e -> {
      boolean track = trackCamera.get();
      PersistentSettings.setFollowCamera(track);
      if (track) {
        panToCamera();
      }
    });

    mapPane.widthProperty().addListener((observable, oldValue, newValue) -> {
      mapCanvas.setWidth(newValue.doubleValue());
      mapOverlay.setWidth(newValue.doubleValue());
      mapView.setMapSize((int) mapCanvas.getWidth(), (int) mapCanvas.getHeight());
    });
    mapPane.heightProperty().addListener((observable, oldValue, newValue) -> {
      mapCanvas.setHeight(newValue.doubleValue());
      mapOverlay.setHeight(newValue.doubleValue());
      mapView.setMapSize((int) mapCanvas.getWidth(), (int) mapCanvas.getHeight());
    });

    mapOverlay.setOnMouseExited(e -> map.tooltip.hide());

    // Set up property bindings for the map view.
    ChunkView initialView = map.getView();  // Initial map view - only used to initialize controls.

    // A scale factor of 16 is used to convert map positions between block/chunk coordinates.
    DoubleProperty xProperty = new SimpleDoubleProperty(initialView.x);
    DoubleProperty zProperty = new SimpleDoubleProperty(initialView.z);

    // Bind controls with properties.
    xPosition.valueProperty().bindBidirectional(xProperty);
    zPosition.valueProperty().bindBidirectional(zProperty);
    scale.setRange(ChunkView.BLOCK_SCALE_MIN, ChunkView.BLOCK_SCALE_MAX);
    scale.clampBoth();
    scale.set(initialView.scale);
    scale.setTooltip("Map scale (zoom) measured in pixels per chunk.");

    // Add listeners to the properties to control the map view.
    GroupedChangeListener.ListenerGroup group = GroupedChangeListener.newGroup();
    xProperty.addListener(new GroupedChangeListener<>(group, (observable, oldValue, newValue) -> {
      ChunkView view = mapView.getMapView();
      mapView.panTo(newValue.doubleValue() / 16, view.z);
    }));
    zProperty.addListener(new GroupedChangeListener<>(group, (observable, oldValue, newValue) -> {
      ChunkView view = mapView.getMapView();
      mapView.panTo(view.x, newValue.doubleValue() / 16);
    }));
    showPlayers.setSelected(this.map.isDrawingPlayers());
    showPlayers.selectedProperty().addListener((observable, oldValue, newValue) -> {
      this.map.setDrawingPlayers(newValue);
      this.map.redrawMap();
    });
    scale.valueProperty().addListener(new GroupedChangeListener<>(group,
        (observable, oldValue, newValue) -> mapView.setScale(newValue.intValue())));
    yMin.valueProperty().addListener(new GroupedChangeListener<>(group, (observable, oldValue, newValue) -> {
      if (!ignoreYUpdate.get()) {
        mapView.setYMin(newValue.intValue());
        mapLoader.reloadWorld();
      }
    }));
    yMax.valueProperty().addListener(new GroupedChangeListener<>(group, (observable, oldValue, newValue) -> {
      if (!ignoreYUpdate.get()) {
        mapView.setYMax(newValue.intValue());
        mapLoader.reloadWorld();
      }
    }));

    // Add map view listener to control the individual value properties.
    mapView.getMapViewProperty().addListener(new GroupedChangeListener<>(group,
        (observable, oldValue, newValue) -> {
          xProperty.set(newValue.x * 16);
          zProperty.set(newValue.z * 16);
          scale.set(newValue.scale);
        }));

    creditsMenuItem.setOnAction(e -> {
      try {
        Credits credits = new Credits();
        credits.show();
      } catch (IOException e1) {
        Log.warn("Failed to create credits window.", e1);
      }
    });

    Collection<Tab> javaFxTabs = new ArrayList<>(mainTabs.getTabs());
    // Call the hook to let plugins add their tabs.
    javaFxTabs = chunky.getMainTabTransformer().apply(javaFxTabs);
    mainTabs.getTabs().setAll(javaFxTabs);

    trackPlayerBtn.selectedProperty().bindBidirectional(trackPlayer);
    trackPlayerBtn.setTooltip(new Tooltip("Center the map view over the player."));
    trackCameraBtn.selectedProperty().bindBidirectional(trackCamera);
    trackCameraBtn.setTooltip(new Tooltip("Center the map view over the camera."));

    int currentDimension = mapLoader.getDimension();
    overworldBtn.setSelected(currentDimension == World.OVERWORLD_DIMENSION);
    overworldBtn.setTooltip(new Tooltip("Full of grass and Creepers!"));

    netherBtn.setSelected(currentDimension == World.NETHER_DIMENSION);
    netherBtn.setTooltip(new Tooltip("The land of Zombie Pig-men."));

    endBtn.setSelected(currentDimension == World.END_DIMENSION);
    endBtn.setTooltip(new Tooltip("Watch out for the dragon."));

    changeWorldBtn.setOnAction(e -> {
      try {
        WorldChooser worldChooser = new WorldChooser(mapLoader);
        worldChooser.show();
      } catch (IOException e1) {
        Log.error("Failed to create world chooser window.", e1);
      }
    });

    reloadWorldBtn.setGraphic(new ImageView(Icon.reload.fxImage()));
    reloadWorldBtn.setOnAction(e -> mapLoader.reloadWorld());

    overworldBtn.setGraphic(new ImageView(Icon.grass.fxImage()));
    overworldBtn.setOnAction(e -> mapLoader.setDimension(World.OVERWORLD_DIMENSION));

    netherBtn.setGraphic(new ImageView(Icon.netherrack.fxImage()));
    netherBtn.setOnAction(e -> mapLoader.setDimension(World.NETHER_DIMENSION));

    endBtn.setGraphic(new ImageView(Icon.endStone.fxImage()));
    endBtn.setOnAction(e -> mapLoader.setDimension(World.END_DIMENSION));

    loadScene.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
    loadSceneFile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
    saveScene.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
    saveSceneAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
    menuExit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));

    mapView.setMapSize((int) mapCanvas.getWidth(), (int) mapCanvas.getHeight());
    mapOverlay.setOnScroll(map::onScroll);
    mapOverlay.setOnMousePressed(map::onMousePressed);
    mapOverlay.setOnMouseReleased(map::onMouseReleased);
    mapOverlay.setOnMouseMoved(map::onMouseMoved);
    mapOverlay.setOnMouseDragged(map::onMouseDragged);
    mapOverlay.addEventFilter(MouseEvent.ANY, event -> mapOverlay.requestFocus());
    mapOverlay.setOnKeyPressed(map::onKeyPressed);
    mapOverlay.setOnKeyReleased(map::onKeyReleased);

    mapLoader.loadWorld(PersistentSettings.getLastWorld());
    if (mapLoader.getWorld().getVersionId() >= World.VERSION_21W06A) {
      mapView.setYMin(-64);
      mapView.setYMax(320);
    } else {
      mapView.setYMin(0);
      mapView.setYMax(256);
    }

    menuExit.setOnAction(event -> {
      Platform.exit();
      System.exit(0);
    });

    canvas = new RenderCanvasFx(this, chunky.getSceneManager().getScene(),
        chunky.getRenderController().getRenderManager());
    canvas.setRenderListener(renderTracker);
    previewTab.setContent(canvas);
    sceneControls = new RenderControlsFxController(this, renderControls, canvas,
        chunky.getRenderController().getRenderManager());
    showWorldMap();
    mainTabs.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          boolean finalize = newValue == previewTab;
          scene.setBufferFinalization(finalize);
          if (finalize && scene.getMode() == RenderMode.PREVIEW) {
            scene.refresh();
          }
        });
    worldMapTab.setGraphic(new ImageView(Icon.map.fxImage()));
    previewTab.setGraphic(new ImageView(Icon.sky.fxImage()));

    start.setGraphic(new ImageView(Icon.play.fxImage()));
    start.setTooltip(new Tooltip("Start/resume rendering."));
    start.setOnAction(e -> {
      if (!scene.isLoading())
        asyncSceneManager.enqueueTask(() -> scene.startRender());
    });
    pause.setGraphic(new ImageView(Icon.pause.fxImage()));
    pause.setTooltip(new Tooltip("Pause the render."));
    pause.setOnAction(e -> {
      if (!scene.isLoading())
        asyncSceneManager.enqueueTask(() -> scene.pauseRender());
    });
    reset.setGraphic(new ImageView(Icon.stop.fxImage()));
    reset.setTooltip(new Tooltip("Resets the current render. Discards render progress."));
    reset.setOnAction(e -> {
      if (!scene.isLoading())
        asyncSceneManager.enqueueTask(() -> scene.haltRender());
    });
    sppLbl.setTooltip(new Tooltip("SPP = Samples Per Pixel, SPS = Samples Per Second"));
    targetSpp.setName("Target SPP");
    targetSpp.setTooltip("Rendering is stopped after reaching the target Samples Per Pixel (SPP).");
    targetSpp.setRange(100, 100000);
    targetSpp.makeLogarithmic();
    targetSpp.set(scene.getTargetSpp());
    targetSpp.onValueChange(value -> scene.setTargetSpp(value));
    saveDefaultSpp.setTooltip(new Tooltip("Make the current SPP target the default."));
    saveDefaultSpp.setOnAction(e ->
        PersistentSettings.setSppTargetDefault(scene.getTargetSpp()));
  }

  public void openSceneChooser() {
    try {
      if (this.sceneChooser == null) {
        this.sceneChooser = new SceneChooser(this);
        this.sceneChooser.show();
      } else {
        this.sceneChooser.toFront();
      }
    } catch (IOException e1) {
      Log.error("Failed to create scene chooser window.", e1);
    }
  }

  public void sceneChooserClosed() {
    this.sceneChooser = null;
  }

  public void saveCurrentFrame() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Current Frame");
    if (saveFrameDirectory != null && saveFrameDirectory.isDirectory()) {
      fileChooser.setInitialDirectory(saveFrameDirectory);
    }
    Map<ExtensionFilter, PictureExportFormat> filters = new IdentityHashMap<>();
    for (PictureExportFormat mode : PictureExportFormats.getFormats()) {
      ExtensionFilter filter = new ExtensionFilter(mode.getDescription(), "*" + mode.getExtension());
      fileChooser.getExtensionFilters().add(filter);
      filters.put(filter, mode);
    }
    fileChooser.getExtensionFilters().stream()
      .filter(e -> filters.get(e).equals(scene.outputMode))
      .findFirst().ifPresent(fileChooser::setSelectedExtensionFilter);
    fileChooser.setInitialFileName(String.format("%s-%d",
        scene.name(), renderManager.getRenderStatus().getSpp()));
    File target = fileChooser.showSaveDialog(mapCanvas.getScene().getWindow());
    if (target != null) {
      saveFrameDirectory = target.getParentFile();
      PictureExportFormat format = filters.getOrDefault(fileChooser.selectedExtensionFilterProperty().get(), PictureExportFormats.PNG);
      if (!target.getName().endsWith(format.getExtension())) {
        target = new File(target.getPath() + format.getExtension());
      }
      scene.saveFrame(target, format, taskTracker);
    }
  }

  public void copyCurrentFrame() {
    try {
      PipedInputStream in = new PipedInputStream();
      PipedOutputStream out = new PipedOutputStream(in);
      new Thread(() -> {
        try {
          scene.writeFrame(out, PictureExportFormats.PNG, new TaskTracker(ProgressListener.NONE));
        } catch (IOException e) {
          Log.warn("Failed to copy image to clipboard", e);
        }
      }).start();
      ClipboardContent content = new ClipboardContent();
      content.putImage(new Image(in));
      Clipboard.getSystemClipboard().setContent(content);
    } catch(IOException e) {
      Log.warn("Failed to copy image to clipboard", e);
    }
  }

  @Override  public boolean allowSceneRefresh() {
    if (scene.getResetReason() == ResetReason.SCENE_LOADED
        || renderManager.getRenderStatus().getRenderTime() < SCENE_EDIT_GRACE_PERIOD) {
      return true;
    } else {
      requestRenderReset();
    }
    return false;
  }

  private void requestRenderReset() {
    if (resetConfirmMutex.compareAndSet(false, true)) {
      Platform.runLater(() -> {
        Alert confirmReset = new Alert(
          AlertType.CONFIRMATION,
          "Something in the scene settings changed which requires a render reset, but the render has already made significant progress. " +
            "\nDo you want to reset the render to apply the changes? " +
            "\nYour current progress will be lost!",
          new ButtonType("Reset", ButtonBar.ButtonData.YES),
          ButtonType.CANCEL
        );
        confirmReset.setTitle("Reset render to apply setting changes?");
        DialogUtils.setupDialogDesign(confirmReset, mapCanvas.getScene());

        ButtonType resultAction = confirmReset
          .showAndWait()
          .orElse(ButtonType.CANCEL);
        if (resultAction.getButtonData() == ButtonBar.ButtonData.YES) {
          asyncSceneManager.applySceneChanges();
        } else {
          asyncSceneManager.discardSceneChanges();
          refreshSettings();
        }
        resetConfirmMutex.set(false);
      });
    }
  }

  public void refreshSettings() {
    targetSpp.set(scene.getTargetSpp());
    sceneControls.refreshSettings();
  }

  private void updateTitle() {
    stage.setTitle(scene.name() + " - " + Chunky.getMainWindowTitle());
  }

  /**
   * Loads a scene into chunky
   * @param sceneName The name of the scene. NOTE: Do not include extension.
   */
  public void loadScene(File sceneDirectory, String sceneName) {
    try {
      chunky.getSceneManager().loadScene(sceneDirectory, sceneName);
    } catch (IOException | InterruptedException e) {
      Log.error("Failed to load scene", e);
    }
  }

  public void panToCamera() {
    chunky.getRenderController().getSceneProvider()
        .withSceneProtected(scene -> mapView.panTo(scene.camera().getPosition()));
  }

  public void moveCameraTo(double x, double z) {
    chunky.getRenderController().getSceneProvider().withEditSceneProtected(scene -> {
      Camera camera = scene.camera();
      Vector3 pos = new Vector3(x, camera.getPosition().y, z);
      camera.setPosition(pos);
    });
  }

  public ChunkMap getMap() {
    return map;
  }

  public Chunky getChunky() {
    return chunky;
  }

  public WorldMapLoader getMapLoader() {
    return mapLoader;
  }

  public void openDirectory(File directory) {
    if (directory != null && directory.isDirectory()) {
      // Running Desktop.open() on the JavaFX application thread seems to
      // lock up the application on Linux, so we create a new thread to run that.
      // This StackOverflow question seems to ask about the same bug:
      // http://stackoverflow.com/questions/23176624/javafx-freeze-on-desktop-openfile-desktop-browseuri
      new Thread(() -> {
        try {
          if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(directory);
          } else {
            Log.warn("Can not open system file browser.");
          }
        } catch (IOException e1) {
          Log.warn("Failed to open directory.", e1);
        }
      }).start();
    }
  }

  public MapView getMapView() {
    return mapView;
  }

  public ChunkSelectionTracker getChunkSelection() {
    return chunkSelection;
  }

  @Override public void cameraViewUpdated() {
    map.cameraViewUpdated();
  }

  private boolean promptSaveScene(String sceneName) {
    File oldFormat = new File(PersistentSettings.getSceneDirectory(), sceneName + Scene.EXTENSION);
    File newFormat = new File(PersistentSettings.getSceneDirectory(), sceneName);
    if (oldFormat.exists() || newFormat.exists()) {
      Alert alert = Dialogs.createAlert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Overwrite existing scene");
      alert.setContentText("A scene with that name already exists. This will overwrite the existing scene, are you sure you want to continue?");

      if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
        return false;
      }
    }
    return  true;
  }
}
