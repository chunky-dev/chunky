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
package se.llbit.math;

import se.llbit.chunky.renderer.Scene;

/**
 * Color utility methods.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public final class Color {
    
    private Color() { }
    
    /**
     * @param c RGB color vector
     * @return INT RGB value corresponding to the given color vector
     */
    public static final int getRGB(Vector3d c) {
        return (0xFF<<24) |
        		((int)(255 * c.x + .5) << 16) |
                ((int)(255 * c.y + .5) << 8) |
                (int)(255 * c.z + .5);
    }

    /**
     * @param r 
     * @param g 
     * @param b 
     * @return INT RGB value corresponding to the given color
     */
    public static final int getRGB(float r, float g, float b) {
        return 0xFF000000 |
            ((int)(255 * r + .5f) << 16) |
            ((int)(255 * g + .5f) << 8) |
            (int)(255 * b + .5f);
    }
    
    /**
     * @param r
     * @param g
     * @param b
     * @return INT RGB value corresponding to the given color
     */
    public static final int getRGB(double r, double g, double b) {
        return 0xFF000000 |
            ((int)(255 * r + .5) << 16) |
            ((int)(255 * g + .5) << 8) |
            (int)(255 * b + .5);
    }
    
    /**
     * @param r
     * @param g
     * @param b
     * @param a
     * @return INT ARGB value corresponding to the given color
     */
    public static final int getRGBA(float r, float g, float b, float a) {
        return ((int)(255 * a + .5f) << 24) |
            ((int)(255 * r + .5f) << 16) |
            ((int)(255 * g + .5f) << 8) |
            (int)(255 * b + .5f);
    }
    
    /**
     * @param c RGBA color vector
     * @return INT ARGB value corresponding to the given color vector
     */
    public static final int getRGBA(Vector4d c) {
        return ((int)(255 * c.w + .5f) << 24) |
            ((int)(255 * c.x + .5f) << 16) |
            ((int)(255 * c.y + .5f) << 8) |
            (int)(255 * c.z + .5f);
    }
    
    /**
     * @param c RGB color vector
     * @return INT RGB value corresponding to the given color vector
     */
    public static final int getRGB(Vector4d c) {
        return 0xFF000000 |
            ((int)(255 * c.x + .5f) << 16) |
            ((int)(255 * c.y + .5f) << 8) |
            (int)(255 * c.z + .5f);
    }
    
    /**
     * @param r
     * @param g
     * @param b
     * @param a
     * @return INT ARGB value corresponding to the given color
     */
    public static final int getRGBA(double r, double g, double b, double a) {
        return ((int)(255 * a + .5) << 24) |
            ((int)(255 * r + .5) << 16) |
            ((int)(255 * g + .5) << 8) |
            (int)(255 * b + .5);
    }

    /**
     * Get the RGB color components from an INT RGB value
     * @param irgb
     * @param frgb
     */
    public static final void getRGBComponents(int irgb, float[] frgb) {
        frgb[0] = (0xFF & (irgb >> 16)) / 255.f;
        frgb[1] = (0xFF & (irgb >> 8)) / 255.f;
        frgb[2] = (0xFF & irgb) / 255.f;
    }
    
    /**
     * Get the RGB color components from an INT RGB value
     * @param irgb
     * @param v
     */
    public static final void getRGBComponents(int irgb, Vector4d v) {
        v.x = (0xFF & (irgb >> 16)) / 255.f;
        v.y = (0xFF & (irgb >> 8)) / 255.f;
        v.z = (0xFF & irgb) / 255.f;
    }
    
    /**
     * Get the RGB color components from an INT RGB value
     * @param irgb
     * @param frgb
     */
    public static final void getRGBComponents(int irgb, double[] frgb) {
        frgb[0] = (0xFF & (irgb >> 16)) / 255.0;
        frgb[1] = (0xFF & (irgb >> 8)) / 255.0;
        frgb[2] = (0xFF & irgb) / 255.0;
    }
    
    /**
     * Get the RGBA color components from an INT ARGB value
     * @param irgb
     * @param frgb
     */
    public static final void getRGBAComponents(int irgb, float[] frgb) {
        frgb[3] = (irgb >>> 24) / 255.f;
        frgb[0] = (0xFF & (irgb >> 16)) / 255.f;
        frgb[1] = (0xFF & (irgb >> 8)) / 255.f;
        frgb[2] = (0xFF & irgb) / 255.f;
    }

    /**
     * Get the RGBA color components from an INT ARGB value
     * @param irgb
     * @param v
     */
    public static final void getRGBAComponents(int irgb, Vector4d v) {
        v.w = (irgb >>> 24) / 255.f;
        v.x = (0xFF & (irgb >> 16)) / 255.f;
        v.y = (0xFF & (irgb >> 8)) / 255.f;
        v.z = (0xFF & irgb) / 255.f;
    }

    /**
     * Get the RGBA color components from an INT ARGB value
     * @param irgb
     * @param frgb
     */
    public static final void getRGBAComponents(int irgb, double[] frgb) {
        frgb[3] = (irgb >>> 24) / 255.0;
        frgb[0] = (0xFF & (irgb >> 16)) / 255.0;
        frgb[1] = (0xFF & (irgb >> 8)) / 255.0;
        frgb[2] = (0xFF & irgb) / 255.0;
    }

    /**
     * @param frgb
     * @return Get INT RGB value corresponding to the given color
     */
    public static int getRGB(float[] frgb) {
        return 0xFF000000 |
            ((int)(255 * frgb[0] + .5f) << 16) |
            ((int)(255 * frgb[1] + .5f) << 8) |
            (int)(255 * frgb[2] + .5f);
    }
    
    /**
     * @param frgb
     * @return Get INT RGB value corresponding to the given color
     */
    public static int getRGB(double[] frgb) {
        return 0xFF000000 |
            ((int)(255 * frgb[0] + .5) << 16) |
            ((int)(255 * frgb[1] + .5) << 8) |
            (int)(255 * frgb[2] + .5);
    }

    /**
     * Transform from xyY colorspace to XYZ colorspace
     * @param in
     * @param out
     */
    public static void xyYtoXYZ(Vector3d in, Vector3d out) {
    	if (in.y <= Ray.EPSILON) {
    		out.set(0, 0, 0);
    		return;
    	}
    	double f = (in.z / in.y);
    	out.x = in.x * f;
    	out.z = (1 - in.x - in.y) * f;
    	out.y = in.z;
    }
    
    /**
     * http://www.w3.org/Graphics/Color/sRGB
     * @param in
     * @param out
     */
	public static void XYZtoRGB(Vector3d in, Vector3d out) {
		out.x = 3.2410*in.x - 1.5374*in.y - 0.4986*in.z;
	    out.y = -0.9692*in.x + 1.8760*in.y + 0.0416*in.z;
	    out.z = 0.0556*in.x - 0.2040*in.y + 1.0570*in.z;
	}

	/**
	 * Convert color components to linear color space
	 * @param components
	 */
	public static void toLinear(float[] components) {
		for (int i = 0; i < components.length; ++i) {
			components[i] = (float) Math.pow(components[i], Scene.DEFAULT_GAMMA);
		}
	}
}
