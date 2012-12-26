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
package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.BlockData;
import se.llbit.math.Color;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

@SuppressWarnings("javadoc")
public class RedstoneWireModel {
	private static final Quad[] quads = {
		// 0000 no connection
		new Quad(new Vector3d(11/16., 0, 5/16.), new Vector3d(5/16., 0, 5/16.),
			new Vector3d(11/16., 0, 11/16.), new Vector4d(11/16., 5/16., 11/16., 5/16.)),
		
		// 0001 east
		new Quad(new Vector3d(1, 0, 0), new Vector3d(0, 0, 0),
			new Vector3d(1, 0, 1), new Vector4d(1, 0, 1, 0)),
		
		// 0010 west
		new Quad(new Vector3d(1, 0, 0), new Vector3d(0, 0, 0),
			new Vector3d(1, 0, 1), new Vector4d(1, 0, 1, 0)),
		
		// 0011 east west 
		new Quad(new Vector3d(1, 0, 0), new Vector3d(0, 0, 0),
			new Vector3d(1, 0, 1), new Vector4d(1, 0, 1, 0)),
		
		// 0100 north
		new Quad(new Vector3d(1, 0, 1), new Vector3d(1, 0, 0),
			new Vector3d(0, 0, 1), new Vector4d(1, 0, 1, 0)),
		
		// 0101 north east
		new Quad(new Vector3d(1, 0, 0), new Vector3d(5/16., 0, 0),
			new Vector3d(1, 0, 11/16.), new Vector4d(1, 5/16., 1, 5/16.)),
		
		// 0110 north west
		new Quad(new Vector3d(11/16., 0, 0), new Vector3d(0, 0, 0),
			new Vector3d(11/16., 0, 11/16.), new Vector4d(11/16., 0, 1, 5/16.)),
		
		// 0111 north east west
		new Quad(new Vector3d(1, 0, 0), new Vector3d(0, 0, 0),
			new Vector3d(1, 0, 11/16.), new Vector4d(1, 0/16., 1, 5/16.)),
		
		// 1000 south
		new Quad(new Vector3d(1, 0, 1), new Vector3d(1, 0, 0),
			new Vector3d(0, 0, 1), new Vector4d(1, 0, 1, 0)),
		
		// 1001 south east
		new Quad(new Vector3d(1, 0, 5/16.), new Vector3d(5/16., 0, 5/16.),
			new Vector3d(1, 0, 1), new Vector4d(1, 5/16., 11/16., 0)),
		
		// 1010 south west
		new Quad(new Vector3d(11/16., 0, 5/16.), new Vector3d(0, 0, 5/16.),
			new Vector3d(11/16., 0, 1), new Vector4d(11/16., 0, 11/16., 0)),
		
		// 1011 south east west
		new Quad(new Vector3d(16/16., 0, 5/16.), new Vector3d(0/16., 0, 5/16.),
			new Vector3d(16/16., 0, 1), new Vector4d(16/16., 0/16., 11/16., 0)),
		
		// 1100 north south
		new Quad(new Vector3d(1, 0, 1), new Vector3d(1, 0, 0),
			new Vector3d(0, 0, 1), new Vector4d(1, 0, 1, 0)),
		
		// 1101 north south east
		new Quad(new Vector3d(1, 0, 0), new Vector3d(5/16., 0, 0),
			new Vector3d(1, 0, 1), new Vector4d(1, 5/16., 1, 0)),
		
		// 1110 north south west
		new Quad(new Vector3d(11/16., 0, 0), new Vector3d(0, 0, 0),
			new Vector3d(11/16., 0, 1), new Vector4d(11/16., 0, 1, 0)),
		
		// 1111 north south east west
		new Quad(new Vector3d(1, 0, 0), new Vector3d(0, 0, 0),
			new Vector3d(1, 0, 1), new Vector4d(1, 0, 1, 0)),
	};
	
	private static final Quad eastSide = new Quad(
			new Vector3d(1, 1, 0), new Vector3d(1, 0, 0),
			new Vector3d(1, 1, 1), new Vector4d(1, 0, 1, 0));
	private static final Quad  westSide = new Quad(
			new Vector3d(0, 1, 1), new Vector3d(0, 0, 1),
			new Vector3d(0, 1, 0), new Vector4d(1, 0, 1, 0));
	private static final Quad northSide = new Quad(
			new Vector3d(0, 1, 0), new Vector3d(0, 0, 0),
			new Vector3d(1, 1, 0), new Vector4d(1, 0, 1, 0));
	private static final Quad southSide = new Quad(
			new Vector3d(1, 1, 1), new Vector3d(1, 0, 1),
			new Vector3d(0, 1, 1), new Vector4d(1, 0, 1, 0));
	
