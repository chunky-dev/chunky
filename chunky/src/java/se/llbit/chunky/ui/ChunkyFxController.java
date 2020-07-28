/* Copyright (c) 2016-2019 Jesper Öqvist <jesper@llbit.se>
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

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
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
import javafx.util.converter.NumberStringConverter;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.launcher.LauncherSettings;
import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.main.ZipExportJob;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.renderer.CameraViewListener;
import se.llbit.chunky.renderer.OutputMode;
import se.llbit.chunky.renderer.RenderController;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.RenderStatusListener;
import se.llbit.chunky.renderer.Renderer;
import se.llbit.chunky.renderer.ResetReason;
import se.llbit.chunky.renderer.SnapshotControl;
import se.llbit.chunky.renderer.scene.AsynchronousSceneManager;
import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.renderer.scene.RenderResetHandler;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkSelectionTracker;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.DeleteChunksJob;
import se.llbit.chunky.world.Icon;
import se.llbit.chunky.world.World;
import se.llbit.chunky.world.listeners.ChunkUpdateListener;
import se.llbit.fx.ToolPane;
import se.llbit.fxutil.GroupedChangeListener;
import se.llbit.log.Level;
import se.llbit.log.Log;
import se.llbit.math.Vector3;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controller for the main Chunky window.
 */
