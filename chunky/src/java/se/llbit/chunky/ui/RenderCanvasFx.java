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

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.PopupWindow;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.RenderStatusListener;
import se.llbit.chunky.renderer.Renderer;
import se.llbit.chunky.renderer.Repaintable;
import se.llbit.chunky.renderer.SceneStatusListener;
import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.math.Vector2;

import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Shows the current render preview.
 */
public class RenderCanvasFx extends ScrollPane implements Repaintable, SceneStatusListener {
  private static final WritablePixelFormat<IntBuffer> PIXEL_FORMAT =
      PixelFormat.getIntArgbInstance();

  private final se.llbit.chunky.renderer.scene.Scene renderScene;

  private WritableImage image;

  private final AtomicBoolean painting = new AtomicBoolean(false);
  private final Canvas canvas;
  private final StackPane canvasPane;
  private final Renderer renderer;
  private int lastX;
  private int lastY;
  private Vector2 target = new Vector2(0, 0);
  private Tooltip tooltip = new Tooltip();

  private RenderStatusListener renderListener;

  public RenderCanvasFx(se.llbit.chunky.renderer.scene.Scene scene, Renderer renderer) {
    this.renderScene = scene;
    this.renderer = renderer;
    renderer.addSceneStatusListener(this);

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

    canvas.setOnMousePressed(e -> {
      lastX = (int) e.getX();
      lastY = (int) e.getY();
    });

    canvas.setOnMouseDragged(e -> {
      int dx = lastX - (int) e.getX();
      int dy = lastY - (int) e.getY();
      lastX = (int) e.getX();
      lastY = (int) e.getY();
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
    Menu canvasScale = new Menu("Canvas scale");
    ToggleGroup scaleGroup = new ToggleGroup();
    for (int percent : new int[] { 25, 50, 75, 100, 150, 200, 300, 400 }) {
      RadioMenuItem item = new RadioMenuItem(String.format("%d%%", percent));
      item.setToggleGroup(scaleGroup);
      if (percent == 100) {
        item.setSelected(true);
      }
      item.setOnAction(e -> updateCanvasScale(percent / 100.0));
      canvasScale.getItems().add(item);
    }
    contextMenu.getItems().addAll(setTarget, canvasScale);

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
      double diff = -e.getDeltaY() / e.getMultiplierY();
      Camera camera = scene.camera();
      double value = camera.getFov();
      double scale = camera.getMaxFoV() - camera.getMinFoV();
      double offset = value / scale;
      double newValue = scale * Math.exp(Math.log(offset) + 0.1 * diff);
      if (!Double.isNaN(newValue) && !Double.isInfinite(newValue)) {
        camera.setFoV(newValue);
      }
    });
    renderer.setCanvas(this);

    viewportBoundsProperty().addListener((observable, oldValue, newValue) -> updateCanvasPane());

    // Register key event listener for keyboard navigation.
    canvasPane.setFocusTraversable(true);
    canvasPane.setOnMouseClicked(event -> canvasPane.requestFocus());
    canvasPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (!isVisible()) {
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
        case J:
          renderScene.camera().moveBackward(modifier);
          e.consume();
          break;
        case K:
          renderScene.camera().moveForward(modifier);
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

  private void updateCanvasPane() {
    Bounds bounds = getViewportBounds();
    canvasPane.setMinWidth(Math.max(canvas.getWidth() * canvas.getScaleX(), bounds.getWidth()));
    canvasPane.setMinHeight(Math.max(canvas.getHeight() * canvas.getScaleY(), bounds.getHeight()));
  }

  private void updateCanvasScale(double scale) {
    canvas.setScaleX(scale);
    canvas.setScaleY(scale);
    updateCanvasPane();
  }

  @Override public void repaint() {
    if (painting.compareAndSet(false, true)) {
      forceRepaint();
    }
  }

  public void forceRepaint() {
    painting.set(true);
    renderer.withBufferedImage(bitmap -> {
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
    updateCanvasScale(canvas.getScaleX());
  }
}
