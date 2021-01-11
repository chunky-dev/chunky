package se.llbit.chunky.entity;

import se.llbit.chunky.block.Beacon;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texturepack.ColoredTexture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.BeaconBeamMaterial;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.util.JsonUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BeaconBeam extends Entity implements Poseable {

  private static final Quad[] beam = new Quad[]{
      new Quad(
          new Vector3(5 / 16.0, 0.0, 5 / 16.0),
          new Vector3(5 / 16.0, 0.0, 11 / 16.0),
          new Vector3(5 / 16.0, 1.0, 5 / 16.0),
          new Vector4(1.0, 0.0, 1.0, 0.0)
      ),
      new Quad(
          new Vector3(11 / 16.0, 0.0, 11 / 16.0),
          new Vector3(11 / 16.0, 0.0, 5 / 16.0),
          new Vector3(11 / 16.0, 1.0, 11 / 16.0),
          new Vector4(1.0, 0.0, 1.0, 0.0)
      ),
      new Quad(
          new Vector3(5 / 16.0, 1.0, 5 / 16.0),
          new Vector3(11 / 16.0, 1.0, 5 / 16.0),
          new Vector3(5 / 16.0, 0.0, 5 / 16.0),
          new Vector4(1.0, 0.0, 1.0, 0.0)
      ),
      new Quad(
          new Vector3(11 / 16.0, 1.0, 11 / 16.0),
          new Vector3(5 / 16.0, 1.0, 11 / 16.0),
          new Vector3(11 / 16.0, 0.0, 11 / 16.0),
          new Vector4(1.0, 0.0, 1.0, 0.0)
      )
  };

  private final JsonObject pose;
  private double scale = 1;
  private int height = 256;
  private Map<Integer, BeaconBeamMaterial> materials = new HashMap<>();

  public BeaconBeam(Vector3 position) {
    super(position);
    this.pose = new JsonObject();
    pose.add("all", JsonUtil.vec3ToJson(new Vector3(0, 0, 0)));
  }

  public BeaconBeam(JsonObject json) {
    super(JsonUtil.vec3FromJsonObject(json.get("position")));
    this.scale = json.get("scale").asDouble(1);
    this.height = json.get("height").asInt(256);
    this.pose = json.get("pose").object();

    JsonObject materialsList = json.get("beamMaterials").object();
    for (JsonMember obj : materialsList.members) {
      BeaconBeamMaterial mat = new BeaconBeamMaterial(0xFFFFFF);
      mat.loadMaterialProperties(obj.value.object());
      materials.put(Integer.parseInt(obj.name), mat);
    }
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    Vector3 allPose = JsonUtil.vec3FromJsonArray(this.pose.get("all"));
    ArrayList<Primitive> faces = new ArrayList<>();
    BeaconBeamMaterial using = new BeaconBeamMaterial(0x000000);
    //Have 1 block tall model and repeat it for height * scale.
    //This addresses the texture stretching problem and
    //allows for the height to be changed.
    for (int i = 0; i < height; i++) {
      if (materials.containsKey(i)) {
        using = materials.get(i);
      }

      for (Quad quad : beam) {
        quad.addTriangles(faces, using,
            Transform.NONE.translate(-0.5, -0.5, -0.5)
                .scale(scale)
                .translate(0.0, i * scale, 0.0)
                .rotateX(allPose.x)
                .rotateY(allPose.y)
                .rotateZ(allPose.z)
                .translate(0.5, 0.5, 0.5)
                .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z)
        );
      }
    }
    return faces;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "beaconBeam");
    json.add("position", position.toJson());
    json.add("height", height);
    json.add("scale", getScale());
    json.add("pose", pose);

    JsonObject materialsList = new JsonObject();
    for (int i : materials.keySet()) {
      BeaconBeamMaterial material = materials.get(i);
      JsonObject object = new JsonObject(materials.size());
      material.saveMaterialProperties(object);
      materialsList.add(String.valueOf(i), object);
    }

    json.add("beamMaterials", materialsList);
    return json;
  }

  public static BeaconBeam fromJson(JsonObject json) {
    return new BeaconBeam(json);
  }

  @Override
  public String[] partNames() {
    return new String[]{"all"};
  }

  @Override
  public double getScale() {
    return scale;
  }

  @Override
  public void setScale(double value) {
    this.scale = value;
  }

  @Override
  public JsonObject getPose() {
    return pose;
  }

  @Override
  public boolean hasHead() {
    return false;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public Map<Integer, BeaconBeamMaterial> getMaterials() {
    return materials;
  }
}
