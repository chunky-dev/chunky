/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer;

import java.awt.Color;
import java.util.Random;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.DoubleTag;
import se.llbit.util.VectorPool;

/**
 * Sun model for ray tracing
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Sun {
	
	/**
	 * Default sun intensity
	 */
	public static final double DEFAULT_INTENSITY = 1.5;
	
	/**
	 * Maximum sun intensity
	 */
	public static final double MAX_INTENSITY = 10;
	
	/**
	 * Minimum sun intensity
	 */
	public static final double MIN_INTENSITY = 0;
	
	private static final double xZenithChroma[][] = {
		{0.00166, -0.00375, 0.00209, 0},
		{-0.02903, 0.06377, -0.03203, 0.00394},
		{0.11693, -0.21196, 0.06052, 0.25886},
	};
	private static final double yZenithChroma[][] = {
		{ 0.00275, -0.00610, 0.00317, 0},
		{-0.04214, 0.08970, -0.04153, 0.00516},
		{0.15346, -0.26756, 0.06670, 0.26688},
	};
	private static final double mdx[][] = {
		{ -0.0193, -0.2592 },
		{ -0.0665,  0.0008 },
		{ -0.0004,  0.2125 },
		{ -0.0641, -0.8989 },
		{ -0.0033,  0.0452 } };
	private static final double mdy[][] = {
		{ -0.0167, -0.2608 },
		{ -0.0950,  0.0092 },
		{ -0.0079,  0.2102 },
		{ -0.0441, -1.6537 },
		{ -0.0109,  0.0529 } };
	private static final double mdY[][] = {
		{  0.1787, -1.4630 },
		{ -0.3554,  0.4275 },
		{ -0.0227,  5.3251 },
		{  0.1206, -2.5771 },
		{ -0.0670,  0.3703 } };
	
	private static double turb = 2.5;
	private static double turb2 = turb*turb;
	private static Vector3d A = new Vector3d();
	private static Vector3d B = new Vector3d();
	private static Vector3d C = new Vector3d();
	private static Vector3d D = new Vector3d();
	private static Vector3d E = new Vector3d();

	/**
	 * Sun texture
	 */
	public static Texture texture = new Texture();
	
	static {
		A.x = mdx[0][0] * turb + mdx[0][1];
		B.x = mdx[1][0] * turb + mdx[1][1];
		C.x = mdx[2][0] * turb + mdx[2][1];
		D.x = mdx[3][0] * turb + mdx[3][1];
		E.x = mdx[4][0] * turb + mdx[4][1];
		
		A.y = mdy[0][0] * turb + mdy[0][1];
		B.y = mdy[1][0] * turb + mdy[1][1];
		C.y = mdy[2][0] * turb + mdy[2][1];
		D.y = mdy[3][0] * turb + mdy[3][1];
		E.y = mdy[4][0] * turb + mdy[4][1];
		
		A.z = mdY[0][0] * turb + mdY[0][1];
		B.z = mdY[1][0] * turb + mdY[1][1];
		C.z = mdY[2][0] * turb + mdY[2][1];
		D.z = mdY[3][0] * turb + mdY[3][1];
		E.z = mdY[4][0] * turb + mdY[4][1];
	}
	
	private double zenith_Y;
	private double zenith_x;
	private double zenith_y;
	private double f0_Y;
	private double f0_x;
	private double f0_y;

	private final Scene scene;
	
	/**
	 * Sun radius
	 */
	public static final double RADIUS = .03;
	@SuppressWarnings("javadoc")
	public static final double RADIUS_COS = Math.cos(RADIUS);
	@SuppressWarnings("javadoc")
	public static final double RADIUS_COS_2 = Math.cos(RADIUS*2);
	@SuppressWarnings("javadoc")
	public static final double RADIUS_SIN = Math.sin(RADIUS);
	@SuppressWarnings("javadoc")
	public static final double RADIUS_COS_SQ = RADIUS_COS * RADIUS_COS;
	@SuppressWarnings("javadoc")
	public static final double SUN_WEIGHT =
		1 - Math.sqrt(1 - RADIUS_SIN*RADIUS_SIN);

	private static final double AMBIENT = .3;
	
	private double intensity = DEFAULT_INTENSITY;
	
	private double polarAngle = Math.PI / 2.5;
	private double thetaZ = Math.PI / 3;
	
	private Vector3d su = new Vector3d();
	private Vector3d sv = new Vector3d();
	private Vector3d sw = new Vector3d();
	
	final Vector3d emittance = new Vector3d(1, 1, 1);
	private final Vector3d color = new Vector3d(1, 1, 1);
	
	/**
	 * Calculate skylight for ray
	 * @param ray
	 */
	public void skylight(Ray ray) {
		Vector4d c = ray.color;
		
		if (ray.d.y < 0)
			ray.d.y = -ray.d.y;
		double cosTheta = ray.d.y;
		double cosGamma = ray.d.dot(sw);
		double gamma = Math.acos(cosGamma);
		double cos2Gamma = cosGamma * cosGamma;
		c.x = zenith_x * perezF(cosTheta, gamma, cos2Gamma, A.x, B.x, C.x, D.x, E.x) * f0_x;
		c.y = zenith_y * perezF(cosTheta, gamma, cos2Gamma, A.y, B.y, C.y, D.y, E.y) * f0_y;
		c.z = zenith_Y * perezF(cosTheta, gamma, cos2Gamma, A.z, B.z, C.z, D.z, E.z) * f0_Y;
		c.z = 1 - Math.exp(-(1/17.) * c.z);
		//c.z /= 20;
		if (c.y <= Ray.EPSILON) {
    		c.set(0, 0, 0, 1);
    	} else {
    		double f = (c.z / c.y);
    		c.set(c.x * f, c.z, (1 - c.x - c.y) * f, 1);
    	}
		c.set(3.2410*c.x - 1.5374*c.y - 0.4986*c.z,
				c.y = -0.9692*c.x + 1.8760*c.y + 0.0416*c.z,
				0.0556*c.x - 0.2040*c.y + 1.0570*c.z, 1);
	}
	
	private double chroma(double turb, double turb2, double sunTheta,
			double[][] matrix) {
		
		double t1 = sunTheta;
		double t2 = t1*t1;
		double t3 = t1*t2;
		
		return turb2 * (matrix[0][0]*t3 + matrix[0][1]*t2 + matrix[0][2]*t1 + matrix[0][3]) +
				turb * (matrix[1][0]*t3 + matrix[1][1]*t2 + matrix[1][2]*t1 + matrix[1][3]) +
				(matrix[2][0]*t3 + matrix[2][1]*t2 + matrix[2][2]*t1 + matrix[2][3]);
	}

	private static double perezF(double cosTheta, double gamma, double cos2Gamma,
			double A, double B, double C, double D, double E) {
		
		return (1 + A * Math.exp(B / cosTheta)) * (1 + C * Math.exp(D*gamma) + E * cos2Gamma);
	}
	
	/**
	 * Create new sun model
	 * @param scene
	 */
	public Sun(Scene scene) {
		this.scene = scene;
		initSun();
	}
	
	/**
	 * Set equal to other sun model
	 * @param other
	 */
	public void set(Sun other) {
		polarAngle = other.polarAngle;
		thetaZ = other.thetaZ;
		color.set(other.color);
		intensity = other.intensity;
		initSun();
	}
	
	private void initSun() {
		double theta = polarAngle;
		double phi = thetaZ;
		
		sw.x = Math.cos(theta);
		sw.y = Math.sin(phi);
		sw.z = Math.sin(theta);

		double r = Math.sqrt(sw.x*sw.x + sw.z*sw.z);
		r = Math.abs(Math.cos(phi) / r);
		
		sw.x *= r;
		sw.z *= r;
		
		if (Math.abs(sw.x) > .1)
			su.set(0, 1, 0);
		else
			su.set(1, 0, 0);
		sv.cross(sw, su);
		sv.normalize();
		su.cross(sv, sw);
		
		emittance.set(color);
		emittance.scale(Math.pow(intensity, Scene.DEFAULT_GAMMA));
		
		updateSkylightValues();
	}
	
	/**
	 * @return CompoundTag containing serialized sun model
	 */
	public CompoundTag store() {
		CompoundTag sunTag = new CompoundTag();
		sunTag.addItem("pitch", new DoubleTag(thetaZ));
		sunTag.addItem("yaw", new DoubleTag(polarAngle));
		sunTag.addItem("intensity", new DoubleTag(intensity));
		CompoundTag colorTag = new CompoundTag();
		colorTag.addItem("red", new DoubleTag(color.x));
		colorTag.addItem("green", new DoubleTag(color.y));
		colorTag.addItem("blue", new DoubleTag(color.z));
		sunTag.addItem("color", colorTag);
		return sunTag;
	}
	
	/**
	 * Deserialize the sun model from CompoundTag
	 * @param tag
	 */
	public void load(CompoundTag tag) {
		thetaZ = tag.get("pitch").doubleValue();
		polarAngle = tag.get("yaw").doubleValue();
		intensity = tag.get("intensity").doubleValue(DEFAULT_INTENSITY);

		if (tag.get("color").isCompoundTag()) {
			CompoundTag colorTag = (CompoundTag) tag.get("color");
			color.x = colorTag.get("red").doubleValue(1);
			color.y = colorTag.get("green").doubleValue(1);
			color.z = colorTag.get("blue").doubleValue(1);
		}
		
		initSun();
	}

	/**
	 * Set the sun polar angle
	 * @param value
	 */
	public void setPolarAngle(double value) {
		polarAngle = value;
		initSun();
		scene.refresh();
	}

	/**
	 * Set the sun zenith angle
	 * @param value
	 */
	public void setTheta(double value) {
		thetaZ = value;
		initSun();
		scene.refresh();
	}

	/**
	 * @return Zenith angle
	 */
	public double getTheta() {
		return thetaZ;
	}

	/**
	 * @return Polar angle
	 */
	public double getPolarAngle() {
		return polarAngle;
	}

	/**
	 * Check if the ray intersects the sun
	 * @param ray
	 * @return <code>true</code> if the ray intersects the sun model
	 */
	public boolean intersect(Ray ray) {
		/*double dot = ray.d.x * sw.x + ray.d.y * sw.y + ray.d.z * sw.z;
		if (dot >= RADIUS_COS) {
			ray.color.x = emittance.x * 3;
			ray.color.y = emittance.y * 3;
			ray.color.z = emittance.z * 3;
			ray.hit = true;
			return true;
		}*/
		
		if (ray.d.dot(sw) < .5)
			return false;
		
		double WIDTH = RADIUS*4;
		double WIDTH2 = WIDTH*2;
		double a;
		a = Math.PI/2 - Math.acos(ray.d.dot(su)) + WIDTH;
		if (a >= 0 && a < WIDTH2) {
			double b = Math.PI/2 - Math.acos(ray.d.dot(sv)) + WIDTH;
			if (b >= 0 && b < WIDTH2) {
				texture.getColor(a / WIDTH2, b / WIDTH2, ray.color);
				ray.color.x *= emittance.x * 10;
				ray.color.y *= emittance.y * 10;
				ray.color.z *= emittance.z * 10;
				ray.hit = true;
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Calculate flat shading for ray
	 * @param ray
	 */
	public void flatShading(Ray ray) {
		double shading = ray.n.x * sw.x + ray.n.y * sw.y + ray.n.z * sw.z;
		shading = Math.max(AMBIENT, shading);
		ray.color.x *= emittance.x * shading;
		ray.color.y *= emittance.y * shading;
		ray.color.z *= emittance.z * shading;
	}
	
	/**
	 * @param color
	 */
	public void setColor(java.awt.Color color) {
		this.color.x = Math.pow(color.getRed() / 255., Scene.DEFAULT_GAMMA);
		this.color.y = Math.pow(color.getGreen() / 255., Scene.DEFAULT_GAMMA);
		this.color.z = Math.pow(color.getBlue() / 255., Scene.DEFAULT_GAMMA);
		initSun();
		scene.refresh();
	}
	
	private void updateSkylightValues() {
		double sunTheta = Math.PI/2 - thetaZ;
		double cosTheta = Math.cos(sunTheta);
		double cos2Theta = cosTheta*cosTheta;
		double chi = (4.0/9.0 - turb/120.0)*(Math.PI - 2*sunTheta);
		zenith_Y = (4.0453*turb - 4.9710)*Math.tan(chi) - 0.2155*turb + 2.4192;
		zenith_Y = (zenith_Y < 0) ? -zenith_Y : zenith_Y;
		zenith_x = chroma(turb, turb2, sunTheta, xZenithChroma);
		zenith_y = chroma(turb, turb2, sunTheta, yZenithChroma);
		f0_x = 1 / perezF(1, sunTheta, cos2Theta, A.x, B.x, C.x, D.x, E.x);
		f0_y = 1 / perezF(1, sunTheta, cos2Theta, A.y, B.y, C.y, D.y, E.y);
		f0_Y = 1 / perezF(1, sunTheta, cos2Theta, A.z, B.z, C.z, D.z, E.z);
	}

	/**
	 * Set the sun intensity
	 * @param value
	 */
	public void setIntensity(double value) {
		intensity = value;
		initSun();
		scene.refresh();
	}

	/**
	 * @return The sun intensity
	 */
	public double getIntensity() {
		return intensity;
	}

	/**
	 * Point ray in random direction within sun solid angle
	 * @param reflected
	 * @param random
	 * @param vectorPool
	 */
	public void getRandomSunDirection(Ray reflected, Random random, VectorPool vectorPool) {
		double x1 = random.nextDouble();
		double x2 = random.nextDouble();
		double cos_a = 1-x1 + x1*RADIUS_COS;
		double sin_a = Math.sqrt(1 - cos_a*cos_a);
		double phi = 2 * Math.PI * x2;

		Vector3d u = vectorPool.get(su);
		Vector3d v = vectorPool.get(sv);
		Vector3d w = vectorPool.get(sw);

		u.scale(Math.cos(phi)*sin_a);
		v.scale(Math.sin(phi)*sin_a);
		w.scale(cos_a);

		reflected.d.add(u, v);
		reflected.d.add(w);
		reflected.d.normalize();
		
		vectorPool.dispose(u);
		vectorPool.dispose(v);
		vectorPool.dispose(w);
	}

	/**
	 * Atmospheric extinction and inscattering of ray.
	 * @param ray
	 * @param s 
	 * @param attenuation 
	 */
	public void doAtmos(Ray ray, double s, double attenuation) {
		double Br = 0.00002*10;
		double Bm = 0.0007*10;
		double g = -.001*10000;
		double Fex = Math.exp(-(Br + Bm) * s);
		if (attenuation < Ray.EPSILON) {
			ray.color.x *= Fex;
			ray.color.y *= Fex;
			ray.color.z *= Fex;
		} else {
			double theta = ray.d.dot(sw);
			double cos_theta = Math.cos(theta);
			double cos2_theta = cos_theta*cos_theta;
			double Brt = (3 / (16*Math.PI)) * Br * (1 + cos2_theta);
			double Bmt = (1 / (4*Math.PI)) * Bm * ((1-g)*(1-g)) / Math.pow(1 + g*g + 2*g*cos_theta, 3/2.);
			double Fin = ((Brt + Bmt) / (Br + Bm)) * (1 - Fex);
			ray.color.x = ray.color.x * Fex + attenuation * Fin * emittance.x;
			ray.color.y = ray.color.y * Fex + attenuation * Fin * emittance.y;
			ray.color.z = ray.color.z * Fex + attenuation * Fin * emittance.z;
		}
	}

	/**
	 * @param d
	 * @return Cosine of angle between sun and vector
	 */
	public double theta(Vector3d d) {
		return d.dot(sw);
	}

	private static final double Br = 0.0002;
	private static final double Bm = 0.0009;
	private static final double g = -.0007;
	
	/**
	 * @param s
	 * @return Extinction factor
	 */
	public double extinction(double s) {
		return Math.exp(-(Br + Bm) * s);
	}
	
	/**
	 * @param Fex
	 * @param theta
	 * @return Inscatter factor
	 */
	public double inscatter(double Fex, double theta) {
		double cos_theta = Math.cos(theta);
		double cos2_theta = cos_theta*cos_theta;
		double Brt = (3 / (16*Math.PI)) * Br * (1 + cos2_theta);
		double Bmt = (1 / (4*Math.PI)) * Bm * ((1-g)*(1-g)) / Math.pow(1 + g*g + 2*g*cos_theta, 3/2.);
		return ((Brt + Bmt) / (Br + Bm)) * (1 - Fex);
	}

	/**
	 * @return An AWT Color object representing the current sun color
	 */
	public Color getAwtColor() {
		return new Color(
				(float) Math.min(1, color.x),
				(float) Math.min(1, color.y),
				(float) Math.min(1, color.z));
	}
}
