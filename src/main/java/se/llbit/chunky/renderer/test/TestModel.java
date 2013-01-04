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
		quads = new Quad[4];
		
		// west
		quads[0] = new Quad(new Vector3d(15/16., 3/16., 7/16.),
				new Vector3d(15/16., 3/16., 9/16.),
				new Vector3d(11/16., 13/16., 7/16.),
				new Vector4d(7/16., 9/16., 0, 10/16.));
		
		// east
		quads[1] = new Quad(new Vector3d(13/16., 13/16., 7/16.),
				new Vector3d(13/16., 13/16., 9/16.),
				new Vector3d(17/16., 3/16., 7/16.),
				new Vector4d(9/16., 7/16., 10/16., 0));
		
		// top
		quads[2] = new Quad(
				new Vector3d(13/16., 13/16., 7/16.),
				new Vector3d(11/16., 13/16., 7/16.),
				new Vector3d(13/16., 13/16., 9/16.),
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
				new Vector3d(17/16., 3/16., 9/16.),
				new Vector3d(15/16., 3/16., 9/16.),
				new Vector3d(11/16., 13/16., 9/16.),
				new Vector2d(9/16., 0),
				new Vector2d(7/16., 0),
				new Vector2d(7/16., 10/16.));
		uvtriangles[1] = new UVTriangle(
				new Vector3d(11/16., 13/16., 9/16.),
				new Vector3d(13/16., 13/16., 9/16.),
				new Vector3d(17/16., 3/16., 9/16.),
				new Vector2d(7/16., 10/16.),
				new Vector2d(9/16., 10/16.),
				new Vector2d(9/16., 0));
		// facing north
		uvtriangles[2] = new UVTriangle(
				new Vector3d(17/16., 3/16., 7/16.),
				new Vector3d(15/16., 3/16., 7/16.),
				new Vector3d(11/16., 13/16., 7/16.),
				new Vector2d(7/16., 0),
				new Vector2d(9/16., 0),
				new Vector2d(9/16., 10/16.));
		uvtriangles[3] = new UVTriangle(
				new Vector3d(11/16., 13/16., 7/16.),
				new Vector3d(13/16., 13/16., 7/16.),
				new Vector3d(17/16., 3/16., 7/16.),
				new Vector2d(9/16., 10/16.),
				new Vector2d(7/16., 10/16.),
				new Vector2d(7/16., 0));
		
		light = new Vector3d(.1, 1, -.1);
		light.normalize();
	}
	
	/**
	 * Ray-model intersection
	 * @param ray
	 */
	public void intersect(Ray ray) {
		ray.currentMaterial = 1 << 8;
		
		for (int i = 0; i < quads.length; ++i) {
			if (quads[i].intersect(ray)) {
				Texture.torch.getColor(ray);
				ray.t = ray.tNear;
			}
		}
		for (int i = 0; i < uvtriangles.length; ++i) {
			if (uvtriangles[i].intersect(ray)) {
				Texture.torch.getColor(ray);
				ray.t = ray.tNear;
			}
		}
	}

}
