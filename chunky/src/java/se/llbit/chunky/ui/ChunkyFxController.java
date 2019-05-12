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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.launcher.LauncherSettings;
import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.scene.AsynchronousSceneManager;
import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.ui.render.RenderControlsFx;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkSelectionListener;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.Icon;
import se.llbit.chunky.world.World;
import se.llbit.chunky.world.listeners.ChunkUpdateListener;
import se.llbit.fxutil.GroupedChangeListener;
import se.llbit.log.Level;
import se.llbit.log.Log;
import se.llbit.math.QuickMath;
import se.llbit.math.Vector3;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * Controller for the main Chunky window.
 */
public class ChunkyFxController
    implements Initializable, ChunkSelectionListener, ChunkUpdateListener {

  private final Chunky chunky;
  private WorldMapLoader mapLoader;
  private ChunkMap map;
  private MapView mapView;

  @FXML private Canvas mapCanvas;
  @FXML private Canvas mapOverlay;
  @FXML private Label mapName;
  @FXML private MenuItem menuExit;
  @FXML private VBox rootContainer;
  @FXML private Button clearSelectionBtn;
  @FXML private Button changeWorldBtn;
  @FXML private Button reloadWorldBtn;
  @FXML private ToggleButton overworldBtn;
  @FXML private ToggleButton netherBtn;
  @FXML private ToggleButton endBtn;
  @FXML private TextField scaleField;
  @FXML private Slider scaleSlider;
  @FXML private ToggleButton trackPlayerBtn;
  @FXML private ToggleButton trackCameraBtn;
  @FXML private Tab mapViewTab;
  @FXML private Tab chunksTab;
  @FXML private Tab optionsTab;
  @FXML private Tab renderTab;
  @FXML private Tab aboutTab;
  @FXML private Button editResourcePacks;
  @FXML private CheckBox singleColorBtn;
  @FXML private CheckBox showLauncherBtn;
  @FXML private Button clearSelectionBtn2;
  @FXML private Button newSceneBtn;
  @FXML private Button loadSceneBtn;
  @FXML private Button openSceneDirBtn;
  @FXML private Button changeSceneDirBtn;
  @FXML private Hyperlink documentationLink;
  @FXML private Hyperlink gitHubLink;
  @FXML private Hyperlink issueTrackerLink;
  @FXML private Hyperlink forumLink;
  @FXML private Button creditsBtn;
  @FXML private TextField xPosition;
  @FXML private TextField zPosition;
  @FXML private Button deleteChunks;
  @FXML private Button exportZip;
  @FXML private Button renderPng;
  @FXML private StackPane mapPane;
  @FXML private TabPane tabPane;

  private BooleanProperty trackPlayer =
      new SimpleBooleanProperty(PersistentSettings.getFollowPlayer());
  private BooleanProperty trackCamera =
      new SimpleBooleanProperty(PersistentSettings.getFollowCamera());

  private RenderControlsFx controls = null;
  private Stage stage;
  private Path prevPngDir = null;

  public ChunkyFxController(Chunky chunky) {
    this.chunky = chunky;
    mapView = new MapView();
    mapLoader = new WorldMapLoader(this, mapView);
    map = new ChunkMap(mapLoader, this, mapView);
    mapView.addViewListener(mapLoader);
    mapView.addViewListener(map);
    mapLoader.getChunkSelection().addSelectionListener(this);
    mapLoader.getChunkSelection().addChunkUpdateListener(map);
    mapLoader.addWorldLoadListener(() -> {
      map.redrawMap();
    });

    map.setOnViewDragged(() -> {
      trackPlayer.set(false);
      trackCamera.set(false);
    });

    trackPlayer.addListener(e -> {
      boolean track = trackPlayer.get();
      PersistentSettings.setFollowPlayer(track);
      if (track) {
        mapLoader.panToPlayer();
      }
    });

    trackCamera.addListener(e -> {
      boolean track = trackCamera.get();
      PersistentSettings.setFollowCamera(track);
      if (track) {
        panToCamera();
      }
    });

  }

  @Override public void initialize(URL fxmlUrl, ResourceBundle resources) {
    Log.setReceiver(new UILogReceiver(), Level.ERROR, Level.WARNING);
    map.setCanvas(mapCanvas);

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
    IntegerProperty scaleProperty = new SimpleIntegerProperty(initialView.scale);

    // Bind controls with properties.
    xPosition.textProperty().bindBidirectional(xProperty, new NumberStringConverter());
    zPosition.textProperty().bindBidirectional(zProperty, new NumberStringConverter());
    scaleField.textProperty().bindBidirectional(scaleProperty, new NumberStringConverter());
    scaleSlider.valueProperty().bindBidirectional(scaleProperty);

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
    scaleProperty.addListener(new GroupedChangeListener<>(group,
        (observable, oldValue, newValue) -> mapView.setScale(newValue.intValue())));

    // Add map view listener to control the individual value properties.
    mapView.getMapViewProperty().addListener(new GroupedChangeListener<>(group,
        (observable, oldValue, newValue) -> {
          xProperty.set(newValue.x * 16);
          zProperty.set(newValue.z * 16);
          scaleProperty.set(newValue.scale);
        }));

    clearSelectionBtn2.setOnAction(e -> mapLoader.clearChunkSelection());

    deleteChunks.setTooltip(new Tooltip("Delete selected chunks."));
    deleteChunks.setOnAction(e -> {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Delete Selected Chunks");
      alert.setContentText(
          "Do you really want to delete the selected chunks? This can not be undone.");
      if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
        mapLoader.deleteSelectedChunks(ProgressTracker.NONE);
      }
    });

    exportZip.setTooltip(new Tooltip("Export selected chunks to Zip archive."));
    exportZip.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Export Chunks to Zip");
      fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Zip files", "*.zip"));
      fileChooser.setInitialFileName(String.format("%s.zip", mapLoader.getWorldName()));
      File target = fileChooser.showSaveDialog(stage);
      if (target != null) {
        mapLoader.exportZip(target, ProgressTracker.NONE);
      }
    });

    renderPng.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Export PNG");
      fileChooser
          .setSelectedExtensionFilter(new FileChooser.ExtensionFilter("PNG images", "*.png"));
      fileChooser.setInitialFileName(String.format("%s.png", mapLoader.getWorldName()));
      if (prevPngDir != null) {
        fileChooser.setInitialDirectory(prevPngDir.toFile());
      }
      File target = fileChooser.showSaveDialog(stage);
      if (target != null) {
        Path path = target.toPath();
        if (!target.getName().endsWith(".png")) {
          path = path.resolveSibling(target.getName() + ".png");
        }
        prevPngDir = path.getParent();
        map.renderView(path.toFile(), ProgressTracker.NONE);
      }
    });

    newSceneBtn.setTooltip(
        new Tooltip("Creates a new 3D scene with the currently selected chunks."));
    newSceneBtn.setOnAction(e -> createNew3DScene());
    loadSceneBtn.setGraphic(new ImageView(Icon.load.fxImage()));
    loadSceneBtn.setOnAction(e -> loadScene());

    openSceneDirBtn.setOnAction(e -> openSceneDirectory());

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
    renderTab.setGraphic(new ImageView(Icon.sky.fxImage()));
    aboutTab.setGraphic(new ImageView(Icon.question.fxImage()));

    Collection<Tab> javaFxTabs = new ArrayList<>();
    javaFxTabs.add(mapViewTab);
    javaFxTabs.add(chunksTab);
    javaFxTabs.add(optionsTab);
    javaFxTabs.add(renderTab);
    javaFxTabs.add(aboutTab);
    // Call the hook to let plugins add their tabs.
    javaFxTabs = chunky.getMainTabTransformer().apply(javaFxTabs);
    tabPane.getTabs().setAll(javaFxTabs);

    editResourcePacks.setTooltip(
        new Tooltip("Select resource packs Chunky uses to load textures."));
    editResourcePacks.setGraphic(new ImageView(Icon.pencil.fxImage()));
    editResourcePacks.setOnAction(e -> {
      ResourceLoadOrderEditor editor = new ResourceLoadOrderEditor();
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

    overworldBtn.setSelected(mapLoader.getDimension() == World.OVERWORLD_DIMENSION);
    overworldBtn.setTooltip(new Tooltip("Full of grass and Creepers!"));

    netherBtn.setSelected(mapLoader.getDimension() == World.NETHER_DIMENSION);
    netherBtn.setTooltip(new Tooltip("The land of Zombie Pigmen."));

    endBtn.setSelected(mapLoader.getDimension() == World.END_DIMENSION);
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
    clearSelectionBtn.setOnAction(event -> mapLoader.clearChunkSelection());

    mapView.setMapSize((int) mapCanvas.getWidth(), (int) mapCanvas.getHeight());
    mapOverlay.setOnScroll(map::onScroll);
    mapOverlay.setOnMousePressed(map::onMousePressed);
    mapOverlay.setOnMouseReleased(map::onMouseReleased);
    mapOverlay.setOnMouseMoved(map::onMouseMoved);
    mapOverlay.setOnMouseDragged(map::onMouseDragged);
    mapOverlay.addEventFilter(MouseEvent.ANY, event -> mapOverlay.requestFocus());
    mapOverlay.setOnKeyPressed(map::onKeyPressed);
    mapOverlay.setOnKeyReleased(map::onKeyReleased);

    mapLoader.addWorldLoadListener(
        () -> mapName.setText(mapLoader.getWorld().levelName()));
    mapLoader.loadWorld(PersistentSettings.getLastWorld());
  }

  public void setStageAndScene(ChunkyFx app, Stage stage, Scene scene) {
    this.stage = stage;
    documentationLink.setOnAction(
        e -> app.getHostServices().showDocument("http://chunky.llbit.se"));

    issueTrackerLink.setOnAction(
        e -> app.getHostServices().showDocument("https://github.com/llbit/chunky/issues"));

    gitHubLink.setOnAction(
        e -> app.getHostServices().showDocument("https://github.com/llbit/chunky"));

    forumLink.setOnAction(
        e -> app.getHostServices().showDocument("https://www.reddit.com/r/chunky"));

    stage.setOnCloseRequest(event -> {
      Platform.exit();
      System.exit(0);
    });
    menuExit.setOnAction(event -> {
      Platform.exit();
      System.exit(0);
    });

    rootContainer.prefHeightProperty().bind(scene.heightProperty());
    rootContainer.prefWidthProperty().bind(scene.widthProperty());
  }

  /**
   * Open the 3D chunk view.
   */
  private synchronized void open3DView() {
    try {
      if (controls == null) {
        controls = new RenderControlsFx(this);
        controls.show();
      } else {
        controls.show();
        controls.toFront();
      }
    } catch (IOException e) {
      Log.error("Failed to create render controls window.", e);
    }
  }

  public void createNew3DScene() {
    if (hasActiveRenderControls()) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Create New Scene");
      alert.setHeaderText("Overwrite existing scene?");
      alert.setContentText(
          "It seems like a scene already exists. Do you wish to overwrite it?");
      if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
        return;
      }
    }

    // Choose a default scene name.
    World world = mapLoader.getWorld();
    RenderContext context = chunky.getRenderContext();
    String preferredName =
        AsynchronousSceneManager.preferredSceneName(context, world.levelName());
    if (!AsynchronousSceneManager.sceneNameIsValid(preferredName)
        || !AsynchronousSceneManager.sceneNameIsAvailable(context, preferredName)) {
      preferredName = "Untitled Scene";
    }

    // Reset the scene state to the default scene state.
    chunky.getRenderController().getSceneManager().getScene().resetScene(preferredName,
        chunky.getSceneFactory());

    // Show the render controls etc.
    open3DView();

    // Load selected chunks.
    Collection<ChunkPosition> selection = mapLoader.getChunkSelection().getSelection();
    if (selection.isEmpty()) {
      chunky.getSceneManager().getScene().camera().setView(0.0, QuickMath.degToRad(-68.0), 0.0);
      chunky.getSceneManager().getScene().camera().setPosition(new Vector3(0, 84, 0));
    } else {
      chunky.getSceneManager().loadFreshChunks(mapLoader.getWorld(), selection);
    }
  }

  public void loadScene(String sceneName) {
    open3DView();
    try {
      chunky.getSceneManager().loadScene(sceneName);
    } catch (IOException | InterruptedException e) {
      Log.error("Failed to load scene", e);
    }
  }

  /**
   * Show the scene selector dialog.
   */
  public void loadScene() {
    try {
      SceneChooser chooser = new SceneChooser(this);
      chooser.show();
    } catch (IOException e) {
      Log.error("Failed to create scene chooser window.", e);
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

  public boolean hasActiveRenderControls() {
    return controls != null && controls.isShowing();
  }

  public ChunkMap getMap() {
    return map;
  }

  @Override public void chunkSelectionChanged() {
  }

  @Override public void regionUpdated(ChunkPosition region) {
  }

  @Override public void chunkUpdated(ChunkPosition region) {
  }

  public Chunky getChunky() {
    return chunky;
  }

  public WorldMapLoader getMapLoader() {
    return mapLoader;
  }

  public Canvas getMapOverlay() {
    return mapOverlay;
  }

  public void openSceneDirectory() {
    File sceneDir = chunky.options.sceneDir;
    if (sceneDir != null) {
      // Running Desktop.open() on the JavaFX application thread seems to
      // lock up the application on Linux, so we create a new thread to run that.
      // This StackOverflow question seems to ask about the same bug:
      // http://stackoverflow.com/questions/23176624/javafx-freeze-on-desktop-openfile-desktop-browseuri
      new Thread(() -> {
        try {
          if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(sceneDir);
          } else {
            Log.warn("Can not open system file browser.");
          }
        } catch (IOException e1) {
          Log.warn("Failed to open scene directory.", e1);
        }
      }).start();
    }
  }

  public MapView getMapView() {
    return mapView;
  }
}
