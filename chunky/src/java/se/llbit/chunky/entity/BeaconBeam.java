package se.llbit.chunky.entity;

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

import java.util.ArrayList;
import java.util.Collection;

public class BeaconBeam extends Entity implements Poseable {

  public static final Material beaconBeamMaterial = new TextureMaterial(Texture.beaconBeam);

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
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    Vector3 allPose = JsonUtil.vec3FromJsonArray(this.pose.get("all"));
    return primitives(Transform.NONE
        .translate(-0.5, -0.5, -0.5)
        .scale(scale)
        .rotateY(allPose.y) // Ignore x and z rotation because it breaks the beam into segments
        .translate(0.5, 0.5, 0.5)
        .translate(position.x + offset.x, position.y + offset.y, position.z + offset.z));
  }

  public Collection<Primitive> primitives(Transform transform) {
    ArrayList<Primitive> faces = new ArrayList<>();
    //Have 1 block tall model and repeat it for height * scale.
    //This addresses the texture stretching problem and
    //allows for the height to be changed.
    for (int i = 0; i < height; i++) {
      for (Quad quad : beam) {
        quad.addTriangles(faces, beaconBeamMaterial, transform.translate(0.0, 1.0 * i * scale, 0.0));
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
}
