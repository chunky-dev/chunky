package se.llbit.chunky.entity;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.resources.SignTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;

import java.util.Collection;
import java.util.LinkedHashSet;

public class HangingSignEntity extends Entity {
  private static final Quad[] quadsAttached = new Quad[]{
    new Quad(
      new Vector3(-7 / 16.0, 10 / 16.0, 1 / 16.0),
      new Vector3(7 / 16.0, 10 / 16.0, 1 / 16.0),
      new Vector3(-7 / 16.0, 10 / 16.0, -1 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(-7 / 16.0, 0 / 16.0, -1 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, -1 / 16.0),
      new Vector3(-7 / 16.0, 0 / 16.0, 1 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(-7 / 16.0, 10 / 16.0, 1 / 16.0),
      new Vector3(-7 / 16.0, 10 / 16.0, -1 / 16.0),
      new Vector3(-7 / 16.0, 0 / 16.0, 1 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 10 / 16.0, -1 / 16.0),
      new Vector3(7 / 16.0, 10 / 16.0, 1 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, -1 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(-7 / 16.0, 10 / 16.0, -1 / 16.0),
      new Vector3(7 / 16.0, 10 / 16.0, -1 / 16.0),
      new Vector3(-7 / 16.0, 0 / 16.0, -1 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 10 / 16.0, 1 / 16.0),
      new Vector3(-7 / 16.0, 10 / 16.0, 1 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 1 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(-6 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(6 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(-6 / 16.0, 10 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(6 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(-6 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(6 / 16.0, 10 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    )
  };

  private static final Quad[] quadsNotAttached = Model.join(
    new Quad[]{
      new Quad(
        new Vector3(-7 / 16.0, 10 / 16.0, 1 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 1 / 16.0),
        new Vector3(-7 / 16.0, 10 / 16.0, -1 / 16.0),
        new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(-7 / 16.0, 0 / 16.0, -1 / 16.0),
        new Vector3(7 / 16.0, 0 / 16.0, -1 / 16.0),
        new Vector3(-7 / 16.0, 0 / 16.0, 1 / 16.0),
        new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(-7 / 16.0, 10 / 16.0, 1 / 16.0),
        new Vector3(-7 / 16.0, 10 / 16.0, -1 / 16.0),
        new Vector3(-7 / 16.0, 0 / 16.0, 1 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 10 / 16.0, -1 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 1 / 16.0),
        new Vector3(7 / 16.0, 0 / 16.0, -1 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
        new Vector3(-7 / 16.0, 10 / 16.0, -1 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, -1 / 16.0),
        new Vector3(-7 / 16.0, 0 / 16.0, -1 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
        new Vector3(7 / 16.0, 10 / 16.0, 1 / 16.0),
        new Vector3(-7 / 16.0, 10 / 16.0, 1 / 16.0),
        new Vector3(7 / 16.0, 0 / 16.0, 1 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      )
    },
    Model.transform(
      new Quad[]{
        new Quad(
          new Vector3(-1.5 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(1.5 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(-1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(1.5 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(-1.5 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        )
      },
      Transform.NONE
        .translate(0.5, 0.5, 0.5)
        .rotateY(Math.toRadians(-45))
        .translate(-0.5, -0.5, -0.5)
        .translate(-5 / 16.0, 0 / 16.0, 0 / 16.0)
    ),
    Model.transform(
      new Quad[]{
        new Quad(
          new Vector3(-1.5 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(1.5 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(-1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(1.5 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(-1.5 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        )
      },
      Transform.NONE
        .translate(0.5, 0.5, 0.5)
        .rotateY(Math.toRadians(45))
        .translate(-0.5, -0.5, -0.5)
        .translate(-5 / 16.0, 0 / 16.0, 0 / 16.0)
    ),
    Model.transform(
      new Quad[]{
        new Quad(
          new Vector3(-1.5 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(1.5 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(-1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(1.5 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(-1.5 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        )
      },
      Transform.NONE
        .translate(0.5, 0.5, 0.5)
        .rotateY(Math.toRadians(-45))
        .translate(-0.5, -0.5, -0.5)
        .translate(5 / 16.0, 0 / 16.0, 0 / 16.0)
    ),
    Model.transform(
      new Quad[]{
        new Quad(
          new Vector3(-1.5 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(1.5 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(-1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(1.5 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(-1.5 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        )
      },
      Transform.NONE
        .translate(0.5, 0.5, 0.5)
        .rotateY(Math.toRadians(45))
        .translate(-0.5, -0.5, -0.5)
        .translate(5 / 16.0, 0 / 16.0, 0 / 16.0)
    )
  );

  private static Quad[][] rotatedQuadsAttached = new Quad[16][];
  private static Quad[][] rotatedQuadsNotAttached = new Quad[16][];

  static {
    rotatedQuadsAttached[0] = Model.translate(quadsAttached, 0.5, 0, 0.5);
    rotatedQuadsNotAttached[0] = Model.translate(quadsNotAttached, 0.5, 0, 0.5);

    for (int i = 1; i < 16; ++i) {
      rotatedQuadsAttached[i] = Model.rotateY(rotatedQuadsAttached[0], -i * Math.PI / 8);
      rotatedQuadsNotAttached[i] = Model.rotateY(rotatedQuadsNotAttached[0], -i * Math.PI / 8);
    }
  }

  private final JsonArray[] frontText;
  private final JsonArray[] backText;
  private final int angle;
  private final boolean attached;
  private final SignTexture frontTexture;
  private final SignTexture backTexture;
  private final Texture texture;
  private final String material;

  public HangingSignEntity(Vector3 position, CompoundTag entityTag, int rotation, boolean attached, String material) {
    this(position, SignEntity.getFrontTextLines(entityTag), SignEntity.getBackTextLines(entityTag), rotation, attached, material);
  }

  public HangingSignEntity(Vector3 position, JsonArray[] frontText, JsonArray[] backText, int rotation, boolean attached, String material) {
    super(position);
    Texture signTexture = SignEntity.textureFromMaterial(material);
    this.frontText = frontText;
    this.backText = backText;
    this.angle = rotation;
    this.attached = attached;
    this.frontTexture = frontText != null ? new SignTexture(frontText, signTexture, false) : null;
    this.backTexture = backText != null ? new SignTexture(backText, signTexture, true) : null;
    this.texture = signTexture;
    this.material = material;
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    LinkedHashSet<Primitive> set = new LinkedHashSet<>();
    Quad[] quads = attached ? rotatedQuadsAttached[angle] : rotatedQuadsNotAttached[angle];
    for (Quad quad : quads) {
      quad.addTriangles(set, new TextureMaterial(Texture.redWool),
        Transform.NONE.translate(position.x + offset.x, position.y + offset.y, position.z + offset.z));
    }
    return set;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "hangingSign");
    json.add("position", position.toJson());
    if (frontText != null) {
      json.add("text", SignEntity.textToJson(frontText));
    }
    if (backText != null) {
      json.add("backText", SignEntity.textToJson(backText));
    }
    json.add("direction", angle);
    json.add("attached", attached);
    json.add("material", material);
    return json;
  }

  /**
   * Unmarshalls a sign entity from JSON data.
   */
  public static Entity fromJson(JsonObject json) {
    Vector3 position = new Vector3();
    position.fromJson(json.get("position").object());
    JsonArray[] frontText = null;
    if (json.get("text").isArray()) {
      frontText = SignEntity.textFromJson(json.get("text"));
    }
    JsonArray[] backText = null;
    if (json.get("backText").isArray()) {
      backText = SignEntity.textFromJson(json.get("backText"));
    }
    int direction = json.get("direction").intValue(0);
    boolean attached = json.get("attached").boolValue(false);
    String material = json.get("material").stringValue("oak");
    return new HangingSignEntity(position, frontText, backText, direction, attached, material);
  }
}

