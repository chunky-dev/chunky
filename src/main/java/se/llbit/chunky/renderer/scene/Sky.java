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

import org.apache.log4j.Logger;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.SkymapTexture;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.nbt.AnyTag;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.DoubleTag;
import se.llbit.nbt.IntTag;
import se.llbit.nbt.StringTag;
import se.llbit.util.ProgramProperties;

/**
 * Sky model for ray tracing
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Sky {

	/**
	 * Sky rendering mode
	 * @author Jesper Öqvist <jesper@llbit.se>
	 */
	public enum SkyMode {
		/**
		 * Use simulated sky
		 */
		SIMULATED,
		/**
		 * Use a panormaic skymap
		 */
		SKYMAP,
		/**
		 * Use a gradient
		 */
		GRADIENT,
		/**
		 * Use a skybox
		 */
		SKYBOX
	};

	private static final Logger logger =
			Logger.getLogger(Sky.class);

	private Texture skymap = null;
	private String skymapFileName = "";
	private Scene scene;
	private double rotation;
	private boolean mirrored = true;

	// final to ensure that we don't do a lot of redundant re-allocation
	private final Vector3d groundColor = new Vector3d(0, 0, 1);

	/**
	 * @param scene
	 */
	public Sky(Scene scene) {
		this.scene = scene;

		try {
			rotation = Double.parseDouble(ProgramProperties.getProperty(
					"skymapRotation", "0"));
		} catch (NumberFormatException e) {
			rotation = 0;
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
				ProgramProperties.setProperty("skymap", fileName);
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
		ProgramProperties.removeProperty("skymap");
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
			theta = Math.asin(ray.d.z / Math.sqrt(r));
		if (ray.d.x < 0)
			theta = Math.PI - theta;
		theta += rotation;
		if (theta > 2 * Math.PI || theta < 0) {
			theta = theta % (2 * Math.PI);
			if (theta < 0)
				theta += 2 * Math.PI;
		}
		double phi = Math.abs(Math.asin(ray.d.y));
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
				theta = Math.asin(ray.d.z / Math.sqrt(r));
			if (ray.d.x < 0)
				theta = Math.PI - theta;
			theta += rotation;
			if (theta > 2 * Math.PI || theta < 0) {
				theta = theta % (2 * Math.PI);
				if (theta < 0)
					theta += 2 * Math.PI;
			}
			double phi = Math.abs(Math.asin(ray.d.y));
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
	 * Load sky description from world tag
	 * @param worldTag
	 */
	public void load(CompoundTag worldTag) {
		AnyTag skymapNameTag = worldTag.get("skymapFileName");
		if (!skymapNameTag.isError() && !skymapNameTag.stringValue().isEmpty())
			loadSkyMap(skymapNameTag.stringValue());
		AnyTag rotationTag = worldTag.get("skyYaw");
		if (!rotationTag.isError()) {
			rotation = rotationTag.doubleValue();
		}
		mirrored = worldTag.get("skyMirrored").boolValue(true);

		if (worldTag.get("groundColor").isCompoundTag()) {
			CompoundTag colorTag = (CompoundTag) worldTag.get("groundColor");
			groundColor.x = colorTag.get("red").doubleValue(1);
			groundColor.y = colorTag.get("green").doubleValue(1);
			groundColor.z = colorTag.get("blue").doubleValue(1);
		}
	}

	/**
	 * Save sky description to world tag
	 * @param worldTag
	 */
	public void save(CompoundTag worldTag) {
		if (skymap != null)
			worldTag.addItem("skymapFileName", new StringTag(skymapFileName));
		worldTag.addItem("skyYaw", new DoubleTag(rotation));
		worldTag.addItem("skyMirrored", new IntTag(mirrored));
		CompoundTag groundColorTag = new CompoundTag();
		groundColorTag.addItem("red", new DoubleTag(groundColor.x));
		groundColorTag.addItem("green", new DoubleTag(groundColor.y));
		groundColorTag.addItem("blue", new DoubleTag(groundColor.z));
		worldTag.addItem("groundColor", groundColorTag);
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
				(float) Math.min(1, groundColor.x),
				(float) Math.min(1, groundColor.y),
				(float) Math.min(1, groundColor.z));
	}

	/**
	 * Set a new ground color
	 * @param color
	 */
	public void setGroundColor(Color color) {
		groundColor.x = Math.pow(color.getRed() / 255., Scene.DEFAULT_GAMMA);
		groundColor.y = Math.pow(color.getGreen() / 255., Scene.DEFAULT_GAMMA);
		groundColor.z = Math.pow(color.getBlue() / 255., Scene.DEFAULT_GAMMA);
		scene.refresh();
	}
}
