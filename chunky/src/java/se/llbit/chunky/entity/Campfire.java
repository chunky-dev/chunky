package se.llbit.chunky.entity;

import java.util.Collection;
import java.util.LinkedList;
import se.llbit.chunky.model.Model;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.util.JsonUtil;

public class Campfire extends Entity {

  public enum Kind {
    CAMPFIRE(Campfire.flameMaterial),
    SOUL_CAMPFIRE(Campfire.soulFlameMaterial);

    final Material flameMaterial;

    Kind(Material flameMaterial) {
      this.flameMaterial = flameMaterial;
    }
  }

  private static final Texture log = Texture.campfireLog;
  private static final Texture litlog = Texture.campfireLogLit;
  private static final Texture soullitlog = Texture.soulCampfireLogLit;

  private static final Texture[] tex = new Texture[]{
      log, log, log, log, log, log, log, log, log, log, log, log, log, log, log,
      log, log, log, log, log, log, log, log, log, log, log, log, log,
  };

  private static final Texture[] texLitCampfire = new Texture[]{
      log, log, log, litlog, log, log, log, litlog, log, log, litlog, litlog, log, log, litlog,
      log, log, log, log, litlog, log, log, litlog, litlog, litlog, log, log, log
  };

  private static final Texture[] texLitSoulCampfire = new Texture[]{
      log, log, log, soullitlog, log, log, log, soullitlog, log, log, soullitlog, soullitlog, log,
      log, soullitlog,
      log, log, log, log, soullitlog, log, log, soullitlog, soullitlog, soullitlog, log, log, log
  };

