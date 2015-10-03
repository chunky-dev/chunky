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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.resources.SignTexture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.SignMaterial;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.AnyTag;
import se.llbit.nbt.CompoundTag;

public class SignEntity extends Entity {

	// Facing south.
	protected static Quad[] sides = {
		// Front face.
		new Quad(new Vector3d(0, 9/16., 9/16.), new Vector3d(1, 9/16., 9/16.),
				new Vector3d(0, 17/16., 9/16.), new Vector4d(0, 1, 0, 1)),

		// Back face.
		new Quad(new Vector3d(1, 9/16., 7/16.), new Vector3d(0, 9/16., 7/16.),
				new Vector3d(1, 17/16., 7/16.), new Vector4d(28/64., 52/64., 18/32., 30/32.)),

		// Left face.
		new Quad(new Vector3d(0, 9/16., 7/16.), new Vector3d(0, 9/16., 9/16.),
				new Vector3d(0, 17/16., 7/16.), new Vector4d(0, 2/64., 18/32., 30/32.)),

		// Right face.
		new Quad(new Vector3d(1, 9/16., 9/16.), new Vector3d(1, 9/16., 7/16.),
				new Vector3d(1, 17/16., 9/16.), new Vector4d(26/64., 28/64., 18/32., 30/32.)),

		// Top face.
		new Quad(new Vector3d(1, 17/16., 7/16.), new Vector3d(0, 17/16., 7/16.),
				new Vector3d(1, 17/16., 9/16.), new Vector4d(2/64., 26/64., 1, 30/32.)),

		// Bottom face.
		new Quad(new Vector3d(0, 9/16., 7/16.), new Vector3d(1, 9/16., 7/16.),
				new Vector3d(0, 9/16., 9/16.), new Vector4d(26/64., 50/64., 1, 30/32.)),

		// Post front.
		new Quad(new Vector3d(7/16., 0, 9/16.), new Vector3d(9/16., 0, 9/16.),
				new Vector3d(7/16., 9/16., 9/16.), new Vector4d(2/64., 4/64., 2/32., 16/32.)),

		// Post back.
		new Quad(new Vector3d(9/16., 0, 7/16.), new Vector3d(7/16., 0, 7/16.),
				new Vector3d(9/16., 9/16., 7/16.), new Vector4d(4/64., 6/64., 2/32., 16/32.)),

		// Post left.
		new Quad(new Vector3d(7/16., 0, 7/16.), new Vector3d(7/16., 0, 9/16.),
				new Vector3d(7/16., 9/16., 7/16.), new Vector4d(0, 2/64., 2/32., 16/32.)),

		// Post right.
		new Quad(new Vector3d(9/16., 0, 9/16.), new Vector3d(9/16., 0, 7/16.),
				new Vector3d(9/16., 9/16., 9/16.), new Vector4d(6/64., 8/64., 2/32., 16/32.)),

		// Post bottom.
		new Quad(new Vector3d(7/16., 0, 7/16.), new Vector3d(9/16., 0, 7/16.),
				new Vector3d(7/16., 0, 9/16.), new Vector4d(4/64., 6/64., 16/32., 18/32.)),

	};

	private static final Quad[][] rot = new Quad[16][];

	static {
		// Rotate the sign post to face the correct direction.
		rot[0] = sides;
		for (int i = 1; i < 16; ++i) {
			rot[i] = Model.rotateY(sides, - i * Math.PI/8);
		}
	}

	private final String[] text;
	private final int angle;
	private final SignTexture texture;

	public SignEntity(Vector3d position, CompoundTag entityTag, int blockData) {
		this(position, getTextLines(entityTag), blockData & 0xF);
	}

	public SignEntity(Vector3d position, String[] text, int direction) {
		super(position);
		this.text = text;
		this.angle = direction;
		this.texture = new SignTexture(text);
	}

	/**
	 * Extracts the text lines from a sign entity tag.
	 * @return array of text lines.
	 */
	protected static String[] getTextLines(CompoundTag entityTag) {
		return new String[] {
				extractText(entityTag.get("Text1")),
				extractText(entityTag.get("Text2")),
				extractText(entityTag.get("Text3")),
				extractText(entityTag.get("Text4")),
		};
	}

	/** Extract text from entity tag. */
	private static String extractText(AnyTag tag) {
		String data = tag.stringValue("");
		if (data.startsWith("\"")) {
			return data.substring(1, data.length() - 1);
		} else {
			// TODO(jesper): handle colored text.
			JsonParser parser = new JsonParser(new ByteArrayInputStream(data.getBytes()));
			try {
				JsonValue value = parser.parse();
				if (value.isObject()) {
					JsonObject obj = value.object();
					StringBuilder text = new StringBuilder(obj.get("text").stringValue(""));
					JsonArray extraArray = obj.get("extra").array();
					for (JsonValue extra : extraArray.getElementList()) {
						if (extra.isObject()) {
							text.append(extra.object().get("text").stringValue(""));
						} else {
							text.append(extra.stringValue(""));
						}
					}
					return text.toString();
				} else {
					StringBuilder text = new StringBuilder();
					for (JsonValue item : value.array().getElementList()) {
						text.append(item.stringValue(""));
					}
					return text.toString();
				}
			} catch (IOException e) {
			} catch (SyntaxError e) {
			}
			return "";
		}
	}

	@Override
	public Collection<Primitive> primitives(Vector3d offset) {
		Collection<Primitive> primitives = new LinkedList<Primitive>();
		Transform transform = Transform.NONE.translate(position.x + offset.x,
				position.y + offset.y, position.z + offset.z);
		for (int i = 0; i < sides.length; ++i) {
			Quad quad = rot[angle][i];
			Material material;
			if (i != 0) {
				material = SignMaterial.INSTANCE;
			} else {
				material = new TextureMaterial(texture);
			}
			quad.addTriangles(primitives, material, transform);
		}
		return primitives;
	}

	@Override
	public JsonValue toJson() {
		JsonObject json = new JsonObject();
		json.add("kind", "sign");
		json.add("position", position.toJson());
		json.add("text", textToJson(text));
		json.add("direction", angle);
		return json;
	}

	/**
	 * Unmarshalls a sign entity from JSON data.
	 */
	public static Entity fromJson(JsonObject json) {
		Vector3d position = new Vector3d();
		position.fromJson(json.get("position").object());
		String[] text = textFromJson(json.get("text"));
		int direction = json.get("direction").intValue(0);
		return new SignEntity(position, text, direction);
	}

	/**
	 * Marshalls sign text to JSON representation.
	 */
	protected static JsonArray textToJson(String[] text) {
		JsonArray array = new JsonArray();
		array.add(text[0]);
		array.add(text[1]);
		array.add(text[2]);
		array.add(text[3]);
		return array;
	}

	/**
	 * Unmarshalls sign text from JSON representation.
	 */
	protected static String[] textFromJson(JsonValue json) {
		JsonArray array = json.array();
		String[] text = new String[4];
		text[0] = array.get(0).stringValue("");
		text[1] = array.get(1).stringValue("");
		text[2] = array.get(2).stringValue("");
		text[3] = array.get(3).stringValue("");
		return text;
	}

}
