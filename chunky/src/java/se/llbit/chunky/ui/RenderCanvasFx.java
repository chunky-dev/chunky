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
package se.llbit.chunky.ui;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;
import javafx.stage.PopupWindow;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.*;
import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.controller.ChunkyFxController;
import se.llbit.math.Vector2;

import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Shows the current render preview.
 */
public class RenderCanvasFx extends ScrollPane implements Repaintable, SceneStatusListener {
  private static final WritablePixelFormat<IntBuffer> PIXEL_FORMAT =
      PixelFormat.getIntArgbInstance();

  private final ChunkyFxController chunkyFxController;
  private final Scene renderScene;

  private WritableImage image;

  private final AtomicBoolean painting = new AtomicBoolean(false);
  private final Canvas canvas;
  private final Group guideGroup = new Group();
  private final StackPane canvasPane;
  private final Label noChunksLabel;
  private final RenderManager renderManager;
  private int lastX;
  private int lastY;
  private Vector2 target = new Vector2(0, 0);
  private Tooltip tooltip = new Tooltip();

  private CheckMenuItem showGuides = new CheckMenuItem("Show guides");
  private Line hGuide1 = new Line();
  private Line hGuide2 = new Line();
  private Line vGuide1 = new Line();
  private Line vGuide2 = new Line();

  private RadioMenuItem percent25 = new RadioMenuItem(String.format("%d%%", 25));
  private RadioMenuItem percent50 = new RadioMenuItem(String.format("%d%%", 50));
  private RadioMenuItem percent75 = new RadioMenuItem(String.format("%d%%", 75));
  private RadioMenuItem percent100 = new RadioMenuItem(String.format("%d%%", 100));
  private RadioMenuItem percent150 = new RadioMenuItem(String.format("%d%%", 150));
  private RadioMenuItem percent200 = new RadioMenuItem(String.format("%d%%", 200));
  private RadioMenuItem percent300 = new RadioMenuItem(String.format("%d%%", 300));
  private RadioMenuItem percent400 = new RadioMenuItem(String.format("%d%%", 400));
  private RadioMenuItem fit = new RadioMenuItem("Fit to screen");

  private boolean fitToScreen = PersistentSettings.getCanvasFitToScreen();

  private RenderStatusListener renderListener;

  public RenderCanvasFx(ChunkyFxController chunkyFxController,
      Scene scene, RenderManager renderManager) {
    this.chunkyFxController = chunkyFxController;
    this.renderScene = scene;
    this.renderManager = renderManager;
    renderManager.addSceneStatusListener(this);

    tooltip.setAutoHide(true);
    tooltip.setConsumeAutoHidingEvents(false);
    tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);

    canvas = new Canvas();
    synchronized (scene) {
      canvas.setWidth(scene.width);
      canvas.setHeight(scene.height);
      image = new WritableImage(scene.width, scene.height);
    }

    canvasPane = new StackPane(canvas);
    setContent(canvasPane);

    noChunksLabel = new Label("No chunks selected for rendering.\nOpen a world and select and load chunks in the Map tab to get started.");
    noChunksLabel.setTextAlignment(TextAlignment.CENTER);
    canvasPane.getChildren().add(noChunksLabel);

    guideGroup.getChildren().addAll(hGuide1, hGuide2, vGuide1, vGuide2);
    canvasPane.getChildren().add(guideGroup);

    hGuide1.setVisible(false);
    hGuide1.setStroke(Color.rgb(0, 0, 0, 0.5));
    hGuide1.setStartX(0);
    hGuide1.endXProperty().bind(canvas.widthProperty());
    hGuide1.startYProperty().bind(canvas.heightProperty().divide(3));
    hGuide1.endYProperty().bind(hGuide1.startYProperty());

    hGuide2.setVisible(false);
    hGuide2.setStroke(Color.rgb(0, 0, 0, 0.5));
    hGuide2.setStartX(0);
    hGuide2.endXProperty().bind(canvas.widthProperty());
    hGuide2.startYProperty().bind(canvas.heightProperty().multiply(2 / 3.0));
    hGuide2.endYProperty().bind(hGuide2.startYProperty());

    vGuide1.setVisible(false);
    vGuide1.setStroke(Color.rgb(0, 0, 0, 0.5));
    vGuide1.setStartY(0);
    vGuide1.endYProperty().bind(canvas.heightProperty());
    vGuide1.startXProperty().bind(canvas.widthProperty().divide(3));
    vGuide1.endXProperty().bind(vGuide1.startXProperty());

    vGuide2.setVisible(false);
    vGuide2.setStroke(Color.rgb(0, 0, 0, 0.5));
    vGuide2.setStartY(0);
    vGuide2.endYProperty().bind(canvas.heightProperty());
    vGuide2.startXProperty().bind(canvas.widthProperty().multiply(2 / 3.0));
    vGuide2.endXProperty().bind(vGuide2.startXProperty());