	private static final Texture[] tex = {
		Texture.redstoneWireCross,
		Texture.redstoneWire,
		Texture.redstoneWire,
		Texture.redstoneWire,
		Texture.redstoneWire,
		Texture.redstoneWireCross,
		Texture.redstoneWireCross,
		Texture.redstoneWireCross,
		Texture.redstoneWire,
		Texture.redstoneWireCross,
		Texture.redstoneWireCross,
		Texture.redstoneWireCross,
		Texture.redstoneWire,
		Texture.redstoneWireCross,
		Texture.redstoneWireCross,
		Texture.redstoneWireCross,
	};
	
	private static final float[][] wireColor = new float[16][3];
	
	static {
		float[] color0 = new float[3];
		float[] color1 = new float[3];
		Color.getRGBComponents(0x4D0000, color0);
		Color.toLinear(color0);
		Color.getRGBComponents(0xFD3100, color1);
		Color.toLinear(color1);
		for (int i = 0; i < 16; ++i) {
			wireColor[i][0] = color0[0] + (i / 15.f) * (color1[0] - color0[0]);
			wireColor[i][1] = color0[1] + (i / 15.f) * (color1[1] - color0[1]);
			wireColor[i][2] = color0[2] + (i / 15.f) * (color1[2] - color0[2]);
		}
	}

	public static boolean intersect(Ray ray) {
		int data = ray.currentMaterial;
		boolean hit = false;
		int power = ray.getBlockData();
		int connection = 0xF & (data >> BlockData.RSW_EAST_CONNECTION);
		ray.t = Double.POSITIVE_INFINITY;
		Quad quad = quads[connection];
		if (quad.intersect(ray)) {
			float[] color = tex[connection].getColor(ray.u, ray.v);
			if (color[3] > Ray.EPSILON) {
				ray.color.x = color[0] * wireColor[power][0];
				ray.color.y = color[1] * wireColor[power][1];
				ray.color.z = color[2] * wireColor[power][2];
				ray.color.w = color[3];
				ray.n.set(quad.n);
				ray.t = ray.tNear;
				hit = true;
			}
		}
		if ((data & (1 << BlockData.RSW_EAST_SIDE)) != 0) {
			if (eastSide.intersect(ray)) {
				float[] color = Texture.redstoneWire.getColor(ray.u, ray.v);
				if (color[3] > Ray.EPSILON) {
					ray.color.x = color[0] * wireColor[power][0];
					ray.color.y = color[1] * wireColor[power][1];
					ray.color.z = color[2] * wireColor[power][2];
					ray.color.w = color[3];
					ray.n.set(eastSide.n);
					ray.t = ray.tNear;
					hit = true;
				}
			}
		}
		if ((data & (1 << BlockData.RSW_WEST_SIDE)) != 0) {
			if (westSide.intersect(ray)) {
				float[] color = Texture.redstoneWire.getColor(ray.u, ray.v);
				if (color[3] > Ray.EPSILON) {
					ray.color.x = color[0] * wireColor[power][0];
					ray.color.y = color[1] * wireColor[power][1];
					ray.color.z = color[2] * wireColor[power][2];
					ray.color.w = color[3];
					ray.n.set(westSide.n);
					ray.t = ray.tNear;
					hit = true;
				}
			}
		}
		if ((data & (1 << BlockData.RSW_NORTH_SIDE)) != 0) {
			if (northSide.intersect(ray)) {
				float[] color = Texture.redstoneWire.getColor(ray.u, ray.v);
				if (color[3] > Ray.EPSILON) {
					ray.color.x = color[0] * wireColor[power][0];
					ray.color.y = color[1] * wireColor[power][1];
					ray.color.z = color[2] * wireColor[power][2];
					ray.color.w = color[3];
					ray.n.set(northSide.n);
					ray.t = ray.tNear;
					hit = true;
				}
			}
		}
		if ((data & (1 << BlockData.RSW_SOUTH_SIDE)) != 0) {
			if (southSide.intersect(ray)) {
				float[] color = Texture.redstoneWire.getColor(ray.u, ray.v);
				if (color[3] > Ray.EPSILON) {
					ray.color.x = color[0] * wireColor[power][0];
					ray.color.y = color[1] * wireColor[power][1];
					ray.color.z = color[2] * wireColor[power][2];
					ray.color.w = color[3];
					ray.n.set(southSide.n);
					ray.t = ray.tNear;
					hit = true;
				}
			}
		}
		if (hit) {
			ray.distance += ray.tNear;
			ray.x.scaleAdd(ray.tNear, ray.d, ray.x);
			return true;
		}
		return false;
	}
}
