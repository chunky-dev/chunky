/* Copyright (c) 2016-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016-2021 Chunky Contributors
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
package se.llbit.chunky.ui.render.tabs;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.entity.ArmorStand;
import se.llbit.chunky.entity.Book;
import se.llbit.chunky.entity.PaintingEntity;
import se.llbit.chunky.entity.PlayerEntity;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.renderer.RenderController;
import se.llbit.chunky.renderer.scene.AsynchronousSceneManager;
import se.llbit.chunky.renderer.scene.EntityLoadingPreferences;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.Icons;
import se.llbit.chunky.ui.IntegerAdjuster;
import se.llbit.chunky.ui.elements.SizeInput;
import se.llbit.chunky.ui.ValidatingNumberStringConverter;
import se.llbit.chunky.ui.controller.ChunkyFxController;
import se.llbit.chunky.ui.controller.RenderControlsFxController;
import se.llbit.chunky.ui.dialogs.SettingsExport;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.world.EmptyWorld;
import se.llbit.chunky.world.Icon;
import se.llbit.chunky.world.World;
import se.llbit.fxutil.Dialogs;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.log.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class GeneralTab extends ScrollPane implements RenderControlsTab, Initializable {
  private Scene scene;
  private final Node wrapper;

  @FXML private Button openSceneDirBtn;
  @FXML private Button exportSettings;
  @FXML private Button importSettings;
  @FXML private Button restoreDefaults;
  @FXML private Button loadSelectedChunks;
  @FXML private Button reloadChunks;
  @FXML private Label canvasSizeLabel;
  @FXML private SizeInput canvasSizeInput;
  @FXML private Button applySize;
  @FXML private Button makeDefaultSize;
  @FXML private Button flipAxesBtn;
  @FXML private Pane scaleButtonArea;
  @FXML private Button setDefaultYMin;
  @FXML private Button setDefaultYMax;
  @FXML private Button loadAllEntities;
  @FXML private Button loadNoEntity;
  @FXML private CheckBox loadPlayers;
  @FXML private CheckBox loadArmorStands;
  @FXML private CheckBox loadBooks;
  @FXML private CheckBox loadPaintings;
  @FXML private CheckBox loadOtherEntities;
  @FXML private CheckBox biomeColors;
  @FXML private CheckBox use3dBiomes;
  @FXML private CheckBox saveDumps;
  @FXML private CheckBox saveSnapshots;
  @FXML private ComboBox<Number> dumpFrequency;
  @FXML private IntegerAdjuster yMin;
  @FXML private IntegerAdjuster yMax;

  private final Double[] scaleButtonValues = {0.5, 1.5, 2.0};

  private RenderController controller;
  private WorldMapLoader mapLoader;
  private RenderControlsFxController renderControls;
  private ChunkyFxController chunkyFxController;

  public GeneralTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("GeneralTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();

    this.wrapper = new VBox(this);
  }

  @Override public void update(Scene scene) {
    if (scene.getWorld() instanceof EmptyWorld) {
      scene.setYClipMin(yMin.get());
      scene.setYClipMax(yMax.get());
    }
    yMin.set(scene.getYClipMin());
    yMax.set(scene.getYClipMax());

    if (scene.shouldSaveDumps()) {
      dumpFrequency.setValue(scene.getDumpFrequency());
      dumpFrequency.setDisable(false);
      saveDumps.setSelected(true);
    } else {
      dumpFrequency.setDisable(true);
      saveDumps.setSelected(false);
    }
    {
      EntityLoadingPreferences preferences = scene.getEntityLoadingPreferences();
      loadPlayers.setSelected(preferences.shouldLoadClass(PlayerEntity.class));
      loadArmorStands.setSelected(preferences.shouldLoadClass(ArmorStand.class));
      loadBooks.setSelected(preferences.shouldLoadClass(Book.class));
      loadPaintings.setSelected(preferences.shouldLoadClass(PaintingEntity.class));
      loadOtherEntities.setSelected(preferences.shouldLoadClass(null));
    }
    biomeColors.setSelected(scene.biomeColorsEnabled());
    use3dBiomes.setSelected(scene.using3dBiomes());
    saveSnapshots.setSelected(scene.shouldSaveSnapshots());
    reloadChunks.setDisable(scene.numberOfChunks() == 0);
    loadSelectedChunks.setDisable(
      mapLoader.getWorld() instanceof EmptyWorld ||
      mapLoader.getWorld() == null ||
      chunkyFxController.getChunkSelection().size() == 0
    );
    chunkyFxController.getChunkSelection().addSelectionListener(() -> {
      loadSelectedChunks.setDisable(
        mapLoader.getWorld() instanceof EmptyWorld ||
        mapLoader.getWorld() == null ||
        chunkyFxController.getChunkSelection().size() == 0
      );
    });
    canvasSizeInput.setSize(scene.canvasWidth(), scene.canvasHeight());
    openSceneDirBtn.setDisable(!controller.getContext().getSceneDirectory().exists());
    ((AsynchronousSceneManager) controller.getSceneManager()).setOnSceneSaved(() -> {
      openSceneDirBtn.setDisable(!controller.getContext().getSceneDirectory().exists());
    });
  }

  @Override public String getTabTitle() {
    return "Scene";
  }

  @Override public Node getTabContent() {
    return this.wrapper;
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    // TODO: parentTab.setGraphic(new ImageView(Icon.wrench.fxImage()));

    exportSettings.setOnAction(event -> {
      SettingsExport dialog = new SettingsExport(scene.toJson());
      dialog.show();
    });

    importSettings.setOnAction(event -> {
      TextInputDialog dialog = new TextInputDialog();
      dialog.setTitle("Settings Import");
      dialog.setHeaderText("Import scene settings");
      dialog.setContentText("Settings JSON:");
      Optional<String> result = dialog.showAndWait();
      if (result.isPresent()) {
        String text = result.get();
        try (JsonParser parser = new JsonParser(new ByteArrayInputStream(text.getBytes()))) {
          JsonObject json = parser.parse().object();
          scene.importFromJson(json);
        } catch (IOException e) {
          Log.warn("Failed to import scene settings.");
        } catch (JsonParser.SyntaxError syntaxError) {
          Log.warnf("Failed to import settings: syntax error in JSON string (%s).",
              syntaxError.getMessage());
        }
      }
    });

    restoreDefaults.setOnAction(
        event -> {
          Alert alert = Dialogs.createAlert(AlertType.CONFIRMATION);
          alert.setTitle("Restore default settings");
          alert.setContentText("Do you really want to reset all scene settings?");
          if (alert.showAndWait().get() == ButtonType.OK) {
            scene.resetScene(scene.name, controller.getContext().getChunky().getSceneFactory());
            chunkyFxController.refreshSettings();
          }
        });

    loadPlayers.setTooltip(new Tooltip("Enable/disable player entity loading. "
        + "Takes effect on next scene creation."));
    loadPlayers.selectedProperty().addListener((observable, oldValue, newValue) -> {
      scene.getEntityLoadingPreferences().setPreference(PlayerEntity.class, newValue);
      PersistentSettings.setLoadPlayers(newValue);
    });
    loadPlayers.setOnAction(event -> {
      renderControls.showPopup(
              "This takes effect the next time a new scene is created.", loadPlayers);
    });
    loadArmorStands.setTooltip(new Tooltip("Enable/disable armor stand entity loading. "
            + "Takes effect on next scene creation."));
    loadArmorStands.selectedProperty().addListener((observable, oldValue, newValue) -> {
      scene.getEntityLoadingPreferences().setPreference(ArmorStand.class, newValue);
      PersistentSettings.setLoadArmorStands(newValue);
    });
    loadArmorStands.setOnAction(event -> {
      renderControls.showPopup(
              "This takes effect the next time a new scene is created.", loadArmorStands);
    });
    loadBooks.setTooltip(new Tooltip("Enable/disable book entity loading. "
            + "Takes effect on next scene creation."));
    loadBooks.selectedProperty().addListener((observable, oldValue, newValue) -> {
      scene.getEntityLoadingPreferences().setPreference(Book.class, newValue);
      PersistentSettings.setLoadBooks(newValue);
    });
    loadBooks.setOnAction(event -> {
      renderControls.showPopup(
              "This takes effect the next time a new scene is created.", loadBooks);
    });
    loadPaintings.setTooltip(new Tooltip("Enable/disable painting entity loading. "
            + "Takes effect on next scene creation."));
    loadPaintings.selectedProperty().addListener((observable, oldValue, newValue) -> {
      scene.getEntityLoadingPreferences().setPreference(PaintingEntity.class, newValue);
      PersistentSettings.setLoadPaintings(newValue);
    });
    loadPaintings.setOnAction(event -> {
      renderControls.showPopup(
              "This takes effect the next time a new scene is created.", loadPaintings);
    });
    loadOtherEntities.setTooltip(new Tooltip("Enable/disable other entity loading. "
            + "Takes effect on next scene creation."));
    loadOtherEntities.selectedProperty().addListener((observable, oldValue, newValue) -> {
      scene.getEntityLoadingPreferences().setPreference(null, newValue);
      PersistentSettings.setLoadOtherEntities(newValue);
    });
    loadOtherEntities.setOnAction(event -> {
      renderControls.showPopup(
              "This takes effect the next time a new scene is created.", loadOtherEntities);
    });
    loadAllEntities.setOnAction(event -> {
      loadPlayers.setSelected(true);
      loadArmorStands.setSelected(true);
      loadBooks.setSelected(true);
      loadPaintings.setSelected(true);
      loadOtherEntities.setSelected(true);
    });
    loadNoEntity.setOnAction(event -> {
      loadPlayers.setSelected(false);
      loadArmorStands.setSelected(false);
      loadBooks.setSelected(false);
      loadPaintings.setSelected(false);
      loadOtherEntities.setSelected(false);
    });

    biomeColors.setTooltip(new Tooltip("Colors grass and tree leaves according to biome."));
    biomeColors.selectedProperty().addListener((observable, oldValue, newValue) -> {
      scene.setBiomeColorsEnabled(newValue);
    });
    use3dBiomes.setTooltip(new Tooltip("Attempt to load biomes as 3d (Added in 1.18+)."));
    use3dBiomes.selectedProperty().addListener((observable, oldValue, newValue) -> {
      scene.setUse3dBiomes(newValue);
    });
    dumpFrequency.setConverter(new ValidatingNumberStringConverter(true));
    dumpFrequency.getItems().addAll(50, 100, 500, 1000, 2500, 5000);
    dumpFrequency.setValue(Scene.DEFAULT_DUMP_FREQUENCY);
    dumpFrequency.setEditable(true);
    dumpFrequency.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (saveDumps.isSelected()) {
        scene.setDumpFrequency(newValue.intValue());
      } else {
        scene.setDumpFrequency(0);
      }
    });
    saveDumps.selectedProperty().addListener((observable, oldValue, enable) -> {
      dumpFrequency.setDisable(!enable);
      if (enable) {
        Number frequency = dumpFrequency.getValue();
        if (frequency != null) {
          scene.setDumpFrequency(frequency.intValue());
        }
      } else {
        scene.setDumpFrequency(0);
      }
    });
    saveSnapshots.selectedProperty().addListener((observable1, oldValue1, newValue1) -> {
      scene.setSaveSnapshots(newValue1);
    });
    yMax.setTooltip(
        "Blocks above this Y value are not loaded. Requires reloading chunks to take effect.");
    yMax.onValueChange(value -> {
      scene.setYClipMax(value);
      renderControls.showPopup("Reload the chunks for this to take effect.", yMax);
    });
    yMin.setTooltip(
        "Blocks below this Y value are not loaded. Requires reloading chunks to take effect.");
    yMin.onValueChange(value -> {
      scene.setYClipMin(value);
      renderControls.showPopup("Reload the chunks for this to take effect.", yMax);
    });
    openSceneDirBtn.setTooltip(
        new Tooltip("Open the directory where Chunky stores the scene description and renders of this scene."));
    openSceneDirBtn.setOnAction(e -> chunkyFxController.openDirectory(chunkyFxController.getRenderController().getContext().getSceneDirectory()));
    loadSelectedChunks
        .setTooltip(new Tooltip("Load the chunks that are currently selected in the map view"));
    loadSelectedChunks.setOnAction(e -> {
      controller.getSceneManager()
          .loadChunks(mapLoader.getWorld(), chunkyFxController.getChunkSelection().getSelection());
      reloadChunks.setDisable(chunkyFxController.getChunkSelection().size() == 0);
    });
    reloadChunks.setTooltip(new Tooltip("Reload all chunks in the scene."));
    reloadChunks.setGraphic(new ImageView(Icon.reload.fxImage()));
    reloadChunks.setOnAction(e -> controller.getSceneManager().reloadChunks());

    setDefaultYMin.setTooltip(new Tooltip("Make this the default lower Y clip plane."));
    setDefaultYMin.setOnAction(e -> PersistentSettings.setYClipMin(yMin.get()));
    setDefaultYMax.setTooltip(new Tooltip("Make this the default upper Y clip plane."));
    setDefaultYMax.setOnAction(e -> PersistentSettings.setYClipMax(yMax.get()));

    canvasSizeLabel.setGraphic(new ImageView(Icon.scale.fxImage()));
    canvasSizeInput.getSize().addListener(this::updateCanvasSize);

    SVGPath swapAxesIcon = new SVGPath();
    swapAxesIcon.setContent(Icons.PORTRAIT_TO_LANDSCAPE);
    swapAxesIcon.contentProperty().bind(
      Bindings.when(canvasSizeInput.isSquareRatioProperty())
        .then(Icons.SQUARE_DIAGONALLY_CROSSED)
        .otherwise(Icons.PORTRAIT_TO_LANDSCAPE)
    );
    swapAxesIcon.rotateProperty().bind(
      Bindings.when(canvasSizeInput.isPortraitRatioProperty())
        .then(0)
        .otherwise(-90)
    );
    flipAxesBtn.setGraphic(swapAxesIcon);
    flipAxesBtn.disableProperty().bind(canvasSizeInput.isSquareRatioProperty());
    Tooltip flipAxesTooltip = new Tooltip();
    flipAxesTooltip.textProperty().bind(
      Bindings.when(canvasSizeInput.isPortraitRatioProperty())
        .then("Flip image to landscape format")
        .otherwise("Flip image to portrait format")
    );
    flipAxesBtn.setTooltip(flipAxesTooltip);
    flipAxesBtn.setOnAction(e -> canvasSizeInput.swapAxes());

    for(Double scale : scaleButtonValues) {
      Button scaleButton = new Button("×" + scale.toString());
      scaleButton.setMnemonicParsing(false);
      scaleButton.setTooltip(new Tooltip("Scale the canvas size by " + scale));
      scaleButton.setOnAction(e -> canvasSizeInput.scaleSize(scale));
      scaleButtonArea.getChildren().add(scaleButton);
    }
    applySize.setTooltip(new Tooltip("Apply the new size to the render canvas."));
    applySize.setOnAction(e -> canvasSizeInput.applyChanges());
    makeDefaultSize.setTooltip(new Tooltip("Make the current canvas size the default."));
    makeDefaultSize.setOnAction(e -> PersistentSettings
      .set3DCanvasSize(scene.canvasWidth(), scene.canvasHeight()));
  }

  private void updateCanvasSize(int width, int height) {
    renderControls.getCanvas().setCanvasSize(width, height);
    scene.setCanvasSize(width, height);
  }

  @Override public void setController(RenderControlsFxController controls) {
    this.renderControls = controls;
    this.chunkyFxController = controls.getChunkyController();
    this.mapLoader = chunkyFxController.getMapLoader();
    mapLoader.addWorldLoadListener((world, reloaded) -> {
      loadSelectedChunks.setDisable(world instanceof EmptyWorld || world == null);
    });
    mapLoader.addWorldLoadListener((world, reloaded) -> updateYClipSlidersRanges(world));
    updateYClipSlidersRanges(mapLoader.getWorld());
    this.controller = controls.getRenderController();
    this.scene = this.controller.getSceneManager().getScene();
  }

  private void updateYClipSlidersRanges(World world) {
    if (world != null && world.getVersionId() >= World.VERSION_21W06A) {
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
  }
}