    canvas.setOnMousePressed(e -> {
      lastX = (int) e.getX();
      lastY = (int) e.getY();
    });

    canvas.setOnMouseDragged(e -> {
      if (e.isSecondaryButtonDown() || scene.camera().getCameraLocked()) {
        // do not drag when right-clicking or when the camera angle is locked
        return;
      }
      double dx = lastX - (int) e.getX();
      double dy = lastY - (int) e.getY();
      lastX = (int) e.getX();
      lastY = (int) e.getY();
      if (e.isShiftDown()) {
        dx *= 0.1;
        dy *= 0.1;
      }
      scene.camera().rotateView((Math.PI / 250) * dx, -(Math.PI / 250) * dy);
    });

    canvas.setOnMouseExited(event -> {
      // Hide the tooltip so that it won't prevent clicking outside the render canvas.
      // This is to work around an OpenJDK bug on Linux.
      tooltip.hide();
    });

    ContextMenu contextMenu = new ContextMenu();
    MenuItem setTarget = new MenuItem("Set target");
    setTarget.setOnAction(e -> {
      scene.camera().setTarget(target.x, target.y);
      if (scene.getMode() == RenderMode.PREVIEW) {
        scene.forceReset();
      }
    });
    contextMenu.getItems().add(setTarget);

    showGuides.setSelected(false);
    showGuides.selectedProperty().addListener((observable, oldValue, newValue) -> {
      changeShowGuides(newValue);
      chunkyFxController.syncShowGuides(newValue);
    });
    contextMenu.getItems().add(showGuides);

    Menu canvasScale = new Menu("Canvas scale");
    ToggleGroup scaleGroup = new ToggleGroup();
    percent25.setToggleGroup(scaleGroup);
    percent50.setToggleGroup(scaleGroup);
    percent75.setToggleGroup(scaleGroup);
    percent100.setToggleGroup(scaleGroup);
    percent150.setToggleGroup(scaleGroup);
    percent200.setToggleGroup(scaleGroup);
    percent300.setToggleGroup(scaleGroup);
    percent400.setToggleGroup(scaleGroup);
    fit.setToggleGroup(scaleGroup);

    percent25.setSelected(PersistentSettings.getCanvasScale() == 25 && !fitToScreen);
    percent50.setSelected(PersistentSettings.getCanvasScale() == 50 && !fitToScreen);
    percent75.setSelected(PersistentSettings.getCanvasScale() == 75 && !fitToScreen);
    percent100.setSelected(PersistentSettings.getCanvasScale() == 100 && !fitToScreen);
    percent150.setSelected(PersistentSettings.getCanvasScale() == 150 && !fitToScreen);
    percent200.setSelected(PersistentSettings.getCanvasScale() == 200 && !fitToScreen);
    percent300.setSelected(PersistentSettings.getCanvasScale() == 300 && !fitToScreen);
    percent400.setSelected(PersistentSettings.getCanvasScale() == 400 && !fitToScreen);
    fit.setSelected(fitToScreen);

    percent25.setOnAction(e -> {
      changeCanvasScale(25);
      chunkyFxController.syncCanvasScale(25);
    });
    percent50.setOnAction(e -> {
      changeCanvasScale(50);
      chunkyFxController.syncCanvasScale(50);
    });
    percent75.setOnAction(e -> {
      changeCanvasScale(75);
      chunkyFxController.syncCanvasScale(75);
    });
    percent100.setOnAction(e -> {
      changeCanvasScale(100);
      chunkyFxController.syncCanvasScale(100);
    });
    percent150.setOnAction(e -> {
      changeCanvasScale(150);
      chunkyFxController.syncCanvasScale(150);
    });
    percent200.setOnAction(e -> {
      changeCanvasScale(200);
      chunkyFxController.syncCanvasScale(200);
    });
    percent300.setOnAction(e -> {
      changeCanvasScale(300);
      chunkyFxController.syncCanvasScale(300);
    });
    percent400.setOnAction(e -> {
      changeCanvasScale(400);
      chunkyFxController.syncCanvasScale(400);
    });
    fit.setOnAction(e -> {
      changeFitToScreen();
      chunkyFxController.syncFitToScreen();
    });

    canvasScale.getItems().add(percent25);
    canvasScale.getItems().add(percent50);
    canvasScale.getItems().add(percent75);
    canvasScale.getItems().add(percent100);
    canvasScale.getItems().add(percent150);
    canvasScale.getItems().add(percent200);
    canvasScale.getItems().add(percent300);
    canvasScale.getItems().add(percent400);
    canvasScale.getItems().add(fit);

