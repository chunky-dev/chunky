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
package se.llbit.chunky.world;

import java.util.Collection;
import java.util.LinkedList;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Transform;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;
import se.llbit.math.primitive.Box;
import se.llbit.math.primitive.Primitive;

public class PlayerEntity extends Entity {

	Collection<Primitive> primitives = new LinkedList<Primitive>();

	public PlayerEntity(Vector3d position) {
		super(position);
		Box head = new Box(position.x-4/16., position.x+4/16., position.y+24/16., position.y+32/16., position.z-4/16., position.z+4/16., new Vector3d(0, 0, 1));
		head.addFrontFaces(primitives, Texture.steveHeadFront, new Vector4d(0, 1, 0, 1));
		head.addBackFaces(primitives, Texture.steveHeadBack, new Vector4d(0, 1, 0, 1));
		head.addLeftFaces(primitives, Texture.steveHeadLeft, new Vector4d(0, 1, 0, 1));
		head.addRightFaces(primitives, Texture.steveHeadRight, new Vector4d(0, 1, 0, 1));
		head.addTopFaces(primitives, Texture.steveHeadTop, new Vector4d(0, 1, 0, 1));
		head.addBottomFaces(primitives, Texture.steveHeadBottom, new Vector4d(0, 1, 0, 1));
		Box chest = new Box(-4/16., 4/16., -6/16., 6/16., -2/16., 2/16., new Vector3d(0, 1, 0));
		chest.transform(Transform.NONE.translate(position).translate(0, 18/16., 0));
		chest.addFrontFaces(primitives, Texture.steveChestFront, new Vector4d(0, 1, 0, 1));
		chest.addBackFaces(primitives, Texture.steveChestBack, new Vector4d(0, 1, 0, 1));
		chest.addLeftFaces(primitives, Texture.steveChestLeft, new Vector4d(0, 1, 0, 1));
		chest.addRightFaces(primitives, Texture.steveChestRight, new Vector4d(0, 1, 0, 1));
		chest.addTopFaces(primitives, Texture.steveChestTop, new Vector4d(0, 1, 0, 1));
		chest.addBottomFaces(primitives, Texture.steveChestBottom, new Vector4d(0, 1, 0, 1));
		Box leftLeg = new Box(-2/16., 2/16., -6/16., 6/16., -2/16., 2/16.);
		leftLeg.transform(Transform.NONE.translate(0, -6/16., 0).rotateX(0.4).translate(position).translate(-2/16., 12/16., 0));
		leftLeg.addFrontFaces(primitives, Texture.steveLeftLegFront, new Vector4d(0, 1, 0, 1));
		leftLeg.addBackFaces(primitives, Texture.steveLeftLegBack, new Vector4d(0, 1, 0, 1));
		leftLeg.addLeftFaces(primitives, Texture.steveLeftLegLeft, new Vector4d(0, 1, 0, 1));
		leftLeg.addRightFaces(primitives, Texture.steveLeftLegRight, new Vector4d(0, 1, 0, 1));
		leftLeg.addTopFaces(primitives, Texture.steveLeftLegTop, new Vector4d(0, 1, 0, 1));
		leftLeg.addBottomFaces(primitives, Texture.steveLeftLegBottom, new Vector4d(0, 1, 0, 1));
		Box rightLeg = new Box(-2/16., 2/16., -6/16., 6/16., -2/16., 2/16.);
		rightLeg.transform(Transform.NONE.translate(0, -6/16., 0).rotateX(-0.4).translate(position).translate(2/16., 12/16., 0));
		rightLeg.addFrontFaces(primitives, Texture.steveRightLegFront, new Vector4d(0, 1, 0, 1));
		rightLeg.addBackFaces(primitives, Texture.steveRightLegBack, new Vector4d(0, 1, 0, 1));
		rightLeg.addLeftFaces(primitives, Texture.steveRightLegLeft, new Vector4d(0, 1, 0, 1));
		rightLeg.addRightFaces(primitives, Texture.steveRightLegRight, new Vector4d(0, 1, 0, 1));
		rightLeg.addTopFaces(primitives, Texture.steveRightLegTop, new Vector4d(0, 1, 0, 1));
		rightLeg.addBottomFaces(primitives, Texture.steveRightLegBottom, new Vector4d(0, 1, 0, 1));
		Box leftArm = new Box(-2/16., 2/16., -6/16., 6/16., -2/16., 2/16.);
		leftArm.transform(Transform.NONE.translate(0, -5/16., 0).rotateX(0.4).translate(position).translate(-6/16., 23/16., 0));
		leftArm.addFrontFaces(primitives, Texture.steveLeftArmFront, new Vector4d(0, 1, 0, 1));
		leftArm.addBackFaces(primitives, Texture.steveLeftArmBack, new Vector4d(0, 1, 0, 1));
		leftArm.addLeftFaces(primitives, Texture.steveLeftArmLeft, new Vector4d(0, 1, 0, 1));
		leftArm.addRightFaces(primitives, Texture.steveLeftArmRight, new Vector4d(0, 1, 0, 1));
		leftArm.addTopFaces(primitives, Texture.steveLeftArmTop, new Vector4d(0, 1, 0, 1));
		leftArm.addBottomFaces(primitives, Texture.steveLeftArmBottom, new Vector4d(0, 1, 0, 1));
		Box rightArm = new Box(-2/16., 2/16., -6/16., 6/16., -2/16., 2/16.);
		rightArm.transform(Transform.NONE.translate(0, -5/16., 0).rotateX(-0.4).translate(position).translate(6/16., 23/16., 0));
		rightArm.addFrontFaces(primitives, Texture.steveRightArmFront, new Vector4d(0, 1, 0, 1));
		rightArm.addBackFaces(primitives, Texture.steveRightArmBack, new Vector4d(0, 1, 0, 1));
		rightArm.addLeftFaces(primitives, Texture.steveRightArmLeft, new Vector4d(0, 1, 0, 1));
		rightArm.addRightFaces(primitives, Texture.steveRightArmRight, new Vector4d(0, 1, 0, 1));
		rightArm.addTopFaces(primitives, Texture.steveRightArmTop, new Vector4d(0, 1, 0, 1));
		rightArm.addBottomFaces(primitives, Texture.steveRightArmBottom, new Vector4d(0, 1, 0, 1));
	}

	@Override
	public boolean intersect(Ray ray) {
		boolean any = false;
		for (Primitive p: primitives) {
			if (p.intersect(ray)) {
				ray.currentMaterial = Block.STONE_ID;
				any = true;
			}
		}
		return any;
	}
}
