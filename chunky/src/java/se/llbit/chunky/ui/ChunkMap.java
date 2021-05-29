/* Copyright (c) 2012-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2012-2021 Chunky contributors
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
import javafx.geometry.Point2D;
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
import se.llbit.chunky.map.MapBuffer;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.renderer.CameraViewListener;
import se.llbit.chunky.renderer.ChunkViewListener;
import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.renderer.scene.SceneManager;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkSelectionTracker;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.Icon;
import se.llbit.chunky.world.PlayerEntityData;
import se.llbit.chunky.world.World;
import se.llbit.chunky.world.listeners.ChunkUpdateListener;
import se.llbit.log.Log;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector2;
import se.llbit.math.Vector3;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * UI component for the 2D world map.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkMap implements ChunkUpdateListener, ChunkViewListener, CameraViewListener {
  /** Minimum time between JavaFX draws due to chunk updates. */
  private final long MAX_CHUNK_UPDATE_RATE = 1000/3;

  /** Controls the selection area when selecting visible chunks. */
  private static final double CHUNK_SELECT_RADIUS = -8 * 1.4142;
  protected final WorldMapLoader mapLoader;
  protected final ChunkyFxController controller;
  protected final ChunkSelectionTracker chunkSelection;
  private MapView mapView;
  protected final MapBuffer mapBuffer;
  protected final ContextMenu contextMenu = new ContextMenu();
  protected final MenuItem moveCameraHere;
  protected final MenuItem selectVisible;
  public Tooltip tooltip = new Tooltip();
  public int lastX;
  public int lastY;
  public int clickX;
  public int clickY;

  /**
   * The map view can be modified by external threads.
   * Objects of type ChunkView are immutable, but each time
   * we need to read the view we should only read the view
   * reference once.
   */
  protected volatile ChunkView view = ChunkView.EMPTY;

  /**
   * Indicates whether or not the selection rectangle should be drawn.
   */
  protected volatile boolean selectRect = false;
  protected volatile ChunkPosition start = ChunkPosition.get(0, 0);
  protected volatile ChunkPosition end = ChunkPosition.get(0, 0);
  protected boolean ctrlModifier = false;
  protected boolean shiftModifier = false;
  protected boolean dragging = false;
  protected boolean mouseDown = false;

  private final Canvas canvas;
  private final Canvas mapOverlay;

  private static final int VIEW_MAX_UPDATES = Math.max(Integer.parseInt(System.getProperty("chunky.mapviewfps", "30")), 1);

  private volatile long lastUpdate = System.nanoTime();
  private volatile boolean viewUpdateScheduled = false;
  private volatile boolean repaintQueued = false;
  private volatile boolean scheduledUpdate = false;
  private volatile long lastRedraw = 0;
  private Runnable onViewDragged = () -> {};
  private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  public ChunkMap(WorldMapLoader loader, ChunkyFxController controller,
      MapView mapView, ChunkSelectionTracker chunkSelection,
      Canvas canvas, Canvas mapOverlay) {
    this.mapLoader = loader;
    this.controller = controller;
    this.mapView = mapView;
    this.chunkSelection = chunkSelection;
    this.canvas = canvas;
    this.mapOverlay = mapOverlay;

    // Register to listen for events:
    mapView.addViewListener(this);
    chunkSelection.addChunkUpdateListener(this);

    mapBuffer = new MapBuffer();
    moveCameraHere = new MenuItem("Move camera here");
    selectVisible = new MenuItem("Select camera-visible chunks");

    tooltip.setAutoHide(true);
    tooltip.setConsumeAutoHidingEvents(false);
    tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);

    MenuItem clearSelection = new MenuItem("Clear selection");
    clearSelection.setGraphic(new ImageView(Icon.clear.fxImage()));
    clearSelection.setOnAction(event -> chunkSelection.clearSelection());
    clearSelection.setDisable(chunkSelection.size() == 0);

    MenuItem newScene = new MenuItem("New scene from selection");
    newScene.setGraphic(new ImageView(Icon.sky.fxImage()));
    newScene.setOnAction(event -> {
      SceneManager sceneManager = controller.getRenderController().getSceneManager();
      sceneManager
          .loadFreshChunks(mapLoader.getWorld(), controller.getChunkSelection().getSelection());
    });
    newScene.setDisable(chunkSelection.size() == 0);
    chunkSelection.addSelectionListener(() -> {
      boolean noChunksSelected = chunkSelection.size() == 0;
      clearSelection.setDisable(noChunksSelected);
      newScene.setDisable(noChunksSelected);
    });

    moveCameraHere.setOnAction(event -> {
      ChunkView theView = new ChunkView(view);  // Make thread-local copy.
      double scale = theView.scale;
      double x = theView.x + (clickX - getWidth() / 2) / scale;
      double z = theView.z + (clickY - getHeight() / 2) / scale;
      controller.moveCameraTo(x * 16, z * 16);
    });

    selectVisible.setGraphic(new ImageView(Icon.eye.fxImage()));
    selectVisible.setOnAction(event -> {
      ChunkView chunkView = new ChunkView(view);  // Make thread-local copy.
      if (controller.getChunky().sceneInitialized()) {
        controller.getChunky().getRenderController().getSceneProvider().withSceneProtected(
            scene -> selectVisibleChunks(chunkView, scene));
      }
    });

    contextMenu.getItems()
        .addAll(newScene, clearSelection, moveCameraHere, selectVisible);
  }

  @Override public void chunkUpdated(ChunkPosition chunk) {
    if (view.chunkScale >= 16) {
      mapBuffer.drawTile(mapLoader, chunk, chunkSelection);
      repaintRatelimited();
    } else {
      regionUpdated(chunk.getRegionPosition());
    }
  }

  protected final void repaintDirect() {
    if (!repaintQueued) {
      repaint(canvas.getGraphicsContext2D());
    }
  }

  protected final void repaintDeferred() {
    if (!repaintQueued) {
      repaintQueued = true;
      Platform.runLater(() -> {
        repaint(canvas.getGraphicsContext2D());
        repaintQueued = false;
      });
    }
  }

  protected final void repaintRatelimited() {
    if (lastRedraw == -1) {
      return;
    }

    long delay = (lastRedraw + MAX_CHUNK_UPDATE_RATE) - System.currentTimeMillis();
    if (delay > 0) {

      // Prevent redraw from occurring until this is done.
      lastRedraw = -1;

      executor.schedule(() -> {
        lastRedraw = System.currentTimeMillis();
        repaintDeferred();
      }, delay, TimeUnit.MILLISECONDS);
    } else {
      // No need to be ratelimited, redraw now
      lastRedraw = System.currentTimeMillis();
      repaintDeferred();
    }
  }

  /**
   * Draws a visualization of the 3D camera view on the 2D map.
   */
  protected void drawViewBounds(Canvas canvas) {
    ChunkView mapView = new ChunkView(view);  // Make thread-local copy.
    GraphicsContext gc = canvas.getGraphicsContext2D();

    // `withSceneProtected` will block for a long time when a new scene is loaded. This bocks in the JavaFX thread and
    // freezes the user interface. Here we check if there has already been an update scheduled, and if not will schedule
    // one. Draw view bounds must be run on the JavaFX thread.
    if (!scheduledUpdate) {
      scheduledUpdate = true;
      executor.submit(() -> controller.getChunky().getRenderController().getSceneProvider().withSceneProtected(
        scene -> Platform.runLater(() -> {
          gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
          ChunkMap.drawViewBounds(gc, mapView, scene);
          scheduledUpdate = false;
        }
      )));
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
        chunkSelection.deselectChunks(x0, z0, x1, z1);
      } else {
        chunkSelection.selectChunks(mapLoader.getWorld(), x0, z0, x1, z1);
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

    double scale = mapView.scale / (float)mapView.chunkScale;

    ChunkPosition cp = end;
    gc.setStroke(javafx.scene.paint.Color.RED);

    if (selectRect) {
      ChunkPosition cp0 = start;
      ChunkPosition cp1 = end;
      int x0 = Math.min(cp0.x, cp1.x);
      int x1 = Math.max(cp0.x, cp1.x);
      int z0 = Math.min(cp0.z, cp1.z);
      int z1 = Math.max(cp0.z, cp1.z);
      x0 = (int) (Math.round((mapView.scale * (x0 - mapView.x0)) / scale - 1) * scale);
      z0 = (int) (Math.round((mapView.scale * (z0 - mapView.z0)) / scale) * scale);
      x1 = (int) (Math.round((mapView.scale * (x1 - mapView.x0 + 1)) / scale - 1) * scale);
      z1 = (int) (Math.round((mapView.scale * (z1 - mapView.z0 + 1)) / scale) * scale);
      gc.strokeRect(x0, z0, x1 - x0, z1 - z0);
    } else {
      // Test if hovered chunk is visible.
      if (mapView.isChunkVisible(cp)) {

        if (mapView.scale >= 16) {
          int x0 = (int) (Math.round((mapView.scale * (cp.x - mapView.x0)) / scale - 1) * scale);
          int y0 = (int) (Math.round((mapView.scale * (cp.z - mapView.z0)) / scale) * scale);
          int blockScale = mapView.scale;
          gc.strokeRect(x0, y0, blockScale, blockScale);
        } else {
          // Hovered region.
          int rx = cp.x >> 5;
          int rz = cp.z >> 5;
          int x0 = (int) (Math.round((mapView.scale * (rx * 32 - mapView.x0)) / scale - 1) * scale);
          int y0 = (int) (Math.round((mapView.scale * (rz * 32 - mapView.z0)) / scale) * scale);
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

  @Override public void regionUpdated(ChunkPosition region) {
    if (view.scale < 16) {
      mapBuffer.drawTile(mapLoader, region, chunkSelection);
      mapLoader.regionUpdated(region);
      repaintRatelimited();
    }
  }

  @Override public void viewUpdated(ChunkView view) {
    if (!viewUpdateScheduled) {
      viewUpdateScheduled = true;
      executor.schedule(() -> {
        lastUpdate = System.nanoTime();
        viewUpdateScheduled = false;

        this.view = view;
        mapBuffer.updateView(view);
        mapBuffer.redrawView(mapLoader, chunkSelection);
        repaintDeferred();
      }, (1_000_000_000 / VIEW_MAX_UPDATES) - (System.nanoTime() - lastUpdate), TimeUnit.NANOSECONDS);
    }
  }

  public int getWidth() {
    return mapView.getMapView().width;
  }

  public int getHeight() {
    return mapView.getMapView().height;
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
      mapView.viewDragged(dx, dy);
      onViewDragged.run();
    }
  }

  private ChunkPosition getChunk(MouseEvent event) {
    ChunkView mapView = new ChunkView(view);  // Make thread-local copy.

    double scale = mapView.scale;
    double x = mapView.x + (event.getX() - getWidth() / 2.0) / scale;
    double z = mapView.z + (event.getY() - getHeight() / 2.0) / scale;
    int cx = (int) QuickMath.floor(x);
    int cz = (int) QuickMath.floor(z);
    int bx = (int) QuickMath.floor((x - cx) * Chunk.X_MAX);
    int bz = (int) QuickMath.floor((z - cz) * Chunk.Z_MAX);
    bx = Math.max(0, Math.min(Chunk.X_MAX - 1, bx));
    bz = Math.max(0, Math.min(Chunk.Z_MAX - 1, bz));
    // Calculate the world block position of the cursor
    int worldBlockX = cx * Chunk.X_MAX + bx;
    int worldBlockZ = cz * Chunk.Z_MAX + bz;
    ChunkPosition cp = ChunkPosition.get(cx, cz);
    if (!mouseDown) {
      Chunk hoveredChunk = mapLoader.getWorld().getChunk(cp);
      if (!hoveredChunk.isEmpty()) {
        tooltip.setText(
            String.format("%s, %s\nBlock: [%s, %s]\n%d chunks selected", hoveredChunk.toString(), hoveredChunk.biomeAt(bx, bz), worldBlockX, worldBlockZ, chunkSelection.size()));
      } else {
        tooltip.setText(
            String.format("%s\nBlock: [%s, %s]\n%d chunks selected", hoveredChunk.toString(), worldBlockX, worldBlockZ, chunkSelection.size()));
      }
      Scene scene = mapOverlay.getScene();
      if (mapOverlay.isFocused()) {
        Point2D offset = mapOverlay.localToScene(0, 0);
        tooltip.show(scene.getWindow(),
            offset.getX() + scene.getX() + scene.getWindow().getX(),
            offset.getY() + scene.getY() + scene.getWindow().getY() + mapOverlay.getHeight());
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
      contextMenu.show(mapOverlay, event.getScreenX(), event.getScreenY());
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
        ChunkView theView = mapView.getMapView();
        double scale = theView.scale;
        int cx = (int) QuickMath.floor(theView.x + (x - getWidth() / 2) / scale);
        int cz = (int) QuickMath.floor(theView.z + (y - getHeight() / 2) / scale);
        if (theView.scale >= 16) {
          chunkSelection.toggleChunk(mapLoader.getWorld(), cx, cz);
        } else {
          chunkSelection.selectRegion(mapLoader.getWorld(), cx, cz);
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

    int scale = mapView.getScale();
    int newScale = scale - diff;
    if (newScale <= 16) {
      mapView.setScale(newScale);
    } else if ((scale - diff * 4) <= 64) {
      mapView.setScale(scale - diff * 4);
    } else {
      mapView.setScale(scale - diff * 16);
    }
  }

  protected void repaint(GraphicsContext gc) {
    mapBuffer.drawBuffered(gc);
    drawPlayers(gc);
    drawSpawn(gc);
    drawSelectionRect(gc);
    drawViewBounds(mapOverlay);
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
      int pw = (int) QuickMath.max(16, QuickMath.min(32, blockScale * 4));
      ppx = Math.min(mapView.width - pw, Math.max(0, ppx - pw / 2));
      ppy = Math.min(mapView.height - pw, Math.max(0, ppy - pw / 2));

      gc.drawImage(Icon.player.fxImage(), ppx, ppy, pw, pw);
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

    gc.drawImage(Icon.home.fxImage(), ppx, ppy, pw, pw);
  }

  public ChunkView getView() {
    return view;
  }

  public void redrawMap() {
    mapBuffer.clearBuffer();
    mapBuffer.redrawView(mapLoader, chunkSelection);
    repaintDeferred();
  }

  @Override public void cameraViewUpdated() {
    drawViewBounds(mapOverlay);
  }

  public void selectVisibleChunks(ChunkView cv, se.llbit.chunky.renderer.scene.Scene scene) {
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

    World world = mapLoader.getWorld();
    for (int x = cv.px0; x <= cv.px1; ++x) {
      for (int z = cv.pz0; z <= cv.pz1; ++z) {
        // Chunk top center position:
        Vector3 pos = new Vector3((x + 0.5) * 16, 63, (z + 0.5) * 16);
        pos.sub(o);
        if (norm[0].dot(pos) > CHUNK_SELECT_RADIUS && norm[1].dot(pos) > CHUNK_SELECT_RADIUS
            && norm[2].dot(pos) > CHUNK_SELECT_RADIUS && norm[3].dot(pos) > CHUNK_SELECT_RADIUS) {
          chunkSelection.selectChunk(world, x, z);
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

    Vector3[] direction = new Vector3[4];
    Vector2[] bounds = new Vector2[4];

    camera.calcViewRay(ray, -halfWidth, -0.5);
    direction[0] = new Vector3(ray.d);
    bounds[0] = findMapPos(ray, cv);

    camera.calcViewRay(ray, -halfWidth, 0.5);
    direction[1] = new Vector3(ray.d);
    bounds[1] = findMapPos(ray, cv);

    camera.calcViewRay(ray, halfWidth, 0.5);
    direction[2] = new Vector3(ray.d);
    bounds[2] = findMapPos(ray, cv);

    camera.calcViewRay(ray, halfWidth, -0.5);
    direction[3] = new Vector3(ray.d);
    bounds[3] = findMapPos(ray, cv);

    gc.setStroke(javafx.scene.paint.Color.YELLOW);
    for (int i = 0; i < 4; ++i) {
      int j = (i + 1) % 4;
      Vector2 start = null;
      Vector2 end = null;
      if (bounds[i] != null && bounds[j] != null) {
        start = new Vector2(bounds[i]);
        end = new Vector2(bounds[j]);
        Vector2 d = new Vector2(end);
        d.sub(start);
        double tFar = Math.sqrt(d.lengthSquared());
        d.normalize();
        if (!clipToMap(cv, start, end, d, tFar)) {
          continue;
        }
      }

      if (bounds[i] != null && bounds[j] == null) {
        start = new Vector2(bounds[i]);
        end = new Vector2();
        Vector2 d = new Vector2(direction[j].x, direction[j].z);
        d.normalize();
        if (!clipToMap(cv, start, end, d, Double.POSITIVE_INFINITY)) {
          continue;
        }
      }

      if (bounds[j] != null && bounds[i] == null) {
        start = new Vector2(bounds[j]);
        end = new Vector2();
        Vector2 d = new Vector2(direction[i].x, direction[i].z);
        d.normalize();
        if (!clipToMap(cv, start, end, d, Double.POSITIVE_INFINITY)) {
          continue;
        }
      }

      if (start != null && end != null) {
        start.x -= cv.x0;
        start.y -= cv.z0;
        end.x -= cv.x0;
        end.y -= cv.z0;
        start.scale(cv.scale);
        end.scale(cv.scale);
        drawLine(gc, start, end);
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
   *
   * <p>The result is given in chunk coordinates.
   */
  private static Vector2 findMapPos(Ray ray, ChunkView cv) {
    if (ray.d.y < 0 && ray.o.y > 63 || ray.d.y > 0 && ray.o.y < 63) {
      // Ray intersects ground.
      double d = (63 - ray.o.y) / ray.d.y;
      Vector3 pos = new Vector3();
      pos.scaleAdd(d, ray.d, ray.o);
      return new Vector2(pos.x / 16, pos.z / 16);
    } else {
      return null;
    }
  }

  /**
   * Clip a line to the map view boundaries.
   *
   * <p>The line is specified by a start point, a direction,
   * and distance. The end parameter receives the clipped
   * line end point.
   *
   * @param cv map view.
   * @param start start of the line to clip.
   * @param end end point of the clipped line (output).
   * @param d normalized direction of the line to clip.
   * @param tFar maximul line length.
   * @return {@code true} if any part of the line intersects the map view.
   */
  private static boolean clipToMap(ChunkView cv, Vector2 start, Vector2 end, Vector2 d, double tFar) {
    double tNear = 0;
    double tx0 = (cv.x0 - start.x) / d.x;
    double tx1 = (cv.x1 - start.x) / d.x;
    tNear = Math.max(tNear, Math.min(tx0, tx1));
    tFar = Math.min(tFar, Math.max(tx0, tx1));
    double tz0 = (cv.z0 - start.y) / d.y;
    double tz1 = (cv.z1 - start.y) / d.y;
    tNear = Math.max(tNear, Math.min(tz0, tz1));
    tFar = Math.min(tFar, Math.max(tz0, tz1));
    if (tNear < tFar) {
      end.scaleAdd(tFar, d, start);
      start.scaleAdd(tNear, d);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Draws a line between two points specified by 2-vectors.
   */
  private static void drawLine(GraphicsContext gc, Vector2 v1, Vector2 v2) {
    int x1 = (int) v1.x;
    int y1 = (int) v1.y;
    int x2 = (int) v2.x;
    int y2 = (int) v2.y;
    gc.strokeLine(x1, y1, x2, y2);
  }

  public void setOnViewDragged(Runnable onViewDragged) {
    this.onViewDragged = onViewDragged;
  }
}
