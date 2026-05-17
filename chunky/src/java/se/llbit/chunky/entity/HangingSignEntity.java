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
import java.util.LinkedList;

public class HangingSignEntity extends Entity {
  private static final Quad[] quadsAttached = new Quad[]{
    new Quad(
      new Vector3(1 / 16.0, 10 / 16.0, 9 / 16.0),
      new Vector3(15 / 16.0, 10 / 16.0, 9 / 16.0),
      new Vector3(1 / 16.0, 10 / 16.0, 7 / 16.0),
      new Vector4(1 / 16.0, 8 / 16.0, 8 / 16.0, 9 / 16.0)
    ),
    new Quad(
      new Vector3(1 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector3(15 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector3(1 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(1 / 16.0, 8 / 16.0, 2 / 16.0, 3 / 16.0)
    ),
    new Quad(
      new Vector3(1 / 16.0, 10 / 16.0, 9 / 16.0),
      new Vector3(1 / 16.0, 10 / 16.0, 7 / 16.0),
      new Vector3(1 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(1 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
    ),
    new Quad(
      new Vector3(15 / 16.0, 10 / 16.0, 7 / 16.0),
      new Vector3(15 / 16.0, 10 / 16.0, 9 / 16.0),
      new Vector3(15 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector4(9 / 16.0, 8 / 16.0, 8 / 16.0, 3 / 16.0)
    ),
    new Quad(
      new Vector3(1 / 16.0, 10 / 16.0, 7 / 16.0),
      new Vector3(15 / 16.0, 10 / 16.0, 7 / 16.0),
      new Vector3(1 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector4(16 / 16.0, 9 / 16.0, 8 / 16.0, 3 / 16.0)
    ),
    new Quad(
      new Vector3(15 / 16.0, 10 / 16.0, 9 / 16.0),
      new Vector3(1 / 16.0, 10 / 16.0, 9 / 16.0),
      new Vector3(15 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(8 / 16.0, 1 / 16.0, 8 / 16.0, 3 / 16.0)
    ),
    new Quad(
      new Vector3(2 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(14 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(2 / 16.0, 10 / 16.0, 8 / 16.0),
      new Vector4(10 / 16.0, 16 / 16.0, 16 / 16.0, 13 / 16.0)
    ),
    new Quad(
      new Vector3(14 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(2 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(14 / 16.0, 10 / 16.0, 8 / 16.0),
      new Vector4(16 / 16.0, 10 / 16.0, 16 / 16.0, 13 / 16.0)
    )
  };

  private static final Quad[] quadsNotAttached = Model.join(
    new Quad[]{
      new Quad(
        new Vector3(1 / 16.0, 10 / 16.0, 9 / 16.0),
        new Vector3(15 / 16.0, 10 / 16.0, 9 / 16.0),
        new Vector3(1 / 16.0, 10 / 16.0, 7 / 16.0),
        new Vector4(1 / 16.0, 8 / 16.0, 8 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(1 / 16.0, 0 / 16.0, 7 / 16.0),
        new Vector3(15 / 16.0, 0 / 16.0, 7 / 16.0),
        new Vector3(1 / 16.0, 0 / 16.0, 9 / 16.0),
        new Vector4(1 / 16.0, 8 / 16.0, 2 / 16.0, 3 / 16.0)
      ),
      new Quad(
        new Vector3(1 / 16.0, 10 / 16.0, 9 / 16.0),
        new Vector3(1 / 16.0, 10 / 16.0, 7 / 16.0),
        new Vector3(1 / 16.0, 0 / 16.0, 9 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 8 / 16.0, 3 / 16.0)
      ),
      new Quad(
        new Vector3(15 / 16.0, 10 / 16.0, 7 / 16.0),
        new Vector3(15 / 16.0, 10 / 16.0, 9 / 16.0),
        new Vector3(15 / 16.0, 0 / 16.0, 7 / 16.0),
        new Vector4(9 / 16.0, 8 / 16.0, 8 / 16.0, 3 / 16.0)
      ),
      new Quad(
        new Vector3(1 / 16.0, 10 / 16.0, 7 / 16.0),
        new Vector3(15 / 16.0, 10 / 16.0, 7 / 16.0),
        new Vector3(1 / 16.0, 0 / 16.0, 7 / 16.0),
        new Vector4(16 / 16.0, 9 / 16.0, 8 / 16.0, 3 / 16.0)
      ),
      new Quad(
        new Vector3(15 / 16.0, 10 / 16.0, 9 / 16.0),
        new Vector3(1 / 16.0, 10 / 16.0, 9 / 16.0),
        new Vector3(15 / 16.0, 0 / 16.0, 9 / 16.0),
        new Vector4(8 / 16.0, 1 / 16.0, 8 / 16.0, 3 / 16.0)
      )
    },
    Model.rotateY(
      new Quad[]{
        new Quad(
          new Vector3(2.96447 / 16.0, 16 / 16.0, 4.46447 / 16.0),
          new Vector3(5.96447 / 16.0, 16 / 16.0, 4.46447 / 16.0),
          new Vector3(2.96447 / 16.0, 10 / 16.0, 4.46447 / 16.0),
          new Vector4(11 / 16.0, 12.5 / 16.0, 12.5 / 16.0, 9.5 / 16.0)
        ),
        new Quad(
          new Vector3(5.96447 / 16.0, 16 / 16.0, 4.46447 / 16.0),
          new Vector3(2.96447 / 16.0, 16 / 16.0, 4.46447 / 16.0),
          new Vector3(5.96447 / 16.0, 10 / 16.0, 4.46447 / 16.0),
          new Vector4(12.5 / 16.0, 11 / 16.0, 12.5 / 16.0, 9.5 / 16.0)
        )
      },
      Math.toRadians(45)
    ),
    Model.rotateY(
      new Quad[]{
        new Quad(
          new Vector3(2.96447 / 16.0, 15 / 16.0, 11.53553 / 16.0),
          new Vector3(5.96447 / 16.0, 15 / 16.0, 11.53553 / 16.0),
          new Vector3(2.96447 / 16.0, 11 / 16.0, 11.53553 / 16.0),
          new Vector4(14 / 16.0, 15.5 / 16.0, 12 / 16.0, 10 / 16.0)
        ),
        new Quad(
          new Vector3(5.96447 / 16.0, 15 / 16.0, 11.53553 / 16.0),
          new Vector3(2.96447 / 16.0, 15 / 16.0, 11.53553 / 16.0),
          new Vector3(5.96447 / 16.0, 11 / 16.0, 11.53553 / 16.0),
          new Vector4(15.5 / 16.0, 14 / 16.0, 12 / 16.0, 10 / 16.0)
        )
      },
      Math.toRadians(-45)
    ),
    Model.rotateY(
      new Quad[]{
        new Quad(
          new Vector3(10.03553 / 16.0, 16 / 16.0, 11.53553 / 16.0),
          new Vector3(13.03553 / 16.0, 16 / 16.0, 11.53553 / 16.0),
          new Vector3(10.03553 / 16.0, 10 / 16.0, 11.53553 / 16.0),
          new Vector4(11 / 16.0, 12.5 / 16.0, 12.5 / 16.0, 9.5 / 16.0)
        ),
        new Quad(
          new Vector3(13.03553 / 16.0, 16 / 16.0, 11.53553 / 16.0),
          new Vector3(10.03553 / 16.0, 16 / 16.0, 11.53553 / 16.0),
          new Vector3(13.03553 / 16.0, 10 / 16.0, 11.53553 / 16.0),
          new Vector4(12.5 / 16.0, 11 / 16.0, 12.5 / 16.0, 9.5 / 16.0)
        )
      },
      Math.toRadians(45)
    ),
    Model.rotateY(
      new Quad[]{
        new Quad(
          new Vector3(10.03553 / 16.0, 15 / 16.0, 4.46447 / 16.0),
          new Vector3(13.03553 / 16.0, 15 / 16.0, 4.46447 / 16.0),
          new Vector3(10.03553 / 16.0, 11 / 16.0, 4.46447 / 16.0),
          new Vector4(14 / 16.0, 15.5 / 16.0, 12 / 16.0, 10 / 16.0)
        ),
        new Quad(
          new Vector3(13.03553 / 16.0, 15 / 16.0, 4.46447 / 16.0),
          new Vector3(10.03553 / 16.0, 15 / 16.0, 4.46447 / 16.0),
          new Vector3(13.03553 / 16.0, 11 / 16.0, 4.46447 / 16.0),
          new Vector4(15.5 / 16.0, 14 / 16.0, 12 / 16.0, 10 / 16.0)
        )
      },
      Math.toRadians(-45)
    )
  );

  private static Quad[][] rotatedQuadsAttached = new Quad[16][];
  private static Quad[][] rotatedQuadsNotAttached = new Quad[16][];

  private static Quad[] frontFaceWithText = new Quad[16];
  private static Quad[] backFaceWithText = new Quad[16];

  static {
    rotatedQuadsAttached[0] = quadsAttached;
    rotatedQuadsNotAttached[0] = quadsNotAttached;

    for (int i = 1; i < 16; ++i) {
      rotatedQuadsAttached[i] = Model.rotateY(rotatedQuadsAttached[0], Math.PI - i * Math.PI / 8);
      rotatedQuadsNotAttached[i] = Model.rotateY(rotatedQuadsNotAttached[0], Math.PI - i * Math.PI / 8);
    }

    frontFaceWithText[0] = new Quad(quadsAttached[4], Transform.NONE);
    frontFaceWithText[0].uv.set(0, 1, 0, 1);
    for (int i = 1; i < 16; ++i) {
      frontFaceWithText[i] = frontFaceWithText[0].transform(Transform.NONE.rotateY(Math.PI - i * Math.PI / 8));
    }

    backFaceWithText[0] = new Quad(quadsAttached[5], Transform.NONE);
    backFaceWithText[0].uv.set(0, 1, 0, 1);
    for (int i = 1; i < 16; ++i) {
      backFaceWithText[i] = backFaceWithText[0].transform(Transform.NONE.rotateY(Math.PI - i * Math.PI / 8));
    }
  }

  private final JsonArray[] frontText;
  private final JsonArray[] backText;
  private final int angle;
  private final boolean attached;
  private final SignTexture frontTexture;
  private final SignTexture backTexture;
  private final SignEntity.Color frontDye;
  private final boolean frontGlowing;
  private final SignEntity.Color backDye;
  private final boolean backGlowing;
  private final Texture texture;
  private final String material;

  public HangingSignEntity(Vector3 position, CompoundTag entityTag, int rotation, boolean attached, String material) {
    this(position, SignEntity.getFrontTextLines(entityTag), SignEntity.getFrontDyeColor(entityTag), SignEntity.getFrontGlowing(entityTag), SignEntity.getBackTextLines(entityTag), SignEntity.getBackDyeColor(entityTag), SignEntity.getBackGlowing(entityTag), rotation, attached, material);
  }

  public HangingSignEntity(Vector3 position, JsonArray[] frontText, SignEntity.Color frontDye, boolean frontGlowing, JsonArray[] backText, SignEntity.Color backDye, boolean backGlowing, int rotation, boolean attached, String material) {
    super(position);
    Texture signTexture = HangingSignEntity.textureFromMaterial(material);
    this.frontText = frontText;
    this.backText = backText;
    this.frontDye = frontDye;
    this.frontGlowing = frontGlowing;
    this.backDye = backDye;
    this.backGlowing = backGlowing;
    this.angle = rotation;
    this.attached = attached;
    this.frontTexture = frontText != null ? new SignTexture(frontText, frontDye, frontGlowing, signTexture, 14, 10, 2 / 32., 16 / 32., 16 / 32., 26 / 32., 4.5, 3, 9) : null;
    this.backTexture = backText != null ? new SignTexture(backText, backDye, backGlowing, signTexture, 14, 10, 18 / 32., 16 / 32., 32 / 32., 26 / 32., 4.5, 3, 9) : null;
    this.texture = signTexture;
    this.material = material;
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> primitives = new LinkedList<>();
    Transform transform = Transform.NONE.translate(position.x + offset.x, position.y + offset.y, position.z + offset.z);
    Quad[] quads = attached ? rotatedQuadsAttached[angle] : rotatedQuadsNotAttached[angle];
    for (int i = 0; i < quads.length; ++i) {
      Quad quad = quads[i];
      Texture tex = texture;
      if (i == 4 && frontTexture != null) {
        tex = frontTexture;
        quad = frontFaceWithText[angle];
      } else if (i == 5 && backTexture != null) {
        tex = backTexture;
        quad = backFaceWithText[angle];
      }
      quad.addTriangles(primitives, new TextureMaterial(tex), transform);
    }
    return primitives;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "hangingSign");
    json.add("position", position.toJson());
    if (frontText != null) {
      json.add("text", SignEntity.textToJson(frontText));
      if (frontDye != null) {
        json.add("dye", frontDye.name().replace("DYE_", "").toLowerCase());
      }
      json.add("glowing", frontGlowing);
    }
    if (backText != null) {
      json.add("backText", SignEntity.textToJson(backText));
      if (backDye != null) {
        json.add("backDye", backDye.name().replace("DYE_", "").toLowerCase());
      }
      json.add("backGlowing", backGlowing);
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
    SignEntity.Color dye = SignEntity.Color.getFromDyedSign(json.get("dye").stringValue(null));
    boolean glowing = json.get("glowing").boolValue(false);
    SignEntity.Color backDye = SignEntity.Color.getFromDyedSign(json.get("backDye").stringValue(null));
    boolean backGlowing = json.get("backGlowing").boolValue(false);
    return new HangingSignEntity(position, frontText, dye, glowing, backText, backDye, backGlowing, direction, attached, material);
  }

  public static Texture textureFromMaterial(String material) {
    switch (material) {
      case "oak":
        return Texture.oakHangingSign;
      case "spruce":
        return Texture.spruceHangingSign;
      case "birch":
        return Texture.birchHangingSign;
      case "jungle":
        return Texture.jungleHangingSign;
      case "acacia":
        return Texture.acaciaHangingSign;
      case "dark_oak":
        return Texture.darkOakHangingSign;
      case "crimson":
        return Texture.crimsonHangingSign;
      case "warped":
        return Texture.warpedHangingSign;
      case "mangrove":
        return Texture.mangroveHangingSign;
      case "bamboo":
        return Texture.bambooHangingSign;
      case "cherry":
        return Texture.cherryHangingSign;
      case "pale_oak":
        return Texture.paleOakHangingSign;
      default:
        throw new IllegalArgumentException("Unknown hanging sign material: " + material);
    }
  }
}

