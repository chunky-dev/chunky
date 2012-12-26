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
package se.llbit.chunky.renderer.test;

import se.llbit.chunky.model.BrewingStandModel;
import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.RedstoneRepeaterModel;
import se.llbit.chunky.model.RedstoneWireModel;
import se.llbit.chunky.model.SignPostModel;
import se.llbit.chunky.model.SpriteModel;
import se.llbit.chunky.model.TripwireHookModel;
import se.llbit.chunky.model.VineModel;
import se.llbit.chunky.model.WaterModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.BlockData;
import se.llbit.math.AABB;
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Triangle;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("unused")
public class TestModel {
	
	private AABB[] boxes;
	private Quad[] quads;
	private Triangle[] triangles;
	private Vector3d light;
	
	/**
	 * Set up the model
	 */
	public void setUp() {
		// north east
		quads = new Quad[] {
		// east
		new Quad(new Vector3d(1, 1, 0), new Vector3d(1, 0, 0),
			new Vector3d(1, 1, 1), new Vector4d(1, 0, 1, 0)),
		
		// west
		new Quad(new Vector3d(0, 1, 1), new Vector3d(0, 0, 1),
			new Vector3d(0, 1, 0), new Vector4d(1, 0, 1, 0)),
		
		// north
		new Quad(new Vector3d(0, 1, 0), new Vector3d(0, 0, 0),
			new Vector3d(1, 1, 0), new Vector4d(1, 0, 1, 0)),
		
		// south
		new Quad(new Vector3d(1, 1, 1), new Vector3d(1, 0, 1),
			new Vector3d(0, 1, 1), new Vector4d(1, 0, 1, 0)),
		};
		
		light = new Vector3d(.1, 1, -.1);
		light.normalize();
	}
	
	/**
	 * Ray-model intersection
	 * @param ray
	 */
	public void intersect(Ray ray) {
		int c0 = 4;
		int c1 = 0;
		int c2 = 0;
		int c3 = 0;
		int isFull = 1;
		int level = 0;
		ray.currentMaterial = (level << 8) | (isFull << 12) |
				(c0 << 16) | (c1 << 20) | (c2 << 24) | (c3 << 28);
		if (WaterModel.intersect(ray)) {
			double dot = -ray.n.dot(ray.d);
			ray.color.scale(dot);
		}
	}

}
