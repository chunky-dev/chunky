/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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

import javafx.scene.image.Image;
import se.llbit.chunky.renderer.projection.ProjectionMode;
import se.llbit.chunky.world.Icon;

/**
 * Camera presets.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public enum CameraPreset {
  ISO_NORTH_WEST("Isometric North-West", Icon.isoNW.fxImage(), -Math.PI / 4, -Math.PI / 4) {
    @Override public void apply(Camera camera) {
      camera.setView(yaw, pitch, 0);
      camera.setProjectionMode(ProjectionMode.PARALLEL);
      camera.setShift(0, 0);
    }
  },
  ISO_NORTH_EAST("Isometric North-East", Icon.isoNE.fxImage(), -3 * Math.PI / 4, -Math.PI / 4) {
    @Override public void apply(Camera camera) {
      camera.setView(yaw, pitch, 0);
      camera.setProjectionMode(ProjectionMode.PARALLEL);
      camera.setShift(0, 0);
    }
  },
  ISO_SOUTH_EAST("Isometric South-East", Icon.isoSE.fxImage(), -5 * Math.PI / 4, -Math.PI / 4) {
    @Override public void apply(Camera camera) {
      camera.setView(yaw, pitch, 0);
      camera.setProjectionMode(ProjectionMode.PARALLEL);
      camera.setShift(0, 0);
    }
  },
  ISO_SOUTH_WEST("Isometric South-West", Icon.isoSW.fxImage(), -7 * Math.PI / 4, -Math.PI / 4) {
    @Override public void apply(Camera camera) {
      camera.setView(yaw, pitch, 0);
      camera.setProjectionMode(ProjectionMode.PARALLEL);
      camera.setShift(0, 0);
    }
  },
  SKYBOX_RIGHT("Skybox Right", Icon.skyboxRight.fxImage(), Math.PI, -Math.PI / 2) {
    @Override public void apply(Camera camera) {
      camera.setView(yaw, pitch, 0);
      camera.setProjectionMode(ProjectionMode.PINHOLE);
      camera.setFoV(90);
      camera.setShift(0, 0);
    }
  },
  SKYBOX_LEFT("Skybox Left", Icon.skyboxLeft.fxImage(), 0, -Math.PI / 2) {
    @Override public void apply(Camera camera) {
      camera.setView(yaw, pitch, 0);
      camera.setProjectionMode(ProjectionMode.PINHOLE);
      camera.setFoV(90);
      camera.setShift(0, 0);
    }
  },
  SKYBOX_UP("Skybox Up", Icon.skyboxUp.fxImage(), -Math.PI / 2, Math.PI) {
    @Override public void apply(Camera camera) {
      camera.setView(yaw, pitch, 0);
      camera.setProjectionMode(ProjectionMode.PINHOLE);
      camera.setFoV(90);
      camera.setShift(0, 0);
    }
  },
  SKYBOX_DOWN("Skybox Down", Icon.skyboxDown.fxImage(), -Math.PI / 2, 0) {
    @Override public void apply(Camera camera) {
      camera.setView(yaw, pitch, 0);
      camera.setProjectionMode(ProjectionMode.PINHOLE);
      camera.setFoV(90);
      camera.setShift(0, 0);
    }
  },
  SKYBOX_FRONT("Skybox Front (North)", Icon.skyboxFront.fxImage(), -Math.PI / 2, -Math.PI / 2) {
    @Override public void apply(Camera camera) {
      camera.setView(yaw, pitch, 0);
      camera.setProjectionMode(ProjectionMode.PINHOLE);
      camera.setFoV(90);
      camera.setShift(0, 0);
    }
  },
  SKYBOX_BACK("Skybox Back", Icon.skyboxBack.fxImage(), Math.PI / 2, -Math.PI / 2) {
    @Override public void apply(Camera camera) {
      camera.setView(yaw, pitch, 0);
      camera.setProjectionMode(ProjectionMode.PINHOLE);
      camera.setFoV(90);
      camera.setShift(0, 0);
    }
  };

  protected final double yaw;
  protected final double pitch;
  private final String name;
  private final Image icon;

  CameraPreset(String name, Image icon, double yaw, double pitch) {
    this.name = name;
    this.icon = icon;
    this.yaw = yaw;
    this.pitch = pitch;
  }

  @Override public String toString() {
    return name;
  }

  /**
   * Applies a preset to a camera.
   */
  abstract public void apply(Camera camera);

  public Image getIcon() {
    return icon;
  }
}
