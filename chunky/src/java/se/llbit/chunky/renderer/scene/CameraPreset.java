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

import se.llbit.chunky.renderer.projection.ProjectionMode;
import se.llbit.chunky.world.Icon;

/**
 * Camera presets.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
abstract public class CameraPreset {

	public static CameraPreset NONE = new CameraPreset("None", null) {
		@Override
		public void apply(Camera camera) {
		}
		@Override
		public ImageIcon getIcon() {
			return null;
		}
	};
	public static CameraPreset ISO_WEST_NORTH = new Isometric("West-North",
			Icon.isoWN.imageIcon(), -Math.PI/4, -Math.PI/4);
	public static CameraPreset ISO_NORTH_EAST = new Isometric("North-East",
			Icon.isoNE.imageIcon(), -3*Math.PI/4, -Math.PI/4);
	public static CameraPreset ISO_EAST_SOUTH = new Isometric("East-South",
			Icon.isoES.imageIcon(), -5*Math.PI/4, -Math.PI/4);
	public static CameraPreset ISO_SOUTH_WEST = new Isometric("South-West",
			Icon.isoSW.imageIcon(), -7*Math.PI/4, -Math.PI/4);
	public static CameraPreset SKYBOX_RIGHT = new Skybox("Right", Icon.skyboxRight.imageIcon(), Math.PI, -Math.PI/2);
	public static CameraPreset SKYBOX_LEFT = new Skybox("Left", Icon.skyboxLeft.imageIcon(), 0, -Math.PI/2);
	public static CameraPreset SKYBOX_UP = new Skybox("Up", Icon.skyboxUp.imageIcon(), -Math.PI/2, Math.PI);
	public static CameraPreset SKYBOX_DOWN = new Skybox("Down", Icon.skyboxDown.imageIcon(), -Math.PI/2, 0);
	public static CameraPreset SKYBOX_FRONT = new Skybox("Front (North)", Icon.skyboxFront.imageIcon(), -Math.PI/2, -Math.PI/2);
	public static CameraPreset SKYBOX_BACK = new Skybox("Back", Icon.skyboxBack.imageIcon(), Math.PI/2, -Math.PI/2);

	public static class Isometric extends CameraPreset {

		private final double yaw;
		private final double pitch;

		public Isometric(String name, ImageIcon icon, double yaw, double pitch) {
			super("Isometric " + name, icon);
			this.yaw = yaw;
			this.pitch = pitch;
		}

		@Override
		public void apply(Camera camera) {
			camera.setView(yaw, pitch, 0);
			camera.setProjectionMode(ProjectionMode.PARALLEL);
		}
	}

	public static class Skybox extends CameraPreset {

		private final double yaw;
		private final double pitch;
		private final ImageIcon icon;

		public Skybox(String name, ImageIcon icon, double yaw, double pitch) {
			super("Skybox " + name, icon);
			this.yaw = yaw;
			this.pitch = pitch;
			this.icon = icon;
		}

		@Override
		public void apply(Camera camera) {
			camera.setFoV(90);
			camera.setView(yaw, pitch, 0);
			camera.setProjectionMode(ProjectionMode.PINHOLE);
		}

		@Override
		public ImageIcon getIcon() {
			return icon;
		}
	}

	private final String name;
	private ImageIcon icon;

	public CameraPreset(String name, ImageIcon icon) {
		this.name = name;
		this.icon = icon;
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

	public ImageIcon getIcon() {
		return icon;
	}
}
