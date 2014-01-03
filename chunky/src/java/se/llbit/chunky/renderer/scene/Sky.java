/* Copyright (c) 2012-2013 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.Logger;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.SkymapTexture;
import se.llbit.json.JsonObject;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.util.JSONifiable;

/**
 * Sky model for ray tracing
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Sky implements JSONifiable {

	/**
	 * Sky rendering mode
	 * @author Jesper Öqvist <jesper@llbit.se>
	 */
	public enum SkyMode {
		/**
		 * Use simulated sky
		 */
		SIMULATED("Simulated"),
		/**
		 * Use a panormaic skymap
		 */
		SKYMAP("Panoramic Skymap (above horizon)"),
		/**
		 * Use a gradient
		 */
		GRADIENT("Color Gradient"),
		/**
		 * Use a skybox
		 */
		SKYBOX("Skybox");

		private String name;

		SkyMode(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	};

	private static final Logger logger =
			Logger.getLogger(Sky.class);

	private Texture skymap = null;
	private String skymapFileName = "";
	private final SceneDescription scene;
	private double rotation;
	private boolean mirrored = true;

	// final to ensure that we don't do a lot of redundant re-allocation
	private final Vector3d groundColor = new Vector3d(0, 0, 1);

	/**
	 * Current rendering mode
	 */
	private SkyMode mode = SkyMode.SIMULATED;

	/**
	 * @param sceneDescription
	 */
	public Sky(SceneDescription sceneDescription) {
		this.scene = sceneDescription;

		rotation = PersistentSettings.getSkymapRotation();
	}

	/**
	 * Load the configured skymap file
	 * @param fileName
	 */
	public void loadSkyMap() {
		if (!skymapFileName.isEmpty()) {
			loadSkyMap(skymapFileName);
		}
	}

	/**
	 * Load a panoramic skymap texture
	 * @param fileName
	 */
	public void loadSkyMap(String fileName) {
		skymapFileName = fileName;
		File sky = new File(skymapFileName);
		if (sky.exists()) {
			try {
				logger.info("Loading sky map: " + fileName);
				skymap = new SkymapTexture(ImageIO.read(sky));
				PersistentSettings.setSkymap(fileName);
			} catch (IOException e) {
				logger.warn("Could not load skymap: " + fileName);
			} catch (Throwable e) {
				logger.error("Unexpected exception ocurred!", e);
			}
		} else {
			logger.warn("Skymap could not be opened: " + fileName);
		}
		scene.refresh();
	}

	/**
	 * Set the sky equal to other sky
	 * @param other
	 */
	public void set(Sky other) {
		skymapFileName = other.skymapFileName;
		skymap = other.skymap;
		rotation = other.rotation;
		mirrored = other.mirrored;
		groundColor.set(other.groundColor);
	}

	/**
	 * Unload the skymap texture and use the default sky instead
	 */
	public synchronized void unloadSkymap() {
		skymapFileName = "";
		skymap = null;
		PersistentSettings.removeSetting("skymap");
		scene.refresh();
	}

	/**
	 * Panormaic skymap color
	 * @param ray
	 * @param blackBelowHorizon
	 */
	public void getSkyDiffuseColor(Ray ray, boolean blackBelowHorizon) {
		if (getGroundColor(ray, blackBelowHorizon)) {
			return;
		} else if (skymap == null) {
			scene.sun().skylight(ray);
			ray.hit = true;
			return;
		}
		double r = ray.d.z * ray.d.z + ray.d.x * ray.d.x;
		double theta = 0;
		if (r > Ray.EPSILON)
			theta = FastMath.asin(ray.d.z / FastMath.sqrt(r));
		if (ray.d.x < 0)
			theta = Math.PI - theta;
		theta += rotation;
		if (theta > 2 * Math.PI || theta < 0) {
			theta = theta % (2 * Math.PI);
			if (theta < 0)
				theta += 2 * Math.PI;
		}
		double phi = QuickMath.abs(FastMath.asin(ray.d.y));
		skymap.getColor(theta / (2*Math.PI), (2 * phi / Math.PI), ray.color);
		ray.hit = true;
	}

	private boolean getGroundColor(Ray ray, boolean blackBelowHorizon) {
		if (blackBelowHorizon && ray.d.y < 0) {
			ray.color.set(0, 0, 0, 1);
			ray.hit = true;
			return true;
		} else if (!mirrored && ray.d.y < 0) {
			ray.color.set(groundColor.x, groundColor.y, groundColor.z, 1);
			ray.hit = true;
			return true;
		}
		return false;
	}

	/**
	 * Bilinear interpolated panoramic skymap color
	 * @param ray
	 * @param blackBelowHorizon
	 */
	public void getSkyColorInterpolated(Ray ray, boolean blackBelowHorizon) {
		if (getGroundColor(ray, blackBelowHorizon)) {
			return;

		} else if (scene.sunEnabled && scene.sun().intersect(ray)) {
			double r = ray.color.x;
			double g = ray.color.y;
			double b = ray.color.z;
			getPanoramaColorInterpolated(ray);
			ray.color.x = ray.color.x + r;
			ray.color.y = ray.color.y + g;
			ray.color.z = ray.color.z + b;

		} else {

			getPanoramaColorInterpolated(ray);
		}
		ray.hit = true;
	}

	private void getPanoramaColorInterpolated(Ray ray) {
		if (skymap == null) {
			scene.sun().skylight(ray);
		} else {
			double r = ray.d.z * ray.d.z + ray.d.x * ray.d.x;
			double theta = 0;
			if (r > Ray.EPSILON)
				theta = FastMath.asin(ray.d.z / FastMath.sqrt(r));
			if (ray.d.x < 0)
				theta = Math.PI - theta;
			theta += rotation;
			if (theta > 2 * Math.PI || theta < 0) {
				theta = theta % (2 * Math.PI);
				if (theta < 0)
					theta += 2 * Math.PI;
			}
			double phi = QuickMath.abs(FastMath.asin(ray.d.y));
			theta /= 2 * Math.PI;
			phi /= Math.PI / 2;
			phi = 1 - phi;
			skymap.getColorInterpolated(theta, phi, ray.color);
		}
	}

	/**
	 * Get the specular sky color for the ray
	 * @param ray
	 * @param blackBelowHorizon
	 */
	public void getSkySpecularColor(Ray ray, boolean blackBelowHorizon) {
		if (scene.sunEnabled && scene.sun().intersect(ray)) {
			double r = ray.color.x;
			double g = ray.color.y;
			double b = ray.color.z;
			getSkyDiffuseColor(ray, blackBelowHorizon);
			ray.color.x = ray.color.x + r;
			ray.color.y = ray.color.y + g;
			ray.color.z = ray.color.z + b;
			ray.hit = true;

		} else {
			getSkyDiffuseColor(ray, blackBelowHorizon);
		}
	}

	/**
	 * Set the polar offset of the skymap
	 * @param value
	 */
	public void setRotation(double value) {
		rotation = value;
		scene.refresh();
	}

	/**
	 * @return The polar offset of the skymap
	 */
	public double getRotation() {
		return rotation;
	}

	/**
	 * Set sky mirroring at the horizon
	 * @param b
	 */
	public void setMirrored(boolean b) {
		if (b != mirrored) {
			mirrored = b;
			scene.refresh();
		}
	}

	/**
	 * @return <code>true</code> if the sky is mirrored at the horizon
	 */
	public boolean isMirrored() {
		return mirrored;
	}

	/**
	 * @return The current ground color
	 */
	public Color getGroundColor() {
		return new Color(
				(float) QuickMath.min(1, groundColor.x),
				(float) QuickMath.min(1, groundColor.y),
				(float) QuickMath.min(1, groundColor.z));
	}

	/**
	 * Set a new ground color
	 * @param color
	 */
	public void setGroundColor(Color color) {
		groundColor.x = FastMath.pow(color.getRed() / 255., Scene.DEFAULT_GAMMA);
		groundColor.y = FastMath.pow(color.getGreen() / 255., Scene.DEFAULT_GAMMA);
		groundColor.z = FastMath.pow(color.getBlue() / 255., Scene.DEFAULT_GAMMA);
		scene.refresh();
	}

	/**
	 * Set the sky rendering mode
	 * @param mode
	 */
	public void setSkyMode(SkyMode mode) {
		this.mode = mode;
	}

	/**
	 * @return Current sky rendering mode
	 */
	public SkyMode getSkyMode() {
		return mode;
	}

	@Override
	public JsonObject toJson() {
		JsonObject sky = new JsonObject();
		if (skymap != null) {
			sky.add("skymap", skymapFileName);
		}
		sky.add("skyYaw", rotation);
		sky.add("skyMirrored", mirrored);
		JsonObject groundColorObj = new JsonObject();
		groundColorObj.add("red", groundColor.x);
		groundColorObj.add("green", groundColor.y);
		groundColorObj.add("blue", groundColor.z);
		sky.add("groundColor", groundColorObj);
		return sky;
	}

	@Override
	public void fromJson(JsonObject obj) {
		skymapFileName = obj.get("skymap").stringValue("");
		if (skymapFileName.isEmpty()) {
			skymapFileName = obj.get("skymapFileName").stringValue("");
		}
		rotation = obj.get("skyYaw").doubleValue(0);
		mirrored = obj.get("skyMirrored").boolValue(true);

		JsonObject groundColorObj = obj.get("groundColor").object();
		groundColor.x = groundColorObj.get("red").doubleValue(1);
		groundColor.y = groundColorObj.get("green").doubleValue(1);
		groundColor.z = groundColorObj.get("blue").doubleValue(1);
	}
}
