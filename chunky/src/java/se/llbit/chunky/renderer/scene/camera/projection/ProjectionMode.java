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
import se.llbit.chunky.renderer.scene.camera.Camera;
import se.llbit.chunky.renderer.scene.camera.projection.stereo.*;
import se.llbit.log.Log;
import se.llbit.util.Registerable;
import se.llbit.util.annotation.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static se.llbit.chunky.renderer.scene.camera.projection.ProjectorFactory.*;

/**
 * A ProjectionMode is a {@link Registerable} {@link ProjectorFactory}.
 *
 * <p>All registered presets can be found in the {@link Registry}.
 */
public class ProjectionMode implements Registerable, ProjectorFactory {
  public static final ProjectionMode PINHOLE = new ProjectionMode(
    "PINHOLE",
    "Standard",
    camera -> applyShift(
      camera,
      applyDoF(
        camera,
        new PinholeProjector(camera.getFov()),
        camera.getSubjectDistance()
      )
    )
  );
  public static final ProjectionMode PARALLEL = new ProjectionMode(
    "PARALLEL",
    "Parallel",
    camera -> applyShift(
      camera,
      applyDoF(
        camera,
        new ForwardDisplacementProjector(
          new ParallelProjector(
            camera.getWorldDiagonalSize(),
            camera.getFov()
          ),
          -camera.getWorldDiagonalSize()
        ),
        camera.getSubjectDistance() + camera.getWorldDiagonalSize()
      )
    )
  );
  public static final ProjectionMode PANORAMIC = new ProjectionMode(
    "PANORAMIC",
    "Panoramic (equirectangular)",
    camera -> applySphericalDoF(
      camera,
      new PanoramicProjector(camera.getFov())
    )
  );

  @PluginApi
  public static final Registry REGISTRY = new Registry(
    PINHOLE,
    PARALLEL,
    new ProjectionMode(
      "FISHEYE",
      "Fisheye",
      camera -> applySphericalDoF(
        camera,
        new FisheyeProjector(camera.getFov())
      )
    ),
    new ProjectionMode(
      "STEREOGRAPHIC",
      "Stereographic",
      camera -> new StereographicProjector(camera.getFov())
    ),
    PANORAMIC,
    new ProjectionMode(
      "PANORAMIC_SLOT",
      "Panoramic (slot)",
      camera -> applySphericalDoF(
        camera,
        new PanoramicSlotProjector(camera.getFov())
      )
    ),
    new ProjectionMode(
      "ODS_LEFT",
      "Omni‐directional Stereo (left eye)",
      camera -> new ODSSinglePerspectiveProjector(ODSSinglePerspectiveProjector.Eye.LEFT)
    ),
    new ProjectionMode(
      "ODS_RIGHT",
      "Omni‐directional Stereo (right eye)",
      camera -> new ODSSinglePerspectiveProjector(ODSSinglePerspectiveProjector.Eye.RIGHT)
    ),
    new ProjectionMode(
      "ODS_STACKED",
      "Omni‐directional Stereo (both eyes, vertically stacked)",
      camera -> new ODSVerticalStackedProjector()
    )
  );

  public static class Registry {
    private Registry(ProjectionMode... presets) {
      for (ProjectionMode preset : presets) {
        addProjectionPreset(preset);
      }
    }

    @PluginApi
    private final Map<String, ProjectionMode> projectionPresets = new LinkedHashMap<>();

    @PluginApi
    public void addProjectionPreset(ProjectionMode preset) {
      projectionPresets.put(preset.getId(), preset);
    }

    /**
     * @return projector with given id or fallback (preferred fallback is PINHOLE)
     */
    @PluginApi
    @Nullable
    public ProjectionMode getProjectionPreset(String id, ProjectionMode fallback) {
      ProjectionMode preset = projectionPresets.get(id);
      if (preset != null)
        return preset;

      Log.errorf("Unknown projection preset: %s, using %s", id, fallback.getId());
      return fallback;
    }

    @PluginApi
    public Collection<ProjectionMode> getProjectionPresets() {
      return projectionPresets.values();
    }
  }

  public final String ID;
  private final String name;
  private final ProjectorFactory factory;

  ProjectionMode(String id, String name, ProjectorFactory factory) {
    this.ID = id;
    this.name = name;
    this.factory = factory;
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public Projector create(Camera camera) {
    return factory.create(camera);
  }
}
