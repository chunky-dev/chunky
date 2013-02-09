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
import se.llbit.chunky.model.FenceGateModel;
import se.llbit.chunky.model.FenceModel;
import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.RedstoneRepeaterModel;
import se.llbit.chunky.model.RedstoneWireModel;
import se.llbit.chunky.model.SignPostModel;
import se.llbit.chunky.model.SpriteModel;
import se.llbit.chunky.model.TorchModel;
import se.llbit.chunky.model.TripwireHookModel;
import se.llbit.chunky.model.VineModel;
import se.llbit.chunky.model.WaterModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.BlockData;
import se.llbit.math.AABB;
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Triangle;
import se.llbit.math.UVTriangle;
import se.llbit.math.Vector2d;
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
	private UVTriangle[] uvtriangles;
	private Vector3d light;
	
	/**
	 * Set up the model
	 */
	public void setUp() {
		boxes = new AABB[] {
			// east
			new AABB(10/16., 11/16., 0, 6/16., 5/16., 11/16.),
			// west
			new AABB(5/16., 6/16., 0, 6/16., 5/16., 11/16.),
			// north
			new AABB(5/16., 11/16., 0, 6/16., 5/16., 6/16.),
			// south
			new AABB(5/16., 11/16., 0, 6/16., 10/16., 11/16.),
			// center
			new AABB(6/16., 10/16., 0, 4/16., 6/16., 10/16.),
		};
	}
	
	private void setUpTorch() {
		quads = new Quad[4];
		
		// west
		quads[0] = new Quad(new Vector3d(15/16., 3/16., 0),
				new Vector3d(15/16., 3/16., 1),
				new Vector3d((11-12/10.)/16., 1, 0),
				new Vector4d(0, 1, 0, 13/16.));
		
		// east
		quads[1] = new Quad(new Vector3d((13-12/10.)/16., 1, 0),
				new Vector3d((13-12/10.)/16., 1, 1),
				new Vector3d(17/16., 3/16., 0),
				new Vector4d(1, 0, 13/16., 0));
		
		// top
		quads[2] = new Quad(
				new Vector3d(13/16., 13/16., 9/16.),
				new Vector3d(13/16., 13/16., 7/16.),
				new Vector3d(11/16., 13/16., 9/16.),
				new Vector4d(9/16., 7/16., 10/16., 8/16.));
		
		// bottom
		quads[3] = new Quad(
				new Vector3d(15/16., 3/16., 7/16.),
				new Vector3d(17/16., 3/16., 7/16.),
				new Vector3d(15/16., 3/16., 9/16.),
				new Vector4d(7/16., 9/16., 0/16., 2/16.));
		
		uvtriangles = new UVTriangle[4];
		
		// facing south
		uvtriangles[0] = new UVTriangle(
				new Vector3d(8/16., 3/16., 9/16.),
				new Vector3d(24/16., 3/16., 9/16.),
				new Vector3d((4-12/10.)/16., 1, 9/16.),
				new Vector2d(0, 0),
				new Vector2d(1, 0),
				new Vector2d(0., 13/16.));
		uvtriangles[1] = new UVTriangle(
				new Vector3d((20-12/10.)/16., 1, 9/16.),
				new Vector3d((4-12/10.)/16., 1, 9/16.),
				new Vector3d(24/16., 3/16., 9/16.),
				new Vector2d(1, 13/16.),
				new Vector2d(0, 13/16.),
				new Vector2d(1, 0));
		
		// facing north
		uvtriangles[2] = new UVTriangle(
				new Vector3d(24/16., 3/16., 7/16.),
				new Vector3d(8/16., 3/16., 7/16.),
				new Vector3d((4-12/10.)/16., 1, 7/16.),
				new Vector2d(1, 0),
				new Vector2d(0, 0),
				new Vector2d(0, 13/16.));
		uvtriangles[3] = new UVTriangle(
				new Vector3d((4-12/10.)/16., 1, 7/16.),
				new Vector3d((20-12/10.)/16., 1, 7/16.),
				new Vector3d(24/16., 3/16., 7/16.),
				new Vector2d(0, 13/16.),
				new Vector2d(1, 13/16.),
				new Vector2d(1, 0));
		
		light = new Vector3d(.1, 1, -.1);
		light.normalize();
	}
	
	/**
	 * Ray-model intersection
	 * @param ray
	 */
	public void intersect(Ray ray) {
		ray.currentMaterial = 1 << 8;
		
		for (int i = 0; i < boxes.length; ++i) {
			if (boxes[i].intersect(ray)) {
				if (i == 4)
					Texture.dirt.getColor(ray);
				else
					Texture.flowerPot.getColor(ray);
				ray.color.w = 1;
				ray.t = ray.tNear;
			}
		}
		
		// TORCH
		/*
		float[] color = null;
		for (int i = 0; i < quads.length; ++i) {
			if (quads[i].intersect(ray)) {
				float[] c = Texture.torch.getColor(ray.u, ray.v);
				if (c[3] > Ray.EPSILON) {
					color = c;
					ray.t = ray.tNear;
					hit = true;
				}
			}
		}
		for (int i = 0; i < uvtriangles.length; ++i) {
			if (uvtriangles[i].intersect(ray)) {
				float[] c = Texture.torch.getColor(ray.u, ray.v);
				if (c[3] > Ray.EPSILON) {
					color = c;
					ray.t = ray.tNear;
					hit = true;
				}
			}
		}
		if (hit) {
			double px = ray.x.x - QuickMath.floor(ray.x.x + ray.d.x * Ray.OFFSET) + ray.d.x * ray.tNear;
			double py = ray.x.y - QuickMath.floor(ray.x.y + ray.d.y * Ray.OFFSET) + ray.d.y * ray.tNear;
			double pz = ray.x.z - QuickMath.floor(ray.x.z + ray.d.z * Ray.OFFSET) + ray.d.z * ray.tNear;
			if (px >= 0 && px <= 1 && py >= 0 && py <= 1 && pz >= 0 && pz <= 1) {
				ray.color.set(color);
				ray.color.w = 1;
			}
		}
		*/
	}

}
