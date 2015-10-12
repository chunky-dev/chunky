/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world.entity;

import java.util.Collection;
import java.util.LinkedList;

import se.llbit.chunky.resources.Texture;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.QuickMath;
import se.llbit.math.Transform;
import se.llbit.math.Vector3d;
import se.llbit.math.primitive.Box;
import se.llbit.math.primitive.Primitive;

public class PlayerEntity extends Entity {

	private final double yaw;
	private final double pitch;
	private final double leftLegPose;
	private final double rightLegPose;
	private final double leftArmPose;
	private final double rightArmPose;

	public PlayerEntity(Vector3d position, double yawDegrees, double pitchDegrees) {
		this(position, QuickMath.degToRad(180 - yawDegrees), -QuickMath.degToRad(pitchDegrees),
				0.4, -0.4, 0.4, -0.4);
	}

	public PlayerEntity(Vector3d position, double yaw, double pitch,
			double leftLegPose, double rightLegPose, double leftArmPose, double rightArmPose) {
		super(position);
		this.yaw = yaw;
		this.pitch = pitch;
		this.leftLegPose = leftLegPose;
		this.rightLegPose = rightLegPose;
		this.leftArmPose = leftArmPose;
		this.rightArmPose = rightArmPose;
	}

	private void poseLimb(Box part, Transform transform, Transform offset) {
		part.transform(transform);
		part.transform(Transform.NONE.rotateY(yaw));
		part.transform(offset);
	}

	private void poseHead(Box part, Transform transform, Transform offset) {
		part.transform(Transform.NONE.rotateX(pitch));
		part.transform(transform);
		part.transform(Transform.NONE.rotateY(yaw));
		part.transform(offset);
	}

