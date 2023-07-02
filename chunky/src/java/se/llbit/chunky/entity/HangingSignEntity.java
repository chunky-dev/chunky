package se.llbit.chunky.entity;

import se.llbit.chunky.resources.SignTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;

import java.util.Collection;
import java.util.LinkedHashSet;

import static se.llbit.chunky.entity.SignEntity.*;

public class HangingSignEntity extends Entity {
  private final JsonArray[] frontText;
  private final JsonArray[] backText;
  private final int angle;
  private final boolean attached;
  private final SignTexture frontTexture;
  private final SignTexture backTexture;
  private final Texture texture;
  private final String material;

  public HangingSignEntity(Vector3 position, CompoundTag entityTag, int rotation, boolean attached, String material) {
    this(position, getFrontTextLines(entityTag), getBackTextLines(entityTag), rotation, attached, material);
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
    // TODO
    return new LinkedHashSet<>();
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
      frontText = textFromJson(json.get("text"));
    }
    JsonArray[] backText = null;
    if (json.get("backText").isArray()) {
      backText = textFromJson(json.get("backText"));
    }
    int direction = json.get("direction").intValue(0);
    boolean attached = json.get("attached").boolValue(false);
    String material = json.get("material").stringValue("oak");
    return new HangingSignEntity(position, frontText, backText, direction, attached, material);
  }
}

