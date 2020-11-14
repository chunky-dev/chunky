package se.llbit.chunky.entity;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.StreamSupport;
import se.llbit.chunky.model.Model;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.util.JsonUtil;

public class FlameParticles extends Entity {

  private static final Quad[] quads = Model.join(
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(7 / 16.0, 2 / 16.0, 8 / 16.0),
              new Vector3(9 / 16.0, 2 / 16.0, 8 / 16.0),
              new Vector3(7 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(1, 0, 1, 0)
          ),
          new Quad(
              new Vector3(9 / 16.0, 2 / 16.0, 8 / 16.0),
              new Vector3(7 / 16.0, 2 / 16.0, 8 / 16.0),
              new Vector3(9 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(1, 0, 1, 0)
          )
      }, Math.toRadians(45)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(7 / 16.0, 2 / 16.0, 8 / 16.0),
              new Vector3(9 / 16.0, 2 / 16.0, 8 / 16.0),
              new Vector3(7 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(1, 0, 1, 0)
          ),
          new Quad(
              new Vector3(9 / 16.0, 2 / 16.0, 8 / 16.0),
              new Vector3(7 / 16.0, 2 / 16.0, 8 / 16.0),
              new Vector3(9 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(1, 0, 1, 0)
          )
      }, Math.toRadians(-45))
  );

  private final Vector3[] flames;

  public FlameParticles(Vector3 position, Vector3[] flames) {
    super(position);
    this.flames = flames;
  }

  public FlameParticles(Vector3 position) {
    super(position);
    this.flames = new Vector3[]{position};
  }

  public FlameParticles(JsonObject json) {
    super(JsonUtil.vec3FromJsonObject(json.get("position")));
    if (json.get("particles").isArray()) {
      this.flames = StreamSupport.stream(json.get("particles").array().spliterator(), false)
          .map(JsonUtil::vec3FromJsonObject)
          .toArray(Vector3[]::new);
    } else {
      this.flames = new Vector3[]{position};
    }
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> faces = new LinkedList<>();
    for (Vector3 flameOffset : flames) {
      for (Quad quad : quads) {
        quad.addTriangles(faces, Candle.flameMaterial,
            Transform.NONE.translate(offset).translate(position).translate(flameOffset));
      }
    }
    return faces;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "flameParticles");
    json.add("position", position.toJson());
    if (flames.length > 1 || !position.equals(flames[0])) {
      JsonArray particles = new JsonArray();
      for (Vector3 particle : flames) {
        particles.add(particle.toJson());
      }
      json.add("particles", particles);
    }
    return json;
  }

  public static Entity fromJson(JsonObject json) {
    return new FlameParticles(json);
  }
}
