package se.llbit.chunky.entity;

import se.llbit.chunky.block.minecraft.CalibratedSculkSensor;
import se.llbit.chunky.model.Model;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.math.*;
import se.llbit.math.primitive.Primitive;
import se.llbit.util.JsonUtil;

import java.util.Collection;
import java.util.LinkedList;

public class CalibratedSculkSensorAmethyst extends Entity {
  private static final Quad[] quadsNorth = Model.join(
    Model.rotateY(
      new Quad[]{
        new Quad(
          new Vector3(8 / 16.0, 20 / 16.0, 16 / 16.0),
          new Vector3(8 / 16.0, 20 / 16.0, 0 / 16.0),
          new Vector3(8 / 16.0, 8 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(8 / 16.0, 20 / 16.0, 0 / 16.0),
          new Vector3(8 / 16.0, 20 / 16.0, 16 / 16.0),
          new Vector3(8 / 16.0, 8 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
        )
      },
      Math.toRadians(45)
    ),
    Model.rotateY(
      new Quad[]{
        new Quad(
          new Vector3(0 / 16.0, 20 / 16.0, 8 / 16.0),
          new Vector3(16 / 16.0, 20 / 16.0, 8 / 16.0),
          new Vector3(0 / 16.0, 8 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(16 / 16.0, 20 / 16.0, 8 / 16.0),
          new Vector3(0 / 16.0, 20 / 16.0, 8 / 16.0),
          new Vector3(16 / 16.0, 8 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
        )
      },
      Math.toRadians(45)
    )
  );
  private static final Quad[] quadsEast = Model.rotateY(quadsNorth);
  private static final Quad[] quadsSouth = Model.rotateY(quadsEast);
  private static final Quad[] quadsWest = Model.rotateNegY(quadsNorth);

  private final String facing;
  private final boolean active;
  private final CalibratedSculkSensor block;

  public static final Material activeMaterial = new TextureMaterial(Texture.calibratedSculkSensorAmethyst);
  public static final Material inactiveMaterial = new TextureMaterial(Texture.calibratedSculkSensorAmethyst);

  public CalibratedSculkSensorAmethyst(Point3 position, String facing, boolean active, CalibratedSculkSensor block) {
    super(position);
    this.facing = facing;
    this.active = active;
    this.block = block;
  }

  public CalibratedSculkSensorAmethyst(JsonObject json) {
    super(JsonUtil.point3FromJsonObject(json.get("position")));
    this.facing = json.get("facing").stringValue("north");
    this.active = json.get("active").boolValue(true);
    this.block = null;
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> faces = new LinkedList<>();
    Transform transform = Transform.NONE
      .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z);

    Quad[] quads;
    if (facing.equals("east")) {
      quads = quadsEast;
    } else if (facing.equals("south")) {
      quads = quadsSouth;
    } else if (facing.equals("west")) {
      quads = quadsWest;
    } else {
      quads = quadsNorth;
    }

    Material material = active ? activeMaterial : inactiveMaterial;
    for (Quad quad : quads) {
      quad.addTriangles(faces, material, transform);
    }

    return faces;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "calibratedSculkSensorAmethyst");
    json.add("position", position.toJson());
    json.add("facing", facing);
    json.add("active", active);
    return json;
  }

  public static Entity fromJson(JsonObject json) {
    return new CalibratedSculkSensorAmethyst(json);
  }

  @Override
  public Grid.EmitterPosition[] getEmitterPosition() {
    if (block == null) {
      Log.warn("Attempted to build emitter grid from unassociated campfire entity.");
      return new Grid.EmitterPosition[0];
    }

    if (active) {
      Grid.EmitterPosition[] pos = new Grid.EmitterPosition[1];
      pos[0] = new Grid.EmitterPosition((int) position.x, (int) position.y, (int) position.z, block);
      return pos;
    } else {
      return new Grid.EmitterPosition[0];
    }
  }
}