    contextMenu.getItems().add(canvasScale);

    if (fitToScreen) {
      updateCanvasFit();
    } else {
      updateCanvasScale(PersistentSettings.getCanvasScale() / 100.0);
    }

    contextMenu.getItems().add(new SeparatorMenuItem());

    MenuItem saveCurrentFrame = new MenuItem("Save image as…");
    saveCurrentFrame.setOnAction(e -> chunkyFxController.saveCurrentFrame());
    contextMenu.getItems().add(saveCurrentFrame);

    MenuItem copyFrame = new MenuItem("Copy image to clipboard");
    copyFrame.setOnAction(e -> chunkyFxController.copyCurrentFrame());
    contextMenu.getItems().add(copyFrame);

    chunkyFxController.getChunky()
      .getRenderContextMenuTransformers()
      .forEach(t -> t.accept(contextMenu));

    canvas.setOnMouseClicked(event -> {
      if (event.getButton() == MouseButton.SECONDARY) {
        double invHeight = 1.0 / canvas.getHeight();
        double halfWidth = canvas.getWidth() / (2.0 * canvas.getHeight());
        target.set(-halfWidth + event.getX() * invHeight,
            -0.5 + event.getY() * invHeight);
        contextMenu.show(getScene().getWindow(), event.getScreenX(), event.getScreenY());
      }
    });
    canvas.setOnScroll(e -> {
      if (scene.camera().getCameraLocked()) {
        // do not scroll when the camera angle is locked
        return;
      }
      // deltaY is zero if shift is pressed because shift switches to horizontal scrolling in JavaFX
      double diff = -(Math.abs(e.getDeltaY()) > 0 ? e.getDeltaY() / e.getMultiplierY() : e.getDeltaX() / e.getMultiplierX());
      if (e.isShiftDown()) {
        diff *= 0.1;
      }
      Camera camera = scene.camera();
      double value = camera.getFov();
      double scale = camera.getMaxFoV() - camera.getMinFoV();
      double offset = value / scale;
      double newValue = scale * Math.exp(Math.log(offset) + 0.1 * diff);
      if (!Double.isNaN(newValue) && !Double.isInfinite(newValue)) {
        camera.setFoV(newValue);
      }
    });
    renderManager.setCanvas(this);

    viewportBoundsProperty().addListener((observable, oldValue, newValue) -> updateCanvasPane());

    // Register key event listener for keyboard navigation.
    canvasPane.setFocusTraversable(true);

    // TODO(llbit): find better way of preventing the tab header from stealing focus?
    // The canvas pane needs focus or else it will not receive keyboard events.
    canvasPane.setOnMouseDragged(event -> {
      if (!canvasPane.isFocused()) {
        canvasPane.requestFocus();
      }
    });
    canvasPane.setOnMouseClicked(event -> canvasPane.requestFocus());

    canvasPane.setOnMouseEntered(e -> {
      updateCanvasFit();
    });

    canvasPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (!isVisible()) {
        return;
      }
      if (scene.camera().getCameraLocked()) {
        return;
      }
      double modifier = 1;
      if (e.isControlDown()) {
        modifier *= 100;
      }
      if (e.isShiftDown()) {
        modifier *= 0.1;
      }
      switch (e.getCode()) {
        case ESCAPE:
          // TODO
          //hide();
          e.consume();
          break;
        case W:
          renderScene.camera().moveForward(modifier);
          e.consume();
          break;
        case S:
          renderScene.camera().moveBackward(modifier);
          e.consume();
          break;
        case A:
          renderScene.camera().strafeLeft(modifier);
          e.consume();
          break;
        case D:
          renderScene.camera().strafeRight(modifier);
          e.consume();
          break;
        case R:
          renderScene.camera().moveUp(modifier);
          e.consume();
          break;
        case F:
          renderScene.camera().moveDown(modifier);
          e.consume();
          break;
        case SPACE:
          synchronized (renderScene) {
            if (renderScene.getMode() == RenderMode.RENDERING) {
              renderScene.pauseRender();
            } else {
              renderScene.startRender();
            }
            renderListener.renderStateChanged(renderScene.getMode());
          }
          e.consume();
          break;
      }
    });
  }

  public void changeShowGuides(boolean value) {
    hGuide1.setVisible(value);
    hGuide2.setVisible(value);
    vGuide1.setVisible(value);
    vGuide2.setVisible(value);
  }

  public void syncShowGuides(boolean value) {
    showGuides.setSelected(value);
  }

  public void changeCanvasScale(int percent) {
    updateCanvasScale(percent / 100.0);
    PersistentSettings.setCanvasScale(percent);
    if (fitToScreen) {
      fitToScreen = false;
      PersistentSettings.setCanvasFitToScreen(false);
    }
  }

  public void syncCanvasScale(int percent) {
    if (percent == 25) {
      percent25.setSelected(true);
    } else if (percent == 50) {
      percent50.setSelected(true);
    } else if (percent == 75) {
      percent75.setSelected(true);
    } else if (percent == 100) {
      percent100.setSelected(true);
    } else if (percent == 150) {
      percent150.setSelected(true);
    } else if (percent == 200) {
      percent200.setSelected(true);
    } else if (percent == 300) {
      percent300.setSelected(true);
    } else if (percent == 400) {
      percent400.setSelected(true);
    }
  }

  public void changeFitToScreen() {
    fitToScreen = true;
    PersistentSettings.setCanvasFitToScreen(true);
    updateCanvasFit();
  }

  public void syncFitToScreen() {
    fit.setSelected(true);
  }

  private void updateCanvasPane() {
    Bounds bounds = getViewportBounds();
    canvasPane.setMinWidth(Math.max(canvas.getWidth() * canvas.getScaleX(), bounds.getWidth()));
    canvasPane.setMinHeight(Math.max(canvas.getHeight() * canvas.getScaleY(), bounds.getHeight()));
  }

  private void updateCanvasScale(double scale) {
    canvas.setScaleX(scale);
    canvas.setScaleY(scale);
    updateCanvasPane();
    guideGroup.setScaleX(scale);
    guideGroup.setScaleY(scale);
    updateCanvasScroll();
  }

  private void updateCanvasScroll() {
    // Force layout to occur or else scroll might not work
    layout();

    if (fitToScreen) {
      setVvalue(0.5);
      setHvalue(0.5);
    } else {
      double scaleX = canvas.getScaleX();
      double scrollX = getHvalue();

      if (scrollX > scaleX/2 + 0.5) {
        setHvalue(scaleX/2 + 0.5);
      } else if (scrollX < -scaleX/2 + 0.5) {
        setHvalue(-scaleX/2 + 0.5);
      }

      double scaleY = canvas.getScaleY();
      double scrollY = getVvalue();

      if (scrollY > scaleY/2 + 0.5) {
        setVvalue(scaleY/2 + 0.5);
      } else if (scrollY < -scaleY/2 + 0.5) {
        setVvalue(-scaleY/2 + 0.5);
      }
    }
  }

  private void updateCanvasFit() {
    if (!fitToScreen) {
      return;
    }

    double width = canvas.getWidth();
    double height = canvas.getHeight();
    double fitWidth = this.getWidth();
    double fitHeight = this.getHeight();

    double scaleX = fitWidth / width;
    double scaleY = fitHeight / height;

    double scale = Math.min(scaleX, scaleY);
    scale = Math.floor(scale * 0.99 * 8) / 8;

    updateCanvasScale(scale);
  }

  @Override public void repaint() {
    updateCanvasFit();
    if (painting.compareAndSet(false, true)) {
      forceRepaint();
    }
  }

  public void forceRepaint() {
    painting.set(true);
    renderManager.withBufferedImage(bitmap -> {
      if (bitmap.width == (int) image.getWidth()
          && bitmap.height == (int) image.getHeight()) {
        image.getPixelWriter().setPixels(0, 0, bitmap.width, bitmap.height, PIXEL_FORMAT,
            bitmap.data, 0, bitmap.width);
      }
    });
    Platform.runLater(() -> {
      GraphicsContext gc = canvas.getGraphicsContext2D();
      gc.drawImage(image, 0, 0);
      painting.set(false);
    });
  }

  public void setRenderListener(RenderStatusListener renderListener) {
    this.renderListener = renderListener;
  }

  @Override public void sceneStatus(String status) {
    Platform.runLater(() -> {
      Point2D offset = localToScene(0, 0);
      tooltip.setText(status);
      tooltip.show(this,
          offset.getX() + getScene().getX() + getScene().getWindow().getX(),
          offset.getY() + getScene().getY() + getScene().getWindow().getY() + getHeight());
      noChunksLabel.setBackground(new Background(new BackgroundFill(Paint.valueOf("#00000099"),null,null)));
      noChunksLabel.setPadding(new Insets(20));
      noChunksLabel.setVisible(!renderScene.haveLoadedChunks());
    });
  }

  /**
   * Should only be called on the JavaFX application thread.
   */
  public void setCanvasSize(int width, int height) {
    canvas.setWidth(width);
    canvas.setHeight(height);
    if (image == null || width != image.getWidth() || height != image.getHeight()) {
      image = new WritableImage(width, height);
    }

    if (fitToScreen) {
      updateCanvasFit();
    } else {
      updateCanvasScale(canvas.getScaleX());
    }
  }
}
