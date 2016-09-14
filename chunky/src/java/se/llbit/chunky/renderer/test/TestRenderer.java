/* Copyright (c) 2012-2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer.test;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.Refreshable;
import se.llbit.chunky.renderer.RenderStatus;
import se.llbit.chunky.renderer.RenderStatusListener;
import se.llbit.chunky.renderer.Repaintable;
import se.llbit.chunky.renderer.Renderer;
import se.llbit.chunky.renderer.SceneProvider;
import se.llbit.chunky.renderer.SceneStatusListener;
import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Block;
import se.llbit.log.Log;
import se.llbit.math.ColorUtil;
import se.llbit.math.Matrix3;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Test renderer
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class TestRenderer extends Thread
    implements Refreshable, Renderer {

  private static final int NUM_BUFFERS = 3;

  //private Chunk3DView view;
  private BufferedImage buffer;
  private BufferedImage backBuffer;
  private final int width;
  private final int height;
  private double yaw, pitch;
  private final Matrix3 nextTransform = new Matrix3();
  private final Matrix3 transform = new Matrix3();
  private boolean refresh = true;
  private final Vector3 camPos = new Vector3();
  private final Matrix3 rot = new Matrix3();
  private final Matrix3 tmpRot = new Matrix3();
  private double distance;
  private double nextDistance = 1.5;
  private final int blockId;
  private final double fov = 70;
  private final double fovTan = Camera.clampedFovTan(fov);

  private final Object renderLock = new Object();

  /**
   * Mock scene object required by some block renderers
   */
  private final Scene scene;

  private static final Texture[] tex =
      {new Texture("east"), new Texture("west"), new Texture("north"), new Texture("south"),};

  private final Quad[] quads =
      {new Quad(new Vector3(1, 0, 0), new Vector3(1, 0, 1), new Vector3(1, 1, 0),
          new Vector4(0, 1, 0, 1)),
          new Quad(new Vector3(0, 0, 1), new Vector3(0, 0, 0), new Vector3(0, 1, 1),
              new Vector4(0, 1, 0, 1)),
          new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 1, 0),
              new Vector4(0, 1, 0, 1)),
          new Quad(new Vector3(1, 0, 1), new Vector3(0, 0, 1), new Vector3(1, 1, 1),
              new Vector4(0, 1, 0, 1)),};

  private final TestModel testModel = new TestModel();
  private final String targetFile;

  private final boolean showCompass;

  public TestRenderer(JFrame parent) {
    this(parent, -1);
  }

  public TestRenderer(JFrame parent, int blockId) {
    this(parent, blockId, "", false);
  }

  /**
   * Render a block and write the image to a target file.
   */
  public TestRenderer(JFrame parent, int blockId, String targetFile, boolean compass) {
    super("Test Renderer");

    showCompass = compass;
    this.blockId = blockId;
    this.targetFile = targetFile;
    scene = new Scene();
    scene.setBiomeColorsEnabled(false);

    width = PersistentSettings.DEFAULT_3D_CANVAS_WIDTH;
    height = PersistentSettings.DEFAULT_3D_CANVAS_HEIGHT;

    buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    yaw = -3 * Math.PI / 4;
    pitch = -5 * Math.PI / 6;

    updateTransform();

    if (targetFile.isEmpty()) {
      /*view = new Chunk3DView();
      view.addViewListener(this);
      view.setRenderer(this);
      view.setVisible(true);*/
      throw new Error("Live rendering currently disabled.");
    }
  }

  @Override public void run() {

    try {
      while (!isInterrupted()) {

        testModel.setUp();
        synchronized (renderLock) {
          waitRefresh();
          transform.set(nextTransform);
          distance = nextDistance;
        }

        synchronized (backBuffer) {
          raytrace();

          if (!targetFile.isEmpty()) {
            writeBufferToFile(backBuffer, targetFile);
            return;
          } else {
            // flip buffers
            BufferedImage tmp = backBuffer;
            backBuffer = buffer;
            buffer = tmp;
          }
        }

        //view.getCanvas().repaint();

      }
    } catch (InterruptedException e) {
    }
  }

  private void writeBufferToFile(BufferedImage buffer, String fileName) {
    try {
      ImageIO.write(buffer, "png", new FileOutputStream(fileName));
    } catch (FileNotFoundException e) {
      Log.error(e);
    } catch (IOException e) {
      Log.error(e);
    }
  }

  /**
   * Raytrace one frame.
   */
  private void raytrace() {

    double aspect = width / (double) height;

    Ray ray = new Ray();

    camPos.set(0, -distance, 0);
    transform.transform(camPos);
    camPos.add(.5, .5, .5);

    for (int x = 0; x < width; ++x) {

      double rayx = fovTan * aspect * (.5 - ((double) x) / width);

      for (int y = 0; y < height; ++y) {

        ray.setDefault();
        ray.d.set(rayx, 1, fovTan * (.5 - ((double) y) / height));
        ray.d.normalize();
        transform.transform(ray.d);

        ray.o.set(camPos);
        raytrace(ray);

        ray.color.x = QuickMath.min(1, FastMath.sqrt(ray.color.x));
        ray.color.y = QuickMath.min(1, FastMath.sqrt(ray.color.y));
        ray.color.z = QuickMath.min(1, FastMath.sqrt(ray.color.z));
        backBuffer.setRGB(x, y, ColorUtil.getRGB(ray.color));
      }
    }
  }

  private void raytrace(Ray ray) {
    double[] nearfar = new double[2];
    enterBlock(ray, nearfar);
    double tNear = nearfar[0];
    double tFar = nearfar[1];

    ray.color.set(1, 1, 1, 1);

    if (tNear <= tFar && tFar >= 0) {
      ray.o.scaleAdd(tNear, ray.d);
      ray.distance += tNear;

      if (blockId == -1) {
        renderTestModel(ray);
      } else {
        if (showCompass) {
          renderCompass(ray);
        }

        ray.setPrevMat(Block.AIR, 0);
        Block theBlock = Block.get(blockId);
        ray.setCurrentMat(theBlock, blockId);
        theBlock.intersect(ray, scene);
      }
    }
  }

  private void renderTestModel(Ray ray) {
    renderCompass(ray);

    ray.t = Double.POSITIVE_INFINITY;
    testModel.intersect(ray);
  }

  private void renderCompass(Ray ray) {
    ray.t = Double.POSITIVE_INFINITY;
    for (int i = 0; i < quads.length; ++i) {
      if (quads[i].intersect(ray)) {
        ray.t = ray.tNext;
        tex[i].getColor(ray);
      }
    }
  }

  private void enterBlock(Ray ray, double[] nearfar) {
    int level = 0;
    double t1, t2;
    double tNear = Double.NEGATIVE_INFINITY;
    double tFar = Double.POSITIVE_INFINITY;
    Vector3 d = ray.d;
    Vector3 o = ray.o;

    if (d.x != 0) {
      t1 = -o.x / d.x;
      t2 = ((1 << level) - o.x) / d.x;

      if (t1 > t2) {
        double t = t1;
        t1 = t2;
        t2 = t;
      }

      if (t1 > tNear)
        tNear = t1;
      if (t2 < tFar)
        tFar = t2;
    }

    if (d.y != 0) {
      t1 = -o.y / d.y;
      t2 = ((1 << level) - o.y) / d.y;

      if (t1 > t2) {
        double t = t1;
        t1 = t2;
        t2 = t;
      }

      if (t1 > tNear)
        tNear = t1;
      if (t2 < tFar)
        tFar = t2;
    }

    if (d.z != 0) {
      t1 = -o.z / d.z;
      t2 = ((1 << level) - o.z) / d.z;

      if (t1 > t2) {
        double t = t1;
        t1 = t2;
        t2 = t;
      }

      if (t1 > tNear)
        tNear = t1;
      if (t2 < tFar)
        tFar = t2;
    }

    nearfar[0] = tNear;
    nearfar[1] = tFar;
  }

  /*
  @Override public void onMoveForward() {
    synchronized (renderLock) {
      nextDistance -= .1;
      nextDistance = QuickMath.max(.1, nextDistance);
    }
    refresh();
  }

  @Override public void onMoveBackward() {
    synchronized (renderLock) {
      nextDistance += .1;
      nextDistance = QuickMath.max(.1, nextDistance);
    }
    refresh();
  }

  @Override public void onMouseDragged(int dx, int dy) {
    synchronized (renderLock) {
      double fovRad = QuickMath.degToRad(fov / 2);

      yaw += (Math.PI / 250) * dx * fovRad;
      pitch += (Math.PI / 250) * dy * fovRad;

      if (yaw > QuickMath.TAU) {
        yaw -= QuickMath.TAU;
      } else if (yaw < -QuickMath.TAU) {
        yaw += QuickMath.TAU;
      }

      updateTransform();
    }
    refresh();
  }

  @Override public void onZoom(int diff) {
    synchronized (renderLock) {
      nextDistance += .1 * diff;
      nextDistance = QuickMath.max(.1, nextDistance);
    }
    refresh();
  }
  */

  @Override public void setSceneProvider(SceneProvider sceneProvider) {

  }

  @Override public void setCanvas(Repaintable canvas) {

  }

  @Override public void setCPULoad(int loadPercent) {

  }

  @Override public void setNumThreads(int numThreads) {

  }

  @Override public void setRenderListener(RenderStatusListener renderStatusListener) {

  }

  @Override public void drawBufferedImage(GraphicsContext gc, double offsetX, double offsetY,
      double width, double height) {
    synchronized (backBuffer) {
      gc.drawImage(SwingFXUtils.toFXImage(buffer, null), offsetX, offsetY, width, height);
    }
  }

  @Override public synchronized void refresh() {
    synchronized (renderLock) {
      refresh = true;
      renderLock.notifyAll();
    }
  }

  private void waitRefresh() throws InterruptedException {
    synchronized (renderLock) {
      while (!refresh) {
        renderLock.wait();
      }
      refresh = false;
    }
  }

  @Override public void addSceneStatusListener(SceneStatusListener listener) {
  }

  @Override public void removeSceneStatusListener(SceneStatusListener listener) {
  }

  @Override public RenderStatus getRenderStatus() {
    return null;
  }

  @Override public void withSampleBufferProtected(SampleBufferConsumer consumer) {

  }

  @Override public void shutdown() {
    interrupt();
  }

  private void updateTransform() {
    Matrix3 tmpTransform = new Matrix3();

    nextTransform.setIdentity();

    // Yaw (y axis rotation).
    tmpTransform.rotY(QuickMath.HALF_PI + yaw);
    nextTransform.mul(tmpTransform);

    // Pitch (x axis rotation).
    tmpTransform.rotX(QuickMath.HALF_PI - pitch);
    nextTransform.mul(tmpTransform);
  }
}
