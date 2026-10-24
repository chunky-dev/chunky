package se.llbit.chunky.entity;

import se.llbit.chunky.model.builder.BoxModelBuilder;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Primitive;
import se.llbit.util.JsonUtil;

import java.util.ArrayList;
import java.util.Collection;

public class Cushion extends Entity {
  private static final Material[] materials = new Material[]{
    new TextureMaterial(Texture.whiteCushion),
    new TextureMaterial(Texture.orangeCushion),
    new TextureMaterial(Texture.magentaCushion),
    new TextureMaterial(Texture.lightBlueCushion),
    new TextureMaterial(Texture.yellowCushion),
    new TextureMaterial(Texture.limeCushion),
    new TextureMaterial(Texture.pinkCushion),
    new TextureMaterial(Texture.grayCushion),
    new TextureMaterial(Texture.lightGrayCushion),
    new TextureMaterial(Texture.cyanCushion),
    new TextureMaterial(Texture.purpleCushion),
    new TextureMaterial(Texture.blueCushion),
    new TextureMaterial(Texture.brownCushion),
    new TextureMaterial(Texture.greenCushion),
    new TextureMaterial(Texture.redCushion),
    new TextureMaterial(Texture.blackCushion),
  };

  private static final Quad[] quads = new BoxModelBuilder()
    .addBlockUnitsBox(-8, 0, -8, 16, 4, 16, box ->
      box.forTextureSize(64, 64).atUVCoordinates(0, 0)
        .addAllFaces())
    .toQuads();

  private final String color;
  private final Material material;

  public Cushion(Vector3 position, String color) {
    super(position);
    this.color = color;
    this.material = materials[switch (color) {
      case "orange" -> 1;
      case "magenta" -> 2;
      case "light_blue" -> 3;
      case "yellow" -> 4;
      case "lime" -> 5;
      case "pink" -> 6;
      case "gray" -> 7;
      case "light_gray" -> 8;
      case "cyan" -> 9;
      case "purple" -> 10;
      case "blue" -> 11;
      case "brown" -> 12;
      case "green" -> 13;
      case "red" -> 14;
      case "black" -> 15;
      default -> 0;
    }];
  }

  public static Entity fromJson(JsonObject json) {
    return new Cushion(JsonUtil.vec3FromJsonObject(json.get("position")),
      json.get("color").stringValue("white"));
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    ArrayList<Primitive> primitives = new ArrayList<>(quads.length);
    Transform transform = Transform.NONE
      .translate(position)
      .translate(offset);
    for (Quad quad : quads) {
      quad.addTriangles(primitives, material, transform);
    }
    return primitives;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "cushion");
    json.add("color", color);
    json.add("position", position.toJson());
    return json;
  }
}
