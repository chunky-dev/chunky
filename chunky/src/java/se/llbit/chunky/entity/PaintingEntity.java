/*
 * Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import se.llbit.chunky.world.material.PaintingMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;

public class PaintingEntity extends Entity {

  static class Painting {

    protected final Quad[] quads;
    protected double ox;
    protected double oy;

    public Painting(int px0, int px1, int py0, int py1) {
      double x0 = px0 / 256.;
      double x1 = px1 / 256.;
      double y0 = (256 - py0) / 256.;
      double y1 = (256 - py1) / 256.;
      int pw = px1 - px0;
      int ph = py1 - py0;
      int w = pw / 16;
      int h = ph / 16;
      this.ox = -w / 2.0;
      this.oy = -h / 2.0;

      double offset = -.5 / 16.;
      double off = 0;
      quads = new Quad[] {
          // north (front)
          new Quad(new Vector3(w, 0, offset), new Vector3(0, 0, offset),
              new Vector3(w, h, offset), new Vector4(x0, x1, y1, y0)),

          // south (back)
          new Quad(new Vector3(0, 0, off), new Vector3(w, 0, off), new Vector3(0, h, off),
              new Vector4(192 / 256., (192 + pw) / 256., 1, (256 - ph) / 256.)),

          // west (left)
          new Quad(new Vector3(0, 0, offset), new Vector3(0, 0, off), new Vector3(0, h, offset),
              new Vector4(192 / 256., 193 / 256., (256 - ph) / 256., 1)),

          // east (right)
          new Quad(new Vector3(w, 0, off), new Vector3(w, 0, offset), new Vector3(w, h, off),
              new Vector4(192 / 256., 193 / 256., (256 - ph) / 256., 1)),

          // top
          new Quad(new Vector3(w, h, offset), new Vector3(0, h, offset), new Vector3(w, h, off),
              new Vector4(192 / 256., (192 + pw) / 256., 255 / 256., 1)),

          // bottom
          new Quad(new Vector3(0, 0, offset), new Vector3(w, 0, 0.0), new Vector3(0, 0, off),
              new Vector4(192 / 256., (192 + pw) / 256., 1, 255 / 256.)),};
    }
  }


  static final Map<String, Painting> paintings = new HashMap<>();

  static {
    paintings.put("Kebab", new Painting(0, 16, 0, 16));
    paintings.put("Aztec", new Painting(16, 32, 0, 16));
    paintings.put("Alban", new Painting(32, 48, 0, 16));
    paintings.put("Aztec2", new Painting(48, 64, 0, 16));
    paintings.put("Bomb", new Painting(64, 80, 0, 16));
    paintings.put("Plant", new Painting(80, 96, 0, 16));
    paintings.put("Wasteland", new Painting(96, 112, 0, 16));
    paintings.put("Wanderer", new Painting(0, 16, 64, 96));
    paintings.put("Graham", new Painting(16, 32, 64, 96));
    paintings.put("Pool", new Painting(0, 32, 32, 48));
    paintings.put("Courbet", new Painting(32, 64, 32, 48));
    paintings.put("Sunset", new Painting(64, 96, 32, 48));
    paintings.put("Sea", new Painting(96, 128, 32, 48));
    paintings.put("Creebet", new Painting(128, 160, 32, 48));
    paintings.put("Match", new Painting(0, 32, 128, 160));
    paintings.put("Bust", new Painting(32, 64, 128, 160));
    paintings.put("Stage", new Painting(64, 96, 128, 160));
    paintings.put("Void", new Painting(96, 128, 128, 160));
    paintings.put("SkullAndRoses", new Painting(128, 160, 128, 160));
    paintings.put("Wither", new Painting(160, 192, 128, 160));
    paintings.put("Fighters", new Painting(0, 64, 96, 128));
    paintings.put("Skeleton", new Painting(192, 256, 64, 112));
    paintings.put("DonkeyKong", new Painting(192, 256, 112, 160));
    paintings.put("Pointer", new Painting(0, 64, 192, 256));
    paintings.put("Pigscene", new Painting(64, 128, 192, 256));
    paintings.put("BurningSkull", new Painting(128, 192, 192, 256));
  }

  private final double angle;
  private final String art;

  public PaintingEntity(Vector3 position, String art, double angle) {
    super(position);
    this.art = art;
    this.angle = angle;
  }

  @Override public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> primitives = new LinkedList<>();
    Painting painting = paintings.get(art);
    if (painting == null) {
      return primitives;
    }
    double rot = QuickMath.degToRad(180 - angle);
    Transform transform = Transform.NONE.translate(painting.ox, painting.oy, 0).rotateY(rot)
              .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z);
    for (Quad quad : painting.quads) {
      quad.addTriangles(primitives, PaintingMaterial.INSTANCE, transform);
    }

    return primitives;
  }

  @Override public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "painting");
    json.add("position", position.toJson());
    json.add("art", art);
    json.add("angle", angle);
    return json;
  }

  /**
   * Deserialize entity from JSON.
   *
   * @return deserialized entity, or {@code null} if it was not a valid entity
   */
  public static Entity fromJson(JsonObject json) {
    Vector3 position = new Vector3();
    position.fromJson(json.get("position").object());
    String art = json.get("art").stringValue("");
    double angle = json.get("angle").doubleValue(0.0);
    return new PaintingEntity(position, art, angle);
  }
}
