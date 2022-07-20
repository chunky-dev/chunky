/*
 * Copyright (c) 2017-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2021 Chunky contributors
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

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.*;
import se.llbit.math.primitive.Primitive;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PaintingEntity extends Entity {

  public static class Painting {

    protected final Quad[] quads;
    protected final Material material;
    protected double ox;
    protected double oy;

    public Painting(Texture painting, int w, int h) {
      this.ox = -w / 2.0;
      this.oy = -h / 2.0;

      double offset = -1 / 16.;
      double off = 0;

      quads = new Quad[]{
        // north (front)
        new Quad(new Vector3(w, 0, offset), new Vector3(0, 0, offset),
          new Vector3(w, h, offset), new Vector4(0, 1, 0, 1)),

        // south (back)
        new Quad(new Vector3(0, 0, off), new Vector3(w, 0, off), new Vector3(0, h, off),
          new Vector4(0, w / 4., 1, 1 - h / 4.)),

        // west (left)
        new Quad(new Vector3(0, 0, offset), new Vector3(0, 0, off), new Vector3(0, h, offset),
          new Vector4(0, 1 / 64., 1 - h / 4., 1)),

        // east (right)
        new Quad(new Vector3(w, 0, off), new Vector3(w, 0, offset), new Vector3(w, h, off),
          new Vector4(0, 1 / 64., 1 - h / 4., 1)),

        // top
        new Quad(new Vector3(w, h, offset), new Vector3(0, h, offset), new Vector3(w, h, off),
          new Vector4(0, w / 4., 1 - 1 / 64., 1)),

        // bottom
        new Quad(new Vector3(0, 0, offset), new Vector3(w, 0, offset), new Vector3(0, 0, off),
          new Vector4(0, w / 4., 1, 1 - 1 / 64.)),};
      material = new TextureMaterial(painting);
    }
  }

  static final Map<String, Painting> paintings = new HashMap<>();

  static {
    resetPaintings();
  }

  private static final Material BACK_MATERIAL = new TextureMaterial(Texture.paintingBack);

  private final double angle;
  private final String art;

  public PaintingEntity(Vector3 position, String art, double angle) {
    super(position);
    this.art = art;
    this.angle = angle;
  }

  public PaintingEntity(Vector3 position, String art, int facing) {
    super(position);
    this.art = art;
    switch (facing) {
      case 1:
        this.angle = 90;
        break;
      case 2:
        this.angle = 180;
        break;
      case 3:
        this.angle = 270;
        break;
      case 0:
      default:
        this.angle = 0;
        break;
    }
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> primitives = new LinkedList<>();
    Painting painting = paintings.get(art);
    if (painting == null) {
      return primitives;
    }
    double rot = QuickMath.degToRad(180 - angle);
    Transform transform = Transform.NONE.translate(painting.ox, painting.oy, 0.5 / 16).rotateY(rot)
      .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z);
    Quad[] quads = painting.quads;
    quads[0].addTriangles(primitives, painting.material, transform); // front face
    for (int i = 1; i < quads.length; i++) { // other faces
      Quad quad = quads[i];
      quad.addTriangles(primitives, BACK_MATERIAL, transform);
    }
    return primitives;
  }

  @Override
  public JsonValue toJson() {
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
  public static Collection<Entity> fromJson(JsonObject json) {
    Vector3 position = new Vector3();
    position.fromJson(json.get("position").object());
    String art = json.get("art").stringValue("");
    double angle = json.get("angle").doubleValue(0.0);
    return Collections.singletonList(new PaintingEntity(position, art, angle));
  }

  public static void resetPaintings() {
    paintings.clear();

    // hard-coded pre-24w18a paintings with legacy aliases
    paintings.put("Kebab", new Painting(Texture.paintingKebab, 1, 1));
    paintings.put("minecraft:kebab", new Painting(Texture.paintingKebab, 1, 1));
    paintings.put("Aztec", new Painting(Texture.paintingAztec, 1, 1));
    paintings.put("minecraft:aztec", new Painting(Texture.paintingAztec, 1, 1));
    paintings.put("Alban", new Painting(Texture.paintingAlban, 1, 1));
    paintings.put("minecraft:alban", new Painting(Texture.paintingAlban, 1, 1));
    paintings.put("Aztec2", new Painting(Texture.paintingAztec2, 1, 1));
    paintings.put("minecraft:aztec2", new Painting(Texture.paintingAztec2, 1, 1));
    paintings.put("Bomb", new Painting(Texture.paintingBomb, 1, 1));
    paintings.put("minecraft:bomb", new Painting(Texture.paintingBomb, 1, 1));
    paintings.put("Plant", new Painting(Texture.paintingPlant, 1, 1));
    paintings.put("minecraft:plant", new Painting(Texture.paintingPlant, 1, 1));
    paintings.put("Wasteland", new Painting(Texture.paintingWasteland, 1, 1));
    paintings.put("minecraft:wasteland", new Painting(Texture.paintingWasteland, 1, 1));
    paintings.put("Wanderer", new Painting(Texture.paintingWanderer, 1, 2));
    paintings.put("minecraft:wanderer", new Painting(Texture.paintingWanderer, 1, 2));
    paintings.put("Graham", new Painting(Texture.paintingGraham, 1, 2));
    paintings.put("minecraft:graham", new Painting(Texture.paintingGraham, 1, 2));
    paintings.put("Pool", new Painting(Texture.paintingPool, 2, 1));
    paintings.put("minecraft:pool", new Painting(Texture.paintingPool, 2, 1));
    paintings.put("Courbet", new Painting(Texture.paintingCourbet, 2, 1));
    paintings.put("minecraft:courbet", new Painting(Texture.paintingCourbet, 2, 1));
    paintings.put("Sunset", new Painting(Texture.paintingSunset, 2, 1));
    paintings.put("minecraft:sunset", new Painting(Texture.paintingSunset, 2, 1));
    paintings.put("Sea", new Painting(Texture.paintingSea, 2, 1));
    paintings.put("minecraft:sea", new Painting(Texture.paintingSea, 2, 1));
    paintings.put("Creebet", new Painting(Texture.paintingCreebet, 2, 1));
    paintings.put("minecraft:creebet", new Painting(Texture.paintingCreebet, 2, 1));
    paintings.put("Match", new Painting(Texture.paintingMatch, 2, 2));
    paintings.put("minecraft:match", new Painting(Texture.paintingMatch, 2, 2));
    paintings.put("Bust", new Painting(Texture.paintingBust, 2, 2));
    paintings.put("minecraft:bust", new Painting(Texture.paintingBust, 2, 2));
    paintings.put("Stage", new Painting(Texture.paintingStage, 2, 2));
    paintings.put("minecraft:stage", new Painting(Texture.paintingStage, 2, 2));
    paintings.put("Void", new Painting(Texture.paintingVoid, 2, 2));
    paintings.put("minecraft:void", new Painting(Texture.paintingVoid, 2, 2));
    paintings.put("SkullAndRoses", new Painting(Texture.paintingSkullAndRoses, 2, 2));
    paintings.put("minecraft:skull_and_roses", new Painting(Texture.paintingSkullAndRoses, 2, 2));
    paintings.put("Wither", new Painting(Texture.paintingWither, 2, 2));
    paintings.put("minecraft:wither", new Painting(Texture.paintingWither, 2, 2));
    paintings.put("Fighters", new Painting(Texture.paintingFighters, 4, 2));
    paintings.put("minecraft:fighters", new Painting(Texture.paintingFighters, 4, 2));
    paintings.put("Skeleton", new Painting(Texture.paintingSkeleton, 4, 3));
    paintings.put("minecraft:skeleton", new Painting(Texture.paintingSkeleton, 4, 3));
    paintings.put("DonkeyKong", new Painting(Texture.paintingDonkeyKong, 4, 3));
    paintings.put("minecraft:donkey_kong", new Painting(Texture.paintingDonkeyKong, 4, 3));
    paintings.put("Pointer", new Painting(Texture.paintingPointer, 4, 4));
    paintings.put("minecraft:pointer", new Painting(Texture.paintingPointer, 4, 4));
    paintings.put("Pigscene", new Painting(Texture.paintingPigscene, 4, 4));
    paintings.put("minecraft:pigscene", new Painting(Texture.paintingPigscene, 4, 4));
    paintings.put("BurningSkull", new Painting(Texture.paintingBurningSkull, 4, 4));
    paintings.put("minecraft:burning_skull", new Painting(Texture.paintingBurningSkull, 4, 4));
  }

  public static void registerPainting(String id, Painting painting) {
    paintings.put(id, painting);
  }

  public static boolean containsPainting(String id) {
    return paintings.containsKey(id);
  }
}
