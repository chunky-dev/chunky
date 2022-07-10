/* Copyright (c) 2022 Chunky contributors
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

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.resources.MinecraftFinder;
import se.llbit.chunky.resources.ResourcePackLoader;
import se.llbit.chunky.ui.Icons;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.log.Log;
import se.llbit.util.MinecraftText;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourcePackChooserController implements Initializable {
  @FXML
  private ListView<PackListItem> availablePacksListView;
  private ObservableList<PackListItem> availablePacksList;

  @FXML
  private ListView<PackListItem> targetPacksListView;
  private ObservableList<PackListItem> targetPacksList;

  @FXML
  private Button removeFromTargetPacksBtn;
  @FXML
  private Button moveToTargetPacksBtn;
  @FXML
  private Button addNewTargetPackBtn;

  @FXML
  private CheckBox disableDefaultTexturesBtn;
  @FXML
  private Button cancelBtn;
  private Runnable onCancel;
  @FXML
  private Button applyAsDefaultBtn;
  private Consumer<List<File>> onApplyAsDefault;

  private final static double ROW_SIZE = 48.0;

  private enum Side {
    AVAILABLE,
    TARGETED
  }

  private ListView<PackListItem> getAssociatedList(Side side) {
    switch (side) {
      case TARGETED:
        return targetPacksListView;
      case AVAILABLE:
        return availablePacksListView;
      default:
        throw new IllegalStateException();
    }
  }

  private static class PackListItemCell extends ListCell<PackListItem> {
    private final Side side;
    private final ResourcePackChooserController controller;

    private final ImageView icon = new ImageView();
    private final Label name = new Label();
    private final StringProperty nameProp = new SimpleStringProperty("Loading...");
    private final Label formatVersion = new Label();
    private final Label description = new Label();
    private final GridPane root = new GridPane();

    private final BooleanProperty disableMoveUp = new SimpleBooleanProperty(true);
    private final BooleanProperty disableMoveDown = new SimpleBooleanProperty(true);
    private final BooleanProperty packDisabled = new SimpleBooleanProperty(false);

    public PackListItemCell(Side side, ResourcePackChooserController controller) {
      this.side = side;
      this.controller = controller;
      setOnMouseClicked(e -> {
        if (isEmpty() || getItem().isDefaultPack())
          return;
        if (e.getClickCount() == 2) {
          switch (side) {
            case AVAILABLE:
              controller.movePacksToTargetList(Collections.singletonList(getItem()));
              break;
            case TARGETED:
              controller.removePacksFromTargetList(Collections.singletonList(getItem()));
              break;
          }
          e.consume();
        }
      });
      setGraphic(root);

      setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      icon.setPreserveRatio(true);
      icon.setFitHeight(ROW_SIZE);
      icon.setFitWidth(ROW_SIZE);

      name.setStyle("-fx-font-weight: bold");
      name.textProperty().bind(Bindings.concat(
        nameProp,
        Bindings.createStringBinding(() -> packDisabled.get() ? " (disabled)" : "", packDisabled)
      ));
      formatVersion.setTextAlignment(TextAlignment.RIGHT);
      formatVersion.setStyle("-fx-text-alignment: RIGHT");

      root.setPrefHeight(ROW_SIZE);
      root.setHgap(6.0);

      int columnOffset = 0;
      if (side == Side.TARGETED) {
        root.add(buildControls(), 0, 0, 1, 2);
        columnOffset++;
        root.getColumnConstraints().add(new ColumnConstraints());
      }
      root.getColumnConstraints().addAll(
        new ColumnConstraints(ROW_SIZE, ROW_SIZE, ROW_SIZE, Priority.NEVER, HPos.CENTER, false), // icon
        new ColumnConstraints(0, 150, Integer.MAX_VALUE, Priority.ALWAYS, HPos.LEFT, true), // name
        new ColumnConstraints(18, 18, 18, Priority.NEVER, HPos.RIGHT, true) // format version
      );

      root.add(icon, columnOffset, 0, 1, 2);
      root.add(name, columnOffset + 1, 0);
      root.add(formatVersion, columnOffset + 2, 0);
      root.add(description, columnOffset + 1, 1, 2, 1);

      disableProperty().bind(packDisabled);
      initContextMenu();
    }

    private Node buildControls() {
      Button moveUpBtn = new Button();
      Icons.buildIcon(Icons.HEAVY_ARROW_RIGHT).withSize(12).rotateCCW().setAsGraphicOn(moveUpBtn);
      moveUpBtn.setOnAction(evt ->
        Collections.swap(controller.targetPacksList, getIndex(), getIndex() - 1)
      );
      moveUpBtn.disableProperty().bind(disableMoveUp);
      Button moveDownBtn = new Button();
      Icons.buildIcon(Icons.HEAVY_ARROW_RIGHT).withSize(12).rotateCW().setAsGraphicOn(moveDownBtn);
      moveDownBtn.setOnAction(evt ->
        Collections.swap(controller.targetPacksList, getIndex(), getIndex() + 1)
      );
      moveDownBtn.disableProperty().bind(disableMoveDown);
      return new VBox(
        moveUpBtn,
        moveDownBtn
      );
    }

    private void initContextMenu() {
      this.setContextMenu(new ContextMenu(
        buildMenuItem("Open in system file browser",
          evt -> {
            if(getItem() == null)
              return;
            try {
              Desktop.getDesktop().open(
                getItem().isDefaultPack()
                  ? getItem().file.getParentFile()
                  : getItem().file
              );
            } catch (IOException ex) {
              Log.warn("Failed to open resource pack file in system file browser.", ex);
            }
          },
          menuItem -> {
            if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
              menuItem.disableProperty().bind(itemProperty().isNull());
            } else {
              menuItem.setDisable(true);
            }
          }
        ),
        new SeparatorMenuItem(),
        buildMenuItem("Select all", evt ->
          controller.updateListSelection(side, false)),
        buildMenuItem("Deselect all", evt ->
          controller.updateListSelection(side, true))
      ));
    }

    private MenuItem buildMenuItem(String label, EventHandler<ActionEvent> eventHandler) {
      return buildMenuItem(label,eventHandler, menuItem -> {});
    }
    private MenuItem buildMenuItem(String label, EventHandler<ActionEvent> eventHandler, Consumer<MenuItem> init) {
      MenuItem item = new MenuItem(label);
      item.setOnAction(eventHandler);
      init.accept(item);
      return item;
    }

    private void updateItemData() {
      PackListItem item = getItem();
      if (item == null)
        return;

      icon.setImage(item.getIcon());
      nameProp.set(item.getName());
      if (!item.isDefaultPack()) {
        formatVersion.setText(item.getFormatVersionString());
        description.setText(item.getDescription());

        packDisabled.unbind();
        packDisabled.set(false);

        // disable move up if we are the first item
        disableMoveUp.set(getIndex() == 0);
        // disable move down if we are the second last item (last item is default resource pack)
        disableMoveDown.set(getIndex() == getListView().getItems().size() - 2);
      } else {
        formatVersion.setText("");
        description.setText("loaded from " + item.getFile().getName());

        packDisabled.bind(controller.disableDefaultTexturesBtn.selectedProperty());

        disableMoveDown.set(true);
        disableMoveUp.set(true);
      }
    }

    @Override
    protected void updateItem(PackListItem item, boolean empty) {
      super.updateItem(item, empty);

      if (empty || item == null) {
        setText(null);
        setGraphic(null);
      } else {
        setGraphic(root);

        item.loading.addListener(prop -> updateItemData());
        updateItemData();
      }
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    availablePacksList = availablePacksListView.getItems();
    availablePacksListView.setItems(
      availablePacksList.sorted(Comparator.comparing(PackListItem::getName, String.CASE_INSENSITIVE_ORDER))
    );
    availablePacksListView.setCellFactory(list -> new PackListItemCell(Side.AVAILABLE, this));
    availablePacksListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    targetPacksList = targetPacksListView.getItems();
    targetPacksListView.setCellFactory(list -> new PackListItemCell(Side.TARGETED, this));
    targetPacksListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    ObservableList<PackListItem> selectedItems = targetPacksListView.getSelectionModel().getSelectedItems();
    Icons.buildIcon(Icons.HEAVY_ARROW_RIGHT).withSize(12).flipX().setAsGraphicOn(removeFromTargetPacksBtn, true);
    removeFromTargetPacksBtn.disableProperty()
      .bind(Bindings.createBooleanBinding(
        () -> selectedItems.isEmpty()
          || selectedItems.stream().allMatch(PackListItem::isDefaultPack),
        selectedItems
      ));
    removeFromTargetPacksBtn.setOnAction(evt ->
      removePacksFromTargetList(targetPacksListView.getSelectionModel().getSelectedItems())
    );
    Icons.buildIcon(Icons.HEAVY_ARROW_RIGHT).withSize(12).setAsGraphicOn(moveToTargetPacksBtn, true);
    moveToTargetPacksBtn.disableProperty()
      .bind(availablePacksListView.getSelectionModel().selectedItemProperty().isNull());
    moveToTargetPacksBtn.setOnAction(evt ->
      movePacksToTargetList(availablePacksListView.getSelectionModel().getSelectedItems())
    );

    Icons.buildIcon(Icons.HEAVY_PLUS).withSize(14).setAsGraphicOn(addNewTargetPackBtn);
    addNewTargetPackBtn.setOnAction(evt -> browseForUnlistedPack());
    disableDefaultTexturesBtn.setTooltip(new Tooltip("Disable loading of textures from Minecraft and revert to internal textures and any loaded resource packs.\nRequires restart for changes to take effect."));
    disableDefaultTexturesBtn.setSelected(PersistentSettings.getDisableDefaultTextures());
    disableDefaultTexturesBtn.selectedProperty().addListener((observable, oldValue, newValue) -> {
      PersistentSettings.setDisableDefaultTextures(newValue);
    });

    cancelBtn.setOnAction(evt -> onCancel.run());
    applyAsDefaultBtn.setOnAction(evt -> onApplyAsDefault.accept(getSelectedResourcePacks()));
  }

  private void browseForUnlistedPack() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose Resource Pack(s)");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
      "Resource Packs",
      "*.zip",
      "*.jar",
      "pack.mcmeta"
    ));
    fileChooser.setInitialDirectory(MinecraftFinder.getResourcePacksDirectory());
    
    List<File> newlyAddedFiles = fileChooser.showOpenMultipleDialog(addNewTargetPackBtn.getScene().getWindow());
    if (newlyAddedFiles == null)
      return;

    List<PackListItem> newPacks = newlyAddedFiles.stream()
      .map(file ->
        // file -> pack
        Stream.concat(availablePacksList.stream(), targetPacksList.stream())
          .filter(availablePack -> availablePack.getFile().equals(file))
          // use pack from available/target list, if found there
          .findFirst()
          // otherwise create a new pack object
          .orElseGet(() -> new PackListItem(
            file.getName().endsWith(".mcmeta") // directory resource pack
              ? file.getParentFile()
              : file
          ))
      )
      .collect(Collectors.toList());

    // remove all already existing packs to guarantee them being added on top
    targetPacksList.removeAll(newPacks);
    movePacksToTargetList(newPacks);
  }

  public List<File> getSelectedResourcePacks() {
    return targetPacksList.stream()
      .filter(pack -> !pack.isDefaultPack())
      .map(PackListItem::getFile)
      .collect(Collectors.toList());
  }

  private void updateListSelection(Side side, boolean deselect) {
    ListView<PackListItem> list = getAssociatedList(side);
    if (deselect) {
      list.getSelectionModel().clearSelection();
    } else {
      list.getSelectionModel().selectAll();
    }
  }

  private void removePacksFromTargetList(List<PackListItem> packs) {
    packs = packs.stream()
      .filter(item -> !item.isDefaultPack())
      .collect(Collectors.toList());
    availablePacksList.addAll(packs);
    targetPacksList.removeAll(packs);
  }

  private void movePacksToTargetList(List<PackListItem> packs) {
    targetPacksList.addAll(0, packs);
    availablePacksList.removeAll(packs);
  }

  public void populate(
    Consumer<List<File>> onApplyAsDefault,
    Runnable onCancel
  ) {
    this.onCancel = onCancel;
    this.onApplyAsDefault = onApplyAsDefault;

    List<File> available = ResourcePackLoader.getAvailableResourcePacks();
    List<File> loaded = ResourcePackLoader.getLoadedResourcePacks();
    available.removeAll(loaded);

    availablePacksList.setAll(
      available.stream()
        .map(PackListItem::new)
        .collect(Collectors.toList())
    );

    targetPacksList.setAll(
      loaded.stream()
        .map(PackListItem::new)
        .collect(Collectors.toList())
    );

    PackListItem defaultPack = PackListItem.getDefault();
    if (defaultPack != null) {
      availablePacksList.removeAll(defaultPack);
      targetPacksList.removeAll(defaultPack);
      targetPacksList.add(defaultPack);
    }
  }

  private static class PackListItem {

    private final static Executor PACK_PARSER_EXECUTOR = Executors.newSingleThreadExecutor();

    private static PackListItem DEFAULT = null;

    public static PackListItem getDefault() {
      File minecraftJar = MinecraftFinder.getMinecraftJar();
      if (minecraftJar == null)
        return null;
      if (DEFAULT == null || !DEFAULT.file.equals(minecraftJar)) {
        DEFAULT = new PackListItem(minecraftJar, "Default Resource Pack");

        // load missing_pack.png from current minecraft jar
        loadMissingPackPng(minecraftJar);
      }
      return DEFAULT;
    }

    public static Image MISSING_PACK_PNG = null;

    private static void loadMissingPackPng(File minecraftJar) {
      try (FileSystem zipFs = FileSystems.newFileSystem(
        URI.create("jar:" + minecraftJar.toURI()),
        Collections.emptyMap()
      )) {
        Path unknownPackPng = zipFs.getPath("assets/minecraft/textures/misc/unknown_pack.png");
        try (InputStream unknownPackPngStream = Files.newInputStream(unknownPackPng)) {
          MISSING_PACK_PNG = new Image(unknownPackPngStream);
        }
      } catch (IOException ignored) {
      }
    }

    private final File file;
    private final String name;
    private final boolean isDefaultPack;

    public final BooleanProperty loading = new SimpleBooleanProperty(true);

    private Image icon = MISSING_PACK_PNG;
    private String description = "loading …";
    private int formatVersion = 0;

    public File getFile() {
      return file;
    }

    public String getName() {
      return name;
    }

    public boolean isDefaultPack() {
      return isDefaultPack;
    }

    public Image getIcon() {
      return icon;
    }

    public String getDescription() {
      return description;
    }

    public String getFormatVersionString() {
      return formatVersion <= 0
         ? ""
         : ("v" + formatVersion);
    }

    public PackListItem(File resourcePackFile) {
      this(resourcePackFile, null);
    }

    private PackListItem(File resourcePackFile, String name) {
      this(resourcePackFile, name, PACK_PARSER_EXECUTOR);
    }

    private PackListItem(File resourcePackFile, String name, Executor backgroundLoadExecutor) {
      file = resourcePackFile;
      if (name == null) {
        String filename = MinecraftText.removeFormatChars(resourcePackFile.getName());
        int endOfFilename = filename.lastIndexOf('.');
        this.name = (endOfFilename > 0)
          ? filename.substring(0, endOfFilename)
          : filename;
        this.isDefaultPack = false;
      } else {
        this.name = name;
        this.isDefaultPack = true;
      }
      backgroundLoadExecutor.execute(() -> parseResourcePack(resourcePackFile));
    }

    private void parseResourcePack(File resourcePackFile) {
      try (FileSystem resourcePack = ResourcePackLoader.getPackFileSystem(resourcePackFile)) {
        Path root = ResourcePackLoader.getPackRootPath(resourcePackFile, resourcePack);

        Path mcMetaPath = root.resolve("pack.mcmeta");
        if (!isDefaultPack && Files.exists(mcMetaPath)) {
          try (InputStream inputStream = Files.newInputStream(mcMetaPath)) {
            loadMcMeta(inputStream);
          }
        } else {
          description = "[did not find pack.mcmeta]";
        }

        Path iconPath = root.resolve("pack.png");
        if (Files.exists(iconPath)) {
          try (InputStream inputStream = Files.newInputStream(iconPath)) {
            loadIcon(inputStream);
          }
        }
      } catch (UnsupportedOperationException uoex) {
        // default file systems do not support closing
      } catch (IOException ioex) {
        Log.infof("Could not load resource pack metadata: %s [%s]", resourcePackFile.getAbsolutePath(),
          ioex.getMessage());
      }
      Platform.runLater(() -> loading.set(false));
    }

    private void loadMcMeta(InputStream inputStream) throws IOException {
      try (
        JsonParser parser = new JsonParser(inputStream)
      ) {
        JsonObject packInformation = parser.parse().object().get("pack").object();
        formatVersion = packInformation.get("pack_format").intValue(1);
        description = MinecraftText.removeFormatChars(packInformation.get("description").stringValue(""));
      } catch (JsonParser.SyntaxError jpex) {
        Log.infof("Json error in pack.mcmeta: %s (%s)", file.getAbsolutePath(), jpex.getMessage());
        description = "[failed to load pack.mcmeta]";
      }
    }

    private void loadIcon(InputStream inputStream) {
      icon = new Image(inputStream);
    }

    @Override
    public String toString() {
      return file.toString();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof PackListItem)) return false;
      PackListItem item = (PackListItem) o;
      return file.equals(item.file);
    }

    @Override
    public int hashCode() {
      return Objects.hash(file);
    }
  }
}
