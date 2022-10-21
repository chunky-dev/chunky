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
package se.llbit.chunky.renderer.scene.camera.projection;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.camera.ApertureShape;
import se.llbit.chunky.renderer.scene.camera.Camera;

/**
 * Creates a projector based on the given camera settings.
 */
@PluginApi
@FunctionalInterface
public interface ProjectorFactory {
  Projector create(Camera camera);

  static Projector applyDoF(
    Camera camera,
    Projector projector,
    double subjectDistance
  ) {
    if (camera.isInfiniteDoF()) {
      return projector;
    }

    if (camera.getApertureShape() == ApertureShape.CUSTOM) {
      return new ApertureProjector(
        projector,
        subjectDistance / camera.getDof(),
        subjectDistance,
        camera.getApertureMaskFilename()
      );
    } else if (camera.getApertureShape() == ApertureShape.CIRCLE) {
      return new ApertureProjector(
        projector,
        subjectDistance / camera.getDof(),
        subjectDistance
      );
    } else
      return new ApertureProjector(
        projector,
        subjectDistance / camera.getDof(),
        subjectDistance,
        camera.getApertureShape()
      );
  }

  static Projector applySphericalDoF(
    Camera camera,
    Projector projector
  ) {
    return camera.isInfiniteDoF() ?
      projector :
      new SphericalApertureProjector(
        projector,
        camera.getSubjectDistance() / camera.getDof(),
        camera.getSubjectDistance()
      );
  }

  static Projector applyShift(
    Camera camera,
    Projector projector
  ) {
    if (Math.abs(camera.getShiftX()) > 0 || Math.abs(camera.getShiftY()) > 0) {
      return new ShiftProjector(
        projector,
        camera.getShiftX(),
        camera.getShiftY()
      );
    }
    return projector;
  }
}