	@Override
	public Collection<Primitive> primitives(Vector3d offset) {
		Collection<Primitive> faces = new LinkedList<Primitive>();
		Transform offsetTransform = Transform.NONE.translate(
				position.x + offset.x,
				position.y + offset.y,
				position.z + offset.z);
		Box head = new Box(-4/16., 4/16., -4/16., 4/16., -4/16., 4/16.);
		poseHead(head, Transform.NONE.translate(0, 28/16., 0), offsetTransform);
		head.addFrontFaces(faces, Texture.steve, Texture.steve.headFront);
		head.addBackFaces(faces, Texture.steve, Texture.steve.headBack);
		head.addLeftFaces(faces, Texture.steve, Texture.steve.headLeft);
		head.addRightFaces(faces, Texture.steve, Texture.steve.headRight);
		head.addTopFaces(faces, Texture.steve, Texture.steve.headTop);
		head.addBottomFaces(faces, Texture.steve, Texture.steve.headBottom);
		Box hat = new Box(-4.2/16., 4.2/16., -4.2/16., 4.2/16., -4.2/16., 4.2/16.);
		poseHead(hat, Transform.NONE.translate(0, 28.2/16., 0), offsetTransform);
		hat.addFrontFaces(faces, Texture.steve, Texture.steve.hatFront);
		hat.addBackFaces(faces, Texture.steve, Texture.steve.hatBack);
		hat.addLeftFaces(faces, Texture.steve, Texture.steve.hatLeft);
		hat.addRightFaces(faces, Texture.steve, Texture.steve.hatRight);
		hat.addTopFaces(faces, Texture.steve, Texture.steve.hatTop);
		hat.addBottomFaces(faces, Texture.steve, Texture.steve.hatBottom);
		Box chest = new Box(-4/16., 4/16., -6/16., 6/16., -2/16., 2/16.);
		poseLimb(chest, Transform.NONE.translate(0, 18/16., 0), offsetTransform);
		chest.addFrontFaces(faces, Texture.steve, Texture.steve.chestFront);
		chest.addBackFaces(faces, Texture.steve, Texture.steve.chestBack);
		chest.addLeftFaces(faces, Texture.steve, Texture.steve.chestLeft);
		chest.addRightFaces(faces, Texture.steve, Texture.steve.chestRight);
		chest.addTopFaces(faces, Texture.steve, Texture.steve.chestTop);
		chest.addBottomFaces(faces, Texture.steve, Texture.steve.chestBottom);
		Box leftLeg = new Box(-2/16., 2/16., -6/16., 6/16., -2/16., 2/16.);
		poseLimb(leftLeg, Transform.NONE.translate(0, -6/16., 0).rotateX(leftLegPose)
				.translate(-2/16., 12/16., 0), offsetTransform);
		leftLeg.addFrontFaces(faces, Texture.steve, Texture.steve.leftLegFront);
		leftLeg.addBackFaces(faces, Texture.steve, Texture.steve.leftLegBack);
		leftLeg.addLeftFaces(faces, Texture.steve, Texture.steve.leftLegLeft);
		leftLeg.addRightFaces(faces, Texture.steve, Texture.steve.leftLegRight);
		leftLeg.addTopFaces(faces, Texture.steve, Texture.steve.leftLegTop);
		leftLeg.addBottomFaces(faces, Texture.steve, Texture.steve.leftLegBottom);
		Box rightLeg = new Box(-2/16., 2/16., -6/16., 6/16., -2/16., 2/16.);
		poseLimb(rightLeg, Transform.NONE.translate(0, -6/16., 0).rotateX(rightLegPose)
				.translate(2/16., 12/16., 0), offsetTransform);
		rightLeg.addFrontFaces(faces, Texture.steve, Texture.steve.rightLegFront);
		rightLeg.addBackFaces(faces, Texture.steve, Texture.steve.rightLegBack);
		rightLeg.addLeftFaces(faces, Texture.steve, Texture.steve.rightLegLeft);
		rightLeg.addRightFaces(faces, Texture.steve, Texture.steve.rightLegRight);
		rightLeg.addTopFaces(faces, Texture.steve, Texture.steve.rightLegTop);
		rightLeg.addBottomFaces(faces, Texture.steve, Texture.steve.rightLegBottom);
		Box leftArm = new Box(-2/16., 2/16., -6/16., 6/16., -2/16., 2/16.);
		poseLimb(leftArm, Transform.NONE.translate(0, -5/16., 0).rotateX(leftArmPose)
				.translate(-6/16., 23/16., 0), offsetTransform);
		leftArm.addFrontFaces(faces, Texture.steve, Texture.steve.leftArmFront);
		leftArm.addBackFaces(faces, Texture.steve, Texture.steve.leftArmBack);
		leftArm.addLeftFaces(faces, Texture.steve, Texture.steve.leftArmLeft);
		leftArm.addRightFaces(faces, Texture.steve, Texture.steve.leftArmRight);
		leftArm.addTopFaces(faces, Texture.steve, Texture.steve.leftArmTop);
		leftArm.addBottomFaces(faces, Texture.steve, Texture.steve.leftArmBottom);
		Box rightArm = new Box(-2/16., 2/16., -6/16., 6/16., -2/16., 2/16.);
		poseLimb(rightArm, Transform.NONE.translate(0, -5/16., 0).rotateX(rightArmPose)
				.translate(6/16., 23/16., 0), offsetTransform);
		rightArm.addFrontFaces(faces, Texture.steve, Texture.steve.rightArmFront);
		rightArm.addBackFaces(faces, Texture.steve, Texture.steve.rightArmBack);
		rightArm.addLeftFaces(faces, Texture.steve, Texture.steve.rightArmLeft);
		rightArm.addRightFaces(faces, Texture.steve, Texture.steve.rightArmRight);
		rightArm.addTopFaces(faces, Texture.steve, Texture.steve.rightArmTop);
		rightArm.addBottomFaces(faces, Texture.steve, Texture.steve.rightArmBottom);
		return faces;
	}

	@Override
	public JsonValue toJson() {
		JsonObject json = new JsonObject();
		json.add("kind", "player");
		json.add("position", position.toJson());
		json.add("pitch", pitch);
		json.add("yaw", yaw);
		json.add("leftLegPose", leftLegPose);
		json.add("rightLegPose", rightLegPose);
		json.add("leftArmPose", leftArmPose);
		json.add("rightArmPose", rightArmPose);
		return json;
	}

	public static PlayerEntity fromJson(JsonObject json) {
		Vector3d position = new Vector3d();
		position.fromJson(json.get("position").object());
		double pitch = json.get("pitch").doubleValue(0.0);
		double yaw = json.get("yaw").doubleValue(0.0);
		double leftLegPose = json.get("leftLegPose").doubleValue(0.0);
		double rightLegPose = json.get("rightLegPose").doubleValue(0.0);
		double leftArmPose = json.get("leftArmPose").doubleValue(0.0);
		double rightArmPose = json.get("rightArmPose").doubleValue(0.0);
		return new PlayerEntity(position, yaw, pitch, leftLegPose,
				rightLegPose, leftArmPose, rightArmPose);
	}
}
