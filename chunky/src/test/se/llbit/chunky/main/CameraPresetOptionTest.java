/* Copyright (c) 2026 Chunky contributors
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
package se.llbit.chunky.main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.llbit.chunky.renderer.scene.Scene;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CameraPresetOptionTest {
  private Scene scene;
  private ChunkyOptions options;

  @BeforeEach
  public void setUp() {
    scene = new Scene();
    scene.camera().setFoV(40);
    scene.saveCameraPreset("first");
    scene.camera().setFoV(80);
    scene.saveCameraPreset("second");
    scene.camera().setFoV(60);
    options = ChunkyOptions.getDefaults();
  }

  @Test
  public void testCameraByName() {
    options.cameraName = "second";
    assertTrue(Chunky.loadCameraPreset(scene, options));
    assertEquals(80, scene.camera().getFov());
  }

  @Test
  public void testCameraByIndex() {
    options.cameraIndex = 0;
    assertTrue(Chunky.loadCameraPreset(scene, options));
    assertEquals(40, scene.camera().getFov());

    options.cameraIndex = 1;
    assertTrue(Chunky.loadCameraPreset(scene, options));
    assertEquals(80, scene.camera().getFov());
  }

  @Test
  public void testUnknownCameraName() {
    options.cameraName = "third";
    assertFalse(Chunky.loadCameraPreset(scene, options));
    assertEquals(60, scene.camera().getFov());
  }

  @Test
  public void testCameraIndexOutOfRange() {
    options.cameraIndex = 2;
    assertFalse(Chunky.loadCameraPreset(scene, options));
    assertEquals(60, scene.camera().getFov());
  }
}
