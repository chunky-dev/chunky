/* Copyright (c) 2012-2014 Jesper Öqvist <jesper@llbit.se>
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.Logger;

import se.llbit.chunky.resources.HDRTexture;
import se.llbit.chunky.resources.PFMTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Clouds;
import se.llbit.chunky.world.SkymapTexture;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonNull;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Color;
import se.llbit.math.Constants;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;
import se.llbit.util.JSONifiable;
import se.llbit.util.NotNull;

/**
 * Sky model for ray tracing
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Sky implements JSONifiable {

	private static final double CLOUD_OPACITY = 0.4;

	/**
	 * Default sky light intensity
	 */
	public static final double DEFAULT_INTENSITY = 1;

	/**
	 * Default cloud y-position
	 */
	protected static final int DEFAULT_CLOUD_HEIGHT = 128;

	protected static final int DEFAULT_CLOUD_SIZE = 64;

	/**
	 * Maximum sky light intensity
	 */
	public static final double MAX_INTENSITY = 50;

	/**
	 * Minimum sky light intensity
	 */
	public static final double MIN_INTENSITY = 0.01;

	public static final int SKYBOX_UP = 0;
	public static final int SKYBOX_DOWN = 1;
	public static final int SKYBOX_FRONT = 2;
	public static final int SKYBOX_BACK = 3;
	public static final int SKYBOX_RIGHT = 4;
	public static final int SKYBOX_LEFT = 5;

	/**
	 * Sky rendering mode
	 * @author Jesper Öqvist <jesper@llbit.se>
	 */
	public enum SkyMode {
		/**
		 * Use simulated sky
		 */
		SIMULATED("Simulated"),
		// TODO
		///**
		// * Simulated night-time
		// */
		//SIMULATED_NIGHT("Simulated (night)"),
		/**
		 * Use a gradient
		 */
		GRADIENT("Color Gradient"),
		/**
		 * Use a panormaic skymap
		 */
		SKYMAP_PANORAMIC("Skymap (panoramic)"),
		/**
		 * Light probe
		 */
		SKYMAP_SPHERICAL("Skymap (spherical)"),
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

		public static final SkyMode DEFAULT = SIMULATED;
		public static final SkyMode[] values = values();

		public static SkyMode get(String name) {
			for (SkyMode mode: values) {
				if (mode.name().equals(name)) {
					return mode;
				}
			}
			return DEFAULT;
		}

	};

	private static final Logger logger =
			Logger.getLogger(Sky.class);

	@NotNull
	private Texture skymap = Texture.EMPTY_TEXTURE;
	private final Texture skybox[] = {
			Texture.EMPTY_TEXTURE, Texture.EMPTY_TEXTURE,
			Texture.EMPTY_TEXTURE, Texture.EMPTY_TEXTURE,
			Texture.EMPTY_TEXTURE, Texture.EMPTY_TEXTURE };
	private String skymapFileName = "";
	private final String skyboxFileName[] = {"", "", "", "", "", ""};
	private final SceneDescription scene;
	private double rotation = 0;
	private boolean mirrored = true;
	private double horizonOffset = 0.1;
	private boolean cloudsEnabled = false;
	private double cloudSize = DEFAULT_CLOUD_SIZE;
	private final Vector3d cloudOffset = new Vector3d(0, DEFAULT_CLOUD_HEIGHT, 0);

	private double skyLightModifier = DEFAULT_INTENSITY;

	private List<Vector4d> gradient = new LinkedList<Vector4d>();

	/**
	 * Current rendering mode
	 */
	private SkyMode mode = SkyMode.DEFAULT;

	/**
	 * @param sceneDescription
	 */
	public Sky(SceneDescription sceneDescription) {
		this.scene = sceneDescription;
		makeDefaultGradient(gradient);
	}

	/**
	 * Load the configured skymap file
	 * @param fileName
	 */
	public void loadSkymap() {
		switch (mode) {
		case SKYMAP_PANORAMIC:
		case SKYMAP_SPHERICAL:
			if (!skymapFileName.isEmpty()) {
				loadSkymap(skymapFileName);
			}
			break;
		case SKYBOX:
			for (int i = 0; i < 6; ++i) {
				if (!skyboxFileName[i].isEmpty()) {
					loadSkyboxTexture(skyboxFileName[i], i);
				}
			}
		default:
			break;
		}
	}

	/**
	 * Load a panoramic skymap texture
	 * @param fileName
	 */
	public void loadSkymap(String fileName) {
		skymapFileName = fileName;
		skymap = loadSkyTexture(fileName, skymap);
		scene.refresh();
	}

	/**
	 * Set the sky equal to other sky
	 * @param other
	 */
	public void set(Sky other) {
		horizonOffset = other.horizonOffset;
		cloudsEnabled = other.cloudsEnabled;
		cloudOffset.set(other.cloudOffset);
		cloudSize = other.cloudSize;
		skymapFileName = other.skymapFileName;
		skymap = other.skymap;
		rotation = other.rotation;
		mirrored = other.mirrored;
		skyLightModifier = other.skyLightModifier;
		gradient = new ArrayList<Vector4d>(other.gradient);
		mode = other.mode;
		for (int i = 0; i < 6; ++i) {
			skybox[i] = other.skybox[i];
			skyboxFileName[i] = other.skyboxFileName[i];
		}
	}

	/**
	 * Calculate sky color for the ray, based on sky mode
	 * @param ray
	 * @param blackBelowHorizon
	 */
	public void getSkyDiffuseColorInner(Ray ray, boolean blackBelowHorizon) {
		switch (mode) {
		case GRADIENT:
		{
			double angle = Math.asin(ray.d.y);
			int x = 0;
			if (gradient.size() > 1) {
				double pos = (angle+Constants.HALF_PI)/Math.PI;
				Vector4d c0 = gradient.get(x);
				Vector4d c1 = gradient.get(x+1);
				double xx = (pos - c0.w) / (c1.w-c0.w);
				while (x+2 < gradient.size() && xx > 1) {
					x += 1;
					c0 = gradient.get(x);
					c1 = gradient.get(x+1);
					xx = (pos - c0.w) / (c1.w-c0.w);
				}
				xx = 0.5*(Math.sin(Math.PI*xx-Constants.HALF_PI)+1);
				double a = 1-xx;
				double b = xx;
				ray.color.set(a*c0.x+b*c1.x, a*c0.y+b*c1.y, a*c0.z+b*c1.z, 1);
			}
			break;
        }
		case SIMULATED:
		{
			scene.sun().calcSkyLight(ray, horizonOffset);
			break;
		}
		case SKYMAP_PANORAMIC:
		{
			if (mirrored) {
				double theta = FastMath.atan2(ray.d.z, ray.d.x);
				theta += rotation;
				theta /= Constants.TAU;
				if (theta > 1 || theta < 0) {
					theta = (theta%1 + 1) % 1;
				}
				double phi = Math.abs(Math.asin(ray.d.y)) / Constants.HALF_PI;
				skymap.getColor(theta, phi, ray.color);
			} else {
				double theta = FastMath.atan2(ray.d.z, ray.d.x);
				theta += rotation;
				theta /= Constants.TAU;
				theta = (theta%1 + 1) % 1;
				double phi = (Math.asin(ray.d.y) + Constants.HALF_PI) / Math.PI;
				skymap.getColor(theta, phi, ray.color);
			}
			break;
		}
		case SKYMAP_SPHERICAL:
		{
			double cos = FastMath.cos(-rotation);
			double sin = FastMath.sin(-rotation);
			double x = cos*ray.d.x + sin*ray.d.z;
			double y = ray.d.y;
			double z = -sin*ray.d.x + cos*ray.d.z;
			double len = Math.sqrt(x*x + y*y);
			double theta = (len < Ray.EPSILON) ? 0 : Math.acos(-z)/Constants.TAU;
			double u = theta*x + .5;
			double v = .5 + theta*y;
			skymap.getColor(u, v, ray.color);
			break;
		}
		case SKYBOX:
		{
			double cos = FastMath.cos(-rotation);
			double sin = FastMath.sin(-rotation);
			double x = cos*ray.d.x + sin*ray.d.z;
			double y = ray.d.y;
			double z = -sin*ray.d.x + cos*ray.d.z;
			double xabs = QuickMath.abs(x);
			double yabs = QuickMath.abs(y);
			double zabs = QuickMath.abs(z);
			if (y > xabs && y > zabs) {
				double alpha = 1 / yabs;
				skybox[SKYBOX_UP].getColor(
						(1 + x*alpha)/2.0,
						(1 + z*alpha)/2.0,
						ray.color);
			}
			else if (-z > xabs && -z > yabs) {
				double alpha = 1 / zabs;
				skybox[SKYBOX_FRONT].getColor(
						(1 + x*alpha)/2.0,
						(1 + y*alpha)/2.0,
						ray.color);
			}
			else if (z > xabs && z > yabs) {
				double alpha = 1 / zabs;
				skybox[SKYBOX_BACK].getColor(
						(1 - x*alpha)/2.0,
						(1 + y*alpha)/2.0,
						ray.color);
			}
			else if (-x > zabs && -x > yabs) {
				double alpha = 1 / xabs;
				skybox[SKYBOX_LEFT].getColor(
						(1 - z*alpha)/2.0,
						(1 + y*alpha)/2.0,
						ray.color);
			}
			else if (x > zabs && x > yabs) {
				double alpha = 1 / xabs;
				skybox[SKYBOX_RIGHT].getColor(
						(1 + z*alpha)/2.0,
						(1 + y*alpha)/2.0,
						ray.color);
			}
			else if (-y > xabs && -y > zabs) {
				double alpha = 1 / yabs;
				skybox[SKYBOX_DOWN].getColor(
						(1 + x*alpha)/2.0,
						(1 - z*alpha)/2.0,
						ray.color);
			}
			break;
		}
		default:
			break;
		}
		ray.hit = true;
	}

	/**
	 * Panormaic skymap color
	 * @param ray
	 * @param blackBelowHorizon
	 */
	public void getSkyColor(Ray ray, boolean blackBelowHorizon) {
		getSkyDiffuseColorInner(ray, blackBelowHorizon);
		ray.color.scale(skyLightModifier);
		ray.color.w = 1;
	}

	/**
	 * Bilinear interpolated panoramic skymap color
	 * @param ray
	 * @param blackBelowHorizon
	 */
	public void getSkyColorInterpolated(Ray ray, boolean blackBelowHorizon) {
		switch (mode) {
		case SKYMAP_PANORAMIC:
		{
			if (mirrored) {
				double theta = FastMath.atan2(ray.d.z, ray.d.x);
				theta += rotation;
				theta /= Constants.TAU;
				theta = (theta%1 + 1) % 1;
				double phi = Math.abs(Math.asin(ray.d.y)) / Constants.HALF_PI;
				skymap.getColorInterpolated(theta, phi, ray.color);
			} else {
				double theta = FastMath.atan2(ray.d.z, ray.d.x);
				theta += rotation;
				theta /= Constants.TAU;
				if (theta > 1 || theta < 0) {
					theta = (theta%1 + 1) % 1;
				}
				double phi = (Math.asin(ray.d.y) + Constants.HALF_PI) / Math.PI;
				skymap.getColorInterpolated(theta, phi, ray.color);
			}
			break;
		}
		case SKYMAP_SPHERICAL:
		{
			double cos = FastMath.cos(-rotation);
			double sin = FastMath.sin(-rotation);
			double x = cos*ray.d.x + sin*ray.d.z;
			double y = ray.d.y;
			double z = -sin*ray.d.x + cos*ray.d.z;
			double len = Math.sqrt(x*x + y*y);
			double theta = (len < Ray.EPSILON) ? 0 : Math.acos(-z)/Constants.TAU;
			double u = theta*x + .5;
			double v = .5 + theta*y;
			skymap.getColorInterpolated(u, v, ray.color);
			break;
		}
		case SKYBOX:
		{
			double cos = FastMath.cos(-rotation);
			double sin = FastMath.sin(-rotation);
			double x = cos*ray.d.x + sin*ray.d.z;
			double y = ray.d.y;
			double z = -sin*ray.d.x + cos*ray.d.z;
			double xabs = QuickMath.abs(x);
			double yabs = QuickMath.abs(y);
			double zabs = QuickMath.abs(z);
			if (y > xabs && y > zabs) {
				double alpha = 1 / yabs;
				skybox[SKYBOX_UP].getColorInterpolated(
						(1 + x*alpha)/2.0,
						(1 + z*alpha)/2.0,
						ray.color);
			}
			else if (-z > xabs && -z > yabs) {
				double alpha = 1 / zabs;
				skybox[SKYBOX_FRONT].getColorInterpolated(
						(1 + x*alpha)/2.0,
						(1 + y*alpha)/2.0,
						ray.color);
			}
			else if (z > xabs && z > yabs) {
				double alpha = 1 / zabs;
				skybox[SKYBOX_BACK].getColorInterpolated(
						(1 - x*alpha)/2.0,
						(1 + y*alpha)/2.0,
						ray.color);
			}
			else if (-x > zabs && -x > yabs) {
				double alpha = 1 / xabs;
				skybox[SKYBOX_LEFT].getColorInterpolated(
						(1 - z*alpha)/2.0,
						(1 + y*alpha)/2.0,
						ray.color);
			}
			else if (x > zabs && x > yabs) {
				double alpha = 1 / xabs;
				skybox[SKYBOX_RIGHT].getColorInterpolated(
						(1 + z*alpha)/2.0,
						(1 + y*alpha)/2.0,
						ray.color);
			}
			else if (-y > xabs && -y > zabs) {
				double alpha = 1 / yabs;
				skybox[SKYBOX_DOWN].getColorInterpolated(
						(1 + x*alpha)/2.0,
						(1 - z*alpha)/2.0,
						ray.color);
			}
			break;
		}
		default:
			getSkyDiffuseColorInner(ray, blackBelowHorizon);
		}
		if (scene.sunEnabled) {
			addSunColor(ray);
		}
		ray.hit = true;
		//ray.color.scale(skyLightModifier);
		ray.color.w = 1;
	}

	/**
	 * Get the specular sky color for the ray
	 * @param ray
	 * @param blackBelowHorizon
	 */
	public void getSkySpecularColor(Ray ray, boolean blackBelowHorizon) {
		getSkyColor(ray, blackBelowHorizon);
		if (scene.sunEnabled) {
			addSunColor(ray);
		}
	}

	/**
	 * Add sun color contribution. This does not alpha blend the sun color
	 * because the Minecraft sun texture has no alpha channel.
	 * @param ray
	 */
	private void addSunColor(Ray ray) {
		double r = ray.color.x;
		double g = ray.color.y;
		double b = ray.color.z;
		if (scene.sun().intersect(ray)) {
			// blend sun color with current color
			ray.color.x = ray.color.x + r;
			ray.color.y = ray.color.y + g;
			ray.color.z = ray.color.z + b;
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
	 * Set the sky rendering mode
	 * @param newMode
	 */
	public void setSkyMode(SkyMode newMode) {
		if (this.mode != newMode) {
			this.mode = newMode;
			if (newMode != SkyMode.SKYMAP_PANORAMIC && newMode != SkyMode.SKYMAP_SPHERICAL) {
				skymapFileName = "";
				skymap = Texture.EMPTY_TEXTURE;
			}
			if (newMode != SkyMode.SKYBOX) {
				for (int i = 0; i < 6; ++i) {
					skybox[i] = Texture.EMPTY_TEXTURE;
					skyboxFileName[i] = "";
				}
			}
			scene.refresh();
		}
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
		sky.add("skyYaw", rotation);
		sky.add("skyMirrored", mirrored);
		sky.add("skyLight", skyLightModifier);
		sky.add("mode", mode.name());
		sky.add("horizonOffset", horizonOffset);
		sky.add("cloudsEnabled", cloudsEnabled);
		sky.add("cloudSize", cloudSize);
		sky.add("cloudOffset", cloudOffset.toJson());

		// always save gradient
		sky.add("gradient", gradientJson(gradient));

		switch (mode) {
		case SKYMAP_PANORAMIC:
		case SKYMAP_SPHERICAL:
		{
			if (!skymap.isEmptyTexture()) {
				sky.add("skymap", skymapFileName);
			}
			break;
		}
		case SKYBOX:
		{
			JsonArray array = new JsonArray();
			for (int i = 0; i < 6; ++i) {
				if (!skybox[i].isEmptyTexture()) {
					array.add(skyboxFileName[i]);
				} else {
					array.add(new JsonNull());
				}
			}
			sky.add("skybox", array);
			break;
		}
		default:
			break;
		}
		return sky;
	}

	@Override
	public void fromJson(JsonObject sky) {
		rotation = sky.get("skyYaw").doubleValue(0);
		mirrored = sky.get("skyMirrored").boolValue(true);
		skyLightModifier = sky.get("skyLight").doubleValue(DEFAULT_INTENSITY);
		mode = SkyMode.get(sky.get("mode").stringValue(""));
		horizonOffset = sky.get("horizonOffset").doubleValue(0.0);
		cloudsEnabled = sky.get("cloudsEnabled").boolValue(false);
		cloudSize = sky.get("cloudSize").doubleValue(DEFAULT_CLOUD_SIZE);
		cloudOffset.fromJson(sky.get("cloudOffset").object());

		List<Vector4d> theGradient = gradientFromJson(sky.get("gradient").array());
		if (theGradient != null && theGradient.size() >= 2) {
			gradient = theGradient;
		}

		switch (mode) {
		case SKYMAP_PANORAMIC:
		{
			skymapFileName = sky.get("skymap").stringValue("");
			if (skymapFileName.isEmpty()) {
				skymapFileName = sky.get("skymapFileName").stringValue("");
			}
			break;
		}
		case SKYBOX:
		{
			JsonArray array = sky.get("skybox").array();
			for (int i = 0; i < 6; ++i) {
				JsonValue value = array.get(i);
				skyboxFileName[i] = value.stringValue("");
			}
			break;
		}
		default:
			break;
		}
	}

	/**
	 * Set the sky light modifier
	 * @param newValue
	 */
	public void setSkyLight(double newValue) {
		skyLightModifier = newValue;
		scene.refresh();
	}

	/**
	 * @return Current sky light modifier
	 */
	public double getSkyLight() {
		return skyLightModifier;
	}

	public void setGradient(List<Vector4d> newGradient) {
		gradient = new ArrayList<Vector4d>(newGradient.size());
		for (Vector4d stop: newGradient) {
			gradient.add(new Vector4d(stop));
		}
		scene.refresh();
	}

	public List<Vector4d> getGradient() {
		List<Vector4d> copy = new ArrayList<Vector4d>(gradient.size());
		for (Vector4d stop: gradient) {
			copy.add(new Vector4d(stop));
		}
		return copy;
	}

	public static JsonArray gradientJson(Collection<Vector4d> gradient) {
		JsonArray array = new JsonArray();
		for (Vector4d stop: gradient) {
			JsonObject obj = new JsonObject();
			obj.add("rgb", Color.toString(stop.x, stop.y, stop.z));
			obj.add("pos", stop.w);
			array.add(obj);
		}
		return array;
	}

	/**
	 * @param array
	 * @return {@code null} if the gradient was not valid
	 */
	public static List<Vector4d> gradientFromJson(JsonArray array) {
		List<Vector4d> gradient = new ArrayList<Vector4d>(array.getNumElement());
		for (int i = 0; i < array.getNumElement(); ++i) {
			JsonObject obj = array.getElement(i).object();
			Vector3d color = new Vector3d();
			try {
				Color.fromString(obj.get("rgb").stringValue(""), 16, color);
				Vector4d stop = new Vector4d(color.x, color.y, color.z, obj.get("pos").doubleValue(Double.NaN));
				if (!Double.isNaN(stop.w)) {
					gradient.add(stop);
				}
			} catch (NumberFormatException e) {
			}
		}
		boolean errors = false;
		for (int i = 0; i < gradient.size(); ++i) {
			Vector4d stop = gradient.get(i);
			if (i == 0) {
				if (stop.w != 0) {
					errors = true;
					break;
				}
			} else if (i < gradient.size()-1) {
				if (stop.w < gradient.get(i-1).w) {
					errors = true;
					break;
				}
			} else {
				if (stop.w != 1) {
					errors = true;
					break;
				}
			}
		}
		if (errors) {
			// error in gradient data
			return null;
		} else {
			return gradient;
		}
	}

	public static void makeDefaultGradient(Collection<Vector4d> gradient) {
		gradient.add(new Vector4d(9/255., 183/255., 217/255., 0));
		gradient.add(new Vector4d(212/255., 245/255., 251/255., 1));
	}

	public void loadSkyboxTexture(String fileName, int index) {
		if (index < 0 || index >= 6) {
			throw new IllegalArgumentException();
		}
		skyboxFileName[index] = fileName;
		skybox[index] = loadSkyTexture(fileName, skybox[index]);
		scene.refresh();
	}

	private Texture loadSkyTexture(String fileName, Texture prevTexture) {
		File textureFile = new File(fileName);
		if (!textureFile.exists()) {
			return prevTexture;
		}
		if (textureFile.exists()) {
			try {
				logger.info("Loading sky map: " + fileName);
				if (fileName.toLowerCase().endsWith(".pfm")) {
					return new PFMTexture(textureFile);
				} else if (fileName.toLowerCase().endsWith(".hdr")) {
					return new HDRTexture(textureFile);
				} else {
					return new SkymapTexture(ImageIO.read(textureFile));
				}
			} catch (IOException e) {
				logger.warn("Could not load skymap: " + fileName);
			} catch (Throwable e) {
				logger.error("Unexpected exception ocurred!", e);
			}
		} else {
			logger.warn("Skymap could not be opened: " + fileName);
		}
		return prevTexture;
	}

	public void setHorizonOffset(double newValue) {
		newValue = Math.min(1, Math.max(0, newValue));
		if (newValue != horizonOffset) {
			horizonOffset = newValue;
			scene.refresh();
		}
	}

	public double getHorizonOffset() {
		return horizonOffset;
	}


	public void setCloudSize(double newValue) {
		if (newValue != cloudSize) {
			cloudSize = newValue;
			if (cloudsEnabled) {
				scene.refresh();
			}
		}
	}

	public double cloudSize() {
		return cloudSize;
	}

	public void setCloudXOffset(double newValue) {
		if (newValue != cloudOffset.x) {
			cloudOffset.x = newValue;
			if (cloudsEnabled) {
				scene.refresh();
			}
		}
	}

	/**
	 * Change the cloud height
	 * @param value
	 */
	public void setCloudYOffset(double newValue) {
		if (newValue != cloudOffset.y) {
			cloudOffset.y = newValue;
			if (cloudsEnabled) {
				scene.refresh();
			}
		}
	}
	public void setCloudZOffset(double newValue) {
		if (newValue != cloudOffset.z) {
			cloudOffset.z = newValue;
			if (cloudsEnabled) {
				scene.refresh();
			}
		}
	}

	public double cloudXOffset() {
		return cloudOffset.x;
	}

	/**
	 * @return The current cloud height
	 */
	public double cloudYOffset() {
		return cloudOffset.y;
	}

	public double cloudZOffset() {
		return cloudOffset.z;
	}


	/**
	 * Enable/disable clouds rendering
	 * @param newValue
	 */
	public void setCloudsEnabled(boolean newValue) {
		if (newValue != cloudsEnabled) {
			cloudsEnabled = newValue;
			scene.refresh();
		}
	}

	/**
	 * @return <code>true</code> if cloud rendering is enabled
	 */
	public boolean cloudsEnabled() {
		return cloudsEnabled;
	}

	public boolean cloudIntersection(Scene scene, Ray ray, Random random) {
		double offsetX = cloudOffset.x;
		double offsetY = cloudOffset.y;
		double offsetZ = cloudOffset.z;
		double inv_size = 1/scene.sky().cloudSize();
		double cloudBot = offsetY - scene.origin.y;
		double cloudTop = offsetY - scene.origin.y + 5;
		int target = 1;
		double t_offset = 0;
		ray.tNear = Double.POSITIVE_INFINITY;
		if (ray.x.y < cloudBot || ray.x.y > cloudTop) {
			if (ray.d.y > 0) {
				t_offset = (cloudBot - ray.x.y) / ray.d.y;
			} else {
				t_offset = (cloudTop - ray.x.y) / ray.d.y;
			}
			if (t_offset < 0) {
				return false;
			}
			// ray is entering cloud
			if (inCloud((ray.d.x*t_offset + ray.x.x)*inv_size + offsetX, (ray.d.z*t_offset + ray.x.z)*inv_size + offsetZ)) {
				ray.n.set(0, -Math.signum(ray.d.y), 0);
				onCloudEnter(ray, t_offset, random);
				return true;
			}
		} else if (inCloud(ray.x.x*inv_size + offsetX, ray.x.z*inv_size + offsetZ)) {
			target = 0;
		}
		double tExit = Double.MAX_VALUE;
		if (ray.d.y > 0) {
			tExit = (cloudTop - ray.x.y) / ray.d.y - t_offset;
		} else {
			tExit = (cloudBot - ray.x.y) / ray.d.y - t_offset;
		}
		double x0 = (ray.x.x + ray.d.x*t_offset)*inv_size + offsetX;
		double z0 = (ray.x.z + ray.d.z*t_offset)*inv_size + offsetZ;
		double xp = x0;
		double zp = z0;
		int ix = (int) Math.floor(xp);
		int iz = (int) Math.floor(zp);
		int xmod = (int)Math.signum(ray.d.x), zmod = (int)Math.signum(ray.d.z);
		int xo = (1+xmod)/2, zo = (1+zmod)/2;
		double dx = Math.abs(ray.d.x)*inv_size;
		double dz = Math.abs(ray.d.z)*inv_size;
		double t = 0;
		int i = 0;
		int nx = 0, nz = 0;
		if (dx > dz) {
			double m = dz/dx;
			double xrem = xmod * (ix+xo - xp);
			double zlimit = xrem*m;
			while (t < tExit) {
				double zrem = zmod * (iz+zo - zp);
				if (zrem < zlimit) {
					iz += zmod;
					if (Clouds.getCloud(ix, iz) == target) {
						t = i/dx + zrem/dz;
						nx = 0;
						nz = -zmod;
						break;
					}
					ix += xmod;
					if (Clouds.getCloud(ix, iz) == target) {
						t = (i+xrem)/dx;
						nx = -xmod;
						nz = 0;
						break;
					}
				} else {
					ix += xmod;
					if (Clouds.getCloud(ix, iz) == target) {
						t = (i+xrem)/dx;
						nx = -xmod;
						nz = 0;
						break;
					}
					if (zrem <= m) {
						iz += zmod;
						if (Clouds.getCloud(ix, iz) == target) {
							t = i/dx + zrem/dz;
							nx = 0;
							nz = -zmod;
							break;
						}
					}
				}
				t = i/dx;
				i+=1;
				zp = z0 + zmod*i*m;
			}
		} else {
			double m = dx/dz;
			double zrem = zmod * (iz+zo - zp);
			double xlimit = zrem*m;
			while (t < tExit) {
				double xrem = xmod * (ix+xo - xp);
				if (xrem < xlimit) {
					ix += xmod;
					if (Clouds.getCloud(ix, iz) == target) {
						t = i/dz + xrem/dx;
						nx = -xmod;
						nz = 0;
						break;
					}
					iz += zmod;
					if (Clouds.getCloud(ix, iz) == target) {
						t = (i+zrem)/dz;
						nx = 0;
						nz = -zmod;
						break;
					}
				} else {
					iz += zmod;
					if (Clouds.getCloud(ix, iz) == target) {
						t = (i+zrem)/dz;
						nx = 0;
						nz = -zmod;
						break;
					}
					if (xrem <= m) {
						ix += xmod;
						if (Clouds.getCloud(ix, iz) == target) {
							t = i/dz + xrem/dx;
							nx = -xmod;
							nz = 0;
							break;
						}
					}
				}
				t = i/dz;
				i+=1;
				xp = x0 + xmod*i*m;
			}
		}
		int ny = 0;
		if (target == 1) {
			if (t > tExit) {
				return false;
			}
			ray.n.set(nx, ny, nz);
			onCloudEnter(ray, t+t_offset, random);
			return true;
		} else {
			if (t > tExit) {
				nx = 0;
				ny = (int) Math.signum(ray.d.y);
				nz = 0;
				t = tExit;
			} else {
				nx = -nx;
				nz = -nz;
			}
			if (t > .2) {
				onCloudExit(ray, .2, random);
			} else {
				ray.n.set(nx, ny, nz);
				onCloudExit(ray, t, random);

			}
		}
		return true;
	}

	private static void onCloudEnter(Ray ray, double t, Random random) {
		ray.scatterNormal(random);
		ray.tNear = t;
		ray.distance += t;
		ray.color.set(1,1,1,0);
	}

	private static void onCloudExit(Ray ray, double t, Random random) {
		//ray.diffuseReflection(ray, random);
		ray.tNear = t;
		ray.distance += t;
		ray.color.set(1,1,1,0.075*t*5);
	}

	private static boolean inCloud(double x, double z) {
		return Clouds.getCloud((int)Math.floor(x), (int)Math.floor(z)) == 1;
	}

}
