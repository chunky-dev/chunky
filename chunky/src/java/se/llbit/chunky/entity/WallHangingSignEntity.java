package se.llbit.chunky.entity;

import se.llbit.chunky.block.minecraft.WallHangingSign;
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

public class WallHangingSignEntity extends Entity {
  private static final Quad[] quads = Model.join(
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
    new Quad[]{
      new Quad(
        new Vector3(0 / 16.0, 16 / 16.0, 10 / 16.0),
        new Vector3(16 / 16.0, 16 / 16.0, 10 / 16.0),
        new Vector3(0 / 16.0, 16 / 16.0, 6 / 16.0),
        new Vector4(0 / 16.0, 8 / 16.0, 14 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(0 / 16.0, 14 / 16.0, 6 / 16.0),
        new Vector3(16 / 16.0, 14 / 16.0, 6 / 16.0),
        new Vector3(0 / 16.0, 14 / 16.0, 10 / 16.0),
        new Vector4(8 / 16.0, 0 / 16.0, 9.5 / 16.0, 11.5 / 16.0)
      ),
      new Quad(
        new Vector3(0 / 16.0, 16 / 16.0, 10 / 16.0),
        new Vector3(0 / 16.0, 16 / 16.0, 6 / 16.0),
        new Vector3(0 / 16.0, 14 / 16.0, 10 / 16.0),
        new Vector4(10 / 16.0, 8 / 16.0, 12.5 / 16.0, 11.5 / 16.0)
      ),
      new Quad(
        new Vector3(16 / 16.0, 16 / 16.0, 6 / 16.0),
        new Vector3(16 / 16.0, 16 / 16.0, 10 / 16.0),
        new Vector3(16 / 16.0, 14 / 16.0, 6 / 16.0),
        new Vector4(10 / 16.0, 8 / 16.0, 14 / 16.0, 13 / 16.0)
      ),
      new Quad(
        new Vector3(0 / 16.0, 16 / 16.0, 6 / 16.0),
        new Vector3(16 / 16.0, 16 / 16.0, 6 / 16.0),
        new Vector3(0 / 16.0, 14 / 16.0, 6 / 16.0),
        new Vector4(8 / 16.0, 0 / 16.0, 12.5 / 16.0, 11.5 / 16.0)
      ),
      new Quad(
        new Vector3(16 / 16.0, 16 / 16.0, 10 / 16.0),
        new Vector3(0 / 16.0, 16 / 16.0, 10 / 16.0),
        new Vector3(16 / 16.0, 14 / 16.0, 10 / 16.0),
        new Vector4(8 / 16.0, 0 / 16.0, 14 / 16.0, 13 / 16.0)
      )
    },
    Model.rotateY(
      new Quad[]{
        new Quad(
          new Vector3(1.5 / 16.0, 12 / 16.0, 8 / 16.0),
          new Vector3(4.5 / 16.0, 12 / 16.0, 8 / 16.0),
          new Vector3(1.5 / 16.0, 10 / 16.0, 8 / 16.0),
          new Vector4(11 / 16.0, 12.5 / 16.0, 10.5 / 16.0, 9.5 / 16.0)
        ),
        new Quad(
          new Vector3(4.5 / 16.0, 12 / 16.0, 8 / 16.0),
          new Vector3(1.5 / 16.0, 12 / 16.0, 8 / 16.0),
          new Vector3(4.5 / 16.0, 10 / 16.0, 8 / 16.0),
          new Vector4(12.5 / 16.0, 11 / 16.0, 10.5 / 16.0, 9.5 / 16.0)
        )
      },
      Math.toRadians(45),
      new Vector3(3 / 16.0, 0, 8 / 16.0)
    ),
    Model.rotateY(
      new Quad[]{
        new Quad(
          new Vector3(1.5 / 16.0, 14 / 16.0, 8 / 16.0),
          new Vector3(4.5 / 16.0, 14 / 16.0, 8 / 16.0),
          new Vector3(1.5 / 16.0, 11 / 16.0, 8 / 16.0),
          new Vector4(14 / 16.0, 15.5 / 16.0, 11.5 / 16.0, 10 / 16.0)
        ),
        new Quad(
          new Vector3(4.5 / 16.0, 14 / 16.0, 8 / 16.0),
          new Vector3(1.5 / 16.0, 14 / 16.0, 8 / 16.0),
          new Vector3(4.5 / 16.0, 11 / 16.0, 8 / 16.0),
          new Vector4(15.5 / 16.0, 14 / 16.0, 11.5 / 16.0, 10 / 16.0)
        )
      },
      Math.toRadians(-45),
      new Vector3(3 / 16.0, 0, 8 / 16.0)
    ),
    Model.rotateY(
      new Quad[]{
        new Quad(
          new Vector3(11.5 / 16.0, 12 / 16.0, 8 / 16.0),
          new Vector3(14.5 / 16.0, 12 / 16.0, 8 / 16.0),
          new Vector3(11.5 / 16.0, 10 / 16.0, 8 / 16.0),
          new Vector4(11 / 16.0, 12.5 / 16.0, 10.5 / 16.0, 9.5 / 16.0)
        ),
        new Quad(
          new Vector3(14.5 / 16.0, 12 / 16.0, 8 / 16.0),
          new Vector3(11.5 / 16.0, 12 / 16.0, 8 / 16.0),
          new Vector3(14.5 / 16.0, 10 / 16.0, 8 / 16.0),
          new Vector4(12.5 / 16.0, 11 / 16.0, 10.5 / 16.0, 9.5 / 16.0)
        )
      },
      Math.toRadians(45),
      new Vector3(13 / 16.0, 0, 8 / 16.0)
    ),
    Model.rotateY(
      new Quad[]{
        new Quad(
          new Vector3(11.5 / 16.0, 14 / 16.0, 8 / 16.0),
          new Vector3(14.5 / 16.0, 14 / 16.0, 8 / 16.0),
          new Vector3(11.5 / 16.0, 11 / 16.0, 8 / 16.0),
          new Vector4(14 / 16.0, 15.5 / 16.0, 11.5 / 16.0, 10 / 16.0)
        ),
        new Quad(
          new Vector3(14.5 / 16.0, 14 / 16.0, 8 / 16.0),
          new Vector3(11.5 / 16.0, 14 / 16.0, 8 / 16.0),
          new Vector3(14.5 / 16.0, 11 / 16.0, 8 / 16.0),
          new Vector4(15.5 / 16.0, 14 / 16.0, 11.5 / 16.0, 10 / 16.0)
        )
      },
      Math.toRadians(-45),
      new Vector3(13 / 16.0, 0, 8 / 16.0)
    )
  );
  private static Quad[][] rotatedQuads = new Quad[4][];

  private static Quad[] frontFaceWithText = new Quad[4];
  private static Quad[] backFaceWithText = new Quad[4];

  static {
    rotatedQuads[0] = quads;
    rotatedQuads[1] = Model.rotateY(rotatedQuads[0]);
    rotatedQuads[2] = Model.rotateY(rotatedQuads[1]);
    rotatedQuads[3] = Model.rotateY(rotatedQuads[2]);

    frontFaceWithText[0] = new Quad(quads[4], Transform.NONE);
    frontFaceWithText[0].uv.set(0, 1, 0, 1);
    frontFaceWithText[1] = frontFaceWithText[0].transform(Transform.NONE.rotateY());
    frontFaceWithText[2] = frontFaceWithText[1].transform(Transform.NONE.rotateY());
    frontFaceWithText[3] = frontFaceWithText[2].transform(Transform.NONE.rotateY());

    backFaceWithText[0] = new Quad(quads[5], Transform.NONE);
    backFaceWithText[0].uv.set(0, 1, 0, 1);
    backFaceWithText[1] = backFaceWithText[0].transform(Transform.NONE.rotateY());
    backFaceWithText[2] = backFaceWithText[1].transform(Transform.NONE.rotateY());
    backFaceWithText[3] = backFaceWithText[2].transform(Transform.NONE.rotateY());
  }

  private final JsonArray[] frontText;
  private final JsonArray[] backText;
  private final WallHangingSign.Facing orientation;
  private final SignTexture frontTexture;
  private final SignTexture backTexture;
  private final SignEntity.Color frontDye;
  private final SignEntity.Color backDye;
  private final boolean frontGlowing;
  private final boolean backGlowing;
  private final Texture texture;
  private final String material;

  public WallHangingSignEntity(Vector3 position, CompoundTag entityTag, WallHangingSign.Facing direction, String material) {
    this(position, SignEntity.getFrontTextLines(entityTag), SignEntity.getFrontDyeColor(entityTag), SignEntity.getFrontGlowing(entityTag), SignEntity.getBackTextLines(entityTag), SignEntity.getBackDyeColor(entityTag), SignEntity.getBackGlowing(entityTag), direction, material);
  }

  public WallHangingSignEntity(Vector3 position, JsonArray[] frontText, SignEntity.Color frontDye, boolean frontGlowing, JsonArray[] backText, SignEntity.Color backDye, boolean backGlowing, WallHangingSign.Facing direction, String material) {
    super(position);
    Texture signTexture = HangingSignEntity.textureFromMaterial(material);
    this.frontText = frontText;
    this.backText = backText;
    this.frontDye = frontDye;
    this.backDye = backDye;
    this.frontGlowing = frontGlowing;
    this.backGlowing = backGlowing;
    this.orientation = direction;
    this.frontTexture = frontText != null ? new SignTexture(frontText, frontDye, frontGlowing, signTexture, 14, 10, 2 / 32., 16 / 32., 16 / 32., 26 / 32., 4.5, 3, 9) : null;
    this.backTexture = backText != null ? new SignTexture(backText, backDye, backGlowing, signTexture, 14, 10, 18 / 32., 16 / 32., 32 / 32., 26 / 32., 4.5, 3, 9) : null;
    this.texture = signTexture;
    this.material = material;
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    LinkedHashSet<Primitive> set = new LinkedHashSet<>();
    Quad[] quads = rotatedQuads[orientation.ordinal()];
    for (int i = 0; i < quads.length; ++i) {
      Quad quad = quads[i];
      Texture tex = texture;
      if (i == 4 && frontTexture != null) {
        tex = frontTexture;
        quad = frontFaceWithText[orientation.ordinal()];
      } else if (i == 5 && backTexture != null) {
        tex = backTexture;
        quad = backFaceWithText[orientation.ordinal()];
      }
      quad.addTriangles(set, new TextureMaterial(tex),
        Transform.NONE.translate(position.x + offset.x, position.y + offset.y, position.z + offset.z));
    }
    return set;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "wallHangingSign");
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
    json.add("direction", orientation.toString());
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
    WallHangingSign.Facing direction = WallHangingSign.Facing.fromString(json.get("direction").stringValue("north"));
    String material = json.get("material").stringValue("oak");
    SignEntity.Color dye = SignEntity.Color.getFromDyedSign(json.get("dye").stringValue(null));
    boolean glowing = json.get("glowing").boolValue(false);
    SignEntity.Color backDye = SignEntity.Color.getFromDyedSign(json.get("backDye").stringValue(null));
    boolean backGlowing = json.get("backGlowing").boolValue(false);
    return new WallHangingSignEntity(position, frontText, dye, glowing, backText, backDye, backGlowing, direction, material);
  }
}
