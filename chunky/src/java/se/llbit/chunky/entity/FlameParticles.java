package se.llbit.chunky.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.StreamSupport;

import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.minecraft.Candle;
import se.llbit.chunky.model.Model;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.math.*;
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
  private final double scale;
  private final Block block;

  public FlameParticles(Vector3 position, double scale, Block block, Vector3[] flames) {
    super(position);
    this.flames = flames;
    this.scale = scale;
    this.block = block;
  }

  public FlameParticles(Vector3 position, Block block, Vector3[] flames) {
    this(position, 1, block, flames);
  }

  public FlameParticles(Block block, Vector3[] flames) {
    this(new Vector3(0, 0, 0), 1, block, flames);
  }

  public FlameParticles(Vector3 position, FlameParticles other) {
    this(position, other.scale, other.block, other.flames);
  }

  public FlameParticles(JsonObject json) {
    super(JsonUtil.vec3FromJsonObject(json.get("position")));
    this.block = null;
    this.scale = json.get("scale").asDouble(1.0);
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
            Transform.NONE
                .translate(-0.5, -1 / 16.0, -0.5)
                .scale(scale)
                .translate(0.5, 1 / 16.0, 0.5)
                .translate(flameOffset)
                .translate(offset)
                .translate(position));
      }
    }
    return faces;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "flameParticles");
    json.add("position", position.toJson());
    json.add("scale", scale);
    if (flames.length > 1 || !position.equals(flames[0])) {
      JsonArray particles = new JsonArray();
      for (Vector3 particle : flames) {
        particles.add(particle.toJson());
      }
      json.add("particles", particles);
    }
    return json;
  }

  public static Collection<Entity> fromJson(JsonObject json) {
    return Collections.singletonList(new FlameParticles(json));
  }

  @Override
  public Grid.EmitterPosition[] getEmitterPosition() {
    if (block == null) {
      Log.warn("Attempted to build emitter grid from unassociated flame particle.");
      return new Grid.EmitterPosition[0];
    }

    Grid.EmitterPosition[] pos = new Grid.EmitterPosition[flames.length];
    for(int i = 0; i < flames.length; i++) {
      pos[i] = new Grid.EmitterPosition((int) position.x, (int) position.y, (int) position.z, this.block);
    }
    return pos;
  }

  public int faceCount() {
    return quads.length * flames.length;
  }

  public void sample(int face, Vector3 loc, Random rand) {
    quads[face % quads.length].sample(loc, rand);
    loc.add(flames[face / quads.length]);
  }

  public double surfaceArea(int face) {
    return quads[face % quads.length].surfaceArea();
  }
}
