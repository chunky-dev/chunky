/* Copyright (c) 2012-2014 Jesper Öqvist <jesper@llbit.se>
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

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.PopupWindow;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.Icon;
import se.llbit.chunky.world.PlayerEntityData;
import se.llbit.chunky.world.World;
import se.llbit.log.Log;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector2;
import se.llbit.math.Vector3;

import java.io.File;
import java.io.IOException;

/**
 * UI component that draws a 2D Minecraft map.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkMap extends Map2D {

  /** Controls the selection area when selecting visible chunks. */
  private static final double CHUNK_SELECT_RADIUS = -8 * 1.4142;

  /**
   * Indicates whether or not the selection rectangle should be drawn.
   */
  protected volatile boolean selectRect = false;

  private final ContextMenu contextMenu = new ContextMenu();
  private final MenuItem moveCameraHere;
  private final MenuItem selectVisible;

  private volatile ChunkPosition start = ChunkPosition.get(0, 0);
  private volatile ChunkPosition end = ChunkPosition.get(0, 0);

  public int lastX;
  public int lastY;

  public int clickX;
  public int clickY;

  protected boolean ctrlModifier = false;
  protected boolean shiftModifier = false;

  protected boolean dragging = false;
  protected boolean mouseDown = false;

  public Tooltip tooltip = new Tooltip();

  public ChunkMap(final WorldMapLoader loader, final ChunkyFxController controller) {
    super(loader, controller);

    tooltip.setAutoHide(true);
    tooltip.setConsumeAutoHidingEvents(false);
    tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);

    MenuItem createScene = new MenuItem("New 3D scene...");
    createScene.setGraphic(new ImageView(Icon.sky.fxImage()));
    createScene.setOnAction(event -> controller.createNew3DScene());

    MenuItem loadScene = new MenuItem("Load scene...");
    loadScene.setGraphic(new ImageView(Icon.load.fxImage()));
    loadScene.setOnAction(event -> controller.loadScene());

    MenuItem clearSelection = new MenuItem("Clear selection");
    clearSelection.setGraphic(new ImageView(Icon.clear.fxImage()));
    clearSelection.setOnAction(event -> loader.clearChunkSelection());

    moveCameraHere = new MenuItem("Move camera here");
    moveCameraHere.setOnAction(event -> {
      ChunkView theView = new ChunkView(view);  // Make thread-local copy.
      double scale = theView.scale;
      double x = theView.x + (clickX - getWidth() / 2) / scale;
      double z = theView.z + (clickY - getHeight() / 2) / scale;
      controller.moveCameraTo(x * 16, z * 16);
    });

    selectVisible = new MenuItem("Select visible chunks");
    selectVisible.setGraphic(new ImageView(Icon.eye.fxImage()));
    selectVisible.setOnAction(event -> {
      ChunkView mapView = new ChunkView(view);  // Make thread-local copy.
      if (controller.getChunky().sceneInitialized()) {
        controller.getChunky().getRenderController().getSceneProvider().withSceneProtected(
            scene -> selectVisibleChunks(mapView, loader, scene));
      }
    });

    contextMenu.getItems()
        .addAll(createScene, loadScene, clearSelection, moveCameraHere, selectVisible);
  }

  /**
   * Draws a visualization of the 3D camera view on the 2D map.
   */
  protected void drawViewBounds(Canvas canvas) {
    ChunkView mapView = new ChunkView(view);  // Make thread-local copy.
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    if (controller.hasActiveRenderControls()) {
      controller.getChunky().getRenderController().getSceneProvider().withSceneProtected(
          scene -> drawViewBounds(gc, mapView, scene));
    }
  }

  protected synchronized void selectWithinRect() {
    if (selectRect) {
      ChunkPosition cp0 = start;
      ChunkPosition cp1 = end;
      int x0 = Math.min(cp0.x, cp1.x);
      int x1 = Math.max(cp0.x, cp1.x);
      int z0 = Math.min(cp0.z, cp1.z);
      int z1 = Math.max(cp0.z, cp1.z);
      if (ctrlModifier) {
        mapLoader.deselectChunks(x0, x1, z0, z1);
      } else {
        mapLoader.selectChunks(x0, x1, z0, z1);
      }
    }
  }

  protected void clearSelectionRect() {
    if (selectRect) {
      selectRect = false;
      repaintDeferred();
    }
  }

  /**
   * Draw the selection rectangle or chunk hover rectangle.
   */
  private void drawSelectionRect(GraphicsContext gc) {
    ChunkView mapView = new ChunkView(view);  // Make thread-local copy.

    ChunkPosition cp = end;
    gc.setStroke(javafx.scene.paint.Color.RED);

    if (selectRect) {
      ChunkPosition cp0 = start;
      ChunkPosition cp1 = end;
      int x0 = Math.min(cp0.x, cp1.x);
      int x1 = Math.max(cp0.x, cp1.x);
      int z0 = Math.min(cp0.z, cp1.z);
      int z1 = Math.max(cp0.z, cp1.z);
      x0 = (int) (mapView.scale * (x0 - mapView.x0));
      z0 = (int) (mapView.scale * (z0 - mapView.z0));
      x1 = (int) (mapView.scale * (x1 - mapView.x0 + 1));
      z1 = (int) (mapView.scale * (z1 - mapView.z0 + 1));
      gc.strokeRect(x0, z0, x1 - x0, z1 - z0);
    } else {
      // Test if hovered chunk is visible.
      if (mapView.isChunkVisible(cp)) {

        if (mapView.scale >= 16) {
          int x0 = (int) (mapView.scale * (cp.x - mapView.x0));
          int y0 = (int) (mapView.scale * (cp.z - mapView.z0));
          int blockScale = mapView.scale;
          gc.strokeRect(x0, y0, blockScale, blockScale);
        } else {
          // Hovered region.
          int rx = cp.x >> 5;
          int rz = cp.z >> 5;
          int x0 = (int) (mapView.scale * (rx * 32 - mapView.x0));
          int y0 = (int) (mapView.scale * (rz * 32 - mapView.z0));
          gc.strokeRect(x0, y0, mapView.scale * 32, mapView.scale * 32);
        }
      }
    }
  }

  /**
   * Render the current view to a PNG image.
   */
  public void renderView(File targetFile, ProgressTracker progress) {
    if (!progress.isBusy()) {
      if (progress.tryStartJob()) {
        progress.setJobName("PNG export");
        progress.setJobSize(1);
        try {
          mapBuffer.renderPng(targetFile);
        } catch (IOException e) {
          Log.error("Failed to export PNG.", e);
        }
        progress.finishJob();
      }
    }
  }

  public int getWidth() {
    return mapLoader.getMapView().width;
  }

  public int getHeight() {
    return mapLoader.getMapView().height;
  }

  public void onKeyPressed(KeyEvent keyEvent) {
    switch (keyEvent.getCode()) {
      case CONTROL:
        ctrlModifier = true;
        break;
      case SHIFT:
        shiftModifier = true;
        break;
    }
  }

  public void onKeyReleased(KeyEvent keyEvent) {
    switch (keyEvent.getCode()) {
      case CONTROL:
        ctrlModifier = false;
        break;
      case SHIFT:
        shiftModifier = false;
        break;
    }
  }

  public void onMouseDragged(MouseEvent event) {
    int dx = lastX - (int) event.getX();
    int dy = lastY - (int) event.getY();
    lastX = (int) event.getX();
    lastY = (int) event.getY();

    ChunkPosition chunk = getChunk(event);
    if (chunk != end) {
      end = chunk;
      repaintDirect();
    }

    if (selectRect || !dragging && shiftModifier) {
      selectRect = true;
    } else {
      dragging = true;
      mapLoader.viewDragged(dx, dy);
    }
  }

  private ChunkPosition getChunk(MouseEvent event) {
    ChunkView mapView = new ChunkView(view);  // Make thread-local copy.

    double scale = mapView.scale;
    double x = mapView.x + (event.getX() - getWidth() / 2.0) / scale;
    double z = mapView.z + (event.getY() - getHeight() / 2.0) / scale;
    int cx = (int) QuickMath.floor(x);
    int cz = (int) QuickMath.floor(z);
    int bx = (int) QuickMath.floor((x - cx) * 16);
    int bz = (int) QuickMath.floor((z - cz) * 16);
    bx = Math.max(0, Math.min(Chunk.X_MAX - 1, bx));
    bz = Math.max(0, Math.min(Chunk.Z_MAX - 1, bz));
    ChunkPosition cp = ChunkPosition.get(cx, cz);
    if (!mouseDown) {
      Chunk hoveredChunk = mapLoader.getWorld().getChunk(cp);
      if (!hoveredChunk.isEmpty()) {
        tooltip.setText(
            String.format("%s, %s", hoveredChunk.toString(), hoveredChunk.biomeAt(bx, bz)));
      } else {
        tooltip.setText(hoveredChunk.toString());
      }
      Canvas mapOverlay = controller.getMapOverlay();
      Scene scene = mapOverlay.getScene();
      if (mapOverlay.isFocused()) {
        tooltip.show(scene.getWindow(), scene.getWindow().getX(),
            scene.getWindow().getY() + scene.getWindow().getHeight());
      }
    }
    return cp;
  }

  public void onMousePressed(MouseEvent event) {
    lastX = (int) event.getX();
    lastY = (int) event.getY();
    if (event.getButton() == MouseButton.SECONDARY) {
      clickX = lastX;
      clickY = lastY;
      moveCameraHere.setVisible(controller.hasActiveRenderControls());
      selectVisible.setVisible(controller.hasActiveRenderControls());
      contextMenu.show(controller.getMapOverlay(), event.getScreenX(), event.getScreenY());
    } else {
      if (contextMenu.isShowing()) {
        contextMenu.hide();
      } else {
        mouseDown = true;
      }
    }
  }

  public void onMouseReleased(MouseEvent event) {
    if (!mouseDown) {
      return;
    }
    mouseDown = false;

    if (!selectRect) {
      if (!dragging) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        ChunkView theView = mapLoader.getMapView();
        double scale = theView.scale;
        int cx = (int) QuickMath.floor(theView.x + (x - getWidth() / 2) / scale);
        int cz = (int) QuickMath.floor(theView.z + (y - getHeight() / 2) / scale);

        if (theView.scale >= 16) {
          mapLoader.toggleChunkSelection(cx, cz);
        } else {
          mapLoader.selectRegion(cx, cz);
        }
      }
    } else {
      selectWithinRect();
      clearSelectionRect();
    }
    start = end;
    dragging = false;
  }

  public void onMouseMoved(MouseEvent event) {
    ChunkPosition chunk = getChunk(event);
    if (chunk != start) {
      start = chunk;
      end = chunk;
      repaintDirect();
    }
    lastX = (int) event.getX();
    lastY = (int) event.getY();
  }

  public void onScroll(ScrollEvent event) {
    int diff = (int) (-event.getDeltaY() / event.getMultiplierY());

    if (ctrlModifier) {
      mapLoader.setLayer(mapLoader.getLayer() + diff);
    } else {
      int scale = mapLoader.getScale();
      if ((scale - diff) <= 16) {
        mapLoader.setScale(scale - diff);
      } else if ((scale - diff * 4) <= 64) {
        mapLoader.setScale(scale - diff * 4);
      } else if ((scale - diff * 16) <= 128) {
        mapLoader.setScale(scale - diff * 16);
      } else {
        mapLoader.setScale(scale - diff * 64);
      }
    }
  }

  @Override protected void repaint(GraphicsContext gc) {
    super.repaint(gc);
    drawPlayers(gc);
    drawSpawn(gc);
    drawSelectionRect(gc);
    if (controller.hasActiveRenderControls()) {
      drawViewBounds(controller.getMapOverlay());
    }
  }

  private void drawPlayers(GraphicsContext gc) {
    ChunkView mapView = new ChunkView(view);  // Make thread-local copy.
    World world = mapLoader.getWorld();
    double blockScale = mapView.scale / 16.;
    for (PlayerEntityData player : world.getPlayerPositions()) {
      int px = (int) QuickMath.floor(player.x * blockScale);
      int py = (int) QuickMath.floor(player.y);
      int pz = (int) QuickMath.floor(player.z * blockScale);
      int ppx = px - (int) QuickMath.floor(mapView.x0 * mapView.scale);
      int ppy = pz - (int) QuickMath.floor(mapView.z0 * mapView.scale);
      int pw = (int) QuickMath.max(8, QuickMath.min(16, blockScale * 2));
      ppx = Math.min(mapView.width - pw, Math.max(0, ppx - pw / 2));
      ppy = Math.min(mapView.height - pw, Math.max(0, ppy - pw / 2));

      if (py == mapLoader.getLayer()) {
        gc.drawImage(Icon.face.fxImage(), ppx, ppy, pw, pw);
      } else {
        gc.drawImage(Icon.face_t.fxImage(), ppx, ppy, pw, pw);
      }
    }
  }

  private void drawSpawn(GraphicsContext gc) {
    ChunkView mapView = new ChunkView(view);  // Make thread-local copy.
    World world = mapLoader.getWorld();
    double blockScale = mapView.scale / 16.;
    if (!world.haveSpawnPos()) {
      return;
    }
    int px = (int) QuickMath.floor(world.spawnPosX() * blockScale);
    int py = (int) QuickMath.floor(world.spawnPosY());
    int pz = (int) QuickMath.floor(world.spawnPosZ() * blockScale);
    int ppx = px - (int) QuickMath.floor(mapView.x0 * mapView.scale);
    int ppy = pz - (int) QuickMath.floor(mapView.z0 * mapView.scale);
    int pw = (int) QuickMath.max(8, QuickMath.min(16, blockScale * 2));
    ppx = Math.min(mapView.width - pw, Math.max(0, ppx - pw / 2));
    ppy = Math.min(mapView.height - pw, Math.max(0, ppy - pw / 2));

    if (py == mapLoader.getLayer()) {
      gc.drawImage(Icon.home.fxImage(), ppx, ppy, pw, pw);
    } else {
      gc.drawImage(Icon.home_t.fxImage(), ppx, ppy, pw, pw);
    }
  }

  @Override public void cameraPositionUpdated() {
    drawViewBounds(controller.getMapOverlay());
  }

  public void selectVisibleChunks(ChunkView cv, WorldMapLoader loader,
      se.llbit.chunky.renderer.scene.Scene scene) {
    Camera camera = scene.camera();
    int width = scene.canvasWidth();
    int height = scene.canvasHeight();

    double halfWidth = width / (2.0 * height);

    Vector3 o = new Vector3(camera.getPosition());

    Ray ray = new Ray();
    Vector3[] corners = new Vector3[4];

    camera.calcViewRay(ray, -halfWidth, -0.5);
    corners[0] = new Vector3(ray.d);
    camera.calcViewRay(ray, -halfWidth, 0.5);
    corners[1] = new Vector3(ray.d);
    camera.calcViewRay(ray, halfWidth, 0.5);
    corners[2] = new Vector3(ray.d);
    camera.calcViewRay(ray, halfWidth, -0.5);
    corners[3] = new Vector3(ray.d);

    Vector3[] norm = new Vector3[4];
    norm[0] = new Vector3();
    norm[0].cross(corners[1], corners[0]);
    norm[0].normalize();
    norm[1] = new Vector3();
    norm[1].cross(corners[2], corners[1]);
    norm[1].normalize();
    norm[2] = new Vector3();
    norm[2].cross(corners[3], corners[2]);
    norm[2].normalize();
    norm[3] = new Vector3();
    norm[3].cross(corners[0], corners[3]);
    norm[3].normalize();

    for (int x = cv.px0; x <= cv.px1; ++x) {
      for (int z = cv.pz0; z <= cv.pz1; ++z) {
        // Chunk top center position:
        Vector3 pos = new Vector3((x + 0.5) * 16, 63, (z + 0.5) * 16);
        pos.sub(o);
        if (norm[0].dot(pos) > CHUNK_SELECT_RADIUS && norm[1].dot(pos) > CHUNK_SELECT_RADIUS
            && norm[2].dot(pos) > CHUNK_SELECT_RADIUS && norm[3].dot(pos) > CHUNK_SELECT_RADIUS) {
          loader.selectChunk(x, z);
        }
      }
    }
  }

  /**
   * Draws a visualization of the camera view from the specified scene on the map.
   */
  public static void drawViewBounds(GraphicsContext gc, ChunkView cv,
      se.llbit.chunky.renderer.scene.Scene scene) {
    Camera camera = scene.camera();
    int width = scene.canvasWidth();
    int height = scene.canvasHeight();

    double halfWidth = width / (2.0 * height);

    Ray ray = new Ray();

    Vector3[] corners = new Vector3[4];
    Vector2[] bounds = new Vector2[4];

    camera.calcViewRay(ray, -halfWidth, -0.5);
    corners[0] = new Vector3(ray.d);
    bounds[0] = findMapPos(ray, cv);

    camera.calcViewRay(ray, -halfWidth, 0.5);
    corners[1] = new Vector3(ray.d);
    bounds[1] = findMapPos(ray, cv);

    camera.calcViewRay(ray, halfWidth, 0.5);
    corners[2] = new Vector3(ray.d);
    bounds[2] = findMapPos(ray, cv);

    camera.calcViewRay(ray, halfWidth, -0.5);
    corners[3] = new Vector3(ray.d);
    bounds[3] = findMapPos(ray, cv);

    gc.setStroke(javafx.scene.paint.Color.YELLOW);
    for (int i = 0; i < 4; ++i) {
      int j = (i + 1) % 4;
      if (bounds[i] != null && bounds[j] != null) {
        drawLine(gc, bounds[i], bounds[j]);
      } else if (bounds[i] != null && bounds[j] == null) {
        drawExtended(gc, cv, bounds, corners, i, j);
      } else if (bounds[j] != null && bounds[i] == null) {
        drawExtended(gc, cv, bounds, corners, j, i);
      }
    }

    int ox = (int) (cv.scale * (ray.o.x / 16 - cv.x0));
    int oy = (int) (cv.scale * (ray.o.z / 16 - cv.z0));

    // Draw the camera facing direction indicator.
    camera.calcViewRay(ray, 0, 0);
    Vector3 o = new Vector3(ray.o);
    o.x /= 16;
    o.z /= 16;
    o.scaleAdd(1, ray.d);
    int x = (int) (cv.scale * (o.x - cv.x0));
    int y = (int) (cv.scale * (o.z - cv.z0));
    gc.strokeLine(ox, oy, x, y);

    // Draw the camera icon.
    gc.drawImage(Icon.camera.fxImage(), ox - 8, oy - 8);
  }

  /**
   * Find the point where the ray intersects the ground (y=63).
   */
  private static Vector2 findMapPos(Ray ray, ChunkView cv) {
    if (ray.d.y < 0 && ray.o.y > 63 || ray.d.y > 0 && ray.o.y < 63) {
      // Ray intersects ground.
      double d = (63 - ray.o.y) / ray.d.y;
      Vector3 pos = new Vector3();
      pos.scaleAdd(d, ray.d, ray.o);

      return new Vector2(cv.scale * (pos.x / 16 - cv.x0), cv.scale * (pos.z / 16 - cv.z0));
    } else {
      return null;
    }
  }

  private static void drawExtended(GraphicsContext gc, ChunkView cv, Vector2[] bounds, Vector3[] corners,
      int i, int j) {
    Vector3 c = new Vector3();
    c.cross(corners[i], corners[j]);
    Vector2 c2 = new Vector2();
    c2.x = c.z;
    c2.y = -c.x;
    c2.normalize();
    if (corners[i].y > 0) {
      c2.scale(-1);
    }
    double tNear = Double.POSITIVE_INFINITY;
    double t = -bounds[i].x / c2.x;
    if (t > 0 && t < tNear) {
      tNear = t;
    }
    t = (cv.scale * (cv.x1 - cv.x0) - bounds[i].x) / c2.x;
    if (t > 0 && t < tNear) {
      tNear = t;
    }
    t = -bounds[i].y / c2.y;
    if (t > 0 && t < tNear) {
      tNear = t;
    }
    t = (cv.scale * (cv.z1 - cv.z0) - bounds[i].y) / c2.y;
    if (t > 0 && t < tNear) {
      tNear = t;
    }
    if (tNear != Double.POSITIVE_INFINITY) {
      Vector2 p = new Vector2(bounds[i]);
      p.scaleAdd(tNear, c2);
      drawLine(gc, p, bounds[i]);
    }
  }

  private static void drawLine(GraphicsContext gc, Vector2 v1, Vector2 v2) {
    int x1 = (int) v1.x;
    int y1 = (int) v1.y;
    int x2 = (int) v2.x;
    int y2 = (int) v2.y;
    gc.strokeLine(x1, y1, x2, y2);
  }
}