  private static final Quad[] quads = new Quad[]{
      new Quad(
          new Vector3(5 / 16.0, 4 / 16.0, 16 / 16.0),
          new Vector3(5 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector3(1 / 16.0, 4 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(5 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(5 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector3(1 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(1 / 16.0, 4 / 16.0, 16 / 16.0),
          new Vector3(1 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector3(1 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(5 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector3(5 / 16.0, 4 / 16.0, 16 / 16.0),
          new Vector3(5 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 15 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(1 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector3(5 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector3(1 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
      ),
      new Quad(
          new Vector3(5 / 16.0, 4 / 16.0, 16 / 16.0),
          new Vector3(1 / 16.0, 4 / 16.0, 16 / 16.0),
          new Vector3(5 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 7 / 16.0, 11 / 16.0),
          new Vector3(0 / 16.0, 7 / 16.0, 11 / 16.0),
          new Vector3(16 / 16.0, 7 / 16.0, 15 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 3 / 16.0, 11 / 16.0),
          new Vector3(16 / 16.0, 3 / 16.0, 11 / 16.0),
          new Vector3(0 / 16.0, 3 / 16.0, 15 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 8 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 7 / 16.0, 15 / 16.0),
          new Vector3(0 / 16.0, 7 / 16.0, 11 / 16.0),
          new Vector3(0 / 16.0, 3 / 16.0, 15 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 7 / 16.0, 11 / 16.0),
          new Vector3(16 / 16.0, 7 / 16.0, 15 / 16.0),
          new Vector3(16 / 16.0, 3 / 16.0, 11 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 7 / 16.0, 11 / 16.0),
          new Vector3(16 / 16.0, 7 / 16.0, 11 / 16.0),
          new Vector3(0 / 16.0, 3 / 16.0, 11 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 7 / 16.0, 15 / 16.0),
          new Vector3(0 / 16.0, 7 / 16.0, 15 / 16.0),
          new Vector3(16 / 16.0, 3 / 16.0, 15 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(15 / 16.0, 4 / 16.0, 16 / 16.0),
          new Vector3(15 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector3(11 / 16.0, 4 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(15 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(15 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector3(11 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(11 / 16.0, 4 / 16.0, 16 / 16.0),
          new Vector3(11 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector3(11 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 15 / 16.0, 11 / 16.0)
      ),
      new Quad(
          new Vector3(15 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector3(15 / 16.0, 4 / 16.0, 16 / 16.0),
          new Vector3(15 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(11 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector3(15 / 16.0, 4 / 16.0, 0 / 16.0),
          new Vector3(11 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
      ),
      new Quad(
          new Vector3(15 / 16.0, 4 / 16.0, 16 / 16.0),
          new Vector3(11 / 16.0, 4 / 16.0, 16 / 16.0),
          new Vector3(15 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 7 / 16.0, 1 / 16.0),
          new Vector3(0 / 16.0, 7 / 16.0, 1 / 16.0),
          new Vector3(16 / 16.0, 7 / 16.0, 5 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 12 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 3 / 16.0, 1 / 16.0),
          new Vector3(16 / 16.0, 3 / 16.0, 1 / 16.0),
          new Vector3(0 / 16.0, 3 / 16.0, 5 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 8 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 7 / 16.0, 5 / 16.0),
          new Vector3(0 / 16.0, 7 / 16.0, 1 / 16.0),
          new Vector3(0 / 16.0, 3 / 16.0, 5 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 7 / 16.0, 1 / 16.0),
          new Vector3(16 / 16.0, 7 / 16.0, 5 / 16.0),
          new Vector3(16 / 16.0, 3 / 16.0, 1 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 12 / 16.0, 8 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 7 / 16.0, 1 / 16.0),
          new Vector3(16 / 16.0, 7 / 16.0, 1 / 16.0),
          new Vector3(0 / 16.0, 3 / 16.0, 1 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 7 / 16.0, 5 / 16.0),
          new Vector3(0 / 16.0, 7 / 16.0, 5 / 16.0),
          new Vector3(16 / 16.0, 3 / 16.0, 5 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(11 / 16.0, 1 / 16.0, 16 / 16.0),
          new Vector3(11 / 16.0, 1 / 16.0, 0 / 16.0),
          new Vector3(5 / 16.0, 1 / 16.0, 16 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 2 / 16.0, 8 / 16.0)
      ),
      new Quad(
          new Vector3(11 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector3(11 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector3(5 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 2 / 16.0, 8 / 16.0)
      ),
      new Quad(
          new Vector3(5 / 16.0, 1 / 16.0, 0 / 16.0),
          new Vector3(11 / 16.0, 1 / 16.0, 0 / 16.0),
          new Vector3(5 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(6 / 16.0, 0 / 16.0, 1 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(11 / 16.0, 1 / 16.0, 16 / 16.0),
          new Vector3(5 / 16.0, 1 / 16.0, 16 / 16.0),
          new Vector3(11 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 10 / 16.0, 1 / 16.0, 0 / 16.0)
      ),
      rotateFire(new Quad(
          new Vector3(0.8 / 16.0, 17 / 16.0, 8 / 16.0),
          new Vector3(15.2 / 16.0, 17 / 16.0, 8 / 16.0),
          new Vector3(0.8 / 16.0, 1 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      )),
      rotateFire(new Quad(
          new Vector3(15.2 / 16.0, 17 / 16.0, 8 / 16.0),
          new Vector3(0.8 / 16.0, 17 / 16.0, 8 / 16.0),
          new Vector3(15.2 / 16.0, 1 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      )),
      rotateFire(new Quad(
          new Vector3(8 / 16.0, 17 / 16.0, 15.2 / 16.0),
          new Vector3(8 / 16.0, 17 / 16.0, 0.8 / 16.0),
          new Vector3(8 / 16.0, 1 / 16.0, 15.2 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      )),
      rotateFire(new Quad(
          new Vector3(8 / 16.0, 17 / 16.0, 0.8 / 16.0),
          new Vector3(8 / 16.0, 17 / 16.0, 15.2 / 16.0),
          new Vector3(8 / 16.0, 1 / 16.0, 0.8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ))
  };

  static final Quad[][] orientedQuads = new Quad[4][];

  static {
    orientedQuads[0] = quads;
    orientedQuads[1] = Model.rotateY(orientedQuads[0]);
    orientedQuads[2] = Model.rotateY(orientedQuads[1]);
    orientedQuads[3] = Model.rotateY(orientedQuads[2]);
  }

  private static Quad rotateFire(Quad quad) {
    double rotatedWidth = 14.4 * Math.cos(Math.toRadians(45)); // TODO rescale
    return new Quad(quad, Transform.NONE.rotateY(Math.toRadians(45)));
  }

  public static final Material flameMaterial = new TextureMaterial(Texture.campfireFire);
  public static final Material soulFlameMaterial = new TextureMaterial(Texture.soulCampfireFire);

  private final Campfire.Kind kind;
  private final String facing;
  private final boolean isLit;

  public Campfire(Campfire.Kind kind, Vector3 position, String facing, boolean lit) {
    super(position);
    this.kind = kind;
    this.facing = facing;
    this.isLit = lit;
  }

  public Campfire(JsonObject json) {
    super(JsonUtil.vec3FromJsonObject(json.get("position")));
    this.kind = Campfire.Kind.valueOf(json.get("campfireKind").stringValue("CAMPFIRE"));
    this.facing = json.get("facing").stringValue("north");
    this.isLit = json.get("lit").boolValue(true);
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> faces = new LinkedList<>();
    Transform transform = Transform.NONE
        .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z);
    int facing = getOrientationIndex(this.facing);
    Texture[] textures =
        isLit ? (kind == Kind.SOUL_CAMPFIRE ? texLitSoulCampfire : texLitCampfire) : tex;
    for (int i = 0; i < orientedQuads[facing].length - 4; i++) {
      Material material = new TextureMaterial(textures[i]);
      orientedQuads[facing][i].addTriangles(faces, material, transform);
    }
    if (isLit) {
      for (int i = orientedQuads[facing].length - 4; i < orientedQuads[facing].length; i++) {
        orientedQuads[facing][i].addTriangles(faces, kind.flameMaterial, transform);
      }
    }
    return faces;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "campfire");
    json.add("campfireKind", kind.name());
    json.add("position", position.toJson());
    json.add("facing", facing);
    json.add("lit", isLit);
    return json;
  }

  public static Entity fromJson(JsonObject json) {
    return new Campfire(json);
  }

  private static int getOrientationIndex(String facing) {
    switch (facing) {
      case "north":
          return 0;
      case "east":
          return 1;
      case "south":
          return 2;
      case "west":
          return 3;
      default:
          return 0;
    }
  }

  @Override
  public Vector3 getEmitterPosition() {
    if(isLit)
      return new Vector3(position.x + 0.5, position.y + 0.625, position.z + 0.5);
    else
      return null;
  }
  private static int getOrientationIndex(String facing) {
    switch (facing) {
      case "north":
        return 0;
      case "east":
        return 1;
      case "south":
        return 2;
      case "west":
        return 3;
      default:
        return 0;
    }
  }
}