public class ChunkyFxController
    implements Initializable, CameraViewListener, RenderResetHandler {

  private final Chunky chunky;
  private WorldMapLoader mapLoader;
  private ChunkMap map;
  private MapView mapView;
  protected ChunkSelectionTracker chunkSelection = new ChunkSelectionTracker();

  @FXML private Canvas mapCanvas;
  @FXML private Canvas mapOverlay;
  @FXML private Label mapName;
  @FXML private MenuItem menuExit;
  @FXML private ToolPane renderControls;
  @FXML private Button clearSelectionBtn;
  @FXML private Button changeWorldBtn;
  @FXML private Button reloadWorldBtn;
  @FXML private ToggleButton overworldBtn;
  @FXML private ToggleButton netherBtn;
  @FXML private ToggleButton endBtn;
  @FXML private IntegerAdjuster scale;
  @FXML private ToggleButton trackPlayerBtn;
  @FXML private ToggleButton trackCameraBtn;
  @FXML private Tab mapViewTab;
  @FXML private Tab chunksTab;
  @FXML private Tab optionsTab;
  @FXML private Tab aboutTab;
  @FXML private Button editResourcePacks;
  @FXML private CheckBox singleColorBtn;
  @FXML private CheckBox showLauncherBtn;
  @FXML private Button openSceneDirBtn;
  @FXML private Button changeSceneDirBtn;
  @FXML private Hyperlink documentationLink;
  @FXML private Hyperlink originalDocumentationLink;
  @FXML private Hyperlink gitHubLink;
  @FXML private Hyperlink issueTrackerLink;
  @FXML private Hyperlink forumLink;
  @FXML private Hyperlink discordLink;
  @FXML private Hyperlink guideLink;
  @FXML private Button creditsBtn;
  @FXML private TextField xPosition;
  @FXML private TextField zPosition;
  @FXML private Button deleteChunks;
  @FXML private Button exportZip;
  @FXML private Button renderPng;
  @FXML private StackPane mapPane;
  @FXML private SplitPane splitPane;
  @FXML private TabPane mapTabs;
  @FXML private TabPane mainTabs;
  @FXML private Tab worldMapTab;
  @FXML private Tab previewTab;

  @FXML private TextField sceneNameField;
  @FXML private Button saveScene;
  @FXML private Button loadScene;

  @FXML private Button saveFrameBtn;
  @FXML private Button copyFrameBtn;
  @FXML private ProgressBar progressBar;
  @FXML private Label progressLbl;
  @FXML private Label etaLbl;
  @FXML private Label renderTimeLbl;
  @FXML private Label sppLbl;
  @FXML private IntegerAdjuster targetSpp;
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
    private int sps;

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
  private Renderer renderer;
  private RenderController renderController;

  public ChunkyFxController(Chunky chunky) {
    this.chunky = chunky;
    mapView = new MapView();
    renderTracker = new GUIRenderListener(this);
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

  @Override public void initialize(URL fxmlUrl, ResourceBundle resources) {
    scene = chunky.getSceneManager().getScene();
    renderController = chunky.getRenderController();
    renderer = renderController.getRenderer();
    asyncSceneManager =
        (AsynchronousSceneManager) renderController.getSceneManager();
    asyncSceneManager.setResetHandler(this);
    TaskTracker taskTracker = new TaskTracker(progressListener);
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
        getChunkSelection().setSelection(chunky.getSceneManager().getScene().getChunks());
      });
      new Thread(() -> {
        try {
          guiUpdateLatch.await();
          showRenderPreview();
          canvas.forceRepaint();
        } catch (InterruptedException ignored) {
          // Ignored.
        }
      }).start();
    });
    renderer.setSnapshotControl(SnapshotControl.DEFAULT);
    renderer.setOnFrameCompleted((scene1, spp) -> {
      if (SnapshotControl.DEFAULT.saveSnapshot(scene1, spp)) {

        scene1.saveSnapshot(new File(renderController.getContext().getSceneDirectory(), "snapshots"), taskTracker, renderController.getContext().numRenderThreads());
      }

      if (SnapshotControl.DEFAULT.saveRenderDump(scene1, spp)) {
        // Save the scene description and current render dump.
        asyncSceneManager.saveScene();
      }
    });

    renderer.addRenderListener(renderTracker);
    renderer.setRenderTask(taskTracker.backgroundTask());

    saveScene.setGraphic(new ImageView(Icon.disk.fxImage()));
    saveScene.setOnAction(e -> saveSceneSafe(sceneNameField.getText()));

    loadScene.setGraphic(new ImageView(Icon.load.fxImage()));
    loadScene.setOnAction(e -> openSceneChooser());

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
    sceneNameField.setOnAction(event -> saveSceneSafe(sceneNameField.getText()));

    Log.setReceiver(new UILogReceiver(), Level.ERROR, Level.WARNING);

    mapLoader = new WorldMapLoader(this, mapView);
    map = new ChunkMap(mapLoader, this, mapView, chunkSelection,
        mapCanvas, mapOverlay);

    mapLoader.addWorldLoadListener(
        world -> {
          chunkSelection.clearSelection();
          world.addChunkDeletionListener(chunkSelection);
          Optional<Vector3> playerPos = world.playerPos();
          world.addChunkUpdateListener(map);
          world.addChunkUpdateListener(
              new ChunkUpdateListener() {
                private boolean warningShown = false;

                @Override
                public void chunkUpdated(ChunkPosition chunkPosition) {
                  if (!warningShown) {
                  String version = world.getChunk(chunkPosition).getVersion();
                  if (version != null && !version.equals("1.13")) {
                      warningShown = true;
                      Log.warn(
                          "This version of Chunky only supports worlds created with Minecraft 1.13 or later. " +
                          "To render worlds from older Minecraft versions, please use Chunky 1.4.x or convert it to the new format using a current version of Minecraft.");
                    }
                  }
                }
              });

          Platform.runLater(
              () -> {
                mapView.panTo(playerPos.orElse(new Vector3(0, 0, 0)));
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
    xPosition.textProperty().bindBidirectional(xProperty, new NumberStringConverter());
    zPosition.textProperty().bindBidirectional(zProperty, new NumberStringConverter());
    scale.setRange(ChunkView.BLOCK_SCALE_MIN, ChunkView.BLOCK_SCALE_MAX);
    scale.clampBoth();
    scale.set(initialView.scale);

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
    scale.valueProperty().addListener(new GroupedChangeListener<>(group,
        (observable, oldValue, newValue) -> mapView.setScale(newValue.intValue())));

    // Add map view listener to control the individual value properties.
    mapView.getMapViewProperty().addListener(new GroupedChangeListener<>(group,
        (observable, oldValue, newValue) -> {
          xProperty.set(newValue.x * 16);
          zProperty.set(newValue.z * 16);
          scale.set(newValue.scale);
        }));

    deleteChunks.setTooltip(new Tooltip("Delete selected chunks."));
    deleteChunks.setOnAction(e -> {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Delete Selected Chunks");
      alert.setContentText(
          "Do you really want to delete the selected chunks? This can not be undone.");
      if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
        deleteSelectedChunks(ProgressTracker.NONE);
      }
    });

    exportZip.setTooltip(new Tooltip("Export selected chunks to Zip archive."));
    exportZip.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Export Chunks to Zip");
      fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Zip files", "*.zip"));
      mapLoader.withWorld(world -> fileChooser.setInitialFileName(world.levelName() + ".zip"));
      File target = fileChooser.showSaveDialog(exportZip.getScene().getWindow());
      if (target != null) {
        exportZip(target, ProgressTracker.NONE);
      }
    });

    renderPng.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Export PNG");
      fileChooser
          .getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG images", "*.png"));
      mapLoader.withWorld(world -> fileChooser.setInitialFileName(world.levelName() + ".png"));
      if (prevPngDir != null) {
        fileChooser.setInitialDirectory(prevPngDir.toFile());
      }
      File target = fileChooser.showSaveDialog(exportZip.getScene().getWindow());
      if (target != null) {
        Path path = target.toPath();
        if (!target.getName().endsWith(".png")) {
          path = path.resolveSibling(target.getName() + ".png");
        }
        prevPngDir = path.getParent();
        map.renderView(path.toFile(), ProgressTracker.NONE);
      }
    });

    openSceneDirBtn.setTooltip(
        new Tooltip("Open the directory where Chunky stores scene descriptions and renders."));
    openSceneDirBtn.setOnAction(e -> openDirectory(chunky.options.sceneDir));

    changeSceneDirBtn.setOnAction(e -> SceneDirectoryPicker.changeSceneDirectory(chunky.options));

    creditsBtn.setOnAction(e -> {
      try {
        Credits credits = new Credits();
        credits.show();
      } catch (IOException e1) {
        Log.warn("Failed to create credits window.", e1);
      }
    });

    mapViewTab.setGraphic(new ImageView(Icon.map.fxImage()));
    chunksTab.setGraphic(new ImageView(Icon.mapSelected.fxImage()));
    optionsTab.setGraphic(new ImageView(Icon.wrench.fxImage()));
    aboutTab.setGraphic(new ImageView(Icon.question.fxImage()));

    Collection<Tab> javaFxTabs = new ArrayList<>();
    javaFxTabs.add(mapViewTab);
    javaFxTabs.add(chunksTab);
    javaFxTabs.add(optionsTab);
    javaFxTabs.add(aboutTab);
    // Call the hook to let plugins add their tabs.
    javaFxTabs = chunky.getMainTabTransformer().apply(javaFxTabs);
    mapTabs.getTabs().setAll(javaFxTabs);
    splitPane.setDividerPositions(0.2, 0.8);

    editResourcePacks.setTooltip(
        new Tooltip("Select resource packs Chunky uses to load textures."));
    editResourcePacks.setGraphic(new ImageView(Icon.pencil.fxImage()));
    editResourcePacks.setOnAction(e -> {
      ResourceLoadOrderEditor editor = new ResourceLoadOrderEditor(() -> {
        scene.refresh();
      });
      editor.show();
    });

    LauncherSettings settings = new LauncherSettings();
    settings.load();
    showLauncherBtn
        .setTooltip(new Tooltip("Opens the Chunky launcher when starting Chunky next time."));
    showLauncherBtn.setSelected(settings.showLauncher);
    showLauncherBtn.selectedProperty().addListener((observable, oldValue, newValue) -> {
      LauncherSettings launcherSettings = new LauncherSettings();
      launcherSettings.load();
      launcherSettings.showLauncher = newValue;
      launcherSettings.save();
    });

    singleColorBtn.setSelected(PersistentSettings.getSingleColorTextures());
    singleColorBtn.selectedProperty().addListener((observable, oldValue, newValue) -> {
      PersistentSettings.setSingleColorTextures(newValue);
    });

    trackPlayerBtn.selectedProperty().bindBidirectional(trackPlayer);
    trackCameraBtn.selectedProperty().bindBidirectional(trackCamera);

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

    menuExit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
    clearSelectionBtn.setOnAction(event -> chunkSelection.clearSelection());

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

    menuExit.setOnAction(event -> {
      Platform.exit();
      System.exit(0);
    });

    canvas = new RenderCanvasFx(chunky.getSceneManager().getScene(),
        chunky.getRenderController().getRenderer());
    canvas.setRenderListener(renderTracker);
    previewTab.setContent(canvas);
    sceneControls = new RenderControlsFxController(this, renderControls, canvas,
        chunky.getRenderController().getRenderer());
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

    saveFrameBtn.setOnAction(this::saveCurrentFrame);
    copyFrameBtn.setOnAction(this::copyCurrentFrame);
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
    targetSpp.set(scene.getTargetSpp());
    targetSpp.onValueChange(value -> scene.setTargetSpp(value));
    saveDefaultSpp.setTooltip(new Tooltip("Make the current SPP target the default."));
    saveDefaultSpp.setOnAction(e ->
        PersistentSettings.setSppTargetDefault(scene.getTargetSpp()));
  }

  public void setApplication(Application app) {
    documentationLink.setOnAction(
        e -> app.getHostServices().showDocument("https://lemaik.github.io/chunky"));

    originalDocumentationLink.setOnAction(
        e -> app.getHostServices().showDocument("http://chunky.llbit.se"));

    issueTrackerLink.setOnAction(
        e -> app.getHostServices().showDocument("https://github.com/llbit/chunky/issues"));

    gitHubLink.setOnAction(
        e -> app.getHostServices().showDocument("https://github.com/llbit/chunky"));

    forumLink.setOnAction(
        e -> app.getHostServices().showDocument("https://www.reddit.com/r/chunky"));

    discordLink.setOnAction(
        e -> app.getHostServices().showDocument("https://discord.com/invite/VqcHpsF"));

    guideLink.setOnAction(
        e -> app.getHostServices().showDocument("https://jackjt8.github.io/ChunkyGuide/"));
  }

  public void openSceneChooser() {
    try {
      SceneChooser chooser = new SceneChooser(this);
      chooser.show();
    } catch (IOException e1) {
      Log.error("Failed to create scene chooser window.", e1);
    }
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
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PNG files", "*.png"));
        break;
      case TIFF_32:
        extension = ".tiff";
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("TIFF files", "*.tiff"));
        break;
    }
    fileChooser.setInitialFileName(String.format("%s-%d%s",
        scene.name(), renderer.getRenderStatus().getSpp(), extension));
    File target = fileChooser.showSaveDialog(saveFrameBtn.getScene().getWindow());
    if (target != null) {
      saveFrameDirectory = target.getParentFile();
      if (!target.getName().endsWith(extension)) {
        target = new File(target.getPath() + extension);
      }
      // TODO:
      //scene.saveFrame(target, taskTracker);
      scene.saveFrame(target, new TaskTracker(ProgressListener.NONE), renderController.getContext().numRenderThreads());
    }
  }

  private void copyCurrentFrame(Event event) {
    try {
      PipedInputStream in = new PipedInputStream();
      PipedOutputStream out = new PipedOutputStream(in);
      new Thread(() -> {
        try {
          scene.writeFrame(out, OutputMode.PNG, new TaskTracker(ProgressListener.NONE), renderController.getContext().numRenderThreads());
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
          popup.show(renderControls.getScene().getWindow());
        } catch (IOException e) {
          Log.warn("Could not open reset confirmation dialog.", e);
        }
      });
    }
  }

  public void refreshSettings() {
    targetSpp.set(scene.getTargetSpp());
    sceneControls.refreshSettings();
  }

  private void updateTitle() {
    // TODO
    // stage.setTitle(chunky.getSceneManager().getScene().name());
  }

  /**
   * Loads a scene into chunky
   * @param sceneName The name of the scene. NOTE: Do not include extension.
   */
  public void loadScene(String sceneName) {
    try {
      chunky.getSceneManager().loadScene(sceneName);
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

  private void saveSceneSafe(String sceneName) {
    File oldFormat = new File(PersistentSettings.getSceneDirectory(), sceneName + Scene.EXTENSION);
    File newFormat = new File(PersistentSettings.getSceneDirectory(), sceneName);
    if (oldFormat.exists() || newFormat.exists()) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Overwrite existing scene");
      alert.setContentText("A scene with that name already exists. This will overwrite the existing scene, are you sure you want to continue?");

      if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
    }
    scene.setName(sceneName);
    renderController.getSceneProvider().withSceneProtected(scene1 -> scene1.setName(sceneName));
    updateTitle();
    asyncSceneManager.saveScene();
  }

}
