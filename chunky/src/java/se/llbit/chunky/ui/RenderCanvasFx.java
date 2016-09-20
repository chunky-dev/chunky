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
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.RenderStatusListener;
import se.llbit.chunky.renderer.Repaintable;
import se.llbit.chunky.renderer.Renderer;
import se.llbit.chunky.renderer.SceneStatusListener;
import se.llbit.chunky.renderer.scene.Camera;

import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Shows the current render preview.
 */
public class RenderCanvasFx extends Stage implements Repaintable, SceneStatusListener {
  private static final WritablePixelFormat<IntBuffer> PIXEL_FORMAT =
      PixelFormat.getIntArgbInstance();

  private WritableImage image;

  private final AtomicBoolean painting = new AtomicBoolean(false);
  private final Canvas canvas;
  private final Pane canvasPane;
  private final ScrollPane scrollPane;
  private final Renderer renderer;
  private int lastX;
  private int lastY;
  private Tooltip tooltip = new Tooltip();

  private RenderStatusListener renderListener;

  public RenderCanvasFx(se.llbit.chunky.renderer.scene.Scene scene, Renderer renderer) {
    setTitle("Render Preview");
    getIcons().add(new Image(getClass().getResourceAsStream("/chunky-icon.png")));
    this.renderer = renderer;
    renderer.addSceneStatusListener(this);

    tooltip.setAutoHide(true);
    tooltip.setConsumeAutoHidingEvents(false);
    tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);

    Pane parentPane = new Pane();
    canvasPane = new Pane();
    canvas = new Canvas();
    synchronized (scene) {
      canvas.setWidth(scene.width);
      canvas.setHeight(scene.height);
      image = new WritableImage(scene.width, scene.height);
    }
    Line hGuide1 = new Line();
    Line hGuide2 = new Line();
    Line vGuide1 = new Line();
    Line vGuide2 = new Line();
    canvasPane.getChildren().addAll(canvas, hGuide1, hGuide2, vGuide1, vGuide2);
    parentPane.getChildren().add(canvasPane);

    scrollPane = new ScrollPane();
    scrollPane.setContent(parentPane);

    hGuide1.setVisible(false);
    hGuide1.setStroke(Color.rgb(0, 0, 0, 0.5));
    hGuide1.setStartX(0);
    hGuide1.endXProperty().bind(canvasPane.widthProperty());
    hGuide1.startYProperty().bind(canvasPane.heightProperty().divide(3));
    hGuide1.endYProperty().bind(hGuide1.startYProperty());

    hGuide2.setVisible(false);
    hGuide2.setStroke(Color.rgb(0, 0, 0, 0.5));
    hGuide2.setStartX(0);
    hGuide2.endXProperty().bind(canvasPane.widthProperty());
    hGuide2.startYProperty().bind(canvasPane.heightProperty().multiply(2 / 3.0));
    hGuide2.endYProperty().bind(hGuide2.startYProperty());

    vGuide1.setVisible(false);
    vGuide1.setStroke(Color.rgb(0, 0, 0, 0.5));
    vGuide1.setStartY(0);
    vGuide1.endYProperty().bind(canvasPane.heightProperty());
    vGuide1.startXProperty().bind(canvasPane.widthProperty().divide(3));
    vGuide1.endXProperty().bind(vGuide1.startXProperty());

    vGuide2.setVisible(false);
    vGuide2.setStroke(Color.rgb(0, 0, 0, 0.5));
    vGuide2.setStartY(0);
    vGuide2.endYProperty().bind(canvasPane.heightProperty());
    vGuide2.startXProperty().bind(canvasPane.widthProperty().multiply(2 / 3.0));
    vGuide2.endXProperty().bind(vGuide2.startXProperty());

    canvasPane.translateXProperty().bind(
        scrollPane.widthProperty().subtract(canvasPane.widthProperty()).divide(2));
    canvasPane.translateYProperty().bind(
        scrollPane.heightProperty().subtract(canvasPane.heightProperty()).divide(2));

    setScene(new Scene(scrollPane));
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
    CheckMenuItem showGuides = new CheckMenuItem("Show guides");
    showGuides.setSelected(false);
    showGuides.selectedProperty().addListener((observable, oldValue, newValue) -> {
      hGuide1.setVisible(newValue);
      hGuide2.setVisible(newValue);
      vGuide1.setVisible(newValue);
      vGuide2.setVisible(newValue);
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
    contextMenu.getItems().addAll(showGuides, canvasScale);

    canvas.setOnMouseClicked(event -> {
      if (event.getButton() == MouseButton.SECONDARY) {
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
    addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      double modifier = 1;
      if (e.isControlDown()) {
        modifier *= 100;
      }
      if (e.isShiftDown()) {
        modifier *= 0.1;
      }
      switch (e.getCode()) {
        case ESCAPE:
          hide();
          e.consume();
          break;
        case W:
          scene.camera().moveForward(modifier);
          e.consume();
          break;
        case S:
          scene.camera().moveBackward(modifier);
          e.consume();
          break;
        case A:
          scene.camera().strafeLeft(modifier);
          e.consume();
          break;
        case D:
          scene.camera().strafeRight(modifier);
          e.consume();
          break;
        case R:
          scene.camera().moveUp(modifier);
          e.consume();
          break;
        case F:
          scene.camera().moveDown(modifier);
          e.consume();
          break;
        case J:
          scene.camera().moveBackward(modifier);
          e.consume();
          break;
        case K:
          scene.camera().moveForward(modifier);
          e.consume();
          break;
        case SPACE:
          synchronized (scene) {
            if (scene.getMode() == RenderMode.RENDERING) {
              scene.pauseRender();
            } else {
              scene.startRender();
            }
            renderListener.renderStateChanged(scene.getMode());
          }
          e.consume();
          break;
      }
    });
    setOnShowing(e -> {
      renderer.setCanvas(this);
      // Note: the buffer update flag must be copied in SceneProvider.withSceneProtected().
      scene.setBufferFinalization(true);
    });
    setOnHiding(e -> {
      // Note: the buffer update flag must be copied in SceneProvider.withSceneProtected().
      scene.setBufferFinalization(false);
      renderer.setCanvas(RenderManager.EMPTY_CANVAS);
    });
    repaint();
  }

  private void updateCanvasScale(double scale) {
    double scaledWidth = canvas.getWidth() * scale;
    double scaledHeight = canvas.getHeight() * scale;
    canvas.setLayoutX((scaledWidth - canvas.getWidth()) / 2);
    canvas.setLayoutY((scaledHeight - canvas.getHeight()) / 2);
    canvas.setScaleX(scale);
    canvas.setScaleY(scale);
    canvasPane.setPrefWidth(scaledWidth);
    canvasPane.setPrefHeight(scaledHeight);
    scrollPane.setPrefViewportWidth(scaledWidth);
    scrollPane.setPrefViewportHeight(scaledHeight);
    double xInset = getWidth() - scrollPane.getWidth() + 2;
    double yInset = getHeight() - scrollPane.getHeight() + 2;
    setWidth(scaledWidth + xInset);
    setHeight(scaledHeight + yInset);
  }

  @Override public void repaint() {
    if (painting.compareAndSet(false, true)) {
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
  }

  public void setRenderListener(RenderStatusListener renderListener) {
    this.renderListener = renderListener;
  }

  @Override public void sceneStatus(String status) {
    Platform.runLater(() -> {
      if (isFocused()) {
        tooltip.setText(status);
        tooltip.show(this, getX(), getY() + getHeight());
      }
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
