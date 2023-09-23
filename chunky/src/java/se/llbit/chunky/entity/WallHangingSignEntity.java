package se.llbit.chunky.entity;

import se.llbit.chunky.block.WallHangingSign;
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
      // sign top
      new Quad(
        new Vector3(-7 / 16.0, 10 / 16.0, 1 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 1 / 16.0),
        new Vector3(-7 / 16.0, 10 / 16.0, -1 / 16.0),
        new Vector4(2 / 64., 16 / 64., 1 - 12 / 32., 1 - 14 / 32.)
      ),
      // sign bottom
      new Quad(
        new Vector3(-7 / 16.0, 0 / 16.0, -1 / 16.0),
        new Vector3(7 / 16.0, 0 / 16.0, -1 / 16.0),
        new Vector3(-7 / 16.0, 0 / 16.0, 1 / 16.0),
        new Vector4(16 / 64., 30 / 64., 1 - 12 / 32., 1 - 14 / 32.)
      ),
      // sign left
      new Quad(
        new Vector3(-7 / 16.0, 10 / 16.0, 1 / 16.0),
        new Vector3(-7 / 16.0, 10 / 16.0, -1 / 16.0),
        new Vector3(-7 / 16.0, 0 / 16.0, 1 / 16.0),
        new Vector4(0 / 64., 2 / 64.0, 1 - 14 / 32., 1 - 24 / 32.)
      ),
      // sign right
      new Quad(
        new Vector3(7 / 16.0, 10 / 16.0, -1 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, 1 / 16.0),
        new Vector3(7 / 16.0, 0 / 16.0, -1 / 16.0),
        new Vector4(16 / 64., 18 / 64.0, 1 - 14 / 32., 1 - 24 / 32.)
      ),
      // sign front
      new Quad(
        new Vector3(-7 / 16.0, 10 / 16.0, -1 / 16.0),
        new Vector3(7 / 16.0, 10 / 16.0, -1 / 16.0),
        new Vector3(-7 / 16.0, 0 / 16.0, -1 / 16.0),
        new Vector4(16 / 64., 2 / 64., 1 - 14 / 32., 1 - 24 / 32.)
      ),
      // sign back
      new Quad(
        new Vector3(7 / 16.0, 10 / 16.0, 1 / 16.0),
        new Vector3(-7 / 16.0, 10 / 16.0, 1 / 16.0),
        new Vector3(7 / 16.0, 0 / 16.0, 1 / 16.0),
        new Vector4(32 / 64., 18 / 64., 1 - 14 / 32., 1 - 24 / 32.)
      )
    },
    new Quad[]{
      // bar top
      new Quad(
        new Vector3(-8 / 16.0, 16 / 16.0, 2 / 16.0),
        new Vector3(8 / 16.0, 16 / 16.0, 2 / 16.0),
        new Vector3(-8 / 16.0, 16 / 16.0, -2 / 16.0),
        new Vector4(4 / 64., 20 / 64., 1 - 0 / 32., 1 - 4 / 32.)
      ),
      // bar bottom
      new Quad(
        new Vector3(-8 / 16.0, 14 / 16.0, -2 / 16.0),
        new Vector3(8 / 16.0, 14 / 16.0, -2 / 16.0),
        new Vector3(-8 / 16.0, 14 / 16.0, 2 / 16.0),
        new Vector4(20 / 64., 36 / 64., 1 - 0 / 32., 1 - 4 / 32.)
      ),
      // bar left
      new Quad(
        new Vector3(-8 / 16.0, 16 / 16.0, 2 / 16.0),
        new Vector3(-8 / 16.0, 16 / 16.0, -2 / 16.0),
        new Vector3(-8 / 16.0, 14 / 16.0, 2 / 16.0),
        new Vector4(0 / 64., 4 / 64., 1 - 4 / 32., 1 - 6 / 32.)
      ),
      // bar right
      new Quad(
        new Vector3(8 / 16.0, 16 / 16.0, -2 / 16.0),
        new Vector3(8 / 16.0, 16 / 16.0, 2 / 16.0),
        new Vector3(8 / 16.0, 14 / 16.0, -2 / 16.0),
        new Vector4(20 / 64., 24 / 64., 1 - 4 / 32., 1 - 6 / 32.)
      ),
      // bar front
      new Quad(
        new Vector3(-8 / 16.0, 16 / 16.0, -2 / 16.0),
        new Vector3(8 / 16.0, 16 / 16.0, -2 / 16.0),
        new Vector3(-8 / 16.0, 14 / 16.0, -2 / 16.0),
        new Vector4(4 / 64., 20 / 64., 1 - 4 / 32., 1 - 6 / 32.)
      ),
      // bar back
      new Quad(
        new Vector3(8 / 16.0, 16 / 16.0, 2 / 16.0),
        new Vector3(-8 / 16.0, 16 / 16.0, 2 / 16.0),
        new Vector3(8 / 16.0, 14 / 16.0, 2 / 16.0),
        new Vector4(24 / 64., 40 / 64., 1 - 4 / 32., 1 - 6 / 32.)
      )
    },
    // chains
    Model.transform(
      new Quad[]{
        new Quad(
          new Vector3(-1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(-1.5 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector4(6 / 64., 9 / 64., 1 - 6 / 32., 1 - 12 / 32.)
        ),
        new Quad(
          new Vector3(1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(-1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(1.5 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector4(6 / 64., 9 / 64., 1 - 6 / 32., 1 - 12 / 32.)
        )
      },
      Transform.NONE
        .translate(0.5, 0.5, 0.5)
        .rotateY(Math.toRadians(-45))
        .translate(-0.5, -0.5, -0.5)
        .translate(-5 / 16.0, 6 / 16.0, 0 / 16.0)
    ),
    Model.transform(
      new Quad[]{
        new Quad(
          new Vector3(-1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(-1.5 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector4(0 / 64., 3 / 64., 1 - 6 / 32., 1 - 12 / 32.)
        ),
        new Quad(
          new Vector3(1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(-1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(1.5 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector4(0 / 64., 3 / 64., 1 - 6 / 32., 1 - 12 / 32.)
        )
      },
      Transform.NONE
        .translate(0.5, 0.5, 0.5)
        .rotateY(Math.toRadians(45))
        .translate(-0.5, -0.5, -0.5)
        .translate(-5 / 16.0, 6 / 16.0, 0 / 16.0)
    ),
    Model.transform(
      new Quad[]{
        new Quad(
          new Vector3(-1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(-1.5 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector4(6 / 64., 9 / 64., 1 - 6 / 32., 1 - 12 / 32.)
        ),
        new Quad(
          new Vector3(1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(-1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(1.5 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector4(6 / 64., 9 / 64., 1 - 6 / 32., 1 - 12 / 32.)
        )
      },
      Transform.NONE
        .translate(0.5, 0.5, 0.5)
        .rotateY(Math.toRadians(-45))
        .translate(-0.5, -0.5, -0.5)
        .translate(5 / 16.0, 6 / 16.0, 0 / 16.0)
    ),
    Model.transform(
      new Quad[]{
        new Quad(
          new Vector3(-1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(-1.5 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector4(0 / 64., 3 / 64., 1 - 6 / 32., 1 - 12 / 32.)
        ),
        new Quad(
          new Vector3(1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(-1.5 / 16.0, 10 / 16.0, 0 / 16.0),
          new Vector3(1.5 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector4(0 / 64., 3 / 64., 1 - 6 / 32., 1 - 12 / 32.)
        )
      },
      Transform.NONE
        .translate(0.5, 0.5, 0.5)
        .rotateY(Math.toRadians(45))
        .translate(-0.5, -0.5, -0.5)
        .translate(5 / 16.0, 6 / 16.0, 0 / 16.0)
    )
  );
  private static Quad[][] rotatedQuads = new Quad[4][];

  private static Quad[] frontFaceWithText = new Quad[4];
  private static Quad[] backFaceWithText = new Quad[4];

  static {
    rotatedQuads[0] = Model.translate(quads, 0.5, 0, 0.5);
    rotatedQuads[1] = Model.rotateY(rotatedQuads[0]);
    rotatedQuads[2] = Model.rotateY(rotatedQuads[1]);
    rotatedQuads[3] = Model.rotateY(rotatedQuads[2]);

    frontFaceWithText[0] = new Quad(
      new Vector3(-7 / 16.0, 10 / 16.0, -1 / 16.0),
      new Vector3(7 / 16.0, 10 / 16.0, -1 / 16.0),
      new Vector3(-7 / 16.0, 0 / 16.0, -1 / 16.0),
      new Vector4(1, 0, 1, 0)
    );
    frontFaceWithText[0] = frontFaceWithText[0].transform(Transform.NONE.translate(0.5, 0, 0.5));
    frontFaceWithText[1] = frontFaceWithText[0].transform(Transform.NONE.rotateY());
    frontFaceWithText[2] = frontFaceWithText[1].transform(Transform.NONE.rotateY());
    frontFaceWithText[3] = frontFaceWithText[2].transform(Transform.NONE.rotateY());

    backFaceWithText[0] = new Quad(
      new Vector3(7 / 16.0, 10 / 16.0, 1 / 16.0),
      new Vector3(-7 / 16.0, 10 / 16.0, 1 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 1 / 16.0),
      new Vector4(1, 0, 1, 0)
    );
    backFaceWithText[0] = backFaceWithText[0].transform(Transform.NONE.translate(0.5, 0, 0.5));
    backFaceWithText[1] = backFaceWithText[0].transform(Transform.NONE.rotateY());
    backFaceWithText[2] = backFaceWithText[1].transform(Transform.NONE.rotateY());
    backFaceWithText[3] = backFaceWithText[2].transform(Transform.NONE.rotateY());
  }

  private final JsonArray[] frontText;
  private final JsonArray[] backText;
  private final WallHangingSign.Facing orientation;
  private final SignTexture frontTexture;
  private final SignTexture backTexture;
  private final Texture texture;
  private final String material;

  public WallHangingSignEntity(Vector3 position, CompoundTag entityTag, WallHangingSign.Facing direction, String material) {
    this(position, SignEntity.getFrontTextLines(entityTag), SignEntity.getBackTextLines(entityTag), direction, material);
  }

  public WallHangingSignEntity(Vector3 position, JsonArray[] frontText, JsonArray[] backText, WallHangingSign.Facing direction, String material) {
    super(position);
    Texture signTexture = HangingSignEntity.textureFromMaterial(material);
    this.frontText = frontText;
    this.backText = backText;
    this.orientation = direction;
    this.frontTexture = frontText != null ? new SignTexture(frontText, signTexture, 14, 10, 2 / 64., 1 - 24 / 32., 16 / 64., 1 - 14 / 32., 4.5, 3, 9) : null;
    this.backTexture = backText != null ? new SignTexture(backText, signTexture, 14, 10, 18 / 64., 1 - 24 / 32., 32 / 64., 1 - 14 / 32., 4.5, 3, 9) : null;
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
    }
    if (backText != null) {
      json.add("backText", SignEntity.textToJson(backText));
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
    return new WallHangingSignEntity(position, frontText, backText, direction, material);
  }
}
