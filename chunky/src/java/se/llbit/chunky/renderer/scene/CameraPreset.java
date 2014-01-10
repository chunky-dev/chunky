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

import javax.swing.ImageIcon;

import se.llbit.chunky.renderer.scene.Camera.ProjectionMode;
import se.llbit.chunky.world.Icon;

/**
 * Camera presets.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
abstract public class CameraPreset {

	public static CameraPreset NONE = new CameraPreset("None") {
		@Override
		public void apply(Camera camera) {
		}
		@Override
		public ImageIcon getIcon() {
			return null;
		}
	};
	public static CameraPreset ISO_WEST_NORTH = new Isometric("West-North",
			Icon.isoWN.createIcon(), -Math.PI/4, -Math.PI/4);
	public static CameraPreset ISO_NORTH_EAST = new Isometric("North-East",
			Icon.isoNE.createIcon(), -3*Math.PI/4, -Math.PI/4);
	public static CameraPreset ISO_EAST_SOUTH = new Isometric("East-South",
			Icon.isoES.createIcon(), -5*Math.PI/4, -Math.PI/4);
	public static CameraPreset ISO_SOUTH_WEST = new Isometric("South-West",
			Icon.isoSW.createIcon(), -7*Math.PI/4, -Math.PI/4);
	public static CameraPreset SKYBOX_EAST = new Skybox("East", Math.PI, -Math.PI/2);
	public static CameraPreset SKYBOX_WEST = new Skybox("West", 0, -Math.PI/2);
	public static CameraPreset SKYBOX_UP = new Skybox("Up", -Math.PI/2, Math.PI);
	public static CameraPreset SKYBOX_DOWN = new Skybox("Down", -Math.PI/2, 0);
	public static CameraPreset SKYBOX_NORTH = new Skybox("North", -Math.PI/2, -Math.PI/2);
	public static CameraPreset SKYBOX_SOUTH = new Skybox("South", Math.PI/2, -Math.PI/2);

	public static class Isometric extends CameraPreset {

		private final double yaw;
		private final double pitch;
		private final ImageIcon icon;

		public Isometric(String name, ImageIcon icon, double yaw, double pitch) {
			super("Isometric " + name);
			this.yaw = yaw;
			this.pitch = pitch;
			this.icon = icon;
		}

		@Override
		public void apply(Camera camera) {
			camera.setView(yaw, pitch, 0);
			camera.setProjectionMode(ProjectionMode.PARALLEL);
		}

		@Override
		public ImageIcon getIcon() {
			return icon;
		}
	}

	public static class Skybox extends CameraPreset {

		private final double yaw;
		private final double pitch;

		public Skybox(String name, double yaw, double pitch) {
			super("Skybox " + name);
			this.yaw = yaw;
			this.pitch = pitch;
		}

		@Override
		public void apply(Camera camera) {
			camera.setFoV(90);
			camera.setView(yaw, pitch, 0);
			camera.setProjectionMode(ProjectionMode.PINHOLE);
		}

		@Override
		public ImageIcon getIcon() {
			return null;
		}
	}

	private final String name;

	public CameraPreset(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Apply the preset to a camera
	 * @param camera
	 */
	abstract public void apply(Camera camera);

	abstract public ImageIcon getIcon();
}
