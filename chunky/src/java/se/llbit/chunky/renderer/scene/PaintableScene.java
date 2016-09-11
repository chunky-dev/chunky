/*
 * Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer.scene;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;

import java.nio.IntBuffer;

/**
 * A scene that can be painted to a canvas.
 * This is used for non-headless rendering where the
 * scene state is painted to the render preview.
 */
public class PaintableScene extends Scene {
  private static final WritablePixelFormat<IntBuffer> PIXEL_FORMAT =
      PixelFormat.getIntArgbInstance();

  private WritableImage image;

  public PaintableScene() {
    super();
  }

  /** Creates a paintable scene copy of another scene. */
  public PaintableScene(Scene scene) {
    super(scene);
  }

  @Override protected synchronized void initBuffers() {
    super.initBuffers();
    image = new WritableImage(width, height);
  }

  /**
   * Draw the buffered image to a canvas.
   */
  @Override
  public synchronized void drawBufferedImage(GraphicsContext gc, double offsetX, double offsetY,
      double canvasWidth, double canvasHeight) {
    image.getPixelWriter().setPixels(0, 0, width, height, PIXEL_FORMAT, buffer, 0, width);
    gc.drawImage(image, offsetX, offsetY, canvasWidth, canvasHeight);
  }
}
